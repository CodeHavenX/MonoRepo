# Screen: Splash

## Overview

The launch screen shown while the app initialises. Displays the app wordmark
and a loading indicator, then automatically advances to the Main graph after
one second. No user interaction is possible.

**Route:** `FlyerBoardWindowNavGraphDestination.SplashNavGraphDestination`
(registered directly in `WindowNavigationHost`, not inside a sub-graph)

**Auth required:** No

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| 1-second delay (auto) | Main graph (`MainNavGraphDestination`) | — |

The navigation clears the entire back stack so the user cannot return to the
splash screen with the system back gesture.

---

## Files

| File | Path | Status |
|---|---|---|
| `SplashScreen.kt` | `features/splash/` | Exists — needs wordmark added |
| `SplashViewModel.kt` | `features/splash/` | Complete |
| `SplashUIState.kt` | `features/splash/` | Complete |
| `SplashEvent.kt` | `features/splash/` | Complete |
| `SplashScreen.preview.kt` | `features/splash/` | Complete |

---

## UI Layer

### Shared components used

| Component | From |
|---|---|
| `LoadingStateBox` | `shared-ui` (Phase 1.5) |

### Layout

```
Box(fillMaxSize, contentAlignment = Center)
  Column(horizontalAlignment = CenterHorizontally, verticalArrangement = spacedBy(Padding.LARGE))
    Text("FlyerBoard", style = displayLarge)   ← wordmark (add this)
    LoadingStateBox()                           ← replace inline CircularProgressIndicator
```

The wordmark can be a `Text` composable styled with `MaterialTheme.typography.displayLarge`
until a vector logo asset is available. If a `DrawableResource` named
`ic_flyerboard_wordmark` is added to `shared-ui/src/commonMain/resources/`, it
can be loaded with `painterResource` and rendered as an `Image` instead.

### Current state vs target

**Current `SplashContent`:**
```kotlin
Box(modifier.fillMaxSize(), contentAlignment = Center) {
    if (content.isLoading) CircularProgressIndicator()
}
```

**Target `SplashContent`:**
```kotlin
Box(modifier.fillMaxSize(), contentAlignment = Center) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Padding.LARGE),
    ) {
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.displayLarge,
        )
        if (content.isLoading) {
            LoadingStateBox()
        }
    }
}
```

Add string resource `app_name = "FlyerBoard"` to `app` resources.

---

## UIState

```kotlin
data class SplashUIState(val isLoading: Boolean) : ViewModelUIState {
    companion object {
        val Initial = SplashUIState(isLoading = true)
    }
}
```

No changes needed. `isLoading` starts as `true` so the indicator is shown
immediately on launch.

---

## Event

```kotlin
sealed class SplashEvent : ViewModelEvent {
    data object Noop : SplashEvent()
}
```

No changes needed.

---

## ViewModel

**Class:** `SplashViewModel`
**Dependencies:** `ViewModelDependencies` (no managers needed)

### Existing method

```kotlin
fun navigateToMainScreen()
```

Called from `LifecycleEventEffect(ON_CREATE)` in the screen. Waits
`SPLASH_DELAY_MS` (1 000 ms) then emits:

```kotlin
FlyerBoardWindowsEvent.NavigateToNavGraph(
    destination = FlyerBoardWindowNavGraphDestination.MainNavGraphDestination,
    clearStack = true,
)
```

### No changes needed to the ViewModel.

---

## Manager Layer

No manager calls. The splash screen performs no network operations.

---

## Service Layer

No service calls.

---

## String Resources to Add

| Key | Value |
|---|---|
| `app_name` | `FlyerBoard` |
