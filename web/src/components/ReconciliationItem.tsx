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
