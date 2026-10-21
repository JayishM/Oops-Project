package com.nightmarket.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import android.util.LruCache;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Small native Android client for the NightMarket listing API.
 * The UI is built in Java so the project needs no external layout files.
 */
public class MainActivity extends AppCompatActivity {
    // A compact design system keeps the native UI consistent and easy to maintain.
    private static final int INK = Color.rgb(24, 29, 48);
    private static final int MUTED = Color.rgb(105, 113, 132);
    private static final int ACCENT = Color.rgb(104, 82, 236);
    private static final int BACKGROUND = Color.rgb(246, 247, 251);
    private static final int BORDER = Color.rgb(231, 233, 242);

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
    private final ExecutorService imageExecutor = Executors.newFixedThreadPool(2);
    private final LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(8 * 1024 * 1024) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    };
    private LinearLayout listingContainer;
    private EditText searchInput;
    private TextView statusView;
    private Button refreshButton;
    private LinearLayout categoryChipContainer;
    private ScrollView screenScrollView;
    private LinearLayout bottomNavContainer;
    private TextView sortButton;
    private String sortMode = "LATEST";
    private JSONArray cachedListings = new JSONArray();
    private String selectedCategory = "ALL";
    private final String apiBaseUrl = BuildConfig.API_BASE_URL.replaceAll("/+$", "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(25, 29, 57));
        getWindow().setNavigationBarColor(Color.rgb(25, 29, 57));
        buildScreen();
        loadListings("");
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BACKGROUND);

        // Compact brand bar stays visible while the marketplace content scrolls.
        LinearLayout appBar = new LinearLayout(this);
        appBar.setGravity(Gravity.CENTER_VERTICAL);
        appBar.setPadding(dp(18), dp(10), dp(18), dp(10));
        appBar.setBackgroundColor(Color.WHITE);

        TextView brandMark = text("N", 23, Color.WHITE, true);
        brandMark.setGravity(Gravity.CENTER);
        GradientDrawable markGradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(104, 82, 236), Color.rgb(66, 51, 177)});
        markGradient.setCornerRadius(dp(14));
        brandMark.setBackground(markGradient);
        appBar.addView(brandMark, new LinearLayout.LayoutParams(dp(44), dp(44)));

        LinearLayout brandCopy = new LinearLayout(this);
        brandCopy.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams brandCopyParams = new LinearLayout.LayoutParams(0, -2, 1f);
        brandCopyParams.leftMargin = dp(11);
        appBar.addView(brandCopy, brandCopyParams);
        brandCopy.addView(text("NightMarket", 17, INK, true));
        TextView brandSub = text("THE CAMPUS EXCHANGE", 9, MUTED, true);
        brandSub.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams brandSubParams = new LinearLayout.LayoutParams(-2, -2);
        brandSubParams.topMargin = dp(2);
        brandCopy.addView(brandSub, brandSubParams);

        TextView campusBadge = text("CAMPUS\nEDITION", 9, ACCENT, true);
        campusBadge.setGravity(Gravity.CENTER);
        campusBadge.setLineSpacing(0, 1f);
        campusBadge.setPadding(dp(10), dp(7), dp(10), dp(7));
        campusBadge.setBackground(rounded(Color.rgb(244, 242, 255), Color.rgb(234, 230, 255), 11));
        appBar.addView(campusBadge);
        root.addView(appBar, new LinearLayout.LayoutParams(-1, -2));

        screenScrollView = new ScrollView(this);
        screenScrollView.setFillViewport(true);
        screenScrollView.setClipToPadding(false);
        screenScrollView.setVerticalScrollBarEnabled(false);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, dp(12), 0, dp(24));
        screenScrollView.addView(content, new ScrollView.LayoutParams(-1, -2));

        // Editorial hero with clear message and a branded graphic tile.
        LinearLayout hero = new LinearLayout(this);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(dp(20), dp(21), dp(16), dp(21));
        GradientDrawable heroGradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(25, 29, 57), Color.rgb(52, 49, 119), Color.rgb(103, 78, 210)});
        heroGradient.setCornerRadius(dp(23));
        hero.setBackground(heroGradient);
        LinearLayout.LayoutParams heroParams = new LinearLayout.LayoutParams(-1, -2);
        heroParams.setMargins(dp(14), 0, dp(14), dp(13));
        content.addView(hero, heroParams);

        LinearLayout heroCopy = new LinearLayout(this);
        heroCopy.setOrientation(LinearLayout.VERTICAL);
        hero.addView(heroCopy, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView eyebrow = text("BUY LOCAL. LIVE BETTER.", 9, Color.rgb(214, 209, 255), true);
        eyebrow.setLetterSpacing(0.12f);
        heroCopy.addView(eyebrow);
        TextView heroTitle = text("Good finds.\nBetter prices.", 27, Color.WHITE, true);
        heroTitle.setLineSpacing(dp(1), 1.0f);
        LinearLayout.LayoutParams heroTitleParams = new LinearLayout.LayoutParams(-1, -2);
        heroTitleParams.topMargin = dp(10);
        heroCopy.addView(heroTitle, heroTitleParams);
        TextView heroSub = text("Buy, sell and pass it on — right here on campus.", 11, Color.rgb(229, 227, 250), false);
        heroSub.setMaxLines(2);
        heroSub.setLineSpacing(dp(2), 1.0f);
        LinearLayout.LayoutParams heroSubParams = new LinearLayout.LayoutParams(-1, -2);
        heroSubParams.topMargin = dp(8);
        heroCopy.addView(heroSub, heroSubParams);

        LinearLayout heroStamp = new LinearLayout(this);
        heroStamp.setOrientation(LinearLayout.VERTICAL);
        heroStamp.setGravity(Gravity.CENTER);
        heroStamp.setPadding(dp(8), dp(12), dp(8), dp(12));
        GradientDrawable stampBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(129, 109, 255), Color.rgb(74, 64, 174)});
        stampBg.setCornerRadius(dp(18));
        stampBg.setStroke(dp(1), Color.rgb(151, 134, 255));
        heroStamp.setBackground(stampBg);
        LinearLayout.LayoutParams stampParams = new LinearLayout.LayoutParams(dp(78), dp(100));
        stampParams.leftMargin = dp(10);
        hero.addView(heroStamp, stampParams);
        TextView stampN = text("NM", 24, Color.WHITE, true);
        stampN.setGravity(Gravity.CENTER);
        heroStamp.addView(stampN);
        View stampLine = new View(this);
        stampLine.setBackgroundColor(Color.rgb(185, 176, 255));
        LinearLayout.LayoutParams stampLineParams = new LinearLayout.LayoutParams(dp(28), dp(1));
        stampLineParams.topMargin = dp(7);
        stampLineParams.bottomMargin = dp(7);
        heroStamp.addView(stampLine, stampLineParams);
        TextView stampText = text("BUY · SELL\n· REUSE", 8, Color.WHITE, true);
        stampText.setGravity(Gravity.CENTER);
        stampText.setLineSpacing(dp(2), 1.0f);
        heroStamp.addView(stampText);

        // Compact value strip replaces the older oversized trust panel.
        LinearLayout valueStrip = new LinearLayout(this);
        valueStrip.setGravity(Gravity.CENTER_VERTICAL);
        valueStrip.setPadding(dp(13), dp(12), dp(13), dp(12));
        valueStrip.setBackground(rounded(Color.WHITE, BORDER, 15));
        LinearLayout.LayoutParams valueStripParams = new LinearLayout.LayoutParams(-1, -2);
        valueStripParams.setMargins(dp(14), 0, dp(14), dp(14));
        content.addView(valueStrip, valueStripParams);
        addTrustItem(valueStrip, "01", "Find a deal");
        addTrustDivider(valueStrip);
        addTrustItem(valueStrip, "02", "Meet on campus");
        addTrustDivider(valueStrip);
        addTrustItem(valueStrip, "03", "Reuse more");

        // Search field uses the API-backed search endpoint; no fake local results.
        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        searchRow.setPadding(dp(9), dp(5), dp(5), dp(5));
        searchRow.setBackground(rounded(Color.WHITE, Color.rgb(222, 224, 236), 16));
        LinearLayout.LayoutParams searchRowParams = new LinearLayout.LayoutParams(-1, dp(58));
        searchRowParams.setMargins(dp(14), 0, dp(14), dp(12));
        content.addView(searchRow, searchRowParams);
        TextView searchIcon = text("⌕", 29, ACCENT, false);
        searchIcon.setGravity(Gravity.CENTER);
        searchRow.addView(searchIcon, new LinearLayout.LayoutParams(dp(39), -1));
        searchInput = new EditText(this);
        searchInput.setSingleLine(true);
        searchInput.setTextSize(14);
        searchInput.setTextColor(INK);
        searchInput.setHintTextColor(Color.rgb(148, 153, 169));
        searchInput.setHint("Search books, tech, essentials…");
        searchInput.setPadding(0, 0, dp(5), 0);
        searchInput.setBackgroundColor(Color.TRANSPARENT);
        searchInput.setSelectAllOnFocus(false);
        searchRow.addView(searchInput, new LinearLayout.LayoutParams(0, -1, 1f));
        Button searchButton = button("→", ACCENT);
        searchButton.setTextSize(22);
        searchButton.setPadding(0, 0, 0, dp(2));
        searchRow.addView(searchButton, new LinearLayout.LayoutParams(dp(46), dp(46)));
        searchButton.setOnClickListener(v -> submitSearch());
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            submitSearch();
            return true;
        });

        // A secondary seller CTA is discoverable but doesn't crowd the search action.
        LinearLayout sellPrompt = new LinearLayout(this);
        sellPrompt.setGravity(Gravity.CENTER_VERTICAL);
        sellPrompt.setPadding(dp(14), dp(12), dp(12), dp(12));
        sellPrompt.setBackground(rounded(Color.rgb(250, 249, 255), Color.rgb(232, 228, 255), 16));
        LinearLayout.LayoutParams sellPromptParams = new LinearLayout.LayoutParams(-1, -2);
        sellPromptParams.setMargins(dp(14), 0, dp(14), dp(17));
        content.addView(sellPrompt, sellPromptParams);
        LinearLayout sellCopy = new LinearLayout(this);
        sellCopy.setOrientation(LinearLayout.VERTICAL);
        sellPrompt.addView(sellCopy, new LinearLayout.LayoutParams(0, -2, 1f));
        sellCopy.addView(text("Got something to pass on?", 12, INK, true));
        TextView sellHint = text("Give your stuff a second life.", 10, MUTED, false);
        LinearLayout.LayoutParams sellHintParams = new LinearLayout.LayoutParams(-1, -2);
        sellHintParams.topMargin = dp(3);
        sellCopy.addView(sellHint, sellHintParams);
        Button sellButton = button("Sell item +", ACCENT);
        sellButton.setTextSize(12);
        LinearLayout.LayoutParams sellButtonParams = new LinearLayout.LayoutParams(-2, dp(40));
        sellButtonParams.leftMargin = dp(8);
        sellPrompt.addView(sellButton, sellButtonParams);
        sellButton.setOnClickListener(v -> showCreateListingDialog());

        LinearLayout browseHeading = new LinearLayout(this);
        browseHeading.setGravity(Gravity.CENTER_VERTICAL);
        browseHeading.setPadding(dp(18), 0, dp(18), dp(7));
        TextView browseTitle = text("Explore categories", 17, INK, true);
        browseHeading.addView(browseTitle, new LinearLayout.LayoutParams(0, -2, 1f));
        browseHeading.addView(text("PICK A LANE", 9, MUTED, true));
        content.addView(browseHeading);

        HorizontalScrollViewWrap chipScroll = new HorizontalScrollViewWrap(this);
        categoryChipContainer = chipScroll.container;
        categoryChipContainer.setPadding(dp(14), dp(2), dp(14), dp(9));
        content.addView(chipScroll.scrollView, new LinearLayout.LayoutParams(-1, -2));
        refreshCategoryChips();

        LinearLayout feedHeader = new LinearLayout(this);
        feedHeader.setGravity(Gravity.CENTER_VERTICAL);
        feedHeader.setPadding(dp(18), dp(8), dp(18), dp(1));
        TextView feedTitle = text("Fresh on campus", 20, INK, true);
        feedHeader.addView(feedTitle, new LinearLayout.LayoutParams(0, -2, 1f));
        LinearLayout feedActions = new LinearLayout(this);
        feedActions.setGravity(Gravity.CENTER_VERTICAL);
        sortButton = text("Sort: Latest  ▾", 11, Color.rgb(77, 69, 160), true);
        sortButton.setGravity(Gravity.CENTER);
        sortButton.setPadding(dp(9), dp(9), dp(9), dp(9));
        sortButton.setBackground(rounded(Color.rgb(243, 241, 255), Color.rgb(230, 226, 255), 11));
        feedActions.addView(sortButton);
        sortButton.setOnClickListener(v -> showSortDialog());
        refreshButton = button("↻", INK);
        refreshButton.setTextSize(20);
        LinearLayout.LayoutParams refreshParams = new LinearLayout.LayoutParams(dp(39), dp(39));
        refreshParams.leftMargin = dp(7);
        feedActions.addView(refreshButton, refreshParams);
        refreshButton.setOnClickListener(v -> loadListings(searchInput.getText().toString().trim()));
        feedHeader.addView(feedActions);
        content.addView(feedHeader);

        statusView = text("Connecting to the marketplace…", 10, MUTED, false);
        statusView.setPadding(dp(19), dp(3), dp(18), dp(5));
        content.addView(statusView);

        listingContainer = new LinearLayout(this);
        listingContainer.setOrientation(LinearLayout.VERTICAL);
        listingContainer.setPadding(dp(14), dp(7), dp(14), dp(12));
        content.addView(listingContainer, new LinearLayout.LayoutParams(-1, -2));

        root.addView(screenScrollView, new LinearLayout.LayoutParams(-1, 0, 1f));
        buildBottomNavigation(root);
        setContentView(root);
    }

    private void submitSearch() {
        String query = searchInput.getText().toString().trim();
        searchInput.clearFocus();
        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (keyboard != null) keyboard.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        loadListings(query);
        screenScrollView.post(() -> screenScrollView.smoothScrollTo(0,
                Math.max(0, listingContainer.getTop() - dp(20))));
    }

    private void buildBottomNavigation(LinearLayout root) {
        bottomNavContainer = new LinearLayout(this);
        bottomNavContainer.setGravity(Gravity.CENTER_VERTICAL);
        bottomNavContainer.setPadding(dp(10), dp(6), dp(10), dp(6));
        bottomNavContainer.setBackground(rounded(Color.WHITE, Color.rgb(238, 239, 246), 0));
        root.addView(bottomNavContainer, new LinearLayout.LayoutParams(-1, dp(68)));
        refreshBottomNavigation();
    }

    private void refreshBottomNavigation() {
        if (bottomNavContainer == null) return;
        bottomNavContainer.removeAllViews();
        addBottomNavItem("⌂", "Explore", "ALL", () -> {
            selectedCategory = "ALL";
            searchInput.setText("");
            refreshCategoryChips();
            refreshBottomNavigation();
            screenScrollView.smoothScrollTo(0, 0);
            loadListings("");
        });
        addBottomNavItem("♡", "Saved", "SAVED", () -> {
            selectedCategory = "SAVED";
            searchInput.setText("");
            refreshCategoryChips();
            refreshBottomNavigation();
            screenScrollView.smoothScrollTo(0, 0);
            loadListings("");
        });
        addBottomNavItem("+", "Sell", "SELL", this::showCreateListingDialog);
    }

    private void addBottomNavItem(String glyph, String label, String key, Runnable action) {
        boolean active = key.equals(selectedCategory) || ("ALL".equals(key) && !"SAVED".equals(selectedCategory));
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(4), dp(2), dp(4), dp(1));
        item.setBackgroundColor(Color.TRANSPARENT);
        TextView icon = text(glyph, 22, active ? ACCENT : MUTED, true);
        icon.setGravity(Gravity.CENTER);
        if (active) icon.setBackground(rounded(Color.rgb(243, 241, 255), Color.rgb(243, 241, 255), 12));
        item.addView(icon, new LinearLayout.LayoutParams(dp(50), dp(30)));
        TextView title = text(label, 10, active ? ACCENT : MUTED, active);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dp(2);
        item.addView(title, titleParams);
        item.setOnClickListener(v -> action.run());
        bottomNavContainer.addView(item, new LinearLayout.LayoutParams(0, -1, 1f));
    }

    private void addTrustItem(LinearLayout row, String number, String label) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        TextView numberView = text(number, 10, ACCENT, true);
        TextView labelView = text(label, 10, INK, true);
        labelView.setGravity(Gravity.CENTER);
        item.addView(numberView);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(-2, -2);
        labelParams.topMargin = dp(3);
        item.addView(labelView, labelParams);
        row.addView(item, new LinearLayout.LayoutParams(0, -2, 1f));
    }

    private void addTrustDivider(LinearLayout row) {
        View divider = new View(this);
        divider.setBackgroundColor(BORDER);
        row.addView(divider, new LinearLayout.LayoutParams(dp(1), dp(28)));
    }

    private void refreshCategoryChips() {
        if (categoryChipContainer == null) return;
        categoryChipContainer.removeAllViews();
        String[][] filters = {
                {"ALL", "Everything"}, {"TEXTBOOKS", "Books"}, {"ELECTRONICS", "Tech"},
                {"CLOTHING", "Fashion"}, {"HOSTEL_ESSENTIALS", "Room & hostel"},
                {"SPORTS_FITNESS", "Sports"}, {"FREE_STUFF", "Free"}, {"OTHER", "Other"}
        };
        for (String[] filter : filters) {
            boolean active = selectedCategory.equals(filter[0]);
            Button chip = new Button(this);
            chip.setText(filter[1]);
            chip.setAllCaps(false);
            chip.setTextSize(12);
            chip.setTypeface(Typeface.DEFAULT, active ? Typeface.BOLD : Typeface.NORMAL);
            chip.setTextColor(active ? Color.WHITE : Color.rgb(78, 84, 105));
            chip.setMinHeight(dp(36));
            chip.setMinimumHeight(dp(36));
            chip.setPadding(dp(13), 0, dp(13), 0);
            chip.setBackground(rounded(active ? INK : Color.WHITE, active ? INK : BORDER, 18));
            chip.setBackgroundTintList(null);
            chip.setElevation(active ? dp(1) : 0);
            chip.setOnClickListener(v -> {
                selectedCategory = filter[0];
                refreshCategoryChips();
                refreshBottomNavigation();
                renderListings();
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(36));
            params.rightMargin = dp(7);
            categoryChipContainer.addView(chip, params);
        }
    }

    private final class HorizontalScrollViewWrap {
        final android.widget.HorizontalScrollView scrollView;
        final LinearLayout container;
        HorizontalScrollViewWrap(android.content.Context context) {
            scrollView = new android.widget.HorizontalScrollView(context);
            scrollView.setHorizontalScrollBarEnabled(false);
            container = new LinearLayout(context);
            container.setOrientation(LinearLayout.HORIZONTAL);
            scrollView.addView(container, new android.widget.HorizontalScrollView.LayoutParams(-2, -2));
        }
    }

    private void loadListings(String query) {
        setLoading(true);
        listingContainer.removeAllViews();
        LinearLayout loadingCard = new LinearLayout(this);
        loadingCard.setGravity(Gravity.CENTER_VERTICAL);
        loadingCard.setPadding(dp(17), dp(20), dp(17), dp(20));
        loadingCard.setBackground(rounded(Color.WHITE, BORDER, 18));
        TextView loadingMark = text("NM", 15, ACCENT, true);
        loadingMark.setGravity(Gravity.CENTER);
        loadingMark.setBackground(rounded(Color.rgb(243, 241, 255), Color.rgb(243, 241, 255), 12));
        loadingCard.addView(loadingMark, new LinearLayout.LayoutParams(dp(48), dp(48)));
        LinearLayout loadingCopy = new LinearLayout(this);
        loadingCopy.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams loadingCopyParams = new LinearLayout.LayoutParams(0, -2, 1f);
        loadingCopyParams.leftMargin = dp(12);
        loadingCard.addView(loadingCopy, loadingCopyParams);
        loadingCopy.addView(text("Finding the good stuff…", 14, INK, true));
        TextView loadingSub = text("Fetching the latest campus listings", 11, MUTED, false);
        LinearLayout.LayoutParams loadingSubParams = new LinearLayout.LayoutParams(-1, -2);
        loadingSubParams.topMargin = dp(4);
        loadingCopy.addView(loadingSub, loadingSubParams);
        listingContainer.addView(loadingCard);
        executor.execute(() -> {
            try {
                String path = "/api/listings/feed";
                if (query != null && !query.isEmpty()) {
                    path = "/api/listings/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8.name());
                }
                JSONArray results = new JSONArray(request(path, "GET", null));
                runOnUiThread(() -> {
                    setLoading(false);
                    cachedListings = results;
                    renderListings();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    listingContainer.removeAllViews();
                    LinearLayout errorCard = new LinearLayout(this);
                    errorCard.setOrientation(LinearLayout.VERTICAL);
                    errorCard.setPadding(dp(19), dp(21), dp(19), dp(19));
                    errorCard.setBackground(rounded(Color.WHITE, Color.rgb(246, 220, 222), 18));
                    TextView errorMark = text("!", 22, Color.rgb(174, 62, 72), true);
                    errorMark.setGravity(Gravity.CENTER);
                    errorMark.setBackground(rounded(Color.rgb(255, 239, 240), Color.rgb(255, 239, 240), 14));
                    errorCard.addView(errorMark, new LinearLayout.LayoutParams(dp(48), dp(48)));
                    TextView errorTitle = text("The marketplace is taking a break", 17, INK, true);
                    LinearLayout.LayoutParams errorTitleParams = new LinearLayout.LayoutParams(-1, -2);
                    errorTitleParams.topMargin = dp(13);
                    errorCard.addView(errorTitle, errorTitleParams);
                    TextView errorMessage = text("Make sure the Java Spring Boot backend is running, then try again.\n\n" + safeMessage(e), 12, MUTED, false);
                    errorMessage.setLineSpacing(dp(3), 1.0f);
                    LinearLayout.LayoutParams errorMessageParams = new LinearLayout.LayoutParams(-1, -2);
                    errorMessageParams.topMargin = dp(6);
                    errorCard.addView(errorMessage, errorMessageParams);
                    Button retryButton = button("Try again", ACCENT);
                    LinearLayout.LayoutParams retryParams = new LinearLayout.LayoutParams(-1, dp(44));
                    retryParams.topMargin = dp(15);
                    errorCard.addView(retryButton, retryParams);
                    retryButton.setOnClickListener(v -> loadListings(searchInput.getText().toString().trim()));
                    listingContainer.addView(errorCard);
                    statusView.setText("Connection unavailable · check backend");
                });
            }
        });
    }

    private void renderListings() {
        if (listingContainer == null || statusView == null) return;
        listingContainer.removeAllViews();
        Set<String> saved = getSavedIds();
        List<JSONObject> visibleItems = new ArrayList<>();
        for (int i = 0; i < cachedListings.length(); i++) {
            JSONObject item = cachedListings.optJSONObject(i);
            if (item == null) continue;
            String category = item.optString("category", "OTHER").toUpperCase(java.util.Locale.ROOT);
            String id = item.optString("id", "");
            if ("SAVED".equals(selectedCategory) && !saved.contains(id)) continue;
            if (!"ALL".equals(selectedCategory) && !"SAVED".equals(selectedCategory)
                    && !selectedCategory.equals(category)) continue;
            visibleItems.add(item);
        }

        // Sorting runs on the fetched feed, so it never fabricates marketplace data.
        if ("PRICE_LOW".equals(sortMode)) {
            Collections.sort(visibleItems, Comparator.comparingDouble(item -> item.optDouble("price", 0)));
        } else if ("PRICE_HIGH".equals(sortMode)) {
            Collections.sort(visibleItems, (a, b) -> Double.compare(b.optDouble("price", 0), a.optDouble("price", 0)));
        }

        for (JSONObject item : visibleItems) listingContainer.addView(createListingCard(item));
        String feedState = "SAVED".equals(selectedCategory) ? "SAVED"
                : "ALL".equals(selectedCategory) ? "CAMPUS FEED" : prettify(selectedCategory).toUpperCase(java.util.Locale.ROOT);
        statusView.setText(visibleItems.size() + (visibleItems.size() == 1 ? " listing" : " listings")
                + "  ·  " + feedState + (sortMode.equals("LATEST") ? "" : "  ·  SORTED BY PRICE"));

        if (visibleItems.isEmpty()) {
            LinearLayout empty = new LinearLayout(this);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(24), dp(29), dp(24), dp(25));
            empty.setBackground(rounded(Color.WHITE, BORDER, 20));

            TextView mark = text("NM", 22, ACCENT, true);
            mark.setGravity(Gravity.CENTER);
            GradientDrawable emptyMarkBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[]{Color.rgb(246, 244, 255), Color.rgb(231, 226, 255)});
            emptyMarkBg.setCornerRadius(dp(18));
            mark.setBackground(emptyMarkBg);
            empty.addView(mark, new LinearLayout.LayoutParams(dp(62), dp(62)));

            TextView title = text("No listings to show", 18, INK, true);
            title.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
            titleParams.topMargin = dp(14);
            empty.addView(title, titleParams);
            String message = "Try another search or category, or be the first to add something to the campus market.";
            if ("SAVED".equals(selectedCategory)) message = "Listings you save with the heart button will be collected here for later.";
            else if (!"ALL".equals(selectedCategory)) message = "There are no listings in this category at the moment. Explore another category.";
            TextView copy = text(message, 13, MUTED, false);
            copy.setGravity(Gravity.CENTER);
            copy.setLineSpacing(dp(3), 1.0f);
            LinearLayout.LayoutParams copyParams = new LinearLayout.LayoutParams(-1, -2);
            copyParams.topMargin = dp(7);
            empty.addView(copy, copyParams);
            Button emptyAction = button("Explore all listings", ACCENT);
            LinearLayout.LayoutParams emptyActionParams = new LinearLayout.LayoutParams(-1, dp(44));
            emptyActionParams.topMargin = dp(17);
            empty.addView(emptyAction, emptyActionParams);
            emptyAction.setOnClickListener(v -> {
                selectedCategory = "ALL";
                refreshCategoryChips();
                refreshBottomNavigation();
                renderListings();
            });
            listingContainer.addView(empty);
        }
    }

    private View createListingCard(JSONObject item) {
        String category = item.optString("category", "OTHER").toUpperCase(java.util.Locale.ROOT);
        String id = item.optString("id", "");
        boolean isSaved = !id.isEmpty() && getSavedIds().contains(id);
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(13));
        card.setBackground(rounded(Color.WHITE, Color.rgb(232, 234, 243), 19));
        card.setElevation(dp(2));

        LinearLayout topRow = new LinearLayout(this);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView categoryPill = text(categoryDisplay(category), 10, categoryColor(category), true);
        categoryPill.setSingleLine(true);
        categoryPill.setPadding(dp(9), dp(6), dp(9), dp(6));
        categoryPill.setBackground(rounded(categoryTint(category), categoryTint(category), 9));
        topRow.addView(categoryPill);

        String condition = item.optString("conditionDisplayName", prettify(item.optString("condition", "GOOD")));
        if (!condition.trim().isEmpty()) {
            TextView conditionPill = text(condition, 10, MUTED, true);
            conditionPill.setSingleLine(true);
            conditionPill.setPadding(dp(8), dp(6), dp(8), dp(6));
            conditionPill.setBackground(rounded(Color.rgb(247, 248, 251), Color.rgb(240, 241, 247), 9));
            LinearLayout.LayoutParams conditionParams = new LinearLayout.LayoutParams(-2, -2);
            conditionParams.leftMargin = dp(6);
            topRow.addView(conditionPill, conditionParams);
        }
        View topSpacer = new View(this);
        topRow.addView(topSpacer, new LinearLayout.LayoutParams(0, dp(1), 1f));

        TextView saveButton = text(isSaved ? "♥" : "♡", 23,
                isSaved ? Color.rgb(221, 63, 106) : MUTED, true);
        saveButton.setGravity(Gravity.CENTER);
        saveButton.setContentDescription(isSaved ? "Remove saved item" : "Save item");
        saveButton.setBackground(rounded(isSaved ? Color.rgb(255, 240, 245) : Color.rgb(248, 248, 252),
                isSaved ? Color.rgb(255, 225, 236) : Color.rgb(241, 242, 248), 12));
        topRow.addView(saveButton, new LinearLayout.LayoutParams(dp(39), dp(37)));
        saveButton.setOnClickListener(v -> {
            toggleSaved(id);
            refreshBottomNavigation();
            renderListings();
        });
        card.addView(topRow);

        LinearLayout productRow = new LinearLayout(this);
        productRow.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams productRowParams = new LinearLayout.LayoutParams(-1, -2);
        productRowParams.topMargin = dp(13);
        card.addView(productRow, productRowParams);

        View artwork = listingArtwork(item, category);
        LinearLayout.LayoutParams artworkParams = new LinearLayout.LayoutParams(dp(84), dp(100));
        artworkParams.rightMargin = dp(13);
        productRow.addView(artwork, artworkParams);

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        productRow.addView(details, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView title = text(item.optString("title", "Untitled listing"), 16, INK, true);
        title.setMaxLines(2);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        title.setLineSpacing(dp(1), 1.0f);
        details.addView(title);

        TextView description = text(item.optString("description", "No description provided."), 12,
                Color.rgb(101, 107, 126), false);
        description.setMaxLines(2);
        description.setEllipsize(android.text.TextUtils.TruncateAt.END);
        description.setLineSpacing(dp(2), 1.0f);
        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(-1, -2);
        descriptionParams.topMargin = dp(5);
        details.addView(description, descriptionParams);

        LinearLayout priceRow = new LinearLayout(this);
        priceRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams priceRowParams = new LinearLayout.LayoutParams(-1, -2);
        priceRowParams.topMargin = dp(9);
        details.addView(priceRow, priceRowParams);
        String priceText = formatPrice(item.optString("price", "0"));
        TextView price = text(priceText, 19,
                "FREE".equals(priceText) ? Color.rgb(30, 140, 94) : ACCENT, true);
        priceRow.addView(price);
        if (item.optBoolean("negotiable", false)) {
            TextView negotiable = text("OFFER OK", 8, Color.rgb(35, 132, 101), true);
            negotiable.setPadding(dp(6), dp(5), dp(6), dp(5));
            negotiable.setBackground(rounded(Color.rgb(231, 248, 241), Color.rgb(231, 248, 241), 7));
            LinearLayout.LayoutParams negotiableParams = new LinearLayout.LayoutParams(-2, -2);
            negotiableParams.leftMargin = dp(7);
            priceRow.addView(negotiable, negotiableParams);
        }

        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(240, 241, 247));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(-1, dp(1));
        dividerParams.topMargin = dp(13);
        dividerParams.bottomMargin = dp(10);
        card.addView(divider, dividerParams);

        LinearLayout footer = new LinearLayout(this);
        footer.setGravity(Gravity.CENTER_VERTICAL);
        TextView sellerMark = text(initials(item.optString("sellerName", "Student seller")), 10, ACCENT, true);
        sellerMark.setGravity(Gravity.CENTER);
        sellerMark.setBackground(rounded(Color.rgb(242, 240, 255), Color.rgb(242, 240, 255), 11));
        footer.addView(sellerMark, new LinearLayout.LayoutParams(dp(31), dp(31)));

        LinearLayout sellerInfo = new LinearLayout(this);
        sellerInfo.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams sellerInfoParams = new LinearLayout.LayoutParams(0, -2, 1f);
        sellerInfoParams.leftMargin = dp(9);
        footer.addView(sellerInfo, sellerInfoParams);
        TextView sellerView = text(item.optString("sellerName", "Student seller"), 11, INK, true);
        sellerView.setMaxLines(1);
        sellerView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        sellerInfo.addView(sellerView);
        String meetup = item.optString("meetupArea", "Campus meetup");
        TextView meetupView = text("MEETUP  ·  " + meetup, 9, MUTED, false);
        meetupView.setMaxLines(1);
        meetupView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams meetupParams = new LinearLayout.LayoutParams(-1, -2);
        meetupParams.topMargin = dp(3);
        sellerInfo.addView(meetupView, meetupParams);
        TextView openDetails = text("Details  ›", 10, ACCENT, true);
        footer.addView(openDetails);
        card.addView(footer);

        card.setOnClickListener(v -> showListingDetails(id));
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
        cardParams.bottomMargin = dp(13);
        card.setLayoutParams(cardParams);
        card.setClickable(true);
        card.setFocusable(true);
        return card;
    }

    /** Displays a real listing photo when supplied by the API, with custom category art as fallback. */
    private View listingArtwork(JSONObject item, String category) {
        String source = item.optString("primaryImageUrl", "").trim();
        if (source.isEmpty() || "null".equalsIgnoreCase(source)) return categoryArtwork(category);
        String imageUrl = resolveImageUrl(source);
        FrameLayout frame = new FrameLayout(this);
        frame.setBackground(rounded(Color.WHITE, BORDER, 15));
        View fallback = categoryArtwork(category);
        frame.addView(fallback, new FrameLayout.LayoutParams(-1, -1));

        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setBackground(rounded(Color.WHITE, BORDER, 15));
        image.setClipToOutline(true);
        image.setTag(imageUrl);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(-1, -1);
        frame.addView(image, imageParams);
        Bitmap cached = imageCache.get(imageUrl);
        if (cached != null) {
            image.setImageBitmap(cached);
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.INVISIBLE);
            imageExecutor.execute(() -> {
                Bitmap loaded = downloadListingImage(imageUrl);
                if (loaded == null) return;
                imageCache.put(imageUrl, loaded);
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed() && imageUrl.equals(image.getTag())) {
                        image.setImageBitmap(loaded);
                        image.setVisibility(View.VISIBLE);
                    }
                });
            });
        }
        return frame;
    }

    private String resolveImageUrl(String source) {
        if (source.startsWith("https://") || source.startsWith("http://")) return source;
        if (source.startsWith("/")) return apiBaseUrl + source;
        return apiBaseUrl + "/" + source.replaceFirst("^\\./", "");
    }

    private Bitmap downloadListingImage(String imageUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.setConnectTimeout(4500);
            connection.setReadTimeout(6500);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Accept", "image/*");
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) return null;
            int contentLength = connection.getContentLength();
            final int maxBytes = 4 * 1024 * 1024;
            if (contentLength > maxBytes) return null;
            byte[] bytes;
            try (InputStream input = connection.getInputStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int count;
                int total = 0;
                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    if (total > maxBytes) return null;
                    output.write(buffer, 0, count);
                }
                bytes = output.toByteArray();
            }
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bounds);
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            int sampleSize = 1;
            while (bounds.outWidth / (sampleSize * 2) >= 320
                    || bounds.outHeight / (sampleSize * 2) >= 320) sampleSize *= 2;
            options.inSampleSize = sampleSize;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private View categoryArtwork(String category) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(5), dp(8), dp(5), dp(7));
        int tint = categoryTint(category);
        GradientDrawable artBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{tint, mixWithWhite(tint, 0.43f)});
        artBg.setCornerRadius(dp(15));
        artBg.setStroke(dp(1), mixWithWhite(tint, 0.20f));
        tile.setBackground(artBg);
        String glyph;
        String caption;
        switch (category) {
            case "TEXTBOOKS": glyph = "Aa"; caption = "READ & LEARN"; break;
            case "ELECTRONICS": glyph = "01"; caption = "TECH FINDS"; break;
            case "CLOTHING": glyph = "✦"; caption = "PRE-LOVED"; break;
            case "HOSTEL_ESSENTIALS": glyph = "⌂"; caption = "ROOM READY"; break;
            case "SPORTS_FITNESS": glyph = "↗"; caption = "GET MOVING"; break;
            case "FREE_STUFF": glyph = "FREE"; caption = "PASS IT ON"; break;
            default: glyph = "NM"; caption = "GOOD FINDS"; break;
        }
        TextView artMark = text(glyph, glyph.length() > 2 ? 19 : 28, categoryColor(category), true);
        artMark.setGravity(Gravity.CENTER);
        tile.addView(artMark, new LinearLayout.LayoutParams(-1, 0, 1f));
        TextView artCaption = text(caption, 7, categoryColor(category), true);
        artCaption.setGravity(Gravity.CENTER);
        artCaption.setLetterSpacing(0.03f);
        tile.addView(artCaption);
        return tile;
    }

    private int mixWithWhite(int color, float whiteShare) {
        int red = Color.red(color), green = Color.green(color), blue = Color.blue(color);
        return Color.rgb((int) (red + (255 - red) * whiteShare),
                (int) (green + (255 - green) * whiteShare),
                (int) (blue + (255 - blue) * whiteShare));
    }

    private String categoryDisplay(String category) {
        switch (category) {
            case "TEXTBOOKS": return "BOOKS & STUDY";
            case "ELECTRONICS": return "TECH & GADGETS";
            case "CLOTHING": return "FASHION";
            case "HOSTEL_ESSENTIALS": return "HOSTEL ESSENTIALS";
            case "SPORTS_FITNESS": return "SPORTS & FITNESS";
            case "FREE_STUFF": return "FREE FIND";
            default: return "CAMPUS FIND";
        }
    }

    private String initials(String name) {
        if (name == null || name.trim().isEmpty()) return "NM";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase(java.util.Locale.ROOT);
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(java.util.Locale.ROOT);
    }

    private void showSortDialog() {
        String[] options = {"Latest first", "Price: low to high", "Price: high to low"};
        int checked = "PRICE_LOW".equals(sortMode) ? 1 : "PRICE_HIGH".equals(sortMode) ? 2 : 0;
        new AlertDialog.Builder(this)
                .setTitle("Sort listings")
                .setSingleChoiceItems(options, checked, (dialog, which) -> {
                    sortMode = which == 1 ? "PRICE_LOW" : which == 2 ? "PRICE_HIGH" : "LATEST";
                    sortButton.setText("Sort: " + (which == 1 ? "Price ↑" : which == 2 ? "Price ↓" : "Latest") + "  ▾");
                    dialog.dismiss();
                    renderListings();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private Set<String> getSavedIds() {
        Set<String> ids = getSharedPreferences("nightmarket_saved", MODE_PRIVATE)
                .getStringSet("listing_ids", new HashSet<>());
        return ids == null ? new HashSet<String>() : new HashSet<>(ids);
    }

    private void toggleSaved(String id) {
        if (id == null || id.trim().isEmpty()) {
            Toast.makeText(this, "This item cannot be saved yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        Set<String> ids = getSavedIds();
        boolean added;
        if (ids.contains(id)) {
            ids.remove(id);
            added = false;
        } else {
            ids.add(id);
            added = true;
        }
        getSharedPreferences("nightmarket_saved", MODE_PRIVATE).edit().putStringSet("listing_ids", ids).apply();
        Toast.makeText(this, added ? "Saved to your list" : "Removed from saved", Toast.LENGTH_SHORT).show();
    }

    private int categoryColor(String category) {
        switch (category) {
            case "TEXTBOOKS": return Color.rgb(55, 101, 184);
            case "ELECTRONICS": return Color.rgb(105, 82, 220);
            case "CLOTHING": return Color.rgb(190, 75, 127);
            case "HOSTEL_ESSENTIALS": return Color.rgb(40, 139, 116);
            case "SPORTS_FITNESS": return Color.rgb(195, 117, 38);
            case "FREE_STUFF": return Color.rgb(40, 137, 92);
            default: return Color.rgb(94, 100, 120);
        }
    }

    private int categoryTint(String category) {
        switch (category) {
            case "TEXTBOOKS": return Color.rgb(235, 242, 255);
            case "ELECTRONICS": return Color.rgb(241, 238, 255);
            case "CLOTHING": return Color.rgb(255, 239, 247);
            case "HOSTEL_ESSENTIALS": return Color.rgb(232, 248, 243);
            case "SPORTS_FITNESS": return Color.rgb(255, 244, 231);
            case "FREE_STUFF": return Color.rgb(232, 249, 239);
            default: return Color.rgb(242, 243, 248);
        }
    }

    private void showListingDetails(String id) {
        if (id == null || id.isEmpty()) return;
        executor.execute(() -> {
            try {
                JSONObject detail = new JSONObject(request("/api/listings/" + id, "GET", null));
                runOnUiThread(() -> showListingDetailsDialog(detail, id));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Could not load listing details. Check the connection and try again.", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void showListingDetailsDialog(JSONObject detail, String id) {
        String category = detail.optString("category", "OTHER").toUpperCase(java.util.Locale.ROOT);
        boolean saved = getSavedIds().contains(id);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(19), dp(10), dp(19), dp(10));
        scroll.addView(body, new ScrollView.LayoutParams(-1, -2));

        LinearLayout summary = new LinearLayout(this);
        summary.setGravity(Gravity.CENTER_VERTICAL);
        summary.setPadding(dp(13), dp(13), dp(13), dp(13));
        summary.setBackground(rounded(Color.rgb(248, 247, 255), Color.rgb(233, 229, 255), 17));
        body.addView(summary, new LinearLayout.LayoutParams(-1, -2));
        View art = listingArtwork(detail, category);
        summary.addView(art, new LinearLayout.LayoutParams(dp(72), dp(82)));
        LinearLayout summaryCopy = new LinearLayout(this);
        summaryCopy.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams summaryCopyParams = new LinearLayout.LayoutParams(0, -2, 1f);
        summaryCopyParams.leftMargin = dp(12);
        summary.addView(summaryCopy, summaryCopyParams);
        TextView title = text(detail.optString("title", "Listing details"), 17, INK, true);
        title.setMaxLines(3);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        summaryCopy.addView(title);
        String detailPrice = formatPrice(detail.optString("price", "0"));
        TextView price = text(detailPrice, 22,
                "FREE".equals(detailPrice) ? Color.rgb(30, 140, 94) : ACCENT, true);
        LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(-1, -2);
        priceParams.topMargin = dp(7);
        summaryCopy.addView(price, priceParams);

        LinearLayout metaRow = new LinearLayout(this);
        metaRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(-1, -2);
        metaParams.topMargin = dp(14);
        body.addView(metaRow, metaParams);
        TextView categoryLabel = text(categoryDisplay(category), 9, categoryColor(category), true);
        categoryLabel.setPadding(dp(9), dp(7), dp(9), dp(7));
        categoryLabel.setBackground(rounded(categoryTint(category), categoryTint(category), 9));
        metaRow.addView(categoryLabel);
        TextView conditionLabel = text(detail.optString("conditionDisplayName",
                prettify(detail.optString("condition", "GOOD"))), 10, MUTED, true);
        conditionLabel.setPadding(dp(9), dp(7), dp(9), dp(7));
        conditionLabel.setBackground(rounded(Color.rgb(246, 247, 250), BORDER, 9));
        LinearLayout.LayoutParams conditionLabelParams = new LinearLayout.LayoutParams(-2, -2);
        conditionLabelParams.leftMargin = dp(7);
        metaRow.addView(conditionLabel, conditionLabelParams);
        if (detail.optBoolean("negotiable", false)) {
            TextView negotiableLabel = text("OPEN TO OFFERS", 8, Color.rgb(35, 132, 101), true);
            negotiableLabel.setPadding(dp(8), dp(7), dp(8), dp(7));
            negotiableLabel.setBackground(rounded(Color.rgb(231, 248, 241), Color.rgb(231, 248, 241), 9));
            LinearLayout.LayoutParams negotiableLabelParams = new LinearLayout.LayoutParams(-2, -2);
            negotiableLabelParams.leftMargin = dp(7);
            metaRow.addView(negotiableLabel, negotiableLabelParams);
        }

        addDetailSection(body, "ABOUT THIS ITEM",
                detail.optString("description", "No description provided."));
        String categorySummary = detail.optString("categorySummary", "");
        if (!categorySummary.trim().isEmpty()) addDetailSection(body, "ITEM SPECIFICS", categorySummary);

        // A clear contact action belongs in the listing details. It launches the
        // phone/email app when the API supplies seller contact information.
        Button contactSellerButton = button("Contact seller  →", ACCENT);
        contactSellerButton.setTextSize(14);
        LinearLayout.LayoutParams contactSellerParams = new LinearLayout.LayoutParams(-1, dp(48));
        contactSellerParams.topMargin = dp(16);
        body.addView(contactSellerButton, contactSellerParams);
        contactSellerButton.setOnClickListener(v -> contactSeller(detail));

        LinearLayout meetupCard = new LinearLayout(this);
        meetupCard.setOrientation(LinearLayout.VERTICAL);
        meetupCard.setPadding(dp(13), dp(12), dp(13), dp(12));
        meetupCard.setBackground(rounded(Color.WHITE, BORDER, 14));
        LinearLayout.LayoutParams meetupCardParams = new LinearLayout.LayoutParams(-1, -2);
        meetupCardParams.topMargin = dp(14);
        body.addView(meetupCard, meetupCardParams);
        meetupCard.addView(text("CAMPUS MEETUP", 9, MUTED, true));
        TextView meetup = text(detail.optString("meetupArea", "Campus meetup"), 14, INK, true);
        LinearLayout.LayoutParams meetupTextParams = new LinearLayout.LayoutParams(-1, -2);
        meetupTextParams.topMargin = dp(5);
        meetupCard.addView(meetup, meetupTextParams);
        TextView seller = text("Listed by " + detail.optString("sellerName", "Student seller"), 11, MUTED, false);
        LinearLayout.LayoutParams sellerParams = new LinearLayout.LayoutParams(-1, -2);
        sellerParams.topMargin = dp(4);
        meetupCard.addView(seller, sellerParams);

        JSONArray checklist = detail.optJSONArray("safetyChecklist");
        if (checklist != null && checklist.length() > 0) {
            TextView safetyTitle = text("MEET SAFELY", 10, Color.rgb(34, 130, 96), true);
            LinearLayout.LayoutParams safetyTitleParams = new LinearLayout.LayoutParams(-1, -2);
            safetyTitleParams.topMargin = dp(16);
            body.addView(safetyTitle, safetyTitleParams);
            for (int i = 0; i < checklist.length(); i++) {
                TextView safetyItem = text("•  " + checklist.optString(i), 12, MUTED, false);
                safetyItem.setLineSpacing(dp(2), 1.0f);
                LinearLayout.LayoutParams safetyItemParams = new LinearLayout.LayoutParams(-1, -2);
                safetyItemParams.topMargin = dp(6);
                body.addView(safetyItem, safetyItemParams);
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(scroll)
                .setPositiveButton("Done", null)
                .setNeutralButton(saved ? "♥ Saved" : "♡ Save item", (d, which) -> {
                    toggleSaved(id);
                    refreshBottomNavigation();
                    renderListings();
                })
                .create();
        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ACCENT);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ACCENT);
        });
        dialog.show();
    }

    /**
     * Opens a genuine phone/SMS/email composer when the listing API provides
     * seller contact details. The current demo backend may not provide them yet.
     */
    private void contactSeller(JSONObject detail) {
        String sellerName = detail.optString("sellerName", "the seller");
        String title = detail.optString("title", "this item");
        String phone = firstNonBlank(detail, "sellerPhone", "contactPhone", "phone");
        String email = firstNonBlank(detail, "sellerEmail", "contactEmail", "email");

        if (phone.isEmpty() && email.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Seller contact not available")
                    .setMessage("The current NightMarket backend returns the seller's name, but it does not yet provide a phone number or email address. A real contact action needs seller contact details in the backend response or an in-app messaging endpoint.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        List<String> actions = new ArrayList<>();
        List<String> actionTypes = new ArrayList<>();
        if (!phone.isEmpty()) {
            actions.add("Call seller");
            actionTypes.add("CALL");
            actions.add("Text seller");
            actionTypes.add("SMS");
        }
        if (!email.isEmpty()) {
            actions.add("Email seller");
            actionTypes.add("EMAIL");
        }

        new AlertDialog.Builder(this)
                .setTitle("Contact " + sellerName)
                .setItems(actions.toArray(new String[0]), (dialog, which) -> {
                    String action = actionTypes.get(which);
                    try {
                        Intent intent;
                        if ("CALL".equals(action)) {
                            intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        } else if ("SMS".equals(action)) {
                            intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phone, null));
                            intent.putExtra("sms_body", "Hi " + sellerName + ", I'm interested in your listing: " + title + ". Is it still available?");
                        } else {
                            Uri emailUri = Uri.parse("mailto:" + Uri.encode(email)
                                    + "?subject=" + Uri.encode("Interested in: " + title)
                                    + "&body=" + Uri.encode("Hi " + sellerName
                                    + ",\n\nI'm interested in your NightMarket listing: " + title
                                    + ". Is it still available?\n"));
                            intent = new Intent(Intent.ACTION_SENDTO, emailUri);
                        }
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, "No compatible app is installed for this contact method.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Could not open the selected contact method.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String firstNonBlank(JSONObject object, String... keys) {
        for (String key : keys) {
            String value = object.optString(key, "").trim();
            if (!value.isEmpty() && !"null".equalsIgnoreCase(value)) return value;
        }
        return "";
    }

    private void addDetailSection(LinearLayout body, String heading, String content) {
        TextView headingView = text(heading, 10, MUTED, true);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, -2);
        headingParams.topMargin = dp(16);
        body.addView(headingView, headingParams);
        TextView contentView = text(content, 13, INK, false);
        contentView.setLineSpacing(dp(3), 1.0f);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(-1, -2);
        contentParams.topMargin = dp(6);
        body.addView(contentView, contentParams);
    }

    private void showCreateListingDialog() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(6), dp(20), dp(4));
        TextView formIntro = text("Give your pre-loved items a new home. Clear details help other students buy with confidence.", 12, MUTED, false);
        formIntro.setLineSpacing(dp(3), 1.0f);
        formIntro.setPadding(dp(12), dp(12), dp(12), dp(12));
        formIntro.setBackground(rounded(Color.rgb(247, 246, 255), Color.rgb(235, 231, 255), 13));
        form.addView(formIntro);

        EditText title = formInput("Item title (3–100 characters)", InputType.TYPE_CLASS_TEXT);
        EditText description = formInput("Description (at least 10 characters)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        description.setMinLines(3);
        description.setGravity(Gravity.TOP | Gravity.START);
        EditText price = formInput("Price in INR (0 for free)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText sellerName = formInput("Your display name", InputType.TYPE_CLASS_TEXT);
        EditText sellerPhone = formInput("Phone number (recommended)", InputType.TYPE_CLASS_PHONE);
        EditText sellerEmail = formInput("Email address (recommended)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText meetup = formInput("Campus meetup area", InputType.TYPE_CLASS_TEXT);
        Spinner category = spinner(CATEGORY_LABELS);
        Spinner condition = spinner(CONDITION_LABELS);

        addFormField(form, "Item title", title);
        addFormField(form, "Description", description);
        addFormField(form, "Price (₹)", price);
        addFormField(form, "Your name", sellerName);
        TextView contactHint = text("Add at least one contact method. These details appear on your listing's detail page so buyers can reach you.", 10, MUTED, false);
        contactHint.setLineSpacing(dp(2), 1.0f);
        LinearLayout.LayoutParams contactHintParams = new LinearLayout.LayoutParams(-1, -2);
        contactHintParams.topMargin = dp(3);
        form.addView(contactHint, contactHintParams);
        addFormField(form, "Phone number", sellerPhone);
        addFormField(form, "Email address", sellerEmail);
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
            String sellerNameValue = sellerName.getText().toString().trim();
            String sellerPhoneValue = sellerPhone.getText().toString().trim();
            String sellerEmailValue = sellerEmail.getText().toString().trim();
            String meetupValue = meetup.getText().toString().trim();
            if (titleValue.length() < 3 || descValue.length() < 10 || priceValue.isEmpty() || meetupValue.isEmpty()
                    || sellerNameValue.length() < 2 || (sellerPhoneValue.isEmpty() && sellerEmailValue.isEmpty())) {
                Toast.makeText(this, "Add the item details, your name, meetup area, and at least one contact method.", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("title", titleValue);
                requestBody.put("description", descValue);
                double parsedPrice = Double.parseDouble(priceValue);
                if (!Double.isFinite(parsedPrice) || parsedPrice < 0) {
                    Toast.makeText(this, "Price must be a non-negative number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestBody.put("price", parsedPrice);
                requestBody.put("category", CATEGORY_VALUES[category.getSelectedItemPosition()]);
                requestBody.put("condition", CONDITION_VALUES[condition.getSelectedItemPosition()]);
                requestBody.put("sellerName", sellerNameValue);
                requestBody.put("sellerPhone", sellerPhoneValue);
                requestBody.put("sellerEmail", sellerEmailValue);
                requestBody.put("meetupArea", meetupValue);
                requestBody.put("negotiable", true);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("Publishing…");
                executor.execute(() -> {
                    try {
                        request("/api/listings/add", "POST", requestBody);
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(this, "Listing published!", Toast.LENGTH_SHORT).show();
                            searchInput.setText("");
                            selectedCategory = "ALL";
                            refreshCategoryChips();
                            refreshBottomNavigation();
                            loadListings("");
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("Publish listing");
                            Toast.makeText(this, "Could not publish: " + safeMessage(e), Toast.LENGTH_LONG).show();
                        });
                    }
                });
            } catch (JSONException | NumberFormatException e) {
                Toast.makeText(this, "Enter a valid price.", Toast.LENGTH_SHORT).show();
            }
        }));
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ACCENT);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(MUTED);
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
        if (refreshButton == null) return;
        refreshButton.setEnabled(!loading);
        refreshButton.setAlpha(loading ? 0.55f : 1f);
        refreshButton.setText(loading ? "…" : "↻");
    }

    private EditText formInput(String hint, int inputType) {
        EditText input = new EditText(this);
        input.setSingleLine((inputType & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0);
        input.setInputType(inputType);
        input.setHint(hint);
        input.setTextSize(14);
        input.setTextColor(INK);
        input.setHintTextColor(Color.rgb(148, 153, 169));
        input.setPadding(dp(12), dp(10), dp(12), dp(10));
        input.setBackground(rounded(Color.WHITE, BORDER, 12));
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
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(Color.WHITE);
        button.setPadding(dp(12), 0, dp(12), 0);
        button.setBackground(rounded(color, color, 13));
        button.setBackgroundTintList(null);
        button.setElevation(dp(1));
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
        imageExecutor.shutdownNow();
        imageCache.evictAll();
        super.onDestroy();
    }
}
