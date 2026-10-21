package com.nightmarket.domain.product;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.exception.DomainValidationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Specialized product subclass for academic textbooks and notes.
 * Demonstrates Inheritance and Polymorphic method overrides.
 */
public class Textbook extends Product {

    private String isbn;
    private String courseCode;
    private String author;
    private String edition;
    private boolean hasMarkings;

    public Textbook() {
        super();
        setCategory(Category.TEXTBOOKS);
    }

    public Textbook(String title, String description, BigDecimal price, Condition condition,
                    String sellerId, String sellerName, String sellerCampus, String meetupArea,
                    boolean negotiable, String isbn, String courseCode, String author,
                    String edition, boolean hasMarkings) {
        super(title, description, price, Category.TEXTBOOKS, condition, sellerId, sellerName,
                sellerCampus, meetupArea, negotiable);
        this.isbn = isbn;
        this.courseCode = courseCode;
        this.author = author;
        this.edition = edition;
        this.hasMarkings = hasMarkings;
    }

    @Override
    public String getCategorySpecificSummary() {
        return String.format("Textbook [%s] by %s (Edition: %s, Markings: %s)",
                courseCode != null ? courseCode : "General",
                author != null ? author : "Unknown Author",
                edition != null ? edition : "Standard",
                hasMarkings ? "Annotated / Notes Included" : "Clean Pages");
    }

    @Override
    public List<String> getSafetyChecklist() {
        return Arrays.asList(
                "Verify syllabus match and edition requirement",
                "Inspect pages for water damage or missing sections",
                "Check for accompanying CD/access codes if relevant"
        );
    }

    @Override
    public void validateCategoryDetails() throws DomainValidationException {
        if (author != null && author.trim().length() > 100) {
            throw new DomainValidationException("Author name is too long.");
        }
    }

    // Getters and Setters
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

    public boolean isHasMarkings() {
        return hasMarkings;
    }

    public void setHasMarkings(boolean hasMarkings) {
        this.hasMarkings = hasMarkings;
    }
}
