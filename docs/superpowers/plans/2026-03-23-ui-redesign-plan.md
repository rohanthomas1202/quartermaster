# UI Redesign Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restyle all frontend components from unstyled inline React styles to a dark & vibrant design using Tailwind CSS v4, with a layout shift from matrix-centered to list-first with stats bar and slide-over form.

**Architecture:** Install Tailwind CSS v4 with the Vite plugin. Create shared UI components (NavBar, SlideOver, StatusBadge, StatCard). Restyle all 11 existing components with Tailwind utility classes. No backend changes, no new routes, no state management changes.

**Tech Stack:** Tailwind CSS v4, @tailwindcss/vite, React 18, TypeScript (strict), Vite 6

**Spec:** `docs/superpowers/specs/2026-03-23-ui-redesign-design.md`

---

## File Structure

### New Files

```
web/src/index.css                        # Tailwind import, @theme, global styles
web/src/components/NavBar.tsx            # Shared top navigation bar
web/src/components/SlideOver.tsx         # Reusable slide-over panel
web/src/components/StatusBadge.tsx       # Lifecycle status badge
web/src/components/StatCard.tsx          # Stats bar card
web/src/components/CommitItemRow.tsx     # Styled item row for list view
```

### Modified Files

```
web/package.json                         # Add tailwindcss, @tailwindcss/vite
web/vite.config.ts                       # Add tailwindcss plugin
web/src/main.tsx                         # Import index.css
web/src/WeeklyCommitsApp.tsx             # Wrap routes in NavBar layout
web/src/components/WeekSelector.tsx      # Tailwind classes
web/src/components/EisenhowerMatrix.tsx  # Tailwind dark theme
web/src/components/CommitItemForm.tsx    # Tailwind + dark inputs
web/src/components/RcdoCascadeSelector.tsx # Tailwind selects
web/src/components/CommitEntryView.tsx   # List-first layout + stats bar + slide-over
web/src/components/LockedView.tsx        # Alert banner + styled items
web/src/components/ReconciliationView.tsx # Styled summary + submit
web/src/components/ReconciliationItem.tsx # Color-coded cards
web/src/components/ManagerDashboard.tsx  # Dashboard grid + table
web/src/components/TeamSummaryCards.tsx  # Stat cards grid
web/src/components/RcdoAlignmentChart.tsx # Gradient progress bars
```

---

## Task 1: Tailwind CSS v4 Setup

**Files:**
- Modify: `web/package.json`
- Modify: `web/vite.config.ts`
- Create: `web/src/index.css`
- Modify: `web/src/main.tsx`

- [ ] **Step 1: Install Tailwind CSS v4 and the Vite plugin**

Run: `cd web && npm install -D tailwindcss @tailwindcss/vite`

- [ ] **Step 2: Add Tailwind plugin to vite.config.ts**

Add `import tailwindcss from '@tailwindcss/vite'` at the top and `tailwindcss()` to the plugins array (before the react plugin):

```typescript
import tailwindcss from '@tailwindcss/vite';

// In plugins array, add as first plugin:
plugins: [
  tailwindcss(),
  react(),
  // ... federation plugin
],
```

- [ ] **Step 3: Create index.css with Tailwind import and custom theme**

```css
/* web/src/index.css */
@import "tailwindcss";

@theme {
  /* Base palette */
  --color-surface: rgba(255, 255, 255, 0.04);
  --color-surface-hover: rgba(255, 255, 255, 0.08);
  --color-border: rgba(255, 255, 255, 0.08);
  --color-border-strong: rgba(255, 255, 255, 0.15);

  /* Quadrant accents */
  --color-q-dofirst: #ef4444;
  --color-q-schedule: #818cf8;
  --color-q-delegate: #f59e0b;
  --color-q-eliminate: #9ca3af;

  /* Quadrant tints */
  --color-q-dofirst-tint: rgba(127, 29, 29, 0.3);
  --color-q-schedule-tint: rgba(30, 27, 75, 0.3);
  --color-q-delegate-tint: rgba(120, 53, 15, 0.3);
  --color-q-eliminate-tint: rgba(55, 65, 81, 0.3);

  /* Status */
  --color-status-completed: #22c55e;
  --color-status-partial: #f59e0b;
  --color-status-notdone: #ef4444;
  --color-status-pending: #64748b;

  /* Brand */
  --color-brand-primary: #7c3aed;
  --color-brand-primary-light: #a78bfa;
}

/* Global styles */
body {
  background: linear-gradient(135deg, #0f0f1a 0%, #1a1a2e 50%, #16213e 100%);
  min-height: 100vh;
  color: #f1f5f9;
  font-family: system-ui, -apple-system, sans-serif;
}

/* Dark scrollbar */
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

/* Dark form elements */
input, textarea, select {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 0.5rem;
  color: #f1f5f9;
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  outline: none;
  transition: border-color 0.15s;
}
input:focus, textarea:focus, select:focus {
  border-color: #7c3aed;
}
select option {
  background: #1e293b;
  color: #f1f5f9;
}
```

- [ ] **Step 4: Import index.css in main.tsx**

Add `import './index.css';` as the first import in `web/src/main.tsx`.

- [ ] **Step 5: Verify build**

Run: `cd web && npm run build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Verify dev server shows dark background**

Run: `cd web && npm run dev` — open http://localhost:3001/weekly-commits
Expected: Dark gradient background visible, existing unstyled components still render

- [ ] **Step 7: Run tests**

Run: `cd web && npm test`
Expected: All 13 tests PASS

- [ ] **Step 8: Commit**

```bash
git add web/
git commit -m "feat: install Tailwind CSS v4 with custom dark theme"
```

---

## Task 2: Shared UI Components (NavBar, StatusBadge, StatCard, SlideOver)

**Files:**
- Create: `web/src/components/NavBar.tsx`
- Create: `web/src/components/StatusBadge.tsx`
- Create: `web/src/components/StatCard.tsx`
- Create: `web/src/components/SlideOver.tsx`

- [ ] **Step 1: Create StatusBadge component**

```tsx
// web/src/components/StatusBadge.tsx
import type { WeeklyCommitStatus } from '../api/types';

const STATUS_STYLES: Record<WeeklyCommitStatus, string> = {
  DRAFT: 'bg-gradient-to-r from-green-500 to-green-600',
  LOCKED: 'bg-gradient-to-r from-amber-500 to-amber-600',
  RECONCILING: 'bg-gradient-to-r from-purple-400 to-purple-600',
  RECONCILED: 'bg-slate-500',
};

interface Props {
  status: WeeklyCommitStatus;
}

export function StatusBadge({ status }: Props) {
  return (
    <span className={`${STATUS_STYLES[status]} text-white px-3 py-1 rounded-full text-xs font-semibold tracking-wide`}>
      {status}
    </span>
  );
}
```

- [ ] **Step 2: Create StatCard component**

```tsx
// web/src/components/StatCard.tsx
interface Props {
  value: number;
  label: string;
  sublabel: string;
  accentColor: string;   // e.g. 'text-purple-400'
  bgColor: string;       // e.g. 'from-purple-500/20 to-purple-500/10'
  borderColor: string;   // e.g. 'border-purple-500/30'
}

export function StatCard({ value, label, sublabel, accentColor, bgColor, borderColor }: Props) {
  return (
    <div className="flex items-center gap-3">
      <div className={`w-9 h-9 bg-gradient-to-br ${bgColor} border ${borderColor} rounded-lg flex items-center justify-center text-lg font-bold ${accentColor}`}>
        {value}
      </div>
      <div>
        <div className="text-sm text-slate-200 font-medium">{label}</div>
        <div className="text-xs text-slate-500">{sublabel}</div>
      </div>
    </div>
  );
}
```

- [ ] **Step 3: Create NavBar component**

```tsx
// web/src/components/NavBar.tsx
import type { ReactNode } from 'react';

interface Props {
  breadcrumb?: string;
  children?: ReactNode;  // right-side content (week selector, status badge, toggle)
}

export function NavBar({ breadcrumb, children }: Props) {
  return (
    <div className="flex justify-between items-center px-6 py-3 border-b border-white/[0.08]">
      <div className="flex items-center gap-2.5">
        <div className="w-7 h-7 bg-gradient-to-br from-purple-600 to-purple-700 rounded-md flex items-center justify-center text-sm font-extrabold text-white">
          Q
        </div>
        <span className="text-base font-bold bg-gradient-to-r from-purple-300 to-indigo-400 bg-clip-text text-transparent">
          Quartermaster
        </span>
        {breadcrumb && (
          <span className="text-sm text-slate-500 ml-1">/ {breadcrumb}</span>
        )}
      </div>
      <div className="flex items-center gap-4">
        {children}
      </div>
    </div>
  );
}
```

- [ ] **Step 4: Create SlideOver component**

```tsx
// web/src/components/SlideOver.tsx
import { useEffect, type ReactNode } from 'react';

interface Props {
  open: boolean;
  onClose: () => void;
  title: string;
  children: ReactNode;
}

export function SlideOver({ open, onClose, title, children }: Props) {
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    if (open) document.addEventListener('keydown', handleEsc);
    return () => document.removeEventListener('keydown', handleEsc);
  }, [open, onClose]);

  if (!open) return null;

  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/70 z-40"
        onClick={onClose}
      />
      {/* Panel */}
      <div className="fixed right-0 top-0 bottom-0 w-1/2 min-w-[400px] bg-gradient-to-b from-slate-800 to-slate-900 border-l border-white/[0.15] z-50 shadow-[-8px_0_24px_rgba(0,0,0,0.5)] overflow-y-auto">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-lg font-semibold text-slate-200">{title}</h2>
            <button
              onClick={onClose}
              className="text-slate-500 hover:text-slate-300 text-xl"
            >
              ✕
            </button>
          </div>
          {children}
        </div>
      </div>
    </>
  );
}
```

- [ ] **Step 5: Verify build**

Run: `cd web && npm run build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add web/src/components/
git commit -m "feat: add shared UI components — NavBar, StatusBadge, StatCard, SlideOver"
```

---

## Task 3: Restyle WeekSelector + RcdoCascadeSelector

**Files:**
- Modify: `web/src/components/WeekSelector.tsx`
- Modify: `web/src/components/RcdoCascadeSelector.tsx`

- [ ] **Step 1: Restyle WeekSelector**

Replace the entire component body with Tailwind classes. Keep the same props interface and aria-labels.

```tsx
// web/src/components/WeekSelector.tsx
interface WeekSelectorProps {
  weekStartDate: string;
  onPrev: () => void;
  onNext: () => void;
  onCurrent: () => void;
}

function formatWeekRange(startDate: string): string {
  const start = new Date(startDate + 'T00:00:00');
  const end = new Date(start);
  end.setDate(start.getDate() + 6);
  const fmt = (d: Date) => d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  return `${fmt(start)} – ${fmt(end)}`;
}

export function WeekSelector({ weekStartDate, onPrev, onNext, onCurrent }: WeekSelectorProps) {
  return (
    <div className="flex items-center gap-2 bg-white/[0.06] border border-white/10 rounded-lg px-3 py-1.5">
      <button
        type="button"
        onClick={onPrev}
        aria-label="Previous week"
        className="text-slate-500 hover:text-slate-300 transition-colors"
      >
        ←
      </button>
      <span className="text-sm text-slate-200 font-medium min-w-[120px] text-center">
        {formatWeekRange(weekStartDate)}
      </span>
      <button
        type="button"
        onClick={onNext}
        aria-label="Next week"
        className="text-slate-500 hover:text-slate-300 transition-colors"
      >
        →
      </button>
      <button
        type="button"
        onClick={onCurrent}
        className="text-xs text-slate-400 hover:text-slate-200 ml-1 transition-colors"
      >
        Current
      </button>
    </div>
  );
}
```

- [ ] **Step 2: Restyle RcdoCascadeSelector**

Replace inline styles with Tailwind. Keep aria-labels and all behavior unchanged.

```tsx
// web/src/components/RcdoCascadeSelector.tsx
// Keep all imports and logic the same, replace the return JSX:

return (
  <div className="flex gap-2">
    <select
      aria-label="Rally Cry"
      value={selectedRcId}
      onChange={(e) => handleRcChange(e.target.value)}
      className="flex-1"
    >
      <option value="">Select Rally Cry</option>
      {rallyCries.map((rc) => (
        <option key={rc.id} value={rc.id}>{rc.title}</option>
      ))}
    </select>

    <select
      aria-label="Defining Objective"
      value={selectedObjId}
      onChange={(e) => handleObjChange(e.target.value)}
      disabled={!selectedRcId}
      className="flex-1 disabled:opacity-40"
    >
      <option value="">Select Objective</option>
      {currentObjectives.map((obj) => (
        <option key={obj.id} value={obj.id}>{obj.title}</option>
      ))}
    </select>

    <select
      aria-label="Outcome"
      value={selectedOutcomeId}
      onChange={(e) => handleOutcomeChange(e.target.value)}
      disabled={!selectedObjId}
      className="flex-1 disabled:opacity-40"
    >
      <option value="">Select Outcome</option>
      {currentOutcomes.map((out) => (
        <option key={out.id} value={out.id}>{out.title}</option>
      ))}
    </select>
  </div>
);
```

- [ ] **Step 3: Run tests**

Run: `cd web && npm test`
Expected: All tests PASS (aria-labels unchanged)

- [ ] **Step 4: Commit**

```bash
git add web/src/components/WeekSelector.tsx web/src/components/RcdoCascadeSelector.tsx
git commit -m "feat: restyle WeekSelector and RcdoCascadeSelector with Tailwind"
```

---

## Task 4: Create CommitItemRow + Restyle EisenhowerMatrix

**Files:**
- Create: `web/src/components/CommitItemRow.tsx`
- Modify: `web/src/components/EisenhowerMatrix.tsx`

- [ ] **Step 1: Create CommitItemRow component**

This is the styled item row for the list view — shows quadrant dot, title, description, quadrant badge, RCDO trail, and action buttons.

```tsx
// web/src/components/CommitItemRow.tsx
import type { CommitItem } from '../api/types';

const QUADRANT_CONFIG = {
  'HIGH-HIGH': { label: 'DO FIRST', color: 'border-l-red-500', dot: 'bg-red-500 shadow-[0_0_8px_rgba(239,68,68,0.5)]', badge: 'bg-red-900/50 border-red-500/40 text-red-300' },
  'LOW-HIGH':  { label: 'SCHEDULE', color: 'border-l-indigo-400', dot: 'bg-indigo-400 shadow-[0_0_8px_rgba(129,140,248,0.5)]', badge: 'bg-indigo-950/50 border-indigo-400/40 text-indigo-300' },
  'HIGH-LOW':  { label: 'DELEGATE', color: 'border-l-amber-500', dot: 'bg-amber-500 shadow-[0_0_8px_rgba(245,158,11,0.5)]', badge: 'bg-amber-900/50 border-amber-500/40 text-amber-200' },
  'LOW-LOW':   { label: 'ELIMINATE', color: 'border-l-slate-400', dot: 'bg-slate-400', badge: 'bg-slate-700/50 border-slate-500/40 text-slate-400' },
} as const;

interface Props {
  item: CommitItem;
  onEdit?: (item: CommitItem) => void;
  onDelete?: (itemId: string) => void;
  readOnly?: boolean;
  dimmed?: boolean;
}

export function CommitItemRow({ item, onEdit, onDelete, readOnly, dimmed }: Props) {
  const key = `${item.urgency}-${item.importance}` as keyof typeof QUADRANT_CONFIG;
  const q = QUADRANT_CONFIG[key];

  return (
    <div className={`flex items-center gap-3 bg-gradient-to-r from-white/[0.06] to-white/[0.03] border border-white/[0.08] rounded-[10px] px-4 py-3.5 mb-1.5 border-l-[3px] ${q.color} group transition-colors hover:bg-white/[0.08] ${dimmed ? 'opacity-[0.85]' : ''}`}>
      <div className={`w-2 h-2 rounded-full shrink-0 ${q.dot}`} />
      <div className="flex-1 min-w-0">
        <div className="text-sm text-slate-100 font-medium truncate">
          {item.carriedFromId && <span className="text-amber-400 text-xs mr-1">↻</span>}
          {item.title}
        </div>
        {item.description && (
          <div className="text-xs text-slate-500 truncate mt-0.5">{item.description}</div>
        )}
      </div>
      <span className={`text-[10px] font-semibold tracking-wide px-2 py-0.5 border rounded ${q.badge} shrink-0`}>
        {q.label}
      </span>
      {!readOnly && (
        <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity shrink-0">
          {onEdit && (
            <button onClick={() => onEdit(item)} className="w-7 h-7 flex items-center justify-center bg-white/[0.06] rounded-md text-xs text-slate-500 hover:text-slate-300">✎</button>
          )}
          {onDelete && (
            <button onClick={() => onDelete(item.id)} className="w-7 h-7 flex items-center justify-center bg-white/[0.06] rounded-md text-xs text-slate-500 hover:text-slate-300">×</button>
          )}
        </div>
      )}
    </div>
  );
}
```

- [ ] **Step 2: Restyle EisenhowerMatrix with dark theme**

Keep the same props, role attributes, and aria-labels. Replace inline styles with Tailwind.

```tsx
// web/src/components/EisenhowerMatrix.tsx
import type { CommitItem, Priority } from '../api/types';

interface QuadrantDef {
  urgency: Priority;
  importance: Priority;
  label: string;
  bg: string;
  border: string;
  accent: string;
}

export const QUADRANTS: QuadrantDef[] = [
  { urgency: 'HIGH', importance: 'HIGH', label: 'Do First', bg: 'bg-red-900/20', border: 'border-red-500/20', accent: 'text-red-400' },
  { urgency: 'LOW', importance: 'HIGH', label: 'Schedule', bg: 'bg-indigo-950/30', border: 'border-indigo-400/20', accent: 'text-indigo-400' },
  { urgency: 'HIGH', importance: 'LOW', label: 'Delegate', bg: 'bg-amber-900/20', border: 'border-amber-500/20', accent: 'text-amber-400' },
  { urgency: 'LOW', importance: 'LOW', label: 'Eliminate', bg: 'bg-slate-700/20', border: 'border-slate-500/20', accent: 'text-slate-400' },
];

interface EisenhowerMatrixProps {
  items: CommitItem[];
  onEdit?: (item: CommitItem) => void;
  onDelete?: (itemId: string) => void;
  readOnly?: boolean;
}

export function EisenhowerMatrix({ items, onEdit, onDelete, readOnly }: EisenhowerMatrixProps) {
  return (
    <div role="grid" className="grid grid-cols-2 gap-2">
      {QUADRANTS.map((q) => {
        const quadrantItems = items.filter(
          (item) => item.urgency === q.urgency && item.importance === q.importance,
        );
        return (
          <div
            key={q.label}
            role="gridcell"
            aria-label={q.label}
            className={`${q.bg} border ${q.border} rounded-lg p-3`}
          >
            <div className={`text-[9px] uppercase tracking-widest ${q.accent} mb-2 font-semibold`}>{q.label}</div>
            {quadrantItems.length === 0 && (
              <div className="text-xs text-slate-600">No items</div>
            )}
            {quadrantItems.map((item) => (
              <div key={item.id} className="text-xs text-slate-200 bg-white/[0.05] px-2 py-1.5 rounded mb-1 flex items-center justify-between group">
                <span>
                  {item.carriedFromId && <span className="text-amber-400 mr-1">↻ carried</span>}
                  {item.title}
                </span>
                {!readOnly && (
                  <span className="opacity-0 group-hover:opacity-100 flex gap-1">
                    {onEdit && <button onClick={() => onEdit(item)} className="text-slate-500 hover:text-slate-300">✎</button>}
                    {onDelete && <button onClick={() => onDelete(item.id)} className="text-slate-500 hover:text-slate-300">×</button>}
                  </span>
                )}
              </div>
            ))}
          </div>
        );
      })}
    </div>
  );
}
```

- [ ] **Step 3: Run tests**

Run: `cd web && npm test`
Expected: All tests PASS (role and aria-label attributes preserved)

- [ ] **Step 4: Commit**

```bash
git add web/src/components/
git commit -m "feat: add CommitItemRow and restyle EisenhowerMatrix with dark theme"
```

---

## Task 5: Restyle CommitItemForm

**Files:**
- Modify: `web/src/components/CommitItemForm.tsx`

- [ ] **Step 1: Restyle CommitItemForm with Tailwind**

Keep all state, handlers, aria-labels, and validation logic. Replace the JSX with styled versions.

```tsx
// Replace the return JSX in CommitItemForm.tsx:

return (
  <form onSubmit={handleSubmit} className="space-y-4">
    <input
      type="text"
      aria-label="Commit title"
      value={title}
      onChange={(e) => setTitle(e.target.value)}
      placeholder="What will you deliver this week?"
      required
      className="w-full text-base"
    />

    <textarea
      aria-label="Commit description"
      value={description}
      onChange={(e) => setDescription(e.target.value)}
      placeholder="Description (optional)"
      rows={2}
      className="w-full text-sm"
    />

    <RcdoCascadeSelector
      onSelect={setOutcomeId}
      initialOutcomeId={initialValues?.outcomeId}
    />

    <div className="flex gap-4">
      <div className="flex gap-1.5">
        <span className="text-xs text-slate-400 self-center mr-1">Urgency:</span>
        <button
          type="button"
          aria-pressed={urgency === 'HIGH'}
          onClick={() => setUrgency(urgency === 'HIGH' ? 'LOW' : 'HIGH')}
          className={`px-3 py-1 rounded text-xs font-semibold border transition-colors ${urgency === 'HIGH' ? 'bg-red-500/30 border-red-500/50 text-red-400' : 'bg-white/[0.06] border-white/10 text-slate-500'}`}
        >
          Urgent
        </button>
      </div>
      <div className="flex gap-1.5">
        <span className="text-xs text-slate-400 self-center mr-1">Importance:</span>
        <button
          type="button"
          aria-pressed={importance === 'HIGH'}
          onClick={() => setImportance(importance === 'HIGH' ? 'LOW' : 'HIGH')}
          className={`px-3 py-1 rounded text-xs font-semibold border transition-colors ${importance === 'HIGH' ? 'bg-indigo-500/30 border-indigo-500/50 text-indigo-400' : 'bg-white/[0.06] border-white/10 text-slate-500'}`}
        >
          Important
        </button>
      </div>
    </div>

    <div className="flex gap-3 pt-2">
      <button
        type="submit"
        disabled={isSubmitDisabled}
        className="flex-1 bg-gradient-to-r from-purple-600 to-purple-700 text-white py-2 rounded-lg text-sm font-semibold disabled:opacity-40 hover:shadow-[0_0_20px_rgba(124,58,237,0.3)] transition-shadow"
      >
        Add Commit
      </button>
      {onCancel && (
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 border border-white/20 rounded-lg text-sm text-slate-400 hover:text-slate-200 transition-colors"
        >
          Cancel
        </button>
      )}
    </div>
  </form>
);
```

- [ ] **Step 2: Run tests**

Run: `cd web && npm test`
Expected: All tests PASS

- [ ] **Step 3: Commit**

```bash
git add web/src/components/CommitItemForm.tsx
git commit -m "feat: restyle CommitItemForm with dark theme and toggle buttons"
```

---

## Task 6: Restyle CommitEntryView (List-First + Stats + SlideOver)

**Files:**
- Modify: `web/src/components/CommitEntryView.tsx`
- Modify: `web/src/WeeklyCommitsApp.tsx`
- Modify: `web/src/main.tsx`

This is the biggest task — transforms the DRAFT view from matrix-centered to list-first layout.

- [ ] **Step 1: Update WeeklyCommitsApp to include NavBar**

```tsx
// web/src/WeeklyCommitsApp.tsx
import { Routes, Route } from 'react-router-dom';
import { CommitEntryView } from './components/CommitEntryView';
import { ReconciliationView } from './components/ReconciliationView';
import { ManagerDashboard } from './components/ManagerDashboard';

export default function WeeklyCommitsApp() {
  return (
    <div className="min-h-screen">
      <Routes>
        <Route path="/" element={<CommitEntryView />} />
        <Route path="/reconcile" element={<ReconciliationView />} />
        <Route path="/manager" element={<ManagerDashboard />} />
      </Routes>
    </div>
  );
}
```

- [ ] **Step 2: Rewrite CommitEntryView with list-first layout, stats bar, slide-over, and view toggle**

Full rewrite of `web/src/components/CommitEntryView.tsx`. The component now:
- Shows NavBar with WeekSelector, StatusBadge, and List/Matrix toggle
- Stats bar with 3 StatCards (Commits, Do First, Carried)
- Item list using CommitItemRow components
- SlideOver containing CommitItemForm
- Matrix view toggled via state
- Keeps all existing behavior (fetch, add, delete, status routing)

```tsx
// web/src/components/CommitEntryView.tsx
import { useEffect, useState } from 'react';
import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { NavBar } from './NavBar';
import { WeekSelector } from './WeekSelector';
import { StatusBadge } from './StatusBadge';
import { StatCard } from './StatCard';
import { SlideOver } from './SlideOver';
import { CommitItemForm } from './CommitItemForm';
import { CommitItemRow } from './CommitItemRow';
import { EisenhowerMatrix } from './EisenhowerMatrix';
import { LockedView } from './LockedView';
import { ReconciliationView } from './ReconciliationView';
import type { CreateCommitItemRequest } from '../api/types';

function navigateWeek(currentDate: string, offsetDays: number): string {
  const date = new Date(currentDate + 'T00:00:00');
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().slice(0, 10);
}

export function CommitEntryView() {
  const { currentWeek, loading, error, fetchCurrentWeek, fetchWeek, addItem, deleteItem } =
    useWeeklyCommitStore();
  const [showForm, setShowForm] = useState(false);
  const [viewMode, setViewMode] = useState<'list' | 'matrix'>('list');

  useEffect(() => { fetchCurrentWeek(); }, [fetchCurrentWeek]);

  if (loading) return <div className="flex items-center justify-center h-screen text-slate-400">Loading...</div>;
  if (error) return <div role="alert" className="flex items-center justify-center h-screen text-red-400">Error: {error}</div>;
  if (!currentWeek) return <div className="flex items-center justify-center h-screen text-slate-400">No data available</div>;

  if (currentWeek.status === 'LOCKED') return <LockedView />;
  if (currentWeek.status === 'RECONCILING' || currentWeek.status === 'RECONCILED') return <ReconciliationView />;

  const handleAddItem = async (data: CreateCommitItemRequest) => {
    await addItem(currentWeek.weekStartDate, data);
  };

  const handleDeleteItem = async (itemId: string) => {
    await deleteItem(currentWeek.weekStartDate, itemId);
  };

  const doFirstCount = currentWeek.items.filter(i => i.urgency === 'HIGH' && i.importance === 'HIGH').length;
  const carriedCount = currentWeek.items.filter(i => i.carriedFromId !== null).length;

  return (
    <div>
      <NavBar>
        <WeekSelector
          weekStartDate={currentWeek.weekStartDate}
          onPrev={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, -7))}
          onNext={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, 7))}
          onCurrent={() => fetchCurrentWeek()}
        />
        <StatusBadge status={currentWeek.status} />
        <div className="flex bg-white/[0.06] border border-white/10 rounded-md p-0.5">
          <button
            onClick={() => setViewMode('list')}
            className={`px-2.5 py-1 text-xs rounded transition-colors ${viewMode === 'list' ? 'bg-white/10 text-slate-200 font-medium' : 'text-slate-500'}`}
          >
            List
          </button>
          <button
            onClick={() => setViewMode('matrix')}
            className={`px-2.5 py-1 text-xs rounded transition-colors ${viewMode === 'matrix' ? 'bg-white/10 text-slate-200 font-medium' : 'text-slate-500'}`}
          >
            Matrix
          </button>
        </div>
      </NavBar>

      {/* Stats bar */}
      <div className="flex gap-5 items-center px-6 py-4 border-b border-white/[0.06]">
        <StatCard value={currentWeek.items.length} label="Commits" sublabel="this week" accentColor="text-purple-400" bgColor="from-purple-500/20 to-purple-500/10" borderColor="border-purple-500/30" />
        <StatCard value={doFirstCount} label="Do First" sublabel="urgent + important" accentColor="text-red-400" bgColor="from-red-500/20 to-red-500/10" borderColor="border-red-500/30" />
        <StatCard value={carriedCount} label="Carried" sublabel="from last week" accentColor="text-amber-400" bgColor="from-amber-500/20 to-amber-500/10" borderColor="border-amber-500/30" />
        <div className="flex-1" />
        <button
          onClick={() => setShowForm(true)}
          className="bg-gradient-to-r from-purple-600 to-purple-700 text-white px-4 py-2 rounded-lg text-sm font-semibold shadow-[0_0_20px_rgba(124,58,237,0.3)] hover:shadow-[0_0_30px_rgba(124,58,237,0.4)] transition-shadow"
        >
          + New Commit
        </button>
      </div>

      {/* Content */}
      <div className="px-6 py-4">
        {viewMode === 'list' ? (
          currentWeek.items.length === 0 ? (
            <div className="text-center py-16 text-slate-500 text-sm">
              No commits yet. Click "+ New Commit" to plan your week.
            </div>
          ) : (
            currentWeek.items.map(item => (
              <CommitItemRow key={item.id} item={item} onDelete={handleDeleteItem} />
            ))
          )
        ) : (
          <EisenhowerMatrix items={currentWeek.items} onDelete={handleDeleteItem} />
        )}
      </div>

      {/* Slide-over form */}
      <SlideOver open={showForm} onClose={() => setShowForm(false)} title="New Commit">
        <CommitItemForm onSubmit={handleAddItem} onCancel={() => setShowForm(false)} />
      </SlideOver>
    </div>
  );
}
```

- [ ] **Step 3: Run tests**

Run: `cd web && npm test`
Expected: Tests pass. If CommitEntryView tests fail due to DOM restructuring, update selectors to account for the NavBar and new layout.

- [ ] **Step 4: Verify visually**

Run dev server, open http://localhost:3001/weekly-commits
Expected: Dark themed list view with stats bar, items with quadrant badges, "+ New Commit" opens slide-over

- [ ] **Step 5: Commit**

```bash
git add web/src/
git commit -m "feat: restyle CommitEntryView with list-first layout, stats bar, and slide-over form"
```

---

## Task 7: Restyle LockedView + ReconciliationView + ReconciliationItem

**Files:**
- Modify: `web/src/components/LockedView.tsx`
- Modify: `web/src/components/ReconciliationView.tsx`
- Modify: `web/src/components/ReconciliationItem.tsx`

- [ ] **Step 1: Restyle LockedView**

```tsx
// web/src/components/LockedView.tsx
import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { NavBar } from './NavBar';
import { WeekSelector } from './WeekSelector';
import { StatusBadge } from './StatusBadge';
import { CommitItemRow } from './CommitItemRow';

function navigateWeek(currentDate: string, offsetDays: number): string {
  const date = new Date(currentDate + 'T00:00:00');
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().slice(0, 10);
}

export function LockedView() {
  const { currentWeek, fetchCurrentWeek, fetchWeek } = useWeeklyCommitStore();
  if (!currentWeek) return null;

  return (
    <div>
      <NavBar>
        <WeekSelector
          weekStartDate={currentWeek.weekStartDate}
          onPrev={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, -7))}
          onNext={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, 7))}
          onCurrent={() => fetchCurrentWeek()}
        />
        <StatusBadge status="LOCKED" />
      </NavBar>

      <div className="px-6 py-4">
        {/* Alert banner */}
        <div className="flex items-center gap-3 p-4 bg-gradient-to-r from-amber-900/20 to-amber-900/10 border border-amber-500/20 rounded-xl mb-4" role="alert">
          <span className="text-xl">🔒</span>
          <div>
            <div className="text-sm text-amber-200 font-semibold">Week is in progress</div>
            <div className="text-xs text-amber-700">Commits are locked. Reconciliation opens Friday 5:00 PM.</div>
          </div>
        </div>

        {/* Read-only items */}
        {currentWeek.items.map(item => (
          <CommitItemRow key={item.id} item={item} readOnly dimmed />
        ))}
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Restyle ReconciliationItem**

```tsx
// web/src/components/ReconciliationItem.tsx
import { useState } from 'react';
import type { CommitItem, CompletionStatus } from '../api/types';

interface ReconciliationItemProps {
  item: CommitItem;
  onReconcile: (itemId: string, status: CompletionStatus, notes: string) => void;
}

const STATUS_STYLES: Record<CompletionStatus, { bg: string; border: string; borderLeft: string }> = {
  PENDING:   { bg: 'bg-white/[0.04]', border: 'border-white/[0.08]', borderLeft: 'border-l-slate-500' },
  COMPLETED: { bg: 'bg-green-900/20', border: 'border-green-500/20', borderLeft: 'border-l-green-500' },
  PARTIAL:   { bg: 'bg-amber-900/15', border: 'border-amber-500/20', borderLeft: 'border-l-amber-500' },
  NOT_DONE:  { bg: 'bg-red-900/15', border: 'border-red-500/20', borderLeft: 'border-l-red-500' },
};

const SELECT_STYLES: Record<CompletionStatus, string> = {
  PENDING:   'bg-white/[0.08] border-white/[0.15] text-slate-400',
  COMPLETED: 'bg-green-900/30 border-green-500/50 text-green-400',
  PARTIAL:   'bg-amber-900/30 border-amber-500/50 text-amber-400',
  NOT_DONE:  'bg-red-900/30 border-red-500/50 text-red-400',
};

export function ReconciliationItem({ item, onReconcile }: ReconciliationItemProps) {
  const [status, setStatus] = useState<CompletionStatus>(item.completionStatus ?? 'PENDING');
  const [notes, setNotes] = useState(item.completionNotes ?? '');

  const s = STATUS_STYLES[status];

  const handleStatusChange = (newStatus: CompletionStatus) => {
    setStatus(newStatus);
    onReconcile(item.id, newStatus, notes);
  };

  const handleNotesBlur = () => {
    onReconcile(item.id, status, notes);
  };

  return (
    <div className={`${s.bg} border ${s.border} border-l-[3px] ${s.borderLeft} rounded-xl p-4 mb-2 transition-colors`}>
      <div className="flex justify-between items-center mb-2">
        <span className="text-sm text-slate-100 font-medium">{item.title}</span>
        <select
          aria-label={`Status for ${item.title}`}
          value={status}
          onChange={(e) => handleStatusChange(e.target.value as CompletionStatus)}
          className={`${SELECT_STYLES[status]} rounded-md px-2.5 py-1 text-xs font-semibold`}
        >
          <option value="PENDING">Pending</option>
          <option value="COMPLETED">Completed</option>
          <option value="PARTIAL">Partial</option>
          <option value="NOT_DONE">Not Done</option>
        </select>
      </div>
      <input
        aria-label={`Notes for ${item.title}`}
        type="text"
        value={notes}
        onChange={(e) => setNotes(e.target.value)}
        onBlur={handleNotesBlur}
        placeholder="Notes (optional)"
        className="w-full text-xs"
      />
    </div>
  );
}
```

- [ ] **Step 3: Restyle ReconciliationView**

```tsx
// web/src/components/ReconciliationView.tsx
import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { NavBar } from './NavBar';
import { WeekSelector } from './WeekSelector';
import { StatusBadge } from './StatusBadge';
import { ReconciliationItem } from './ReconciliationItem';
import type { CompletionStatus } from '../api/types';

function navigateWeek(currentDate: string, offsetDays: number): string {
  const date = new Date(currentDate + 'T00:00:00');
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().slice(0, 10);
}

export function ReconciliationView() {
  const { currentWeek, fetchCurrentWeek, fetchWeek, reconcileItem, submitReconciliation } =
    useWeeklyCommitStore();
  if (!currentWeek) return null;

  const items = currentWeek.items;
  const completedCount = items.filter((i) => i.completionStatus === 'COMPLETED').length;
  const partialCount = items.filter((i) => i.completionStatus === 'PARTIAL').length;
  const notDoneCount = items.filter((i) => i.completionStatus === 'NOT_DONE').length;

  const handleReconcile = (itemId: string, status: CompletionStatus, notes: string) => {
    reconcileItem(currentWeek.weekStartDate, itemId, {
      completionStatus: status,
      completionNotes: notes || undefined,
    });
  };

  return (
    <div>
      <NavBar>
        <WeekSelector
          weekStartDate={currentWeek.weekStartDate}
          onPrev={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, -7))}
          onNext={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, 7))}
          onCurrent={() => fetchCurrentWeek()}
        />
        <StatusBadge status="RECONCILING" />
      </NavBar>

      <div className="px-6 py-4">
        {items.map((item) => (
          <ReconciliationItem key={item.id} item={item} onReconcile={handleReconcile} />
        ))}

        {/* Summary bar */}
        <div className="flex gap-4 items-center p-4 bg-white/[0.04] border border-white/[0.08] rounded-xl my-4">
          <div className="flex items-center gap-1.5">
            <div className="w-2.5 h-2.5 rounded-full bg-green-500" />
            <span className="text-sm text-slate-200 font-medium">{completedCount}</span>
            <span className="text-xs text-slate-500">Completed</span>
          </div>
          <div className="flex items-center gap-1.5">
            <div className="w-2.5 h-2.5 rounded-full bg-amber-500" />
            <span className="text-sm text-slate-200 font-medium">{partialCount}</span>
            <span className="text-xs text-slate-500">Partial</span>
          </div>
          <div className="flex items-center gap-1.5">
            <div className="w-2.5 h-2.5 rounded-full bg-red-500" />
            <span className="text-sm text-slate-200 font-medium">{notDoneCount}</span>
            <span className="text-xs text-slate-500">Not Done</span>
          </div>
          <div className="flex-1" />
          <span className="text-xs text-slate-500">{items.length} total</span>
        </div>

        {/* Submit */}
        <div className="text-center">
          <button
            type="button"
            onClick={() => submitReconciliation(currentWeek.weekStartDate)}
            className="bg-gradient-to-r from-purple-600 to-purple-700 text-white px-7 py-2.5 rounded-lg text-sm font-semibold shadow-[0_0_24px_rgba(124,58,237,0.3)] hover:shadow-[0_0_30px_rgba(124,58,237,0.4)] transition-shadow"
          >
            Submit Reconciliation
          </button>
          <p className="text-xs text-slate-500 mt-2">Partial and Not Done items will carry forward to next week</p>
        </div>
      </div>
    </div>
  );
}
```

- [ ] **Step 4: Run tests**

Run: `cd web && npm test`
Expected: All tests PASS. LockedView test checks for "Reconciliation opens Friday 5:00 PM" — still present. ReconciliationView tests check for item title, status dropdown aria-label, and "Submit Reconciliation" — all preserved.

- [ ] **Step 5: Commit**

```bash
git add web/src/components/
git commit -m "feat: restyle LockedView, ReconciliationView, and ReconciliationItem"
```

---

## Task 8: Restyle Manager Dashboard

**Files:**
- Modify: `web/src/components/ManagerDashboard.tsx`
- Modify: `web/src/components/TeamSummaryCards.tsx`
- Modify: `web/src/components/RcdoAlignmentChart.tsx`

- [ ] **Step 1: Restyle TeamSummaryCards**

```tsx
// web/src/components/TeamSummaryCards.tsx
import type { TeamSummary } from '../api/types';

interface Props { summary: TeamSummary; }

const CARDS = [
  { key: 'reconciled', bg: 'from-green-500/20 to-green-500/5', border: 'border-green-500/25', color: 'text-green-400', label: 'Reconciled' },
  { key: 'reconciling', bg: 'from-purple-500/15 to-purple-500/5', border: 'border-purple-500/25', color: 'text-purple-400', label: 'Reconciling' },
  { key: 'locked', bg: 'from-amber-500/15 to-amber-500/5', border: 'border-amber-500/25', color: 'text-amber-400', label: 'Locked' },
  { key: 'avg', bg: 'from-blue-500/15 to-blue-500/5', border: 'border-blue-500/25', color: 'text-blue-400', label: 'Avg Completion' },
] as const;

export function TeamSummaryCards({ summary }: Props) {
  const values = {
    reconciled: summary.reconciledCount,
    reconciling: summary.reconcilingCount,
    locked: summary.lockedCount,
    avg: summary.avgCompletionPercent,
  };

  return (
    <div className="grid grid-cols-4 gap-3">
      {CARDS.map(c => (
        <div key={c.key} className={`bg-gradient-to-br ${c.bg} border ${c.border} rounded-xl p-4`}
             aria-label={c.key === 'avg' ? 'Average completion' : `${c.label} count`}>
          <div className={`text-2xl font-bold ${c.color}`}>
            {values[c.key]}{c.key === 'avg' ? '%' : ''}
          </div>
          <div className="text-xs text-slate-400 mt-0.5">{c.label}</div>
        </div>
      ))}
    </div>
  );
}
```

- [ ] **Step 2: Restyle RcdoAlignmentChart**

```tsx
// web/src/components/RcdoAlignmentChart.tsx
import type { RcdoAlignment } from '../api/types';

const BAR_COLORS = [
  'from-purple-600 to-purple-400',
  'from-blue-600 to-blue-400',
  'from-green-600 to-green-400',
  'from-amber-600 to-amber-400',
  'from-pink-600 to-pink-400',
];

interface Props { alignment: RcdoAlignment; }

export function RcdoAlignmentChart({ alignment }: Props) {
  return (
    <div aria-label="RCDO Alignment" className="bg-white/[0.04] border border-white/[0.08] rounded-xl p-4">
      <h3 className="text-sm font-semibold text-slate-200 mb-3">RCDO Alignment</h3>
      {alignment.rallyCries.map((rc, i) => (
        <div key={rc.rallyCryId} className="mb-2.5 last:mb-0">
          <div className="flex justify-between text-xs mb-1">
            <span className="text-slate-200">{rc.rallyCryTitle}</span>
            <span className="text-slate-400">{rc.commitCount} commits ({rc.percentage}%)</span>
          </div>
          <div className="h-1.5 bg-white/[0.08] rounded-full overflow-hidden">
            <div
              className={`h-full bg-gradient-to-r ${BAR_COLORS[i % BAR_COLORS.length]} rounded-full`}
              style={{ width: `${rc.percentage}%` }}
            />
          </div>
        </div>
      ))}
    </div>
  );
}
```

- [ ] **Step 3: Restyle ManagerDashboard**

```tsx
// web/src/components/ManagerDashboard.tsx
// Keep all state, handlers, and data fetching logic.
// Replace the return JSX:

return (
  <div>
    <NavBar breadcrumb="Manager">
      <WeekSelector
        weekStartDate={weekStart}
        onPrev={handlePrev}
        onNext={handleNext}
        onCurrent={handleCurrent}
      />
    </NavBar>

    <div className="px-6 py-4 space-y-4">
      <TeamSummaryCards summary={summary} />

      {/* Team table */}
      <div>
        <h3 className="text-sm font-semibold text-slate-200 mb-3">Team Members</h3>
        <div className="bg-white/[0.04] border border-white/[0.08] rounded-xl overflow-hidden">
          <div className="grid grid-cols-[2fr_1fr_1fr_1fr_1fr] px-4 py-2.5 border-b border-white/[0.08] text-[10px] uppercase tracking-widest text-slate-500">
            <div>Member</div><div>Status</div><div>Done</div><div>Partial</div><div>Not Done</div>
          </div>
          {summary.members.map(m => (
            <div key={m.userId} className="grid grid-cols-[2fr_1fr_1fr_1fr_1fr] px-4 py-3 border-b border-white/[0.06] text-sm items-center last:border-b-0">
              <div className="flex items-center gap-2">
                <div className="w-7 h-7 bg-gradient-to-br from-purple-600 to-indigo-500 rounded-full flex items-center justify-center text-[11px] font-semibold text-white">
                  {m.userId.slice(0, 2).toUpperCase()}
                </div>
                <span className="text-slate-200">{m.userId}</span>
              </div>
              <div>
                <span className={`text-xs px-2 py-0.5 rounded ${
                  m.status === 'RECONCILED' ? 'bg-green-900/20 text-green-400' :
                  m.status === 'RECONCILING' ? 'bg-purple-900/20 text-purple-400' :
                  m.status === 'LOCKED' ? 'bg-amber-900/20 text-amber-400' :
                  'bg-white/[0.08] text-slate-400'
                }`}>
                  {m.status}
                </span>
              </div>
              <div className="text-green-400 font-semibold">{m.completedCount}</div>
              <div className="text-amber-400">{m.partialCount}</div>
              <div className="text-red-400">{m.notDoneCount}</div>
            </div>
          ))}
        </div>
      </div>

      <RcdoAlignmentChart alignment={alignment} />
    </div>
  </div>
);
```

Add the NavBar import at top of file: `import { NavBar } from './NavBar';`

- [ ] **Step 4: Run tests**

Run: `cd web && npm test`
Expected: All tests PASS. ManagerDashboard test checks for "75% Avg Completion", member names, and "Retention" — all text preserved.

- [ ] **Step 5: Verify visually**

Open http://localhost:3001/weekly-commits/manager
Expected: Dark themed dashboard with gradient stat cards, styled team table, gradient progress bars

- [ ] **Step 6: Commit**

```bash
git add web/src/components/
git commit -m "feat: restyle Manager Dashboard with summary cards, team table, and alignment chart"
```

---

## Task 9: Fix Tests + Final Verification

**Files:**
- Modify: test files as needed

- [ ] **Step 1: Run full test suite**

Run: `cd web && npm test`

If any tests fail, fix them by updating selectors or pre-populating stores with the data the new components expect. Common fixes:
- Tests that query `WeekSelector` within `CommitEntryView` or `LockedView` — may need to account for `NavBar` wrapping
- Tests that look for "Add Commit" button text — now says "+ New Commit"
- Tests that expect form fields visible on initial render — now behind slide-over toggle

- [ ] **Step 2: Run build**

Run: `cd web && npm run build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Visual smoke test all views**

Open http://localhost:3001/weekly-commits — verify DRAFT view
Open http://localhost:3001/weekly-commits/manager — verify Manager Dashboard
Verify slide-over opens/closes, Escape key works, form submits

- [ ] **Step 4: Commit any test fixes**

```bash
git add web/
git commit -m "fix: update tests for restyled component structure"
```

- [ ] **Step 5: Push to remote**

```bash
git push
```
