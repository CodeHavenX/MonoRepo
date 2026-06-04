# Edifikana

This is the documentation for Edifikana, a software project focused on building a comprehensive application for
managing and organizing various aspects of a business. The documentation covers the high-level application design,
visual design system, back-end architecture, and front-end architecture.

## Wiki Links

- [High-Level Application Design](https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/Projects/Edifikana/Design/application-design)
- [Visual Design System](https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/Projects/Edifikana/Design/visual-design-system)
- [Back-End Architecture](https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/Projects/Edifikana/Architecture/back-end)
- [Front-End Architecture](https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/Projects/Edifikana/Architecture/front-end)

## Mocks

For a high level overview of the application's user interface and user experience, please refer to the
following [mockups](mockups/index.html).

- **Medium-fidelity (local HTML):** `edifikana/mockups/<screen>.html` — interactive click-through prototype, version-controlled.
- **High-fidelity (Stitch):** [Stitch project 17740130412186841222](https://stitch.google.com/projects/17740130412186841222) — rendered designs with Manrope font, professional blue palette, and polished components.

---

## Screen Inventory

53 screens total. Issue links reference [github.com/CodeHavenX/MonoRepo](https://github.com/CodeHavenX/MonoRepo/issues).

### Authentication (7)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Sign In | pre-existing | [sign-in.html](mockups/sign-in.html) | — |
| Sign Up | pre-existing | [sign-up.html](mockups/sign-up.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Email Verification (OTP) | pre-existing | [otp-validation.html](mockups/otp-validation.html) | — |
| Password Reset | [#390](https://github.com/CodeHavenX/MonoRepo/issues/390) | [password-reset.html](mockups/password-reset.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Password Reset Confirmation | [#390](https://github.com/CodeHavenX/MonoRepo/issues/390) | [password-reset-confirmation.html](mockups/password-reset-confirmation.html) | — |
| Set New Password | [#496](https://github.com/CodeHavenX/MonoRepo/issues/496) | [set-new-password.html](mockups/set-new-password.html) | — |
| Invitation Accept | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) | [invitation-accept.html](mockups/invitation-accept.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |

### Organization Management (6)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Organization Picker | pre-existing | [organization-picker.html](mockups/organization-picker.html) | — |
| Create Organization | pre-existing | [create-organization.html](mockups/create-organization.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Join Organization | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) | [join-organization.html](mockups/join-organization.html) | — |
| My Organizations | [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) | [my-organizations.html](mockups/my-organizations.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Organization Detail | [#284](https://github.com/CodeHavenX/MonoRepo/issues/284), [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) | [organization-detail.html](mockups/organization-detail.html) | — |
| Transfer Ownership | [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) | [transfer-ownership.html](mockups/transfer-ownership.html) | — |

### Dashboard (1)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Dashboard | [#395](https://github.com/CodeHavenX/MonoRepo/issues/395), [#404](https://github.com/CodeHavenX/MonoRepo/issues/404) | [dashboard.html](mockups/dashboard.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |

### Properties (7)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Property List | [#356](https://github.com/CodeHavenX/MonoRepo/issues/356) | [property-list.html](mockups/property-list.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Property Detail | [#356](https://github.com/CodeHavenX/MonoRepo/issues/356), [#370](https://github.com/CodeHavenX/MonoRepo/issues/370) | [property-detail.html](mockups/property-detail.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Add / Edit Property | pre-existing | [add-property.html](mockups/add-property.html) | — |
| Unit List | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) | [unit-list.html](mockups/unit-list.html) | — |
| Unit Detail | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) | [unit-detail.html](mockups/unit-detail.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Add / Edit Unit | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) | [add-unit.html](mockups/add-unit.html) | — |
| Common Area Detail | [#427](https://github.com/CodeHavenX/MonoRepo/issues/427) | [common-area-detail.html](mockups/common-area-detail.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |

### Tasks (4)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Task List | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) | [task-list.html](mockups/task-list.html) | — |
| Task Detail | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) | [task-detail.html](mockups/task-detail.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Add / Edit Task | [#402](https://github.com/CodeHavenX/MonoRepo/issues/402) | [add-task.html](mockups/add-task.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Request Review | [#428](https://github.com/CodeHavenX/MonoRepo/issues/428) | [request-review.html](mockups/request-review.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |

### Guests (4)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Guest List | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) | [guest-list.html](mockups/guest-list.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Guest Check-In | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) | [guest-checkin.html](mockups/guest-checkin.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Guest Check-Out | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) | [guest-checkout.html](mockups/guest-checkout.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Guest Detail | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) | [guest-detail.html](mockups/guest-detail.html) | — |

### Occupants (3)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Occupant List | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) | [occupant-list.html](mockups/occupant-list.html) | — |
| Add Occupant | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) | [add-occupant.html](mockups/add-occupant.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Occupant Detail | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) | [occupant-detail.html](mockups/occupant-detail.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |

### Financials (3)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Financial Overview | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) | [financial-overview.html](mockups/financial-overview.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Payment List | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) | [payment-list.html](mockups/payment-list.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Add Payment Record | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) | [add-payment.html](mockups/add-payment.html) | — |

### Documents (3)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Document Library | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) | [document-library.html](mockups/document-library.html) | — |
| Document Viewer | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) | [document-viewer.html](mockups/document-viewer.html) | — |
| Upload Document | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) | [upload-document.html](mockups/upload-document.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |

### Settings & Admin (7)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Settings | [#435](https://github.com/CodeHavenX/MonoRepo/issues/435) | [settings.html](mockups/settings.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Profile | [#409](https://github.com/CodeHavenX/MonoRepo/issues/409) | [profile.html](mockups/profile.html) | — |
| Edit Profile | [#409](https://github.com/CodeHavenX/MonoRepo/issues/409) | [edit-profile.html](mockups/edit-profile.html) | — |
| Change Password | pre-existing | [change-password.html](mockups/change-password.html) | — |
| Team Management | [#434](https://github.com/CodeHavenX/MonoRepo/issues/434) | [team-management.html](mockups/team-management.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Notification Preferences | pre-existing | [notification-preferences.html](mockups/notification-preferences.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Delete Account | [#409](https://github.com/CodeHavenX/MonoRepo/issues/409) | [delete-account.html](mockups/delete-account.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |

### Resident Views (8)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Resident Home | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) | [resident-home.html](mockups/resident-home.html) | — |
| My Unit | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) | [my-unit.html](mockups/my-unit.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Submit Request | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) | [submit-request.html](mockups/submit-request.html) | — |
| My Requests | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) | [my-requests.html](mockups/my-requests.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Request Detail | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) | [request-detail.html](mockups/request-detail.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| My Documents | [#432](https://github.com/CodeHavenX/MonoRepo/issues/432) | [my-documents.html](mockups/my-documents.html) | — |
| Resident Profile | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) | [resident-profile.html](mockups/resident-profile.html) | [Stitch](https://stitch.google.com/projects/17740130412186841222) |
| Resident Change Password | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) | [resident-change-password.html](mockups/resident-change-password.html) | — |

---

## Hi-fi coverage summary

29 of 53 screens have a high-fidelity version in the Stitch project. The 24 screens still needing hi-fi renders are:

| Category | Missing hi-fi screens |
|----------|-----------------------|
| Auth | Sign In, Email Verification, Password Reset Confirmation, Set New Password |
| Org Management | Organization Picker, Join Organization, Organization Detail, Transfer Ownership |
| Properties | Add/Edit Property, Unit List, Add/Edit Unit |
| Tasks | Task List |
| Guests | Guest Detail |
| Occupants | Occupant List |
| Financials | Add Payment Record |
| Documents | Document Library, Document Viewer |
| Settings | Profile, Edit Profile, Change Password |
| Resident | Resident Home, Submit Request, My Documents, Resident Change Password |
