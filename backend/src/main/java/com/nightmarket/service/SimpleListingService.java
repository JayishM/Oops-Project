package com.nightmarket.service;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.domain.marketplace.ListingStatus;
import com.nightmarket.domain.product.*;
import com.nightmarket.dto.request.CreateListingRequest;
import com.nightmarket.dto.response.ListingDetailResponse;
import com.nightmarket.dto.response.ListingSummaryResponse;
import com.nightmarket.exception.DomainValidationException;
import com.nightmarket.exception.ListingNotFoundException;
import com.nightmarket.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MongoDB-backed listing service.
 *
 * OOP Concepts:
 *   - Factory Method Pattern  : createProduct() instantiates category-specific subclasses.
 *   - Polymorphism            : getCategorySpecificSummary() and getSafetyChecklist() differ per subclass.
 *   - Encapsulation           : domain mutations flow through Product methods.
 *   - Persistence             : ProductRepository stores each concrete product in MongoDB.
 *   - Exception Handling      : missing listings use ListingNotFoundException.
 */
@Service
public class SimpleListingService {

    private static final Logger log = LoggerFactory.getLogger(SimpleListingService.class);
    private final ProductRepository productRepository;
    private final boolean seedDataEnabled;

    public SimpleListingService(ProductRepository productRepository,
                                @Value("${nightmarket.seed-data-enabled:true}") boolean seedDataEnabled) {
        this.productRepository = productRepository;
        this.seedDataEnabled = seedDataEnabled;
    }

    /**
     * Seed demo listings once, after the application has started. If MongoDB is
     * not reachable, keep the API process alive so /api/health can explain the
     * configuration issue; listing API calls will report the database failure.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        if (!seedDataEnabled) return;
        try {
            if (productRepository.count() == 0) {
                seedDemoData();
                log.info("Seeded demo NightMarket listings into MongoDB.");
            }
        } catch (Exception ex) {
            log.warn("MongoDB is not reachable; configure MONGODB_URI before using listing endpoints. {}",
                    ex.getClass().getSimpleName());
        }
    }

    private void seedDemoData() {
        Textbook tb = new Textbook();
        tb.setId(UUID.randomUUID().toString());
        tb.setTitle("Data Structures & Algorithms – 4th Ed.");
        tb.setDescription("Lightly annotated. Key chapters highlighted. Perfect for CS3 students.");
        tb.setPrice(new java.math.BigDecimal("350"));
        tb.setCondition(Condition.GOOD);
        tb.setSellerId("demo-user");
        tb.setSellerName("Priya Sharma");
        tb.setSellerCampus("Tech Campus Block C");
        tb.setMeetupArea("Library Ground Floor");
        tb.setNegotiable(true);
        tb.setCourseCode("CS301");
        tb.setAuthor("Cormen et al.");
        tb.setEdition("4th");
        productRepository.save(tb);

        Electronics calc = new Electronics();
        calc.setId(UUID.randomUUID().toString());
        calc.setTitle("Casio FX-991EX Scientific Calculator");
        calc.setDescription("Works perfectly, all buttons responsive, box included.");
        calc.setPrice(new java.math.BigDecimal("750"));
        calc.setCondition(Condition.LIKE_NEW);
        calc.setSellerId("demo-user");
        calc.setSellerName("Priya Sharma");
        calc.setSellerCampus("Tech Campus Block C");
        calc.setMeetupArea("Cafeteria");
        calc.setNegotiable(false);
        calc.setBrand("Casio");
        calc.setModel("FX-991EX");
        calc.setWarrantyMonths(3);
        calc.setIncludesAccessories(true);
        calc.setWorkingBattery(true);
        productRepository.save(calc);

        Electronics earphones = new Electronics();
        earphones.setId(UUID.randomUUID().toString());
        earphones.setTitle("JBL Tune 120 TWS Earbuds");
        earphones.setDescription("Sound quality is great, one earbud has minor scratch on case.");
        earphones.setPrice(new java.math.BigDecimal("900"));
        earphones.setCondition(Condition.GOOD);
        earphones.setSellerId("demo-user-2");
        earphones.setSellerName("Arjun Mehta");
        earphones.setSellerCampus("Main Campus");
        earphones.setMeetupArea("Student Center");
        earphones.setNegotiable(true);
        earphones.setBrand("JBL");
        earphones.setModel("Tune 120 TWS");
        earphones.setWarrantyMonths(0);
        earphones.setIncludesAccessories(true);
        earphones.setWorkingBattery(true);
        productRepository.save(earphones);

        HostelEssential kettle = new HostelEssential();
        kettle.setId(UUID.randomUUID().toString());
        kettle.setTitle("Prestige Electric Kettle 1.5L");
        kettle.setDescription("Heats up in 3 minutes, no leaks, clean interior.");
        kettle.setPrice(new java.math.BigDecimal("400"));
        kettle.setCondition(Condition.GOOD);
        kettle.setSellerId("demo-user-2");
        kettle.setSellerName("Arjun Mehta");
        kettle.setSellerCampus("Main Campus");
        kettle.setMeetupArea("Hostel Block B Gate");
        kettle.setNegotiable(true);
        kettle.setItemSubtype("Kettle");
        kettle.setDurationUsedMonths(8);
        kettle.setBulky(false);
        productRepository.save(kettle);

        GeneralProduct notes = new GeneralProduct();
        notes.setId(UUID.randomUUID().toString());
        notes.setTitle("Hand-written OS Notes – Complete Semester");
        notes.setDescription("Full Operating Systems notes, organized chapter-wise. FREE!");
        notes.setPrice(new java.math.BigDecimal("0"));
        notes.setCondition(Condition.GOOD);
        notes.setSellerId("demo-user");
        notes.setSellerName("Priya Sharma");
        notes.setSellerCampus("Tech Campus Block C");
        notes.setMeetupArea("Library Ground Floor");
        notes.setNegotiable(false);
        notes.setCategory(Category.FREE_STUFF);
        productRepository.save(notes);
    }

    // ─── Public API ────────────────────────────────────────────────────────────

    /** Returns all ACTIVE listings as summaries (feed). */
    public List<ListingSummaryResponse> getFeed() {
        return productRepository.findByStatusOrderByCreatedAtDesc(ListingStatus.ACTIVE).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    /** Full detail for a single listing by ID. */
    public ListingDetailResponse getListingDetail(String id) {
        Product p = findOrThrow(id);
        p.incrementViewCount();
        productRepository.save(p);
        return toDetail(p);
    }

    /**
     * Factory Method: creates the right Product subclass from the request,
     * then validates and stores it.
     */
    public ListingDetailResponse createListing(CreateListingRequest req) {
        Product product = createProduct(req);           // Factory Method
        product.setId(UUID.randomUUID().toString());
        product.setTitle(req.getTitle().trim());
        product.setDescription(req.getDescription().trim());
        product.setPrice(req.getPrice());
        product.setCondition(req.getCondition());
        product.setImageUrls(req.getImageUrls());
        product.setSellerId("demo-user"); // Authentication is not implemented in this MVP yet.
        String sellerName = req.getSellerName() == null ? "" : req.getSellerName().trim();
        if (sellerName.length() < 2) {
            throw new IllegalArgumentException("Enter your name (at least 2 characters).");
        }
        String phone = req.getSellerPhone() == null ? "" : req.getSellerPhone().trim();
        String email = req.getSellerEmail() == null ? "" : req.getSellerEmail().trim();
        if (phone.isEmpty() && email.isEmpty()) {
            throw new IllegalArgumentException("Add a phone number or email so buyers can contact you.");
        }
        if (!email.isEmpty() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid seller email address.");
        }
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (!phone.isEmpty() && (phoneDigits.length() < 7 || phoneDigits.length() > 15)) {
            throw new IllegalArgumentException("Enter a valid seller phone number (7–15 digits).");
        }
        product.setSellerName(sellerName);
        product.setSellerPhone(phone);
        product.setSellerEmail(email);
        product.setSellerCampus("Your Campus");
        product.setMeetupArea(req.getMeetupArea() != null ? req.getMeetupArea().trim() : "Campus");
        product.setNegotiable(req.isNegotiable());

        try {
            product.validate();                         // Polymorphic validation
        } catch (DomainValidationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        productRepository.save(product);
        return toDetail(product);
    }

    /** Marks a listing as sold (encapsulated state transition). */
    public void markAsSold(String id) {
        Product p = findOrThrow(id);
        p.markSold();                                   // encapsulated mutation
        productRepository.save(p);
    }

    /** Returns all listings belonging to the demo user. */
    public List<ListingSummaryResponse> getMyListings() {
        return productRepository.findBySellerIdOrderByCreatedAtDesc("demo-user").stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    /** Simple keyword search. */
    public List<ListingSummaryResponse> search(String query) {
        return productRepository.findAll().stream()
                .filter(p -> p.getStatus() == ListingStatus.ACTIVE)
                .filter(p -> p.matchesSearchCriteria(query))    // Searchable interface
                .sorted(Comparator.comparing(Product::getCreatedAt).reversed())
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    // ─── Factory Method ────────────────────────────────────────────────────────

    /**
     * Factory Method Pattern: returns the correct concrete Product subclass
     * based on the requested category. Demonstrates OOP Factory Pattern.
     */
    private Product createProduct(CreateListingRequest req) {
        if (req.getCategory() == null) return new GeneralProduct();
        switch (req.getCategory()) {
            case TEXTBOOKS:
                Textbook tb = new Textbook();
                tb.setIsbn(req.getIsbn());
                tb.setCourseCode(req.getCourseCode());
                tb.setAuthor(req.getAuthor());
                tb.setEdition(req.getEdition());
                tb.setHasMarkings(Boolean.TRUE.equals(req.getHasMarkings()));
                return tb;
            case ELECTRONICS:
                Electronics e = new Electronics();
                e.setBrand(req.getBrand());
                e.setModel(req.getModel());
                e.setWarrantyMonths(req.getWarrantyMonths() != null ? req.getWarrantyMonths() : 0);
                e.setIncludesAccessories(Boolean.TRUE.equals(req.getIncludesAccessories()));
                e.setWorkingBattery(req.getWorkingBattery() == null || req.getWorkingBattery());
                return e;
            case HOSTEL_ESSENTIALS:
                HostelEssential h = new HostelEssential();
                h.setItemSubtype(req.getItemSubtype());
                h.setDurationUsedMonths(req.getDurationUsedMonths() != null ? req.getDurationUsedMonths() : 0);
                h.setBulky(Boolean.TRUE.equals(req.getBulky()));
                return h;
            case CLOTHING:
                Clothing c = new Clothing();
                c.setSize(req.getSize() != null ? req.getSize() : "M");
                c.setGenderFit(req.getGenderFit());
                c.setBrand(req.getBrand());
                c.setMaterial(req.getMaterial());
                return c;
            case SPORTS_FITNESS:
                SportsFitness s = new SportsFitness();
                s.setSportType(req.getSportType());
                s.setEquipmentWeightKg(req.getEquipmentWeightKg() != null ? req.getEquipmentWeightKg() : 0.0);
                return s;
            default:
                GeneralProduct g = new GeneralProduct();
                g.setCategory(req.getCategory());
                return g;
        }
    }

    // ─── Mappers ───────────────────────────────────────────────────────────────

    private ListingSummaryResponse toSummary(Product p) {
        ListingSummaryResponse r = new ListingSummaryResponse();
        r.setId(p.getId());
        r.setTitle(p.getTitle());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setCategory(p.getCategory());
        r.setCondition(p.getCondition());
        r.setSellerId(p.getSellerId());
        r.setSellerName(p.getSellerName());
        r.setSellerCampus(p.getSellerCampus());
        r.setMeetupArea(p.getMeetupArea());
        r.setStatus(p.getStatus());
        r.setNegotiable(p.isNegotiable());
        r.setViewCount(p.getViewCount());
        r.setFavoriteCount(p.getFavoriteCount());
        r.setCreatedAt(p.getCreatedAt());
        if (p.getImageUrls() != null && !p.getImageUrls().isEmpty())
            r.setPrimaryImageUrl(p.getImageUrls().get(0));
        return r;
    }

    private ListingDetailResponse toDetail(Product p) {
        ListingDetailResponse r = new ListingDetailResponse();
        r.setId(p.getId());
        r.setTitle(p.getTitle());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setCategory(p.getCategory());
        r.setCondition(p.getCondition());
        r.setImageUrls(p.getImageUrls());
        r.setSellerId(p.getSellerId());
        r.setSellerName(p.getSellerName());
        r.setSellerCampus(p.getSellerCampus());
        r.setSellerPhone(p.getSellerPhone());
        r.setSellerEmail(p.getSellerEmail());
        r.setMeetupArea(p.getMeetupArea());
        r.setStatus(p.getStatus());
        r.setNegotiable(p.isNegotiable());
        r.setViewCount(p.getViewCount());
        r.setFavoriteCount(p.getFavoriteCount());
        r.setCreatedAt(p.getCreatedAt());
        r.setCategorySummary(p.getCategorySpecificSummary());   // Polymorphism
        r.setSafetyChecklist(p.getSafetyChecklist());           // Polymorphism
        r.setOwner("demo-user".equals(p.getSellerId()));
        if (p.getImageUrls() != null && !p.getImageUrls().isEmpty())
            r.setPrimaryImageUrl(p.getImageUrls().get(0));
        return r;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Product findOrThrow(String id) {
        return productRepository.findById(id).orElseThrow(() -> new ListingNotFoundException(id));
    }
}
