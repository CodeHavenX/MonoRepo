# Skill: Upgrade Package Versions

Systematically upgrade dependencies in `versions.properties` using refreshVersions, with iterative verification and proper documentation.

## Prerequisites

- The project uses [refreshVersions](https://splitties.github.io/refreshVersions/) for dependency management
- Versions are defined in `versions.properties`
- The `releaseAll` gradle task validates all projects compile and pass tests

## Process Overview

1. **Refresh available versions** - Run `./gradlew refreshVersions`
2. **Analyze and group packages** - Identify related packages that should be upgraded together
3. **Iterative upgrades** - Upgrade one group at a time, verify with `releaseAll`
4. **Handle failures** - Roll back failed upgrades, document issues
5. **Commit per group** - Create atomic commits for each successful upgrade
6. **Document blockers** - Add comments for packages that cannot be upgraded

## Step 1: Refresh Available Versions

```bash
./gradlew refreshVersions
```

This updates `versions.properties` with available version comments like:
```properties
version.example=1.0.0
##      # available=1.0.1
##      # available=1.1.0
```

## Step 2: Analyze and Group Packages

Group packages by these criteria:

### Group by Family (Must Stay in Sync)
Packages from the same library family should be upgraded together:
- All `supabase` packages
- All `log4j` packages
- All `roborazzi` packages
- `plugin.*` and `version.*` for the same library (e.g., Ktor, Dagger)

### Group by Risk Level
Order upgrades from low to high risk:

1. **Patch updates** (x.y.Z) - Usually safe
2. **Minor updates** (x.Y.0) - May have API changes
3. **Major updates** (X.0.0) - Breaking changes likely

### Known Dependencies
Some packages have upgrade dependencies:
- **KSP** must match **Kotlin** version
- **ComposablePreviewScanner** tied to **Roborazzi**
- **ui-tooling-preview** should match **JetBrains Compose**

## Step 3: Iterative Upgrade Process

For each group:

### 3.1 Edit versions.properties
Update the version and remove the `available` comments:

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

### 3.2 Verify with releaseAll
```bash
./gradlew releaseAll 2>&1 | tail -30
```

Check for `BUILD SUCCESSFUL` at the end.

### 3.3 On Success - Commit
```bash
git add versions.properties && git commit -m "[DEPS] Upgrade <package-name> to <version>

<optional details>

Co-Authored-By: Claude <noreply@anthropic.com>"
```

### 3.4 On Failure - Roll Back and Document
```bash
git checkout versions.properties
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
6. **Dagger/Hilt** - Check AGP compatibility
7. **Kotlinx Serialization** - After Kotlin if WasmJS issues
8. **Ktor** - After Kotlin if WasmJS issues
9. **JetBrains Compose** - Coordinate with ui-tooling-preview
10. **AndroidX Compose** - UI, Foundation
11. **Kotlin + KSP** - Major undertaking, do last

## Packages with Special Handling

### Kotlin
Requires manual update of `kotlin-stdlib-wasm-js` workaround in multiple gradle files.
See: https://github.com/CodeHavenX/MonoRepo/issues/228

### Roborazzi
Blocked by version detection issue with ComposablePreviewScanner.
See: https://github.com/takahirom/roborazzi/issues/753

### Navigation Compose (JetBrains)
Does not update automatically from refreshVersions. Check manually:
https://mvnrepository.com/artifact/org.jetbrains.androidx.navigation/navigation-compose

## Example Session

```bash
# 1. Refresh versions
./gradlew refreshVersions

# 2. Read current versions
cat versions.properties

# 3. Edit a group (e.g., Log4j)
# Update: version.org.apache.logging.log4j..* from 2.25.2 to 2.25.3

# 4. Verify
./gradlew releaseAll 2>&1 | tail -30

# 5. If successful, commit
git add versions.properties
git commit -m "[DEPS] Upgrade Log4j packages to 2.25.3

Co-Authored-By: Claude <noreply@anthropic.com>"

# 6. If failed, roll back and document
git checkout versions.properties
# Add failure comment to versions.properties

# 7. Repeat for next group
```

## Final Checklist

- [ ] All successful upgrades committed with descriptive messages
- [ ] All failed upgrades documented with error details and date
- [ ] All blocked upgrades have comments linking to relevant issues
- [ ] All pending upgrades have TODO comments
- [ ] Run `./gradlew refreshVersions` one final time to verify state
