# Low-Level Plan

## Phase 0: Database & API Foundation

### Epic 0.1 — Schema Migrations

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `DB-01` | Create `units` table | Done | [#381](https://github.com/CodeHavenX/MonoRepo/issues/381) |
| `DB-02` | Create `common_areas` table | Done | [#422](https://github.com/CodeHavenX/MonoRepo/issues/422) |
| `DB-03` | Create `unit_occupants` table | Done | [#382](https://github.com/CodeHavenX/MonoRepo/issues/382) |
| `DB-04` | Create `tasks` table — `status`, `priority`, `assignee_id`, `unit_id`, `due_date`, `status_changed_at` | Done | [#439](https://github.com/CodeHavenX/MonoRepo/issues/439) |
| `DB-05` | Create `maintenance_requests` table — resident-submitted; links to `tasks` via `originating_task_id` | Deferred | [#416](https://github.com/CodeHavenX/MonoRepo/issues/416) |
| `DB-06` | Create `rent_config` table | Done | [#383](https://github.com/CodeHavenX/MonoRepo/issues/383) |
| `DB-07` | Create `payment_records` table | Done | [#383](https://github.com/CodeHavenX/MonoRepo/issues/383) |
| `DB-08` | Create `guests` table — `check_in_time`, `expected_checkout`, `check_out_time` | Deferred | [#417](https://github.com/CodeHavenX/MonoRepo/issues/417) |
| `DB-09` | Create `documents` table | Done | [#384](https://github.com/CodeHavenX/MonoRepo/issues/384) |
| `DB-10` | Alter `user_organization_mapping` — introduce `OrgMemberStatus`, `OrgRole` enums; add `status`, `invited_by`, `joined_at`; extend `invites` with `invite_code`, `accepted_at`, `invited_by`, `unit_id`; introduce `InviteRole`; drop permissive RLS on `rent_config` and `payment_records` | Done | [#418](https://github.com/CodeHavenX/MonoRepo/issues/418) |
| `DB-11` | Enable RLS blanket deny on all new tables — drop existing permissive policies; enforce deny-all for non-service-role connections | Missing | [#394](https://github.com/CodeHavenX/MonoRepo/issues/394) |
| `DB-12` | Remove `org_id` from `units`, `tasks`, `unit_occupants`, `rent_config`, `payment_records` — DB migrations, entities, shared models, services | Missing | [#463](https://github.com/CodeHavenX/MonoRepo/issues/463) |

### Epic 0.2 — API Contracts

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `API-01a` | Define `UnitApi.kt` — CRUD for units | Missing | [#386](https://github.com/CodeHavenX/MonoRepo/issues/386) |
| `API-01b` | Define `CommonAreaApi.kt` — CRUD for common areas | Done | [#424](https://github.com/CodeHavenX/MonoRepo/issues/424) |
| `API-02` | Define `TaskApi.kt` — CRUD + filter by org/property/unit/status/assignee/priority | Missing | [#387](https://github.com/CodeHavenX/MonoRepo/issues/387) |
| `API-03` | Define `MaintenanceRequestApi.kt` — submit, list, update status, convert to task | Missing | [#416](https://github.com/CodeHavenX/MonoRepo/issues/416) |
| `API-04` | Define `OccupantApi.kt` — add occupant, list by unit, deactivate | Missing | [#419](https://github.com/CodeHavenX/MonoRepo/issues/419) |
| `API-05` | Define `GuestApi.kt` — check-in, check-out, list by unit | Deferred | [#417](https://github.com/CodeHavenX/MonoRepo/issues/417) |
| `API-06` | Define `PaymentRecordApi.kt` — CRUD for payment records and rent config | Missing | [#388](https://github.com/CodeHavenX/MonoRepo/issues/388) |
| `API-07` | Define `DocumentApi.kt` | Done | [#389](https://github.com/CodeHavenX/MonoRepo/issues/389) |
| `API-08` | Extend `OrganizationApi.kt` — invite, role management, leave org, ownership transfer | Done | [#426](https://github.com/CodeHavenX/MonoRepo/issues/426) |
| `API-09` | Extend `UserApi.kt` — password reset endpoint | Done | [#425](https://github.com/CodeHavenX/MonoRepo/issues/425) |

### Epic 0.3 — Back-End Controllers, Services, and Datastores

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `BE-01a` | `UnitController` + `UnitService` + `UnitDatastore` | Missing | [#386](https://github.com/CodeHavenX/MonoRepo/issues/386) |
| `BE-01b` | `CommonAreaController` + `CommonAreaService` + `CommonAreaDatastore` | Done | [#424](https://github.com/CodeHavenX/MonoRepo/issues/424) |
| `BE-02` | `TaskController` + `TaskService` + `TaskDatastore` + `SupabaseTaskDatastore` | Missing | [#387](https://github.com/CodeHavenX/MonoRepo/issues/387) |
| `BE-03` | `MaintenanceRequestController` + `MaintenanceRequestService` + `MaintenanceRequestDatastore` | Deferred | [#416](https://github.com/CodeHavenX/MonoRepo/issues/416) |
| `BE-04` | `OccupantController` + `OccupantService` + `OccupantDatastore` | Missing | [#419](https://github.com/CodeHavenX/MonoRepo/issues/419) |
| `BE-05` | `GuestController` + `GuestService` + `GuestDatastore` | Deferred | [#417](https://github.com/CodeHavenX/MonoRepo/issues/417) |
| `BE-06` | `PaymentRecordController` + `PaymentRecordService` + `PaymentRecordDatastore` + `SupabasePaymentRecordDatastore` | Missing | [#388](https://github.com/CodeHavenX/MonoRepo/issues/388) |
| `BE-07` | `DocumentController` + `DocumentService` + `DocumentDatastore` (leverages existing `StorageService`) | Done | [#389](https://github.com/CodeHavenX/MonoRepo/issues/389) |
| `BE-08` | Update `OrganizationController` — membership management, invite, leave, ownership transfer | Done | [#426](https://github.com/CodeHavenX/MonoRepo/issues/426) |
| `BE-09` | Update `UserController` — password reset flow | Done | [#425](https://github.com/CodeHavenX/MonoRepo/issues/425) |

### Epic 0.4 — Shared Data Models

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `MDL-01a` | Add `UnitModel.kt`, `UnitId.kt`, unit network request/response models | Missing | [#386](https://github.com/CodeHavenX/MonoRepo/issues/386) |
| `MDL-01b` | Add `CommonAreaModel.kt`, `CommonAreaId.kt`, common area network request/response models | Done | [#424](https://github.com/CodeHavenX/MonoRepo/issues/424) |
| `MDL-02` | Add `TaskModel.kt` with `TaskStatus` (Open/InProgress/Completed/Cancelled) and `TaskPriority` (Low/Medium/High) enums | Done | [#440](https://github.com/CodeHavenX/MonoRepo/issues/440) |
| `MDL-03` | Add `MaintenanceRequestModel.kt` with `RequestUrgency` and `RequestStatus` enums | Deferred | [#416](https://github.com/CodeHavenX/MonoRepo/issues/416) |
| `MDL-04` | Add `OccupantModel.kt` with `OccupantType` and `OccupancyStatus` enums | Done | [#382](https://github.com/CodeHavenX/MonoRepo/issues/382) |
| `MDL-05` | Add `GuestModel.kt` | Deferred | [#417](https://github.com/CodeHavenX/MonoRepo/issues/417) |
| `MDL-06` | Add `PaymentRecordModel.kt` with `PaymentType`, `PaymentStatus` enums; `RentConfigModel.kt` | Done | [#388](https://github.com/CodeHavenX/MonoRepo/issues/388) |
| `MDL-07` | Add `DocumentModel.kt` with `DocumentType` enum | Done | [#389](https://github.com/CodeHavenX/MonoRepo/issues/389) |
| `MDL-08` | Add `OrganizationMembershipModel.kt` with updated role and status enums; `InviteModel.kt` extensions | Done | [#418](https://github.com/CodeHavenX/MonoRepo/issues/418) |

### Security & CI/CD (Phase 0)

| Issue | Description | Status |
|-------|-------------|--------|
| [#393](https://github.com/CodeHavenX/MonoRepo/issues/393) | Remove insecure hash — delegate all password management to Supabase Auth | Done |
| [#394](https://github.com/CodeHavenX/MonoRepo/issues/394) | RLS blanket deny on all new tables | Missing |
| [#457](https://github.com/CodeHavenX/MonoRepo/issues/457) | Drop permissive RLS policies from completed migrations | Missing |
| [#396](https://github.com/CodeHavenX/MonoRepo/issues/396) | Race condition in user-org association | Missing |
| [#372](https://github.com/CodeHavenX/MonoRepo/issues/372) | RLS gaps on existing tables (cross-tenant leak risk) | Missing |
| [#397](https://github.com/CodeHavenX/MonoRepo/issues/397) | Re-enable Detekt formatting | Open |
| [#399](https://github.com/CodeHavenX/MonoRepo/issues/399) | Build-time constants via BuildConfig | Open |
| [#401](https://github.com/CodeHavenX/MonoRepo/issues/401) | Seed data and local testing setup | Open |
| [#403](https://github.com/CodeHavenX/MonoRepo/issues/403) | Integration tests on PR builds | Open |

---

## Phase 1: Auth & Onboarding Completion

### Epic 1.1 — Password Reset Flow

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `AUTH-01` | Password Reset screen (email entry) | Missing | [#390](https://github.com/CodeHavenX/MonoRepo/issues/390) |
| `AUTH-02` | Password Reset confirmation screen ("check your email") | Missing | [#390](https://github.com/CodeHavenX/MonoRepo/issues/390) |
| `AUTH-03` | `PasswordResetViewModel` + `AuthManager.sendPasswordResetEmail()` extension | Missing | [#390](https://github.com/CodeHavenX/MonoRepo/issues/390) |
| `AUTH-04` | Deep link handler for `/reset/{token}` — exchange code for session, navigate to set-new-password screen | Missing | [#390](https://github.com/CodeHavenX/MonoRepo/issues/390) |
| `AUTH-BE-01` | Backend password reset endpoint | Done | [#425](https://github.com/CodeHavenX/MonoRepo/issues/425) |

### Epic 1.2 — Invitation Accept Flow

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `AUTH-05` | Invitation Accept screen (org/role card + account creation form with disabled email field) | Missing | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) |
| `AUTH-06` | `InvitationAcceptViewModel` — load invitation details from token, create account + join org | Missing | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) |
| `AUTH-07` | Deep link handlers for `edifikana://invite/{token}` and `edifikana://org-invite/{token}` | Missing | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) |
| `AUTH-08` | Handle existing-account vs new-account invitation paths (conditionally show form vs. simple accept) | Missing | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) |

### Epic 1.3 — Organization Onboarding Gaps

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `ORG-01` | My Organizations screen — list all user orgs with role, property count, Current badge | Missing | [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) |
| `ORG-02` | Organization Detail screen — name, status, user role, member count, join date | Missing | [#284](https://github.com/CodeHavenX/MonoRepo/issues/284), [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) |
| `ORG-03` | Leave Organization flow with sole-owner validation block (confirmation dialog) | Missing | [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) |
| `ORG-04` | Transfer Ownership screen — list eligible admins, confirmation dialog, `OrganizationManager.transferOwnership()` | Missing | [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) |
| `ORG-05` | Join Organization via invite code — bottom sheet / modal, `OrganizationManager.joinOrganization(inviteCode)` | Missing | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) |
| `API-08` + `BE-08` | Org membership API + backend — invite, role management, leave, ownership transfer | Done | [#426](https://github.com/CodeHavenX/MonoRepo/issues/426) |

### Security & Bug Fixes (Phase 1)

| Issue | Description | Status |
|-------|-------------|--------|
| [#332](https://github.com/CodeHavenX/MonoRepo/issues/332) | Correct default role assigned on sign-up | Open |
| [#333](https://github.com/CodeHavenX/MonoRepo/issues/333) | Missing `isLoading` reset after successful invitation | Open |
| [#360](https://github.com/CodeHavenX/MonoRepo/issues/360) | Password field in MyAccount not updating after change | Done |
| [#413](https://github.com/CodeHavenX/MonoRepo/issues/413) | CAPTCHA on sign-in and sign-up | Open |

---

## Phase 2: Dashboard

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `DASH-01` | Dashboard screen — 2×2 metric grid (Properties, Units, Pending Tasks, Overdue Payments) | Missing | [#395](https://github.com/CodeHavenX/MonoRepo/issues/395) |
| `DASH-02` | `DashboardViewModel` — aggregated stats from PropertyManager, TaskManager, PaymentManager | Missing | [#395](https://github.com/CodeHavenX/MonoRepo/issues/395) |
| `DASH-03` | `DashboardUIState` + `DashboardStats` + `TaskSummary` data classes | Missing | [#395](https://github.com/CodeHavenX/MonoRepo/issues/395) |
| `DASH-04` | Pending Tasks mini-list on dashboard (3 tasks, sorted by priority then due date) with "View All" link | Missing | [#395](https://github.com/CodeHavenX/MonoRepo/issues/395) |
| `DASH-05` | Recent Activity feed (optional; can ship as empty state for MVP) | Missing | [#395](https://github.com/CodeHavenX/MonoRepo/issues/395) |
| `DASH-06` | FAB → Add Task | Missing | [#395](https://github.com/CodeHavenX/MonoRepo/issues/395) |
| `DASH-07` | Navigation from dashboard metric cards to filtered list screens | Missing | [#395](https://github.com/CodeHavenX/MonoRepo/issues/395) |
| `DASH-08` | Wire Dashboard to bottom nav `[Dashboard][Properties][Tasks][More]`; replace `OrganizationHomeScreen` as default home | Refactor | [#290](https://github.com/CodeHavenX/MonoRepo/issues/290), [#355](https://github.com/CodeHavenX/MonoRepo/issues/355), [#404](https://github.com/CodeHavenX/MonoRepo/issues/404) |

---

## Phase 3: Unit Management

### Existing Property Screens (Refactor)

| Screen | Status | Issue |
|--------|--------|-------|
| Properties Overview (list of properties) | Refactor | [#356](https://github.com/CodeHavenX/MonoRepo/issues/356) |
| Property Home / Detail screen | Refactor | [#356](https://github.com/CodeHavenX/MonoRepo/issues/356), [#370](https://github.com/CodeHavenX/MonoRepo/issues/370) |
| Add Property screen + ViewModel | Done | — |

### Epic 3.1 — Front-End Services

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `UNIT-01` | `UnitService.kt` (client-side) — CRUD via `UnitApi` | Missing | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) |
| `UNIT-02` | `UnitManager.kt` — business logic, `UnitNetworkResponse → UnitModel` mapping | Missing | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) |

### Epic 3.2 — Unit Screens

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `UNIT-03` | Unit List screen (within Property Detail > Units tab) | Missing | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) |
| `UNIT-04` | `UnitListViewModel` + `UnitListUIState` | Missing | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) |
| `UNIT-05` | Unit Detail screen — tabbed: Info, Occupants, Financials, Guests, Tasks, Documents | Missing | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) |
| `UNIT-06` | `UnitDetailViewModel` with tab state management | Missing | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) |
| `UNIT-07` | Add/Edit Unit screen (modal) — unit number, floor, bedrooms, bathrooms, sq ft | Missing | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) |
| `UNIT-08` | `AddEditUnitViewModel` | Missing | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) |
| `UNIT-09` | Update `PropertyDetailScreen` to include Units tab and Common Areas tab | Refactor | [#356](https://github.com/CodeHavenX/MonoRepo/issues/356), [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) |

### Epic 3.3 — Common Areas

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `AREA-01` | Common Area Detail screen (name, type, tasks tab) | Missing | [#427](https://github.com/CodeHavenX/MonoRepo/issues/427) |
| `AREA-02` | `CommonAreaDetailViewModel` | Missing | [#427](https://github.com/CodeHavenX/MonoRepo/issues/427) |
| `AREA-03` | `CommonAreaService.kt` + `CommonAreaManager.kt` (client-side) | Missing | [#427](https://github.com/CodeHavenX/MonoRepo/issues/427) |

---

## Phase 4: Task Management

### Epic 4.1 — Services

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `TASK-01` | `TaskService.kt` (client-side) — wraps `TaskApi` with Task-typed filtering | Missing | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) |
| `TASK-02` | `TaskManager.kt` — create, update, status transitions, filtering | Missing | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) |

### Epic 4.2 — Admin/Staff Screens

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `TASK-03` | Task List screen — filterable by property, status, assignee, priority | Missing | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) |
| `TASK-04` | `TaskListViewModel` + filter state management | Missing | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) |
| `TASK-05` | Task Detail screen — full info, status update, reassign, view property/unit | Missing | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) |
| `TASK-06` | `TaskDetailViewModel` | Missing | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) |
| `TASK-07` | Add/Edit Task screen — property/unit/area picker, priority, due date, assignee | Missing | [#402](https://github.com/CodeHavenX/MonoRepo/issues/402) |
| `TASK-08` | `AddEditTaskViewModel` | Missing | [#402](https://github.com/CodeHavenX/MonoRepo/issues/402) |
| `TASK-09` | Staff Picker bottom sheet (for assignee selection) | Missing | [#402](https://github.com/CodeHavenX/MonoRepo/issues/402) |
| `TASK-10` | Update bottom nav to include Tasks tab | Missing | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) |

### Epic 4.3 — Maintenance Request Review

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `TASK-11` | `MaintenanceRequestService.kt` + `MaintenanceRequestManager.kt` — client-side wrapper | Missing | [#428](https://github.com/CodeHavenX/MonoRepo/issues/428) |
| `TASK-12` | Request Review List screen (admin: pending resident requests) | Missing | [#428](https://github.com/CodeHavenX/MonoRepo/issues/428) |
| `TASK-13` | `RequestReviewViewModel` | Missing | [#428](https://github.com/CodeHavenX/MonoRepo/issues/428) |
| `TASK-14` | Approve Request → pre-filled Add Task flow | Missing | [#430](https://github.com/CodeHavenX/MonoRepo/issues/430) |
| `TASK-15` | Reject Request confirmation flow | Missing | [#430](https://github.com/CodeHavenX/MonoRepo/issues/430) |

---

## Phase 5: Occupant Management

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `OCC-01` | `OccupantService.kt` + `OccupantManager.kt` | Missing | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) |
| `OCC-02` | Occupant List screen (within Unit Detail > Occupants tab) | Missing | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) |
| `OCC-03` | `OccupantListViewModel` | Missing | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) |
| `OCC-04` | Add Occupant form (name, email, type, start date, is_primary) | Missing | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) |
| `OCC-05` | `AddOccupantViewModel` | Missing | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) |
| `OCC-06` | Occupant Detail screen (view info, remove occupant) | Missing | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) |
| `OCC-07` | Remove Occupant confirmation flow (sets `Status = INACTIVE`) | Missing | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) |

---

## Phase 6: Guest Management *(Deferred)*

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `GUEST-01` | `GuestService.kt` + `GuestManager.kt` | Deferred | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) |
| `GUEST-02` | Guest List screen (current + historical) | Deferred | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) |
| `GUEST-03` | `GuestListViewModel` with active/history filter | Deferred | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) |
| `GUEST-04` | Guest Check-In screen (name, contact, dates) | Deferred | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) |
| `GUEST-05` | `GuestCheckInViewModel` | Deferred | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) |
| `GUEST-06` | Guest Detail screen (read-only info, check-out button) | Deferred | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) |
| `GUEST-07` | Guest Check-Out flow (confirmation → sets `check_out_time`) | Deferred | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) |
| `GUEST-08` | `GuestDetailViewModel` (includes check-out logic) | Deferred | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) |

---

## Phase 7: Financial Tracking

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `FIN-01` | `PaymentRecordService.kt` + `PaymentManager.kt` | Missing | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) |
| `FIN-02` | Financial Overview screen (per unit: rent status, HOA, utilities summary) | Missing | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) |
| `FIN-03` | `FinancialOverviewViewModel` | Missing | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) |
| `FIN-04` | Payment List screen (history by period) | Missing | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) |
| `FIN-05` | `PaymentListViewModel` | Missing | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) |
| `FIN-06` | Add Payment Record screen (type, period as month/year picker, amount) | Missing | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) |
| `FIN-07` | `AddPaymentRecordViewModel` | Missing | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) |
| `FIN-08` | Rent Config management screen (set monthly amount, due day per unit — admin only) | Missing | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) |
| `FIN-09` | Overdue status computed from current date vs. `due_date` | Missing | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) |

---

## Phase 8: Document Management

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `DOC-01` | `DocumentService.kt` + `DocumentManager.kt` — wraps existing `StorageManager` for upload + metadata | Missing | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) |
| `DOC-02` | Document Library screen (browse by property/unit, filter by type) | Missing | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) |
| `DOC-03` | `DocumentLibraryViewModel` | Missing | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) |
| `DOC-04` | Document Viewer screen (PDF + image rendering) | Missing | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) |
| `DOC-05` | `DocumentViewerViewModel` | Missing | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) |
| `DOC-06` | Upload Document screen (title, type, file picker, property/unit scope) | Missing | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) |
| `DOC-07` | `UploadDocumentViewModel` | Missing | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) |
| `DOC-08` | Wire document access from Unit Detail > Documents tab | Missing | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) |
| `DOC-09` | Wire document access from Property Detail > Documents tab | Missing | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) |

---

## Phase 9: Resident Experience

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `RES-01` | Extend `MaintenanceRequestManager` — add resident submission path (`submitRequest`) | Missing | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) |
| `RES-02` | Role-based navigation routing — detect Resident role → resident nav graph | Missing | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) |
| `RES-03` | Resident Home screen (unit card, quick actions) | Missing | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) |
| `RES-04` | `ResidentHomeViewModel` | Missing | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) |
| `RES-05` | My Unit screen (read-only unit info) | Missing | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) |
| `RES-06` | `MyUnitViewModel` | Missing | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) |
| `RES-07` | Submit Maintenance Request screen (description, urgency picker, optional photo) | Missing | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) |
| `RES-08` | `SubmitRequestViewModel` | Missing | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) |
| `RES-09` | My Requests list (status: Pending Review / In Progress / Resolved) | Missing | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) |
| `RES-10` | `MyRequestsViewModel` | Missing | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) |
| `RES-11` | Request Detail screen (read-only status tracking) | Missing | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) |
| `RES-12` | My Documents screen (read-only, filtered to user's unit) | Missing | [#432](https://github.com/CodeHavenX/MonoRepo/issues/432) |
| `RES-13` | `MyDocumentsViewModel` | Missing | [#432](https://github.com/CodeHavenX/MonoRepo/issues/432) |
| `RES-14` | Resident bottom nav: `[Home] [Requests] [Documents] [Profile]` | Missing | [#432](https://github.com/CodeHavenX/MonoRepo/issues/432) |

---

## Phase 10: Settings & Profile Gaps

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `SET-01` | Profile Edit screen (name, avatar) | Missing | [#409](https://github.com/CodeHavenX/MonoRepo/issues/409) |
| `SET-02` | Change Password screen (verify current via `auth.signInWith()` before update) | Missing | [#409](https://github.com/CodeHavenX/MonoRepo/issues/409) |
| `SET-03` | Delete Account flow (soft-delete + GDPR erasure request) | Missing | [#409](https://github.com/CodeHavenX/MonoRepo/issues/409) |
| `SET-04` | Team Management screen — list members with roles | Missing | [#434](https://github.com/CodeHavenX/MonoRepo/issues/434) |
| `SET-05` | Invite Member, Change Role, Remove Member actions | Missing | [#434](https://github.com/CodeHavenX/MonoRepo/issues/434) |
| `SET-06` | Settings Home screen — org context card, profile link, team, notifications | Missing | [#435](https://github.com/CodeHavenX/MonoRepo/issues/435) |
| `SET-07` | Wire "More" tab to Settings Home | Missing | [#435](https://github.com/CodeHavenX/MonoRepo/issues/435) |
| `SET-08` | Staff simplified navigation — Staff users see reduced nav without admin-only items | Missing | [#433](https://github.com/CodeHavenX/MonoRepo/issues/433) |

---

## Phase 11: MVP Polish

| Task | Description | Status | Issue |
|------|-------------|--------|-------|
| `POL-01` | Deep link wiring — all invitation and reset deep links connected through nav graph | Missing | [#411](https://github.com/CodeHavenX/MonoRepo/issues/411) |
| `POL-02` | Accessibility pass — WCAG AA, 44–48px touch targets, focus rings, content descriptions | Missing | [#410](https://github.com/CodeHavenX/MonoRepo/issues/410) |
| `POL-03` | Empty states for all list screens | Missing | [#410](https://github.com/CodeHavenX/MonoRepo/issues/410) |
| `POL-04` | Loading states / skeleton screens | Missing | [#410](https://github.com/CodeHavenX/MonoRepo/issues/410) |
| `POL-05` | Confirmation dialogs for all destructive actions (delete property, delete unit, remove occupant, leave org) | Missing | [#436](https://github.com/CodeHavenX/MonoRepo/issues/436) |
| `POL-06` | Discard guards — warn on unsaved edits when navigating away | Missing | [#436](https://github.com/CodeHavenX/MonoRepo/issues/436) |
| `POL-07` | Icon dropdown closing transition fix | Missing | [#352](https://github.com/CodeHavenX/MonoRepo/issues/352) |
| `POL-08` | Context-return navigation (return to correct screen after deep link entry) | Missing | [#438](https://github.com/CodeHavenX/MonoRepo/issues/438) |
| `POL-09` | Org wipe on sign-out (clear all org-scoped state from memory and local cache) | Missing | [#438](https://github.com/CodeHavenX/MonoRepo/issues/438) |
| `POL-10` | Business rules — block deletion of entities with active dependents (e.g., property with active units) | Missing | [#437](https://github.com/CodeHavenX/MonoRepo/issues/437) |
| `POL-11` | Automatic unassignment of tasks when a staff member is removed from org | Missing | [#437](https://github.com/CodeHavenX/MonoRepo/issues/437) |
| `POL-12` | End-to-end test suite (critical paths: sign-in, create task, resident home) | Missing | [#412](https://github.com/CodeHavenX/MonoRepo/issues/412) |
| `POL-13` | iOS build verification | Missing | [#220](https://github.com/CodeHavenX/MonoRepo/issues/220) |
| `POL-14` | PII/GDPR audit — verify soft-delete on all new tables, confirm erasure support flow, validate unit scoping for residents | Missing | [#414](https://github.com/CodeHavenX/MonoRepo/issues/414) |

### Bug Fixes (Phase 11)

| Issue | Description | Status |
|-------|-------------|--------|
| [#334](https://github.com/CodeHavenX/MonoRepo/issues/334) | Parallel async calls need `awaitAll` | Open |
| [#335](https://github.com/CodeHavenX/MonoRepo/issues/335) | Hardcoded strings / localization | Open |
| [#336](https://github.com/CodeHavenX/MonoRepo/issues/336) | Screen doesn't refresh on resume (UI state stale) | Open |
