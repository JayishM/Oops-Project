package com.nightmarket.app.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nightmarket.app.MainActivity;
import com.nightmarket.app.R;
import com.nightmarket.app.api.ApiClient;
import com.nightmarket.app.model.CreateListingRequest;
import com.nightmarket.app.model.Listing;
import com.nightmarket.app.repository.RemoteListingRepository;
import com.nightmarket.app.util.ImageLoader;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class AddItemFragment extends Fragment {

    private static final int MAX_PHOTOS = 5;

    private ImageView btnAddBack;
    private FrameLayout btnUploadPhoto;
    private TextView tvUploadButtonText;
    private TextView tvPhotoCount;
    private HorizontalScrollView scrollThumbnails;
    private LinearLayout layoutThumbnails;
    private TextView tvUploadProgress;

    private EditText etTitle;
    private Spinner spinnerCategory;
    private EditText etPrice;
    private Spinner spinnerCondition;
    private EditText etDescription;
    private EditText etSellerName;
    private EditText etSellerPhone;
    private EditText etSellerEmail;
    private EditText etMeetupArea;
    private CheckBox cbNegotiable;
    private ProgressBar progressBarAdd;
    private Button btnPublish;

    private RemoteListingRepository repository;
    private final List<Uri> selectedPhotoUris = new ArrayList<>();
    private final ExecutorService uploadExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isSubmitting = false;

    private final String[] categoryNames = {
            "Textbooks & Books", "Electronics & Gadgets", "Fashion & Apparel",
            "Hostel & Room Essentials", "Sports & Fitness", "Free Stuff", "Other"
    };
    private final String[] categoryValues = {
            "TEXTBOOKS", "ELECTRONICS", "CLOTHING", "HOSTEL_ESSENTIALS",
            "SPORTS_FITNESS", "FREE_STUFF", "OTHER"
    };

    private final String[] conditionNames = {"Like new", "Brand new", "Good", "Fair"};
    private final String[] conditionValues = {"LIKE_NEW", "BRAND_NEW", "GOOD", "FAIR"};

    // Photo picker launcher
    private final ActivityResultLauncher<String> photoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
                if (uris != null && !uris.isEmpty()) {
                    int remainingSlots = MAX_PHOTOS - selectedPhotoUris.size();
                    if (remainingSlots <= 0) {
                        Toast.makeText(getContext(), "Maximum 5 photos reached", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int countToAdd = Math.min(remainingSlots, uris.size());
                    for (int i = 0; i < countToAdd; i++) {
                        Uri uri = uris.get(i);
                        if (!selectedPhotoUris.contains(uri)) {
                            selectedPhotoUris.add(uri);
                        }
                    }

                    if (uris.size() > remainingSlots) {
                        Toast.makeText(getContext(), "Kept the first " + remainingSlots + " photos (max 5)", Toast.LENGTH_SHORT).show();
                    }

                    renderThumbnails();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new RemoteListingRepository(requireContext());
        bindViews(view);
        setupSpinners();
        setupListeners();
        renderThumbnails();
    }

    private void bindViews(View view) {
        btnAddBack = view.findViewById(R.id.btnAddBack);
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        tvUploadButtonText = view.findViewById(R.id.tvUploadButtonText);
        tvPhotoCount = view.findViewById(R.id.tvPhotoCount);
        scrollThumbnails = view.findViewById(R.id.scrollThumbnails);
        layoutThumbnails = view.findViewById(R.id.layoutThumbnails);
        tvUploadProgress = view.findViewById(R.id.tvUploadProgress);

        etTitle = view.findViewById(R.id.etTitle);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        etPrice = view.findViewById(R.id.etPrice);
        spinnerCondition = view.findViewById(R.id.spinnerCondition);
        etDescription = view.findViewById(R.id.etDescription);
        etSellerName = view.findViewById(R.id.etSellerName);
        etSellerPhone = view.findViewById(R.id.etSellerPhone);
        etSellerEmail = view.findViewById(R.id.etSellerEmail);
        etMeetupArea = view.findViewById(R.id.etMeetupArea);
        cbNegotiable = view.findViewById(R.id.cbNegotiable);
        progressBarAdd = view.findViewById(R.id.progressBarAdd);
        btnPublish = view.findViewById(R.id.btnPublish);
    }

    private void setupSpinners() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<String> condAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, conditionNames);
        spinnerCondition.setAdapter(condAdapter);
    }

    private void setupListeners() {
        btnAddBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        btnUploadPhoto.setOnClickListener(v -> {
            if (isSubmitting) return;
            if (selectedPhotoUris.size() >= MAX_PHOTOS) {
                Toast.makeText(getContext(), "You've added 5 photos. Remove one to replace it.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                photoPickerLauncher.launch("image/*");
            } catch (Exception e) {
                Toast.makeText(getContext(), "Unable to open image picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnPublish.setOnClickListener(v -> {
            if (!isSubmitting) {
                submitListing();
            }
        });
    }

    private void renderThumbnails() {
        if (layoutThumbnails == null) return;
        layoutThumbnails.removeAllViews();

        int count = selectedPhotoUris.size();
        if (count == 0) {
            scrollThumbnails.setVisibility(View.GONE);
            if (tvUploadButtonText != null) tvUploadButtonText.setText("Add photos of your item");
            if (tvPhotoCount != null) tvPhotoCount.setText("Up to 5 photos · Clear, well-lit images");
            return;
        }

        scrollThumbnails.setVisibility(View.VISIBLE);
        if (tvUploadButtonText != null) tvUploadButtonText.setText("Add more photos");
        if (tvPhotoCount != null) tvPhotoCount.setText(count + "/" + MAX_PHOTOS + " photos selected");

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int i = 0; i < count; i++) {
            final int index = i;
            final Uri uri = selectedPhotoUris.get(i);

            View thumbView = inflater.inflate(R.layout.item_selected_photo, layoutThumbnails, false);
            ImageView ivThumbnail = thumbView.findViewById(R.id.ivThumbnail);
            ImageView btnRemovePhoto = thumbView.findViewById(R.id.btnRemovePhoto);

            try {
                ivThumbnail.setImageURI(uri);
            } catch (Exception ex) {
                ivThumbnail.setImageResource(R.drawable.bg_badge_lavender);
            }

            btnRemovePhoto.setOnClickListener(v -> {
                if (isSubmitting) return;
                selectedPhotoUris.remove(index);
                renderThumbnails();
            });

            layoutThumbnails.addView(thumbView);
        }
    }

    private void submitListing() {
        String title = etTitle.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String sellerName = etSellerName.getText().toString().trim();
        String phone = etSellerPhone.getText().toString().trim();
        String email = etSellerEmail.getText().toString().trim();
        String meetup = etMeetupArea.getText().toString().trim();

        // Validations
        if (title.length() < 3) {
            etTitle.setError("Title must be at least 3 characters");
            etTitle.requestFocus();
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr.isEmpty() ? "0" : priceStr);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                etPrice.setError("Price cannot be negative");
                etPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Enter a valid price");
            etPrice.requestFocus();
            return;
        }

        if (description.length() < 10) {
            etDescription.setError("Please describe the item (at least 10 characters)");
            etDescription.requestFocus();
            return;
        }

        if (sellerName.length() < 2) {
            etSellerName.setError("Enter your name (at least 2 characters)");
            etSellerName.requestFocus();
            return;
        }

        if (phone.isEmpty() && email.isEmpty()) {
            Toast.makeText(getContext(), "Provide at least one contact method (phone or email)", Toast.LENGTH_LONG).show();
            etSellerPhone.setError("Phone or email required");
            etSellerPhone.requestFocus();
            return;
        }

        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etSellerEmail.setError("Enter a valid email address");
            etSellerEmail.requestFocus();
            return;
        }

        CreateListingRequest request = new CreateListingRequest();
        request.setTitle(title);
        request.setPrice(price);
        request.setDescription(description);
        request.setCategory(categoryValues[spinnerCategory.getSelectedItemPosition()]);
        request.setCondition(conditionValues[spinnerCondition.getSelectedItemPosition()]);
        request.setSellerName(sellerName);
        request.setSellerPhone(phone);
        request.setSellerEmail(email);
        request.setMeetupArea(meetup.isEmpty() ? "Campus meetup spot" : meetup);
        request.setNegotiable(cbNegotiable.isChecked());

        isSubmitting = true;
        progressBarAdd.setVisibility(View.VISIBLE);
        btnPublish.setEnabled(false);

        // If no photos selected, publish directly
        if (selectedPhotoUris.isEmpty()) {
            publishListingPayload(request);
            return;
        }

        // Upload photos asynchronously before submitting listing
        tvUploadProgress.setVisibility(View.VISIBLE);
        tvUploadProgress.setText("Preparing " + selectedPhotoUris.size() + " photo(s)...");

        uploadExecutor.execute(() -> {
            List<String> uploadedUrls = new ArrayList<>();
            int total = selectedPhotoUris.size();

            try {
                for (int i = 0; i < total; i++) {
                    final int currentIdx = i + 1;
                    mainHandler.post(() -> {
                        if (isAdded() && tvUploadProgress != null) {
                            tvUploadProgress.setText("Uploading photo " + currentIdx + " of " + total + " to MongoDB Atlas...");
                        }
                    });

                    Uri uri = selectedPhotoUris.get(i);
                    File compressedFile = ImageLoader.compressImage(requireContext(), uri);

                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), compressedFile);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("file", compressedFile.getName(), requestBody);

                    Response<Map<String, String>> response = ApiClient.getApiService().uploadImage(part).execute();
                    if (!response.isSuccessful() || response.body() == null || !response.body().containsKey("imageUrl")) {
                        String errMsg = "Upload failed with code " + response.code();
                        try {
                            if (response.errorBody() != null) errMsg = response.errorBody().string();
                        } catch (Exception ignored) {}
                        throw new IllegalStateException("Failed to upload photo " + currentIdx + ": " + errMsg);
                    }

                    String imageUrl = response.body().get("imageUrl");
                    uploadedUrls.add(imageUrl);
                }

                // All photos uploaded successfully
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    tvUploadProgress.setText("Photos uploaded! Publishing listing...");
                    request.setImageUrls(uploadedUrls);
                    publishListingPayload(request);
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    isSubmitting = false;
                    progressBarAdd.setVisibility(View.GONE);
                    tvUploadProgress.setVisibility(View.GONE);
                    btnPublish.setEnabled(true);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void publishListingPayload(CreateListingRequest request) {
        repository.createListing(request, new RemoteListingRepository.ApiCallback<Listing>() {
            @Override
            public void onSuccess(Listing result) {
                if (!isAdded()) return;
                isSubmitting = false;
                progressBarAdd.setVisibility(View.GONE);
                if (tvUploadProgress != null) tvUploadProgress.setVisibility(View.GONE);
                btnPublish.setEnabled(true);
                Toast.makeText(getContext(), "Listing published successfully! 🎉", Toast.LENGTH_SHORT).show();

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToProductDetail(result.getId());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                isSubmitting = false;
                progressBarAdd.setVisibility(View.GONE);
                if (tvUploadProgress != null) tvUploadProgress.setVisibility(View.GONE);
                btnPublish.setEnabled(true);
                Toast.makeText(getContext(), "Failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
