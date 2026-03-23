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
