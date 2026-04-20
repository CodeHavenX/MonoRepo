I have all the context I need. Here is the low-level plan:

# Low-Level Plan

## Overview

This plan breaks down the FlyerBoard high-level plan into concrete, PR-sized units of work. Each unit references specific files, patterns, and conventions from the existing monorepo (primarily `templatereplaceme/` as scaffold and `edifikana/` as the Supabase integration reference).

---

## Unit 1: Project Scaffolding

**PR 1.1 — Clone templatereplaceme and rename to flyerboard**

- **Approach:** Copy the entire `templatereplaceme/` directory to `flyerboard/`. Rename all occurrences of `templatereplaceme`/`TemplateReplaceMeMe` in:
  - Package names (`com.cramsan.templatereplaceme` → `com.cramsan.flyerboard`)
  - Class names (`TemplateReplaceMeBackEnd` → `FlyerBoardBackEnd`, etc.)
  - Gradle module names, namespaces, and build config
  - WASM module name (`TemplateReplaceMeWasmApp` → `FlyerBoardWasmApp`)
  - Ktor application.conf (main class reference)
  - DI module names
- **Affected files:** Everything under `flyerboard/` (new), `settings.gradle.kts` (add module registrations), root `build.gradle.kts` (add to `releaseAll`)
- **Register modules in `settings.gradle.kts`:** `flyerboard:shared`, `flyerboard:api`, `flyerboard:back-end`, `flyerboard:front-end:shared-ui`, `flyerboard:front-end:shared-app`, `flyerboard:front-end:app-wasm`
- **Exclude:** `app-android/` and `app-jvm/` entry points (out of scope)
- **Validation:** `./gradlew :flyerboard:back-end:release` and `./gradlew :flyerboard:front-end:app-wasm:release` both pass
- **Dependencies:** None

**PR 1.2 — Add supabase-kt dependencies to flyerboard back-end**

- **Approach:** Add `io.github.jan-tennert.supabase:postgrest-kt`, `storage-kt`, `auth-kt` to `flyerboard/back-end/build.gradle.kts`. Add version entries to `versions.properties` if not already present (edifikana already uses them, so versions should exist). Add Ktor client engine dependency (`ktor-client-cio`) needed by supabase-kt.
- **Affected files:** `flyerboard/back-end/build.gradle.kts`, possibly `versions.properties`
- **Reference:** `edifikana/back-end/build.gradle.kts` for exact dependency coordinates
- **Validation:** Module compiles
- **Dependencies:** PR 1.1

---

## Unit 2: Shared Models & API Contracts

**PR 2.1 — Domain models in shared module**

- **Approach:** Create domain value types and enums in `flyerboard/shared/src/commonMain/kotlin/com/cramsan/flyerboard/lib/model/`:
  - `FlyerId.kt` — `@JvmInline value class FlyerId(val flyerId: String) : PathParam` (pattern: `templatereplaceme/.../model/UserId.kt`)
  - `UserId.kt` — `@JvmInline value class UserId(val userId: String) : PathParam`
  - `FlyerStatus.kt` — `@Serializable enum class FlyerStatus { PENDING, APPROVED, REJECTED, ARCHIVED }`
  - `UserRole.kt` — `@Serializable enum class UserRole { USER, ADMIN }`
- **Affected files:** `flyerboard/shared/src/commonMain/.../model/` (new files)
- **Dependencies:** PR 1.1

**PR 2.2 — Network DTOs in shared module**

- **Approach:** Create request/response models in `flyerboard/shared/src/commonMain/.../model/network/`:
  - `CreateFlyerNetworkRequest.kt` — `@Serializable @NetworkModel data class` with fields: `title: String`, `description: String`, `expiresAt: String?` (ISO-8601 nullable)
  - `UpdateFlyerNetworkRequest.kt` — same fields as create, all nullable for partial updates
  - `FlyerNetworkResponse.kt` — `@Serializable @NetworkModel data class` with: `id`, `title`, `description`, `fileUrl`, `status`, `expiresAt`, `uploaderId`, `createdAt`, `updatedAt`
  - `FlyerListNetworkResponse.kt` — wraps `List<FlyerNetworkResponse>` with pagination cursor/total
  - `ModerationActionNetworkRequest.kt` — `@Serializable @NetworkModel data class` with `action: String` (approve/reject)
  - `ErrorNetworkResponse.kt` — `@Serializable @NetworkModel data class` with `message: String`
  - Pagination models: `PaginationParams.kt` as `QueryParam` with `offset: Int`, `limit: Int`
- **Affected files:** `flyerboard/shared/src/commonMain/.../model/network/` (new files)
- **Reference:** `templatereplaceme/.../model/network/CreateUserNetworkRequest.kt` for annotation and serialization patterns. Use `@SerialName` for snake_case JSON mapping.
- **Dependencies:** PR 2.1

**PR 2.3 — API contract definitions**

- **Approach:** Define API objects in `flyerboard/api/src/commonMain/kotlin/com/cramsan/flyerboard/api/`:
  - `FlyerApi.kt` — `object FlyerApi : Api("api/v1/flyers")` with operations:
    - `listFlyers` — `GET`, query params for status filter + pagination, response `FlyerListNetworkResponse`
    - `getFlyer` — `GET` with `FlyerId` path param
    - `createFlyer` — `POST`, authenticated, multipart (see note below)
    - `updateFlyer` — `PUT` with `FlyerId` path param, authenticated
    - `listArchived` — `GET` on sub-path `archive`, paginated
    - `listMyFlyers` — `GET` on sub-path `mine`, authenticated, paginated
  - `ModerationApi.kt` — `object ModerationApi : Api("api/v1/moderation")` with operations:
    - `listPending` — `GET`, admin-only, paginated
    - `moderate` — `POST` with `FlyerId` path param, admin-only
  - `HealthApi.kt` — `object HealthApi : Api("api/v1/health")` with `check` GET operation
- **Note on file upload:** The framework `Operation` system is typed for JSON request bodies. File upload will need a custom route handler that accepts multipart form data alongside JSON metadata. The `createFlyer` and `updateFlyer` operations may need to be registered as custom routes rather than using the `Operation` type system. Document this as a deviation.
- **Affected files:** `flyerboard/api/src/commonMain/.../api/` (new files), `flyerboard/api/build.gradle.kts` (ensure dependency on `flyerboard:shared`)
- **Dependencies:** PR 2.2

---

## Unit 3: Database Schema

**PR 3.1 — Supabase migration: flyers table, indexes, and storage bucket**

- **Approach:** Create migration SQL files in `flyerboard/back-end/supabase/migrations/` following the edifikana naming convention (`YYYYMMDDHHMMSS_description.sql`):
  - **Migration 1 — `YYYYMMDDHHMMSS_initial_schema.sql`:**
    ```sql
    -- Users profile extension (Supabase Auth handles the auth.users table)
    CREATE TABLE public.user_profiles (
      id UUID PRIMARY KEY REFERENCES auth.users(id),
      role TEXT NOT NULL DEFAULT 'user' CHECK (role IN ('user', 'admin')),
      created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
      updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

    -- Flyers table
    CREATE TABLE public.flyers (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      title TEXT NOT NULL,
      description TEXT NOT NULL DEFAULT '',
      file_path TEXT NOT NULL,
      status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected', 'archived')),
      expires_at TIMESTAMPTZ,
      uploader_id UUID NOT NULL REFERENCES public.user_profiles(id),
      created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
      updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

    -- Indexes for large-city scale
    CREATE INDEX idx_flyers_status ON public.flyers(status);
    CREATE INDEX idx_flyers_expires_at ON public.flyers(expires_at) WHERE expires_at IS NOT NULL;
    CREATE INDEX idx_flyers_created_at ON public.flyers(created_at);
    CREATE INDEX idx_flyers_uploader_id ON public.flyers(uploader_id);
    CREATE INDEX idx_flyers_status_created_at ON public.flyers(status, created_at DESC);
    ```
  - **Migration 2 — `YYYYMMDDHHMMSS_rls_policies.sql`:**
    - Enable RLS on both tables
    - Public read for `flyers` where `status IN ('approved', 'archived')`
    - Authenticated insert on `flyers` where `uploader_id = auth.uid()`
    - Authenticated update on `flyers` where `uploader_id = auth.uid()`
    - Admin full access (check `user_profiles.role = 'admin'`)
    - User profiles: users can read their own profile
  - **Storage bucket:** Create `flyer-files` bucket via Supabase dashboard or CLI (document in README). Bucket should be private (no public access).
- **Affected files:** `flyerboard/back-end/supabase/migrations/` (new SQL files)
- **Dependencies:** None (can be done in parallel with PR 1.x)

**Open question resolved — Migration tooling:** Follow the edifikana pattern — commit raw SQL migration files to source control. Use `supabase db push` or apply via Supabase dashboard for deployment. No Supabase CLI required in CI.

**Open question resolved — Admin bootstrap:** The first admin is created by manually updating `user_profiles.role` to `'admin'` in the Supabase dashboard SQL editor after the user registers. Document this in the deployment guide.

---

## Unit 4: Backend Core — Datastores

**PR 4.1 — Supabase entity models and FlyerDatastore**

- **Approach:** Create Supabase entity models and the flyer datastore:
  - `flyerboard/back-end/src/main/kotlin/com/cramsan/flyerboard/server/datastore/entity/FlyerEntity.kt`:
    ```kotlin
    @Serializable @SupabaseModel
    data class FlyerEntity(
      @SerialName("id") val id: String,
      @SerialName("title") val title: String,
      @SerialName("description") val description: String,
      @SerialName("file_path") val filePath: String,
      @SerialName("status") val status: String,
      @SerialName("expires_at") val expiresAt: String?,
      @SerialName("uploader_id") val uploaderId: String,
      @SerialName("created_at") val createdAt: String,
      @SerialName("updated_at") val updatedAt: String,
    ) { companion object { const val COLLECTION = "flyers" } }
    ```
  - `flyerboard/back-end/src/main/kotlin/com/cramsan/flyerboard/server/datastore/entity/UserProfileEntity.kt` — same pattern for `user_profiles` table
  - `FlyerDatastore.kt` (interface) with methods: `createFlyer(...)`, `getFlyer(id)`, `listFlyers(status, offset, limit)`, `updateFlyer(id, ...)`, `listExpiredFlyers(now)`, `listFlyersByUploader(uploaderId, offset, limit)`
  - `SupabaseFlyerDatastore.kt` (implementation) using `Postgrest`:
    - Uses `postgrest.from(FlyerEntity.COLLECTION).select { ... }` pattern from edifikana
    - All list methods take `offset` and `limit` for pagination via `.range(offset.toLong(), (offset + limit - 1).toLong())`
    - Filter by status: `filter { FlyerEntity::status eq status }`
    - Order by `created_at DESC`
    - Expiry query: `filter { FlyerEntity::status eq "approved"; FlyerEntity::expiresAt lt now.toString() }`
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../datastore/` (new files)
- **Reference:** `edifikana/.../datastore/supabase/SupabaseDocumentDatastore.kt` for query patterns
- **Dependencies:** PR 1.2, PR 2.1

**PR 4.2 — FileDatastore (Supabase Storage)**

- **Approach:** Create file storage datastore:
  - `FileDatastore.kt` (interface): `uploadFile(fileName, content): Result<String>`, `getSignedUrl(filePath): Result<String>`, `deleteFile(filePath): Result<Unit>`
  - `SupabaseFileDatastore.kt` (implementation):
    - Uses `storage.from("flyer-files")` bucket
    - Upload: `bucket.upload(fileName, content) { upsert = false }`
    - Signed URL: `bucket.createSignedUrl(filePath, expiresIn = 1.hours)` (1-hour lifetime resolves the signed URL expiry question)
    - File name generation: UUID-based to avoid collisions (`"${UUID.randomUUID()}_${originalFileName}"`)
  - **MIME type validation:** Validate content type server-side before upload. Accepted types: `image/jpeg`, `image/png`, `image/webp`, `application/pdf`. Max size: 10 MB. This validation lives in the service layer, not the datastore.
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../datastore/` (new files)
- **Reference:** `edifikana/.../datastore/supabase/SupabaseStorageDatastore.kt`
- **Dependencies:** PR 1.2

**PR 4.3 — UserProfileDatastore**

- **Approach:**
  - `UserProfileDatastore.kt` (interface): `getUserProfile(userId): Result<UserProfile?>`, `createUserProfile(userId, role): Result<UserProfile>`, `updateUserRole(userId, role): Result<UserProfile>`
  - `SupabaseUserProfileDatastore.kt` using Postgrest against `user_profiles` table
  - Service-layer domain model: `UserProfile(id: UserId, role: UserRole, createdAt, updatedAt)`
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../datastore/` (new files)
- **Dependencies:** PR 1.2, PR 2.1

---

## Unit 5: Backend Core — Services & Controllers

**PR 5.1 — FlyerService (business logic)**

- **Approach:** Create `FlyerService.kt` in `flyerboard/back-end/src/main/kotlin/.../service/`:
  - `createFlyer(uploaderId, title, description, expiresAt, fileContent, fileName, mimeType): Result<Flyer>`:
    1. Validate MIME type and file size (max 10 MB)
    2. Sanitize title and description (strip HTML tags)
    3. Upload file via `FileDatastore`
    4. Create flyer record with `status = PENDING` via `FlyerDatastore`
  - `getFlyer(flyerId): Result<Flyer>` — fetch flyer, generate signed URL for file
  - `listFlyers(status, offset, limit): Result<PaginatedList<Flyer>>` — delegates to datastore, generates signed URLs
  - `updateFlyer(flyerId, requesterId, title, description, expiresAt, fileContent, fileName, mimeType): Result<Flyer>`:
    1. Verify requester is the uploader (ownership check)
    2. If new file provided, validate and upload, delete old file
    3. Update record, reset status to `PENDING`
  - `listFlyersByUploader(uploaderId, offset, limit): Result<PaginatedList<Flyer>>`
  - Domain model `Flyer` in `service/models/Flyer.kt` with all fields plus `fileUrl: String?`
  - Domain model `PaginatedList<T>` with `items: List<T>`, `total: Int`, `offset: Int`, `limit: Int`
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../service/` (new files)
- **Dependencies:** PR 4.1, PR 4.2

**PR 5.2 — ModerationService**

- **Approach:** Create `ModerationService.kt`:
  - `listPendingFlyers(offset, limit): Result<PaginatedList<Flyer>>` — delegates to `FlyerDatastore.listFlyers(status = PENDING, ...)`
  - `approveFlyer(flyerId, adminUserId): Result<Flyer>`:
    1. Verify admin role via `UserProfileDatastore`
    2. Update flyer status to `APPROVED`
  - `rejectFlyer(flyerId, adminUserId): Result<Flyer>`:
    1. Verify admin role
    2. Update flyer status to `REJECTED`
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../service/ModerationService.kt`
- **Dependencies:** PR 4.1, PR 4.3

**PR 5.3 — ExpiryService (scheduled coroutine job)**

- **Approach:** Create `ExpiryService.kt`:
  - Launched as a coroutine in the Ktor application lifecycle (`application.launch { ... }`)
  - Runs a `while(true)` loop with `delay(1.hours)`
  - Each tick: call `FlyerDatastore.listExpiredFlyers(Clock.System.now())` → for each, update status to `ARCHIVED`
  - Log each transition via `logI`
  - Start via DI/application module initialization
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../service/ExpiryService.kt`
- **Dependencies:** PR 4.1

**PR 5.4 — Authentication: SupabaseContextRetriever for flyerboard**

- **Approach:** Create `flyerboard/back-end/src/main/kotlin/.../controller/authentication/`:
  - `FlyerBoardContextPayload.kt` — `data class FlyerBoardContextPayload(val userId: UserId, val role: UserRole)`
  - `FlyerBoardContextRetriever.kt` — implements `ContextRetriever<FlyerBoardContextPayload>`:
    1. Extract token from `Authorization` header (or custom header matching edifikana's `HEADER_TOKEN_AUTH` pattern)
    2. Call `auth.retrieveUser(token)` to validate JWT
    3. Look up `UserProfileDatastore.getUserProfile(userId)` to get role
    4. Return `AuthenticatedClientContext` with payload containing userId and role
  - This allows controllers to access both the user identity and role from the context
- **Reference:** `edifikana/.../controller/authentication/SupabaseContextRetriever.kt`
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../controller/authentication/` (new files)
- **Dependencies:** PR 4.3, PR 1.2

**PR 5.5 — FlyerController (routes)**

- **Approach:** Create `FlyerController.kt`:
  - Implements `Controller` interface
  - `registerRoutes(route: Routing)`:
    - `GET /api/v1/flyers` — unauthenticated, delegates to `FlyerService.listFlyers`, wraps in JSON envelope `{ "data": ... }`
    - `GET /api/v1/flyers/{id}` — unauthenticated, single flyer detail
    - `POST /api/v1/flyers` — authenticated, multipart form data handler (custom route, not via `Operation.handle` due to file upload). Parses multipart parts: `file` (binary), `title`, `description`, `expiresAt`
    - `PUT /api/v1/flyers/{id}` — authenticated, multipart form data, ownership enforced
    - `GET /api/v1/flyers/archive` — unauthenticated, paginated archived flyers
    - `GET /api/v1/flyers/mine` — authenticated, user's own flyers
  - **JSON envelope:** All responses wrapped in `{ "data": <payload>, "error": null }` or `{ "data": null, "error": { "message": "..." } }`. Create a utility function `fun <T> envelope(data: T): Map<String, Any?>` or a dedicated `ApiResponse<T>` wrapper.
  - **Multipart handling:** Use Ktor's `call.receiveMultipart()` API for file upload endpoints. This is a deviation from the `Operation`-based routing used in templatereplaceme but necessary for binary file upload.
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../controller/FlyerController.kt`, utility classes
- **Dependencies:** PR 5.1, PR 5.4, PR 2.3

**PR 5.6 — ModerationController and HealthController**

- **Approach:**
  - `ModerationController.kt`:
    - `GET /api/v1/moderation/pending` — authenticated + admin role check from context payload
    - `POST /api/v1/moderation/{id}/approve` — admin-only
    - `POST /api/v1/moderation/{id}/reject` — admin-only
    - Admin check: `if (context.payload.role != UserRole.ADMIN) throw UnauthorizedException`
  - `HealthController.kt`:
    - `GET /api/v1/health` — unauthenticated, returns `{ "status": "ok" }`
    - Can use the standard `Operation`-based approach since no special handling needed
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../controller/` (new files)
- **Dependencies:** PR 5.2, PR 5.4, PR 2.3

**PR 5.7 — Backend DI wiring**

- **Approach:** Update DI modules in `flyerboard/back-end/src/main/kotlin/.../dependencyinjection/`:
  - `DatastoreModule.kt`: Wire Supabase client (copy pattern from edifikana's `DatastoreModule.kt`), bind all datastore interfaces to Supabase implementations. Add settings keys for `SUPABASE_URL` and `SUPABASE_KEY`.
  - `ServicesModule.kt`: Register `FlyerService`, `ModerationService`, `ExpiryService`
  - `ControllerModule.kt`: Register `FlyerController`, `ModerationController`, `HealthController`
  - `ApplicationModule.kt`: Register `FlyerBoardContextRetriever`, JSON config, settings
  - Create `FlyerBoardSettingKey.kt` with Supabase URL/key setting keys (pattern: `templatereplaceme/.../settings/TemplateReplaceMeSettingKey.kt`)
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../dependencyinjection/` (modify existing scaffolded files)
- **Dependencies:** PR 5.5, PR 5.6, PR 5.3

---

## Unit 6: Frontend — Auth & Core Services

**PR 6.1 — Frontend Supabase Auth integration**

- **Approach:** Set up Supabase client and auth in `flyerboard/front-end/shared-app/`:
  - Update `ServiceModule.kt` (or `ServicePlatformModule.kt`) to create Supabase client:
    ```kotlin
    single {
      createSupabaseClient(supabaseUrl, supabaseKey) {
        install(Auth) { sessionManager = SettingsSessionManager(key = "...") }
      }
    }
    ```
  - Create `AuthManager.kt` in `managers/`:
    - `signUp(email, password): Result<Unit>` — calls `auth.signUpWith(Email) { ... }`
    - `signIn(email, password): Result<Unit>` — calls `auth.signInWith(Email) { ... }`
    - `signOut(): Result<Unit>`
    - `isAuthenticated(): Boolean`
    - `getAccessToken(): String?` — for passing to backend API calls
    - `currentUserId(): UserId?`
    - Observe auth state changes via `auth.sessionStatus` Flow
  - **WASM concern (Risk #3):** supabase-kt's Auth module supports WASM target. Session persistence uses `SettingsSessionManager` which works cross-platform. Verify during implementation that `signInWith(Email)` works in browser context — if not, use JS interop to call Supabase JS SDK for auth only.
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../di/ServiceModule.kt`, `.../managers/AuthManager.kt`
- **Dependencies:** PR 1.1

**PR 6.2 — Frontend FlyerService (API client)**

- **Approach:** Create `FlyerService.kt` in `flyerboard/front-end/shared-app/src/commonMain/.../service/`:
  - Interface + `FlyerServiceImpl.kt`
  - Uses Ktor HTTP client to call backend endpoints
  - Methods mirror backend API: `listFlyers(status, offset, limit)`, `getFlyer(id)`, `createFlyer(...)`, `updateFlyer(...)`, `listArchived(...)`, `listMyFlyers(...)`, `listPending(...)`, `moderate(id, action)`
  - Attaches auth token via `AuthManager.getAccessToken()` in request headers
  - Parses JSON envelope responses
  - **File upload:** Uses Ktor client multipart form data: `submitFormWithBinaryData(...)` for create/update with file
  - Frontend model: `FlyerModel.kt` in `models/` — UI-friendly version of the network response
  - `NetworkMapper.kt` for converting between network DTOs and UI models
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../service/` (new files), `.../models/FlyerModel.kt`
- **Dependencies:** PR 6.1, PR 2.2

---

## Unit 7: Frontend Screens

**PR 7.1 — SignIn and SignUp screens**

- **Approach:** Create two features following the 5-file pattern in `features/auth/`:
  - `signin/` — `SignInScreen.kt`, `SignInViewModel.kt`, `SignInUIState.kt`, `SignInEvent.kt`, `SignInScreen.preview.kt`
    - UIState: `email: String`, `password: String`, `isLoading: Boolean`, `errorMessage: String?`
    - ViewModel: calls `AuthManager.signIn(email, password)`, emits `NavigateToHome` event on success
  - `signup/` — same 5-file pattern
    - UIState adds `confirmPassword: String`
    - ViewModel: calls `AuthManager.signUp(email, password)`, auto-signs-in on success
  - Register both ViewModels in `ViewModelModule.kt`
  - Add `SignInDestination` and `SignUpDestination` to navigation graph
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../features/auth/` (new), DI and nav modules
- **Dependencies:** PR 6.1

**PR 7.2 — FlyerList screen (public browsing)**

- **Approach:** Create `features/flyerlist/` with 5-file pattern:
  - UIState: `flyers: List<FlyerModel>`, `isLoading: Boolean`, `hasMore: Boolean`, `errorMessage: String?`
  - ViewModel: calls `FlyerService.listFlyers(status = APPROVED, offset, limit)`, supports infinite scroll pagination by incrementing offset
  - Screen: grid/list layout showing flyer thumbnail, title, expiry date. Tapping navigates to detail.
  - **Image display:** Use Coil3 (already in shared-app dependencies) for loading signed image URLs
  - **PDF thumbnail:** For PDF flyers, show a generic PDF icon/placeholder (resolves Risk #2 for list view — full PDF rendering deferred to detail screen)
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../features/flyerlist/` (new)
- **Dependencies:** PR 6.2

**PR 7.3 — FlyerDetail screen**

- **Approach:** Create `features/flyerdetail/` with 5-file pattern:
  - UIState: `flyer: FlyerModel?`, `isLoading: Boolean`, `isOwner: Boolean`
  - ViewModel: calls `FlyerService.getFlyer(id)`, checks ownership against `AuthManager.currentUserId()`
  - Screen: full-size image display via Coil3. For PDFs: embed in an `<iframe>` using Compose Web interop (`HtmlView` or `@Composable` wrapping a DOM element). If iframe approach fails (Risk #2), provide a "Download PDF" link to the signed URL.
  - Shows edit button if user is the owner
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../features/flyerdetail/` (new)
- **Dependencies:** PR 7.2

**PR 7.4 — FlyerUpload screen**

- **Approach:** Create `features/flyerupload/` with 5-file pattern:
  - UIState: `title: String`, `description: String`, `expiresAt: String?`, `selectedFileName: String?`, `selectedFileBytes: ByteArray?`, `isLoading: Boolean`, `errorMessage: String?`
  - ViewModel: validates form, calls `FlyerService.createFlyer(...)`, emits navigation event on success
  - **File picker (Risk #1):** Compose for Web doesn't have a native file picker. Use JS interop to create an `<input type="file" accept="image/*,.pdf">` element. Create a `FilePickerHelper` expect/actual in the WASM source set that triggers the file input and returns the selected file's name and bytes.
  - Shows success message indicating flyer is pending moderation
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../features/flyerupload/` (new), WASM-specific interop file
- **Dependencies:** PR 6.2, PR 7.1

**PR 7.5 — FlyerEdit screen**

- **Approach:** Create `features/flyeredit/` with 5-file pattern:
  - Similar to upload but pre-populates fields from existing flyer
  - UIState adds: `originalFlyer: FlyerModel?`, `hasChanges: Boolean`
  - ViewModel: loads existing flyer, calls `FlyerService.updateFlyer(...)`, shows message that edited flyer returns to pending
  - Reuses the same file picker interop from PR 7.4
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../features/flyeredit/` (new)
- **Dependencies:** PR 7.4, PR 7.3

**PR 7.6 — MyFlyers screen**

- **Approach:** Create `features/myflyers/` with 5-file pattern:
  - UIState: `flyers: List<FlyerModel>`, `isLoading: Boolean`, `hasMore: Boolean`
  - ViewModel: calls `FlyerService.listMyFlyers(offset, limit)`, paginated
  - Screen: shows status badge (pending/approved/rejected/archived) on each flyer, tap to view detail, edit button on non-archived flyers
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../features/myflyers/` (new)
- **Dependencies:** PR 6.2, PR 7.1

**PR 7.7 — Archive screen**

- **Approach:** Create `features/archive/` with 5-file pattern:
  - Nearly identical to FlyerList but filters by `status = ARCHIVED`
  - Can share UI components with FlyerList (extract a `FlyerGrid` composable into `shared-ui` if the duplication is significant)
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../features/archive/` (new)
- **Dependencies:** PR 7.2

**PR 7.8 — ModerationQueue screen (admin)**

- **Approach:** Create `features/moderation/` with 5-file pattern:
  - UIState: `pendingFlyers: List<FlyerModel>`, `isLoading: Boolean`, `hasMore: Boolean`
  - ViewModel: calls `FlyerService.listPending(offset, limit)`, `FlyerService.moderate(id, action)`
  - Screen: list of pending flyers with "Approve" and "Reject" buttons inline. Tapping flyer shows detail.
  - Only accessible if user has admin role (check in `AuthManager` or via backend response)
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../features/moderation/` (new)
- **Dependencies:** PR 6.2, PR 7.1

**PR 7.9 — Navigation graph and app shell**

- **Approach:** Wire all screens into the navigation graph:
  - Update `FlyerBoardWindowScreen.kt` (renamed from template) with navigation destinations
  - Define `FlyerBoardWindowNavGraphDestination.kt` with all destinations
  - Add bottom navigation or side menu: Browse, Archive, My Flyers, Upload, Moderation (admin-only)
  - Handle auth state: redirect to SignIn if accessing authenticated screens while logged out
  - App bar with sign-in/sign-out button
- **Affected files:** `flyerboard/front-end/shared-app/src/commonMain/.../features/window/` and `features/application/`
- **Dependencies:** PR 7.1 through PR 7.8

---

## Unit 8: Security Hardening

**PR 8.1 — Input sanitization and validation**

- **Approach:**
  - Create `InputSanitizer.kt` utility in backend service layer:
    - `sanitizeText(input: String): String` — strips HTML tags, trims whitespace, enforces max length (title: 200 chars, description: 2000 chars)
    - Used by `FlyerService.createFlyer` and `updateFlyer`
  - File validation in `FlyerService`:
    - Check MIME type against allowlist: `image/jpeg`, `image/png`, `image/webp`, `application/pdf`
    - Check file size ≤ 10 MB
    - Reject with descriptive error if validation fails
  - Ensure `FlyerController` never exposes `file_path` directly — only signed URLs
  - Verify all IDs in responses are UUIDs (opaque), no internal storage paths leak
- **Affected files:** `flyerboard/back-end/src/main/kotlin/.../service/FlyerService.kt` (modify), new `InputSanitizer.kt`
- **Dependencies:** PR 5.1

**Open question resolved — Image/PDF formats (Risk #7):** Accepted formats are JPEG, PNG, WebP for images and PDF for documents. No PDF page count limit — the 10 MB size limit is the constraint.

---

## Unit 9: Testing

**PR 9.1 — Backend service unit tests**

- **Approach:** Create test classes in `flyerboard/back-end/src/test/`:
  - `FlyerServiceTest.kt` — mock `FlyerDatastore` and `FileDatastore`, test:
    - Create flyer sets status to PENDING
    - Update flyer resets status to PENDING
    - Update flyer by non-owner fails
    - MIME type validation rejects invalid types
    - File size validation rejects oversized files
    - Input sanitization strips HTML
  - `ModerationServiceTest.kt` — mock datastores, test:
    - Approve changes status to APPROVED
    - Reject changes status to REJECTED
    - Non-admin cannot moderate
  - `ExpiryServiceTest.kt` — test that expired flyers get transitioned to ARCHIVED
  - `InputSanitizerTest.kt` — test HTML stripping, length truncation
- **Affected files:** `flyerboard/back-end/src/test/` (new test files)
- **Dependencies:** PR 5.1, PR 5.2, PR 5.3, PR 8.1

**PR 9.2 — Backend integration tests**

- **Approach:** Create integration tests in `flyerboard/back-end/src/integTest/`:
  - Test full request/response cycle using Ktor test engine
  - `FlyerApiIntegrationTest.kt`:
    - Create flyer → verify pending → approve → verify listed publicly
    - Upload with invalid MIME type → verify 400 error
    - Edit by non-owner → verify 403
    - Pagination returns correct page sizes
  - `ModerationApiIntegrationTest.kt`:
    - Approve/reject with admin token
    - Reject non-admin access
  - Uses test Supabase instance or mocked datastores depending on CI setup
- **Affected files:** `flyerboard/back-end/src/integTest/` (new test files)
- **Dependencies:** PR 5.7

**PR 9.3 — Frontend ViewModel tests**

- **Approach:** Create tests in `flyerboard/front-end/shared-app/src/jvmTest/`:
  - `FlyerListViewModelTest.kt` — mock `FlyerService`, verify UIState updates on load, pagination
  - `FlyerUploadViewModelTest.kt` — verify form validation, loading state, success/error events
  - `SignInViewModelTest.kt` — verify auth flow, error handling
  - `ModerationQueueViewModelTest.kt` — verify approve/reject actions update list
  - Use `BaseViewModelTest` from `framework:core-compose` as base class
- **Affected files:** `flyerboard/front-end/shared-app/src/jvmTest/` (new test files)
- **Dependencies:** PR 7.x screens

---

## Unit 10: Deployment Setup

**PR 10.1 — Nginx config, systemd service, and documentation**

- **Approach:**
  - Create `flyerboard/deploy/` directory with:
    - `nginx.conf` — serves WASM static files from `/var/www/flyerboard/`, proxies `/api/` to `http://localhost:8080`
    - `flyerboard.service` — systemd unit file running the fat JAR with environment variable references
    - `env.example` — documents required env vars: `SUPABASE_URL`, `SUPABASE_KEY`, `PORT`
  - Backend `application.conf` (Ktor): configure port from env var, set up logging
  - Ensure `HealthController` is registered and responds at `/api/v1/health`
  - Frontend build: `./gradlew :flyerboard:front-end:app-wasm:wasmJsBrowserDistribution` produces static files
- **Affected files:** `flyerboard/deploy/` (new), `flyerboard/back-end/src/main/resources/application.conf`
- **Dependencies:** PR 5.7, PR 7.9

---

## Critical Path

The longest dependency chain determines the minimum timeline:

```
PR 1.1 (scaffold)
  → PR 1.2 (supabase deps)
    → PR 4.1 (FlyerDatastore)
      → PR 5.1 (FlyerService)
        → PR 5.5 (FlyerController)
          → PR 5.7 (DI wiring)
            → PR 9.2 (integration tests)
              → PR 10.1 (deployment)
```

**Parallel tracks** (can proceed alongside the critical path):
- **Track A (Database):** PR 3.1 can start immediately (no code dependency)
- **Track B (Frontend):** PR 6.1 → PR 6.2 → PR 7.x screens can start after PR 1.1, parallel to backend work
- **Track C (Shared models):** PR 2.1 → PR 2.2 → PR 2.3 should be completed early as both backend and frontend depend on them

---

## Resolved Open Questions Summary

| Question | Resolution |
|----------|-----------|
| Risk #1 — WASM file upload | JS interop for `<input type="file">` in PR 7.4. Create expect/actual `FilePickerHelper`. |
| Risk #2 — PDF rendering | Use `<iframe>` embed via Compose Web interop for detail view. Fallback: download link. List view shows PDF icon placeholder. |
| Risk #3 — Supabase Auth in WASM | Use `SettingsSessionManager` (KMP-compatible). Verify email/password flow works; fallback to JS SDK interop if needed. |
| Risk #4 — Signed URL vs. caching | 1-hour URL lifetime. No refresh mechanism on frontend — reload fetches new URLs. Acceptable for this scale. |
| Risk #5 — Migration tooling | Commit raw SQL files, apply via `supabase db push` or dashboard. No CLI in CI. |
| Risk #6 — Admin bootstrap | Manual SQL update in Supabase dashboard after first admin registers. Documented in deploy guide. |
| Risk #7 — File format constraints | JPEG, PNG, WebP, PDF accepted. 10 MB max. No PDF page count limit. |
| Risk #8 — Search | Not in scope per goals. If added later, use Postgres `ILIKE` on title/description with a GIN trigram index. |