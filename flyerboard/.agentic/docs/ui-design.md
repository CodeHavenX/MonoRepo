# UI Design

## Theme: "Neighborhood Zine"

Bold, energetic, and grassroots — inspired by hand-made flyers and community art. Designed to feel alive and community-driven rather than corporate or municipal.

---

## Color Palette

| Role            | Color Name     | Hex       |
|-----------------|----------------|-----------|
| Primary         | Electric Indigo | `#4F46E5` |
| Secondary       | Hot Coral      | `#F43F5E` |
| Background      | Near-White     | `#FAFAFA` |
| Surface         | White          | `#FFFFFF` |
| Accent          | Lime           | `#84CC16` |
| Text (primary)  | Near-Black     | `#111827` |
| Text (muted)    | Cool Grey      | `#6B7280` |

Cards use bold colored top-borders drawn from the primary/secondary/accent palette to give each flyer a distinct, poster-like identity.

---

## Typography

- **Headlines:** Space Grotesk — bold weights, slightly unconventional, energetic
- **Body / UI:** Inter — highly legible, clean complement to Space Grotesk

---

## Layout & Style

- **Flyer grid:** Masonry layout to accommodate varying flyer heights (images vs. PDFs)
- **Cards:** Rounded corners (`border-radius: 12px`), drop shadow, bold colored top-border accent
- **Spacing:** Generous padding inside cards; tighter grid gaps to create a dense, busy-board feel
- **Buttons:** Filled with primary or secondary color, rounded pill shape for CTAs; ghost/outline style for secondary actions
- **Badges/tags:** Lime accent for status labels (e.g. "New", "Expires soon"), coral for moderation states (e.g. "Pending", "Rejected")
- **Icons:** Outlined icon set (e.g. Lucide or Material Symbols Outlined) for consistency

---

## Key Screens & Application

| Screen              | Notes |
|---------------------|-------|
| Browse (home)       | Masonry grid of flyer cards; search bar prominent at top; filter chips for category/status |
| Flyer detail        | Full-width image or PDF iframe; metadata below; edit button for owners |
| Upload / Edit form  | Clean single-column form; drag-and-drop file zone with dashed coral border |
| Admin queue         | Table or list view; coral "Pending" badges; approve/reject action buttons |
| Auth (sign in/up)   | Centered card on indigo gradient background |
