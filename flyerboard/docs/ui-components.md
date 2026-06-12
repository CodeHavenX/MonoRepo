# Shared UI Components Plan

This document identifies the reusable components to be built in
`flyerboard/front-end/ui-components/src/commonMain/kotlin/com/cramsan/flyerboard/client/ui/components/`.

Each component has a paired preview file following the convention established by
`FlyerBoardTopBar.kt` / `FlyerBoardTopBarPreview.kt`.

---

## Analysis

The screens in `app` contain significant UI duplication:

| Pattern | Duplicated in |
|---|---|
| Centered `CircularProgressIndicator` | FlyerList, Archive, MyFlyers, ModerationQueue, FlyerDetail, FlyerEdit |
| Centered empty-state message text | FlyerList, Archive, MyFlyers, ModerationQueue, FlyerDetail |
| Inline `FlyerCard` (title + description + expiry) | FlyerListScreen, ArchiveScreen, base of MyFlyersScreen |
| `StatusBadge` color label | Private to MyFlyersScreen |
| `FlyerCard` + status badge + edit button | Private to MyFlyersScreen |
| Approve/Reject card | Private to ModerationQueueScreen |

`FlyerBoardSearchBar` is described in the frontend spec (Archive screen search) but is not yet implemented.

---

## Components to Create

### 1. `StatusBadge` + `StatusBadgePreview`

A small colored text chip for `FlyerStatus` (Approved / Pending / Rejected / Archived).

**Currently:** Private function in `MyFlyersScreen.kt`. Needed in My Flyers; applicable to Moderation Queue cards too.

```kotlin
@Composable
fun StatusBadge(status: FlyerStatus, modifier: Modifier = Modifier)
```

**Previews:** 4 — one per status value.

---

### 2. `FlyerCard` + `FlyerCardPreview`

Clickable card showing `title`, `description` (max 2 lines), and optional `expiresAt` label.

**Currently:** Three near-identical private composables — `FlyerCard` in FlyerListScreen, `ArchivedFlyerCard` in ArchiveScreen, and the inner part of `MyFlyerCard` in MyFlyersScreen.

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

**Previews:** 2 — with expiry date, without expiry date.

---

### 3. `FlyerCardWithStatus` + `FlyerCardWithStatusPreview`

Extends `FlyerCard` by adding a `StatusBadge` in the title row and an optional **Edit** button (suppressed when status is `ARCHIVED`).

**Currently:** Private `MyFlyerCard` in `MyFlyersScreen.kt`.

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

**Previews:** 4 — one per status (showing/hiding Edit button appropriately).

---

### 4. `ModerationFlyerCard` + `ModerationFlyerCardPreview`

Card with title, description, optional expiry, and a row of **Reject** (outlined/error) + **Approve** (filled) buttons.

**Currently:** Private `PendingFlyerCard` in `ModerationQueueScreen.kt`.

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

**Previews:** 2 — with expiry, without expiry.

---

### 5. `LoadingStateBox` + `LoadingStateBoxPreview`

Fills its parent and centers a `CircularProgressIndicator`.

**Currently:** Inline `if (uiState.isLoading) { CircularProgressIndicator() }` inside a centered `Box` in every list/detail screen (6 occurrences).

```kotlin
@Composable
fun LoadingStateBox(modifier: Modifier = Modifier)
```

**Previews:** 1 — indicator centered in a fixed-size box.

---

### 6. `EmptyStateBox` + `EmptyStateBoxPreview`

Fills its parent and centers a `bodyLarge` message string. Covers "no flyers", "not found", etc.

**Currently:** Inline `Text(emptyMessage, style = bodyLarge)` inside a centered `Box` in every list screen (5 occurrences).

```kotlin
@Composable
fun EmptyStateBox(message: String, modifier: Modifier = Modifier)
```

**Previews:** 1 — with a sample message string.

---

### 7. `FlyerBoardSearchBar` + `FlyerBoardSearchBarPreview`

Search input field for filtering results. Described in the frontend spec for the Archive screen (`q` query parameter) but not yet implemented in `ArchiveScreen.kt`.

```kotlin
@Composable
fun FlyerBoardSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
)
```

**Previews:** 2 — empty query, with text entered.

---

### 8. `FlyerAsyncImage` + `FlyerAsyncImagePreview`

Coil `AsyncImage` locked to a 4:3 aspect ratio with `ContentScale.Crop` and a loading placeholder. Currently used inline in `FlyerDetailBody`; will be needed by flyer cards once they display thumbnails.

```kotlin
@Composable
fun FlyerAsyncImage(
    url: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
)
```

**Previews:** 2 — with a URL (placeholder image), with `null` URL (empty/no-image state).

---

## Summary

| Component file | Preview file | Previews | Replaces / covers |
|---|---|---|---|
| `StatusBadge.kt` | `StatusBadgePreview.kt` | 4 | Private in `MyFlyersScreen` |
| `FlyerCard.kt` | `FlyerCardPreview.kt` | 2 | 3 private copies across list screens |
| `FlyerCardWithStatus.kt` | `FlyerCardWithStatusPreview.kt` | 4 | Private `MyFlyerCard` |
| `ModerationFlyerCard.kt` | `ModerationFlyerCardPreview.kt` | 2 | Private `PendingFlyerCard` |
| `LoadingStateBox.kt` | `LoadingStateBoxPreview.kt` | 1 | 6 inline occurrences |
| `EmptyStateBox.kt` | `EmptyStateBoxPreview.kt` | 1 | 5 inline occurrences |
| `FlyerBoardSearchBar.kt` | `FlyerBoardSearchBarPreview.kt` | 2 | Not yet implemented (Archive search) |
| `FlyerAsyncImage.kt` | `FlyerAsyncImagePreview.kt` | 2 | Inline in `FlyerDetailBody` |

**16 new files total** (8 component + 8 preview).
