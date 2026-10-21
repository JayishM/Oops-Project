package com.nightmarket.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ListingDetailResponse extends ListingSummaryResponse {

    private List<String> imageUrls = new ArrayList<>();
    private String categorySummary;
    private List<String> safetyChecklist = new ArrayList<>();
    private double sellerRating = 5.0;
    private int sellerSoldListingsCount;
    private boolean isOwner;
<<<<<<< HEAD
    private String sellerPhone;
    private String sellerEmail;
=======
>>>>>>> 39aa8c2ca1fa8976c6cac2d492cd608848470eec

    public ListingDetailResponse() {
        super();
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public String getCategorySummary() {
        return categorySummary;
    }

    public void setCategorySummary(String categorySummary) {
        this.categorySummary = categorySummary;
    }

    public List<String> getSafetyChecklist() {
        return safetyChecklist;
    }

    public void setSafetyChecklist(List<String> safetyChecklist) {
        this.safetyChecklist = safetyChecklist != null ? safetyChecklist : new ArrayList<>();
    }

    public double getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(double sellerRating) {
        this.sellerRating = sellerRating;
    }

    public int getSellerSoldListingsCount() {
        return sellerSoldListingsCount;
    }

    public void setSellerSoldListingsCount(int sellerSoldListingsCount) {
        this.sellerSoldListingsCount = sellerSoldListingsCount;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
<<<<<<< HEAD

    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }

    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }
=======
>>>>>>> 39aa8c2ca1fa8976c6cac2d492cd608848470eec
}
