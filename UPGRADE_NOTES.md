# NightMarket — Frontend Upgrade Notes

## Second redesign pass

The Android frontend has been refined into a more complete campus-marketplace experience while retaining the existing Java/Spring Boot API.

- Reworked `android/app/src/main/java/com/nightmarket/app/MainActivity.java` into a compact brand bar, dark gradient hero, value strip, contained search field, seller call-to-action, category chips, feed controls, and persistent bottom navigation.
- Rebuilt listing cards with category-specific illustrated placeholders, stronger price hierarchy, condition labels, offer indicators, seller initials, campus meetup information, heart/save controls, rounded panels, and clearer spacing.
- Added optional image rendering from the existing `primaryImageUrl` field. Images load off the UI thread, use a small in-memory cache and size limit, and fall back to generated category artwork when there is no usable URL. No third-party image library was added.
- Added functional sort choices: latest, price low-to-high, and price high-to-low. Sorting only rearranges actual API results.
- Added a redesigned listing-details dialog with price, condition, category, description, item specifics, meetup details, seller, safety checklist (when returned by the API), and save/unsave action.
- Improved loading and network-error states, including a working retry action. The create-listing form now has a more polished intro and rejects negative or non-finite prices before sending the request.
- Kept the native Android UI in Java. Existing API endpoints and the Java backend were not changed. Saved listing IDs remain device-local in `SharedPreferences` and are not cloud-synced.

## Existing Java OOP concepts for a viva

- **Abstraction and interfaces:** `backend/src/main/java/com/nightmarket/domain/product/Product.java` is an abstract base type implementing `Validatable`, `Searchable`, `Offerable`, and `Negotiable`.
- **Inheritance:** `Textbook`, `Electronics`, `Clothing`, `HostelEssential`, `SportsFitness`, and `GeneralProduct` extend `Product`. These classes live under `backend/src/main/java/com/nightmarket/domain/product/`.
- **Polymorphism:** product subclasses supply category-specific summaries, safety checklists, and validation behavior. `SimpleListingService.createProduct(...)` selects a concrete product type based on the category, while search uses the `Searchable` abstraction.
- **Encapsulation:** `Product` owns its state and exposes methods for controlled changes and validation.
- **Separation of responsibilities:** endpoint handling is under `controller/`, business logic is in `service/SimpleListingService.java`, request/response payloads are under `dto/`, and domain concepts are under `domain/`.

## Run it

### Backend

```bash
cd backend
mvn spring-boot:run
```

The backend should start at `http://localhost:8080`. Check `/api/health` and `/api/listings/feed`.

### Android

Open the `android` directory in Android Studio, allow Gradle synchronization, start the backend, create/start an emulator, and run the `app` configuration. The default emulator API URL is `http://10.0.2.2:8080`. For a physical device, set `nightmarketApiBaseUrl` in `android/gradle.properties` to the computer's LAN IP and keep both devices on the same network.

## Verification and limitations

- Reviewed the edited source for structural consistency and checked Java delimiter balance.
- Android compilation and on-device rendering were **not verified** in this environment: the Gradle wrapper requires Gradle 8.7 from `services.gradle.org`, which could not be reached, and no Android SDK was installed in the environment.
- The Maven build and live API smoke test were **not run** because Maven is not installed in the environment.
- Listing photos will appear only when the backend returns a usable `primaryImageUrl`. Seeded demo items currently have no image URLs, so they use the built-in category artwork.
- The current backend is still an in-memory MVP; listing data resets when the backend restarts. This pass did not add authentication, a database, or upload UI.
