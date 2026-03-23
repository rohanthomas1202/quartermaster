import { useState } from 'react';
import type { CommitItem, CompletionStatus } from '../api/types';

interface ReconciliationItemProps {
  item: CommitItem;
  onReconcile: (itemId: string, status: CompletionStatus, notes: string) => void;
}

const STATUS_BORDER_COLORS: Record<CompletionStatus, string> = {
  PENDING: 'gray',
  COMPLETED: 'green',
  PARTIAL: 'amber',
  NOT_DONE: 'red',
};

export function ReconciliationItem({ item, onReconcile }: ReconciliationItemProps) {
  const [status, setStatus] = useState<CompletionStatus>(item.completionStatus ?? 'PENDING');
  const [notes, setNotes] = useState(item.completionNotes ?? '');

  const borderColor = STATUS_BORDER_COLORS[status];

  const handleStatusChange = (newStatus: CompletionStatus) => {
    setStatus(newStatus);
    onReconcile(item.id, newStatus, notes);
  };

  const handleNotesBlur = () => {
    onReconcile(item.id, status, notes);
  };

  return (
    <div
      style={{
        borderLeft: `4px solid ${borderColor}`,
        padding: '8px 12px',
        marginBottom: '8px',
      }}
    >
      <div style={{ fontWeight: 'bold' }}>{item.title}</div>

      <select
        aria-label={`Status for ${item.title}`}
        value={status}
        onChange={(e) => handleStatusChange(e.target.value as CompletionStatus)}
      >
        <option value="PENDING">Pending</option>
        <option value="COMPLETED">Completed</option>
        <option value="PARTIAL">Partial</option>
        <option value="NOT_DONE">Not Done</option>
      </select>

      <input
        aria-label={`Notes for ${item.title}`}
        type="text"
        value={notes}
        onChange={(e) => setNotes(e.target.value)}
        onBlur={handleNotesBlur}
        placeholder="Completion notes..."
      />
    </div>
  );
}
