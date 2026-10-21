package com.nightmarket.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nightmarket.app.MainActivity;
import com.nightmarket.app.R;
import com.nightmarket.app.adapter.MyListingAdapter;
import com.nightmarket.app.model.Listing;
import com.nightmarket.app.repository.RemoteListingRepository;

import java.util.ArrayList;
import java.util.List;

public class MyListingsFragment extends Fragment implements MyListingAdapter.OnMyListingActionListener {

    private ImageView btnMyListingsBack;
    private TextView tabActive;
    private TextView tabSold;
    private SwipeRefreshLayout swipeRefreshMyListings;
    private RecyclerView recyclerViewMyListings;
    private ProgressBar progressBarMyListings;
    private LinearLayout layoutMyListingsEmpty;
    private Button btnMyAddNewItem;

    private RemoteListingRepository repository;
    private MyListingAdapter adapter;
    private final List<Listing> myListings = new ArrayList<>();
    private boolean showingActive = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_listings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new RemoteListingRepository(requireContext());
        bindViews(view);
        setupRecyclerView();
        setupListeners();
        loadMyListings();
    }

    private void bindViews(View view) {
        btnMyListingsBack = view.findViewById(R.id.btnMyListingsBack);
        tabActive = view.findViewById(R.id.tabActive);
        tabSold = view.findViewById(R.id.tabSold);
        swipeRefreshMyListings = view.findViewById(R.id.swipeRefreshMyListings);
        recyclerViewMyListings = view.findViewById(R.id.recyclerViewMyListings);
        progressBarMyListings = view.findViewById(R.id.progressBarMyListings);
        layoutMyListingsEmpty = view.findViewById(R.id.layoutMyListingsEmpty);
        btnMyAddNewItem = view.findViewById(R.id.btnMyAddNewItem);
    }

    private void setupRecyclerView() {
        adapter = new MyListingAdapter(requireContext(), this);
        recyclerViewMyListings.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewMyListings.setAdapter(adapter);
    }

    private void setupListeners() {
        btnMyListingsBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        swipeRefreshMyListings.setOnRefreshListener(this::loadMyListings);

        tabActive.setOnClickListener(v -> {
            showingActive = true;
            updateTabAppearance();
            filterListings();
        });

        tabSold.setOnClickListener(v -> {
            showingActive = false;
            updateTabAppearance();
            filterListings();
        });

        btnMyAddNewItem.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddItem();
            }
        });
    }

    private void updateTabAppearance() {
        if (showingActive) {
            tabActive.setBackgroundResource(R.drawable.bg_badge_lime);
            tabActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.nightmarket_midnight));
            tabSold.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));
            tabSold.setTextColor(ContextCompat.getColor(requireContext(), R.color.nightmarket_text_muted));
        } else {
            tabSold.setBackgroundResource(R.drawable.bg_badge_lime);
            tabSold.setTextColor(ContextCompat.getColor(requireContext(), R.color.nightmarket_midnight));
            tabActive.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));
            tabActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.nightmarket_text_muted));
        }
    }

    private void loadMyListings() {
        progressBarMyListings.setVisibility(View.VISIBLE);
        layoutMyListingsEmpty.setVisibility(View.GONE);

        repository.fetchMyListings(new RemoteListingRepository.ApiCallback<List<Listing>>() {
            @Override
            public void onSuccess(List<Listing> result) {
                if (!isAdded()) return;
                progressBarMyListings.setVisibility(View.GONE);
                swipeRefreshMyListings.setRefreshing(false);
                myListings.clear();
                if (result != null) {
                    myListings.addAll(result);
                }
                updateTabCounts();
                filterListings();
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                progressBarMyListings.setVisibility(View.GONE);
                swipeRefreshMyListings.setRefreshing(false);
                Toast.makeText(getContext(), "Could not load listings: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTabCounts() {
        int activeCount = 0;
        int soldCount = 0;
        for (Listing item : myListings) {
            if (item.isSold()) {
                soldCount++;
            } else {
                activeCount++;
            }
        }
        tabActive.setText("Active " + activeCount);
        tabSold.setText("Sold " + soldCount);
    }

    private void filterListings() {
        List<Listing> filtered = new ArrayList<>();
        for (Listing item : myListings) {
            if (showingActive && !item.isSold()) {
                filtered.add(item);
            } else if (!showingActive && item.isSold()) {
                filtered.add(item);
            }
        }

        adapter.setListings(filtered);
        layoutMyListingsEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onListingClick(Listing listing) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToProductDetail(listing.getId());
        }
    }

    @Override
    public void onMarkSoldClick(Listing listing) {
        repository.markSold(listing.getId(), new RemoteListingRepository.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Listing marked as SOLD!", Toast.LENGTH_SHORT).show();
                loadMyListings();
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
