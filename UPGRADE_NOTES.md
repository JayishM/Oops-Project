# NightMarket — Full-Stack Upgrade Notes

## Backend and data persistence

- Kept the backend in Java 17+ with Spring Boot 3.3.4 and Maven.
- Added Spring Data MongoDB persistence so listing creation, reads, searches, views and sold status use the MongoDB `listings` collection.
- Supports MongoDB Community Server for local development and MongoDB Atlas for a hosted database.
- Application configuration is environment based (`MONGODB_URI`, `PORT`, `SEED_DATA_ENABLED`). The Windows launcher reads key/value settings from the project-root `.env` file.
- The `/api/health` endpoint reports whether MongoDB is reachable.
- Start the database separately and run the Spring Boot app directly with Maven or the Maven Wrapper.

## Android UI

- Kept the native Android application implemented in Java and XML.
- Aligned to Figma screens: Discover / Home, Product Details, Add Item, My Listings, and Contact Seller.
- Retained the NightMarket marketplace UI, feed, search, category filters, sorting, listing details, saved-items experience and listing creation flow.
- Seller name and contact fields are available for newly created listings. The Contact seller action triggers the direct phone/SMS/email intents when corresponding contact details are present.

## Java OOP concepts

- **Abstraction and interfaces:** `Product` is an abstract base type implementing `Validatable`, `Searchable`, `Offerable`, and `Negotiable`.
- **Inheritance:** `Textbook`, `Electronics`, `Clothing`, `HostelEssential`, `SportsFitness`, and `GeneralProduct` extend `Product`.
- **Polymorphism:** product subclasses provide category-specific summaries, safety checklists and validation behavior.
- **Encapsulation:** the `Product` class owns product state and exposes methods for guarded changes.
- **Separation of responsibilities:** REST endpoint handling is under `controller/`, business logic in `service/SimpleListingService.java`, persistence through `repository/ProductRepository.java`, and API shapes under `dto/`.

## Run locally

1. Install Java 17+ and Maven 3.9+ (or use the included `mvnw` wrapper).
2. Configure `MONGODB_URI` in `.env` for local MongoDB or Atlas.
3. Run `run-backend.bat` from the project root, or `mvn spring-boot:run` from `backend/`.
4. Confirm `http://localhost:8080/api/health` returns `"database":"CONNECTED"`.
5. Open `android/` in Android Studio, start an emulator and run the `app` configuration.
