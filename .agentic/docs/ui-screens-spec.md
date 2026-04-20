# UI Screens Specification

Detailed screen-by-screen design spec for Flyerboard, implementing the "Neighborhood Zine" theme defined in `ui-design.md`. All layouts target desktop web (~1200px wide). Navigation uses a persistent top nav bar.

---

## Global App Shell

### Top Nav Bar
- **Background:** Near-Black `#111827`
- **Height:** 64px
- **Left:** "FLYERBOARD" wordmark — Space Grotesk Extra Bold, Electric Indigo `#4F46E5`, 22px
- **Center:** Nav links — `Browse` · `My Flyers` · `Archive` (Inter Medium, 15px, white)
  - "My Flyers" is hidden when the user is not authenticated
  - Active link: Electric Indigo `#4F46E5` underline (2px) + Indigo text color
  - Hover: white text
- **Right:**
  - Unauthenticated: "Sign In" — filled pill button, Electric Indigo background, white text
  - Authenticated (regular user): username in white · "Sign Out" ghost button in white
  - Authenticated (admin): additionally shows "Admin" text link in Lime `#84CC16` before username

### Page Body
- Max-width container: **1200px**, horizontally centered
- Horizontal padding: **24px** on each side
- Background: Near-White `#FAFAFA`

### Global Snackbar / Toast
- Position: bottom-center, above FAB if present
- Error (API failures, validation): Coral `#F43F5E` background, white text
- Success (submit, save): Lime `#84CC16` background, Near-Black text
- Auto-dismiss after 4 seconds

---

## 1. Splash Screen

**Destination:** `SplashNavGraphDestination`

**Layout:** Full-screen, no nav bar

| Element | Spec |
|---|---|
| Background | Full-screen gradient: Electric Indigo `#4F46E5` → Purple `#7C3AED`, top to bottom |
| Wordmark | "FLYERBOARD" — Space Grotesk Extra Bold, 48px, white, centered |
| Tagline | "Your neighborhood. Your notices." — Inter Regular, 18px, white at 70% opacity, below wordmark |
| Loader | `CircularProgressIndicator` in white, 32px, 24px below tagline |

**Behavior:** Checks auth state on load; auto-navigates to `FlyerListDestination` (authenticated or not) after initialization completes.

---

## 2. Sign In Screen

**Destination:** `SignInDestination`

**Layout:** Centered card on Near-White background. No top nav bar.

```
┌─────────────────────────────────────────────────┐
│                                                 │
│   ┌─────────────────────────────────────────┐   │
│   │▓▓▓▓ (4px Electric Indigo top border)   │   │
│   │                                         │   │
│   │  Welcome back                           │   │
│   │  Sign in to your account                │   │
│   │                                         │   │
│   │  [Email ____________________________]   │   │
│   │  [Password ________________________]   │   │
│   │                                         │   │
│   │  [       Sign In (filled)          ]    │   │
│   │  [  Don't have an account? Sign Up ]    │   │
│   │                                         │   │
│   └─────────────────────────────────────────┘   │
│                                                 │
└─────────────────────────────────────────────────┘
```

| Element | Spec |
|---|---|
| Card width | 480px |
| Card | White `#FFFFFF`, border-radius 12px, drop shadow `0 4px 24px rgba(0,0,0,0.10)`, 4px Electric Indigo top border |
| Card padding | 40px |
| Headline | "Welcome back" — Space Grotesk Bold, 26px, Near-Black `#111827` |
| Subheading | "Sign in to your account" — Inter Regular, 15px, Cool Grey `#6B7280` |
| Email field | `OutlinedTextField`, full-width, label "Email" |
| Password field | `OutlinedTextField`, full-width, label "Password", `PasswordVisualTransformation`, trailing visibility toggle icon |
| Sign In button | Full-width filled pill (border-radius 50px), Electric Indigo background, white text, Space Grotesk SemiBold 15px |
| Sign Up link | `TextButton`, "Don't have an account? Sign Up", Hot Coral `#F43F5E` |
| Error state | Field border turns red, error message in red Inter Regular 13px below the field |
| Loading overlay | Translucent white `rgba(255,255,255,0.7)` over entire card + `CircularProgressIndicator` centered |

---

## 3. Sign Up Screen

**Destination:** `SignUpDestination`

**Layout:** Same centered card pattern as Sign In.

| Element | Spec |
|---|---|
| Headline | "Join the board" — Space Grotesk Bold, 26px |
| Subheading | "Create your account" — Inter Regular, 15px, Cool Grey |
| Fields | Email, Password, Confirm Password (all `OutlinedTextField`) |
| CTA | "Create Account" — full-width filled pill, Electric Indigo |
| Secondary | "Already have an account? Sign In" — TextButton, Hot Coral |
| Confirm mismatch error | Inline error below Confirm Password field |

---

## 4. Flyer List (Browse) Screen

**Destination:** `FlyerListDestination`

**Layout:** Top nav bar (Browse active) + full page body

```
┌─────────────────────────────────────────────────────────┐
│ FLYERBOARD    Browse  My Flyers  Archive        Sign In │  ← nav bar
├─────────────────────────────────────────────────────────┤
│                                                         │
│  [🔍 Search flyers...                               ]   │  ← search bar
│                                                         │
│  [All] [Events] [Notices] [Announcements]               │  ← filter chips
│                                                         │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐           │
│  │▓▓▓ indigo │  │▓▓▓ coral  │  │▓▓▓ lime   │           │
│  │ [image]   │  │ [image]   │  │ [image]   │           │
│  │ Title     │  │ Title     │  │ Title     │           │
│  │ Desc...   │  │ Desc...   │  │ Desc...   │           │
│  │ 🏷 Expires│  │ 🏷 Soon   │  │ uploader  │           │
│  └───────────┘  └───────────┘  └───────────┘           │
│                                                         │
│                 [ Load more ]                           │
│                                                    [+]  │  ← FAB (auth only)
└─────────────────────────────────────────────────────────┘
```

### Search Bar
- Full-width, rounded pill shape (`border-radius: 50px`)
- Background: white, border: 1px solid `#E5E7EB`
- Focus: 2px Electric Indigo ring
- Leading icon: magnifier in Cool Grey
- Placeholder: "Search flyers…" in Cool Grey
- Margin: 24px below nav bar

### Filter Chips
- Row of `FilterChip` components: "All" · "Events" · "Notices" · "Announcements"
- Selected chip: Electric Indigo background, white text
- Unselected: white background, `#E5E7EB` border, Near-Black text
- Margin: 12px below search bar (placeholder — categories not yet in data model)

### Masonry Grid
- 3 columns, `gap: 16px`
- Cards are variable height based on content

### Flyer Card
| Element | Spec |
|---|---|
| Background | White `#FFFFFF` |
| Border-radius | 12px |
| Shadow | `0 2px 8px rgba(0,0,0,0.08)` |
| Top border | 4px solid — cycles: index%3==0 → Indigo, ==1 → Coral, ==2 → Lime |
| Thumbnail | 16:9 crop via `AsyncImage` with `ContentScale.Crop`; fallback: PDF icon (coral) on grey `#F3F4F6` background |
| Title | Space Grotesk SemiBold, 16px, Near-Black, max 2 lines, ellipsis |
| Description | Inter Regular, 14px, Cool Grey `#6B7280`, max 3 lines, ellipsis |
| Card padding | 16px |
| Footer | Flex row: expiry badge (left) + uploader name (right, 12px Cool Grey) |
| Expiry badge | Rounded pill 10px, lime background if >7 days remaining; coral if ≤7 days; hidden if no expiry |
| Hover | Shadow increases to `0 6px 20px rgba(0,0,0,0.14)`, translateY(-2px) transition |

### Empty State
- Centered vertically in grid area
- Icon placeholder (bulletin-board illustration or 📌 emoji placeholder)
- Text: "No flyers yet. Be the first to post!" — Space Grotesk SemiBold, 18px, Cool Grey
- CTA button (authenticated): "Post a Flyer" — filled coral pill

### Loading State
- 9 skeleton cards (3×3 grid), shimmer animation
- Skeleton: grey `#E5E7EB` rectangles matching card structure

### Pagination
- "Load more" — outline button (indigo border, indigo text), centered below grid
- Shows spinner inside button while loading next page

### FAB
- Visible only when authenticated
- Position: bottom-right, fixed, 24px from edge
- Size: 56px circle, Hot Coral `#F43F5E` background
- Icon: "+" in white, 28px
- Navigates to `FlyerEditDestination(flyerId = null)`

---

## 5. Flyer Detail Screen

**Destination:** `FlyerDetailDestination(flyerId)`

**Layout:** Single-column, max-width 720px, centered, top nav bar visible

```
┌────────────────────────────────────────────────┐
│ FLYERBOARD    Browse  My Flyers  Archive  ...  │
├────────────────────────────────────────────────┤
│                                                │
│  ← Back                                        │
│                                                │
│  ┌──────────────────────────────────────────┐  │
│  │                                          │  │
│  │           [image / PDF iframe]           │  │
│  │                                          │  │
│  └──────────────────────────────────────────┘  │
│  [● Pending Review]  (status badge if needed)  │
│                                                │
│  Flyer Title (large, bold)                     │
│  uploader · Apr 17, 2026 · Expires May 1       │
│                                                │
│  Full description text here...                 │
│                                                │
│  [Edit Flyer]    (owner only)                  │
│  [Approve] [Reject]  (admin only)              │
│                                                │
└────────────────────────────────────────────────┘
```

| Element | Spec |
|---|---|
| Back button | "← Back" — TextButton, Electric Indigo, Inter Medium 15px, top-left of content |
| Image | Full-width `AsyncImage`, aspect-ratio 4:3, `ContentScale.Crop`, border-radius 12px |
| PDF | Full-width `<iframe>` via JS interop, `min-height: 600px`, border-radius 12px |
| Status badge | Shown only for non-approved states — Coral pill "Pending Review", Red pill "Rejected", Grey pill "Archived" |
| Title | Space Grotesk Bold, 28px, Near-Black, below image |
| Metadata row | Inter Regular, 14px, Cool Grey — `uploader · posted date · Expires date` (pipe-separated), 8px below title |
| Description | Inter Regular, 16px, Near-Black `#111827`, line-height 1.7, full text (no truncation) |
| Edit button | Outline button — Indigo border + text, only shown to the flyer's original uploader |
| Approve button | Filled Lime `#84CC16` button, Near-Black text, admin only |
| Reject button | Filled Coral `#F43F5E` button, white text, admin only |
| Loading | `CircularProgressIndicator` centered in page body |
| Not found | "Flyer not found." — Space Grotesk Medium, 18px, Cool Grey, centered |

---

## 6. My Flyers Screen

**Destination:** `MyFlyersDestination`

**Layout:** Top nav bar (My Flyers active) + page body

```
┌────────────────────────────────────────────────┐
│ FLYERBOARD    Browse  My Flyers  Archive  ...  │
├────────────────────────────────────────────────┤
│                                                │
│  My Flyers                   [+ New Flyer]     │
│                                                │
│  ┌──────────────────────────────────────────┐  │
│  │ [thumb] Title              [Approved ✓]  │  │
│  │         Description...  Apr 17   [✏]    │  │
│  ├──────────────────────────────────────────┤  │
│  │ [thumb] Title              [Pending ●]   │  │
│  │         Description...  Apr 15   [✏]    │  │
│  └──────────────────────────────────────────┘  │
│                                                │
└────────────────────────────────────────────────┘
```

| Element | Spec |
|---|---|
| Page headline | "My Flyers" — Space Grotesk Bold, 28px |
| New Flyer button | "+ New Flyer" — filled coral pill button, right-aligned, navigates to `FlyerEditDestination(null)` |
| Grid | 2-column grid at 1200px, `gap: 16px` |
| Row card | White surface, 12px radius, shadow, 4px top-border in status color |
| Thumbnail | 64×64px square, rounded 8px, `ContentScale.Crop` |
| Title | Space Grotesk SemiBold, 16px, Near-Black |
| Date | Inter Regular, 13px, Cool Grey |
| Status badge | Pill — Approved: Lime bg; Pending: Coral bg; Rejected: Red `#DC2626` bg; Archived: Grey `#9CA3AF` bg; white/dark text |
| Edit icon | `IconButton` with pencil icon, indigo, navigates to `FlyerEditDestination(flyerId)` |
| Empty state | "You haven't posted any flyers yet." + "Post your first flyer" coral CTA button |

---

## 7. Archive Screen

**Destination:** `ArchiveDestination`

**Layout:** Top nav bar (Archive active) + page body. Same masonry grid as Browse with archive-specific styling.

| Element | Spec |
|---|---|
| Page headline | "Archive" — Space Grotesk Bold, 28px |
| Subtitle | "Expired community notices, preserved for the record." — Inter Regular, 15px, Cool Grey |
| Search bar | Same as Browse screen |
| Grid | Same 3-column masonry as Browse |
| Card top border | Grey `#9CA3AF` for all archive cards (vs. cycling colors in Browse) |
| Thumbnail overlay | Semi-transparent grey `rgba(0,0,0,0.35)` overlay on image with "Archived" text in white Inter SemiBold 12px, centered |
| No FAB | Archive is read-only; no upload button |

---

## 8. Upload / Edit Flyer Screen

**Destination:** `FlyerEditDestination(flyerId: String?)`
- `flyerId == null` → Create mode ("New Flyer")
- `flyerId != null` → Edit mode ("Edit Flyer")

**Layout:** Single-column form, max-width 640px, centered, top nav bar visible

```
┌──────────────────────────────────────────────────┐
│ FLYERBOARD    Browse  My Flyers  Archive  ...    │
├──────────────────────────────────────────────────┤
│                                                  │
│  New Flyer                                       │
│                                                  │
│  ┌────────────────────────────────────────────┐  │
│  │                                            │  │
│  │   ☁  Drag & drop your image or PDF here,  │  │
│  │      or click to browse                    │  │
│  │                                            │  │
│  └────────────────────────────────────────────┘  │
│  (2px dashed coral border)                       │
│                                                  │
│  [Title _____________________________________]   │
│                                                  │
│  [Description                               ]   │
│  [                                          ]   │
│  [                                          ]   │
│                                                  │
│  [Expires on (optional) _____________ 📅 ]      │
│                                                  │
│  [Submit for Review]      [Cancel]               │
│                                                  │
└──────────────────────────────────────────────────┘
```

| Element | Spec |
|---|---|
| Headline | "New Flyer" or "Edit Flyer" — Space Grotesk Bold, 28px |
| File upload zone | Dashed border box: `border: 2px dashed #F43F5E`, border-radius 12px, min-height 160px, centered content |
| Upload zone icon | Cloud-upload icon, coral `#F43F5E`, 40px |
| Upload zone text | "Drag & drop your image or PDF here, or click to browse" — Inter Regular, 15px, Cool Grey |
| Upload zone (filled) | Shows thumbnail preview (image) or filename chip (PDF); coral "× Remove" button to clear |
| Title field | `OutlinedTextField`, full-width, label "Title", single line |
| Description field | `OutlinedTextField`, full-width, label "Description", min 4 rows, multi-line |
| Expiry field | `OutlinedTextField`, full-width, label "Expires on (optional)", trailing calendar icon |
| Primary CTA | Create: "Submit for Review" / Edit: "Save Changes" — full-width filled indigo pill button |
| Cancel | "Cancel" — full-width ghost button (indigo outline, indigo text) |
| Inline validation | Red Inter Regular 13px text below each field when invalid (e.g. "Title is required") |
| Loading overlay | Translucent white overlay + `CircularProgressIndicator` over entire form on submit |

---

## 9. Moderation Queue Screen

**Destination:** `ModerationQueueDestination` (admin only)

**Layout:** Top nav bar (Admin link active) + page body

```
┌──────────────────────────────────────────────────────────────────────────┐
│ FLYERBOARD    Browse  My Flyers  Archive     Admin   cramsan  Sign Out   │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Moderation Queue  [● 4 pending]                                         │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐    │
│  │ [img] Title of flyer          user@email  Apr 17  [Approve][Rej]│    │
│  │       Description excerpt...                                     │    │
│  ├──────────────────────────────────────────────────────────────────┤    │
│  │ [img] Another flyer           user@email  Apr 16  [Approve][Rej]│    │
│  │       Description excerpt...                                     │    │
│  └──────────────────────────────────────────────────────────────────┘    │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

| Element | Spec |
|---|---|
| Page headline | "Moderation Queue" — Space Grotesk Bold, 28px |
| Pending badge | Coral filled pill beside headline — "● N pending", Inter SemiBold 14px, white text |
| List layout | Full-width rows with dividers; no masonry |
| Row | White surface, 12px radius card, subtle shadow |
| Thumbnail | 64×64px square, rounded 8px, `ContentScale.Crop`, left-aligned |
| Title | Space Grotesk SemiBold, 16px, Near-Black — clicking navigates to `FlyerDetailDestination` |
| Description excerpt | Inter Regular, 14px, Cool Grey, max 2 lines, right of thumbnail |
| Uploader | Inter Regular, 13px, Cool Grey |
| Submitted date | Inter Regular, 13px, Cool Grey |
| Approve button | Filled Lime `#84CC16` button, Near-Black text, "Approve" |
| Reject button | Outline Coral `#F43F5E` border + text button, "Reject" |
| Row spacing | 12px vertical gap between rows |
| Empty state | Centered — Lime checkmark icon (40px) + "No pending flyers. You're all caught up." Space Grotesk Medium 18px Cool Grey |

---

## Navigation Flow

```
App Start
    │
    ▼
[Splash] ──────────────────────────────────────────────────────────┐
    │ (auth resolved)                                               │
    ▼                                                               │
[FlyerList] ◄──────────────────────────────────────────────────────┘
    │   ▲
    │   │ back
    │   │
    ▼   │
[FlyerDetail] ──► [FlyerEdit (edit mode)] ──► back to FlyerList / MyFlyers
    │   ▲
    │   └─── from MyFlyers
    │
[MyFlyers] ──► [FlyerEdit (create mode)] ──► back to MyFlyers

[Archive] ──► [FlyerDetail] (read-only)

[ModerationQueue] ──► [FlyerDetail] (with approve/reject actions)

[Auth flow]
    SignIn ◄──► SignUp
    │
    ▼ (success)
    FlyerList
```

### Auth-Gated Navigation
| Screen | Unauthenticated | Authenticated (user) | Admin |
|---|---|---|---|
| Browse | ✅ | ✅ | ✅ |
| Flyer Detail | ✅ (view only) | ✅ (edit button if owner) | ✅ (approve/reject) |
| My Flyers | — redirect to Sign In | ✅ | ✅ |
| Archive | ✅ | ✅ | ✅ |
| Upload / Edit | — redirect to Sign In | ✅ | ✅ |
| Moderation Queue | — redirect to Sign In | — redirect to Browse | ✅ |
