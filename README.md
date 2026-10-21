# NightMarket — Spring Boot + MongoDB Full-Stack MVP

NightMarket is a native Android campus marketplace backed by a Java 17 / Spring Boot REST API and MongoDB. It demonstrates Java OOP concepts while providing persistent listings and seller contact actions.

## Technology stack

- **Android:** native Android Views in Java, built with Android Studio
- **Backend:** Java 17+, Spring Boot 3.3.4, Spring Web, Bean Validation
- **Build system:** Apache Maven
- **Database:** MongoDB through Spring Data MongoDB; supports local MongoDB Community Server or MongoDB Atlas
- **Communication:** JSON REST APIs over HTTP
- **Saved items:** Android SharedPreferences on the device

## Features

- Browse, search and sort active marketplace listings.
- View listing details, category-specific information and meetup location.
- Create listings with seller name and a phone number and/or email address.
- Contact a seller through the Android phone dialer, SMS composer or email composer when that listing has corresponding contact information.
- Save and unsave listings locally on the device.
- Persist listings and listing state in MongoDB across backend restarts.
- Retain category-specific Java product subclasses to demonstrate abstraction, inheritance, encapsulation, polymorphism and a factory method.
- Check backend/database status at `/api/health`.

> **Coursework/MVP limitation:** authentication and authorization are not implemented in this archive. A listing currently uses the demo seller ID. Contact details returned by a listing detail endpoint can be viewed by clients with access to that listing. Do not publish the service with real student data until authenticated accounts, ownership checks, and appropriate privacy controls are added. In-app chat, offers, and transaction workflows are not implemented yet.

## 1. Requirements

Install these on your Windows computer:

1. **JDK 17 or newer** (Java 17 is the project target).
2. **Apache Maven 3.9 or newer**.
3. **One MongoDB option:** MongoDB Community Server running locally, or a MongoDB Atlas cluster and connection string.
4. Android Studio for the Android client.

Run the Java backend directly with Maven; configure MongoDB separately.

## 2. Configure MongoDB

### Option A — MongoDB Atlas (no local database server needed)

1. Create a MongoDB Atlas cluster and database user.
2. In Atlas Network Access, allow the IP address of the computer running the backend. Avoid unrestricted public access for production.
3. Copy the Atlas application's connection string and use `nightmarket` as the database name.
4. In the project root, make a copy of `.env.example` named `.env`.
5. Edit `.env` and set `MONGODB_URI` to your connection string. URL-encode reserved characters in the password. Do not share or commit this file.

Example format (replace the placeholders with your own values):

```properties
MONGODB_URI=mongodb+srv://USERNAME:PASSWORD@YOUR-CLUSTER.mongodb.net/nightmarket?retryWrites=true&w=majority
PORT=8080
SEED_DATA_ENABLED=true
```

Keep the `MONGODB_URI=` line on one line. Do not wrap the value in quotes in `.env`.

### Option B — Local MongoDB Community Server

1. Install MongoDB Community Server for Windows.
2. Start its MongoDB service.
3. Keep `MONGODB_URI=mongodb://localhost:27017/nightmarket` in `.env`, or leave `.env` out to use the same default.

Do not expose an unsecured development database to the public internet.

## 3. Start the Spring Boot backend on Windows

From the project root (the folder containing `run-backend.bat`), run:

```powershell
.\run-backend.bat
```

The launcher reads simple key/value settings from `.env`, checks for Java and Maven, and starts the app with `mvn spring-boot:run`. You can also start it directly:

```powershell
cd backend
mvn spring-boot:run
```

If PowerShell says `mvn` is not recognized, install Maven and reopen the terminal. The launcher also searches common Maven install directories under `%LOCALAPPDATA%\Programs\Apache`.

The backend listens on port `8080` by default. Check:

- Health: `http://localhost:8080/api/health`
- Listing feed: `http://localhost:8080/api/listings/feed`

A connected database should produce a health response containing `"database":"CONNECTED"`. If it says `DISCONNECTED`, check the database service, `MONGODB_URI`, Atlas network access, and the database username/password. The API process may start even when MongoDB is unreachable; listing operations need a working database connection.

Demo listings are seeded into MongoDB only when the collection is empty and `SEED_DATA_ENABLED=true`. Set it to `false` to skip seeding.

## 4. Run the Android app

1. Start the MongoDB database option you configured.
2. Start the Spring Boot backend using the instructions above and leave its terminal open.
3. Open the `android` folder in Android Studio and wait for Gradle sync.
4. Start an Android emulator.
5. Select the `app` configuration and click the green **Run** button.

The default emulator URL is `http://10.0.2.2:8080`; this special address points from the Android emulator to the host computer. For a physical Android phone, set `nightmarketApiBaseUrl` in `android/gradle.properties` to `http://YOUR_COMPUTER_LAN_IP:8080`, keep both devices on the same Wi-Fi network, and allow access through the Windows firewall. Use HTTPS before any public deployment.

## 5. API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Backend and database status |
| GET | `/api/listings/feed` | Active listing summaries; no seller contact fields |
| GET | `/api/listings/search?q=book` | Search active listings |
| GET | `/api/listings/{id}` | Listing details and contact methods |
| GET | `/api/listings/mine` | Demo user's listings |
| POST | `/api/listings/add` | Create and persist a listing |
| POST | `/api/listings/{id}/mark-sold` | Persist sold status |

### Example listing request

```json
{
  "title": "Scientific Calculator",
  "description": "Good condition and all buttons work properly.",
  "price": 500,
  "category": "ELECTRONICS",
  "condition": "GOOD",
  "sellerName": "A Student",
  "sellerPhone": "+91 9876543210",
  "sellerEmail": "student@example.com",
  "meetupArea": "Campus cafeteria",
  "negotiable": true
}
```

At least one seller contact method is required for newly created listings.

## 6. Environment variables

| Variable | Default | Purpose |
|---|---|---|
| `PORT` | `8080` | HTTP server port |
| `MONGODB_URI` | `mongodb://localhost:27017/nightmarket` | MongoDB connection string |
| `SEED_DATA_ENABLED` | `true` | Insert demo listings if the database collection is empty |
| `UPLOAD_DIR` | `./uploads` | Reserved upload path for future image-storage functionality |

Spring Boot reads environment variables directly. The included Windows launcher additionally reads the project-root `.env` file. Keep your real credentials in `.env`, never in source code or a committed README.

## 7. OOP structure

- `domain/product/Product.java`: abstract domain base class and encapsulated marketplace state.
- `domain/product/`: category-specific product subclasses such as `Textbook`, `Electronics`, `Clothing`, `HostelEssential`, `SportsFitness`, and `GeneralProduct`.
- `domain/interfaces/`: behavior contracts such as `Searchable`, `Negotiable`, `Offerable`, and `Validatable`.
- `controller/`: Spring REST endpoints.
- `service/SimpleListingService.java`: listing rules, validation, object creation and orchestration.
- `repository/ProductRepository.java`: Spring Data MongoDB persistence boundary.
- `dto/`: request/response data transfer objects.

## Current limitations

This release is a persistent listing MVP, not a production-ready marketplace. Authentication, per-user listing ownership enforcement, in-app messaging, offer negotiation, transactions, notifications, and production image uploads need further implementation. Configure MongoDB and confirm `/api/health` reports `CONNECTED` before testing listing create/search flows.
