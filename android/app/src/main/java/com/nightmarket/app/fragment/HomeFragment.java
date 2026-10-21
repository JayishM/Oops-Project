package com.nightmarket.app.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.nightmarket.app.MainActivity;
import com.nightmarket.app.R;
import com.nightmarket.app.adapter.ListingAdapter;
import com.nightmarket.app.model.Listing;
import com.nightmarket.app.repository.RemoteListingRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ListingAdapter.OnListingClickListener {

    private SwipeRefreshLayout swipeRefresh;
    private EditText etSearch;
    private ImageView btnClearSearch;
    private ProgressBar progressBar;
    private MaterialCardView cardError;
    private TextView tvErrorMessage;
    private Button btnRetry;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;
    private RecyclerView recyclerViewListings;
    private TextView tvFilterStatus;
    private TextView tvSectionTitle;

    private RemoteListingRepository repository;
    private ListingAdapter adapter;
    private final List<Listing> allLoadedListings = new ArrayList<>();
    private String selectedCategory = "ALL";
    private boolean isSavedOnly = false;

    public static HomeFragment newInstance(boolean savedOnly) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putBoolean("saved_only", savedOnly);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new RemoteListingRepository(requireContext());
        if (getArguments() != null) {
            isSavedOnly = getArguments().getBoolean("saved_only", false);
        }

        bindViews(view);
        setupRecyclerView();
        setupListeners(view);
        loadFeed();
    }

    private void bindViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        etSearch = view.findViewById(R.id.etSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        progressBar = view.findViewById(R.id.progressBar);
        cardError = view.findViewById(R.id.cardError);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        btnRetry = view.findViewById(R.id.btnRetry);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        recyclerViewListings = view.findViewById(R.id.recyclerViewListings);
        tvFilterStatus = view.findViewById(R.id.tvFilterStatus);
        tvSectionTitle = view.findViewById(R.id.tvSectionTitle);
    }

    private void setupRecyclerView() {
        adapter = new ListingAdapter(requireContext(), repository, this);
        recyclerViewListings.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewListings.setAdapter(adapter);
    }

    private void setupListeners(View root) {
        swipeRefresh.setOnRefreshListener(this::loadFeed);

        btnRetry.setOnClickListener(v -> loadFeed());

        // Category Buttons (Figma Screen 01)
        root.findViewById(R.id.btnCatBooks).setOnClickListener(v -> filterCategory("TEXTBOOKS", "Books"));
        root.findViewById(R.id.btnCatTech).setOnClickListener(v -> filterCategory("ELECTRONICS", "Tech"));
        root.findViewById(R.id.btnCatRoom).setOnClickListener(v -> filterCategory("HOSTEL_ESSENTIALS", "Room"));
        root.findViewById(R.id.btnCatMore).setOnClickListener(v -> filterCategory("ALL", "All categories"));

        // Featured Banner action
        Button btnExploreFinds = root.findViewById(R.id.btnExploreFinds);
        btnExploreFinds.setOnClickListener(v -> {
            filterCategory("ALL", "All finds");
            Toast.makeText(getContext(), "Showing all campus finds", Toast.LENGTH_SHORT).show();
        });

        // See all button
        root.findViewById(R.id.btnSeeAll).setOnClickListener(v -> filterCategory("ALL", "All campus items"));

        // Search action
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                if (s.length() == 0) {
                    applyFilter();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            applyFilter();
        });
    }

    private void filterCategory(String categoryKey, String displayName) {
        selectedCategory = categoryKey;
        tvFilterStatus.setText("Showing: " + displayName);
        applyFilter();
    }

    public void setSavedOnlyMode(boolean savedOnly) {
        this.isSavedOnly = savedOnly;
        if (savedOnly) {
            tvSectionTitle.setText("Saved items");
            tvFilterStatus.setText("Items you favorited");
        } else {
            tvSectionTitle.setText(getString(R.string.popular_near_you));
            tvFilterStatus.setText(getString(R.string.campus_pickup_badge));
        }
        applyFilter();
    }

    private void loadFeed() {
        progressBar.setVisibility(View.VISIBLE);
        cardError.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        repository.fetchFeed(new RemoteListingRepository.ApiCallback<List<Listing>>() {
            @Override
            public void onSuccess(List<Listing> result) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                allLoadedListings.clear();
                if (result != null) {
                    allLoadedListings.addAll(result);
                }
                applyFilter();
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                cardError.setVisibility(View.VISIBLE);
                tvErrorMessage.setText(errorMessage);
            }
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            applyFilter();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        cardError.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        repository.search(query, new RemoteListingRepository.ApiCallback<List<Listing>>() {
            @Override
            public void onSuccess(List<Listing> result) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                allLoadedListings.clear();
                if (result != null) {
                    allLoadedListings.addAll(result);
                }
                applyFilter();
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Search failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {
        List<Listing> filtered = new ArrayList<>();
        java.util.Set<String> savedIds = repository.getSavedListingIds();

        for (Listing item : allLoadedListings) {
            if (isSavedOnly && !savedIds.contains(item.getId())) {
                continue;
            }
            if (!"ALL".equalsIgnoreCase(selectedCategory) && !selectedCategory.equalsIgnoreCase(item.getCategory())) {
                continue;
            }
            filtered.add(item);
        }

        adapter.setListings(filtered);

        if (filtered.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            if (isSavedOnly) {
                tvEmptyMessage.setText("Tap the heart on any listing to save it here for quick access.");
            } else {
                tvEmptyMessage.setText("No items found in this section. Try exploring other categories.");
            }
        } else {
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onListingClick(Listing listing) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToProductDetail(listing.getId());
        }
    }
}
