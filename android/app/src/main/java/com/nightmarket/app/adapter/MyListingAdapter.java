package com.nightmarket.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nightmarket.app.R;
import com.nightmarket.app.model.Listing;

import java.util.ArrayList;
import java.util.List;

public class MyListingAdapter extends RecyclerView.Adapter<MyListingAdapter.ViewHolder> {

    public interface OnMyListingActionListener {
        void onListingClick(Listing listing);
        void onMarkSoldClick(Listing listing);
    }

    private final Context context;
    private final List<Listing> listings = new ArrayList<>();
    private final OnMyListingActionListener actionListener;

    public MyListingAdapter(Context context, OnMyListingActionListener actionListener) {
        this.context = context;
        this.actionListener = actionListener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listings.get(position));
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout myArtworkFrame;
        private final TextView tvMyArtworkPlaceholder;
        private final TextView tvMyTitle;
        private final TextView tvMyPrice;
        private final TextView tvMyStatusBadge;
        private final TextView tvMyStats;
        private final ImageView btnMyMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myArtworkFrame = itemView.findViewById(R.id.myArtworkFrame);
            tvMyArtworkPlaceholder = itemView.findViewById(R.id.tvMyArtworkPlaceholder);
            tvMyTitle = itemView.findViewById(R.id.tvMyTitle);
            tvMyPrice = itemView.findViewById(R.id.tvMyPrice);
            tvMyStatusBadge = itemView.findViewById(R.id.tvMyStatusBadge);
            tvMyStats = itemView.findViewById(R.id.tvMyStats);
            btnMyMenu = itemView.findViewById(R.id.btnMyMenu);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onListingClick(listings.get(pos));
                }
            });
        }

        public void bind(Listing item) {
            tvMyTitle.setText(item.getTitle());
            tvMyPrice.setText(item.getFormattedPrice());

            String viewsStr = item.getViewCount() + (item.getViewCount() == 1 ? " view" : " views");
            String savesStr = item.getFavoriteCount() + (item.getFavoriteCount() == 1 ? " save" : " saves");
            tvMyStats.setText(viewsStr + " · " + savesStr);

            boolean isSold = item.isSold();
            if (isSold) {
                tvMyStatusBadge.setText("SOLD");
                tvMyStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.nightmarket_sold_badge));
                tvMyStatusBadge.setBackgroundResource(R.drawable.bg_rounded_card);
            } else {
                tvMyStatusBadge.setText("ACTIVE");
                tvMyStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.nightmarket_active_badge));
                tvMyStatusBadge.setBackgroundResource(R.drawable.bg_badge_mint);
            }

            // Category artwork
            String cat = item.getCategory().toUpperCase();
            if ("TEXTBOOKS".equals(cat)) {
                tvMyArtworkPlaceholder.setText("📚");
                myArtworkFrame.setBackgroundResource(R.drawable.bg_badge_mint);
            } else if ("ELECTRONICS".equals(cat)) {
                tvMyArtworkPlaceholder.setText("🎧");
                myArtworkFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
            } else if ("CLOTHING".equals(cat)) {
                tvMyArtworkPlaceholder.setText("👕");
                myArtworkFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
            } else if ("HOSTEL_ESSENTIALS".equals(cat)) {
                tvMyArtworkPlaceholder.setText("🛏️");
                myArtworkFrame.setBackgroundResource(R.drawable.bg_badge_mint);
            } else {
                tvMyArtworkPlaceholder.setText("📦");
                myArtworkFrame.setBackgroundResource(R.drawable.bg_badge_lime);
            }

            btnMyMenu.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, btnMyMenu);
                if (!isSold) {
                    popup.getMenu().add(0, 1, 0, "Mark as Sold");
                }
                popup.getMenu().add(0, 2, 1, "View Details");
                popup.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == 1) {
                        if (actionListener != null) actionListener.onMarkSoldClick(item);
                        return true;
                    } else if (menuItem.getItemId() == 2) {
                        if (actionListener != null) actionListener.onListingClick(item);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
}
