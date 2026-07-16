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

- **Medium-fidelity (local HTML):** `edifikana/mockups/<screen>.html` — interactive click-through prototype, version-controlled, adaptive across mobile / tablet / desktop.
- **High-fidelity:** in progress.

---

## Screen Inventory

55 screens total. Issue links reference [github.com/CodeHavenX/MonoRepo](https://github.com/CodeHavenX/MonoRepo/issues).

**MVP scope:** 38 screens · **Deferred:** 17 screens · **No mockup yet (MVP):** 2 screens

---

## MVP Screens (38)

### Authentication (9)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Sign In | pre-existing | [sign-in.html](mockups/sign-in.html) | — |
| Sign Up | pre-existing | [sign-up.html](mockups/sign-up.html) | — |
| Email Verification (OTP) | pre-existing | [otp-validation.html](mockups/otp-validation.html) | — |
| Password Reset | [#390](https://github.com/CodeHavenX/MonoRepo/issues/390) | [password-reset.html](mockups/password-reset.html) | — |
| Password Reset Confirmation | [#390](https://github.com/CodeHavenX/MonoRepo/issues/390) | [password-reset-confirmation.html](mockups/password-reset-confirmation.html) | — |
| Set New Password | [#496](https://github.com/CodeHavenX/MonoRepo/issues/496) | [set-new-password.html](mockups/set-new-password.html) | — |
| Invitation Accept (landing) | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) | [invitation-accept.html](mockups/invitation-accept.html) | — |
| Invitation Accept (accept/decline) | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) | [invitation-accept-authenticated.html](mockups/invitation-accept-authenticated.html) | — |
| Invitation Accept (error) | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) | [invitation-accept-error.html](mockups/invitation-accept-error.html) | — |

### Organization Management (6)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Organization Picker | pre-existing | [organization-picker.html](mockups/organization-picker.html) | — |
| Create Organization | pre-existing | [create-organization.html](mockups/create-organization.html) | — |
| Join Organization | [#391](https://github.com/CodeHavenX/MonoRepo/issues/391) | [join-organization.html](mockups/join-organization.html) | — |
| My Organizations | [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) | [my-organizations.html](mockups/my-organizations.html) | — |
| Organization Detail | [#284](https://github.com/CodeHavenX/MonoRepo/issues/284), [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) | [organization-detail.html](mockups/organization-detail.html) | — |
| Transfer Ownership | [#392](https://github.com/CodeHavenX/MonoRepo/issues/392) | [transfer-ownership.html](mockups/transfer-ownership.html) | — |

### Dashboard (1)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Dashboard | [#395](https://github.com/CodeHavenX/MonoRepo/issues/395), [#404](https://github.com/CodeHavenX/MonoRepo/issues/404) | [dashboard.html](mockups/dashboard.html) | — |

### Properties (9)

> Note: `add-property.html` serves dual purpose as Add and Edit (PROP-01 reuses the same layout). `add-common-area.html` has no mockup yet (AREA-04, TBD issue).

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Property List | [#356](https://github.com/CodeHavenX/MonoRepo/issues/356) | [property-list.html](mockups/property-list.html) | — |
| Property Detail | [#356](https://github.com/CodeHavenX/MonoRepo/issues/356), [#370](https://github.com/CodeHavenX/MonoRepo/issues/370) | [property-detail.html](mockups/property-detail.html) | — |
| Add / Edit Property | pre-existing, PROP-01 (TBD) | [add-property.html](mockups/add-property.html) | — |
| Delete Property confirmation | PROP-03 (TBD) | _(no mockup yet)_ | — |
| Unit List | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) | [unit-list.html](mockups/unit-list.html) | — |
| Unit Detail _(Info + Tasks tabs only)_ | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) | [unit-detail.html](mockups/unit-detail.html) | — |
| Add / Edit Unit | [#398](https://github.com/CodeHavenX/MonoRepo/issues/398) | [add-unit.html](mockups/add-unit.html) | — |
| Common Area Detail | [#427](https://github.com/CodeHavenX/MonoRepo/issues/427) | [common-area-detail.html](mockups/common-area-detail.html) | — |
| Add / Edit Common Area | AREA-04 (TBD) | _(no mockup yet)_ | — |

### Tasks (3)

> Request Review is deferred (Epic 4.3 — maintenance requests backend deferred).

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Task List | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) | [task-list.html](mockups/task-list.html) | — |
| Task Detail | [#400](https://github.com/CodeHavenX/MonoRepo/issues/400) | [task-detail.html](mockups/task-detail.html) | — |
| Add / Edit Task | [#402](https://github.com/CodeHavenX/MonoRepo/issues/402) | [add-task.html](mockups/add-task.html) | — |

### Resident Experience (5)

> Maintenance request submission and tracking (submit-request, my-requests, request-detail) are deferred with the maintenance requests backend. Resident nav is 3 tabs: Home / Documents / Profile.

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Resident Home | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) | [resident-home.html](mockups/resident-home.html) | — |
| My Unit | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) | [my-unit.html](mockups/my-unit.html) | — |
| My Documents | [#432](https://github.com/CodeHavenX/MonoRepo/issues/432) | [my-documents.html](mockups/my-documents.html) | — |
| Resident Profile | [#408](https://github.com/CodeHavenX/MonoRepo/issues/408) | [resident-profile.html](mockups/resident-profile.html) | — |
| Resident Change Password | pre-existing | [resident-change-password.html](mockups/resident-change-password.html) | — |

### Settings & Admin (7)

| Screen | Issue | Med-fi mock | Hi-fi mock |
|--------|-------|-------------|------------|
| Settings | [#435](https://github.com/CodeHavenX/MonoRepo/issues/435) | [settings.html](mockups/settings.html) | — |
| Profile | [#409](https://github.com/CodeHavenX/MonoRepo/issues/409) | [profile.html](mockups/profile.html) | — |
| Edit Profile | [#409](https://github.com/CodeHavenX/MonoRepo/issues/409) | [edit-profile.html](mockups/edit-profile.html) | — |
| Change Password | pre-existing | [change-password.html](mockups/change-password.html) | — |
| Team Management | [#434](https://github.com/CodeHavenX/MonoRepo/issues/434) | [team-management.html](mockups/team-management.html) | — |
| Notification Preferences | pre-existing | [notification-preferences.html](mockups/notification-preferences.html) | — |
| Delete Account | [#409](https://github.com/CodeHavenX/MonoRepo/issues/409) | [delete-account.html](mockups/delete-account.html) | — |

---

## Deferred Screens (17)

These screens are out of MVP scope. Mockups exist for reference but will not be implemented until the corresponding post-MVP phase ships.

### Maintenance Requests — Admin Review (1)
> Deferred: Epic 4.3. Depends on backend decision (EventLog vs. dedicated `maintenance_requests` table, #416).

| Screen | Issue | Med-fi mock |
|--------|-------|-------------|
| Request Review | [#428](https://github.com/CodeHavenX/MonoRepo/issues/428) | [request-review.html](mockups/request-review.html) |

### Resident Maintenance Requests (3)
> Deferred: RES-07–RES-11. Same backend dependency as above.

| Screen | Issue | Med-fi mock |
|--------|-------|-------------|
| Submit Request | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) | [submit-request.html](mockups/submit-request.html) |
| My Requests | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) | [my-requests.html](mockups/my-requests.html) |
| Request Detail | [#431](https://github.com/CodeHavenX/MonoRepo/issues/431) | [request-detail.html](mockups/request-detail.html) |

### Guests — Phase 6 (4)

| Screen | Issue | Med-fi mock |
|--------|-------|-------------|
| Guest List | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) | [guest-list.html](mockups/guest-list.html) |
| Guest Check-In | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) | [guest-checkin.html](mockups/guest-checkin.html) |
| Guest Check-Out | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) | [guest-checkout.html](mockups/guest-checkout.html) |
| Guest Detail | [#429](https://github.com/CodeHavenX/MonoRepo/issues/429) | [guest-detail.html](mockups/guest-detail.html) |

### Occupants — Phase 5 (3)

| Screen | Issue | Med-fi mock |
|--------|-------|-------------|
| Occupant List | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) | [occupant-list.html](mockups/occupant-list.html) |
| Add Occupant | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) | [add-occupant.html](mockups/add-occupant.html) |
| Occupant Detail | [#405](https://github.com/CodeHavenX/MonoRepo/issues/405) | [occupant-detail.html](mockups/occupant-detail.html) |

### Financials — Phase 7 (3)

| Screen | Issue | Med-fi mock |
|--------|-------|-------------|
| Financial Overview | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) | [financial-overview.html](mockups/financial-overview.html) |
| Payment List | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) | [payment-list.html](mockups/payment-list.html) |
| Add Payment Record | [#406](https://github.com/CodeHavenX/MonoRepo/issues/406) | [add-payment.html](mockups/add-payment.html) |

### Documents — Phase 8 (3)

| Screen | Issue | Med-fi mock |
|--------|-------|-------------|
| Document Library | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) | [document-library.html](mockups/document-library.html) |
| Document Viewer | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) | [document-viewer.html](mockups/document-viewer.html) |
| Upload Document | [#407](https://github.com/CodeHavenX/MonoRepo/issues/407) | [upload-document.html](mockups/upload-document.html) |
