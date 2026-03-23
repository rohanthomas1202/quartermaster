# Quartermaster UI Redesign — Design Specification

**Date:** 2026-03-23
**Status:** Approved
**Scope:** Visual overhaul of all frontend components — no backend changes

## Problem

The current UI uses unstyled inline React styles with no design system. It's functional but visually flat — no brand identity, no visual hierarchy, no polish. It looks like a prototype, not a product.

## Solution

A complete visual overhaul using Tailwind CSS with a dark & vibrant design language — deep navy backgrounds, saturated accent colors, gradients, and subtle glows. The layout shifts from matrix-centered to list-first with a stats bar, matching the information density and scanability of tools like Linear and Discord.

## Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Visual direction | Dark & Vibrant | Deep navy + saturated accents. Bold, energetic, professional. |
| CSS framework | Tailwind CSS | Utility-first, fast iteration, excellent dark mode primitives. |
| Layout (DRAFT view) | List-first + stats bar | Scannable item rows with quadrant badges + RCDO trail. Matrix available as toggle. |
| Add/edit form | Slide-over panel | Slides from right, preserves context, supports rapid sequential entry. |

## Color System

### Base Palette

| Token | Value | Usage |
|-------|-------|-------|
| `bg-primary` | `#0f0f1a → #1a1a2e → #16213e` (gradient) | Page background |
| `surface` | `rgba(255,255,255,0.03–0.06)` | Cards, item rows |
| `border` | `rgba(255,255,255,0.06–0.10)` | Subtle borders |
| `text-primary` | `#f1f5f9` | Titles, primary text |
| `text-secondary` | `#94a3b8` | Descriptions, labels |
| `text-muted` | `#64748b` | Hints, metadata |

### Quadrant Colors

| Quadrant | Accent | Tint Background | Badge Background |
|----------|--------|-----------------|------------------|
| Do First (U+I) | `#ef4444` | `#7f1d1d` at 20-30% | `#7f1d1d50` border `#ef444440` |
| Schedule (I) | `#818cf8` | `#1e1b4b` at 20-30% | `#1e1b4b50` border `#6366f140` |
| Delegate (U) | `#f59e0b` | `#78350f` at 20-30% | `#78350f50` border `#f59e0b40` |
| Eliminate | `#9ca3af` | `#374151` at 20-30% | `#37415150` border `#6b728040` |

### Status Colors

| Status | Color | Usage |
|--------|-------|-------|
| DRAFT | `#22c55e → #16a34a` gradient | Status badge |
| LOCKED | `#f59e0b → #d97706` gradient | Status badge |
| RECONCILING | `#a78bfa → #7c3aed` gradient | Status badge |
| RECONCILED | `#64748b` | Status badge (muted) |
| Completed | `#22c55e` | Reconciliation item border + dot |
| Partial | `#f59e0b` | Reconciliation item border + dot |
| Not Done | `#ef4444` | Reconciliation item border + dot |
| Pending | `#64748b` | Reconciliation item border + dot |

### Brand

| Element | Value |
|---------|-------|
| Logo | "Q" in white on `#7c3aed → #6d28d9` gradient, 6px radius |
| Wordmark | "Quartermaster" with `#c4b5fd → #818cf8` gradient text |
| Primary action | `#7c3aed → #6d28d9` gradient with `box-shadow: 0 0 20px rgba(124,58,237,0.3)` |

## Component Specifications

### Shared: Top Navigation Bar

Present on all views. Flex row with:
- **Left:** Q logo (28×28 gradient square) + "Quartermaster" gradient text + optional breadcrumb (e.g., "/ Manager")
- **Right:** Week selector (pill with ← date →), status badge (gradient pill with uppercase text), view toggle (List/Matrix segmented control, DRAFT only)
- **Style:** `border-bottom: 1px solid rgba(255,255,255,0.08)`, padding 12px 24px

### Shared: Week Selector

Contained in a surface-colored pill (`bg-white/6 border border-white/10 rounded-lg`). Arrow buttons are muted text that highlight on hover. Date displayed as "Mar 23 – 29" format.

### View 1: Commit Entry (DRAFT)

**Stats Bar** — Horizontal row below nav. Three stat blocks + action button:
1. **Commits** — total commit count for the week (purple accent, `#a78bfa`)
2. **Do First** — count of items in the urgent+important quadrant (red accent, `#f87171`)
3. **Carried** — count of items carried forward from prior week, i.e. items with `carriedFromId` set (amber accent, `#fbbf24`)

Each stat block: 36×36 icon box with quadrant-tinted gradient background and border, large number in accent color, label + sublabel in secondary/muted text. Right-aligned "+ New Commit" button with primary gradient + glow shadow.

**Item List** — Vertical stack of item rows. Each row:
- Surface background with gradient (`bg-white/6 → bg-white/3`)
- Border `border-white/8`, border-radius 10px
- Left border 3px solid in quadrant color
- Glowing dot (8×8, quadrant color, `box-shadow: 0 0 8px` at 50% opacity)
- Title (14px, `text-primary`, font-weight 500)
- Description (11px, `text-muted`)
- Quadrant badge (uppercase, 10px, quadrant tint background + border)
- RCDO breadcrumb trail (11px, `text-muted`, "Rally Cry › Objective" format)
- Edit/delete icon buttons (28×28, surface background, visible on row hover)

**List/Matrix Toggle** — Segmented control in nav. "List" is default active (surface highlight). "Matrix" switches to the Eisenhower 2×2 grid view (same as current but restyled with dark theme colors).

**Slide-Over Panel** (New Commit form):
- Slides in from right edge, 50% viewport width
- Background: surface gradient with left border `border-white/15`
- Shadow: `-8px 0 24px rgba(0,0,0,0.5)`
- Main content dims to 30% opacity behind via a backdrop overlay div
- **Implementation:** Rendered inline as a sibling to the main content, NOT via a React portal. The slide-over is a fixed-position overlay within the `CommitEntryView` component tree. This avoids portal complications in the module federation context. The backdrop is a full-screen fixed div with `bg-black/70` that sits behind the panel.
- Form fields: title input, description textarea, RCDO cascade selector (3 dropdowns in flex row), urgency toggle (two buttons: "Urgent" / "Not"), importance toggle (same pattern), Add Commit button (primary gradient), Cancel button (outline)
- Active toggle buttons use quadrant tint backgrounds; inactive use surface
- After submit: form clears, stays open for next entry
- Close on Cancel, Escape key, or clicking dimmed backdrop

### View 2: Locked

Same nav bar with amber LOCKED badge. Below nav:

**Alert Banner** — Full-width rounded card with amber tint background (`bg-amber-900/10 border border-amber-500/20`). Lock icon (🔒), bold title "Week is in progress", subtitle "Commits are locked. Reconciliation opens Friday 5:00 PM."

**Item List** — Same structure as DRAFT but:
- Slightly reduced opacity (0.85) on items
- No edit/delete action buttons
- No "+ New Commit" button
- No view toggle (list only)

### View 3: Reconciliation

Same nav bar with purple RECONCILING badge. Items displayed as reconciliation cards:

**Reconciliation Item** — Larger card with:
- Color-coded background gradient based on current completion status (green/amber/red/gray tints)
- Left border 3px solid in status color
- Title row: item title + status dropdown (styled select with status-colored background + border)
- Notes input below title (surface background, placeholder "Notes (optional)")
- Status dropdown options: Completed, Partial, Not Done, Pending

**Summary Bar** — Horizontal flex row in surface card:
- Colored dots (10×10) with counts: Completed (green), Partial (amber), Not Done (red), Pending (gray)
- Total count right-aligned

**Submit Button** — Centered, primary gradient with glow. "Submit Reconciliation" text. Subtitle below: "Partial and Not Done items will carry forward to next week."

### View 4: Manager Dashboard

Same nav bar with "/ Manager" breadcrumb. No status badge.

**Summary Cards** — 4-column grid of stat cards:
- Each card: tinted gradient background + subtle border, large number in accent color, label in muted text
- Cards: Reconciled (green), Reconciling (purple), Locked (amber), Avg Completion (blue, shows percentage)

**Team Table** — Surface card with rounded corners, internal grid layout:
- Header row: uppercase 10px labels (Member, Status, Done, Partial, Not Done) in muted text
- Data rows: avatar circle (28×28, gradient background, initials in white), name, status badge (tinted pill), completion counts (colored numbers)
- Rows separated by `border-bottom: 1px solid rgba(255,255,255,0.06)`

**RCDO Alignment** — Surface card with gradient progress bars:
- Each rally cry: name left-aligned, "N commits (N%)" right-aligned
- Progress bar: 6px height, surface track, gradient fill (purple/blue/green varying per rally cry)

## Implementation Approach

### Tailwind Setup

Install Tailwind CSS v4 (current release) with the Vite plugin (`@tailwindcss/vite`). Tailwind v4 uses CSS-first configuration — no `tailwind.config.js` or `postcss.config.js` needed. Custom theme values (colors, gradients) are defined via `@theme` in the CSS file.

- Import `web/src/index.css` in `web/src/main.tsx`
- `index.css` contains: `@import "tailwindcss"`, `@theme` block with custom colors, and global styles (background gradient, scrollbar styling)
- Font: system-ui stack (no custom fonts)

### File Changes

All changes are in `web/` — no backend modifications.

**New files:**
- `web/src/index.css` — Tailwind import, `@theme` with custom colors, global styles (background gradient, scrollbar)
- `web/src/components/SlideOver.tsx` — Reusable slide-over panel component (fixed-position, no portal)
- `web/src/components/StatusBadge.tsx` — Reusable status badge component
- `web/src/components/StatCard.tsx` — Reusable stat card component
- `web/src/components/NavBar.tsx` — Shared navigation bar

**Modified files (replace inline styles with Tailwind classes):**
- `web/src/main.tsx` — add `import './index.css'`
- `web/src/WeeklyCommitsApp.tsx` — wrap routes in NavBar layout, pass status/breadcrumb props
- `web/src/components/CommitEntryView.tsx`
- `web/src/components/CommitItemForm.tsx`
- `web/src/components/RcdoCascadeSelector.tsx`
- `web/src/components/EisenhowerMatrix.tsx`
- `web/src/components/WeekSelector.tsx`
- `web/src/components/LockedView.tsx`
- `web/src/components/ReconciliationView.tsx`
- `web/src/components/ReconciliationItem.tsx`
- `web/src/components/ManagerDashboard.tsx`
- `web/src/components/TeamSummaryCards.tsx`
- `web/src/components/RcdoAlignmentChart.tsx`

### Testing

Most existing tests query by text content, aria-labels, and user interactions — these remain valid since we're changing visual presentation, not behavior. However:

- **DOM restructuring risks:** Moving the week selector into a shared `NavBar` and wrapping the form in a `SlideOver` changes the component tree. Tests for `CommitEntryView` and `LockedView` that render the component and query for week selector elements may need the `NavBar` to be rendered as part of the tree, or the week selector to be passed as a prop. Review each test file after restyling and update selectors if needed.
- **Slide-over visibility:** Tests that check for form fields in `CommitEntryView` need to account for the slide-over being conditionally rendered (triggered by the "+ New Commit" button click).
- **New components:** `NavBar`, `SlideOver`, `StatusBadge`, and `StatCard` do not need dedicated test files — they are presentational wrappers tested through the view-level tests that render them.
- **Tailwind in tests:** Tailwind v4 with Vite generates CSS at build time. In vitest/jsdom tests, CSS classes are present in the DOM but not visually rendered — this is fine. No special Tailwind test configuration is needed.

## What This Does NOT Change

- No backend changes — Java service, API, database unchanged
- No new routes or state management — Zustand stores unchanged
- No new functionality — same views, same lifecycle, same data
- No new dependencies besides Tailwind CSS v4 (`tailwindcss`, `@tailwindcss/vite`)
