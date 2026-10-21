package com.nightmarket.app.api;

import com.nightmarket.app.model.ConversationResponse;
import com.nightmarket.app.model.CreateListingRequest;
import com.nightmarket.app.model.Listing;
import com.nightmarket.app.model.MessageResponse;
import com.nightmarket.app.model.SendMessageRequest;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

/**
 * Retrofit interface for NightMarket backend REST endpoints.
 */
public interface ApiService {

    @GET("api/listings/feed")
    Call<List<Listing>> getFeed();

    @GET("api/listings/search")
    Call<List<Listing>> searchListings(@Query("q") String query);

    @GET("api/listings/mine")
    Call<List<Listing>> getMyListings();

    @GET("api/listings/{id}")
    Call<Listing> getListingDetail(@Path("id") String id);

    @POST("api/listings/add")
    Call<Listing> createListing(@Body CreateListingRequest request);

    @POST("api/listings/{id}/mark-sold")
    Call<Void> markSold(@Path("id") String id);

    @GET("api/health")
    Call<Map<String, Object>> checkHealth();

    // Image Upload Endpoint
    @Multipart
    @POST("api/images/upload")
    Call<Map<String, String>> uploadImage(@Part MultipartBody.Part file);

    // Conversation and Messaging Endpoints
    @POST("api/conversations/start")
    Call<ConversationResponse> startConversation(
            @Query("listingId") String listingId,
            @Query("buyerId") String buyerId,
            @Query("buyerName") String buyerName
    );

    @GET("api/conversations/listing/{listingId}")
    Call<ConversationResponse> getConversationForListing(
            @Path("listingId") String listingId,
            @Query("buyerId") String buyerId,
            @Query("buyerName") String buyerName
    );

    @GET("api/conversations/{conversationId}/messages")
    Call<List<MessageResponse>> getMessages(
            @Path("conversationId") String conversationId,
            @Query("currentUserId") String currentUserId
    );

    @POST("api/conversations/{conversationId}/messages")
    Call<MessageResponse> sendMessage(
            @Path("conversationId") String conversationId,
            @Query("senderId") String senderId,
            @Query("senderName") String senderName,
            @Body SendMessageRequest request
    );
}
