# Navigation, URL Handling, and Deep Link Analysis

Date: 2026-06-22
Scope: `DeepLinkManager` (architecture/front-end-architecture) and `PathNavigation.kt`
(flyerboard), plus everything they depend on. Goal of the investigation: assess whether
there's an opportunity to converge on a single navigation/URL-handling implementation that
each app configures, per the stated direction in `architecture/front-end-architecture`.

## TL;DR

The two files named in the original ask (`DeepLinkManager.kt` and `PathNavigation.kt`) look
like two competing per-app implementations of "URL → screen," but the underlying mechanics are
**already mostly unified**. The real gaps are narrower than "unify two systems":

1. A small, hand-written per-app aggregator (`PathNavigation.kt` / `EdifikanaPathNavigation.kt`)
   that duplicates the same `?:`-chain shape across apps.
2. `DeepLinkManager` is a separate, less-structured mechanism for native custom-scheme deep
   links that was never folded into the `WebRoute`/`WebDestination` world, even though its only
   real target today (`AuthDestination.SetNewPasswordDestination`) already has a `@WebPath`.
3. A real correctness bug: **no percent-encoding/decoding anywhere** in the URL pipeline.
4. A real infra inconsistency: Edifikana's production web deploy is missing the SPA fallback
   that Flyerboard's has, so deep links 404 in prod despite being fully supported in code.
5. Adoption is uneven across apps (runasimi has none of this machinery at all), so without
   convergence the gap between "apps that do this well" and "apps that don't" will keep growing.

## 1. The actual layered architecture

| Layer | What | Shared across apps? |
|---|---|---|
| Foundation | `WebDestination` / `WebRoute` / `WebRouteRegistry` (`framework/core-compose/src/commonMain/kotlin/com/cramsan/framework/core/compose/navigation/`) + `@WebPath` annotation (`framework/annotations/.../WebPath.kt`) + KSP processor (`framework/web-route-ksp/src/main/kotlin/com/cramsan/framework/webroute/ksp/WebDestinationProcessor.kt`) | ✅ Already fully shared, multiplatform, codegen-backed |
| Browser sync | `BrowserNavigator` expect/actual (`architecture/front-end-architecture/src/{commonMain,wasmJsMain,androidMain,jvmMain}/kotlin/com/cramsan/architecture/client/navigation/BrowserNavigator*.kt`) | ✅ Already fully shared |
| Per-app glue | `PathNavigation.kt` (flyerboard), `EdifikanaPathNavigation.kt` (edifikana), template stub | ❌ Hand-written, duplicated per app |
| Native custom-scheme deep links | `DeepLinkManager` (chain of closures) + Android intent-filter + Supabase `handleDeeplinks` | ❌ Edifikana-only, separate mechanism, not used by flyerboard at all |

Each app's top-level destinations (`MainDestination`, `AuthDestination`, `HomeDestination`,
etc.) are `sealed class X : WebDestination`, whose direct subclasses carry
`@WebPath("/some/path")`. `WebDestinationProcessor` (KSP) generates an internal
`XWebRoutes` object per sealed hierarchy implementing `toWebPath()` / `fromWebPath()` /
`toWebPath(NavBackStackEntry)`, backed by `WebRoute<T>`, which (de)serializes the
destination's fields to/from query params via `KeyValueMapEncoder` / `KeyValueMapDecoder`
(`framework/http-serializers`).

Both Flyerboard and Edifikana consume this identically:
- `flyerboard/.../navigation/PathNavigation.kt:13-16` —
  `pathToDestination(path) = MainDestination.fromWebPath(path) ?: AuthDestination.fromWebPath(path) ?: MainDestination.FlyerListDestination`
- `edifikana/.../navigation/EdifikanaPathNavigation.kt:25-29` — same shape, over
  `AuthDestination`, `HomeDestination`, `AccountDestination`, `SettingsDestination`.
- Both wire the result into `BrowserNavigator.attach(...)` inside their respective
  `*WindowScreen.kt`, and both resolve `BrowserNavigator.getInitialPath()` once at composition
  time to hand to `SplashScreen` so post-auth navigation doesn't clobber the deep-link target
  (`FlyerBoardWindowScreen.kt:69-72`, `EdifikanaWindowScreen.kt:89-92` — same pattern, same
  comment wording).

### Adoption is uneven across apps

- **runasimi** (3rd app, has `app-android`/`app-wasm`/`app-jvm` launchers) uses plain
  `Destination` with no `WebDestination`, no `BrowserNavigator`, no `DeepLinkManager` at all —
  no browser URL sync, no deep links, just Compose Navigation.
- **flyerboard** has web-path routing (`WebDestination`/`WebRoute`/`BrowserNavigator`) but
  **no Android or iOS launcher module yet** — only `app-jvm` (desktop) and `app-wasm` (web)
  under `flyerboard/front-end/`. It therefore never needed `DeepLinkManager` or an
  intent-filter; its apparent simplicity vs. Edifikana is partly a product-maturity artifact,
  not a strictly better design.
- **edifikana** is the only app with both halves wired up: web-path routing *and* a native
  custom-scheme deep link (`edifikana://reset`) via `DeepLinkManager` + an Android intent-filter
  + the Supabase SDK's own `handleDeeplinks`.
- **templatereplaceme** (devtools scaffolding template) ships both mechanisms as opt-in stubs
  with TODO comments reminding the developer to wire them up as they add activities — meaning
  the duplication will keep propagating into every new app unless the template itself changes.

No app currently has an iOS target wired up for navigation purposes (no `Info.plist`/Swift
deep-link handling found for edifikana; flyerboard's `androidMain` source set exists but isn't
packaged — no `launcher-android` module for flyerboard).

## 2. `DeepLinkManager` — detailed findings

File: `architecture/front-end-architecture/src/commonMain/kotlin/com/cramsan/architecture/client/deeplink/DeepLinkManager.kt`
(+ `DeepLinkParams.kt` in the same package)

**What it does:** a chain-of-responsibility router. `register((DeepLinkParams) -> Destination?)`
adds a handler; `resolve(rawInput: String)` parses `rawInput` into `DeepLinkParams` and returns
the first non-null result from `handlers.firstNotNullOfOrNull { it(params) }`
(`DeepLinkManager.kt:28-31`).

`DeepLinkParams.parse` (`DeepLinkParams.kt:15-44`) handles:
- full URL with fragment (`scheme://host#k=v`)
- full URL with query (`scheme://host?k=v`)
- both at once, with **fragment values winning over query values on key collision**
  (`DeepLinkParams.kt:34, 40` — `toMap(queryStr) + toMap(fragmentStr)`)
- bare `k=v&k2=v2` strings with no delimiter

This flexibility exists because Supabase's password-recovery flow emits two different shapes
depending on auth flow type: PKCE (`edifikana://reset?code=CODE`) and implicit
(`edifikana://reset#access_token=...&type=recovery`).

**Current real-world usage (the only one in the codebase):**
- Registered once at Koin startup: `edifikana/.../di/ManagerModule.kt:29-40` registers a handler
  that does `if (params.rawInput.startsWith("edifikana://reset")) AuthDestination.SetNewPasswordDestination else null`.
- Invoked from `EdifikanaWindowViewModel.handleDeepLink(rawUrl)` (`EdifikanaWindowViewModel.kt:50-60`).
- The only caller of `handleDeepLink` in the entire repo is Android's
  `MainActivity.handleIncomingIntent` (`edifikana/.../main/MainActivity.kt:72-79`), which checks
  `intent.data?.scheme == "edifikana"`, calls `supabase.handleDeeplinks(intent)` (3rd-party SDK
  consuming the same intent for its own session-restore purposes), then
  `viewModel.handleDeepLink(uri.toString())`.
- The Android intent-filter that makes this reachable lives in
  `edifikana/front-end/launcher-android/src/main/AndroidManifest.xml:29-36`
  (`scheme="edifikana" host="reset"`, `action=VIEW`, `category=BROWSABLE`).
- Flyerboard does not reference `DeepLinkManager` anywhere (confirmed via repo-wide grep) — it
  is exclusively an Edifikana concept today, propagated to new apps only via the template stub.

**Positives**
- Clean separation of concerns: the router has zero knowledge of app-specific schemes/hosts;
  all routing logic lives in the app's DI layer (per the file's own doc comment,
  `DeepLinkManager.kt:9-11`).
- Handles the two genuinely different Supabase auth-flow shapes (query vs. fragment) without the
  caller needing to know which flow produced the link.
- Well unit-tested: `DeepLinkManagerTest.kt` has 16 cases covering registration order,
  fallthrough on null, fragment/query precedence, bare key=value, empty input, etc.

**Negatives**
- Matching is unstructured. Handlers do raw `rawInput.startsWith(...)` string checks
  (`ManagerModule.kt:34`) instead of validated path/param matching. `WebRoute` gives you
  required-param validation and "don't let another route claim this path" guarding for free
  (`WebRoute.matchesParams`, `WebRoute.kt:60-70`); `DeepLinkManager` handlers get none of that —
  every handler re-derives its own correctness from scratch.
- It is conceptually disconnected from `WebDestination`/`WebRoute` even though its only
  consumer's *target* is a `WebDestination` with its own canonical path
  (`AuthDestination.SetNewPasswordDestination` → `@WebPath("/auth/set-new-password")`,
  `AuthDestination.kt:48-51`). There are now two independent ways to reach that one screen — one
  through `DeepLinkManager` (custom scheme), one through `WebRoute` (browser path) — with no
  shared validation, and no guarantee they'd ever produce the same `Destination` instance if the
  required params differed between the two paths.
- Single global instance, no namespacing (`single { DeepLinkManager() }`,
  `ExtrasModule.kt:83`). Fine with one registered handler; would need care if an app ever
  needed multiple unrelated custom schemes (ordering/collision is entirely handler-defined).
- Propagating via the template as opt-in boilerplate with a TODO
  (`devtools/templates/app/front-end/app/src/commonMain/kotlin/.../di/ManagerModule.kt:20-26`)
  rather than as something with a clear "do you need this?" decision point.

## 3. `WebRoute` / `WebDestination` / `PathNavigation` / `BrowserNavigator` — detailed findings

**Core types** (`framework/core-compose/.../navigation/`):
- `WebRoute<T : Destination>(path, serializer)` — `WebRoute.kt:21`. `toWebPath(destination)`
  encodes fields via `encodeToKeyValueMap` and appends them as `?k=v&k=v` if non-empty
  (`WebRoute.kt:25-29`). `fromWebPath(url)` splits on the first `?`, rejects if the path prefix
  doesn't match, groups query params into `Map<String, List<String>>`, validates via
  `matchesParams` (every required field present, no unknown keys — `WebRoute.kt:60-70`), then
  decodes via `decodeFromKeyValueMap`.
- `WebRouteEntry<D>` / `webRouteEntry<D>(path)` (`WebRouteRegistry.kt:12-25`) — bundles a
  `WebRoute<D>` with a reified `NavBackStackEntry -> String?` lookup.
- `WebRouteRegistry<T>(entries)` (`WebRouteRegistry.kt:48-63`) — tries each entry's
  `fromWebPath`/`toWebPath` in order; exposes `registeredClasses` for an exhaustiveness check
  that is **documented but never used anywhere in the repo** (see Gap 6 below).
- `WebDestination` interface (`WebDestination.kt:44-47`) — just `fun toWebPath(): String`,
  plus an extension `NavBackStackEntry.toWebPathIfRoute<T>()`.
- `@WebPath(path)` annotation (`framework/annotations/.../WebPath.kt`) — source-retention,
  read by KSP only.

**Codegen** (`framework/web-route-ksp/.../WebDestinationProcessor.kt`):
- For every `sealed class` implementing `WebDestination`, walks `getSealedSubclasses()`,
  requires each to carry `@WebPath`, and **fails the build with `logger.error` if any subclass
  is missing one** (`WebDestinationProcessor.kt:56-69`) — this is a real compile-time safety net.
- Generates `internal object <Name>WebRoutes` with `toWebPath(destination)` (exhaustive `when`),
  `fromWebPath(path)`, and `toWebPath(entry: NavBackStackEntry)`
  (`generateWebRoutesSource`, `WebDestinationProcessor.kt:84-118`).

**`BrowserNavigator`** (`architecture/front-end-architecture/.../client/navigation/`):
- `expect class BrowserNavigator()` with `attach(navController, destinationToPath, pathToNavAction)`
  and `getInitialPath(): String?` (`BrowserNavigator.kt:12-32`).
- **wasmJs actual**: on every Compose destination change, computes the canonical path and, if it
  differs from `window.location.pathname + search`, calls `window.history.pushState(null, "", path)`
  (`BrowserNavigator.wasmJs.kt:16-27`). Listens for `popstate` via a `@JsFun`-bridged
  `addEventListener` (`BrowserNavigator.wasmJs.kt:49-50`); on fire, checks whether the target path
  is already in the Compose back stack — if so treats it as browser-back (`popBackStack()`), else
  as browser-forward (`pathToNavAction(path)`) (`BrowserNavigator.wasmJs.kt:28-39`).
  `getInitialPath()` returns null only for blank or `/` (`BrowserNavigator.wasmJs.kt:43-46`).
- **Android / JVM-desktop actuals**: both fully no-op (`BrowserNavigator.android.kt`,
  `BrowserNavigator.jvm.kt`) — correct, since neither platform has a browser address bar.

**Per-app aggregators** (the one genuinely duplicated piece):
- `flyerboard/.../navigation/PathNavigation.kt:13-16, 23-25` — two top-level functions
  `?:`-chaining `MainDestination`/`AuthDestination`.
- `edifikana/.../navigation/EdifikanaPathNavigation.kt:15-29` — same shape, over four
  destinations (`AuthDestination`, `HomeDestination`, `AccountDestination`,
  `SettingsDestination`); notably **excludes** `DebugDestination` (see Gap 5).
- `devtools/templates/.../navigation/PathNavigation.kt` — ships as an explicit empty-list stub
  with inline instructions for `create activity` to extend it manually
  (`PathNavigation.kt:6-27`); `devtools/core/.../Generators.kt:310-329` confirms the `create
  activity` generator only emits a comment reminder, it does not auto-register the new
  activity's routes.

**Positives**
- Genuinely unified already across the two production apps that use it: one `WebRoute<T>`
  primitive, one KSP processor, one `BrowserNavigator` expect/actual. Adding a destination needs
  only `@WebPath("/x")` — no hand-written parse/render code.
- Real compile-time safety: forgetting `@WebPath` on a sealed subclass fails the build
  (not just a runtime gap).
- `BrowserNavigator` isolates the one truly platform-divergent piece behind four small actuals,
  and correctly disambiguates browser-back vs. browser-forward via back-stack membership.
- The "initial deep link races with splash navigation" problem is solved identically and
  correctly in both apps (resolve once at composition time, hand to `SplashScreen`).
- Good test coverage per destination class (`AuthDestinationTest.kt`, `MainDestinationTest.kt`,
  `HomeDestinationTest.kt`, `AccountDestinationTest.kt`, `SettingsDestinationTest.kt`) covering
  required/optional params and unknown-path rejection.

**Negatives / concrete gaps found**

1. **No percent-encoding/decoding anywhere in the pipeline.** `KeyValueMapEncoder.encodeValue`
   (`KeyValueMapEncoder.kt:36-50`) calls `value.toString()` with no escaping; `WebRoute.toWebPath`
   joins raw values with `&`/`=` (`WebRoute.kt:28`); `WebRoute.fromWebPath` and
   `DeepLinkParams.parse` split raw strings on `&`/`=`/`?`/`#` with no unescaping
   (`WebRoute.kt:44-49`, `DeepLinkParams.kt:27-32`); `KeyValueMapDecoder.decodeString`
   (`KeyValueMapDecoder.kt:87-101`) returns the raw substring verbatim.
   **Proof this is unverified, not just theoretical:** `AuthDestinationTest.kt:16` feeds
   `/auth/sign-up?userEmail=test%40example.com` into `fromWebPath` and asserts only
   `assertIs<AuthDestination.SignUpDestination>(...)` — it never asserts the decoded
   `userEmail` field value. Since nothing in the pipeline unescapes `%40`, the resulting field is
   literally the string `"test%40example.com"`, not `"test@example.com"`. The test's choice of a
   percent-encoded fixture suggests the author expected decoding to happen — it doesn't, and the
   assertion shape hides it. Any field value containing `&`, `=`, `#`, or `+` will corrupt the
   *generated* URL on encode (breaks `toWebPath`) or be silently mis-split on decode.
2. **Production deploy inconsistency for the same mechanism.** Flyerboard's
   `flyerboard/deploy/nginx.conf:21` has `try_files $uri $uri/ /index.html;` (SPA fallback), so a
   direct browser load/refresh of `/archive` works in production. Edifikana's
   `edifikana/front-end/launcher-web/Dockerfile:7` is just `FROM nginx:alpine` with the **default**
   nginx config — no custom `nginx.conf` exists anywhere in the edifikana tree. A direct load or
   refresh of `/auth/sign-in` (or any deep link) would **404 in production** for Edifikana's web
   build, even though `EdifikanaPathNavigation.kt`/`BrowserNavigator` fully support it. This gap is
   invisible locally because **both** apps' dev servers set
   `config.devServer.historyApiFallback = true`
   (`flyerboard/front-end/launcher-web/webpack.config.d/devServer.js:4`,
   `edifikana/front-end/launcher-web/webpack.config.d/devServer.js:4` — identical comment text,
   confirming both were added with the same intent, but only one was carried through to prod).
3. **`DebugDestination` forks the `toWebPath` contract.** It implements plain `Destination`
   (not `WebDestination`) with a hand-rolled `abstract fun toWebPath(): String?` (nullable),
   explicitly because `WebDestination.toWebPath()` is non-null and "cannot represent that intent"
   of opting out of URL sync (`edifikana/.../debug/DebugDestination.kt:10-21`). Reasonable escape
   hatch, but it means the codebase now has two different shapes of "how do I become a URL" —
   one required by the interface, one ad hoc and parallel.
4. **`DeepLinkManager` and `WebRoute` are fully disconnected** (expanded in section 2) — the one
   case where a native deep link's target already has a `@WebPath` doesn't reuse any of the
   `WebRoute` validation machinery.
5. **Dead/unused safety-net suggestion.** `WebRouteRegistry.registeredClasses`
   (`WebRouteRegistry.kt:60-62`) is documented as something to "compare against
   `MyDestination::class.sealedSubclasses` in a JVM test" to catch an unrouted destination
   (`WebRouteRegistry.kt:45-46`) — repo-wide grep confirms **nothing uses it**. It's also
   redundant: the KSP processor already enforces every sealed subclass has a route at compile
   time, which is a strictly earlier and stronger guarantee than a JVM test could provide. Worth
   either deleting the property or deleting the misleading doc comment.
6. **Encoder ceiling is implicit, not enforced.** `KeyValueMapEncoder`/`Decoder` only support
   primitives, enums, and top-level lists ("does not support nested objects or complex types" —
   `KeyValueMapEncoder.kt:26`). Fine today since every `WebDestination` subclass is flat, but
   there's no compile-time guard — adding a nested object field fails at runtime via `error(...)`
   inside `encodeElement`/`encodeSerializableValue` (`KeyValueMapEncoder.kt:79-92, 108-134`)
   rather than being caught earlier.
7. **Per-app aggregator duplication.** `PathNavigation.kt` / `EdifikanaPathNavigation.kt` /
   the template stub are structurally identical, hand-maintained `?:`-chains. The `create
   activity` generator only emits a reminder comment (`Generators.kt:310-329`) — nothing
   auto-registers a new activity's destination into the chain. This is the one piece in the
   "already shared" stack that isn't compiler-enforced; it's easy to add a new top-level nav
   graph and forget the one-line registration (the failure mode is silent: the destination falls
   through to the default/start destination instead of failing a build).

## 4. Where unification has the most leverage

Ranked by leverage-to-risk ratio, not necessarily implementation order:

1. **Fix the percent-encoding gap in `KeyValueMapEncoder`/`Decoder`** (shared code — fixing it
   once benefits both apps and the template immediately). Add a regression test that asserts
   *decoded field values*, not just destination type, so this class of bug can't hide again.
2. **Fold the custom-scheme deep link into the `WebRoute` world** instead of running
   `DeepLinkManager` as a fully parallel mechanism: normalize
   `edifikana://reset?code=X` / `#access_token=...&type=recovery` into the canonical path
   (`/auth/set-new-password?...`) and resolve it through the same `fromWebPath` the browser path
   already uses. `DeepLinkManager`'s handler-chain is still useful for genuinely non-path-shaped
   inputs, but a destination that already has a `@WebPath` shouldn't have a second, less-validated
   door into it.
3. **Align Edifikana's production nginx config with Flyerboard's** (or centralize one shared
   nginx template both Dockerfiles reference) so deep-link browser support is actually consistent
   in prod, not just masked by the dev server's `historyApiFallback`.
4. **Generate the `PathNavigation.kt` aggregator instead of hand-maintaining it.** The KSP
   processor already knows every `WebDestination` sealed hierarchy in a module; it (or the
   `create activity` generator) could emit/append the `fromWebPath`/`toWebPath` chain
   automatically, removing the last manually-synced piece in the otherwise compiler-enforced
   stack.
5. **Pick one `toWebPath` contract.** Either let `WebDestination.toWebPath()` return `String?`
   (covering the debug/no-sync case directly, collapsing `DebugDestination`'s parallel method),
   or give `DebugDestination` a clearly documented marker interface instead of an ad hoc method.
6. **Decide explicitly whether `runasimi` (and future apps) should adopt `WebDestination` /
   `BrowserNavigator` / `DeepLinkManager` from day one**, rather than leaving it as something an
   app picks up organically when it happens to need browser URL sync — otherwise the "single
   implementation, per-app configuration" goal will keep eroding as new apps are scaffolded.
7. **Delete or fix `WebRouteRegistry.registeredClasses`** — small cleanup, currently dead and
   the doc comment overstates what it's needed for.

## 5. Open questions for follow-up (not yet investigated)

- Whether Supabase's own `handleDeeplinks(intent)` call (Android, `MainActivity.kt:76`) and
  `DeepLinkManager.resolve` could ever race or duplicate work on the same intent — both consume
  `intent.data` independently.
- Whether iOS support (universal links / `onOpenURL`) is on the roadmap for either app; nothing
  in the current codebase handles it, so the design has never been pressure-tested against a
  third native deep-link mechanism.
- Whether `flyerboard`'s eventual Android/iOS launch will reuse `DeepLinkManager` as-is, or
  whether by then recommendation #2 above should already be in place so it adopts the unified
  shape from day one instead of copying Edifikana's current pattern.
