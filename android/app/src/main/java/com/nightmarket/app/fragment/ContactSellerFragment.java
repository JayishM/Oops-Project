package com.nightmarket.app.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.nightmarket.app.MainActivity;
import com.nightmarket.app.R;
import com.nightmarket.app.model.Listing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ContactSellerFragment extends Fragment {

    private static final String ARG_LISTING = "arg_listing";

    private ImageView btnChatBack;
    private TextView tvChatHeaderTitle;
    private FrameLayout chatProductThumbFrame;
    private TextView tvChatProductPlaceholder;
    private TextView tvChatProductTitle;
    private TextView tvChatProductPrice;
    private TextView btnViewListing;
    private TextView tvChatSellerAvatar;
    private TextView tvSellerInitialMessage;
    private LinearLayout layoutDynamicMessages;
    private ScrollView chatScrollView;
    private Button btnChatCall;
    private Button btnChatSMS;
    private Button btnChatEmail;
    private TextView chipReplyAvailable;
    private TextView chipReplyWhere;
    private TextView chipReplyPrice;
    private EditText etChatMessage;
    private ImageView btnSendMessage;

    private Listing listing;

    public static ContactSellerFragment newInstance(Listing listing) {
        ContactSellerFragment fragment = new ContactSellerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LISTING, listing);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_seller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            listing = (Listing) getArguments().getSerializable(ARG_LISTING);
        }

        bindViews(view);
        populateData();
        setupListeners();
    }

    private void bindViews(View view) {
        btnChatBack = view.findViewById(R.id.btnChatBack);
        tvChatHeaderTitle = view.findViewById(R.id.tvChatHeaderTitle);
        chatProductThumbFrame = view.findViewById(R.id.chatProductThumbFrame);
        tvChatProductPlaceholder = view.findViewById(R.id.tvChatProductPlaceholder);
        tvChatProductTitle = view.findViewById(R.id.tvChatProductTitle);
        tvChatProductPrice = view.findViewById(R.id.tvChatProductPrice);
        btnViewListing = view.findViewById(R.id.btnViewListing);
        tvChatSellerAvatar = view.findViewById(R.id.tvChatSellerAvatar);
        tvSellerInitialMessage = view.findViewById(R.id.tvSellerInitialMessage);
        layoutDynamicMessages = view.findViewById(R.id.layoutDynamicMessages);
        chatScrollView = view.findViewById(R.id.chatScrollView);
        btnChatCall = view.findViewById(R.id.btnChatCall);
        btnChatSMS = view.findViewById(R.id.btnChatSMS);
        btnChatEmail = view.findViewById(R.id.btnChatEmail);
        chipReplyAvailable = view.findViewById(R.id.chipReplyAvailable);
        chipReplyWhere = view.findViewById(R.id.chipReplyWhere);
        chipReplyPrice = view.findViewById(R.id.chipReplyPrice);
        etChatMessage = view.findViewById(R.id.etChatMessage);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);
    }

    private void populateData() {
        if (listing == null) return;

        tvChatHeaderTitle.setText("Chat with " + listing.getSellerName());
        tvChatProductTitle.setText(listing.getTitle());
        tvChatProductPrice.setText(listing.getFormattedPrice() + " · " + listing.getFormattedCondition());
        tvChatSellerAvatar.setText(listing.getSellerInitials());

        tvSellerInitialMessage.setText("Hey! Thanks for your interest in " + listing.getTitle() + ". It is available for campus pickup at " + listing.getMeetupArea() + ".");

        // Category artwork
        String cat = listing.getCategory().toUpperCase();
        if ("TEXTBOOKS".equals(cat)) {
            tvChatProductPlaceholder.setText("📚");
            chatProductThumbFrame.setBackgroundResource(R.drawable.bg_badge_mint);
        } else if ("ELECTRONICS".equals(cat)) {
            tvChatProductPlaceholder.setText("🎧");
            chatProductThumbFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
        } else if ("CLOTHING".equals(cat)) {
            tvChatProductPlaceholder.setText("👕");
            chatProductThumbFrame.setBackgroundResource(R.drawable.bg_badge_lavender);
        } else if ("HOSTEL_ESSENTIALS".equals(cat)) {
            tvChatProductPlaceholder.setText("🛏️");
            chatProductThumbFrame.setBackgroundResource(R.drawable.bg_badge_mint);
        } else {
            tvChatProductPlaceholder.setText("📦");
            chatProductThumbFrame.setBackgroundResource(R.drawable.bg_badge_lime);
        }

        // Direct contact buttons
        boolean hasPhone = listing.getSellerPhone() != null && !listing.getSellerPhone().isEmpty();
        boolean hasEmail = listing.getSellerEmail() != null && !listing.getSellerEmail().isEmpty();
        btnChatCall.setVisibility(hasPhone ? View.VISIBLE : View.GONE);
        btnChatSMS.setVisibility(hasPhone ? View.VISIBLE : View.GONE);
        btnChatEmail.setVisibility(hasEmail ? View.VISIBLE : View.GONE);
    }

    private void setupListeners() {
        btnChatBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        btnViewListing.setOnClickListener(v -> {
            if (listing != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToProductDetail(listing.getId());
            }
        });

        // Quick replies
        chipReplyAvailable.setOnClickListener(v -> sendMessage("Hi! Is this still available?"));
        chipReplyWhere.setOnClickListener(v -> sendMessage("Where and when can we meet on campus?"));
        chipReplyPrice.setOnClickListener(v -> sendMessage("Is the price negotiable? Can you do ₹" + (listing != null ? listing.getPrice() : "…") + "?"));

        // Send button
        btnSendMessage.setOnClickListener(v -> {
            String msg = etChatMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
                etChatMessage.setText("");
            }
        });

        // Intent actions
        btnChatCall.setOnClickListener(v -> {
            if (listing != null && !listing.getSellerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + listing.getSellerPhone()));
                startActivity(intent);
            }
        });

        btnChatSMS.setOnClickListener(v -> {
            if (listing != null && !listing.getSellerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + listing.getSellerPhone()));
                intent.putExtra("sms_body", "Hi " + listing.getSellerName() + ", I saw your listing for " + listing.getTitle() + " on NightMarket.");
                startActivity(intent);
            }
        });

        btnChatEmail.setOnClickListener(v -> {
            if (listing != null && !listing.getSellerEmail().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + listing.getSellerEmail()));
                intent.putExtra(Intent.EXTRA_SUBJECT, "NightMarket Listing: " + listing.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, "Hi " + listing.getSellerName() + ",\nI'm reaching out about your NightMarket item: " + listing.getTitle());
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }

    private void sendMessage(String text) {
        String timeNow = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        // Create Buyer outgoing bubble programmatically
        LinearLayout bubbleContainer = new LinearLayout(requireContext());
        bubbleContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bubbleContainer.setOrientation(LinearLayout.HORIZONTAL);
        bubbleContainer.setGravity(android.view.Gravity.END);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bubbleContainer.getLayoutParams();
        params.topMargin = (int) (10 * getResources().getDisplayMetrics().density);
        bubbleContainer.setLayoutParams(params);

        LinearLayout bubble = new LinearLayout(requireContext());
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setBackgroundResource(R.drawable.bg_chat_outgoing);
        int pad = (int) (12 * getResources().getDisplayMetrics().density);
        bubble.setPadding(pad, pad, pad, pad);

        TextView tvMsg = new TextView(requireContext());
        tvMsg.setText(text);
        tvMsg.setTextSize(13);
        tvMsg.setTextColor(ContextCompat.getColor(requireContext(), R.color.nightmarket_text_primary));
        bubble.addView(tvMsg);

        TextView tvTime = new TextView(requireContext());
        tvTime.setText(timeNow + " ✓✓");
        tvTime.setTextSize(9);
        tvTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.nightmarket_active_badge));
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.gravity = android.view.Gravity.END;
        timeParams.topMargin = (int) (4 * getResources().getDisplayMetrics().density);
        tvTime.setLayoutParams(timeParams);
        bubble.addView(tvTime);

        bubbleContainer.addView(bubble);
        layoutDynamicMessages.addView(bubbleContainer);

        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }
}
