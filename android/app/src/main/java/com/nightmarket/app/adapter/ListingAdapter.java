package com.nightmarket.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nightmarket.app.R;
import com.nightmarket.app.model.Listing;
import com.nightmarket.app.repository.RemoteListingRepository;

import java.util.ArrayList;
import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    public interface OnListingClickListener {
        void onListingClick(Listing listing);
    }

    private final Context context;
    private final List<Listing> listings = new ArrayList<>();
    private final RemoteListingRepository repository;
    private final OnListingClickListener clickListener;

    public ListingAdapter(Context context, RemoteListingRepository repository, OnListingClickListener clickListener) {
        this.context = context;
        this.repository = repository;
        this.clickListener = clickListener;
    }

    public void setListings(List<Listing> newList) {
        listings.clear();
        if (newList != null) {
            listings.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listing item = listings.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategory;
        private final TextView tvCondition;
        private final ImageView btnSave;
        private final FrameLayout artworkFrame;
        private final TextView tvArtworkPlaceholder;
        private final ImageView ivProductImage;
        private final TextView tvOfferBadge;
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvPrice;
        private final TextView tvMeetup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvCondition = itemView.findViewById(R.id.tvCondition);
            btnSave = itemView.findViewById(R.id.btnSave);
            artworkFrame = itemView.findViewById(R.id.artworkFrame);
            tvArtworkPlaceholder = itemView.findViewById(R.id.tvArtworkPlaceholder);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvOfferBadge = itemView.findViewById(R.id.tvOfferBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvMeetup = itemView.findViewById(R.id.tvMeetup);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onListingClick(listings.get(pos));
                }
            });
        }

        public void bind(Listing item) {
            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());
            tvPrice.setText(item.getFormattedPrice());
            tvCondition.setText(item.getFormattedCondition());
            tvCategory.setText(item.getCategoryDisplayName());
            tvMeetup.setText(item.getMeetupArea());

            // Offer badge
            tvOfferBadge.setVisibility(item.isNegotiable() ? View.VISIBLE : View.GONE);

            // Category styling and emoji
            String cat = item.getCategory().toUpperCase();
            switch (cat) {
                case "TEXTBOOKS":
                    tvArtworkPlaceholder.setText("📚");
                    artworkFrame.setBackgroundResource(R.drawable.bg_badge_mint);
                    tvCategory.setBackgroundResource(R.drawable.bg_badge_mint);
                    break;
                case "ELECTRONICS":
                    tvArtworkPlaceholder.setText("🎧");
                    artworkFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
                    tvCategory.setBackgroundResource(R.drawable.bg_badge_lavender);
                    break;
                case "CLOTHING":
                    tvArtworkPlaceholder.setText("👕");
                    artworkFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
                    tvCategory.setBackgroundResource(R.drawable.bg_badge_lavender);
                    break;
                case "HOSTEL_ESSENTIALS":
                    tvArtworkPlaceholder.setText("🛏️");
                    artworkFrame.setBackgroundResource(R.drawable.bg_badge_mint);
                    tvCategory.setBackgroundResource(R.drawable.bg_badge_mint);
                    break;
                case "SPORTS_FITNESS":
                    tvArtworkPlaceholder.setText("⚽");
                    artworkFrame.setBackgroundResource(R.drawable.bg_badge_lime);
                    tvCategory.setBackgroundResource(R.drawable.bg_badge_lime);
                    break;
                case "FREE_STUFF":
                    tvArtworkPlaceholder.setText("🎁");
                    artworkFrame.setBackgroundResource(R.drawable.bg_badge_lime);
                    tvCategory.setBackgroundResource(R.drawable.bg_badge_lime);
                    break;
                default:
                    tvArtworkPlaceholder.setText("📦");
                    artworkFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
                    tvCategory.setBackgroundResource(R.drawable.bg_badge_lavender);
                    break;
            }

            // Saved heart toggle
            boolean isSaved = repository.isListingSaved(item.getId());
            updateHeartIcon(isSaved);

            btnSave.setOnClickListener(v -> {
                boolean newSaved = repository.toggleSaved(item.getId());
                updateHeartIcon(newSaved);
            });
        }

        private void updateHeartIcon(boolean isSaved) {
            if (isSaved) {
                btnSave.setImageResource(R.drawable.ic_heart_filled);
            } else {
                btnSave.setImageResource(R.drawable.ic_nav_saved);
            }
        }
    }
}
