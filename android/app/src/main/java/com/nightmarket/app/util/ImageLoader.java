package com.nightmarket.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.nightmarket.app.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Thread-safe asynchronous image loader and compressor.
 * Features in-memory LRU caching, background HTTP streaming, and client-side photo downsampling.
 */
public class ImageLoader {

    private static ImageLoader instance;
    private final LruCache<String, Bitmap> memoryCache;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final OkHttpClient httpClient = new OkHttpClient();

    private ImageLoader() {
        // Cache size = 1/8th of available memory in bytes
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static synchronized ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    /**
     * Resolves a relative URL against the configured API_BASE_URL.
     */
    public static String resolveImageUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) return "";
        String url = rawUrl.trim();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        String baseUrl = BuildConfig.API_BASE_URL;
        if (baseUrl.endsWith("/") && url.startsWith("/")) {
            return baseUrl + url.substring(1);
        } else if (!baseUrl.endsWith("/") && !url.startsWith("/")) {
            return baseUrl + "/" + url;
        }
        return baseUrl + url;
    }

    /**
     * Loads an image asynchronously into an ImageView with LRU memory caching.
     *
     * @param imageView target view
     * @param rawUrl image URL (relative or absolute)
     * @param placeholderView optional placeholder view to hide when image loads
     */
    public void loadImage(ImageView imageView, String rawUrl, View placeholderView) {
        if (imageView == null) return;

        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            imageView.setImageDrawable(null);
            imageView.setVisibility(View.GONE);
            if (placeholderView != null) placeholderView.setVisibility(View.VISIBLE);
            return;
        }

        String fullUrl = resolveImageUrl(rawUrl);
        imageView.setTag(fullUrl);

        // Check in-memory LRU cache
        Bitmap cached = memoryCache.get(fullUrl);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            imageView.setVisibility(View.VISIBLE);
            if (placeholderView != null) placeholderView.setVisibility(View.GONE);
            return;
        }

        // Fetch asynchronously from network
        executor.execute(() -> {
            Bitmap bitmap = downloadBitmap(fullUrl);
            if (bitmap != null) {
                memoryCache.put(fullUrl, bitmap);
                mainHandler.post(() -> {
                    // Prevent recycled views from displaying wrong image
                    if (fullUrl.equals(imageView.getTag())) {
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                        if (placeholderView != null) placeholderView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private Bitmap downloadBitmap(String url) {
        try {
            Request request = new Request.Builder().url(url).build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    try (InputStream in = response.body().byteStream()) {
                        return BitmapFactory.decodeStream(in);
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Compresses and downsamples a local photo Uri into a temporary JPEG file.
     * Keeps images sharp while reducing bandwidth and upload latency.
     *
     * @param context Android context
     * @param imageUri source content Uri from photo picker
     * @return optimized File ready for multipart upload
     * @throws Exception if processing fails
     */
    public static File compressImage(Context context, Uri imageUri) throws Exception {
        // Step 1: Decode image bounds
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try (InputStream in = context.getContentResolver().openInputStream(imageUri)) {
            BitmapFactory.decodeStream(in, null, options);
        }

        int maxDim = 1280;
        int inSampleSize = 1;
        while (options.outWidth / inSampleSize > maxDim || options.outHeight / inSampleSize > maxDim) {
            inSampleSize *= 2;
        }

        // Step 2: Decode downsampled bitmap
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bitmap;
        try (InputStream in = context.getContentResolver().openInputStream(imageUri)) {
            bitmap = BitmapFactory.decodeStream(in, null, options);
        }

        if (bitmap == null) {
            throw new IllegalArgumentException("Unable to decode image from uri: " + imageUri);
        }

        // Step 3: Write out to cache directory as JPEG (85% quality)
        File cacheDir = context.getCacheDir();
        File tempFile = File.createTempFile("nightmarket_upload_", ".jpg", cacheDir);
        tempFile.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.flush();
        }

        bitmap.recycle();
        return tempFile;
    }
}
