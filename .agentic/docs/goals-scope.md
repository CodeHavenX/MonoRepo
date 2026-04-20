# Goals & Scope

## Goals & Scope

A community poster board web application for a neighborhood or city, where residents can browse and share flyers and posters for local events, announcements, and notices.

### Goals

- **Public browsing:** Allow anyone to view active flyers without requiring an account.
- **User uploads:** Allow registered users to upload flyers (images or PDFs) with a title, description, and optional expiry date.
- **User editing:** Allow the original uploader to edit their own flyers (title, description, image/PDF, expiry date).
- **Admin moderation:** Require admin approval before a flyer becomes publicly visible.
- **Archiving:** Automatically move expired flyers to an archive section that remains publicly browsable.
- **Authentication:** Support user registration and sign-in so uploads are tied to an identity.

### Out of Scope

- Mobile native apps (iOS/Android) — the Compose Multiplatform frontend targets web only in this phase.
- Comments, reactions, or social features on flyers.
- Multi-city or multi-neighborhood support — single community scope.
- Push or email notifications for users.
- Payment or monetization features.
- User-facing reporting or flagging of inappropriate content (admin handles moderation).
