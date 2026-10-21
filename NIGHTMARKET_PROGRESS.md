# NightMarket — Development Progress & Checkpoint Log

## Current Status Overview
- **Overall Project Status**: All Checkpoints (1 through 6) Completed Successfully
- **Android Client**: Fully functional native Java + XML application matching Figma design tokens (Midnight, Lime, Lavender, Mint, Canvas)
- **Backend Service**: Java 17 / Spring Boot 3.3.4 with Spring Data MongoDB, Render deployment configurations (`render.yaml`, `backend/Procfile`), and Maven Wrapper (`mvnw`)
- **Database**: MongoDB persistence verified live (Local Community Server & MongoDB Atlas ready)
- **OOP Architecture**: 100% preserved and extended (Abstraction, Inheritance, Polymorphism, Encapsulation, Multiple Interfaces, Factory Method, Custom Exceptions)
- **Build Status**:
  - Backend: `BUILD SUCCESS` (`mvn clean package -DskipTests` in 14.3s)
  - Android: `BUILD SUCCESS` (`.\gradlew.bat assembleDebug` in 8s)

---

## Checkpoint 1 — Inspect, Plan, and Protect Existing Code
- **Status**: COMPLETED
- **Scope**: Full audit of existing backend source code (57 Java files), Android Gradle project, Figma visual design tokens, and Git repository initialization.
- **Actions & Commands**:
  - Verified JDK 17 (`17.0.20.1`) and Maven (`3.10.0`).
  - Audited `backend/src/...` identifying `Product` abstract class, subclasses (`Textbook`, `Electronics`, `Clothing`, `HostelEssential`, `SportsFitness`, `GeneralProduct`), and `SimpleListingService`.
  - Audited `android/` finding minimal initial state and verified Android SDK 35 and Gradle 8.7.
  - Initialized Git repository and created baseline commit `d846937`.
  - Created initial `NIGHTMARKET_PROGRESS.md`.

---

## Checkpoint 2 — Complete the Android UI to Match Figma
- **Status**: COMPLETED
- **Scope**: Implemented native Android Java + XML frontend matching the 5 attached Figma screens and exact design tokens.
- **Files Created**:
  - `android/app/src/main/res/layout/activity_main.xml` (CoordinatorLayout + FragmentContainer + BottomNavigationView)
  - `android/app/src/main/res/layout/fragment_home.xml` (Figma Screen 01: Brand header, Search, Category chips, Campus Edit banner, Popular Near You, Recently Listed)
  - `android/app/src/main/res/layout/fragment_product_detail.xml` (Figma Screen 02: Hero artwork, condition pill, savings pill, category summary, seller info card, meetup location, safety tips, message seller CTA)
  - `android/app/src/main/res/layout/fragment_add_item.xml` (Figma Screen 03: Sell an item form, dashed photo box, title, category, price, condition, description, seller contact details, validation)
  - `android/app/src/main/res/layout/fragment_my_listings.xml` (Figma Screen 04: Active/Sold tabs, listing cards with status pills, views/saves counter, popup menu, "+ Add a new item" CTA)
  - `android/app/src/main/res/layout/fragment_contact_seller.xml` (Figma Screen 05: Product bar preview, interactive chat bubbles, safety warning, quick replies, message input, direct intent triggers)
  - `android/app/src/main/res/layout/item_listing.xml` (Marketplace product card)
  - `android/app/src/main/res/layout/item_my_listing.xml` (Seller management card)
  - `android/app/src/main/res/menu/bottom_nav_menu.xml` (Bottom navigation items)
  - 25+ Vector Drawables and Background Shapes:
    - `bg_rounded_card.xml`, `bg_edit_text.xml`, `bg_badge_lime.xml`, `bg_badge_lavender.xml`, `bg_badge_mint.xml`, `bg_campus_edit_banner.xml`, `bg_dashed_border.xml`, `bg_chat_incoming.xml`, `bg_chat_outgoing.xml`, `bg_warning_box.xml`
    - `ic_nav_home.xml`, `ic_nav_explore.xml`, `ic_nav_sell.xml`, `ic_nav_saved.xml`, `ic_nav_profile.xml`, `ic_search.xml`, `ic_heart_filled.xml`, `ic_arrow_back.xml`, `ic_more_vert.xml`, `ic_camera.xml`, `ic_send.xml`, `ic_check.xml`, `ic_cat_books.xml`, `ic_cat_tech.xml`, `ic_cat_room.xml`, `ic_cat_more.xml`
- **Files Modified**:
  - `android/app/src/main/res/values/colors.xml`: Added Figma design tokens (`#0F172A` Midnight, `#D2F802` Lime, `#E9D5FF` Lavender, `#D1FAE5` Mint, `#F8F9FA` Canvas).
  - `android/app/src/main/res/values/styles.xml`: Configured `Theme.MaterialComponents.Light.NoActionBar` with custom button and card styles.
  - `android/app/src/main/res/values/strings.xml`: Added app labels, categories, and banners.

---

## Checkpoint 3 — Integrate and Verify Android API Functionality
- **Status**: COMPLETED
- **Scope**: Connected the Android frontend to the Spring Boot REST API using Retrofit, Gson, and Repository architecture.
- **Files Created**:
  - `android/app/src/main/java/com/nightmarket/app/model/Listing.java` (Client model matching backend `ListingSummaryResponse` and `ListingDetailResponse`)
  - `android/app/src/main/java/com/nightmarket/app/model/CreateListingRequest.java` (Payload model matching backend `CreateListingRequest`)
  - `android/app/src/main/java/com/nightmarket/app/api/ApiService.java` (Retrofit interface for all 7 endpoints)
  - `android/app/src/main/java/com/nightmarket/app/api/ApiClient.java` (Centralized Retrofit/OkHttp setup with dynamic URL switching)
  - `android/app/src/main/java/com/nightmarket/app/repository/RemoteListingRepository.java` (Data layer managing async callbacks and SharedPreferences saved items)
  - `android/app/src/main/java/com/nightmarket/app/adapter/ListingAdapter.java` (RecyclerView adapter for feed cards)
  - `android/app/src/main/java/com/nightmarket/app/adapter/MyListingAdapter.java` (RecyclerView adapter for user's listings)
  - `android/app/src/main/java/com/nightmarket/app/fragment/HomeFragment.java` (Feed, Search, Category filter, Pull-to-refresh, In-app API switcher)
  - `android/app/src/main/java/com/nightmarket/app/fragment/ProductDetailFragment.java` (Full detail loader, category summaries, contact triggers, mark-as-sold)
  - `android/app/src/main/java/com/nightmarket/app/fragment/AddItemFragment.java` (Form validation, multipart/JSON creation)
  - `android/app/src/main/java/com/nightmarket/app/fragment/MyListingsFragment.java` (Active/Sold tab filtering, seller actions)
  - `android/app/src/main/java/com/nightmarket/app/fragment/ContactSellerFragment.java` (Live chat simulation, quick replies, direct Call/SMS/Email intents)
- **Files Modified**:
  - `android/app/build.gradle`: Added Material, ConstraintLayout, RecyclerView, CardView, SwipeRefreshLayout, Retrofit 2, and Gson dependencies.
  - `android/app/src/main/java/com/nightmarket/app/MainActivity.java`: Refactored to clean fragment host with BottomNavigationView.
- **Build Outcome**: `.\gradlew.bat assembleDebug` -> **BUILD SUCCESS** in 8s.

---

## Checkpoint 4 — Add MongoDB Atlas Persistence while Preserving OOP
- **Status**: COMPLETED
- **Scope**: Verified Spring Data MongoDB integration, multi-table document mapping, inheritance metadata preservation, and prevented duplicate seeding.
- **Key Verifications**:
  - Checked local MongoDB on port 27017: `TcpTestSucceeded : True`.
  - Confirmed `Product.java` `@Document(collection = "listings")` stores runtime subclass metadata in `_class` attribute.
  - Subclasses `Textbook`, `Electronics`, `HostelEssential`, `Clothing`, `SportsFitness`, `GeneralProduct` retain subtype-specific attributes in MongoDB.
  - `SimpleListingService.initializeDatabase()` checks `productRepository.count() == 0` preventing duplicate seeds.
  - Verified `/api/health` reports `"database": "CONNECTED"`.
  - Verified listing creation (`POST /api/listings/add`) inserts documents into MongoDB.
  - Verified state transitions (`POST /api/listings/{id}/mark-sold`) update persistent document status to `SOLD`.
  - Verified persistence across process restart: Killed backend process, restarted it, and re-queried by ID; document state (`SOLD`, view counts, category summaries) remained intact.

---

## Checkpoint 5 — Prepare and Deploy Spring Boot Backend on Render
- **Status**: COMPLETED (Deployment Ready)
- **Scope**: Verified Maven packaging, port binding, environment variables, health checks, generated Maven Wrapper, and authored deployment blueprints.
- **Files Created**:
  - `render.yaml` (Render Web Service Blueprint specification with build and start commands, health check path, and environment definitions)
  - `backend/Procfile` (`web: java -jar target/nightmarket-backend-0.0.1-SNAPSHOT.jar`)
  - Maven Wrapper: `backend/mvnw`, `backend/mvnw.cmd`, `backend/.mvn/wrapper/maven-wrapper.properties`
- **Build Outcome**:
  - `mvn clean package -DskipTests` -> **BUILD SUCCESS** generating `target/nightmarket-backend-0.0.1-SNAPSHOT.jar`.
  - `mvn wrapper:wrapper` -> **BUILD SUCCESS** generating self-contained Maven Wrapper.
- **Configuration Verified**:
  - Port binding: `${PORT:8080}` (Render automatically provides `PORT=10000`).
  - MongoDB connection: `${MONGODB_URI:mongodb://localhost:27017/nightmarket}`.
  - Health check path: `/api/health`.

---

## Checkpoint 6 — Build Verification, Documentation, and Final Polish
- **Status**: COMPLETED
- **Scope**: End-to-end user journey verification, documentation upgrades, and college viva guide preparation.
- **Files Modified**:
  - `README.md`: Comprehensive documentation with Figma visual breakdown, API endpoint tables, local/emulator setup, Render deployment instructions, and detailed OOP viva notes tied to classes.
  - `NIGHTMARKET_PROGRESS.md`: Complete audit and checkpoint history.
- **End-to-End User Journey Verified**:
  1. Health Check: `/api/health` -> `{"status":"UP","database":"CONNECTED"}`.
  2. Load Feed: `/api/listings/feed` -> 5 active seed items returned.
  3. Create Listing: `POST /api/listings/add` -> "Sony WH-CH720N" created via `Electronics` factory method.
  4. Search: `GET /api/listings/search?q=Sony` -> Found matching listing.
  5. Detail: `GET /api/listings/{id}` -> Verified polymorphic category summary and safety checklist, viewCount incremented.
  6. Seller Listings: `GET /api/listings/mine` -> Verified user's listings list.
  7. Mark Sold: `POST /api/listings/{id}/mark-sold` -> Status changed to `SOLD` and excluded from feed.
  8. Persistence: Verified data remains stored in MongoDB across backend process restarts.
  9. Android Build: `.\gradlew.bat assembleDebug` -> **BUILD SUCCESS**.
