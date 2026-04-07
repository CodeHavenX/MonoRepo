# Architecture & Design

## Architecture & Design

Edifikana is a Kotlin Multiplatform (KMP) application with a Ktor backend and Supabase as the database and authentication layer.

### Components

- **Frontend (KMP — Android, iOS, Web):** Built with Kotlin Multiplatform using the **Feature → Manager → Service** layered pattern. Each feature has its own ViewModel, with Managers coordinating business logic across Services, and Services communicating with the backend API and local state.
- **Backend (Ktor):** A Kotlin server using the **Controller → Service → Datastore** pattern. Controllers handle HTTP routing and request/response mapping; Services enforce business rules and authorization; Datastores interact directly with Supabase via the service role key. Rate limiting and abuse prevention are enforced at the Cloudflare layer before requests reach the backend.
- **Database & Auth (Supabase):** PostgreSQL database managed via Supabase with Row Level Security (RLS) enabled using a **blanket deny** approach — all non-service-role connections are blocked at the DB layer. All authorization (org membership, role gating, resident unit scoping) is enforced exclusively in the Kotlin backend service layer. Supabase Auth handles all authentication and password management.
- **Storage:** Supabase Storage for document uploads, accessed via the existing `StorageService` / `StorageController`.
- **CI/CD:** GitHub Actions pipelines for building, testing, signing, and deploying. Coolify for container deployments. Fastlane + Supply for Android Play Store publishing.

### Key Domain Entities

The database uses dedicated tables for each core domain entity rather than a shared polymorphic table:

| Table | Purpose |
|-------|---------|
| `organizations` | Top-level multi-tenant boundary |
| `properties` | Buildings/properties within an org |
| `units` | Individual rentable/ownable units within a property |
| `common_areas` | Shared amenities (pool, gym, lobby) within a property |
| `unit_occupants` | Residents associated with a unit |
| `tasks` | Work items tied to a unit or common area; fields: status, priority, assignee, due date |
| `maintenance_requests` | Resident-submitted requests; links to tasks via `originating_task_id` |
| `guests` | Check-in/check-out records per unit |
| `rent_config` | Rent and HOA due configuration per unit |
| `payment_records` | Payment history per unit |
| `documents` | Files attached to properties or units |
| `event_log_entries` | Legacy attendance/delivery/incident log (not used for task/guest domain) |
| `invites` | Org invitations with role, status, and optional unit scoping |
| `user_organization_mapping` | Membership records with `OrgRole` and `OrgMemberStatus` |

### User Roles

| Role | Description |
|------|-------------|
| Owner | Organization creator; full access; cannot be removed |
| Admin | Full operational access except org deletion |
| Staff | View all tasks, self-assign, update task status |
| Resident | View own unit, submit maintenance requests, access documents |

### Data Flow

**Frontend → Backend → Supabase:**

1. The KMP frontend makes authenticated HTTP requests to the Ktor backend.
2. The backend Controller validates the request and delegates to a Service.
3. The Service enforces authorization (org membership check, role check, unit scoping for residents) and calls the Datastore.
4. The Datastore queries Supabase using the service role key, which bypasses RLS.
5. Responses propagate back through Datastore → Service → Controller → Frontend.

**Authentication:**
- Users authenticate directly with Supabase Auth (sign-up, sign-in, OTP, password reset).
- The frontend passes the Supabase JWT to the backend on each request.
- The backend validates the JWT and resolves the user's org membership and role before processing any operation.
- **Token expiration & refresh:** JWTs expire after 1 hour (Supabase default). The frontend Supabase SDK handles token refresh automatically; expired tokens are rejected by the backend with a 401.
- **Revocation:** The backend validates tokens server-side via `auth.retrieveUser(token)` on every request. Revoked or invalidated tokens fail immediately (fail-closed behavior). There is no local token cache.
- **Mid-session role/membership changes:** Org membership and role are resolved fresh from the database on every request — they are not embedded in the JWT. Role or membership changes take effect on the next request with no session invalidation required.

### Deployment Stages

| Stage | Description |
|-------|-------------|
| Local | Developer environment; all components run locally |
| Integration | Ephemeral backend; persistent Supabase instance; integration tests run against it on every PR |
| Staging | Persistent pre-production environment (Coolify-hosted backend + frontend, dedicated Supabase project) |
| Production | Live user-facing environment (pending) |

### Navigation Structure

**Admin/Staff:** Bottom navigation with Dashboard, Properties, Tasks, and More (Settings) tabs.

**Resident:** Bottom navigation with Home, Requests, Documents, and Profile tabs.

**Auth Flow:** Sign In → Organization Check → Role-based routing to Admin view or Resident view. Supports invitation accept deep links, org creation, and multi-org selection.

### Build Variants (Android)

Four APK variants from a 2×2 matrix:
- Build types: `debug`, `release`
- Flavors: `preprod`, `prod`

Resulting in: `app-preprod-debug`, `app-preprod-release`, `app-prod-debug`, `app-prod-release`.
