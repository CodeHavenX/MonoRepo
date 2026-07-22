# Mock Fidelity Check

Compare a rendered Compose preview against its source-of-truth HTML mock. This is a
**design-intent comparison, not a pixel diff** — Compose/Robolectric rendering and browser
HTML/CSS will never match pixel-for-pixel even when the implementation is correct (different
fonts, layout engines, anti-aliasing). No headless-browser screenshot tool exists in this repo
for the mock side, so judge fidelity by reading the mock's markup and tokens, and viewing the
rendered screenshot as an image.

## Step 1 — Locate the mock

Find the mock for this screen:

```
Glob: <project-root>/edifikana/mockups/<screen-name>.html
```

If no mock file exists for this screen, skip this check entirely and say so explicitly — don't
invent a comparison against nothing.

## Step 2 — Render the preview

Follow the Roborazzi instructions in `.ai/instructions.md` to render every
`@ComponentPreviews`/`@ScreenPreviews`/`@DevicePreviews` preview on the changed
`<Screen>Preview.kt`:

```bash
./gradlew :<module>:recordRoborazziDebug --tests "*<PreviewFunctionName>*"
```

Locate the resulting PNG(s) under `<module-dir>/screenshots/`.

## Step 3 — Match each preview to a mock use-case

Mocks model multiple states in one file, each marked with an
`<!-- === USE CASE: Name === -->` comment (see `refine-mock`). Match each rendered preview
function to the use-case section it's supposed to represent (e.g. `LoadingPreview` ↔ the
"Loading" section, `EmptyStatePreview` ↔ "Empty state"). If a preview has no matching use-case
in the mock, or a use-case in the mock has no corresponding preview, flag that coverage gap
itself — it's a finding, not something to silently work around.

## Step 4 — Compare

Read the rendered PNG with the Read tool (visual inspection), and read the mock's HTML for the
matched use-case section plus `edifikana/mockups/styles/common.css` for the actual token values
it references (e.g. resolve `var(--color-warning)` to its hex value so you know what color to
expect). Compare:

- **Layout structure** — same grouping/order of elements, same shell type (app-shell / auth /
  setup flow), same nav placement and active-item highlighting
- **Spacing** — matches the mock's spacing scale; a large deviation (e.g. double or half the
  expected gap) is a finding, sub-pixel rounding differences are not
- **Color** — rendered elements match the *resolved* token value the mock specifies, not an
  arbitrary substitute
- **Typography** — matches scale/weight tier (heading vs. body vs. caption), not necessarily the
  exact rendered font
- **Component presence** — every element the mock's use-case shows (badges, icons, empty-state
  illustration, error banner, etc.) is present in the render; nothing appears that isn't in the
  mock
- **State-specific content** — the specific labels/values/copy the mock shows for that use-case

## Step 5 — Report

```
[<Screen>Preview] <use-case name>
Mock shows   : <what the mock has>
Render shows : <what the implementation actually renders>
Fix          : <specific change — Screen.kt structure, a theme token, missing content, etc.>
```

If there are no differences, say so explicitly rather than omitting the section.
