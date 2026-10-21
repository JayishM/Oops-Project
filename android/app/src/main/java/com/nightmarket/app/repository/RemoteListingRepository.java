package com.nightmarket.app.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.nightmarket.app.api.ApiClient;
import com.nightmarket.app.api.ApiService;
import com.nightmarket.app.model.CreateListingRequest;
import com.nightmarket.app.model.Listing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository coordinating remote API operations and local preferences (saved items).
 */
public class RemoteListingRepository {

    private static final String PREFS_NAME = "nightmarket_prefs";
    private static final String KEY_SAVED_IDS = "saved_listing_ids";

    private final ApiService apiService;
    private final SharedPreferences preferences;

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    public RemoteListingRepository(Context context) {
        this.apiService = ApiClient.getApiService();
        this.preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void fetchFeed(ApiCallback<List<Listing>> callback) {
        apiService.getFeed().enqueue(new Callback<List<Listing>>() {
            @Override
            public void onResponse(Call<List<Listing>> call, Response<List<Listing>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Server returned code " + response.code() + ": " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Listing>> call, Throwable t) {
                callback.onError("Network connection failure: " + t.getLocalizedMessage());
            }
        });
    }

    public void search(String query, ApiCallback<List<Listing>> callback) {
        apiService.searchListings(query).enqueue(new Callback<List<Listing>>() {
            @Override
            public void onResponse(Call<List<Listing>> call, Response<List<Listing>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Search failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Listing>> call, Throwable t) {
                callback.onError("Search connection error: " + t.getLocalizedMessage());
            }
        });
    }

    public void fetchMyListings(ApiCallback<List<Listing>> callback) {
        apiService.getMyListings().enqueue(new Callback<List<Listing>>() {
            @Override
            public void onResponse(Call<List<Listing>> call, Response<List<Listing>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load user listings: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Listing>> call, Throwable t) {
                callback.onError("Failed to connect: " + t.getLocalizedMessage());
            }
        });
    }

    public void fetchListingDetail(String id, ApiCallback<Listing> callback) {
        apiService.getListingDetail(id).enqueue(new Callback<Listing>() {
            @Override
            public void onResponse(Call<Listing> call, Response<Listing> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Listing not found or failed to load");
                }
            }

            @Override
            public void onFailure(Call<Listing> call, Throwable t) {
                callback.onError("Connection error: " + t.getLocalizedMessage());
            }
        });
    }

    public void createListing(CreateListingRequest request, ApiCallback<Listing> callback) {
        apiService.createListing(request).enqueue(new Callback<Listing>() {
            @Override
            public void onResponse(Call<Listing> call, Response<Listing> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = "Failed to create listing";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<Listing> call, Throwable t) {
                callback.onError("Failed to reach server: " + t.getLocalizedMessage());
            }
        });
    }

    public void markSold(String id, ApiCallback<Void> callback) {
        apiService.markSold(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Could not mark as sold: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getLocalizedMessage());
            }
        });
    }

    // Local saved items persistence
    public Set<String> getSavedListingIds() {
        return preferences.getStringSet(KEY_SAVED_IDS, new HashSet<>());
    }

    public boolean isListingSaved(String id) {
        return getSavedListingIds().contains(id);
    }

    public boolean toggleSaved(String id) {
        Set<String> current = new HashSet<>(getSavedListingIds());
        boolean isNowSaved;
        if (current.contains(id)) {
            current.remove(id);
            isNowSaved = false;
        } else {
            current.add(id);
            isNowSaved = true;
        }
        preferences.edit().putStringSet(KEY_SAVED_IDS, current).apply();
        return isNowSaved;
    }

    // ── Image Upload ──────────────────────────────────────────────────────────

    public void uploadImage(java.io.File file, ApiCallback<String> callback) {
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), file);
        okhttp3.MultipartBody.Part part = okhttp3.MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        apiService.uploadImage(part).enqueue(new Callback<java.util.Map<String, String>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, String>> call, Response<java.util.Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().containsKey("imageUrl")) {
                    callback.onSuccess(response.body().get("imageUrl"));
                } else {
                    String error = "Image upload failed";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                callback.onError("Network error uploading photo: " + t.getLocalizedMessage());
            }
        });
    }

    // ── Messaging & Conversations ─────────────────────────────────────────────

    public void getOrCreateConversation(String listingId, String buyerName, ApiCallback<com.nightmarket.app.model.ConversationResponse> callback) {
        apiService.startConversation(listingId, "demo-buyer", buyerName).enqueue(new Callback<com.nightmarket.app.model.ConversationResponse>() {
            @Override
            public void onResponse(Call<com.nightmarket.app.model.ConversationResponse> call, Response<com.nightmarket.app.model.ConversationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to start chat: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<com.nightmarket.app.model.ConversationResponse> call, Throwable t) {
                callback.onError("Connection error: " + t.getLocalizedMessage());
            }
        });
    }

    public void fetchMessages(String conversationId, ApiCallback<List<com.nightmarket.app.model.MessageResponse>> callback) {
        apiService.getMessages(conversationId, "demo-buyer").enqueue(new Callback<List<com.nightmarket.app.model.MessageResponse>>() {
            @Override
            public void onResponse(Call<List<com.nightmarket.app.model.MessageResponse>> call, Response<List<com.nightmarket.app.model.MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load messages");
                }
            }

            @Override
            public void onFailure(Call<List<com.nightmarket.app.model.MessageResponse>> call, Throwable t) {
                callback.onError("Error loading messages: " + t.getLocalizedMessage());
            }
        });
    }

    public void sendMessage(String conversationId, String content, ApiCallback<com.nightmarket.app.model.MessageResponse> callback) {
        com.nightmarket.app.model.SendMessageRequest req = new com.nightmarket.app.model.SendMessageRequest(content);
        apiService.sendMessage(conversationId, "demo-buyer", "Student Buyer", req).enqueue(new Callback<com.nightmarket.app.model.MessageResponse>() {
            @Override
            public void onResponse(Call<com.nightmarket.app.model.MessageResponse> call, Response<com.nightmarket.app.model.MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to send message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<com.nightmarket.app.model.MessageResponse> call, Throwable t) {
                callback.onError("Network error sending message: " + t.getLocalizedMessage());
            }
        });
    }
}
