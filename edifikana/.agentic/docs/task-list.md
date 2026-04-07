# Task List

## Task: A1
Title: Units Backend — Entity, Datastore, and Shared Models
Description: Verify units table migration (DB-01); add UnitModel.kt, UnitId.kt, and unit network request/response models (MDL-01a); implement SupabaseUnitDatastore.kt implementing UnitDatastore interface.
Dependencies: none

## Task: A2
Title: Units Backend — Service and RBAC Integration
Description: Implement UnitService.kt enforcing org membership and role checks on all methods; extend per-service authorization to enforce unit-level access; write service unit tests.
Dependencies: A1

## Task: A3
Title: Units Backend — API and Controller
Description: Define UnitApi.kt CRUD endpoints (API-01a); implement UnitController.kt with HTTP routing and request/response mapping (BE-01a); register in DI modules; write unit and integration tests.
Dependencies: A2

## Task: A4
Title: Common Areas Backend — Client Services
Description: Implement CommonAreaService.kt (client-side HTTP client wrapping CommonAreaApi) and CommonAreaManager.kt (business logic and network-to-model mapping) for AREA-03; backend stack already done.
Dependencies: none

## Task: A5
Title: RLS Blanket Deny and Security Hardening
Description: Drop all permissive FOR ALL RLS policies from units, common_areas, unit_occupants, tasks, documents, rent_config, and payment_records (DB-11); fix RLS gaps on existing tables (#372); fix race condition in user-org association (#396); drop historical permissive policies from pre-blanket-deny migrations (#457).
Dependencies: none

## Task: A6
Title: Remove org_id Denormalization
Description: Remove org_id column from units, tasks, unit_occupants, rent_config, and payment_records tables; update DB migrations, entity classes, shared models, and backend services accordingly (DB-12).
Dependencies: A1

## Task: A7
Title: Units Frontend — Manager and Service
Description: Implement UnitService.kt (client-side CRUD via UnitApi returning Result<T>) and UnitManager.kt (business logic and UnitNetworkResponse-to-UnitModel mapping) for UNIT-01 and UNIT-02.
Dependencies: A3

## Task: A8
Title: Units Frontend — List and Detail Screens
Description: Build Unit List screen within Property Detail Units tab with UnitListViewModel and UnitListUIState (UNIT-03, UNIT-04); build Unit Detail screen with six tabs (Info, Occupants, Financials, Guests, Tasks, Documents) and UnitDetailViewModel (UNIT-05, UNIT-06); update PropertyDetailScreen to include Units and Common Areas tabs (UNIT-09).
Dependencies: A7

## Task: A9
Title: Units Frontend — Add and Edit Screen
Description: Build Add/Edit Unit modal screen (unit number, floor, bedrooms, bathrooms, sq ft) and AddEditUnitViewModel (UNIT-07, UNIT-08).
Dependencies: A7

## Task: A10
Title: Common Areas Frontend — Detail Screen
Description: Build Common Area Detail screen (name, type, tasks tab) and CommonAreaDetailViewModel (AREA-01, AREA-02).
Dependencies: A4

## Task: B1
Title: Tasks Backend — Service with Status and Assignment Logic
Description: Implement TaskService.kt enforcing org membership and role checks; handle status transitions (Open -> InProgress -> Completed/Cancelled); use UserId from org membership for assignment (not legacy EmployeeId); write service unit tests. Task model and datastore are already done (BE-02 service portion).
Dependencies: A1

## Task: B2
Title: Tasks Backend — API and Controller
Description: Define TaskApi.kt with CRUD and filter by org/property/unit/status/assignee/priority (API-02); implement TaskController.kt (BE-02 controller portion); register in DI modules; write unit and integration tests.
Dependencies: B1

## Task: B3
Title: Missing Network DTOs — Tasks
Description: Define TaskListNetworkResponse, CreateTaskNetworkRequest, and UpdateTaskNetworkRequest; ensure TaskStatus and TaskPriority enums are used (never raw String).
Dependencies: none

## Task: B4
Title: Missing Network DTOs — Occupants
Description: Define OccupantListNetworkResponse, AddOccupantNetworkRequest, and UpdateOccupantNetworkRequest; ensure OccupantType and OccupancyStatus enums are used (never raw String).
Dependencies: none

## Task: B5
Title: Tasks Frontend — Manager
Description: Implement TaskService.kt (client-side wrapping TaskApi returning Result<T>) and TaskManager.kt (create, update, status transitions, filtering logic) for TASK-01 and TASK-02.
Dependencies: B2

## Task: B6
Title: Tasks Frontend — List Screen with Filtering
Description: Build Task List screen filterable by property, unit, status, assignee, and priority with TaskListViewModel (TASK-03, TASK-04); update bottom nav to include Tasks tab as [Dashboard][Properties][Tasks][More] (TASK-10).
Dependencies: B5

## Task: B7
Title: Tasks Frontend — Detail, Create, and Edit Screens
Description: Build Task Detail screen with status update, reassign, and property/unit view plus TaskDetailViewModel (TASK-05, TASK-06); build Add/Edit Task screen with property/unit/area picker, priority, due date, and assignee plus AddEditTaskViewModel (TASK-07, TASK-08); build Staff Picker bottom sheet for assignee selection (TASK-09).
Dependencies: B5

## Task: B8
Title: Occupants Backend — Entity and Datastore
Description: Verify unit_occupants table migration (DB-03) and OccupantModel (MDL-04) which are already done; implement SupabaseOccupantDatastore.kt implementing OccupantDatastore interface.
Dependencies: none

## Task: B9
Title: Occupants Backend — Service with Unit Scoping
Description: Implement OccupantService.kt enforcing unit-level scoping; provide getUnitsForUser(userId) for resident RBAC lookups; enforce soft-delete (set Status to INACTIVE on removal) and single isPrimary per unit at service layer (BE-04 service portion).
Dependencies: B8, A1

## Task: B10
Title: Occupants Backend — API and Controller
Description: Define OccupantApi.kt (add occupant, list by unit, update, deactivate) (API-04); implement OccupantController.kt (BE-04 controller portion); register in DI modules; write unit and integration tests.
Dependencies: B9

## Task: C1
Title: Financial Network DTOs
Description: Define RentConfigNetworkResponse, CreateRentConfigNetworkRequest, UpdateRentConfigNetworkRequest, PaymentRecordNetworkResponse, and CreatePaymentRecordNetworkRequest; model period_month as first-of-month LocalDate; use PaymentType and PaymentStatus enums (MDL-06 network DTOs portion).
Dependencies: none

## Task: C2
Title: RentConfig Full Backend
Description: Verify rent_config table migration (DB-06); implement RentConfigDatastore, RentConfigService (admin-only write enforcement), and RentConfigController with API endpoints for get and set per unit (API-06 and BE-06 rent config portion); write unit and integration tests.
Dependencies: C1, A1

## Task: C3
Title: PaymentRecord Full Backend
Description: Verify payment_records table migration (DB-07); implement PaymentRecordDatastore, PaymentRecordService, and PaymentRecordController with API endpoints for list by unit/period, create, and update status (API-06 and BE-06 payment record portion); write unit and integration tests.
Dependencies: C1, A1

## Task: C4
Title: Financial Frontend — All Screens
Description: Implement PaymentRecordService.kt and PaymentManager.kt (client-side); build Financial Overview screen (per-unit rent status, HOA, utilities summary) with FinancialOverviewViewModel; build Payment List screen (history by period) with PaymentListViewModel; build Add Payment Record screen (type, month/year picker, amount) with AddPaymentRecordViewModel; build Rent Config management screen (admin only); implement overdue status computed from current date vs due_date (FIN-01 through FIN-09).
Dependencies: C2, C3

## Task: C5
Title: Occupants Frontend — Management Screens
Description: Implement OccupantService.kt and OccupantManager.kt (client-side); build Occupant List screen within Unit Detail Occupants tab with OccupantListViewModel; build Add Occupant form with AddOccupantViewModel; build Occupant Detail screen and Remove Occupant confirmation flow (OCC-01 through OCC-07).
Dependencies: B10

## Task: D1
Title: RBAC Extension — Resident Unit Scoping
Description: Add RBACService.hasResidentAccessToUnit(context, unitId) querying OccupantService.getUnitsForUser(userId); extend all resident-facing service methods to enforce unit scoping before data access; write unit tests for resident unit scoping.
Dependencies: B9

## Task: D2
Title: Role-Based Navigation Routing
Description: After sign-in, detect org role from user_organization_mapping and branch navigation: Admin/Staff go to Dashboard nav graph; Resident goes to Resident nav graph with bottom nav [Home][Requests][Documents][Profile] (RES-02).
Dependencies: D1

## Task: D3
Title: Resident Home and My Unit Screens
Description: Build Resident Home screen (unit card, quick actions) with ResidentHomeViewModel (RES-03, RES-04); build My Unit screen (read-only unit info) with MyUnitViewModel (RES-05, RES-06).
Dependencies: D2, A8

## Task: D4
Title: Resident Documents Screen
Description: Build My Documents screen (read-only, filtered to user unit and property) with MyDocumentsViewModel (RES-12, RES-13); wire Resident bottom nav [Home][Requests][Documents][Profile] (RES-14).
Dependencies: D3

## Task: D5
Title: Resident Profile and Settings
Description: Build Profile Edit screen (name, avatar) with ViewModel (SET-01); build Change Password screen verifying current password via auth.signInWith() before update (SET-02); build Delete Account flow initiating GDPR erasure request via support flow (SET-03).
Dependencies: D2

## Task: E1
Title: Notification Preferences Frontend
Description: Build settings screen for toggling in-app notification preferences per event type; backend notification infrastructure already exists; scope is in-app preferences only with no push, email, or external delivery channels.
Dependencies: none

## Task: E2
Title: GDPR Audit and Authorization Hardening
Description: Audit every new backend Service method to verify org membership check, role check, and resident unit scoping are enforced; confirm soft-delete (deleted_at) on all new tables; confirm RBAC authorization lookups exclude soft-deleted records; document support-flow data erasure process (POL-14).
Dependencies: D1, C4, B7, B10

## Task: E3
Title: MVP Polish — Accessibility, Empty States, and Loading States
Description: WCAG AA compliance pass with 44-48px touch targets, visible focus rings, and content descriptions on all interactive elements (POL-02); add empty states for all list screens (POL-03); add loading states and skeleton screens for async data loads (POL-04).
Dependencies: C4, B7, D4

## Task: E4
Title: Confirmation Dialogs and Discard Guards
Description: Add confirmation dialogs for all destructive actions including delete property, delete unit, remove occupant, leave org, and delete document (POL-05); add discard guards warning on unsaved edits when navigating away from add/edit screens (POL-06).
Dependencies: C4, B7, D4

## Task: E5
Title: Deep Links and Navigation Context
Description: Wire all deep links through nav graph for edifikana://invite/{token}, edifikana://org-invite/{token}, and edifikana://reset (POL-01); implement context-return navigation after deep link flows (POL-08); implement org wipe on sign-out clearing all org-scoped state (POL-09).
Dependencies: D2

## Task: E6
Title: Business Rules — Deletion Blocking and Unassignment
Description: Block deletion of entities with active dependents (property with active units, unit with active occupants) at the service layer (POL-10); automatically unassign tasks when a staff member is removed from the org (POL-11).
Dependencies: B1, B9

## Task: E7
Title: End-to-End Tests and iOS Verification
Description: Build E2E test suite covering critical paths: sign-in to dashboard, create task, and resident home (POL-12); verify all new screens compile and render correctly on iOS targets (POL-13).
Dependencies: E3, E4, E5

## Task: E8
Title: Bug Fixes
Description: Fix parallel async calls to use awaitAll/coroutineScope pattern (#334); replace hardcoded strings with localization keys (#335); fix screens not refreshing on resume (#336); fix icon dropdown closing transition (#352).
Dependencies: none
