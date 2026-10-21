package com.nightmarket.app.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nightmarket.app.MainActivity;
import com.nightmarket.app.R;
import com.nightmarket.app.model.Listing;
import com.nightmarket.app.repository.RemoteListingRepository;

public class ProductDetailFragment extends Fragment {

    private static final String ARG_LISTING_ID = "listing_id";

    private ImageView btnBack;
    private ImageView btnDetailSave;
    private FrameLayout detailArtworkFrame;
    private TextView tvDetailArtworkPlaceholder;
    private ImageView ivDetailProductImage;
    private TextView tvDetailConditionBadge;
    private TextView tvDetailTitle;
    private TextView tvDetailPrice;
    private TextView tvDetailSavingsBadge;
    private TextView tvDetailCategory;
    private TextView tvDetailDescription;
    private TextView tvCategorySummary;
    private TextView tvSellerAvatar;
    private TextView tvSellerName;
    private TextView tvSellerCampus;
    private TextView tvMeetupLocation;
    private Button btnMessageSeller;
    private LinearLayout layoutDirectContact;
    private Button btnCallSeller;
    private Button btnEmailSeller;
    private Button btnMarkSold;

    private RemoteListingRepository repository;
    private String listingId;
    private Listing currentListing;

    public static ProductDetailFragment newInstance(String listingId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_ID, listingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new RemoteListingRepository(requireContext());
        if (getArguments() != null) {
            listingId = getArguments().getString(ARG_LISTING_ID);
        }

        bindViews(view);
        setupListeners();
        loadListingDetails();
    }

    private void bindViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        btnDetailSave = view.findViewById(R.id.btnDetailSave);
        detailArtworkFrame = view.findViewById(R.id.detailArtworkFrame);
        tvDetailArtworkPlaceholder = view.findViewById(R.id.tvDetailArtworkPlaceholder);
        ivDetailProductImage = view.findViewById(R.id.ivDetailProductImage);
        tvDetailConditionBadge = view.findViewById(R.id.tvDetailConditionBadge);
        tvDetailTitle = view.findViewById(R.id.tvDetailTitle);
        tvDetailPrice = view.findViewById(R.id.tvDetailPrice);
        tvDetailSavingsBadge = view.findViewById(R.id.tvDetailSavingsBadge);
        tvDetailCategory = view.findViewById(R.id.tvDetailCategory);
        tvDetailDescription = view.findViewById(R.id.tvDetailDescription);
        tvCategorySummary = view.findViewById(R.id.tvCategorySummary);
        tvSellerAvatar = view.findViewById(R.id.tvSellerAvatar);
        tvSellerName = view.findViewById(R.id.tvSellerName);
        tvSellerCampus = view.findViewById(R.id.tvSellerCampus);
        tvMeetupLocation = view.findViewById(R.id.tvMeetupLocation);
        btnMessageSeller = view.findViewById(R.id.btnMessageSeller);
        layoutDirectContact = view.findViewById(R.id.layoutDirectContact);
        btnCallSeller = view.findViewById(R.id.btnCallSeller);
        btnEmailSeller = view.findViewById(R.id.btnEmailSeller);
        btnMarkSold = view.findViewById(R.id.btnMarkSold);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnDetailSave.setOnClickListener(v -> {
            if (currentListing != null) {
                boolean saved = repository.toggleSaved(currentListing.getId());
                updateSaveIcon(saved);
                Toast.makeText(getContext(), saved ? "Item saved" : "Removed from saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnMessageSeller.setOnClickListener(v -> {
            if (currentListing != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToContactSeller(currentListing);
            }
        });

        btnCallSeller.setOnClickListener(v -> {
            if (currentListing != null && !currentListing.getSellerPhone().isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + currentListing.getSellerPhone()));
                startActivity(callIntent);
            } else {
                Toast.makeText(getContext(), "No phone number provided by seller", Toast.LENGTH_SHORT).show();
            }
        });

        btnEmailSeller.setOnClickListener(v -> {
            if (currentListing != null && !currentListing.getSellerEmail().isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + currentListing.getSellerEmail()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "NightMarket: " + currentListing.getTitle());
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + currentListing.getSellerName() + ",\nI'm interested in your listing: " + currentListing.getTitle());
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            } else {
                Toast.makeText(getContext(), "No email address provided by seller", Toast.LENGTH_SHORT).show();
            }
        });

        btnMarkSold.setOnClickListener(v -> {
            if (currentListing != null) {
                repository.markSold(currentListing.getId(), new RemoteListingRepository.ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Listing marked as SOLD!", Toast.LENGTH_SHORT).show();
                        currentListing.setStatus("SOLD");
                        btnMarkSold.setVisibility(View.GONE);
                        tvDetailConditionBadge.setText("SOLD");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadListingDetails() {
        if (listingId == null || listingId.isEmpty()) return;

        repository.fetchListingDetail(listingId, new RemoteListingRepository.ApiCallback<Listing>() {
            @Override
            public void onSuccess(Listing result) {
                if (!isAdded() || result == null) return;
                currentListing = result;
                populateData(result);
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error loading details: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateData(Listing item) {
        tvDetailTitle.setText(item.getTitle());
        tvDetailPrice.setText(item.getFormattedPrice());
        tvDetailCategory.setText(item.getCategoryDisplayName());
        tvDetailDescription.setText(item.getDescription());
        tvDetailConditionBadge.setText(item.getFormattedCondition().toUpperCase());
        tvMeetupLocation.setText(item.getMeetupArea());
        tvSellerName.setText(item.getSellerName());
        tvSellerAvatar.setText(item.getSellerInitials());
        tvSellerCampus.setText(item.getSellerCampus() + " · 4.8 ★");

        if (item.getCategorySummary() != null && !item.getCategorySummary().isEmpty()) {
            tvCategorySummary.setVisibility(View.VISIBLE);
            tvCategorySummary.setText(item.getCategorySummary());
        } else {
            tvCategorySummary.setVisibility(View.GONE);
        }

        // Category artwork & background styling
        String cat = item.getCategory().toUpperCase();
        if ("TEXTBOOKS".equals(cat)) {
            tvDetailArtworkPlaceholder.setText("📚");
            detailArtworkFrame.setBackgroundResource(R.drawable.bg_badge_mint);
        } else if ("ELECTRONICS".equals(cat)) {
            tvDetailArtworkPlaceholder.setText("🎧");
            detailArtworkFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
        } else if ("CLOTHING".equals(cat)) {
            tvDetailArtworkPlaceholder.setText("👕");
            detailArtworkFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
        } else if ("HOSTEL_ESSENTIALS".equals(cat)) {
            tvDetailArtworkPlaceholder.setText("🛏️");
            detailArtworkFrame.setBackgroundResource(R.drawable.bg_badge_mint);
        } else if ("SPORTS_FITNESS".equals(cat)) {
            tvDetailArtworkPlaceholder.setText("⚽");
            detailArtworkFrame.setBackgroundResource(R.drawable.bg_badge_lime);
        } else {
            tvDetailArtworkPlaceholder.setText("📦");
            detailArtworkFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
        }

        // Savings / Offer badge
        if (item.isNegotiable()) {
            tvDetailSavingsBadge.setVisibility(View.VISIBLE);
            tvDetailSavingsBadge.setText("OFFER OK");
        } else {
            tvDetailSavingsBadge.setVisibility(View.GONE);
        }

        // Direct contact buttons visibility
        boolean hasPhone = !item.getSellerPhone().isEmpty();
        boolean hasEmail = !item.getSellerEmail().isEmpty();
        btnCallSeller.setVisibility(hasPhone ? View.VISIBLE : View.GONE);
        btnEmailSeller.setVisibility(hasEmail ? View.VISIBLE : View.GONE);
        layoutDirectContact.setVisibility(hasPhone || hasEmail ? View.VISIBLE : View.GONE);

        // Mark Sold button (if owner or demo user and not sold yet)
        if (item.isOwner() && !item.isSold()) {
            btnMarkSold.setVisibility(View.VISIBLE);
        } else {
            btnMarkSold.setVisibility(View.GONE);
        }

        // Save status
        updateSaveIcon(repository.isListingSaved(item.getId()));
    }

    private void updateSaveIcon(boolean isSaved) {
        if (isSaved) {
            btnDetailSave.setImageResource(R.drawable.ic_heart_filled);
        } else {
            btnDetailSave.setImageResource(R.drawable.ic_nav_saved);
        }
    }
}
