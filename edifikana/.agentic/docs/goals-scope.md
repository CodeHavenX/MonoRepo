# Goals & Scope

## Goals & Scope

Edifikana is a mobile-first property management application targeting Android, iOS, and web, for tracking work, finances, and resident information across a portfolio of 10–50 properties managed by small teams.

### Goals

- **Property & Unit Management:** Enable admins to organize properties (single-family, apartment buildings, condominiums) with full unit-level tracking including occupants, financials, tasks, and documents.
- **Task Management:** Provide a unified task system tied to specific units or common areas, supporting creation, assignment, prioritization, and status tracking by admin/staff.
- **Financial Tracking:** Record rent, HOA dues, and utility payments per unit with payment history. In scope: recording payment status (paid, overdue, pending) and viewing payment history per unit. Out of scope: generating reports, exporting data, invoicing, or any aggregation beyond per-unit payment status.
- **Document Library:** Organize and store leases, contracts, inspection reports, and HOA documents per property and unit.
- **Team Management:** Allow owners to invite admins and staff, manage roles, and handle organization membership via invite codes and email flows.
- **Resident Experience:** Give residents a simplified view to see their unit and access their documents.
- **Notifications:** Allow users to configure basic in-app notification preferences (e.g., enable/disable notifications per event type). No push notifications, email notifications, or external delivery channels in MVP.
- **Data Privacy & Security:** Protect resident personal information, financial records, and legal documents in compliance with GDPR principles. All data is access-controlled via role-based permissions. Sensitive data is never exposed to unauthorized users or transmitted without authentication. Data subjects have the right to access and erasure of their personal data (erasure via support flow in MVP; self-service deferred).
- **Multi-role Access Control:** Enforce role-based access (Owner, Admin, Staff, Resident) with appropriate permissions at each level.
- **Four-stage Deployment:** Support reliable delivery through Local → Integration → Staging → Production pipeline with automated CI/CD.

### Out of Scope

- Full accounting, invoicing, or financial reporting beyond payment status tracking
- Large enterprise or multi-tenant platforms (target: small teams, 10–50 properties)
- Advanced analytics or business intelligence dashboards
- Self-service account deletion (MVP: contact support flow only)
- Notification delivery beyond basic in-app notification preferences
- Guest management and maintenance request flows (deferred from initial MVP)
- Direct payment processing or integration with payment gateways
