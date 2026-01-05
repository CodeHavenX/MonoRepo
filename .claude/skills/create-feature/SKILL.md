---
name: create-feature
description: "Create a new feature screen in the Edifikana front-end app. Use when asked to create a new screen, feature, or UI component with ViewModel pattern."
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# Create Feature - Front-End Feature Generator

## Purpose
This skill generates the boilerplate files for a new feature screen in the our Kotlin Multiplatform front-end application following the established MVVM architecture pattern.

## Required Information
Before creating a feature, gather:
1. **Feature name** (e.g., "SelectOrg", "AddProperty", "Notifications")
2. **Parent folder**. This is the directory where the new package will be created. If the feature is named "AddProperty", the package will be named `addproperty`. So the parent folder could be `edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/` for a path like `edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/addproperty/`.

Provide the user with some examples of valid feature names and parent folders but DO NOT provide them with options to select from. 

## Files to Create

For a feature named `{FeatureName}` in package `{packagePath}`:

Look at the templates in `.idea/fileTemplates/Compose Feature*` and create each folder by injecting the feature name and package path.

## Registration

After creating the files, run through the TODOs in the templates to complete each step. Some files may need to be moved, for example the
test files should be moved to the test source set.

## Verification

After creating all files, compile and run the tests to ensure everything is set up correctly.

## Examples

### Example 1: Creating "AddProperty" feature in home
- Feature name: `AddProperty`
- Parent folder: `edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/`

- Creating feature `AddProperty` in package `com.cramsan.edifikana.client.lib.features.addproperty` 
- Source folder is `edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/addproperty/`
- Files created:
  - `AddPropertyScreen.kt`
  - `AddPropertyViewModel.kt`
  - `AddPropertyUIState.kt`
  - `AddPropertyEvent.kt`
  - `AddPropertyViewModelTest.kt`
  - `AddPropertyScreen.preview.kt`

- Completing tasks in the TODOs in each file.