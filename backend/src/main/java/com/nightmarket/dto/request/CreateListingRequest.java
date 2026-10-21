package com.nightmarket.dto.request;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CreateListingRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    private BigDecimal price;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Condition is required")
    private Condition condition;

    private List<String> imageUrls = new ArrayList<>();

<<<<<<< HEAD
    @Size(max = 80, message = "Seller name cannot exceed 80 characters")
    private String sellerName;

    @Size(max = 30, message = "Phone number cannot exceed 30 characters")
    private String sellerPhone;

    @Size(max = 120, message = "Email cannot exceed 120 characters")
    private String sellerEmail;

=======
>>>>>>> 39aa8c2ca1fa8976c6cac2d492cd608848470eec
    @NotBlank(message = "Campus meetup area is required")
    private String meetupArea;

    private boolean negotiable;

    // Optional Polymorphic Category Attributes
    // Textbook
    private String isbn;
    private String courseCode;
    private String author;
    private String edition;
    private Boolean hasMarkings;

    // Electronics
    private String brand;
    private String model;
    private Integer warrantyMonths;
    private Boolean includesAccessories;
    private Boolean workingBattery;

    // Hostel Essential
    private String itemSubtype;
    private Integer durationUsedMonths;
    private Boolean bulky;

    // Clothing
    private String size;
    private String genderFit;
    private String material;

    // Sports Fitness
    private String sportType;
    private Double equipmentWeightKg;

    public CreateListingRequest() {}

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

<<<<<<< HEAD
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }

    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }

=======
>>>>>>> 39aa8c2ca1fa8976c6cac2d492cd608848470eec
    public String getMeetupArea() {
        return meetupArea;
    }

    public void setMeetupArea(String meetupArea) {
        this.meetupArea = meetupArea;
    }

    public boolean isNegotiable() {
        return negotiable;
    }

    public void setNegotiable(boolean negotiable) {
        this.negotiable = negotiable;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public Boolean getHasMarkings() {
        return hasMarkings;
    }

    public void setHasMarkings(Boolean hasMarkings) {
        this.hasMarkings = hasMarkings;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public Boolean getIncludesAccessories() {
        return includesAccessories;
    }

    public void setIncludesAccessories(Boolean includesAccessories) {
        this.includesAccessories = includesAccessories;
    }

    public Boolean getWorkingBattery() {
        return workingBattery;
    }

    public void setWorkingBattery(Boolean workingBattery) {
        this.workingBattery = workingBattery;
    }

    public String getItemSubtype() {
        return itemSubtype;
    }

    public void setItemSubtype(String itemSubtype) {
        this.itemSubtype = itemSubtype;
    }

    public Integer getDurationUsedMonths() {
        return durationUsedMonths;
    }

    public void setDurationUsedMonths(Integer durationUsedMonths) {
        this.durationUsedMonths = durationUsedMonths;
    }

    public Boolean getBulky() {
        return bulky;
    }

    public void setBulky(Boolean bulky) {
        this.bulky = bulky;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getGenderFit() {
        return genderFit;
    }

    public void setGenderFit(String genderFit) {
        this.genderFit = genderFit;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public Double getEquipmentWeightKg() {
        return equipmentWeightKg;
    }

    public void setEquipmentWeightKg(Double equipmentWeightKg) {
        this.equipmentWeightKg = equipmentWeightKg;
    }
}
