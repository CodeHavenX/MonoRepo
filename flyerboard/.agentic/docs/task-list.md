Now I'll produce the full structured task list based on the approved plan and codebase patterns.

# Task List

## Task: TASK-001
**Title:** Clone templatereplaceme and rename to flyerboard
**Description:** Copy the entire `templatereplaceme/` directory to `flyerboard/` and rename all occurrences of `templatereplaceme`/`TemplateReplaceMe` to `flyerboard`/`FlyerBoard` across package names, class names, Gradle module names, namespaces, build config, WASM module name, Ktor `application.conf`, and DI module names. Register the new modules in `settings.gradle.kts` and the root `build.gradle.kts` `releaseAll` task. Exclude `app-android/` and `app-jvm/` entry points (out of scope for this phase).
**Dependencies:** none
**Implementation Plan:**
1. Copy `templatereplaceme/` to `flyerboard/`.
2. Rename all package declarations from `com.cramsan.templatereplaceme` to `com.cramsan.flyerboard` across all Kotlin files.
3. Rename all class names containing `TemplateReplaceMe` to `FlyerBoard` (e.g., `TemplateReplaceMeBackEnd` → `FlyerBoardBackEnd`, `TemplateReplaceMeWasmApp` → `FlyerBoardWasmApp`).
4. Update Gradle module names and namespaces in all `build.gradle.kts` files.
5. Update `application.conf` main class reference.
6. Register modules in `settings.gradle.kts`: `flyerboard:shared`, `flyerboard:api`, `flyerboard:back-end`, `flyerboard:front-end:shared-ui`, `flyerboard:front-end:shared-app`, `flyerboard:front-end:app-wasm`.
7. Add `flyerboard` modules to `releaseAll` in root `build.gradle.kts`.
8. Remove `app-android/` and `app-jvm/` directories from `flyerboard/front-end/`.
9. Rename directory structures to match the new package name.
**Testing Plan:**
- Run `./gradlew :flyerboard:back-end:release` and verify it passes.
- Run `./gradlew :flyerboard:front-end:app-wasm:release` and verify it passes.
- Run `./gradlew :flyerboard:shared:release` and verify it passes.
**Acceptance Criteria:**
- All `flyerboard` modules compile without errors.
- No references to `templatereplaceme` remain in `flyerboard/` source files.
- Modules are registered in `settings.gradle.kts`.
- `releaseAll` includes the new modules.
**Sample Code:**
```kotlin
// settings.gradle.kts additions
include(":flyerboard:shared")
include(":flyerboard:api")
include(":flyerboard:back-end")
include(":flyerboard:front-end:shared-ui")
include(":flyerboard:front-end:shared-app")
include(":flyerboard:front-end:app-wasm")
```
**References:**
- Low-Level Plan: PR 1.1
- `templatereplaceme/` directory structure
- `settings.gradle.kts` for module registration pattern
- `.ai/instructions.md` — "New Modules" section

---

## Task: TASK-002
**Title:** Add supabase-kt dependencies to flyerboard back-end
**Description:** Add Supabase Kotlin client dependencies (`postgrest-kt`, `storage-kt`, `auth-kt`) and the Ktor CIO client engine to the flyerboard back-end module. Reference `edifikana/back-end/build.gradle.kts` for exact dependency coordinates and `versions.properties` for version management.
**Dependencies:** TASK-001
**Implementation Plan:**
1. Check `versions.properties` for existing supabase-kt version entries (used by edifikana).
2. Add `io.github.jan-tennert.supabase:postgrest-kt`, `storage-kt`, `auth-kt` dependencies to `flyerboard/back-end/build.gradle.kts`.
3. Add `io.ktor:ktor-client-cio` dependency for the supabase-kt HTTP engine.
4. Verify version placeholders (`_`) resolve correctly from `versions.properties`.
**Testing Plan:**
- Run `./gradlew :flyerboard:back-end:release` and verify compilation succeeds.
**Acceptance Criteria:**
- `flyerboard/back-end/build.gradle.kts` includes all three supabase-kt modules and the Ktor CIO engine.
- The module compiles without errors.
**Sample Code:**
```kotlin
// flyerboard/back-end/build.gradle.kts
dependencies {
    implementation("io.github.jan-tennert.supabase:postgrest-kt:_")
    implementation("io.github.jan-tennert.supabase:storage-kt:_")
    implementation("io.github.jan-tennert.supabase:auth-kt:_")
    implementation("io.ktor:ktor-client-cio:_")
}
```
**References:**
- Low-Level Plan: PR 1.2
- `edifikana/back-end/build.gradle.kts` for dependency coordinates
- `versions.properties` for version management

---

## Task: TASK-003
**Title:** Create domain models in flyerboard shared module
**Description:** Define core domain value types and enums used by both frontend and backend: `FlyerId`, `UserId`, `FlyerStatus`, and `UserRole`. These live in the shared KMP module so both sides share the same type definitions.
**Dependencies:** TASK-001
**Implementation Plan:**
1. Create `flyerboard/shared/src/commonMain/kotlin/com/cramsan/flyerboard/lib/model/FlyerId.kt` — inline value class wrapping `String`, implementing `PathParam`.
2. Create `UserId.kt` — same pattern as `FlyerId`.
3. Create `FlyerStatus.kt` — `@Serializable enum class` with values `PENDING`, `APPROVED`, `REJECTED`, `ARCHIVED`.
4. Create `UserRole.kt` — `@Serializable enum class` with values `USER`, `ADMIN`.
**Testing Plan:**
- Run `./gradlew :flyerboard:shared:release` to verify compilation.
- Serialization of enums is implicitly tested when used in network DTOs (TASK-004).
**Acceptance Criteria:**
- All four model files exist and compile.
- `FlyerId` and `UserId` follow the `PathParam` pattern from `templatereplaceme/.../model/UserId.kt`.
- Enums are `@Serializable`.
**Sample Code:**
```kotlin
// FlyerId.kt
@JvmInline
value class FlyerId(val flyerId: String) : PathParam

// FlyerStatus.kt
@Serializable
enum class FlyerStatus {
    PENDING, APPROVED, REJECTED, ARCHIVED
}
```
**References:**
- Low-Level Plan: PR 2.1
- `templatereplaceme/shared/src/commonMain/.../model/UserId.kt` for pattern

---

## Task: TASK-004
**Title:** Create network DTOs in flyerboard shared module
**Description:** Define serializable request/response data transfer objects for the REST API in the shared module. These DTOs define the JSON contract between frontend and backend.
**Dependencies:** TASK-003
**Implementation Plan:**
1. Create `flyerboard/shared/src/commonMain/kotlin/com/cramsan/flyerboard/lib/model/network/CreateFlyerNetworkRequest.kt` with fields: `title: String`, `description: String`, `expiresAt: String?`.
2. Create `UpdateFlyerNetworkRequest.kt` with all-nullable fields for partial updates.
3. Create `FlyerNetworkResponse.kt` with: `id`, `title`, `description`, `fileUrl`, `status`, `expiresAt`, `uploaderId`, `createdAt`, `updatedAt`.
4. Create `FlyerListNetworkResponse.kt` wrapping `List<FlyerNetworkResponse>` with pagination fields (`total`, `offset`, `limit`).
5. Create `ModerationActionNetworkRequest.kt` with `action: String`.
6. Create `ErrorNetworkResponse.kt` with `message: String`.
7. Create `PaginationParams.kt` as `QueryParam` with `offset: Int`, `limit: Int`.
8. Use `@SerialName` for snake_case JSON field mapping on all DTOs.
**Testing Plan:**
- Run `./gradlew :flyerboard:shared:release` to verify compilation.
- Serialization correctness is verified in backend integration tests (TASK-024).
**Acceptance Criteria:**
- All DTO files exist and compile.
- All classes are annotated with `@Serializable` and `@NetworkModel`.
- JSON field names use snake_case via `@SerialName`.
**Sample Code:**
```kotlin
@Serializable
@NetworkModel
data class FlyerNetworkResponse(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("file_url") val fileUrl: String?,
    @SerialName("status") val status: String,
    @SerialName("expires_at") val expiresAt: String?,
    @SerialName("uploader_id") val uploaderId: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)
```
**References:**
- Low-Level Plan: PR 2.2
- `templatereplaceme/shared/.../model/network/CreateUserNetworkRequest.kt` for annotation pattern

---

## Task: TASK-005
**Title:** Define API contract objects in flyerboard api module
**Description:** Define API operation objects that declare the REST endpoint structure for flyers, moderation, and health. These contracts are used by both frontend (to make requests) and backend (to register routes).
**Dependencies:** TASK-004
**Implementation Plan:**
1. Create `flyerboard/api/src/commonMain/kotlin/com/cramsan/flyerboard/api/FlyerApi.kt`:
   - `object FlyerApi : Api("api/v1/flyers")` with operations: `listFlyers` (GET, paginated, status filter), `getFlyer` (GET, path param), `createFlyer` (POST, authenticated, multipart — documented as custom route deviation), `updateFlyer` (PUT, authenticated, multipart), `listArchived` (GET, sub-path `archive`), `listMyFlyers` (GET, sub-path `mine`, authenticated).
2. Create `ModerationApi.kt` — `object ModerationApi : Api("api/v1/moderation")` with: `listPending` (GET, admin-only), `moderate` (POST with FlyerId, admin-only).
3. Create `HealthApi.kt` — `object HealthApi : Api("api/v1/health")` with `check` GET operation.
4. Document the multipart deviation for `createFlyer` and `updateFlyer` in code comments.
5. Ensure `flyerboard/api/build.gradle.kts` depends on `flyerboard:shared`.
**Testing Plan:**
- Run `./gradlew :flyerboard:api:release` to verify compilation.
**Acceptance Criteria:**
- All three API objects exist and compile.
- Endpoint paths follow RESTful conventions with `/api/v1/` prefix.
- The `api` module depends on `shared`.
**Sample Code:**
```kotlin
object FlyerApi : Api("api/v1/flyers") {
    val listFlyers = Operation<PaginationParams, Unit, FlyerListNetworkResponse>(Method.GET)
    val getFlyer = Operation<Unit, Unit, FlyerNetworkResponse>(Method.GET, FlyerId::class)
    // createFlyer and updateFlyer use custom multipart routes
}
```
**References:**
- Low-Level Plan: PR 2.3
- `templatereplaceme/api/src/commonMain/.../api/UserApi.kt` for pattern

---

## Task: TASK-006
**Title:** Create Supabase migration SQL files
**Description:** Write versioned SQL migration files for the initial database schema (user_profiles and flyers tables, indexes) and Row Level Security policies. These are committed to source control and applied manually via Supabase.
**Dependencies:** none
**Implementation Plan:**
1. Create `flyerboard/back-end/supabase/migrations/` directory.
2. Create `YYYYMMDDHHMMSS_initial_schema.sql` with:
   - `user_profiles` table (id UUID PK references auth.users, role TEXT with CHECK, created_at, updated_at).
   - `flyers` table (id UUID PK with gen_random_uuid(), title, description, file_path, status with CHECK, expires_at, uploader_id FK, created_at, updated_at).
   - Indexes: `idx_flyers_status`, `idx_flyers_expires_at` (partial), `idx_flyers_created_at`, `idx_flyers_uploader_id`, `idx_flyers_status_created_at` (composite).
3. Create `YYYYMMDDHHMMSS_rls_policies.sql` with:
   - Enable RLS on both tables.
   - Public read for flyers where status IN ('approved', 'archived').
   - Authenticated insert/update on flyers where uploader_id = auth.uid().
   - Admin full access via user_profiles role check.
   - User profiles: users can read their own profile.
4. Add a README note about creating the `flyer-files` storage bucket (private) via Supabase dashboard.
**Testing Plan:**
- Validate SQL syntax by reviewing against Supabase migration conventions.
- Verify migrations apply cleanly on a fresh Supabase project (manual step, documented).
**Acceptance Criteria:**
- Two SQL migration files exist in `flyerboard/back-end/supabase/migrations/`.
- Tables have all required columns, constraints, and indexes.
- RLS policies cover public read, authenticated write (ownership), and admin access.
- Storage bucket creation is documented.
**Sample Code:**
```sql
CREATE TABLE public.flyers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title TEXT NOT NULL,
  description TEXT NOT NULL DEFAULT '',
  file_path TEXT NOT NULL,
  status TEXT NOT NULL DEFAULT 'pending'
    CHECK (status IN ('pending', 'approved', 'rejected', 'archived')),
  expires_at TIMESTAMPTZ,
  uploader_id UUID NOT NULL REFERENCES public.user_profiles(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
```
**References:**
- Low-Level Plan: PR 3.1
- `edifikana/back-end/supabase/migrations/` for naming convention
- Architecture Design: "Database" section

---

## Task: TASK-007
**Title:** Implement Supabase entity models and FlyerDatastore
**Description:** Create the Supabase entity models (data classes mapping to database rows) and the FlyerDatastore interface + Supabase implementation for CRUD operations on the flyers table. This is the data access layer for flyer records.
**Dependencies:** TASK-002, TASK-003
**Implementation Plan:**
1. Create `flyerboard/back-end/src/main/kotlin/com/cramsan/flyerboard/server/datastore/entity/FlyerEntity.kt` — `@Serializable` data class with `@SerialName` for snake_case DB columns, companion with `COLLECTION = "flyers"`.
2. Create `UserProfileEntity.kt` for the `user_profiles` table (same pattern).
3. Create `FlyerDatastore.kt` interface with methods: `createFlyer(...)`, `getFlyer(id)`, `listFlyers(status, offset, limit)`, `updateFlyer(id, ...)`, `listExpiredFlyers(now)`, `listFlyersByUploader(uploaderId, offset, limit)`.
4. Create `SupabaseFlyerDatastore.kt` implementation using `Postgrest`:
   - Use `postgrest.from(FlyerEntity.COLLECTION).select { ... }` pattern.
   - All list methods use `.range(offset.toLong(), (offset + limit - 1).toLong())` for pagination.
   - Filter by status, order by `created_at DESC`.
   - Expiry query filters `status eq "approved"` AND `expiresAt lt now`.
**Testing Plan:**
- Write `SupabaseFlyerDatastoreTest.kt` with mocked Postgrest client verifying:
  - `createFlyer` inserts a record and returns the entity.
  - `listFlyers` applies correct status filter and pagination range.
  - `listExpiredFlyers` applies correct status + date filter.
  - `updateFlyer` updates the correct record by ID.
**Acceptance Criteria:**
- Entity classes map all database columns.
- Datastore interface defines all required CRUD methods.
- Supabase implementation correctly uses Postgrest API with filters and pagination.
- Module compiles.
**Sample Code:**
```kotlin
@Serializable
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
) {
    companion object { const val COLLECTION = "flyers" }
}
```
**References:**
- Low-Level Plan: PR 4.1
- `edifikana/.../datastore/supabase/SupabaseDocumentDatastore.kt` for query patterns

---

## Task: TASK-008
**Title:** Implement FileDatastore for Supabase Storage
**Description:** Create the file storage datastore interface and Supabase Storage implementation for uploading flyer files, generating signed URLs, and deleting files. Files are stored in a private `flyer-files` bucket.
**Dependencies:** TASK-002
**Implementation Plan:**
1. Create `FileDatastore.kt` interface with methods: `uploadFile(fileName, content): Result<String>`, `getSignedUrl(filePath): Result<String>`, `deleteFile(filePath): Result<Unit>`.
2. Create `SupabaseFileDatastore.kt` implementation:
   - Uses `storage.from("flyer-files")` bucket.
   - Upload: `bucket.upload(fileName, content) { upsert = false }`.
   - Signed URL: `bucket.createSignedUrl(filePath, expiresIn = 1.hours)`.
   - File name generation: `"${UUID.randomUUID()}_${originalFileName}"` to avoid collisions.
   - Delete: `bucket.delete(filePath)`.
**Testing Plan:**
- Write `SupabaseFileDatastoreTest.kt` with mocked Storage client verifying:
  - `uploadFile` calls bucket upload with correct parameters.
  - `getSignedUrl` generates a URL with 1-hour expiry.
  - `deleteFile` calls bucket delete.
**Acceptance Criteria:**
- Interface and implementation exist and compile.
- File names are UUID-prefixed to prevent collisions.
- Signed URLs have 1-hour lifetime.
**Sample Code:**
```kotlin
class SupabaseFileDatastore(
    private val storage: Storage,
) : FileDatastore {
    override suspend fun uploadFile(fileName: String, content: ByteArray): Result<String> {
        val path = "${UUID.randomUUID()}_$fileName"
        storage.from("flyer-files").upload(path, content) { upsert = false }
        return Result.success(path)
    }
}
```
**References:**
- Low-Level Plan: PR 4.2
- `edifikana/.../datastore/supabase/SupabaseStorageDatastore.kt`

---

## Task: TASK-009
**Title:** Implement UserProfileDatastore
**Description:** Create the user profile datastore interface and Supabase implementation for reading and managing user profiles (role lookups, profile creation). This is used for authentication context and admin role verification.
**Dependencies:** TASK-002, TASK-003
**Implementation Plan:**
1. Create domain model `UserProfile.kt` in `service/models/` with fields: `id: UserId`, `role: UserRole`, `createdAt: String`, `updatedAt: String`.
2. Create `UserProfileDatastore.kt` interface with: `getUserProfile(userId): Result<UserProfile?>`, `createUserProfile(userId, role): Result<UserProfile>`, `updateUserRole(userId, role): Result<UserProfile>`.
3. Create `SupabaseUserProfileDatastore.kt` using Postgrest against `user_profiles` table.
**Testing Plan:**
- Write `SupabaseUserProfileDatastoreTest.kt` verifying:
  - `getUserProfile` returns profile or null.
  - `createUserProfile` inserts with correct role.
  - `updateUserRole` updates the role field.
**Acceptance Criteria:**
- Interface and implementation exist and compile.
- Datastore correctly maps between `UserProfileEntity` and `UserProfile` domain model.
**Sample Code:**
N/A — follows same pattern as TASK-007.
**References:**
- Low-Level Plan: PR 4.3

---

## Task: TASK-010
**Title:** Implement FlyerService business logic
**Description:** Create the core business logic service for flyer operations: create, read, update, list with pagination, and ownership enforcement. This service validates inputs, orchestrates datastore calls, and enforces business rules like status transitions.
**Dependencies:** TASK-007, TASK-008
**Implementation Plan:**
1. Create domain model `Flyer.kt` in `service/models/` with all fields plus `fileUrl: String?`.
2. Create `PaginatedList<T>` in `service/models/` with `items: List<T>`, `total: Int`, `offset: Int`, `limit: Int`.
3. Create `FlyerService.kt` with methods:
   - `createFlyer(uploaderId, title, description, expiresAt, fileContent, fileName, mimeType)`: validate MIME type and file size, sanitize text, upload file, create record with `PENDING` status.
   - `getFlyer(flyerId)`: fetch flyer, generate signed URL.
   - `listFlyers(status, offset, limit)`: delegate to datastore, generate signed URLs for each.
   - `updateFlyer(flyerId, requesterId, ...)`: verify ownership, handle optional new file, reset to `PENDING`.
   - `listFlyersByUploader(uploaderId, offset, limit)`.
4. MIME validation: accept `image/jpeg`, `image/png`, `image/webp`, `application/pdf`.
5. File size validation: reject files > configurable max (default 10 MB).
6. Text sanitization: strip HTML tags, trim whitespace, enforce max lengths (title: 200, description: 2000).
**Testing Plan:**
- Write `FlyerServiceTest.kt` with mocked datastores verifying:
  - Create sets status to PENDING.
  - Update resets status to PENDING.
  - Update by non-owner fails.
  - Invalid MIME type rejected.
  - Oversized file rejected.
  - HTML stripped from title/description.
**Acceptance Criteria:**
- All service methods implemented with proper validation and error handling.
- Ownership check enforced on updates.
- Status correctly set to PENDING on create and update.
- Signed URLs generated for file access.
**Sample Code:**
```kotlin
class FlyerService(
    private val flyerDatastore: FlyerDatastore,
    private val fileDatastore: FileDatastore,
) {
    suspend fun createFlyer(
        uploaderId: UserId,
        title: String,
        description: String,
        expiresAt: String?,
        fileContent: ByteArray,
        fileName: String,
        mimeType: String,
    ): Result<Flyer> {
        validateMimeType(mimeType).getOrElse { return Result.failure(it) }
        validateFileSize(fileContent.size).getOrElse { return Result.failure(it) }
        val sanitizedTitle = sanitizeText(title, maxLength = 200)
        val sanitizedDescription = sanitizeText(description, maxLength = 2000)
        val filePath = fileDatastore.uploadFile(fileName, fileContent).getOrElse { return Result.failure(it) }
        // create record with PENDING status...
    }
}
```
**References:**
- Low-Level Plan: PR 5.1
- Architecture Design: "Data Flow — Upload" section
- Standards: "Security" section

---

## Task: TASK-011
**Title:** Implement ModerationService
**Description:** Create the moderation service for admin approval/rejection of flyers. Enforces admin role verification before allowing status transitions.
**Dependencies:** TASK-007, TASK-009
**Implementation Plan:**
1. Create `ModerationService.kt` with:
   - `listPendingFlyers(offset, limit)`: delegates to `FlyerDatastore.listFlyers(status = PENDING, ...)`.
   - `approveFlyer(flyerId, adminUserId)`: verify admin role via `UserProfileDatastore`, update status to `APPROVED`.
   - `rejectFlyer(flyerId, adminUserId)`: verify admin role, update status to `REJECTED`.
2. Role verification: fetch user profile, check `role == ADMIN`, throw unauthorized exception if not.
**Testing Plan:**
- Write `ModerationServiceTest.kt` verifying:
  - Approve changes status to APPROVED.
  - Reject changes status to REJECTED.
  - Non-admin user cannot moderate (throws exception).
  - List pending returns correct filtered results.
**Acceptance Criteria:**
- Admin role verified before any moderation action.
- Status transitions are correct (PENDING → APPROVED or REJECTED).
- Non-admin requests are rejected with appropriate error.
**Sample Code:**
N/A
**References:**
- Low-Level Plan: PR 5.2
- Architecture Design: "Data Flow — Moderation" section

---

## Task: TASK-012
**Title:** Implement ExpiryService coroutine scheduler
**Description:** Create an in-process coroutine-based scheduler that runs hourly to transition expired flyers from APPROVED to ARCHIVED status. This replaces the need for an external cron job.
**Dependencies:** TASK-007
**Implementation Plan:**
1. Create `ExpiryService.kt`:
   - Accept `FlyerDatastore` as a dependency.
   - Provide a `start(scope: CoroutineScope)` method that launches a coroutine.
   - The coroutine runs `while(true)` with `delay(1.hours)`.
   - Each tick: call `FlyerDatastore.listExpiredFlyers(Clock.System.now())`, update each to `ARCHIVED`.
   - Log each transition.
2. Wire into Ktor application lifecycle so it starts on server boot.
**Testing Plan:**
- Write `ExpiryServiceTest.kt` verifying:
  - Expired flyers are transitioned to ARCHIVED.
  - Non-expired flyers are not affected.
  - Use `TestCoroutineScheduler` to advance time without real delays.
**Acceptance Criteria:**
- Service starts as a coroutine within the Ktor application.
- Correctly identifies and archives expired flyers.
- Runs on an hourly interval.
**Sample Code:**
```kotlin
class ExpiryService(private val flyerDatastore: FlyerDatastore) {
    fun start(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                delay(1.hours)
                val expired = flyerDatastore.listExpiredFlyers(Clock.System.now())
                expired.forEach { flyer ->
                    flyerDatastore.updateFlyer(flyer.id, status = "archived")
                }
            }
        }
    }
}
```
**References:**
- Low-Level Plan: PR 5.3
- Architecture Design: "Data Flow — Expiry" section

---

## Task: TASK-013
**Title:** Implement FlyerBoard authentication context retriever
**Description:** Create the Supabase JWT authentication context retriever that extracts user identity and role from request headers. This enables controllers to access the authenticated user's ID and admin status.
**Dependencies:** TASK-009, TASK-002
**Implementation Plan:**
1. Create `FlyerBoardContextPayload.kt` — data class with `userId: UserId`, `role: UserRole`.
2. Create `FlyerBoardContextRetriever.kt` implementing `ContextRetriever<FlyerBoardContextPayload>`:
   - Extract token from `Authorization` header.
   - Call `auth.retrieveUser(token)` to validate the Supabase JWT.
   - Look up `UserProfileDatastore.getUserProfile(userId)` to get role.
   - Return `AuthenticatedClientContext` with the payload.
   - If no profile exists, auto-create one with `USER` role.
**Testing Plan:**
- Write unit tests verifying:
  - Valid token returns correct userId and role.
  - Invalid/missing token returns unauthenticated context.
  - Missing profile auto-creates with USER role.
**Acceptance Criteria:**
- Context retriever correctly validates Supabase JWTs.
- User role is resolved from the database, not the token.
- Unauthenticated requests are handled gracefully.
**Sample Code:**
```kotlin
class FlyerBoardContextRetriever(
    private val auth: Auth,
    private val userProfileDatastore: UserProfileDatastore,
) : ContextRetriever<FlyerBoardContextPayload> {
    override suspend fun retrieve(call: ApplicationCall): ClientContext<FlyerBoardContextPayload> {
        val token = call.request.header("Authorization")?.removePrefix("Bearer ")
            ?: return AnonymousClientContext()
        val user = auth.retrieveUser(token)
        val profile = userProfileDatastore.getUserProfile(UserId(user.id))
        // ...
    }
}
```
**References:**
- Low-Level Plan: PR 5.4
- `edifikana/.../controller/authentication/SupabaseContextRetriever.kt`

---

## Task: TASK-014
**Title:** Implement input sanitization utility
**Description:** Create a server-side input sanitization utility that strips HTML tags, trims whitespace, and enforces maximum lengths on user-supplied text. Used by FlyerService for title and description fields.
**Dependencies:** TASK-001
**Implementation Plan:**
1. Create `InputSanitizer.kt` in the backend service layer:
   - `sanitizeText(input: String, maxLength: Int): String` — strips HTML tags via regex, trims whitespace, truncates to maxLength.
2. Ensure no HTML/script content can pass through to storage.
**Testing Plan:**
- Write `InputSanitizerTest.kt` verifying:
  - HTML tags are stripped (`<script>alert('x')</script>` → `alert('x')`).
  - Whitespace is trimmed.
  - Strings exceeding maxLength are truncated.
  - Clean text passes through unchanged.
  - Edge cases: empty string, only whitespace, nested tags.
**Acceptance Criteria:**
- HTML tags are removed from all user-supplied text.
- Text is trimmed and length-limited.
- Utility is stateless and reusable.
**Sample Code:**
```kotlin
object InputSanitizer {
    private val htmlTagRegex = Regex("<[^>]*>")

    fun sanitizeText(input: String, maxLength: Int): String {
        return input
            .replace(htmlTagRegex, "")
            .trim()
            .take(maxLength)
    }
}
```
**References:**
- Low-Level Plan: PR 8.1
- Standards: "Security" section

---

## Task: TASK-015
**Title:** Implement FlyerController routes
**Description:** Create the HTTP route controller for flyer CRUD endpoints. Handles both JSON and multipart form data (for file uploads), wraps responses in the standard JSON envelope, and enforces authentication on protected endpoints.
**Dependencies:** TASK-010, TASK-013, TASK-005
**Implementation Plan:**
1. Create `FlyerController.kt` implementing `Controller`:
   - `GET /api/v1/flyers` — unauthenticated, paginated, optional status filter and `q` search param.
   - `GET /api/v1/flyers/{id}` — unauthenticated, single flyer.
   - `POST /api/v1/flyers` — authenticated, multipart form data (file + title + description + expiresAt).
   - `PUT /api/v1/flyers/{id}` — authenticated, multipart, ownership enforced by service.
   - `GET /api/v1/flyers/archive` — unauthenticated, paginated archived flyers.
   - `GET /api/v1/flyers/mine` — authenticated, user's own flyers.
2. Create JSON envelope utility: `ApiResponse<T>` with `data` and `error` fields.
3. Create `NetworkMapper.kt` for converting between domain models and network DTOs.
4. Use Ktor `call.receiveMultipart()` for file upload endpoints.
**Testing Plan:**
- Verified via integration tests in TASK-024.
- Unit test `NetworkMapper` conversions.
**Acceptance Criteria:**
- All six endpoints registered and functional.
- JSON envelope wraps all responses consistently.
- Multipart file upload works for create and update.
- Authentication required on POST/PUT and `/mine` endpoints.
- `q` query parameter performs ILIKE search on title and description.
**Sample Code:**
```kotlin
class FlyerController(
    private val flyerService: FlyerService,
    private val contextRetriever: FlyerBoardContextRetriever,
) : Controller {
    override fun registerRoutes(route: Routing) {
        route.route("api/v1/flyers") {
            get { /* list flyers */ }
            get("{id}") { /* get single flyer */ }
            post { /* create flyer — multipart */ }
            put("{id}") { /* update flyer — multipart */ }
            get("archive") { /* list archived */ }
            get("mine") { /* list user's flyers */ }
        }
    }
}
```
**References:**
- Low-Level Plan: PR 5.5
- Standards: "API Design" section

---

## Task: TASK-016
**Title:** Implement ModerationController and HealthController
**Description:** Create route controllers for admin moderation actions and the health check endpoint. Moderation endpoints require admin role verification.
**Dependencies:** TASK-011, TASK-013, TASK-005
**Implementation Plan:**
1. Create `ModerationController.kt`:
   - `GET /api/v1/moderation/pending` — authenticated + admin role check, paginated.
   - `POST /api/v1/moderation/{id}/approve` — admin-only.
   - `POST /api/v1/moderation/{id}/reject` — admin-only.
   - Admin check: verify `context.payload.role == UserRole.ADMIN`, throw `UnauthorizedException` otherwise.
2. Create `HealthController.kt`:
   - `GET /api/v1/health` — unauthenticated, returns `{ "status": "ok" }`.
**Testing Plan:**
- Verified via integration tests in TASK-024.
- Unit test admin role check logic.
**Acceptance Criteria:**
- Moderation endpoints reject non-admin users with 403.
- Approve/reject correctly delegate to ModerationService.
- Health endpoint returns 200 with `{ "status": "ok" }`.
**Sample Code:**
N/A
**References:**
- Low-Level Plan: PR 5.6

---

## Task: TASK-017
**Title:** Wire backend dependency injection modules
**Description:** Connect all backend components (datastores, services, controllers, auth) via Koin DI modules. Configure the Supabase client, settings keys, and application module.
**Dependencies:** TASK-015, TASK-016, TASK-012
**Implementation Plan:**
1. Update `DatastoreModule.kt`: wire Supabase client (URL/key from settings), bind `FlyerDatastore`, `FileDatastore`, `UserProfileDatastore` to their Supabase implementations.
2. Update `ServicesModule.kt`: register `FlyerService`, `ModerationService`, `ExpiryService`.
3. Update `ControllerModule.kt`: register `FlyerController`, `ModerationController`, `HealthController`.
4. Update `ApplicationModule.kt`: register `FlyerBoardContextRetriever`, JSON configuration.
5. Create `FlyerBoardSettingKey.kt` with setting keys for `SUPABASE_URL`, `SUPABASE_KEY`, `MAX_FILE_SIZE_BYTES`.
6. Start `ExpiryService` in the Ktor application lifecycle.
**Testing Plan:**
- Run `./gradlew :flyerboard:back-end:release` to verify compilation and DI graph resolution.
- Integration tests (TASK-024) verify the full wired application.
**Acceptance Criteria:**
- All components are registered in DI and resolvable.
- Supabase client configured from environment variables.
- ExpiryService starts on application boot.
- Application compiles and starts without DI errors.
**Sample Code:**
```kotlin
val datastoreModule = module {
    single<FlyerDatastore> { SupabaseFlyerDatastore(get()) }
    single<FileDatastore> { SupabaseFileDatastore(get()) }
    single<UserProfileDatastore> { SupabaseUserProfileDatastore(get()) }
}
```
**References:**
- Low-Level Plan: PR 5.7
- `templatereplaceme/.../dependencyinjection/` for DI patterns

---

## Task: TASK-018
**Title:** Implement frontend Supabase Auth integration
**Description:** Set up the Supabase client and authentication manager in the frontend. Handles sign-up, sign-in, sign-out, session persistence, and access token retrieval for API calls.
**Dependencies:** TASK-001
**Implementation Plan:**
1. Update `ServiceModule.kt` to create Supabase client with Auth plugin and `SettingsSessionManager`.
2. Create `AuthManager.kt` in `managers/`:
   - `signUp(email, password): Result<Unit>` via `auth.signUpWith(Email)`.
   - `signIn(email, password): Result<Unit>` via `auth.signInWith(Email)`.
   - `signOut(): Result<Unit>`.
   - `isAuthenticated(): Boolean`.
   - `getAccessToken(): String?` for backend API calls.
   - `currentUserId(): UserId?`.
   - Observe `auth.sessionStatus` Flow for auth state changes.
3. Register in `ManagerModule.kt`.
**Testing Plan:**
- Write `AuthManagerTest.kt` with mocked Supabase Auth verifying:
  - Sign-in success updates auth state.
  - Sign-out clears session.
  - Access token is available after sign-in.
**Acceptance Criteria:**
- Auth flows (sign-up, sign-in, sign-out) work via supabase-kt.
- Session persists across page reloads via `SettingsSessionManager`.
- Access token retrievable for backend API calls.
**Sample Code:**
```kotlin
class AuthManager(private val supabaseClient: SupabaseClient) {
    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }
}
```
**References:**
- Low-Level Plan: PR 6.1
- Architecture Design: "Auth token persistence" decision

---

## Task: TASK-019
**Title:** Implement frontend FlyerService API client
**Description:** Create the frontend service layer that communicates with the backend REST API. Handles JSON parsing, auth token attachment, multipart file uploads, and model mapping.
**Dependencies:** TASK-018, TASK-004
**Implementation Plan:**
1. Create `FlyerService.kt` interface with methods: `listFlyers`, `getFlyer`, `createFlyer`, `updateFlyer`, `listArchived`, `listMyFlyers`, `listPending`, `moderate`.
2. Create `FlyerServiceImpl.kt` using Ktor HTTP client:
   - Attach auth token via `AuthManager.getAccessToken()` in request headers.
   - Parse JSON envelope responses.
   - Use `submitFormWithBinaryData(...)` for file uploads.
3. Create `FlyerModel.kt` — UI-friendly model.
4. Create `NetworkMapper.kt` for DTO ↔ UI model conversions.
5. Register in `ServiceModule.kt`.
**Testing Plan:**
- Write `FlyerServiceImplTest.kt` with mocked HTTP client verifying:
  - Correct HTTP methods and paths.
  - Auth token attached to authenticated requests.
  - JSON envelope correctly parsed.
**Acceptance Criteria:**
- All backend endpoints callable from frontend.
- Auth token automatically attached to protected requests.
- File upload via multipart form data works.
**Sample Code:**
N/A
**References:**
- Low-Level Plan: PR 6.2

---

## Task: TASK-020
**Title:** Implement SignIn and SignUp screens
**Description:** Create authentication screens following the 5-file feature pattern. SignIn allows existing users to log in; SignUp allows new user registration with password confirmation.
**Dependencies:** TASK-018
**Implementation Plan:**
1. Create `features/auth/signin/` with 5 files:
   - `SignInUIState.kt`: `email`, `password`, `isLoading`, `errorMessage`.
   - `SignInEvent.kt`: `NavigateToHome`, `NavigateToSignUp`.
   - `SignInViewModel.kt`: calls `AuthManager.signIn()`.
   - `SignInScreen.kt`: form with email/password fields and submit button.
   - `SignInScreen.preview.kt`.
2. Create `features/auth/signup/` with 5 files:
   - `SignUpUIState.kt`: adds `confirmPassword`.
   - `SignUpViewModel.kt`: validates passwords match, calls `AuthManager.signUp()`.
3. Register ViewModels in `ViewModelModule.kt`.
4. Add `SignInDestination` and `SignUpDestination` to navigation.
**Testing Plan:**
- Write `SignInViewModelTest.kt` verifying auth flow, loading state, error handling.
- Write `SignUpViewModelTest.kt` verifying password validation and sign-up flow.
**Acceptance Criteria:**
- Both screens render and handle user input.
- Successful sign-in navigates to home.
- Password mismatch on sign-up shows error.
- Loading state displayed during async operations.
**Sample Code:**
N/A — follows standard 5-file pattern from `.ai/instructions.md`.
**References:**
- Low-Level Plan: PR 7.1
- `.ai/instructions.md` — Front End feature pattern

---

## Task: TASK-021
**Title:** Implement FlyerList and FlyerDetail screens
**Description:** Create the public flyer browsing screen (grid/list of approved flyers with pagination) and the detail screen (full-size image or PDF iframe display). These are the primary public-facing screens.
**Dependencies:** TASK-019
**Implementation Plan:**
1. Create `features/flyerlist/` with 5 files:
   - `FlyerListUIState.kt`: `flyers: List<FlyerModel>`, `isLoading`, `hasMore`, `errorMessage`.
   - `FlyerListViewModel.kt`: loads approved flyers with pagination (incrementing offset).
   - `FlyerListScreen.kt`: grid/list layout with thumbnail, title, expiry. Tap navigates to detail.
   - Use Coil3 for image loading. Show PDF icon placeholder for PDF flyers.
2. Create `features/flyerdetail/` with 5 files:
   - `FlyerDetailUIState.kt`: `flyer: FlyerModel?`, `isLoading`, `isOwner`.
   - `FlyerDetailViewModel.kt`: loads flyer, checks ownership.
   - `FlyerDetailScreen.kt`: full-size image via Coil3. PDF via `<iframe>` using Compose Web interop. Edit button for owner.
3. Register ViewModels and navigation destinations.
**Testing Plan:**
- Write `FlyerListViewModelTest.kt` verifying load, pagination, error states.
- Write `FlyerDetailViewModelTest.kt` verifying load and ownership check.
**Acceptance Criteria:**
- FlyerList displays approved flyers with infinite scroll pagination.
- FlyerDetail shows full image or embedded PDF.
- Ownership-based edit button visibility.
- Loading and error states handled.
**Sample Code:**
N/A
**References:**
- Low-Level Plan: PR 7.2, PR 7.3

---

## Task: TASK-022
**Title:** Implement FlyerUpload and FlyerEdit screens
**Description:** Create screens for uploading new flyers and editing existing ones. Both require a file picker (via JS interop for WASM) and multipart form submission. Edits reset flyer status to pending.
**Dependencies:** TASK-019, TASK-020, TASK-021
**Implementation Plan:**
1. Create WASM file picker helper:
   - `FilePickerHelper.kt` (expect/actual) in WASM source set.
   - Uses JS interop to create `<input type="file" accept="image/*,.pdf">` element.
   - Returns file name and bytes to Kotlin.
2. Create `features/flyerupload/` with 5 files:
   - `FlyerUploadUIState.kt`: `title`, `description`, `expiresAt`, `selectedFileName`, `selectedFileBytes`, `isLoading`, `errorMessage`.
   - `FlyerUploadViewModel.kt`: validates form, calls `FlyerService.createFlyer()`.
   - Success message indicates flyer is pending moderation.
3. Create `features/flyeredit/` with 5 files:
   - Pre-populates from existing flyer.
   - `FlyerEditUIState.kt` adds: `originalFlyer`, `hasChanges`.
   - Shows message that edited flyer returns to pending.
4. Register ViewModels and navigation destinations.
**Testing Plan:**
- Write `FlyerUploadViewModelTest.kt` verifying form validation, loading state, success/error events.
- Write `FlyerEditViewModelTest.kt` verifying pre-population and update flow.
**Acceptance Criteria:**
- File picker works in WASM browser context.
- Upload creates flyer with pending status.
- Edit pre-populates fields and supports optional file replacement.
- User informed about pending moderation after submit.
**Sample Code:**
```kotlin
// FilePickerHelper.wasmJs.kt
actual class FilePickerHelper {
    actual suspend fun pickFile(accept: String): FilePickerResult? {
        // JS interop: create <input type="file">, trigger click, read bytes
    }
}
```
**References:**
- Low-Level Plan: PR 7.4, PR 7.5
- Architecture Design: "File upload (WASM)" decision

---

## Task: TASK-023
**Title:** Implement MyFlyers, Archive, and ModerationQueue screens
**Description:** Create the remaining screens: MyFlyers (user's own flyers with status badges), Archive (publicly browsable expired flyers), and ModerationQueue (admin-only pending flyer review with approve/reject actions).
**Dependencies:** TASK-019, TASK-020
**Implementation Plan:**
1. Create `features/myflyers/` with 5 files:
   - Shows status badge (pending/approved/rejected/archived) per flyer.
   - Edit button on non-archived flyers.
   - Paginated list of user's own flyers.
2. Create `features/archive/` with 5 files:
   - Same layout as FlyerList but filters by ARCHIVED status.
   - Consider extracting shared `FlyerGrid` composable to `shared-ui` if duplication is significant.
3. Create `features/moderation/` with 5 files:
   - `ModerationQueueUIState.kt`: `pendingFlyers`, `isLoading`, `hasMore`.
   - Inline "Approve" and "Reject" buttons per flyer.
   - Only accessible if user has admin role.
4. Register all ViewModels and navigation destinations.
**Testing Plan:**
- Write `MyFlyersViewModelTest.kt` verifying list load and pagination.
- Write `ModerationQueueViewModelTest.kt` verifying approve/reject actions update the list.
**Acceptance Criteria:**
- MyFlyers shows user's flyers with correct status badges.
- Archive displays expired flyers with pagination.
- ModerationQueue shows pending flyers with approve/reject functionality.
- Admin-only access enforced on ModerationQueue.
**Sample Code:**
N/A
**References:**
- Low-Level Plan: PR 7.6, PR 7.7, PR 7.8

---

## Task: TASK-024
**Title:** Wire navigation graph and app shell
**Description:** Connect all frontend screens into a unified navigation graph with app shell (bottom nav or side menu), auth state handling, and admin-conditional navigation items.
**Dependencies:** TASK-020, TASK-021, TASK-022, TASK-023
**Implementation Plan:**
1. Update `FlyerBoardWindowScreen.kt` with full navigation graph.
2. Define `FlyerBoardWindowNavGraphDestination.kt` with all destinations.
3. Add navigation UI: Browse, Archive, My Flyers, Upload, Moderation (admin-only, conditionally visible).
4. Handle auth state: redirect to SignIn for authenticated-only screens.
5. App bar with sign-in/sign-out button.
6. Admin role check to show/hide moderation nav item.
**Testing Plan:**
- Manual browser testing of navigation flows.
- Verify auth-gated screens redirect to sign-in.
- Verify admin-only nav item visibility.
**Acceptance Criteria:**
- All screens reachable via navigation.
- Auth-required screens redirect unauthenticated users.
- Admin nav item only visible to admin users.
- App shell consistent across all screens.
**Sample Code:**
N/A
**References:**
- Low-Level Plan: PR 7.9

---

## Task: TASK-025
**Title:** Write backend service unit tests
**Description:** Create comprehensive unit tests for all backend services: FlyerService, ModerationService, ExpiryService, and InputSanitizer. Tests use mocked datastores to verify business logic in isolation.
**Dependencies:** TASK-010, TASK-011, TASK-012, TASK-014
**Implementation Plan:**
1. Create `FlyerServiceTest.kt` testing: create sets PENDING, update resets to PENDING, non-owner update fails, MIME validation, file size validation, HTML sanitization.
2. Create `ModerationServiceTest.kt` testing: approve → APPROVED, reject → REJECTED, non-admin fails.
3. Create `ExpiryServiceTest.kt` testing: expired flyers transition to ARCHIVED.
4. Create `InputSanitizerTest.kt` testing: HTML stripping, length truncation, whitespace trimming.
**Testing Plan:**
- Run `./gradlew :flyerboard:back-end:jvmTest` and verify all tests pass.
**Acceptance Criteria:**
- All business logic paths covered by unit tests.
- Tests pass in CI.
- Mocks verify correct datastore interactions.
**Sample Code:**
```kotlin
class FlyerServiceTest {
    @Test
    fun `createFlyer sets status to PENDING`() {
        // arrange: mock datastores
        // act: call createFlyer
        // assert: datastore called with status = PENDING
    }
}
```
**References:**
- Low-Level Plan: PR 9.1
- Standards: "Testing" section

---

## Task: TASK-026
**Title:** Write backend integration tests
**Description:** Create integration tests covering full request/response cycles using Ktor's test engine. Tests verify the complete stack from HTTP request through controller, service, and datastore layers.
**Dependencies:** TASK-017
**Implementation Plan:**
1. Create `FlyerApiIntegrationTest.kt` in `src/integTest/`:
   - Create flyer → verify pending → approve → verify listed publicly.
   - Upload with invalid MIME type → verify 400.
   - Edit by non-owner → verify 403.
   - Pagination returns correct page sizes.
   - Search via `q` parameter returns matching flyers.
2. Create `ModerationApiIntegrationTest.kt`:
   - Approve/reject with admin token succeeds.
   - Non-admin access returns 403.
3. Create `HealthApiIntegrationTest.kt`:
   - Health endpoint returns 200 with `{ "status": "ok" }`.
4. Use mocked datastores or test Supabase instance.
**Testing Plan:**
- Run integration test suite and verify all tests pass.
**Acceptance Criteria:**
- Full lifecycle test (create → moderate → browse) passes.
- Error cases (invalid input, unauthorized access) return correct HTTP status codes.
- JSON envelope format verified in responses.
**Sample Code:**
N/A
**References:**
- Low-Level Plan: PR 9.2
- Standards: "Testing" section

---

## Task: TASK-027
**Title:** Write frontend ViewModel tests
**Description:** Create unit tests for all frontend ViewModels verifying UIState transitions, event emissions, error handling, and service interactions.
**Dependencies:** TASK-020, TASK-021, TASK-022, TASK-023
**Implementation Plan:**
1. Create tests in `flyerboard/front-end/shared-app/src/jvmTest/`:
   - `FlyerListViewModelTest.kt` — mock FlyerService, verify load/pagination UIState updates.
   - `FlyerUploadViewModelTest.kt` — verify form validation, loading state, success/error events.
   - `SignInViewModelTest.kt` — verify auth flow and error handling.
   - `ModerationQueueViewModelTest.kt` — verify approve/reject actions update list.
   - `FlyerEditViewModelTest.kt` — verify pre-population and update flow.
   - `MyFlyersViewModelTest.kt` — verify list load.
2. Use `BaseViewModelTest` from `framework:core-compose`.
**Testing Plan:**
- Run `./gradlew :flyerboard:front-end:shared-app:jvmTest` and verify all tests pass.
**Acceptance Criteria:**
- All ViewModels have corresponding test files.
- UIState transitions verified for success, loading, and error paths.
- Events verified for navigation and user feedback.
**Sample Code:**
N/A
**References:**
- Low-Level Plan: PR 9.3
- Standards: "Testing" section

---

## Task: TASK-028
**Title:** Create deployment configuration and documentation
**Description:** Create Nginx configuration, systemd service file, environment variable documentation, and deployment guide. Ensures the application can be deployed on a VPS with proper process management and TLS termination.
**Dependencies:** TASK-017, TASK-024
**Implementation Plan:**
1. Create `flyerboard/deploy/` directory with:
   - `nginx.conf` — serves WASM static files from `/var/www/flyerboard/`, proxies `/api/` to `http://localhost:8080`.
   - `flyerboard.service` — systemd unit file running the fat JAR with environment variable references.
   - `env.example` — documents required env vars: `SUPABASE_URL`, `SUPABASE_KEY`, `PORT`, `MAX_FILE_SIZE_BYTES`.
2. Update `application.conf` to read port from env var.
3. Document:
   - How to create the `flyer-files` storage bucket in Supabase.
   - How to bootstrap the first admin user (manual SQL update).
   - How to apply database migrations.
   - Frontend build command: `./gradlew :flyerboard:front-end:app-wasm:wasmJsBrowserDistribution`.
**Testing Plan:**
- Validate Nginx config syntax with `nginx -t` (documented step).
- Verify systemd service file has correct ExecStart path.
- Verify `env.example` lists all required variables.
**Acceptance Criteria:**
- Nginx config correctly routes static assets and API requests.
- Systemd service file is functional.
- All required environment variables documented.
- Deployment steps documented for admin bootstrap and migrations.
**Sample Code:**
```nginx
server {
    listen 443 ssl;
    server_name flyerboard.example.com;

    location / {
        root /var/www/flyerboard;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080;
    }
}
```
**References:**
- Low-Level Plan: PR 10.1
- Architecture Design: "Reverse Proxy (Nginx)" section
- Standards: "Deployment" section