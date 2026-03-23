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
          <div key={q.label} role="gridcell" aria-label={q.label}
            className={`${q.bg} border ${q.border} rounded-lg p-3`}>
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
