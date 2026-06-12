# FlyerBoard Screen Documentation Index

This folder contains one detailed implementation document per screen. Each
document covers the full stack: UI layout, shared components, UIState,
ViewModel, Manager, and Service layer — including what currently exists, what
needs to change, and what must be created from scratch.

---

## Reading order

The documents are numbered in the order a developer should tackle them.
Phases 1 and 2 (shared components and service/model gaps) must be complete
before starting any screen work.

| Doc | Screen | Route | Status |
|---|---|---|---|
| [01-splash.md](01-splash.md) | Splash | `SplashNavGraphDestination` | Mostly done — needs wordmark |
| [02-sign-in.md](02-sign-in.md) | Sign In | `AuthDestination.SignInDestination` | Complete |
| [03-sign-up.md](03-sign-up.md) | Sign Up | `AuthDestination.SignUpDestination` | Needs `createUser` + name fields |
| [04-flyer-list.md](04-flyer-list.md) | Flyer List | `MainDestination.FlyerListDestination` | Needs Submit button + component swap |
| [05-flyer-detail.md](05-flyer-detail.md) | Flyer Detail | `MainDestination.FlyerDetailDestination` | Needs component swap + rejection reason |
| [06-archive.md](06-archive.md) | Archive | `MainDestination.ArchiveDestination` | Needs search bar + service query param |
| [07-my-flyers.md](07-my-flyers.md) | My Flyers | `MainDestination.MyFlyersDestination` | Needs Submit button + component swap |
| [08-submit-flyer.md](08-submit-flyer.md) | Submit Flyer | `MainDestination.FlyerSubmitDestination` | **New — all files to create** |
| [09-edit-flyer.md](09-edit-flyer.md) | Edit Flyer | `MainDestination.FlyerEditDestination` | Needs file picker |
| [10-moderation-queue.md](10-moderation-queue.md) | Moderation Queue | `MainDestination.ModerationQueueDestination` | Needs rejection dialog + admin gating |

## Cross-cutting documents

| Doc | Topic |
|---|---|
| [11-file-picker.md](11-file-picker.md) | Platform-specific file picker used by Submit and Edit screens |
| [12-window-layer.md](12-window-layer.md) | Window ViewModel, admin role detection, session management |

---

## Dependency map

```
Phase 1: ui-components components
  └── StatusBadge, FlyerCard, FlyerCardWithStatus, ModerationFlyerCard,
      LoadingStateBox, EmptyStateBox, FlyerBoardSearchBar, FlyerAsyncImage

Phase 2: service / model gaps
  └── FlyerModel.rejectionReason, FlyerService.listArchived(query),
      FlyerService.moderate(reason), FlyerBoardWindowUIState.isAdmin

Phase 3: new screen
  └── FlyerSubmitDestination + 5 feature files

Phase 4: screen updates (all require Phase 1)
  ├── 01 Splash       → Phase 1 only
  ├── 04 Flyer List   → Phase 1 + Phase 3
  ├── 05 Flyer Detail → Phase 1 + Phase 2 (rejectionReason)
  ├── 06 Archive      → Phase 1 + Phase 2 (query param)
  ├── 07 My Flyers    → Phase 1 + Phase 3
  ├── 09 Edit Flyer   → Phase 1 + FilePicker (11)
  ├── 08 Submit Flyer → Phase 1 + Phase 3 + FilePicker (11)
  └── 10 Moderation   → Phase 1 + Phase 2 (reject reason) + Window (12)

Phase 5: auth fixes
  ├── 03 Sign Up → createUser call
  └── 12 Window  → isAdmin detection
```

---

## Key architectural conventions

- Every screen follows the 5-file pattern:
  `Screen.kt`, `ViewModel.kt`, `UIState.kt`, `Event.kt`, `Preview.kt`
- ViewModels emit `FlyerBoardWindowsEvent` for navigation and snackbars;
  they never hold a reference to `NavController` directly.
- All network calls go through Manager → Service. ViewModels call managers
  only, never services directly.
- Shared UI components live in `ui-components`; screen-specific composables live
  in `app` alongside their screen.
- Previews must be wrapped in `AppTheme {}` and annotated with `@Preview`.
- New ViewModels must be registered with `viewModelOf(::FeatureViewModel)`
  in `ViewModelModule.kt`.
