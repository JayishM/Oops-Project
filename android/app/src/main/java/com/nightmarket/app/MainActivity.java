package com.nightmarket.app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Small native Android client for the NightMarket listing API.
 * The UI is built in Java so the project needs no external layout files.
 */
public class MainActivity extends AppCompatActivity {
    private static final int INK = Color.rgb(25, 33, 48);
    private static final int MUTED = Color.rgb(101, 112, 129);
    private static final int ACCENT = Color.rgb(50, 92, 230);
    private static final int BACKGROUND = Color.rgb(246, 248, 252);

    private static final String[] CATEGORY_LABELS = {
            "Textbooks & Academics", "Electronics & Gadgets", "Fashion & Apparel",
            "Hostel & Room Essentials", "Sports & Fitness", "Free Stuff", "Other"
    };
    private static final String[] CATEGORY_VALUES = {
            "TEXTBOOKS", "ELECTRONICS", "CLOTHING", "HOSTEL_ESSENTIALS",
            "SPORTS_FITNESS", "FREE_STUFF", "OTHER"
    };
    private static final String[] CONDITION_LABELS = {"Brand New", "Like New", "Good", "Fair"};
    private static final String[] CONDITION_VALUES = {"BRAND_NEW", "LIKE_NEW", "GOOD", "FAIR"};

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private LinearLayout listingContainer;
    private EditText searchInput;
    private TextView statusView;
    private Button refreshButton;
    private final String apiBaseUrl = BuildConfig.API_BASE_URL.replaceAll("/+$", "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(29, 39, 62));
        getWindow().setNavigationBarColor(Color.rgb(29, 39, 62));
        buildScreen();
        loadListings("");
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BACKGROUND);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(20), dp(22), dp(20), dp(20));
        header.setBackgroundColor(Color.rgb(29, 39, 62));
        TextView brand = text("NightMarket", 28, Color.WHITE, true);
        TextView tagline = text("Your campus. Your marketplace.", 14, Color.rgb(211, 220, 240), false);
        header.addView(brand);
        header.addView(tagline);
        root.addView(header, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        searchRow.setPadding(dp(16), dp(14), dp(16), dp(6));
        searchInput = new EditText(this);
        searchInput.setSingleLine(true);
        searchInput.setTextSize(14);
        searchInput.setHint("Search books, gadgets, essentials…");
        searchInput.setPadding(dp(12), 0, dp(12), 0);
        searchInput.setBackground(rounded(Color.WHITE, Color.rgb(222, 227, 237), 12));
        searchRow.addView(searchInput, new LinearLayout.LayoutParams(0, dp(48), 1f));
        Button searchButton = button("Search", ACCENT);
        LinearLayout.LayoutParams searchButtonParams = new LinearLayout.LayoutParams(-2, dp(48));
        searchButtonParams.leftMargin = dp(8);
        searchRow.addView(searchButton, searchButtonParams);
        searchButton.setOnClickListener(v -> loadListings(searchInput.getText().toString().trim()));
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            loadListings(searchInput.getText().toString().trim());
            return true;
        });
        root.addView(searchRow);

        LinearLayout actionRow = new LinearLayout(this);
        actionRow.setPadding(dp(16), dp(4), dp(16), dp(4));
        Button sellButton = button("+ Sell an item", ACCENT);
        actionRow.addView(sellButton, new LinearLayout.LayoutParams(0, dp(44), 1f));
        refreshButton = button("Refresh", Color.rgb(75, 88, 110));
        LinearLayout.LayoutParams refreshParams = new LinearLayout.LayoutParams(0, dp(44), 1f);
        refreshParams.leftMargin = dp(8);
        actionRow.addView(refreshButton, refreshParams);
        sellButton.setOnClickListener(v -> showCreateListingDialog());
        refreshButton.setOnClickListener(v -> loadListings(searchInput.getText().toString().trim()));
        root.addView(actionRow);

        statusView = text("Connecting to NightMarket API…", 12, MUTED, false);
        statusView.setPadding(dp(18), dp(8), dp(18), dp(8));
        root.addView(statusView);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        listingContainer = new LinearLayout(this);
        listingContainer.setOrientation(LinearLayout.VERTICAL);
        listingContainer.setPadding(dp(16), dp(4), dp(16), dp(20));
        scrollView.addView(listingContainer, new ScrollView.LayoutParams(-1, -2));
        root.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1f));

        setContentView(root);
    }

    private void loadListings(String query) {
        setLoading(true);
        listingContainer.removeAllViews();
        TextView loading = text("Loading campus listings…", 14, MUTED, false);
        loading.setPadding(dp(4), dp(20), dp(4), dp(20));
        listingContainer.addView(loading);
        executor.execute(() -> {
            try {
                String path = "/api/listings/feed";
                if (query != null && !query.isEmpty()) {
                    path = "/api/listings/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8.name());
                }
                JSONArray results = new JSONArray(request(path, "GET", null));
                runOnUiThread(() -> {
                    setLoading(false);
                    listingContainer.removeAllViews();
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject item = results.optJSONObject(i);
                        if (item != null) listingContainer.addView(createListingCard(item));
                    }
                    int count = results.length();
                    statusView.setText(count + (count == 1 ? " listing" : " listings") + " · " + apiBaseUrl);
                    if (count == 0) {
                        TextView empty = text("No listings found. Try another search or publish the first item.", 14, MUTED, false);
                        empty.setPadding(dp(6), dp(20), dp(6), dp(20));
                        listingContainer.addView(empty);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    listingContainer.removeAllViews();
                    TextView error = text("Can’t reach the backend. Start the Spring Boot server, then tap Refresh.\n\n" + safeMessage(e), 14, Color.rgb(164, 54, 54), false);
                    error.setPadding(dp(4), dp(16), dp(4), dp(16));
                    listingContainer.addView(error);
                    statusView.setText("API unavailable · " + apiBaseUrl);
                });
            }
        });
    }

    private View createListingCard(JSONObject item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setBackground(rounded(Color.WHITE, Color.rgb(231, 235, 243), 16));
        card.setElevation(dp(2));

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = text(item.optString("title", "Untitled listing"), 17, INK, true);
        title.setMaxLines(2);
        titleRow.addView(title, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView price = text(formatPrice(item.optString("price", "0")), 18, ACCENT, true);
        titleRow.addView(price);
        card.addView(titleRow);

        String category = prettify(item.optString("category", "OTHER"));
        TextView meta = text(category + "  ·  " + prettify(item.optString("condition", "GOOD")), 12, MUTED, false);
        LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(-1, -2);
        metaParams.topMargin = dp(6);
        card.addView(meta, metaParams);

        TextView description = text(item.optString("description", "No description provided."), 14, Color.rgb(62, 72, 89), false);
        description.setMaxLines(3);
        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(-1, -2);
        descriptionParams.topMargin = dp(9);
        card.addView(description, descriptionParams);

        TextView meetup = text("Meetup: " + item.optString("meetupArea", "Campus")
                + "  ·  " + item.optString("sellerCampus", "Campus"), 12, MUTED, false);
        LinearLayout.LayoutParams meetupParams = new LinearLayout.LayoutParams(-1, -2);
        meetupParams.topMargin = dp(10);
        card.addView(meetup, meetupParams);

        String seller = item.optString("sellerName", "Student seller");
        TextView sellerView = text(seller + (item.optBoolean("negotiable", false) ? "  ·  Price negotiable" : ""), 12, MUTED, false);
        LinearLayout.LayoutParams sellerParams = new LinearLayout.LayoutParams(-1, -2);
        sellerParams.topMargin = dp(4);
        card.addView(sellerView, sellerParams);

        card.setOnClickListener(v -> showListingDetails(item.optString("id", "")));
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);
        card.setClickable(true);
        card.setFocusable(true);
        return card;
    }

    private void showListingDetails(String id) {
        if (id == null || id.isEmpty()) return;
        statusView.setText("Loading listing details…");
        executor.execute(() -> {
            try {
                JSONObject detail = new JSONObject(request("/api/listings/" + id, "GET", null));
                runOnUiThread(() -> {
                    StringBuilder body = new StringBuilder();
                    body.append(detail.optString("description", "No description provided."));
                    body.append("\n\nCategory: ").append(prettify(detail.optString("category", "OTHER")));
                    body.append("\nCondition: ").append(prettify(detail.optString("condition", "GOOD")));
                    body.append("\nMeetup: ").append(detail.optString("meetupArea", "Campus"));
                    body.append("\nSeller: ").append(detail.optString("sellerName", "Student seller"));
                    String summary = detail.optString("categorySummary", "");
                    if (!summary.isEmpty()) body.append("\n\nItem details: ").append(summary);
                    JSONArray checklist = detail.optJSONArray("safetyChecklist");
                    if (checklist != null && checklist.length() > 0) {
                        body.append("\n\nBefore meeting:");
                        for (int i = 0; i < checklist.length(); i++) body.append("\n• ").append(checklist.optString(i));
                    }
                    new AlertDialog.Builder(this)
                            .setTitle(detail.optString("title", "Listing details"))
                            .setMessage(formatPrice(detail.optString("price", "0")) + "\n\n" + body)
                            .setPositiveButton("Close", null)
                            .show();
                    statusView.setText("Connected · " + apiBaseUrl);
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Could not load details: " + safeMessage(e), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void showCreateListingDialog() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(6), dp(20), dp(4));

        EditText title = formInput("Item title (3–100 characters)", InputType.TYPE_CLASS_TEXT);
        EditText description = formInput("Description (at least 10 characters)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        description.setMinLines(3);
        description.setGravity(Gravity.TOP | Gravity.START);
        EditText price = formInput("Price in INR (0 for free)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText meetup = formInput("Campus meetup area", InputType.TYPE_CLASS_TEXT);
        Spinner category = spinner(CATEGORY_LABELS);
        Spinner condition = spinner(CONDITION_LABELS);

        addFormField(form, "Item title", title);
        addFormField(form, "Description", description);
        addFormField(form, "Price (₹)", price);
        addFormField(form, "Category", category);
        addFormField(form, "Condition", condition);
        addFormField(form, "Meetup location", meetup);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(form);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sell an item")
                .setView(scroll)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Publish listing", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String titleValue = title.getText().toString().trim();
            String descValue = description.getText().toString().trim();
            String priceValue = price.getText().toString().trim();
            String meetupValue = meetup.getText().toString().trim();
            if (titleValue.length() < 3 || descValue.length() < 10 || priceValue.isEmpty() || meetupValue.isEmpty()) {
                Toast.makeText(this, "Fill every field. Title needs 3+ characters and description 10+.", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("title", titleValue);
                requestBody.put("description", descValue);
                requestBody.put("price", Double.parseDouble(priceValue));
                requestBody.put("category", CATEGORY_VALUES[category.getSelectedItemPosition()]);
                requestBody.put("condition", CONDITION_VALUES[condition.getSelectedItemPosition()]);
                requestBody.put("meetupArea", meetupValue);
                requestBody.put("negotiable", true);
                executor.execute(() -> {
                    try {
                        request("/api/listings/add", "POST", requestBody);
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(this, "Listing published!", Toast.LENGTH_SHORT).show();
                            searchInput.setText("");
                            loadListings("");
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(this, "Could not publish: " + safeMessage(e), Toast.LENGTH_LONG).show());
                    }
                });
            } catch (JSONException | NumberFormatException e) {
                Toast.makeText(this, "Enter a valid price.", Toast.LENGTH_SHORT).show();
            }
        }));
        dialog.show();
    }

    private String request(String path, String method, JSONObject body) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(apiBaseUrl + path).openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept", "application/json");
            if (body != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(payload);
                }
            }
            int code = connection.getResponseCode();
            InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
            String response = stream == null ? "" : readFully(stream);
            if (code < 200 || code >= 300) {
                throw new IOException("HTTP " + code + (response.isEmpty() ? "" : ": " + response));
            }
            return response;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private String readFully(InputStream stream) throws IOException {
        try (InputStream input = stream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            return output.toString(StandardCharsets.UTF_8.name());
        }
    }

    private void setLoading(boolean loading) {
        refreshButton.setEnabled(!loading);
        refreshButton.setText(loading ? "Loading…" : "Refresh");
    }

    private EditText formInput(String hint, int inputType) {
        EditText input = new EditText(this);
        input.setSingleLine((inputType & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0);
        input.setInputType(inputType);
        input.setHint(hint);
        input.setTextSize(14);
        input.setPadding(dp(10), dp(8), dp(10), dp(8));
        input.setBackground(rounded(Color.WHITE, Color.rgb(220, 225, 235), 8));
        return input;
    }

    private Spinner spinner(String[] values) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private void addFormField(LinearLayout container, String label, View field) {
        TextView labelView = text(label, 12, MUTED, true);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(-1, -2);
        labelParams.topMargin = dp(7);
        container.addView(labelView, labelParams);
        LinearLayout.LayoutParams fieldParams = new LinearLayout.LayoutParams(-1, -2);
        fieldParams.topMargin = dp(3);
        fieldParams.bottomMargin = dp(5);
        container.addView(field, fieldParams);
    }

    private Button button(String label, int color) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(13);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(ColorStateList.valueOf(color));
        return button;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private GradientDrawable rounded(int fill, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private String formatPrice(String value) {
        try {
            double amount = Double.parseDouble(value);
            if (amount == 0) return "FREE";
            if (amount == Math.rint(amount)) return "₹" + String.format(java.util.Locale.US, "%.0f", amount);
            return "₹" + String.format(java.util.Locale.US, "%.2f", amount);
        } catch (Exception ignored) {
            return "₹" + value;
        }
    }

    private String prettify(String value) {
        if (value == null || value.trim().isEmpty()) return "Other";
        String[] parts = value.toLowerCase(java.util.Locale.ROOT).split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (result.length() > 0) result.append(' ');
            if (!part.isEmpty()) result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return result.toString();
    }

    private String safeMessage(Exception e) {
        String message = e.getMessage();
        return message == null || message.trim().isEmpty() ? e.getClass().getSimpleName() : message;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
