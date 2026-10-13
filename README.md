# NightMarket — runnable MVP

NightMarket is a student campus marketplace. This archive contains a Spring Boot REST API and a native Android client. The restored MVP supports browsing/searching listings, viewing details, and publishing a listing. It uses an in-memory store by default, so listings reset whenever the backend restarts.

## Current UI upgrade

The native Android UI has a refreshed visual system: branded gradient hero, clean search, category filters, a persistent Explore/Saved/Sell bottom bar, richer listing cards, optional API-provided listing photos with category-art fallbacks, local save/favourite behavior, and price sorting. Listing details and create-listing flows have clearer hierarchy and validation. The frontend remains Java-based and uses the existing Spring Boot endpoints.

Saved listing IDs persist with Android `SharedPreferences` on the same device only; they are not synchronized with an account or backend. Real item photos display when a listing includes a usable `primaryImageUrl`; seeded demo listings currently use category placeholders.

See [`UPGRADE_NOTES.md`](UPGRADE_NOTES.md) for details, OOP viva points, run commands, and verification limitations.

## What was repaired

- Added the missing `Product` constructor required by category-specific subclasses (the backend could not compile without it).
- Fixed the Spring Boot Maven parent version so Maven can resolve the project model.
- Wired the backend port, upload directory, and seed-data flag to environment variables.
- Added `/api/health` for a quick backend check.
- Added the missing Android launcher activity and theme/resources, plus a working UI for feed/search/details/create-listing.
- Made the Android API URL configurable for emulator or physical-device use.

## Requirements

- Java 17 or newer
- Maven 3.9 or newer
- For Android: Android Studio with Android SDK 35 and a compatible JDK (Android Studio can supply its own JDK)

The backend and Android dependencies need to be downloaded on first build, so the first run requires an internet connection.

## 1. Run the backend

**Windows PowerShell:**

```powershell
cd backend
mvn spring-boot:run
```

**macOS/Linux:**

```bash
cd backend
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. Check it by opening `http://localhost:8080/api/health`. To see the initial demo inventory, open `http://localhost:8080/api/listings/feed`.

### Run with Docker / deploy to Render

From the repository root, build and start the container:

```bash
docker build -t nightmarket .
docker run --rm -p 8080:8080 nightmarket
```

For Render, create a **Web Service** using the **Docker** runtime and this repository's root directory; the included root `Dockerfile` builds the backend. Render's `PORT` environment variable is honored. The current store is in memory, so a restart or redeploy resets listing changes; use a database before relying on hosted data.

Useful endpoints:

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Health check |
| GET | `/api/listings/feed` | Active listings |
| GET | `/api/listings/search?q=book` | Search listings |
| GET | `/api/listings/{id}` | Listing details |
| GET | `/api/listings/mine` | Demo user's listings |
| POST | `/api/listings/add` | Create listing |
| POST | `/api/listings/{id}/mark-sold` | Mark listing sold |

Example create-listing request:

```json
{
  "title": "Scientific Calculator",
  "description": "Good condition and all buttons work properly.",
  "price": 500,
  "category": "ELECTRONICS",
  "condition": "GOOD",
  "meetupArea": "Campus cafeteria",
  "negotiable": true
}
```

Environment variables supported by the backend:

- `PORT` (default `8080`)
- `UPLOAD_DIR` (default `./uploads`)
- `SEED_DATA_ENABLED` (default `true`; set to `false` for an empty in-memory feed)

The `.env.example` is only a reference; Spring Boot does not automatically load a plain `.env` file. Set variables in your shell or run configuration.

## 2. Run the Android app

1. Start the backend first and leave it running.
2. Open the `android` folder in Android Studio.
3. Let Gradle sync/download its dependencies.
4. Start an Android emulator (the default app URL is `http://10.0.2.2:8080`, the emulator's alias for your computer's localhost).
5. Select the `app` run configuration and press **Run**.

For a **physical Android phone**, the phone and computer must be on the same Wi-Fi network. Find your computer's LAN IPv4 address, then set the Gradle project property `nightmarketApiBaseUrl` to `http://YOUR_COMPUTER_IP:8080` (for example, by adding `nightmarketApiBaseUrl=http://192.168.1.10:8080` to `android/gradle.properties`). Allow inbound connections to port 8080 in your computer's firewall. Do not use plain HTTP for a public production deployment; configure HTTPS before deploying publicly.

## Current scope / limitations

The included backend is an in-memory listing MVP. Although request/response DTOs for authentication, offers, messaging, reports, and transactions are present, this archive does **not** include complete backend controllers/services for those features, and no database persistence or real authentication is implemented. The Android client currently covers the listing workflow only. Do not treat it as production-ready until authentication/authorization, persistence, upload validation, and production HTTPS/security are implemented.
