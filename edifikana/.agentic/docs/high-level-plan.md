# High-Level Plan

## Summary

Edifikana's core platform infrastructure (auth, org/property management, team invites, documents, notifications, event logging) is already implemented — roughly 60% of the MVP foundation. The database schemas and shared domain models for the remaining features (units, tasks, financials, occupants, residents) are also defined. The primary work ahead is building out the **backend service layer**, **API definitions**, and **frontend UI** for these features, then adding the resident-facing experience.

The approach is to build vertically — completing each feature domain end-to-end (datastore → service → controller → API → frontend) before moving to the next — following the established Controller → Service → Datastore (backend) and Feature → Manager → Service (frontend) patterns.

**Existing screens usable toward MVP: ~16 of 48**
**New screens to build: ~32**

---

## Phase 0: Database & API Foundation

*All phases depend on this. Build first.*

Establishes the database schema, API contracts, backend controllers/services/datastores, and shared data models for every new domain entity. Most database tables are already created; remaining work is RLS hardening, API interface definitions, and backend service implementations for units, tasks, financials, occupants, and documents.

**Key deliverables:**
- Schema migrations for `units`, `common_areas`, `unit_occupants`, `tasks`, `rent_config`, `payment_records`, `documents`, `user_organization_mapping` extensions
- RLS blanket-deny enforcement on all new and existing tables
- API contracts: `UnitApi`, `TaskApi`, `OccupantApi`, `PaymentRecordApi`, `DocumentApi`, `OrganizationApi` extensions
- Backend stack for each new entity: Controller + Service + Datastore
- Shared KMP models: domain types, ID value classes, status/priority enums

**Architectural decision:** Dedicated domain tables (`tasks`, `maintenance_requests`, `guests`) rather than a shared polymorphic table. Provides better type safety, cleaner RLS policies per entity, and clearer query patterns.

---

## Phase 1: Auth & Onboarding Completion

*Closes remaining holes in the authentication flow.*

Existing auth screens (Sign In, Sign Up, OTP, Select Org, Create New Org) are done. This phase completes:

- **Password reset flow:** Email entry screen, confirmation screen, ViewModel, and deep link handler for `/reset/{token}`
- **Invitation accept flow:** Accept screen with account creation form, ViewModel, deep link handlers for `/invite/{token}` and `/org-invite/{token}`, existing-account vs. new-account paths
- **Organization onboarding gaps:** My Organizations screen, Organization Detail screen, Leave Organization flow, Transfer Ownership screen, Join Organization via invite code

Security items: correct default role on sign-up, CAPTCHA on sign-in/sign-up.

---

## Phase 2: Dashboard

*Admin and Staff home screen.*

Replaces `OrganizationHomeScreen` with a proper dashboard as the default home destination for Admin and Staff users. Introduces the `[Dashboard] [Properties] [Tasks] [More]` bottom navigation.

- 2×2 metric grid: Properties, Units, Pending Tasks, Overdue Payments
- Pending tasks mini-list (3 most urgent open tasks)
- FAB for quick task creation
- Navigation from metric cards to filtered lists
- Aggregated stats loaded concurrently from PropertyManager, TaskManager, PaymentManager

---

## Phase 3: Unit Management

*Most complex new domain. Everything else references units.*

Builds the complete Unit Management domain: listing units within a property, viewing unit details across six tabs (Info, Occupants, Financials, Guests, Tasks, Documents), add/edit units, and common area management.

- **Property screens refactor:** Properties Overview, Property Detail/Home screen
- **Unit screens:** Unit List (within Property Detail > Units tab), Unit Detail (tabbed), Add/Edit Unit modal
- **Common areas:** Common Area Detail screen, `CommonAreaDetailViewModel`, `CommonAreaService`, `CommonAreaManager`
- **Backend:** `UnitController` + `UnitService` + `UnitDatastore`; `CommonAreaController` + `CommonAreaService` + `CommonAreaDatastore`

---

## Phase 4: Task Management

*Primary day-to-day workflow for Admin and Staff.*

Builds a global task system for admin/staff to create, assign, prioritize, and track work items tied to units or common areas.

- **Backend:** `TaskController` + `TaskService` (status transitions, assignment logic) + `TaskDatastore`
- **Admin/Staff screens:** Task List (filterable by property/unit/status/assignee/priority), Task Detail, Add/Edit Task, Staff Picker bottom sheet
- **Maintenance request review:** Request Review List (admin: pending resident requests), Approve → pre-filled Add Task flow, Reject confirmation

---

## Phase 5: Occupant Management

*Per-unit occupant tracking for Admin.*

Manages the residents and owners associated with each unit.

- **Backend:** `OccupantController` + `OccupantService` (with unit scoping enforcement) + `OccupantDatastore`
- **Screens:** Occupant List (within Unit Detail > Occupants tab), Add Occupant form, Occupant Detail, Remove Occupant confirmation
- **Data rules:** Soft-delete only (removed occupant set to `INACTIVE`); one `isPrimary` occupant per unit at a time

---

## Phase 6: Guest Management *(Deferred from MVP)*

Guest check-in/check-out workflows and visit history per unit are deferred from the initial MVP release. Database table (`guests`) is created in Phase 0 to avoid future migrations. Implementation is planned post-MVP.

---

## Phase 7: Financial Tracking

*Per-unit payment status recording. No reports, invoicing, or aggregation.*

Records rent, HOA dues, and utility payments per unit with payment history.

- **Backend:** `PaymentRecordController` + `PaymentRecordService` + `PaymentRecordDatastore`; `RentConfigController` + `RentConfigService` + `RentConfigDatastore`
- **Screens:** Financial Overview (per unit: rent status, HOA, utilities summary), Payment List (history by period), Add Payment Record, Rent Config management (admin only)
- **Scope constraint:** Recording and viewing per-unit payment status only — no reports, exports, invoicing, or cross-unit aggregation

---

## Phase 8: Document Management

*Wraps existing Supabase Storage integration.*

`StorageManager` and `StorageService` already exist. Document management builds on top.

- **Frontend:** `DocumentService` + `DocumentManager` (wraps `StorageManager`); Document Library screen (browse by property/unit, filter by type); Document Viewer; Upload Document screen
- **Integration:** Wire document access into Unit Detail > Documents tab and Property Detail > Documents tab

---

## Phase 9: Resident Experience

*Separate navigation flow and screens for Resident-role users.*

Residents have a different app experience from Admin/Staff: a simplified bottom nav `[Home] [Documents] [Profile]` scoped to their unit.

- **Navigation:** Role-based routing after auth — detect Resident role → resident-specific nav graph
- **Home:** Resident Home screen (unit card, quick actions), My Unit screen (read-only unit info)
- **Documents:** My Documents screen (read-only, scoped to user's unit and property)
- **Profile:** Basic account management

---

## Phase 10: Settings & Profile Gaps

*Completes the Settings surface.*

- Profile editing (name, avatar, password change)
- Team management refactor (invite member, change role, remove member)
- Settings Home screen and More tab wiring
- Staff simplified navigation (Staff users see a reduced nav without admin-only items)
- Organization context card in settings

---

## Phase 11: MVP Polish

*Cross-cutting quality pass before release.*

- Deep link wiring for all invitation and reset flows
- Accessibility: WCAG AA compliance, 44–48px touch targets, visible focus rings
- Empty states and loading states for all list screens
- Confirmation dialogs for all destructive actions
- Context-return navigation (navigating back to correct screen after deep link entry)
- End-to-end test suite
- iOS build verification
- PII/GDPR audit: verify soft-delete coverage, confirm data erasure support flow, validate authorization enforcement across all new service methods

---

## Key Technical Decisions & Constraints

| Decision | Rationale |
|----------|-----------|
| Build units first | Tasks, financials, and occupants all depend on unit references |
| Vertical feature slices | Reduces integration risk; each feature is testable end-to-end before starting the next |
| Dedicated domain tables | Better type safety and cleaner RLS per entity vs. shared polymorphic table |
| Domain types everywhere | All new code must use ID value classes, enums, and `kotlinx.datetime.Instant` — never raw primitives |
| RLS blanket deny maintained | All new datastores use the service role key; authorization stays in the Kotlin service layer |
| Soft-delete only | All new CRUD operations must use `deleted_at` pattern — no hard deletes |
| Integration tests required | Every new backend service must have integration tests against real Supabase before merging |
| Rate limiting at Cloudflare | Application-level rate limiting is not implemented; Cloudflare enforces this at the edge |

---

## Risks & Open Questions

1. **Missing network DTOs for financials:** `RentConfigModel` and `PaymentRecordModel` need `NetworkResponse`/`NetworkRequest` counterparts before backend work begins.
2. **Resident role routing:** The current frontend navigation has no resident-specific navigation graph. Role-based routing after login needs to be designed.
3. **Unit scoping for residents:** `OccupantService` must provide a reliable `user_id → unit_id` lookup that every resident-facing service method can use.
4. **Task assignment model:** Tasks have an `assignee_id` referencing a user. Interaction with org membership, reassignment, and notification triggers needs definition.
5. **Payment period constraints:** `payment_records` enforces `period_month` as first-of-month only. Frontend needs month/year selection, not arbitrary date picking.
6. **Employee vs. User model overlap:** Both `employees` (property-scoped) and `user_organization_mapping` (org-scoped) exist. Task assignment should use `UserId` from org membership — clarify whether `employees` is being phased out.
7. **Integration test infrastructure:** New features will need seed data and test scenarios. The existing seed data script may need updates.
