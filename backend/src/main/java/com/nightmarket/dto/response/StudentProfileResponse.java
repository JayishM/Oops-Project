package com.nightmarket.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public class StudentProfileResponse {

    private String id;
    private String email;
    private String fullName;
    private String campus;
    private String phone;
    private String avatarUrl;
    private String bio;
    private String graduationYear;
    private String department;
    private boolean emailVerified;
    private int activeListingsCount;
    private int soldListingsCount;
    private double sellerRating;
    private int totalRatingsCount;
    private Set<String> favoriteListingIds;
    private LocalDateTime createdAt;

    public StudentProfileResponse() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public int getActiveListingsCount() {
        return activeListingsCount;
    }

    public void setActiveListingsCount(int activeListingsCount) {
        this.activeListingsCount = activeListingsCount;
    }

    public int getSoldListingsCount() {
        return soldListingsCount;
    }

    public void setSoldListingsCount(int soldListingsCount) {
        this.soldListingsCount = soldListingsCount;
    }

    public double getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(double sellerRating) {
        this.sellerRating = sellerRating;
    }

    public int getTotalRatingsCount() {
        return totalRatingsCount;
    }

    public void setTotalRatingsCount(int totalRatingsCount) {
        this.totalRatingsCount = totalRatingsCount;
    }

    public Set<String> getFavoriteListingIds() {
        return favoriteListingIds;
    }

    public void setFavoriteListingIds(Set<String> favoriteListingIds) {
        this.favoriteListingIds = favoriteListingIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
