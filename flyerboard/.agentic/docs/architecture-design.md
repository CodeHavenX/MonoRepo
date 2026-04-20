# Architecture & Design

## Architecture & Design

The system follows a three-tier architecture: a Compose Multiplatform web frontend, a Kotlin JVM backend API, and Supabase as the managed data and file storage layer. Both the frontend and backend are deployed on a VPS.

### Components

- **Frontend (Compose Multiplatform / Web):** A single-page web application written in Kotlin using Compose Multiplatform targeting the browser via Wasm/JS. Handles browsing, user authentication flows, flyer upload forms, and the admin moderation UI.

- **Backend (Kotlin / Ktor):** A RESTful API server responsible for business logic — user management, flyer lifecycle (pending → approved → archived), admin actions, and file upload orchestration. Communicates with Supabase for data and storage.

- **Supabase (Postgres + Storage + Auth):** Provides the relational database (flyer metadata, users, moderation state), blob storage (uploaded image and PDF files), and user authentication via Supabase Auth. Accessed from the backend via **supabase-kt**, the official Kotlin Multiplatform client.

- **Reverse Proxy (Nginx):** Sits in front of both the frontend static assets and the backend API on the VPS, handling TLS termination and routing.

### Data Flow

1. **Browse:** A visitor opens the site → frontend requests active/approved flyers from the backend API → backend queries Supabase Postgres → returns flyer metadata and signed URLs for files → frontend renders the flyer list.

2. **Upload:** An authenticated user submits a flyer form → frontend sends file + metadata to the backend → backend uploads the file to Supabase Storage and writes a `pending` flyer record to Postgres → flyer enters the moderation queue.

3. **Moderation:** An admin reviews pending flyers in the admin UI → approves or rejects via the backend API → backend updates the flyer status in Postgres → approved flyers become publicly visible.

4. **Expiry:** An in-process coroutine scheduler inside the Ktor backend wakes up on a fixed interval (every hour) → queries for flyers whose expiry date has passed → transitions them from `approved` to `archived` → archived flyers remain readable but are surfaced separately in the browse UI.

5. **Edit:** The original uploader edits a flyer → changes are saved as `pending` again and go back through the moderation queue before becoming public.

### Key Design Decisions

- **Flyer states:** `pending` → `approved` → `archived` (also `rejected` for admin-declined submissions). Re-edits reset an approved flyer back to `pending`.
- **File storage:** Files are stored in Supabase Storage. The backend generates long-lived signed URLs (24-hour lifetime) for serving files to clients, keeping the bucket private. Long-lived URLs allow effective browser caching, which is an acceptable trade-off given flyers are already publicly visible once approved.
- **Accepted file formats:** Images (JPEG, PNG, WebP) and PDFs. Maximum file size is configurable via an environment variable (default: 10 MB).
- **File upload (WASM):** The file picker in the Compose Web frontend uses JS interop to trigger a browser `<input type="file">` element and read the selected bytes into Kotlin.
- **PDF display:** PDF flyers are displayed in the frontend using an `<iframe>` embed pointing to the signed URL, relying on native browser PDF rendering. No additional JS library required.
- **Auth token persistence (WASM):** Supabase Auth session tokens are persisted using the existing monorepo KV storage library, which is compatible with the WASM target. No direct `localStorage` JS interop needed.
- **Admin bootstrap:** On startup, the backend checks whether any admin user exists. If none is found, it creates one using credentials read from environment variables (`ADMIN_EMAIL`, `ADMIN_PASSWORD`). This runs once and is a no-op on subsequent restarts.
- **Search:** Basic keyword search on flyer title and description using Postgres `ILIKE` queries. Exposed via an optional `q` query parameter on the `GET /flyers` endpoint.
- **Migration tooling:** Database schema changes are managed as versioned SQL migration files committed directly to source control. Migrations are applied manually against Supabase.
- **Auth:** Supabase Auth handles user registration, sign-in, and JWT issuance. The Ktor backend validates Supabase-issued JWTs on protected endpoints. Supabase Row Level Security (RLS) is enforced at the database layer as a secondary safeguard.
- **Admin role:** A simple role field on the user record distinguishes regular users from admins. Admin-only endpoints are protected at the backend API layer.
- **Expiry job:** Implemented as an in-process `kotlinx-coroutines` scheduler within the Ktor backend. Runs every hour; no external cron job required.
- **Database access:** All Supabase interactions (Postgres queries, Storage, Auth) are handled via **supabase-kt**.
- **Retention:** Archived flyers are kept indefinitely — there is no deletion policy. The archive serves as a permanent public record of community notices.
- **Scale:** Designed for a large city — expect tens of thousands of flyers over time with hundreds of concurrent users. Pagination is mandatory on all list endpoints; Postgres indexes must be defined on frequently filtered columns (`status`, `expires_at`, `created_at`).
