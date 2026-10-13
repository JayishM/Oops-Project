package com.nightmarket.controller;

import com.nightmarket.dto.request.CreateListingRequest;
import com.nightmarket.dto.response.ListingDetailResponse;
import com.nightmarket.dto.response.ListingSummaryResponse;
import com.nightmarket.service.SimpleListingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing the NightMarket listing endpoints.
 *
 * Endpoints:
 *   POST   /api/listings/add           – Add a new listing
 *   GET    /api/listings/feed          – Browse all active listings
 *   GET    /api/listings/search?q=...  – Search listings by keyword
 *   GET    /api/listings/mine          – My listings (demo-user)
 *   GET    /api/listings/{id}          – Full listing detail
 *   POST   /api/listings/{id}/mark-sold – Mark a listing as sold
 */
@RestController
@RequestMapping("/api/listings")
@CrossOrigin(origins = "*")
public class ListingController {

    private final SimpleListingService listingService;

    public ListingController(SimpleListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping("/add")
    public ResponseEntity<ListingDetailResponse> addListing(
            @Valid @RequestBody CreateListingRequest request) {
        return ResponseEntity.ok(listingService.createListing(request));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<ListingSummaryResponse>> getFeed() {
        return ResponseEntity.ok(listingService.getFeed());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ListingSummaryResponse>> search(
            @RequestParam(defaultValue = "") String q) {
        return ResponseEntity.ok(listingService.search(q));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ListingSummaryResponse>> mine() {
        return ResponseEntity.ok(listingService.getMyListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingDetailResponse> getDetail(@PathVariable String id) {
        return ResponseEntity.ok(listingService.getListingDetail(id));
    }

    @PostMapping("/{id}/mark-sold")
    public ResponseEntity<Void> markSold(@PathVariable String id) {
        listingService.markAsSold(id);
        return ResponseEntity.ok().build();
    }
}
