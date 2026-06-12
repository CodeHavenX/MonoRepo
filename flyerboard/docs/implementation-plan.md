# FlyerBoard Front-End Implementation Plan

This document describes the work needed to bring every screen described in
`frontend.md` to its specified state, using the shared components defined in
`ui-components.md`. Changes are ordered by dependency so that each
phase can be merged independently.

Each step includes a **Validation** section that specifies exactly what must
be verified before the step is considered done:

- **UI (Composable):** one `@Preview` per distinct visual state the component
  can display. Wrap every preview in `AppTheme {}`.
- **Business logic (ViewModel / Manager):** a `*Test.kt` in `jvmTest` covering
  the happy path, the failure path, and any navigation events emitted.
- **Network mapper:** a `*Test.kt` in `jvmTest` covering each field mapping and
  the edge cases (null vs. non-null optional fields).
- **Service implementation:** a `*Test.kt` in `jvmTest` using a Ktor mock engine
  to verify the HTTP verb, path, query parameters, and request body for each
  operation.

---

## Gap Analysis

The following items are currently missing or incomplete relative to `frontend.md`:

| Area | Gap |
|---|---|
| **Submit Flyer** | No `FlyerSubmitDestination`, screen, or ViewModel exist |
| **File upload** | `FlyerEditScreen` has no file picker; `createFlyer`/`updateFlyer` fields are wired but UI is absent |
| **Archive search** | `listArchived` has no `query` param; `ArchiveUIState` / ViewModel have no search state; screen has no `SearchBar` |
| **Admin gating** | `FlyerBoardWindowUIState` has no `isAdmin` field; Moderation tab shows for all authenticated users |
| **Rejection reason** | Moderation approve/reject does not prompt for a reason; `FlyerModel` lacks `rejectionReason` |
| **Splash wordmark** | `SplashContent` shows only a `CircularProgressIndicator` — wordmark/logo is absent |
| **Submit header button** | Flyer List and My Flyers top-bar should show a **Submit** button when authenticated |
| **createUser on sign-up** | `SignUpViewModel.signUp()` does not call `UserManager.createUser()` after Supabase auth succeeds |
| **Browser navigation** | URL does not update on navigation; browser back/forward buttons do not work within the app |

---

## Phase 1 — Shared UI Components

> **Location:** `flyerboard/front-end/ui-components/src/commonMain/kotlin/com/cramsan/flyerboard/client/ui/components/`
>
> All components must be wrapped in `AppTheme {}` in their preview files.
> Follow the package convention: `com.cramsan.flyerboard.client.ui.components`.

These components are prerequisites for every screen change in later phases.

### 1.1 `StatusBadge`

**Files:** `StatusBadge.kt`, `StatusBadgePreview.kt`

```kotlin
@Composable
fun StatusBadge(status: FlyerStatus, modifier: Modifier = Modifier)
```

Maps each `FlyerStatus` to a background color and text color according to
the design spec:

| Status | Background | Text |
|---|---|---|
| APPROVED | Lime `#84CC16` | Near-black |
| PENDING | Coral `#F43F5E` | White |
| REJECTED | Red `#DC2626` | White |
| ARCHIVED | Grey `#9CA3AF` | White |

Render as a `Surface` with `RoundedCornerShape(4.dp)` containing a
`labelSmall` `Text`. The constants for these colors should live in the
component file.

**Validation:**
- `StatusBadgePreview.kt` — 4 `@Preview` composables, one per `FlyerStatus`:
  `ApprovedPreview`, `PendingPreview`, `RejectedPreview`, `ArchivedPreview`.

---

### 1.2 `FlyerCard`

**Files:** `FlyerCard.kt`, `FlyerCardPreview.kt`

```kotlin
@Composable
fun FlyerCard(
    title: String,
    description: String,
    expiresAt: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
)
```

Clickable `Card` showing:
- `titleMedium` title text
- `bodyMedium` description text capped at 2 lines
- Optional `labelSmall` expiry text in `outline` color (hidden when null)

This replaces the three near-identical private composables currently in
`FlyerListScreen`, `ArchiveScreen`, and the base of `MyFlyersScreen`.

**Validation:**
- `FlyerCardPreview.kt` — 2 `@Preview` composables:
  `FlyerCardWithExpiryPreview`, `FlyerCardNoExpiryPreview`.

---

### 1.3 `FlyerCardWithStatus`

**Files:** `FlyerCardWithStatus.kt`, `FlyerCardWithStatusPreview.kt`

```kotlin
@Composable
fun FlyerCardWithStatus(
    title: String,
    description: String,
    status: FlyerStatus,
    expiresAt: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEdit: (() -> Unit)? = null,
)
```

Extends `FlyerCard` layout by adding a `StatusBadge` alongside the title and
an **Edit** `Button` at the bottom-end when `onEdit != null` AND
`status != ARCHIVED`. This replaces the private `MyFlyerCard` in `MyFlyersScreen`.

**Validation:**
- `FlyerCardWithStatusPreview.kt` — 4 `@Preview` composables, one per status,
  showing presence/absence of the Edit button as appropriate:
  `ApprovedPreview`, `PendingPreview`, `RejectedPreview`, `ArchivedPreview`.

---

### 1.4 `ModerationFlyerCard`

**Files:** `ModerationFlyerCard.kt`, `ModerationFlyerCardPreview.kt`

```kotlin
@Composable
fun ModerationFlyerCard(
    title: String,
    description: String,
    expiresAt: String? = null,
    modifier: Modifier = Modifier,
    onApprove: () -> Unit,
    onReject: () -> Unit,
)
```

Card with title, description, optional expiry, and a row of two buttons:
- **Reject**: `OutlinedButton` with `contentColor = MaterialTheme.colorScheme.error`
- **Approve**: filled `Button`

Replaces the private `PendingFlyerCard` in `ModerationQueueScreen`.

**Validation:**
- `ModerationFlyerCardPreview.kt` — 2 `@Preview` composables:
  `ModerationFlyerCardWithExpiryPreview`, `ModerationFlyerCardNoExpiryPreview`.

---

### 1.5 `LoadingStateBox`

**Files:** `LoadingStateBox.kt`, `LoadingStateBoxPreview.kt`

```kotlin
@Composable
fun LoadingStateBox(modifier: Modifier = Modifier)
```

Fills its parent with a centered `CircularProgressIndicator`. Replaces the
same inline construct in six screens.

**Validation:**
- `LoadingStateBoxPreview.kt` — 1 `@Preview` composable inside a `Box` with
  a fixed size: `LoadingStateBoxPreview`.

---

### 1.6 `EmptyStateBox`

**Files:** `EmptyStateBox.kt`, `EmptyStateBoxPreview.kt`

```kotlin
@Composable
fun EmptyStateBox(message: String, modifier: Modifier = Modifier)
```

Fills its parent with a centered `bodyLarge` `Text`. Replaces the same
inline construct in five screens.

**Validation:**
- `EmptyStateBoxPreview.kt` — 1 `@Preview` composable with a sample message
  string: `EmptyStateBoxPreview`.

---

### 1.7 `FlyerBoardSearchBar`

**Files:** `FlyerBoardSearchBar.kt`, `FlyerBoardSearchBarPreview.kt`

```kotlin
@Composable
fun FlyerBoardSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
)
```

Uses Material3 `SearchBar` (or `OutlinedTextField` with a search icon) to
provide a consistently styled search input for the Archive screen. This
component does not exist anywhere in the codebase yet.

**Validation:**
- `FlyerBoardSearchBarPreview.kt` — 2 `@Preview` composables:
  `SearchBarEmptyPreview` (query = ""), `SearchBarWithTextPreview` (query = "summer").

---

### 1.8 `FlyerAsyncImage`

**Files:** `FlyerAsyncImage.kt`, `FlyerAsyncImagePreview.kt`

```kotlin
@Composable
fun FlyerAsyncImage(
    url: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
)
```

Coil `AsyncImage` with a fixed `4f / 3f` aspect ratio, `ContentScale.Crop`,
and a `CircularProgressIndicator` placeholder while loading. When `url` is
null, render a neutral placeholder box instead. Extracts the inline image
from `FlyerDetailBody` and will be reused by flyer cards once thumbnails are
displayed.

**Validation:**
- `FlyerAsyncImagePreview.kt` — 2 `@Preview` composables:
  `FlyerAsyncImageWithUrlPreview` (using a sample URL),
  `FlyerAsyncImageNullUrlPreview` (url = null).

---

## Phase 2 — Service and Model Layer Gaps

> **Location:** `flyerboard/front-end/app/src/commonMain/kotlin/...`

### 2.1 Add `query` to archive listing

The backend's `GET /api/v1/flyers/archive` supports a `q` query parameter.
The client does not thread it through yet.

**Files to change:**

| File | Change |
|---|---|
| `service/FlyerService.kt` | Add `query: String? = null` to `listArchived` |
| `service/impl/FlyerServiceImpl.kt` | Pass `ListFlyersQueryParams(q = query)` in the `listArchived` request |
| `managers/FlyerManager.kt` | Add `query: String? = null` to `listArchived`, forward to service |

**Validation:**
- `FlyerManagerTest` — verify `listArchived(query = "foo")` forwards the value
  to `flyerService.listArchived(query = "foo")`; verify `listArchived()` with no
  argument forwards `null`.
- `FlyerServiceImplTest` — using a Ktor mock engine, verify that a call to
  `listArchived(query = "foo")` produces a `GET` request whose URL contains
  `q=foo`; verify that `listArchived()` produces a request without a `q` param.

---

### 2.2 Add `rejectionReason` to `FlyerModel`

The `FlyerObject` response includes `rejection_reason` but it is not mapped.

**Files to change:**

| File | Change |
|---|---|
| `models/FlyerModel.kt` | Add `val rejectionReason: String?` field |
| `service/impl/FlyerNetworkMapper.kt` | Map `response.rejectionReason` → `FlyerModel.rejectionReason` |

**Validation:**
- `FlyerNetworkMapperTest` — 3 test cases:
  1. A `REJECTED` response with `rejection_reason = "Inappropriate"` maps to
     `rejectionReason = "Inappropriate"`.
  2. A `REJECTED` response with `rejection_reason = null` maps to
     `rejectionReason = null`.
  3. An `APPROVED` response with `rejection_reason = null` maps to
     `rejectionReason = null` (confirm no bleed-over from other fields).

---

### 2.3 Add `AuthState` to window state

The Moderation tab must only appear for admin users, but `FlyerBoardWindowUIState`
currently only tracks `isAuthenticated`. Two separate booleans allow the
impossible state `isAdmin=true, isAuthenticated=false`.

**Files to change:**

| File | Change |
|---|---|
| `features/window/FlyerBoardWindowUIState.kt` | Replace `isAuthenticated: Boolean` + `isAdmin: Boolean` with a sealed `AuthState` class (`Unauthenticated` / `Authenticated(isAdmin: Boolean)`) |
| `features/window/FlyerBoardWindowViewModel.kt` | After auth state changes, resolve `AuthState` by calling `listPendingFlyers(limit=1)` speculatively: success → `Authenticated(isAdmin=true)`, failure → `Authenticated(isAdmin=false)`, signed-out → `Unauthenticated` |
| `features/window/FlyerBoardWindowScreen.kt` | Read `authState` to gate the Moderation tab (`authState is Authenticated && authState.isAdmin`) and the My Flyers tap (`authState is Authenticated`) |

> **Note:** Using a sealed class makes `isAdmin=true, isAuthenticated=false`
> unrepresentable at compile time.

**Validation:**
- `FlyerBoardWindowViewModelTest` — 4 test cases:
  1. Initial `authState` is `Unauthenticated`.
  2. Auth flow emits a non-null user ID and `listPendingFlyers` succeeds →
     `authState` becomes `Authenticated(isAdmin = true)`.
  3. Auth flow emits a non-null user ID and `listPendingFlyers` fails →
     `authState` becomes `Authenticated(isAdmin = false)`.
  4. Auth flow emits null (sign-out) → `authState` reverts to `Unauthenticated`.

---

## Phase 3 — Browser Navigation

> **Primary target:** wasmJs (browser). The JVM desktop app is used for testing
> only and does not require URL handling. All browser-specific code lives in
> `wasmJsMain` with no-op `actual` stubs for other targets.

The browser must behave like a standard web app:
- Every screen has a stable, bookmarkable URL.
- Browser back/forward buttons navigate through the in-app history.
- Refreshing the page or entering a URL directly loads the correct screen.

This phase establishes the pattern. **Every new `composable()` registration
added in Phases 4 and 5 must include a matching `deepLinks` entry at the time
the route is registered** — this is part of the definition of done for each
screen from this point on.

### 3.1 URL Scheme

The canonical URL path for every destination. This table is the authoritative
reference; add new destinations here before implementing them.

| Destination | URL Path |
|---|---|
| `FlyerListDestination` | `/` |
| `FlyerDetailDestination(flyerId)` | `/flyer/{flyerId}` |
| `ArchiveDestination` | `/archive` |
| `MyFlyersDestination` | `/my-flyers` |
| `FlyerSubmitDestination` | `/my-flyers/submit` |
| `FlyerEditDestination(flyerId)` | `/my-flyers/edit/{flyerId}` |
| `ModerationQueueDestination` | `/moderation` |
| `SignInDestination` | `/sign-in` |
| `SignUpDestination` | `/sign-up` |

The Splash screen is transient and auto-navigates; it has no URL.

---

### 3.2 Deep Link Registration (Existing Routes)

Add a `deepLinks` entry to every existing `composable()` call in
`MainActivityScreen.kt` and `AuthNavGraph.kt` using the paths from 3.1.

**Pattern:**
```kotlin
composable<FlyerListDestination>(
    deepLinks = listOf(navDeepLink<FlyerListDestination>(basePath = "https://flyerboard.com/"))
) { ... }

composable<FlyerDetailDestination>(
    deepLinks = listOf(navDeepLink<FlyerDetailDestination>(basePath = "https://flyerboard.com/flyer"))
) { ... }
```

Use `https://flyerboard.com` as the base URI throughout. The host is used
internally by Navigation Compose to match destinations; the URL actually shown
in the browser address bar is controlled by the adapter in 3.3.

`FlyerSubmitDestination` does not exist yet — its deep link is registered as
part of Phase 4.3.

---

### 3.3 Browser History Adapter (wasmJs)

**Files to create:**

| File | Notes |
|---|---|
| `commonMain/navigation/BrowserNavigator.kt` | `expect class BrowserNavigator` |
| `wasmJsMain/navigation/BrowserNavigator.wasmJs.kt` | Real implementation using browser History API |
| `jvmMain/navigation/BrowserNavigator.jvm.kt` | No-op `actual` — JVM desktop ignores URL routing |

```kotlin
// commonMain
expect class BrowserNavigator {
    fun attach(navController: NavHostController)
    fun getInitialPath(): String?
}
```

The wasmJs `actual` must implement three behaviours:

1. **NavController → browser (push URL on navigation):**
   Register `addOnDestinationChangedListener` on the `NavHostController`. On
   each change, convert the destination + arguments to the canonical path from
   3.1 and call `window.history.pushState(null, "", path)`.

2. **Browser → NavController (back/forward buttons):**
   Listen for the browser `popstate` event. On each event call
   `navController.popBackStack()`.

3. **Initial path on startup:**
   `getInitialPath()` returns `window.location.pathname` when it is non-trivial
   (not `"/"` and not blank), or `null` otherwise.

**File to change:** `FlyerBoardWindowScreen.kt`

```kotlin
val navController = rememberNavController()
val browserNavigator = remember { BrowserNavigator() }

LaunchedEffect(Unit) {
    browserNavigator.attach(navController)
    browserNavigator.getInitialPath()?.let { path ->
        navController.navigate(pathToDestination(path))
    }
}
```

`pathToDestination(path: String): Destination` is a helper in `commonMain`
that parses a URL path and returns the matching typed destination. Implement
it as a `when` expression keyed on path prefix matching:

```kotlin
fun pathToDestination(path: String): Destination = when {
    path.startsWith("/flyer/")        -> FlyerDetailDestination(path.removePrefix("/flyer/"))
    path.startsWith("/my-flyers/edit/") -> FlyerEditDestination(path.removePrefix("/my-flyers/edit/"))
    path == "/my-flyers/submit"       -> FlyerSubmitDestination
    path == "/archive"                -> ArchiveDestination
    path == "/my-flyers"              -> MyFlyersDestination
    path == "/moderation"             -> ModerationQueueDestination
    path == "/sign-in"                -> SignInDestination
    path == "/sign-up"                -> SignUpDestination
    else                              -> FlyerListDestination
}
```

When a deep-linked path is detected on startup, skip the Splash screen and
navigate directly to the target destination.

**Validation:**
- Navigate Flyer List → Flyer Detail in the browser: address bar updates to `/flyer/{id}`.
- Press browser Back: returns to Flyer List, URL reverts to `/`.
- Enter `/archive` directly in the address bar: Archive screen loads without Splash.
- Refresh on `/my-flyers`: My Flyers screen reloads correctly (requires 3.4).
- JVM desktop app: launches and navigates normally with no errors (no-op adapter).

---

### 3.4 Development Server SPA Fallback

A hard refresh on `/archive` causes the browser to request that path from the
HTTP server. Without a fallback rule the server returns 404 — there is no
`/archive` file on disk.

**Development (`launcher-web` webpack config):**
```javascript
// webpack.config.d/devServer.js
config.devServer = config.devServer || {};
config.devServer.historyApiFallback = true;
```

**Production (nginx example):**
```nginx
location / {
    try_files $uri /index.html;
}
```

**Validation:**
- Run `gradle :flyerboard:launcher-web:wasmJsBrowserDevelopmentRun`, then enter
  `/archive` directly in the browser address bar — the app loads and shows the
  Archive screen.

---

## Phase 4 — New Screen: Submit Flyer

The docs specify a dedicated **Submit Flyer** screen for creating new flyers.
The Edit Flyer screen covers the _update_ path; Submit covers the _create_
path. Both share the same form layout.

### 4.1 Destination

**File:** `features/main/MainDestination.kt`

Add:
```kotlin
@Serializable
data object FlyerSubmitDestination : MainDestination()
```

No arguments are needed — the form starts empty.

---

### 4.2 Screen, ViewModel, UIState, Event, Preview

**New files under:** `features/main/flyer_submit/`

| File | Notes |
|---|---|
| `FlyerSubmitScreen.kt` | Shares the same form layout as `FlyerEditScreen`. Adds a **file picker** section (see Phase 5.7). On save calls `flyerManager.createFlyer(...)`. On success emits `NavigateBack`. On cancel emits `NavigateBack`. |
| `FlyerSubmitViewModel.kt` | Extends `BaseViewModel<FlyerSubmitEvent, FlyerSubmitUIState>`. Fields: `title`, `description`, `expiresAt`, `selectedFileBytes`, `selectedFileName`, `selectedMimeType`, `isSubmitting`, `errorMessage`. Method: `onFileSelected(bytes, name, mime)`, `submit()`, `navigateBack()`. |
| `FlyerSubmitUIState.kt` | Mirrors `FlyerEditUIState` plus `selectedFileName: String?` for display. |
| `FlyerSubmitEvent.kt` | Sealed class; start with `Noop`. |
| `FlyerSubmitPreview.kt` | Preview composable for the submit form in all relevant UI states. |

**Validation:**
- `FlyerSubmitPreview.kt` — 4 `@Preview` composables covering every distinct
  UI state:
  1. `FlyerSubmitEmptyPreview` — all fields blank, no file selected, Submit
     button enabled.
  2. `FlyerSubmitWithFilePreview` — a file name is shown in the picker section.
  3. `FlyerSubmitSavingPreview` — `isSubmitting = true`, form disabled,
     progress indicator visible.
  4. `FlyerSubmitErrorPreview` — `errorMessage` is non-null, error text visible.
- `FlyerSubmitViewModelTest` — 5 test cases:
  1. `submit()` with valid data calls `flyerManager.createFlyer(...)` and emits
     `NavigateBack` on success.
  2. `submit()` failure emits a `ShowSnackbar` window event and clears
     `isSubmitting`.
  3. `onFileSelected(bytes, name, mime)` updates `selectedFileName` in UIState.
  4. `navigateBack()` emits `NavigateBack`.
  5. `submit()` while `isSubmitting = true` is a no-op (guard against double-tap).

---

### 4.3 Register route

**File:** `features/main/MainActivityScreen.kt`

Add a `composable<FlyerSubmitDestination>` entry that renders `FlyerSubmitScreen`
and includes its deep link per Phase 3.1:

```kotlin
composable<FlyerSubmitDestination>(
    deepLinks = listOf(navDeepLink<FlyerSubmitDestination>(basePath = "https://flyerboard.com/my-flyers/submit"))
) {
    FlyerSubmitScreen(...)
}
```

**Validation:** covered by the previews and ViewModel tests in 4.2.

---

### 4.4 Navigate to Submit from My Flyers

**File:** `features/main/my_flyers/MyFlyersViewModel.kt`

Add:
```kotlin
fun onSubmitFlyer() {
    viewModelCoroutineScope.launch {
        emitWindowEvent(FlyerBoardWindowsEvent.NavigateToScreen(MainDestination.FlyerSubmitDestination))
    }
}
```

**File:** `features/main/my_flyers/MyFlyersScreen.kt`

Wire the **Submit** header button to `viewModel.onSubmitFlyer()`. Per the
frontend spec, this button appears in the top-bar of My Flyers.

**Validation:**
- `MyFlyersViewModelTest` — add 1 test case: `onSubmitFlyer()` emits
  `NavigateToScreen(FlyerSubmitDestination)`.

---

### 4.5 Navigate to Submit from Flyer List

**File:** `features/main/flyer_list/FlyerListViewModel.kt`

Add `onSubmitFlyer()` (same pattern as above).

**File:** `features/main/flyer_list/FlyerListScreen.kt`

Add a **Submit** `TextButton` to the top-bar `actions` block; only render it
when `isAuthenticated == true`.

**Validation:**
- `FlyerListViewModelTest` — add 1 test case: `onSubmitFlyer()` emits
  `NavigateToScreen(FlyerSubmitDestination)`.

---

## Phase 5 — Screen-by-Screen Updates

### 5.1 Splash Screen

**File:** `features/splash/SplashScreen.kt`

Replace the bare `CircularProgressIndicator` with a `Column` containing:
1. The app wordmark (use a `Text` with `displayLarge` typography or an image
   resource if the logo asset is available).
2. A `LoadingStateBox` (from Phase 1.5) below the wordmark.

The `SplashViewModel` already navigates to Main after one second — no VM
changes needed.

**Validation:**
- `SplashScreen.preview.kt` — 1 `@Preview` composable: `SplashPreview` showing
  the wordmark and loading indicator together.

---

### 5.2 Flyer List Screen

**File:** `features/main/flyer_list/FlyerListScreen.kt`

| Change | Detail |
|---|---|
| Replace private `FlyerCard` | Use the shared `FlyerCard` from Phase 1.2 |
| Replace loading branch | Use `LoadingStateBox` from Phase 1.5 |
| Replace empty branch | Use `EmptyStateBox` from Phase 1.6 |
| Add **Submit** button | `TextButton` in `TopAppBar` actions; visible only when `isAuthenticated` |

**Validation:**
- `FlyerListPreview.kt` — 4 `@Preview` composables covering every distinct UI
  state:
  1. `FlyerListLoadingPreview` — `isLoading = true`, loading indicator visible.
  2. `FlyerListEmptyPreview` — empty flyer list, empty-state message visible.
  3. `FlyerListAuthenticatedPreview` — flyer list populated, Submit button
     visible in the top bar.
  4. `FlyerListUnauthenticatedPreview` — flyer list populated, Submit button
     absent.
- No new ViewModel logic; existing `FlyerListViewModelTest` is sufficient after
  the 4.5 addition.

---

### 5.3 Flyer Detail Screen

**File:** `features/main/flyer_detail/FlyerDetailScreen.kt`

| Change | Detail |
|---|---|
| Replace inline `AsyncImage` | Use `FlyerAsyncImage` from Phase 1.8 |
| Replace loading branch | Use `LoadingStateBox` from Phase 1.5 |
| Replace not-found branch | Use `EmptyStateBox` from Phase 1.6 |
| Show `rejectionReason` | If the flyer has a `REJECTED` status and a non-null `rejectionReason`, display it below the description (requires Phase 2.2) |

**Validation:**
- `FlyerDetailPreview.kt` — 4 `@Preview` composables covering every distinct
  UI state:
  1. `FlyerDetailLoadingPreview` — `isLoading = true`.
  2. `FlyerDetailNotFoundPreview` — flyer is null, empty-state message visible.
  3. `FlyerDetailApprovedPreview` — approved flyer with image, no rejection
     reason section.
  4. `FlyerDetailRejectedPreview` — rejected flyer with a non-null
     `rejectionReason` displayed below the description.
- No new ViewModel logic; existing `FlyerDetailViewModelTest` is sufficient.

---

### 5.4 Archive Screen

**File:** `features/main/archive/ArchiveUIState.kt`

Add `val query: String` (default `""`).

**File:** `features/main/archive/ArchiveViewModel.kt`

Add `onQueryChanged(q: String)`:
```kotlin
fun onQueryChanged(q: String) {
    viewModelCoroutineScope.launch {
        updateUiState { it.copy(query = q) }
        loadFlyers(query = q)
    }
}
```
Update `loadFlyers()` to accept `query: String? = null` and pass it to
`flyerManager.listArchived(query = query)` (requires Phase 2.1).

**File:** `features/main/archive/ArchiveScreen.kt`

| Change | Detail |
|---|---|
| Add `FlyerBoardSearchBar` | Render it between the `TopAppBar` and the content area; bind to `uiState.query` and `viewModel.onQueryChanged` |
| Replace private `ArchivedFlyerCard` | Use the shared `FlyerCard` from Phase 1.2 |
| Replace loading branch | Use `LoadingStateBox` from Phase 1.5 |
| Replace empty branch | Use `EmptyStateBox` from Phase 1.6 |

**Validation:**
- `ArchivePreview.kt` — 4 `@Preview` composables covering every distinct UI
  state:
  1. `ArchiveLoadingPreview` — `isLoading = true`.
  2. `ArchiveEmptyPreview` — empty list, empty-state message visible, search
     bar present.
  3. `ArchiveWithResultsPreview` — populated list, search bar with empty query.
  4. `ArchiveWithQueryPreview` — populated list, search bar showing an active
     query string.
- `ArchiveViewModelTest` — add 2 test cases:
  1. `onQueryChanged("foo")` updates `uiState.query` to `"foo"` and calls
     `flyerManager.listArchived(query = "foo")`.
  2. `onQueryChanged("")` updates `uiState.query` to `""` and calls
     `flyerManager.listArchived(query = "")`.

---

### 5.5 My Flyers Screen

**File:** `features/main/my_flyers/MyFlyersScreen.kt`

| Change | Detail |
|---|---|
| Replace private `MyFlyerCard` | Use `FlyerCardWithStatus` from Phase 1.3 |
| Replace loading branch | Use `LoadingStateBox` from Phase 1.5 |
| Replace empty branch | Use `EmptyStateBox` from Phase 1.6 |
| Add **Submit** header button | `TextButton` in `TopAppBar` actions; calls `viewModel.onSubmitFlyer()` (Phase 4.4) |

**Validation:**
- `MyFlyersPreview.kt` — 4 `@Preview` composables covering every distinct UI
  state:
  1. `MyFlyersLoadingPreview` — `isLoading = true`.
  2. `MyFlyersEmptyPreview` — empty list, empty-state message visible, Submit
     button present.
  3. `MyFlyersWithFlyersPreview` — list with flyers in mixed statuses (PENDING,
     APPROVED, REJECTED, ARCHIVED), showing correct badge and Edit button
     presence per status.
  4. `MyFlyersArchivedOnlyPreview` — list where all flyers are ARCHIVED, Edit
     buttons absent.
- No new ViewModel logic beyond 4.4; `MyFlyersViewModelTest` additions are
  covered there.

---

### 5.6 Moderation Queue Screen

**File:** `features/main/moderation_queue/ModerationQueueScreen.kt`

| Change | Detail |
|---|---|
| Replace private `PendingFlyerCard` | Use `ModerationFlyerCard` from Phase 1.4 |
| Replace loading branch | Use `LoadingStateBox` from Phase 1.5 |
| Replace empty branch | Use `EmptyStateBox` from Phase 1.6 |
| Add rejection reason dialog | On **Reject** tap, show an `AlertDialog` with a `TextField` for the reason. Confirm calls `viewModel.rejectFlyer(flyerId, reason)` |

**File:** `features/main/moderation_queue/ModerationQueueViewModel.kt`

Change `rejectFlyer` signature to accept an optional `reason: String`:
```kotlin
fun rejectFlyer(flyerId: FlyerId, reason: String = "") {
    viewModelCoroutineScope.launch {
        flyerManager.moderate(flyerId, action = "reject", reason = reason)
        ...
    }
}
```

> **Service / Manager:** The `moderate` method currently takes only `action: String`.
> Add an optional `reason: String? = null` parameter and include it in the
> `ModerationActionNetworkRequest` body if non-null.

**Validation:**
- `ModerationQueuePreview.kt` — 4 `@Preview` composables covering every
  distinct UI state:
  1. `ModerationQueueLoadingPreview` — `isLoading = true`.
  2. `ModerationQueueEmptyPreview` — no pending flyers, empty-state message.
  3. `ModerationQueueWithFlyersPreview` — list of pending flyer cards.
  4. `ModerationQueueRejectDialogPreview` — list visible behind an open
     `AlertDialog` containing the reason `TextField`.
- `ModerationQueueViewModelTest` — add 2 test cases:
  1. `rejectFlyer(flyerId, reason = "Inappropriate")` calls
     `flyerManager.moderate(flyerId, action = "reject", reason = "Inappropriate")`.
  2. `rejectFlyer(flyerId)` (no reason) calls
     `flyerManager.moderate(flyerId, action = "reject", reason = "")`.

---

### 5.7 File Upload (Flyer Edit + Submit)

File upload is a platform-specific capability. The service layer already
accepts `fileBytes: ByteArray`, `fileName: String`, `mimeType: String` — the
gap is the UI file-picker.

**Approach:**

1. Declare an `expect` function (or interface) in `app`'s `commonMain`:

```kotlin
// FilePicker.kt
expect class FilePicker {
    suspend fun pickFile(): PickedFile?
}

data class PickedFile(val bytes: ByteArray, val name: String, val mimeType: String)
```

2. Provide `actual` implementations per target:
   - **wasmJs:** Use the browser File API via JS interop (`<input type="file">`).
   - **Android:** Use `ActivityResultContracts.GetContent`.
   - **JVM:** Use `javax.swing.JFileChooser`.

3. Add a **Choose File** `Button` to `FlyerEditScreen` and `FlyerSubmitScreen`.
   Display the chosen file name once selected.

4. Wire the selected file into the ViewModel (`onFileSelected(bytes, name, mime)`).

**Files to create/change:**

| File | Change |
|---|---|
| `commonMain/FilePicker.kt` | `expect class FilePicker` + `PickedFile` |
| `wasmJsMain/FilePicker.wasmJs.kt` | Browser file-input implementation |
| `androidMain/FilePicker.android.kt` | `ActivityResultContracts.GetContent` implementation |
| `jvmMain/FilePicker.jvm.kt` | Swing `JFileChooser` implementation |
| `FlyerEditScreen.kt` | Add **Choose File** section, call `viewModel.onFileSelected(...)` |
| `FlyerEditUIState.kt` | Add `selectedFileName: String?` for display |
| `FlyerEditViewModel.kt` | Add `onFileSelected(bytes, name, mime)`, update `saveFlyer` to pass file data when provided |
| `FlyerSubmitScreen.kt` | Same as above (already part of Phase 4) |

**Validation:**
- `FlyerEditPreview.kt` — add 2 `@Preview` composables for the new picker
  states:
  1. `FlyerEditNoFilePreview` — "Choose File" button visible, no file name shown.
  2. `FlyerEditWithFilePreview` — a file name displayed after selection.
- `FlyerEditViewModelTest` — add 2 test cases:
  1. `onFileSelected(bytes, "photo.jpg", "image/jpeg")` updates
     `uiState.selectedFileName` to `"photo.jpg"`.
  2. `saveFlyer()` after `onFileSelected` includes `fileBytes`, `fileName`, and
     `mimeType` in the `flyerManager.updateFlyer(...)` call.
- `FlyerSubmitViewModelTest` — the file-selection tests from 4.2 already cover
  the submit path; no additional cases needed here.

---

## Phase 6 — Auth Flow Fixes

### 6.1 Call `createUser` after sign-up

After a successful Supabase Auth sign-up, the backend expects a `POST /user`
call to register the display name. Without it, the user has an auth account
but no profile row.

**File:** `features/auth/sign_up/SignUpViewModel.kt`

After `authManager.signUp(email, password).onSuccess`:

```kotlin
userManager.createUser(firstName = firstName, lastName = lastName)
    .onFailure { /* log; non-fatal */ }
```

The current sign-up form has no first/last name fields. The plan recommends
adding them to keep the backend data complete:

| File | Change |
|---|---|
| `sign_up/SignUpUIState.kt` | Add `firstName: String`, `lastName: String` |
| `sign_up/SignUpScreen.kt` | Add two `OutlinedTextField` entries |
| `sign_up/SignUpViewModel.kt` | `onFirstNameChanged`, `onLastNameChanged`, pass to `userManager.createUser` |

**Validation:**
- `SignUpPreview.kt` — add 2 `@Preview` composables for the updated screen:
  1. `SignUpEmptyPreview` — all fields blank including the new name fields.
  2. `SignUpFilledPreview` — all fields populated, ready to submit.
- `SignUpViewModelTest` — add 2 test cases:
  1. Successful `signUp()` calls `userManager.createUser(firstName, lastName)`
     after `authManager.signUp` succeeds, then emits the navigation event.
  2. `userManager.createUser` failure after a successful `authManager.signUp`
     does not block navigation — the user is still sent to the main graph.

---

### 6.2 Admin role check in window ViewModel

See Phase 2.3. The window ViewModel should check admin status after the
session is established (sign-in success or app startup when a session is
already active). This is fully covered by the `FlyerBoardWindowViewModelTest`
added in Phase 2.3.

---

## Navigation Reference

The table below summarises every navigation edge and the argument carried
across it, for use when reviewing route registrations.

| From | Action | To | Argument |
|---|---|---|---|
| SplashScreen | auto (1 s) | Main graph | — |
| SignInScreen | Sign In success | Main graph (clear stack) | — |
| SignInScreen | "Create Account" | SignUpScreen | — |
| SignUpScreen | Sign Up success | Main graph (clear stack) | — |
| SignUpScreen | "Back" | SignInScreen | — |
| FlyerListScreen | tap card | FlyerDetailScreen | `flyerId: String` |
| FlyerListScreen | "Submit" (auth) | FlyerSubmitScreen | — |
| FlyerListScreen | "Sign In" (unauth) | Auth graph | — |
| FlyerDetailScreen | back | previous screen | — |
| ArchiveScreen | tap card | FlyerDetailScreen | `flyerId: String` |
| MyFlyersScreen | tap card | FlyerDetailScreen | `flyerId: String` |
| MyFlyersScreen | "Edit" | FlyerEditScreen | `flyerId: String` |
| MyFlyersScreen | "Submit" | FlyerSubmitScreen | — |
| FlyerEditScreen | save success | back | — |
| FlyerEditScreen | back/cancel | back | — |
| FlyerSubmitScreen | submit success | back (My Flyers) | — |
| FlyerSubmitScreen | cancel/back | back | — |
| ModerationQueueScreen | approve/reject | refreshes in place | — |
| Bottom nav → My Flyers (unauth) | tap | Auth graph | — |
| Bottom nav → Moderation (non-admin) | tap | Auth graph | — |

`flyerId` is passed as a raw `String` (the `.flyerId` string value of a
`FlyerId`) and is the only non-trivial argument in the graph. It is already
declared as `data class FlyerDetailDestination(val flyerId: String)` and
`data class FlyerEditDestination(val flyerId: String)` in `MainDestination.kt`.
`FlyerSubmitDestination` is a `data object` — no argument needed.

---

## Implementation Order Summary

| Phase | What | Prerequisite |
|---|---|---|
| 1 | 8 ui-components components + previews | — |
| 2 | Service/model layer: archive query, rejectionReason, AuthState | — |
| 3 | Browser navigation: URL scheme, deep links, History adapter, SPA fallback | — |
| 4 | FlyerSubmit screen (5 files) + destination + route | Phases 1, 3 |
| 5.1 | Splash wordmark | Phase 1 |
| 5.2 | FlyerList updates (shared components + Submit button) | Phases 1, 4 |
| 5.3 | FlyerDetail updates (FlyerAsyncImage, LoadingStateBox, rejectionReason) | Phases 1, 2 |
| 5.4 | Archive search bar + shared components | Phases 1, 2 |
| 5.5 | MyFlyers updates (shared components + Submit button) | Phases 1, 4 |
| 5.6 | ModerationQueue updates (shared components + rejection dialog) | Phases 1, 2 |
| 5.7 | File upload (FilePicker + FlyerEdit/Submit wiring) | Phase 4 |
| 6 | Auth fixes (createUser, isAdmin) | Phase 2 |
