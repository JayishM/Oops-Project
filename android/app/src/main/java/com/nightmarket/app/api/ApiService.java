package com.nightmarket.app.api;

import com.nightmarket.app.model.CreateListingRequest;
import com.nightmarket.app.model.Listing;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
}
