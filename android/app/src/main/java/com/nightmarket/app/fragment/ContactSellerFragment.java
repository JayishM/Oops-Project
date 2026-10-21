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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.nightmarket.app.MainActivity;
import com.nightmarket.app.R;
import com.nightmarket.app.model.ConversationResponse;
import com.nightmarket.app.model.Listing;
import com.nightmarket.app.model.MessageResponse;
import com.nightmarket.app.repository.RemoteListingRepository;
import com.nightmarket.app.util.ImageLoader;

import java.util.List;

/**
 * Functional chat interface for NightMarket.
 * Allows prospective student buyers to communicate directly with student sellers.
 * Persists conversations and messages in MongoDB Atlas through Spring Boot backend.
 */
public class ContactSellerFragment extends Fragment {

    private static final String ARG_LISTING = "arg_listing";

    private ImageView btnChatBack;
    private ImageView btnChatRefresh;
    private TextView tvChatHeaderTitle;
    private FrameLayout chatProductThumbFrame;
    private TextView tvChatProductPlaceholder;
    private ImageView ivChatProductImage;
    private TextView tvChatProductTitle;
    private TextView tvChatProductPrice;
    private TextView btnViewListing;

    private ProgressBar progressBarChat;
    private LinearLayout layoutChatError;
    private TextView tvChatErrorMessage;
    private Button btnChatRetry;

    private LinearLayout layoutSellerInitialContainer;
    private TextView tvChatSellerAvatar;
    private TextView tvSellerInitialMessage;
    private LinearLayout layoutChatEmptyState;
    private TextView tvChatEmptyTitle;
    private TextView tvChatEmptySubtitle;
    private LinearLayout layoutDynamicMessages;
    private NestedScrollView chatScrollView;

    private Button btnChatCall;
    private Button btnChatSMS;
    private Button btnChatEmail;

    private TextView chipReplyAvailable;
    private TextView chipReplyWhere;
    private TextView chipReplyPrice;
    private EditText etChatMessage;
    private ImageView btnSendMessage;

    private RemoteListingRepository repository;
    private Listing listing;
    private String conversationId;
    private boolean isSending = false;

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

        repository = new RemoteListingRepository(requireContext());
        if (getArguments() != null) {
            listing = (Listing) getArguments().getSerializable(ARG_LISTING);
        }

        bindViews(view);
        populateListingHeader();
        setupListeners();
        loadConversation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setBottomNavVisibility(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setBottomNavVisibility(true);
        }
    }

    private void bindViews(View view) {
        btnChatBack = view.findViewById(R.id.btnChatBack);
        btnChatRefresh = view.findViewById(R.id.btnChatRefresh);
        tvChatHeaderTitle = view.findViewById(R.id.tvChatHeaderTitle);
        chatProductThumbFrame = view.findViewById(R.id.chatProductThumbFrame);
        tvChatProductPlaceholder = view.findViewById(R.id.tvChatProductPlaceholder);
        ivChatProductImage = view.findViewById(R.id.ivChatProductImage);
        tvChatProductTitle = view.findViewById(R.id.tvChatProductTitle);
        tvChatProductPrice = view.findViewById(R.id.tvChatProductPrice);
        btnViewListing = view.findViewById(R.id.btnViewListing);

        progressBarChat = view.findViewById(R.id.progressBarChat);
        layoutChatError = view.findViewById(R.id.layoutChatError);
        tvChatErrorMessage = view.findViewById(R.id.tvChatErrorMessage);
        btnChatRetry = view.findViewById(R.id.btnChatRetry);

        layoutSellerInitialContainer = view.findViewById(R.id.layoutSellerInitialContainer);
        tvChatSellerAvatar = view.findViewById(R.id.tvChatSellerAvatar);
        tvSellerInitialMessage = view.findViewById(R.id.tvSellerInitialMessage);
        layoutChatEmptyState = view.findViewById(R.id.layoutChatEmptyState);
        tvChatEmptyTitle = view.findViewById(R.id.tvChatEmptyTitle);
        tvChatEmptySubtitle = view.findViewById(R.id.tvChatEmptySubtitle);
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

    private void populateListingHeader() {
        if (listing == null) {
            tvChatHeaderTitle.setText("Chat");
            tvChatProductTitle.setText("Item not found");
            return;
        }

        String sellerName = (listing.getSellerName() != null && !listing.getSellerName().trim().isEmpty())
                ? listing.getSellerName() : "Seller";
        tvChatHeaderTitle.setText("Chat with " + sellerName);
        tvChatProductTitle.setText(listing.getTitle() != null ? listing.getTitle() : "Listing");
        tvChatProductPrice.setText(listing.getFormattedPrice() + " · " + listing.getFormattedCondition());
        tvChatSellerAvatar.setText(listing.getSellerInitials());

        String meetup = (listing.getMeetupArea() != null && !listing.getMeetupArea().trim().isEmpty())
                ? listing.getMeetupArea() : "Campus";
        tvSellerInitialMessage.setText("Hey! Thanks for your interest in " + (listing.getTitle() != null ? listing.getTitle() : "this item") + ". It is available for campus pickup at " + meetup + ".");

        // Category artwork or real image
        String cat = (listing.getCategory() != null) ? listing.getCategory().toUpperCase() : "";
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
        } else if ("SPORTS_FITNESS".equals(cat)) {
            tvChatProductPlaceholder.setText("⚽");
            chatProductThumbFrame.setBackgroundResource(R.drawable.bg_badge_lime);
        } else {
            tvChatProductPlaceholder.setText("📦");
            chatProductThumbFrame.setBackgroundResource(R.drawable.bg_badge_lime);
        }

        // Real image thumbnail in chat header
        String primaryImg = listing.getPrimaryImageUrl();
        if (primaryImg != null && !primaryImg.trim().isEmpty()) {
            ImageLoader.getInstance().loadImage(ivChatProductImage, primaryImg, tvChatProductPlaceholder);
        }

        // Direct contact buttons
        boolean hasPhone = listing.getSellerPhone() != null && !listing.getSellerPhone().trim().isEmpty();
        boolean hasEmail = listing.getSellerEmail() != null && !listing.getSellerEmail().trim().isEmpty();
        btnChatCall.setVisibility(hasPhone ? View.VISIBLE : View.GONE);
        btnChatSMS.setVisibility(hasPhone ? View.VISIBLE : View.GONE);
        btnChatEmail.setVisibility(hasEmail ? View.VISIBLE : View.GONE);
    }

    private void setupListeners() {
        btnChatBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        btnChatRefresh.setOnClickListener(v -> {
            loadConversation();
            Toast.makeText(getContext(), "Refreshed chat", Toast.LENGTH_SHORT).show();
        });

        if (btnChatRetry != null) {
            btnChatRetry.setOnClickListener(v -> loadConversation());
        }

        btnViewListing.setOnClickListener(v -> {
            if (listing != null && !listing.getId().isEmpty() && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToProductDetail(listing.getId());
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Quick replies
        chipReplyAvailable.setOnClickListener(v -> handleSendMessage("Hi! Is this still available?"));
        chipReplyWhere.setOnClickListener(v -> handleSendMessage("Where and when can we meet on campus?"));
        chipReplyPrice.setOnClickListener(v -> {
            String p = (listing != null && listing.getPrice() != null) ? "₹" + listing.getPrice() : "your price";
            handleSendMessage("Is the price negotiable? Can you do " + p + "?");
        });

        // Send button
        btnSendMessage.setOnClickListener(v -> {
            String msg = etChatMessage.getText().toString().trim();
            if (msg.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }
            handleSendMessage(msg);
        });

        // Auto-scroll when user focuses input
        etChatMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && chatScrollView != null) {
                chatScrollView.postDelayed(() -> chatScrollView.fullScroll(View.FOCUS_DOWN), 200);
            }
        });

        // Intent actions
        btnChatCall.setOnClickListener(v -> {
            if (listing != null && listing.getSellerPhone() != null && !listing.getSellerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + listing.getSellerPhone()));
                startActivity(intent);
            }
        });

        btnChatSMS.setOnClickListener(v -> {
            if (listing != null && listing.getSellerPhone() != null && !listing.getSellerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + listing.getSellerPhone()));
                intent.putExtra("sms_body", "Hi " + listing.getSellerName() + ", I saw your listing for " + listing.getTitle() + " on NightMarket.");
                startActivity(intent);
            }
        });

        btnChatEmail.setOnClickListener(v -> {
            if (listing != null && listing.getSellerEmail() != null && !listing.getSellerEmail().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + listing.getSellerEmail()));
                intent.putExtra(Intent.EXTRA_SUBJECT, "NightMarket Listing: " + listing.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, "Hi " + listing.getSellerName() + ",\nI'm reaching out about your NightMarket item: " + listing.getTitle());
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }

    private void loadConversation() {
        if (listing == null || listing.getId() == null || listing.getId().trim().isEmpty()) {
            if (progressBarChat != null) progressBarChat.setVisibility(View.GONE);
            showError("Cannot start chat: Missing listing information");
            return;
        }

        if (progressBarChat != null) progressBarChat.setVisibility(View.VISIBLE);
        if (layoutChatError != null) layoutChatError.setVisibility(View.GONE);

        repository.getOrCreateConversation(listing.getId(), "Student Buyer", new RemoteListingRepository.ApiCallback<ConversationResponse>() {
            @Override
            public void onSuccess(ConversationResponse result) {
                if (!isAdded()) return;
                conversationId = result.getId();
                fetchMessages();
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                if (progressBarChat != null) progressBarChat.setVisibility(View.GONE);
                showError("Unable to connect to chat: " + errorMessage);
            }
        });
    }

    private void fetchMessages() {
        if (conversationId == null) {
            if (progressBarChat != null) progressBarChat.setVisibility(View.GONE);
            return;
        }

        repository.fetchMessages(conversationId, new RemoteListingRepository.ApiCallback<List<MessageResponse>>() {
            @Override
            public void onSuccess(List<MessageResponse> result) {
                if (!isAdded()) return;
                if (progressBarChat != null) progressBarChat.setVisibility(View.GONE);
                if (layoutChatError != null) layoutChatError.setVisibility(View.GONE);

                layoutDynamicMessages.removeAllViews();

                if (result != null && !result.isEmpty()) {
                    if (layoutSellerInitialContainer != null) layoutSellerInitialContainer.setVisibility(View.GONE);
                    if (layoutChatEmptyState != null) layoutChatEmptyState.setVisibility(View.GONE);

                    for (MessageResponse message : result) {
                        appendMessageBubble(message.getContent(), message.getFormattedTime(), message.isSentByCurrentUser());
                    }
                } else {
                    if (layoutSellerInitialContainer != null) layoutSellerInitialContainer.setVisibility(View.VISIBLE);
                    if (layoutChatEmptyState != null) layoutChatEmptyState.setVisibility(View.VISIBLE);
                }

                if (chatScrollView != null) {
                    chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                if (progressBarChat != null) progressBarChat.setVisibility(View.GONE);
                showError("Failed to load messages: " + errorMessage);
            }
        });
    }

    private void handleSendMessage(String text) {
        if (text == null || text.trim().isEmpty() || isSending) return;

        if (conversationId == null) {
            Toast.makeText(getContext(), "Connecting to chat, please wait...", Toast.LENGTH_SHORT).show();
            loadConversation();
            return;
        }

        isSending = true;
        btnSendMessage.setEnabled(false);
        btnSendMessage.setAlpha(0.5f);

        repository.sendMessage(conversationId, text.trim(), new RemoteListingRepository.ApiCallback<MessageResponse>() {
            @Override
            public void onSuccess(MessageResponse result) {
                if (!isAdded()) return;
                isSending = false;
                btnSendMessage.setEnabled(true);
                btnSendMessage.setAlpha(1.0f);
                etChatMessage.setText("");

                if (layoutChatEmptyState != null) layoutChatEmptyState.setVisibility(View.GONE);
                if (layoutSellerInitialContainer != null) layoutSellerInitialContainer.setVisibility(View.GONE);

                appendMessageBubble(result.getContent(), result.getFormattedTime(), true);
                if (chatScrollView != null) {
                    chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                isSending = false;
                btnSendMessage.setEnabled(true);
                btnSendMessage.setAlpha(1.0f);
                Toast.makeText(getContext(), "Failed to send: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showError(String message) {
        if (layoutChatError != null && tvChatErrorMessage != null) {
            tvChatErrorMessage.setText(message);
            layoutChatError.setVisibility(View.VISIBLE);
        } else if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void appendMessageBubble(String text, String timeText, boolean isOutgoing) {
        if (!isAdded() || layoutDynamicMessages == null) return;

        LinearLayout bubbleContainer = new LinearLayout(requireContext());
        bubbleContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bubbleContainer.setOrientation(LinearLayout.HORIZONTAL);
        bubbleContainer.setGravity(isOutgoing ? android.view.Gravity.END : android.view.Gravity.START);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bubbleContainer.getLayoutParams();
        params.topMargin = (int) (8 * getResources().getDisplayMetrics().density);
        bubbleContainer.setLayoutParams(params);

        LinearLayout bubble = new LinearLayout(requireContext());
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setBackgroundResource(isOutgoing ? R.drawable.bg_chat_outgoing : R.drawable.bg_chat_incoming);
        int pad = (int) (12 * getResources().getDisplayMetrics().density);
        bubble.setPadding(pad, pad, pad, pad);

        // Max width so bubbles don't span the entire screen
        int marginOpposite = (int) (48 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (isOutgoing) {
            bubbleParams.leftMargin = marginOpposite;
        } else {
            bubbleParams.rightMargin = marginOpposite;
        }
        bubble.setLayoutParams(bubbleParams);

        TextView tvMsg = new TextView(requireContext());
        tvMsg.setText(text);
        tvMsg.setTextSize(13);
        tvMsg.setTextColor(ContextCompat.getColor(requireContext(), R.color.nightmarket_text_primary));
        bubble.addView(tvMsg);

        if (timeText != null && !timeText.isEmpty()) {
            TextView tvTime = new TextView(requireContext());
            tvTime.setText(timeText + (isOutgoing ? " ✓✓" : ""));
            tvTime.setTextSize(9);
            tvTime.setTextColor(ContextCompat.getColor(requireContext(),
                    isOutgoing ? R.color.nightmarket_active_badge : R.color.nightmarket_text_muted));
            LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            timeParams.gravity = android.view.Gravity.END;
            timeParams.topMargin = (int) (4 * getResources().getDisplayMetrics().density);
            tvTime.setLayoutParams(timeParams);
            bubble.addView(tvTime);
        }

        bubbleContainer.addView(bubble);
        layoutDynamicMessages.addView(bubbleContainer);
    }
}
