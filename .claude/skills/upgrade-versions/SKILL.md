---
name: upgrade-versions
description: "Systematically upgrade dependencies in versions.properties and gradle/libs.versions.toml using refreshVersions, with iterative verification and proper documentation."
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# Skill: Upgrade Package Versions

Systematically upgrade dependencies in `versions.properties` and `gradle/libs.versions.toml` using refreshVersions, with iterative verification and proper documentation.

## Prerequisites

- The project uses [refreshVersions](https://splitties.github.io/refreshVersions/) for dependency management
- Most versions are defined in `versions.properties`
- Build-logic plugin versions are defined in `gradle/libs.versions.toml` (and mirrored in `versions.properties`)
- The `releaseAll --quiet` gradle task validates all projects compile and pass tests

## Version File Responsibilities

### `versions.properties`
Managed by refreshVersions. Covers most runtime and test dependencies. Available upgrades appear as:
```properties
version.example=1.0.0
##      # available=1.0.1
##      # available=1.1.0
```

### `gradle/libs.versions.toml`
Manually managed. Covers:
- **Build-logic plugin classpath deps**: AGP, Kotlin, KSP, Compose Multiplatform, Detekt, Ktor plugin, Roborazzi
- **Deps that cannot use `_` placeholder**: `detekt-rules-ktlint-wrapper`, `composable-preview-scanner`

Available upgrades in this file use the `## ⬆ =` comment style:
```toml
android-agp = "8.13.2"
##       ⬆ = "9.0.0"
##       ⬆ = "9.1.0"
```

> **Important:** The file header states: *"Plugin versions here match the versions in `versions.properties`. When updating a plugin version, update both files."*
> Versions for AGP, Kotlin, KSP, Compose Multiplatform, Detekt, Ktor plugin, and Roborazzi must be kept in sync between both files.

## Process Overview

1. **Refresh available versions** - Run `./gradlew refreshVersions`
2. **Check both files** - Read `versions.properties` AND `gradle/libs.versions.toml` for available upgrades
3. **Analyze and group packages** - Identify related packages that should be upgraded together
4. **Iterative upgrades** - Upgrade one group at a time, verify with `releaseAll --quiet`
5. **Handle failures** - Roll back failed upgrades, document issues
6. **Commit per group** - Create atomic commits for each successful upgrade
7. **Document blockers** - Add comments for packages that cannot be upgraded

## Step 1: Refresh Available Versions

```bash
./gradlew refreshVersions
```

This updates `versions.properties` with available version comments. Then read both files:

```bash
grep "available=" versions.properties
grep "⬆" gradle/libs.versions.toml
```

## Step 2: Analyze and Group Packages

Group packages by these criteria:

### Group by Family (Must Stay in Sync)
Packages from the same library family should be upgraded together:
- All `supabase` packages
- All `log4j` packages
- All `roborazzi` packages (including the version in `libs.versions.toml`)
- `plugin.*` and `version.*` for the same library (e.g., Ktor)
- Any package that appears in both `versions.properties` and `libs.versions.toml`

### Group by Risk Level
Order upgrades from low to high risk:

1. **Patch updates** (x.y.Z) - Usually safe
2. **Minor updates** (x.Y.0) - May have API changes
3. **Major updates** (X.0.0) - Breaking changes likely

### Known Dependencies
Some packages have upgrade dependencies:
- **KSP** must match **Kotlin** version — update both `versions.properties` and `libs.versions.toml`
- **ComposablePreviewScanner** tied to **Roborazzi** — both live in `libs.versions.toml`
- **ui-tooling-preview** should match **JetBrains Compose**
- **Roborazzi** appears in both files — update both when upgrading

## Step 3: Iterative Upgrade Process

For each group:

### 3.1 Edit the relevant file(s)

**`versions.properties`** — update the version and remove `available` comments:

**Before:**
```properties
version.example=1.0.0
##      # available=1.0.1
##      # available=1.1.0
```

**After:**
```properties
version.example=1.1.0
```

**`gradle/libs.versions.toml`** — update the version and remove `⬆` comments:

**Before:**
```toml
android-agp = "8.13.2"
##       ⬆ = "9.0.0"
##       ⬆ = "9.1.0"
```

**After:**
```toml
android-agp = "9.1.0"
```

If a package exists in **both files**, update both in the same step before verifying.

### 3.2 Verify with releaseAll
```bash
./gradlew --stop
./gradlew releaseAll --quiet 2>&1 | tail -30
```

Check for `BUILD SUCCESSFUL` at the end.

### 3.3 On Success - Commit
```bash
git add versions.properties gradle/libs.versions.toml && git commit -m "[DEPS] Upgrade <package-name> to <version>

<optional details>

Co-Authored-By: Claude <noreply@anthropic.com>"
```

### 3.4 On Failure - Roll Back and Document
```bash
git checkout versions.properties gradle/libs.versions.toml
```

Then add a comment documenting the failure (see Step 5).

## Step 4: Common Failure Patterns

### WasmJS Compiler Errors
```
Execution failed for task ':module:compileKotlinWasmJs'.
> Internal compiler error. See log for more details
```
**Cause:** Library version incompatible with current Kotlin/Wasm setup
**Solution:** Usually requires Kotlin upgrade first

### Android Build API Errors
```
com/android/build/api/artifact/ScopedArtifact$POST_COMPILATION_CLASSES
```
**Cause:** Library requires newer Android Gradle Plugin
**Solution:** Investigate AGP compatibility requirements

### Dependency Resolution Errors
```
Could not resolve <dependency>
```
**Cause:** Version mismatch between related dependencies
**Solution:** Ensure all related packages are upgraded together

## Step 5: Document Blocked Upgrades

Add comments above packages that cannot be upgraded:

### For Failed Upgrades
```properties
# <Package> <version> upgrade failed with <error type> (<date>).
# Error: <brief error description>
# <Reason/Solution>
version.example=1.0.0
##      # available=1.1.0
```

### For Known Blocked Upgrades
```properties
# <Reason for block>
# Blocked by: <link to issue>
version.example=1.0.0
##      # available=1.1.0
```

### For Pending/Not Attempted Upgrades
```properties
# <Package> upgrade not attempted (<date>).
# TODO: <what needs to happen first>
version.example=1.0.0
##      # available=1.1.0
```

### For Linked Packages
```properties
# Should be upgraded together with <other-package>
version.example=1.0.0
##      # available=1.1.0
```

## Recommended Upgrade Order

Based on typical dependency chains in this project:

1. **Log4j** - Logging, no dependencies
2. **AndroidX patches** - Camera, SQLite, Room, ExifInterface
3. **Testing libraries** - Robolectric, MockK, JUnit
4. **AndroidX minor** - Navigation, Activity, Lifecycle
5. **Supabase** - After Kotlin if WasmJS issues
6. **Kotlinx Serialization** - After Kotlin if WasmJS issues
7. **Ktor** - Update `versions.properties` and `libs.versions.toml` together
8. **Roborazzi** - Update `versions.properties` and `libs.versions.toml` together; ComposablePreviewScanner follows
9. **JetBrains Compose** - Update `libs.versions.toml`; coordinate with ui-tooling-preview in `versions.properties`
10. **AndroidX Compose** - UI, Foundation
11. **Detekt** - Update `versions.properties` and `libs.versions.toml` together
12. **AGP** - `libs.versions.toml` only; check KMP compatibility before upgrading
13. **Kotlin + KSP** - Update `versions.properties` and `libs.versions.toml` together; major undertaking, do last

## Packages with Special Handling

### Packages that exist in both files
These versions must be kept in sync between `versions.properties` and `gradle/libs.versions.toml`:

| Package | `versions.properties` key | `libs.versions.toml` key |
|---|---|---|
| AGP | `plugin.android` | `android-agp` |
| Kotlin | `version.kotlin` | `kotlin` |
| KSP | `plugin.com.google.devtools.ksp` | `ksp` |
| Compose Multiplatform | `plugin.org.jetbrains.compose` | `compose-multiplatform` |
| Detekt | `plugin.dev.detekt` | `detekt` |
| Ktor plugin | `plugin.io.ktor.plugin` | `ktor-plugin` |
| Roborazzi plugin | `plugin.io.github.takahirom.roborazzi` | `roborazzi` |

### Navigation Compose (JetBrains)
Does not update automatically from refreshVersions. Check manually:
https://mvnrepository.com/artifact/org.jetbrains.androidx.navigation/navigation-compose

### AGP (Android Gradle Plugin)
Available upgrades appear in `gradle/libs.versions.toml` using `## ⬆ =` comments. AGP 9.x currently blocked by a KMP plugin conflict — see the comment in `libs.versions.toml` for details.

## Example Session

```bash
# 1. Refresh versions
./gradlew refreshVersions

# 2. Check both files for available upgrades
grep "available=" versions.properties
grep "⬆" gradle/libs.versions.toml

# 3. Edit a group (e.g., Log4j — versions.properties only)
# Update: version.org.apache.logging.log4j..* from 2.25.2 to 2.25.3

# 4. Edit a group that spans both files (e.g., Roborazzi)
# Update versions.properties: version.io.github.takahirom.roborazzi..* from 1.60.0 to 1.61.0
# Update libs.versions.toml: roborazzi = "1.60.0" → "1.61.0" (remove ⬆ comments)

# 5. Verify
./gradlew releaseAll --quiet 2>&1 | tail -30

# 6. If successful, commit (include both files if both changed)
git add versions.properties gradle/libs.versions.toml
git commit -m "[DEPS] Upgrade Roborazzi to 1.61.0

Co-Authored-By: Claude <noreply@anthropic.com>"

# 7. If failed, roll back both files and document
git checkout versions.properties gradle/libs.versions.toml
# Add failure comment to the relevant file(s)

# 8. Repeat for next group
```

## Final Checklist

- [ ] All successful upgrades committed with descriptive messages
- [ ] Both `versions.properties` and `gradle/libs.versions.toml` updated for shared packages
- [ ] All failed upgrades documented with error details and date
- [ ] All blocked upgrades have comments linking to relevant issues
- [ ] All pending upgrades have TODO comments
- [ ] Run `./gradlew refreshVersions` one final time to verify state
