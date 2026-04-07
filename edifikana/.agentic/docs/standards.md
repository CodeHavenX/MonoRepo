# Standards

## Standards

Coding standards, conventions, and best practices for the Edifikana project.

### Architecture Patterns

- **Frontend (KMP):** Follow the Feature → Manager → Service layered pattern. Each feature module contains its own ViewModel. Managers coordinate business logic across Services. Services communicate with the backend API.
- **Backend (Ktor):** Follow the Controller → Service → Datastore pattern. Controllers handle routing and HTTP; Services enforce authorization and business rules; Datastores interact with Supabase.
- **No direct Supabase access from frontend:** All data access goes through the Ktor backend. The frontend never uses the Supabase service role key.

### Domain Types Convention

Network response/request classes and domain/service model classes must use domain types — never raw primitives for identifiers, roles, or timestamps:

- Use `kotlinx.datetime.Instant` (not `Long`) for timestamps
- Use enum types (e.g. `InviteRole`, `OrgRole`, `TaskStatus`) for role and status fields
- Use ID value classes (e.g. `UnitId`, `PropertyId`) for identifiers
- Entity classes annotated with `@SupabaseModel` are exempt from this rule

### Security

- **RLS Blanket Deny:** All database tables have RLS enabled with no permissive policies. Non-service-role connections are blocked at the DB layer. All authorization is enforced at the Kotlin backend service layer.
- **Authentication:** All password management is delegated to Supabase Auth. Never implement custom password hashing. The client verifies the current password via `auth.signInWith()` before any password update.
- **Authorization enforcement:** Every backend Service method must validate org membership, user role, and (for residents) unit scoping before executing any operation.

### Module Structure

- Create new Gradle modules following the patterns in the `samples/` folder.
- Each module must have a `build.gradle.kts` and be registered in `settings.gradle.kts`.
- Add a dependency from the `releaseAll` task to the new module's `release` task.
- Use local plugins (not direct plugin application) for safe defaults and best practices.

### Coding Style

- Use meaningful, descriptive names for classes, functions, and variables.
- Write self-documenting code; only add comments where the logic is not self-evident.
- Use Detekt for static analysis. Detekt formatting rules are enforced in CI.
- Follow Kotlin idioms: prefer `val` over `var`, use data classes for models, avoid nullable types unless necessary.
- Soft-delete pattern: all domain tables include a `deleted_at TIMESTAMPTZ` column. Never hard-delete records; set `deleted_at` instead.

### Testing

- Unit tests are required for all business logic in Services and Managers.
- Integration tests must run against a real Supabase instance (no mocking the database).
- Use seed data scripts for local testing setup.
- Integration tests must pass before merging a PR (enforced in CI).
- Avoid the deprecated Turbine test pattern for flows — see issue #379 for the current approach.

### UI & Design

- Follow the Edifikana Visual Design System for all UI work:
  - **Color tokens:** Use defined palette (primary blue family, warm off-white backgrounds, semantic success/warning/error/info colors).
  - **Typography:** System default font stack; use the defined type scale (display 32px down to caption 12px).
  - **Spacing:** 4px base unit scale (`space-1` through `space-12`).
  - **Border radius:** Soft/rounded (`radius-sm` 8px through `radius-full` 9999px).
  - **Mobile-first:** Primary viewport 375px; design for mobile, scale up.
  - **Accessibility:** WCAG AA compliance; minimum 44–48px touch targets; visible focus rings.
  - **Icons:** Outline style, 1.5–2px stroke, 24px standard size.

### CI/CD

- GitHub Actions is the CI/CD platform. Secrets are managed via GitHub Environments.
- Android signing uses keystore files stored as base64-encoded GitHub secrets.
- Play Store publishing uses Fastlane with the Supply plugin.
- Deploy pipeline stages: Integration → Staging → Production. Never skip integration tests.
- Use `bundle exec fastlane [lane]` for Fastlane commands.

### Naming Conventions

- Database tables: `snake_case` plural (e.g. `unit_occupants`, `payment_records`)
- Kotlin classes: `PascalCase` (e.g. `UnitService`, `TaskDatastore`)
- API contracts: `PascalCase` suffixed with `Api` (e.g. `UnitApi`, `TaskApi`)
- ID value classes: `PascalCase` suffixed with `Id` (e.g. `UnitId`, `PropertyId`)
- Enum values: `SCREAMING_SNAKE_CASE` (e.g. `OrgRole.OWNER`, `TaskStatus.IN_PROGRESS`)
