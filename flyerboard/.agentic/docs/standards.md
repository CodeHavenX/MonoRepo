# Standards

## Standards

Coding standards, conventions, and best practices for the community flyer board project.

### Coding Style

- Follow the official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) throughout frontend and backend.
- Use meaningful, descriptive names for classes, functions, and variables — avoid abbreviations.
- Keep functions small and focused on a single responsibility.
- Prefer immutable data (`val`, data classes) over mutable state wherever possible.
- Use Kotlin coroutines and `Flow` for asynchronous operations; avoid blocking calls on the main/UI thread.

### Project Structure

- **Frontend:** Compose Multiplatform module; UI components separated from state/viewmodel logic.
- **Backend:** Ktor-based module; routes, services, and repository layers are kept separate.
- **Shared:** Common data models (DTOs, domain objects) live in a shared Kotlin Multiplatform module consumed by both frontend and backend.

### API Design

- RESTful conventions: nouns for resources (`/flyers`, `/users`), HTTP verbs for actions.
- All API responses use a consistent JSON envelope with `data` and `error` fields.
- Paginate list endpoints (cursor or offset-based); do not return unbounded result sets.
- API versioning via URL prefix (`/api/v1/...`).
- List endpoints support an optional `q` query parameter for basic `ILIKE` keyword search on title and description.

### Security

- All file uploads are validated server-side: accepted formats are JPEG, PNG, WebP, and PDF; file size limit is configurable via environment variable (default: 10 MB).
- Never expose internal IDs or storage paths directly; use opaque identifiers and signed URLs.
- Admin endpoints require explicit role verification in addition to authentication.
- Sanitize all user-supplied text fields before storing (strip HTML/scripts).
- On startup, the backend checks for the existence of an admin user and creates one from `ADMIN_EMAIL` / `ADMIN_PASSWORD` env variables if none exists.

### Testing

- Unit tests required for all business logic in the backend service layer.
- Integration tests cover the full request/response cycle for critical API endpoints (upload, approve, archive).
- Frontend UI logic (ViewModels / state holders) should be unit tested independently of rendering.
- Tests live alongside the code they test in a `test` source set.

### Database

- All schema changes managed via **Supabase migrations** (versioned SQL files committed to source control).
- All database and storage access goes through **supabase-kt** — no raw SQL or alternative ORM.
- Every table has `created_at` and `updated_at` timestamp columns.
- Indexes must be defined on all columns used in filters or sort orders (`status`, `expires_at`, `created_at`) to support large-city scale.

### Deployment

- Environment-specific configuration (DB URLs, secrets) provided via environment variables — never committed to source control.
- Required environment variables include: Supabase URL, Supabase service key, `ADMIN_EMAIL`, `ADMIN_PASSWORD`, and `MAX_FILE_SIZE_BYTES`.
- The backend exposes a `/health` endpoint for VPS process monitoring.
- Static frontend assets are built and served by Nginx; backend runs as a systemd service.
- Database schema migrations are versioned SQL files committed to source control and applied manually via the Supabase dashboard or CLI.
