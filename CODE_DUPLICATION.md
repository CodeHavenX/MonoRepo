# Code Duplication Analysis

This document describes known code duplications in the monorepo and explains why they exist.

## Summary

- **Before refactoring**: 36 duplicated files
- **After refactoring**: 9 duplicated files  
- **Files eliminated**: 27 files

## Eliminated Duplications

### Empty Platform-Specific DI Modules (27 files removed)

**Problem**: Multiple projects had empty `actual` implementations of `expect` Koin dependency injection modules that were identical across android, jvm, and wasmJs platforms.

**Solution**: Converted from `expect`/`actual` pattern to direct `val` declarations in `commonMain`. Since the modules were empty and identical across all platforms, there was no need for platform-specific implementations.

**Projects affected**:
- edifikana: ViewModelPlatformModule, ServicePlatformModule (6 files → 2 files)
- runasimi: ViewModelPlatformModule, ServicePlatformModule, ManagerPlatformModule (9 files → 3 files)
- templatereplaceme: ViewModelPlatformModule, ServicePlatformModule, ManagerPlatformModule (9 files → 3 files)
- framework-samples: ExtrasPlatformModule, ApplicationScreen (4 files → 2 files)

**Example**:
```kotlin
// Before (in commonMain):
internal expect val ViewModelPlatformModule: Module

// Before (in androidMain, jvmMain, wasmJsMain - identical):
internal actual val ViewModelPlatformModule = module { }

// After (in commonMain only):
internal val ViewModelPlatformModule = module { }
```

## Remaining Acceptable Duplications

The following duplications are acceptable due to limitations in Kotlin Multiplatform's source set hierarchy:

### 1. Framework Test Utilities (6 files)

**Files**:
- `framework/test/src/androidUnitTest/kotlin/com/cramsan/framework/test/Repository.kt`
- `framework/test/src/jvmTest/kotlin/com/cramsan/framework/test/Repository.kt`
- `framework/thread/src/androidInstrumentedTest/kotlin/.../ThreadUtilCommonTest.kt`
- `framework/thread/src/jvmTest/kotlin/.../ThreadUtilCommonTest.kt`
- `framework/assert/src/androidUnitTest/kotlin/.../AssertUtilCommonTest.kt`
- `framework/assert/src/jvmTest/kotlin/.../AssertUtilCommonTest.kt`

**Why they remain duplicated**:
- These are test files in different test trees (`test` vs `instrumentedTest` for Android)
- Kotlin MPP's default hierarchy template doesn't allow creating intermediate source sets that mix test trees
- Creating custom intermediate source sets conflicts with the default hierarchy template
- The duplication is minimal (2 copies each) and the files are small test utilities

**Recommendation**: Keep as-is. The duplication is acceptable given the constraints of KMP test source sets.

### 2. Framework UUID Stub Implementations (3 files)

**Files**:
- `framework/utils/src/androidMain/kotlin/com/cramsan/framework/utils/uuid/UUID.kt`
- `framework/utils/src/iosMain/kotlin/com/cramsan/framework/utils/uuid/UUID.kt`
- `framework/utils/src/jsMain/kotlin/com/cramsan/framework/utils/uuid/UUID.kt`

**Why they remain duplicated**:
- All three contain identical TODO implementations
- JVM has a different implementation using `java.util.UUID`
- Creating a custom intermediate source set for "all platforms except JVM" conflicts with the default hierarchy template
- The files are small (17 lines each)

**Recommendation**: These should be implemented properly rather than remaining as TODOs. Once implemented with platform-specific UUID generation, the duplication would be necessary and intentional.

## Best Practices

To avoid similar duplications in the future:

1. **Empty Platform Modules**: If a platform-specific module is empty and will remain empty across all platforms, don't use the `expect`/`actual` pattern. Define it directly in `commonMain`.

2. **Platform Differences**: Only use `expect`/`actual` when there are genuine platform-specific differences in implementation.

3. **Test Code**: Accept that some test duplication is necessary when working with different test trees (unit tests vs instrumented tests).

4. **TODO Implementations**: Don't use `expect`/`actual` for functions that are not yet implemented. Either implement them properly or define them in `commonMain` with a single TODO.
