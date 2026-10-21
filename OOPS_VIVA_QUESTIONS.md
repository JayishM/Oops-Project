# NightMarket — 40 Object-Oriented Programming (OOP) Viva Questions & Answers

> **Document Type:** University Viva & Oral Defense Preparation Guide  
> **Target Project:** NightMarket (Android Java / Spring Boot 3 / MongoDB Atlas)  
> **Auditor:** Senior Java & OOP Engineering Specialist  
> **Structure:** 40 Questions across 7 Categories with Code Citations, In-Depth Answers, Tough Follow-Ups, and Rebuttal Strategies.

---

## Table of Contents
1. [Category 1: The Core Four Pillars (Q1 – Q8)](#category-1-the-core-four-pillars)
2. [Category 2: Inheritance & Polymorphism Deep Dive (Q9 – Q14)](#category-2-inheritance--polymorphism-deep-dive)
3. [Category 3: Abstraction & Interface Design (Q15 – Q20)](#category-3-abstraction--interface-design)
4. [Category 4: Encapsulation, State Guards & Immutability (Q21 – Q25)](#category-4-encapsulation-state-guards--immutability)
5. [Category 5: Design Patterns & Architecture in OOP (Q26 – Q30)](#category-5-design-patterns--architecture-in-oop)
6. [Category 6: Android OOP, Lifecycle & Callbacks (Q31 – Q34)](#category-6-android-oop-lifecycle--callbacks)
7. [Category 7: Tough Viva Traps & Edge-Case Questions (Q35 – Q40)](#category-7-tough-viva-traps--edge-case-questions)

---

## Category 1: The Core Four Pillars

### Question 1: What are the four fundamental pillars of OOP, and where is each pillar used in your NightMarket project?
**Answer:**
The four pillars are:
1. **Encapsulation:** In `com.nightmarket.domain.product.Product.java` (Lines 36–62), all 20+ fields are declared `private`. Mutators like `markSold()` (Line 168) and `decrementFavoriteCount()` (Line 182) guard internal state.
2. **Abstraction:** In `Product.java` (Line 34), declared as an `abstract class` with abstract methods `getCategorySpecificSummary()` and `getSafetyChecklist()`. We also define pure behavior interfaces: `Validatable`, `Searchable`, `Offerable`, and `Negotiable`.
3. **Inheritance:** `Textbook.java` (Line 15) and `Electronics.java` (Line 15) extend `Product`, inheriting core marketplace attributes and behavior while specializing in their domain.
4. **Polymorphism:** In `SimpleListingService.java` (Lines 338–339), calling `p.getCategorySpecificSummary()` on a base `Product p` reference dynamically resolves to the concrete subclass implementation at runtime.

**Examiner Follow-Up:** *"Why did you use OOP instead of a simpler procedural approach with switch-cases on category names?"*  
**Follow-Up Answer:** *"If we used procedural logic with switch statements, every time the campus added a new category—like 'Musical Instruments' or 'Bicycles'—we would have had to modify dozens of controllers and services. That violates the Open/Closed Principle (OCP). With OOP, we simply create a new subclass extending `Product` and implement the abstract methods without altering existing code."*

---

### Question 2: Explain how Encapsulation protects business invariants in your project.
**Answer:**
Encapsulation prevents illegal states. For example, in `Product.java` at Line 182:
```java
public void decrementFavoriteCount() { 
    if (this.favoriteCount > 0) this.favoriteCount--; 
}
```
If `favoriteCount` were public, any buggy frontend call could decrement it to `-5`. Furthermore, `markSold()` (Line 168) atomically updates both `status = ListingStatus.SOLD` and `updatedAt = LocalDateTime.now()`. External classes cannot alter status without timestamping the change.

**Examiner Follow-Up:** *"Does your code have any public fields?"*  
**Follow-Up Answer:** *"No instance variables are public. The only public fields are static constants, such as `public static final BigDecimal MIN_OFFER_RATIO = new BigDecimal("0.30");` in `Product.java` (Line 63), which represents an immutable business constant."*

---

### Question 3: What is the difference between Abstraction and Encapsulation? Give examples from NightMarket.
**Answer:**
- **Encapsulation** is about *information hiding and data protection* (bundling data and methods, making fields `private`, guarding mutations). Example: Making `sellerPhone` and `sellerEmail` private in `Product.java` and not including them in `ListingSummaryResponse`.
- **Abstraction** is about *hiding complexity and exposing high-level contracts* (defining what an object does, rather than how). Example: The `Searchable` interface (`matchesSearchCriteria(query)`) abstracts search query tokenization away from the service caller.

**Examiner Follow-Up:** *"Can you have encapsulation without abstraction?"*  
**Follow-Up Answer:** *"Yes. A simple class with private fields and public getters/setters has encapsulation, but if it doesn't separate interface contracts from implementation details or hide algorithmic complexity, it lacks meaningful abstraction."*

---

### Question 4: How does Inheritance promote code reuse in NightMarket?
**Answer:**
In `backend/src/main/java/com/nightmarket/domain/product/`, six classes (`Textbook`, `Electronics`, `HostelEssential`, `Clothing`, `SportsFitness`, and `GeneralProduct`) extend `Product`.
`Product.java` encapsulates:
- Core fields: `id`, `title`, `description`, `price`, `sellerId`, `status`, `viewCount`, `createdAt`.
- Domain behaviors: `markSold()`, `matchesSearchCriteria()`, `validateOfferAmount()`, `isPriceNegotiable()`.

None of the six subclasses re-declare these fields or methods. They inherit them directly, saving hundreds of lines of duplicated boilerplate.

**Examiner Follow-Up:** *"What is the downside of inheritance, and how did you mitigate it?"*  
**Follow-Up Answer:** *"The primary downside is tight coupling to the parent class (Fragile Base Class problem). We mitigated this by keeping `Product` focused strictly on universal marketplace attributes, while declaring category-specific behaviors as abstract methods."*

---

### Question 5: What is Dynamic Method Dispatch, and where does it occur in NightMarket?
**Answer:**
Dynamic Method Dispatch is the mechanism by which a call to an overridden method is resolved at runtime rather than compile time. In `SimpleListingService.java` at Line 338:
```java
private ListingDetailResponse toDetail(Product p) {
    ...
    r.setCategorySummary(p.getCategorySpecificSummary());
    r.setSafetyChecklist(p.getSafetyChecklist());
}
```
The parameter `p` is of type `Product`. When `p.getCategorySpecificSummary()` is called, the JVM inspects the object's virtual method table (vtable). If the instance is a `Textbook`, the `Textbook` implementation runs; if it is an `Electronics`, the `Electronics` implementation runs.

**Examiner Follow-Up:** *"Can dynamic method dispatch happen on a static method?"*  
**Follow-Up Answer:** *"No, static methods in Java are resolved at compile time using static binding based on the class reference type, not the runtime object instance. Method overriding only applies to instance methods."*

---

### Question 6: What is Compile-Time Polymorphism, and where is it in your project?
**Answer:**
Compile-Time Polymorphism (Static Binding) is achieved via Method Overloading. In `ApiResponse.java` (Lines 27–37):
```java
public static <T> ApiResponse<T> success(String message, T data) { ... }
public static <T> ApiResponse<T> success(T data) { ... }
public static <T> ApiResponse<T> error(String message) { ... }
```
The compiler determines which `success(...)` method to invoke based on the parameter count and types at compile time.

**Examiner Follow-Up:** *"Can you overload a method by changing only the return type in Java?"*  
**Follow-Up Answer:** *"No. The Java compiler differentiates overloaded methods solely by their method signature (name and parameter list). Changing only the return type produces a compiler error because the compiler cannot resolve ambiguity when the return value is ignored."*

---

### Question 7: Explain how the Liskov Substitution Principle (LSP) applies to your `Product` class hierarchy.
**Answer:**
LSP (the 'L' in SOLID) states that objects of a superclass should be replaceable with objects of a subclass without breaking application correctness.
In NightMarket, `ProductRepository` stores and retrieves `Product` references. When `SimpleListingService.getFeed()` calls `map(this::toSummary)` across a `List<Product>`, any subclass instance (`Textbook`, `Electronics`, etc.) can be passed to `toSummary(Product p)`. The service does not need to know the subclass type, and no exceptions are thrown because all subclasses fulfill the `Product` contract.

**Examiner Follow-Up:** *"Did any of your subclasses violate LSP by throwing UnsupportedOperationException on an inherited method?"*  
**Follow-Up Answer:** *"No. None of our subclasses throw `UnsupportedOperationException`. Every subclass fully implements `getCategorySpecificSummary()`, `getSafetyChecklist()`, and `validateCategoryDetails()`, adhering strictly to LSP."*

---

### Question 8: Where is the Single Responsibility Principle (SRP) applied in your backend?
**Answer:**
1. `ListingController.java`: Solely responsible for receiving HTTP requests, syntax validation (`@Valid`), and HTTP response status mapping.
2. `SimpleListingService.java`: Solely responsible for business logic orchestration, mapping entities to DTOs, and factory instantiation.
3. `Product.java`: Solely responsible for encapsulating domain state and enforcing domain invariants.
4. `ProductRepository.java`: Solely responsible for MongoDB database I/O.

**Examiner Follow-Up:** *"Why not put `productRepository.save()` directly inside `Product.java` (Active Record pattern)?"*  
**Follow-Up Answer:** *"Placing persistence logic inside domain entities couples business logic to database infrastructure, violating SRP. By using the Data Mapper / Repository pattern with Spring Data, `Product` remains pure Java without database driver dependencies."*

---

## Category 2: Inheritance & Polymorphism Deep Dive

### Question 9: Walk me through the constructor hierarchy when a `Textbook` object is created.
**Answer:**
In `Textbook.java` at Line 28:
```java
public Textbook(String title, String description, BigDecimal price, Condition condition,
                String sellerId, String sellerName, String sellerCampus, String meetupArea,
                boolean negotiable, String isbn, String courseCode, String author,
                String edition, boolean hasMarkings) {
    super(title, description, price, Category.TEXTBOOKS, condition, sellerId, sellerName,
            sellerCampus, meetupArea, negotiable);
    this.isbn = isbn;
    this.courseCode = courseCode;
    ...
}
```
1. When `new Textbook(...)` is invoked, the first statement executed is `super(...)`.
2. This invokes the parameterized constructor in `Product.java` (Line 73), which in turn invokes `this()` (Line 65).
3. The root `Product()` constructor initializes `createdAt = LocalDateTime.now()`, `updatedAt = LocalDateTime.now()`, and `status = ListingStatus.ACTIVE`.
4. Control returns to the parameterized `Product` constructor to set title, price, category, etc.
5. Finally, control returns to `Textbook` to initialize `isbn`, `courseCode`, `author`, and `edition`.

**Examiner Follow-Up:** *"What happens if you omit the `super()` call in a subclass constructor?"*  
**Follow-Up Answer:** *"If omitted, the Java compiler automatically inserts a call to the no-argument superclass constructor `super();`. If the superclass does not have a no-arg constructor, the code will fail to compile."*

---

### Question 10: How does Spring Data MongoDB persist and restore polymorphic Java objects?
**Answer:**
In `ProductRepository.java`:
```java
public interface ProductRepository extends MongoRepository<Product, String>
```
When `productRepository.save(textbook)` is called, Spring Data MongoDB inspects the runtime class and automatically saves a `_class` discriminator attribute in the MongoDB document:
```json
{
  "_id": "uuid-123",
  "_class": "com.nightmarket.domain.product.Textbook",
  "title": "Algorithms 4th Ed",
  "isbn": "978-0131103627",
  "courseCode": "CS301"
}
```
When querying `productRepository.findAll()`, Spring Data reads the `_class` field and uses reflection to instantiate the exact concrete subclass (`Textbook`). When we loop over the returned list, calling `p.getCategorySpecificSummary()` triggers dynamic dispatch on the restored `Textbook` instance.

**Examiner Follow-Up:** *"What would happen if the `_class` attribute was missing or corrupted in the database?"*  
**Follow-Up Answer:** *"Spring Data would fall back to instantiating the type specified in the repository generic declaration (`Product`). But since `Product` is an abstract class, instantiation would fail and throw an `InstantiationException`. That is why the `_class` field is critical for polymorphic document persistence."*

---

### Question 11: Explain how `validate()` demonstrates the Template Method Pattern.
**Answer:**
In `Product.java` (Lines 152–164):
```java
@Override
public void validate() throws DomainValidationException {
    if (title == null || title.trim().length() < 3)
        throw new DomainValidationException("Title must be at least 3 characters.");
    if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
        throw new DomainValidationException("Price cannot be negative.");
    ...
    // Subclass hook method:
    validateCategoryDetails();
}
```
`validate()` defines the template algorithm for validating any product. It verifies universal invariants (title length, non-negative price, non-null seller) and then invokes the abstract hook method `validateCategoryDetails()`.
Subclasses override `validateCategoryDetails()` to inject custom checks (e.g., `HostelEssential` checks that `durationUsedMonths >= 0`, `Electronics` checks `warrantyMonths >= 0`).

**Examiner Follow-Up:** *"Why is this better than having each subclass write its own complete `validate()` method?"*  
**Follow-Up Answer:** *"It guarantees that no subclass author forgets to validate universal rules like non-negative price or minimum title length, eliminating code duplication while still providing customization."*

---

### Question 12: Why is `Product` declared as `abstract` instead of a regular concrete class?
**Answer:**
Because in a real campus marketplace, an item cannot be an amorphous "product". It is always a specific item: a textbook, a pair of headphones, an electric kettle, or apparel. If `Product` were concrete, a developer could execute `new Product()` and insert an item with no category-specific attributes, breaking buyer safety checklists and category summaries. Declaring it `abstract` forces categorization.

**Examiner Follow-Up:** *"Can an abstract class have zero abstract methods?"*  
**Follow-Up Answer:** *"Yes, syntactically an abstract class in Java does not require any abstract methods. A class can be declared abstract purely to prevent instantiation."*

---

### Question 13: What is the purpose of `@Override` annotation, and what happens if you remove it?
**Answer:**
`@Override` is a compile-time safety check that instructs the compiler to verify that the method actually overrides a declaration in a superclass or interface.
If a developer misidentifies a signature (e.g., `public String getcategorySpecificSummary()` with a lowercase 'c'), without `@Override`, the compiler treats it as a new method. With `@Override`, the compiler immediately flags an error: *"method does not override or implement a method from a supertype"*.

**Examiner Follow-Up:** *"Is `@Override` mandatory in Java?"*  
**Follow-Up Answer:** *"No, the code will compile and execute without `@Override` if the signature matches, but omitting it removes compile-time verification and leaves the code vulnerable to silent bugs during refactoring."*

---

### Question 14: How did you verify polymorphism in automated tests?
**Answer:**
In `backend/src/test/java/com/nightmarket/domain/ProductOopTest.java` (Lines 23–45):
```java
@Test
void testPolymorphismCategorySummary() {
    Textbook tb = new Textbook();
    tb.setAuthor("Silberschatz");
    tb.setCourseCode("CS202");

    Electronics el = new Electronics();
    el.setBrand("Sony");
    el.setModel("WH-CH720N");

    Product p1 = tb; // Polymorphic upcasting
    Product p2 = el; // Polymorphic upcasting

    assertTrue(p1.getCategorySpecificSummary().contains("Silberschatz"));
    assertTrue(p2.getCategorySpecificSummary().contains("Sony"));
}
```
We assign instances of `Textbook` and `Electronics` to base `Product` references `p1` and `p2`. We then assert that invoking `getCategorySpecificSummary()` returns the specialized subclass attributes, proving dynamic dispatch works.

---

## Category 3: Abstraction & Interface Design

### Question 15: Name the custom interfaces in your domain model and state the contract of each.
**Answer:**
1. `Validatable` (`com.nightmarket.domain.interfaces.Validatable.java`): Exposes `void validate() throws DomainValidationException` to enforce internal business invariants.
2. `Searchable` (`Searchable.java`): Exposes `boolean matchesSearchCriteria(String query)` and `List<String> extractKeywords()` for search token matching.
3. `Offerable` (`Offerable.java`): Exposes `boolean canReceiveOffers()` and `void validateOfferAmount(BigDecimal offerAmount)` for price negotiation eligibility.
4. `Negotiable` (`Negotiable.java`): Exposes `boolean isPriceNegotiable()` and `BigDecimal getMinimumAcceptablePrice()` to compute acceptable negotiation floors.
5. `NotificationSender` (`NotificationSender.java`): Exposes `void send(Notification notification)` for messaging dispatch.

**Examiner Follow-Up:** *"Why create 4 smaller interfaces instead of one large `MarketplaceItem` interface?"*  
**Follow-Up Answer:** *"This is a direct application of the Interface Segregation Principle (ISP). ISP states that clients should not be forced to depend on methods they do not use. For example, a student user profile might be `Validatable` and `Searchable`, but not `Offerable`. Splitting them allows each capability to be reused independently."*

---

### Question 16: What is the difference between an Interface and an Abstract Class in Java 17?
**Answer:**
1. **Multiple Inheritance:** A class can implement multiple interfaces, but can extend only one abstract class.
2. **State:** Abstract classes can have instance variables and protected state (`Product` has 20+ fields). Interfaces cannot have instance state; they can only have `public static final` constants.
3. **Constructors:** Abstract classes have constructors invoked via `super()`. Interfaces cannot have constructors.
4. **Purpose:** An abstract class defines identity (**IS-A**); an interface defines capability (**CAN-DO**).

**Examiner Follow-Up:** *"Can interfaces have method implementations in Java 17?"*  
**Follow-Up Answer:** *"Yes. Since Java 8, interfaces can have `default` and `static` methods with bodies, and Java 9 added `private` helper methods in interfaces. However, they still cannot hold instance state."*

---

### Question 17: How is `ApiService` an example of Abstraction in the Android application?
**Answer:**
In `android/app/src/main/java/com/nightmarket/app/api/ApiService.java`:
```java
public interface ApiService {
    @GET("api/listings/feed")
    Call<List<Listing>> getFeed();

    @POST("api/listings/add")
    Call<Listing> createListing(@Body CreateListingRequest request);
}
```
`ApiService` is a pure Java interface with HTTP annotations. It declares the API contract. The calling UI (`HomeFragment`) does not know whether the network uses HTTP/1.1 or HTTP/2, how JSON serialization is performed, or how connection pooling works. Retrofit generates a dynamic proxy implementation behind this abstraction.

**Examiner Follow-Up:** *"What design pattern does Retrofit use to create the concrete implementation of `ApiService`?"*  
**Follow-Up Answer:** *"Retrofit uses the Dynamic Proxy pattern (specifically `java.lang.reflect.Proxy`) to intercept interface method calls at runtime and translate them into HTTP network executions."*

---

### Question 18: What is the `ApiCallback<T>` interface in `RemoteListingRepository`?
**Answer:**
In `RemoteListingRepository.java` at Line 30:
```java
public interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(String errorMessage);
}
```
It is a generic callback interface. Because Android strictly forbids network operations on the main UI thread, network operations run on a background thread. `ApiCallback<T>` abstracts the asynchronous response handling. When the response arrives, the repository notifies the caller via `onSuccess` or `onError`.

**Examiner Follow-Up:** *"Why make it generic with `<T>`?"*  
**Follow-Up Answer:** *"Generics make `ApiCallback` reusable across all endpoints: `ApiCallback<List<Listing>>` for feeds, `ApiCallback<Listing>` for item details, and `ApiCallback<Void>` for marking an item sold."*

---

### Question 19: Why does `Product.java` implement `Negotiable` and `Offerable` instead of having negotiation logic in the controller?
**Answer:**
Placing business rules inside domain entities is called a **Rich Domain Model**.
If negotiation rules (such as checking whether an item is active and validating that an offer is at least 30% of the price) were in `ListingController`, any new endpoint or background job making offers would have to duplicate that logic. Placing it inside `Product.validateOfferAmount(BigDecimal offer)` guarantees that negotiation rules are enforced universally.

**Examiner Follow-Up:** *"What do you call the anti-pattern where domain classes only have getters and setters and no business logic?"*  
**Follow-Up Answer:** *"That is called the **Anemic Domain Model** anti-pattern. We deliberately avoided it by encapsulating domain validation and state transitions directly within `Product`."*

---

### Question 20: Can an interface extend another interface in Java? Give an example from your project.
**Answer:**
Yes, an interface can extend one or more other interfaces using the `extends` keyword.
In `backend/src/main/java/com/nightmarket/repository/ProductRepository.java` (Line 14):
```java
public interface ProductRepository extends MongoRepository<Product, String>
```
`ProductRepository` is an interface that extends Spring Data's `MongoRepository` interface, inheriting methods like `findAll()`, `save()`, `findById()`, and `deleteById()`.

---

## Category 4: Encapsulation, State Guards & Immutability

### Question 21: How does your code protect against negative prices or invalid titles?
**Answer:**
Through two layers of validation:
1. **DTO Syntax Validation (Controller Boundary):** In `CreateListingRequest.java`, Bean Validation annotations (`@NotBlank`, `@Min(0)`, `@NotNull`) intercept malformed JSON at the HTTP boundary before it enters business logic.
2. **Domain Invariant Validation (Domain Entity):** In `Product.java` (Lines 152–159), `validate()` executes:
```java
if (title == null || title.trim().length() < 3)
    throw new DomainValidationException("Title must be at least 3 characters.");
if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
    throw new DomainValidationException("Price cannot be negative.");
```
Even if an internal service instantiates a `Product` programmatically without going through the REST controller, `product.validate()` prevents saving invalid data.

**Examiner Follow-Up:** *"Why use `BigDecimal` instead of `double` or `float` for prices?"*  
**Follow-Up Answer:** *"Floating-point types (`double`/`float`) suffer from binary representation rounding errors (e.g., `0.1 + 0.2 = 0.30000000000000004`). In financial and pricing calculations, `BigDecimal` provides exact arbitrary-precision arithmetic."*

---

### Question 22: What is Information Hiding, and how is it implemented between `ListingSummaryResponse` and `ListingDetailResponse`?
**Answer:**
Information Hiding is the practice of concealing internal or sensitive data from external consumers who have no need to know it.
In `SimpleListingService.java`:
- When students browse the public campus feed via `getFeed()`, the service maps products to `ListingSummaryResponse` (Lines 296–316). Notice that `sellerPhone` and `sellerEmail` are completely excluded.
- Only when a student explicitly taps on an item to view details via `getListingDetail(id)` does the service map to `ListingDetailResponse` (Lines 318–344), which includes phone and email.
This protects students from public phone number harvesting and spam bots.

**Examiner Follow-Up:** *"Why not just use one class and return null for the phone field in the feed?"*  
**Follow-Up Answer:** *"Using distinct DTOs creates an explicit API contract. If we used a single class with null fields, frontend developers or API consumers wouldn't know if the phone was genuinely missing or deliberately omitted."*

---

### Question 23: How do you achieve encapsulation in the Android `Listing.java` model?
**Answer:**
In `android/app/src/main/java/com/nightmarket/app/model/Listing.java`:
1. All attributes (`id`, `title`, `price`, `status`, `sellerName`, etc.) are `private`.
2. Access is mediated via getters that provide null-safe defaults (e.g., `getTitle()` returns `title != null ? title : ""` to prevent `NullPointerException` in Android UI rendering).
3. Derived business presentation logic is encapsulated inside the model:
   - `getFormattedPrice()` formats `BigDecimal.ZERO` as `"FREE"` and positive values as `"₹4200"`.
   - `getSellerInitials()` splits the seller name into initials (e.g., `"Priya Sharma"` -> `"PS"`).
   - `isSold()` encapsulates status checking: `"SOLD".equalsIgnoreCase(status)`.

**Examiner Follow-Up:** *"Why put `getFormattedPrice()` inside `Listing.java` instead of inside the `ListingAdapter`?"*  
**Follow-Up Answer:** *"Encapsulating formatting logic in `Listing.java` avoids code duplication. If both `ListingAdapter` and `ProductDetailFragment` need to display the formatted price, they call `listing.getFormattedPrice()` rather than duplicating string concatenation logic."*

---

### Question 24: What are the access modifiers in Java, and what is your rationale for using `protected` on `Product` constructors?
**Answer:**
Java provides four access levels:
- `private`: Accessible only within the declaring class.
- *Default (Package-Private)*: Accessible within the same package.
- `protected`: Accessible within the same package and by subclasses in other packages.
- `public`: Accessible from anywhere.

In `Product.java` (Lines 65, 73), the constructors are declared `protected`:
```java
protected Product() { ... }
protected Product(String title, ...) { ... }
```
**Rationale:** `Product` is an abstract class that should never be directly instantiated outside its inheritance hierarchy. Declaring its constructors `protected` allows its subclasses (`Textbook`, `Electronics`, etc.) to invoke them via `super()`, while preventing external service classes from attempting to invoke `new Product(...)`.

---

### Question 25: How does `Product.java` handle mutable collections to prevent breaking encapsulation?
**Answer:**
In `Product.java` at Line 112:
```java
@Override
public List<String> extractKeywords() {
    List<String> kw = new ArrayList<>();
    if (title != null) kw.addAll(Arrays.asList(title.toLowerCase(Locale.ROOT).split("\\s+")));
    if (category != null) kw.add(category.name().toLowerCase(Locale.ROOT));
    return Collections.unmodifiableList(kw);
}
```
`extractKeywords()` wraps the returned list with `Collections.unmodifiableList(kw)`. If an external caller attempts to call `.add()` or `.clear()` on the returned list, Java throws an `UnsupportedOperationException`, preserving the internal state of the domain object.

---

## Category 5: Design Patterns & Architecture in OOP

### Question 26: What design patterns are implemented in NightMarket?
**Answer:**
1. **Factory Method Pattern:** In `SimpleListingService.java` (Lines 250–292), `createProduct(CreateListingRequest req)` encapsulates subclass instantiation based on the `Category` enum.
2. **Repository Pattern:** `ProductRepository.java` encapsulates data access and persistence mechanics behind a clean interface.
3. **Dependency Injection / Inversion of Control (IoC):** Spring Boot injects dependencies (`ProductRepository` into `SimpleListingService`, and `SimpleListingService` into `ListingController`) via constructor injection.
4. **Adapter Pattern:** `ListingAdapter.java` in Android adapts in-memory domain models (`Listing`) into Android UI widgets (`RecyclerView.ViewHolder`).
5. **Data Transfer Object (DTO) Pattern:** `CreateListingRequest`, `ListingSummaryResponse`, and `ListingDetailResponse` decouple internal domain entities from public REST API payloads.

**Examiner Follow-Up:** *"Show me the exact code of the Factory Method in your project."*  
**Follow-Up Answer:** *"In `SimpleListingService.java` at Line 250:*
```java
private Product createProduct(CreateListingRequest req) {
    if (req.getCategory() == null) return new GeneralProduct();
    switch (req.getCategory()) {
        case TEXTBOOKS:
            Textbook tb = new Textbook();
            tb.setIsbn(req.getIsbn());
            tb.setCourseCode(req.getCourseCode());
            return tb;
        case ELECTRONICS:
            Electronics e = new Electronics();
            e.setBrand(req.getBrand());
            e.setModel(req.getModel());
            return e;
        ...
```
*The client calls `createProduct(req)`, receiving a base `Product` reference without having to manually manage constructor instantiation."*

---

### Question 27: What is Dependency Injection, and why did you use constructor injection over `@Autowired` field injection?
**Answer:**
Dependency Injection is a design pattern where an object receives its dependencies from an external assembler rather than creating them itself.
In `ListingController.java` (Lines 29–33):
```java
public class ListingController {
    private final SimpleListingService listingService;

    public ListingController(SimpleListingService listingService) {
        this.listingService = listingService;
    }
}
```
**Why Constructor Injection?**
1. **Immutability:** The dependency field is declared `final`, ensuring it cannot be changed after initialization.
2. **Null Safety:** The class cannot be instantiated without its required dependencies.
3. **Ease of Unit Testing:** We can easily pass mock objects (e.g., Mockito mocks) in plain JUnit tests without needing a reflection-based Spring container.

---

### Question 28: What is the purpose of `GlobalExceptionHandler.java`? How does it demonstrate OOP principles?
**Answer:**
`GlobalExceptionHandler.java` uses Spring's `@RestControllerAdvice` and `@ExceptionHandler` annotations to intercept uncaught exceptions across all controllers.
It demonstrates OOP Exception Hierarchy and Polymorphism:
- If a `DomainValidationException` is thrown, `handleDomainValidation` catches it and returns HTTP 400 Bad Request.
- If a `ListingNotFoundException` is thrown, `handleListingNotFound` catches it and returns HTTP 404 Not Found.
- If any unexpected `Exception` occurs, `handleGenericException` catches it, logs the error, and returns a safe HTTP 500 payload without exposing internal stack traces or database connection strings.

**Examiner Follow-Up:** *"Are your custom exceptions checked or unchecked? Why?"*  
**Follow-Up Answer:** *"They extend `RuntimeException`, making them **unchecked exceptions**. In modern Spring architectures, unchecked exceptions are preferred because they avoid cluttering method signatures with `throws` clauses, while still allowing centralized handlers like `@RestControllerAdvice` to intercept them cleanly."*

---

### Question 29: Explain the Adapter Pattern as used in your Android `ListingAdapter`.
**Answer:**
In Android, a `RecyclerView` knows how to recycle and display views on screen, but knows nothing about NightMarket listings. A `Listing` model has marketplace data, but knows nothing about Android XML layout rendering.
`ListingAdapter.java` acts as the Adapter:
1. `onCreateViewHolder`: Inflates `R.layout.item_listing`.
2. `onBindViewHolder`: Reads properties from `Listing` (title, price, category) and sets them on the `ViewHolder` widgets.
It bridges two incompatible interfaces without coupling data models to Android UI rendering.

---

### Question 30: Explain how Composition was preferred over Inheritance in `ListingController`.
**Answer:**
Instead of `ListingController extends SimpleListingService`, `ListingController` HAS-A `SimpleListingService` reference.
Inheriting from the service would expose internal persistence methods to the controller and violate the principle of least privilege. Composition provides a strict boundary: the controller only invokes public service methods, maintaining loose coupling.

---

## Category 6: Android OOP, Lifecycle & Callbacks

### Question 31: How do Android Fragments demonstrate inheritance and lifecycle polymorphism?
**Answer:**
In `HomeFragment.java` (Line 35):
```java
public class HomeFragment extends Fragment implements ListingAdapter.OnListingClickListener
```
`HomeFragment` extends Android's `Fragment` superclass. It overrides lifecycle methods:
- `onCreateView`: Inflates the XML layout.
- `onViewCreated`: Binds view IDs, initializes `RemoteListingRepository`, and triggers `loadFeed()`.

The Android Operating System manages the Fragment lifecycle polymorphically. When a screen rotation or navigation event occurs, the OS invokes these overridden methods on any active `Fragment` subclass instance.

**Examiner Follow-Up:** *"Why did you use Fragments inside `MainActivity` instead of making every screen a separate Activity?"*  
**Follow-Up Answer:** *"Using a Single-Activity Architecture (`MainActivity` hosting `HomeFragment`, `ProductDetailFragment`, etc.) provides smoother transitions, shared state management, lower memory overhead, and aligns with modern Android architectural guidelines."*

---

### Question 32: What is the `OnListingClickListener` interface in `ListingAdapter`?
**Answer:**
In `ListingAdapter.java` at Line 25:
```java
public interface OnListingClickListener {
    void onListingClick(Listing listing);
}
```
This demonstrates the **Listener / Observer Pattern**.
`ListingAdapter` handles list rendering, but navigation logic (switching fragments) belongs to the Fragment. By defining an inner interface `OnListingClickListener`, the adapter notifies `HomeFragment` when a user taps a card. `HomeFragment` implements this interface:
```java
public class HomeFragment extends Fragment implements ListingAdapter.OnListingClickListener {
    @Override
    public void onListingClick(Listing listing) {
        // Open ProductDetailFragment passing listing ID
    }
}
```

**Examiner Follow-Up:** *"Why not just pass the Activity context directly into the adapter and start the fragment from there?"*  
**Follow-Up Answer:** *"That would tightly couple the adapter to a specific Activity or Fragment, making the adapter non-reusable. Using an interface allows `ListingAdapter` to be reused anywhere without knowing who handles the click."*

---

### Question 33: How does `Listing` implement `Serializable` in Android?
**Answer:**
In `android/app/src/main/java/com/nightmarket/app/model/Listing.java` (Line 12):
```java
public class Listing implements Serializable
```
`Serializable` is a marker interface (an interface with no methods). By implementing `Serializable`, `Listing` instances can be flattened into a byte stream and passed across Android Fragment transactions via `Bundle.putSerializable()` or preserved during activity state restoration.

**Examiner Follow-Up:** *"What is the difference between `Serializable` and Android's `Parcelable`?"*  
**Follow-Up Answer:** *"`Serializable` is standard Java reflection-based serialization, which is simple to implement but has reflection overhead. `Parcelable` is Android-specific and faster because developers explicitly write and read fields to a `Parcel`. For an MVP with simple models, `Serializable` is sufficient and standard."*

---

### Question 34: Where is the Singleton Pattern used in your Android networking layer?
**Answer:**
In `android/app/src/main/java/com/nightmarket/app/api/ApiClient.java`:
The Retrofit instance and `ApiService` client are created once and shared globally across all fragments. This prevents the application from repeatedly instantiating expensive `OkHttpClient` connection pools, SSL sockets, and thread executors.

---

## Category 7: Tough Viva Traps & Edge-Case Questions

### Question 35: *"Can you point to a class in your code that demonstrates Multiple Inheritance?"*
**Answer (Watch out for the trap!):**
> *"Sir, Java **does not support multiple class inheritance**; a class cannot extend more than one class. If a class could inherit from two classes having the same method, the compiler would encounter the Diamond Problem.
>
> However, Java supports **multiple inheritance of type through interfaces**. In our project, `Product.java` implements four distinct interfaces simultaneously:
> ```java
> public abstract class Product implements Validatable, Searchable, Offerable, Negotiable
> ```
> This allows `Product` to inherit behavior contracts from four sources without the diamond ambiguity problem."*

---

### Question 36: *"Why did you use concrete classes for `Textbook` and `Electronics` instead of just adding a `category` string to `Product`?"*
**Answer:**
> *"If we used a single flat class with a category string, `Product` would have to hold `isbn`, `edition`, `warrantyMonths`, `clothingSize`, and hundreds of category-specific fields.
>
> For any given item, 90% of those fields would be `null`, creating a bloated, sparse data model with no compile-time type safety.
>
> More importantly, we couldn't have polymorphically overridden methods like `getCategorySpecificSummary()` or `getSafetyChecklist()`. We would have had to write massive `switch-case` blocks throughout our service layer, violating the Open/Closed Principle."*

---

### Question 37: *"What is the Diamond Problem, and how does Java avoid it?"*
**Answer:**
The Diamond Problem occurs in languages that allow multiple class inheritance (like C++). If Class B and Class C both inherit from Class A and override method `m()`, and Class D inherits from both B and C, which version of `m()` does D inherit?
Java avoids this by allowing a class to extend only **one** superclass. Even with Java 8 default methods in interfaces, if a class implements two interfaces having the identical default method signature, the Java compiler refuses to compile until the class explicitly overrides and resolves the conflict.

---

### Question 38: *"Is it possible to instantiate an abstract class using the `new` keyword?"*
**Answer (Another common trap!):**
> *"No, you cannot directly instantiate an abstract class like `new Product()`.
>
> However, you can use the `new` keyword with an **Anonymous Inner Class**:
> ```java
> Product p = new Product() {
>     @Override
>     public String getCategorySpecificSummary() { return "Custom"; }
>     @Override
>     public List<String> getSafetyChecklist() { return Collections.emptyList(); }
>     @Override
>     public void validateCategoryDetails() {}
> };
> ```
> Even in this case, the JVM is not instantiating `Product`; it is instantiating an anonymous subclass that extends `Product` and implements its abstract methods."*

---

### Question 39: *"What is the difference between Aggregation and Composition? Give examples from NightMarket."*
**Answer:**
Both are types of HAS-A relationships:
- **Composition (Strong Ownership):** The child's lifecycle depends entirely on the parent. If the parent is destroyed, the child ceases to exist.
  - Example: `ListingController` and its constructor-injected `SimpleListingService`. The service instance is created and managed within the application context for the controller's lifecycle.
- **Aggregation (Weak Ownership):** The child can exist independently of the parent.
  - Example: `ListingAdapter` and its `List<Listing>`. The listing data objects exist before the adapter is instantiated and can be transferred to other fragments or retained in a cache even if the adapter is destroyed during view recycling.

---

### Question 40: *"What are the SOLID principles, and how does NightMarket demonstrate each one?"*
**Answer:**
1. **S — Single Responsibility Principle:** Separate classes for Controller (HTTP), Service (Business Logic), Repository (Storage), and Model (Domain State).
2. **O — Open/Closed Principle:** New categories (e.g., `Bicycle extends Product`) can be added without modifying existing `Product` or `ProductRepository` code.
3. **L — Liskov Substitution Principle:** Any subclass of `Product` (`Textbook`, `Electronics`) can be passed to `SimpleListingService.toSummary(Product p)` without errors.
4. **I — Interface Segregation Principle:** Decomposed capabilities into four specialized interfaces (`Validatable`, `Searchable`, `Offerable`, `Negotiable`) rather than one giant interface.
5. **D — Dependency Inversion Principle:** High-level modules (`ListingController`, `SimpleListingService`) depend on abstractions (`ProductRepository` interface, Spring `@Service` beans) rather than concrete low-level implementations.

---

*End of Viva Preparation Document — All 40 questions verified against NightMarket upgraded codebase.*
