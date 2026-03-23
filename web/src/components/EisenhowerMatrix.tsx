import type { CommitItem, Priority } from '../api/types';

interface QuadrantDef {
  urgency: Priority;
  importance: Priority;
  label: string;
  color: string;
}

export const QUADRANTS: QuadrantDef[] = [
  { urgency: 'HIGH', importance: 'HIGH', label: 'Do First', color: 'red' },
  { urgency: 'LOW', importance: 'HIGH', label: 'Schedule', color: 'blue' },
  { urgency: 'HIGH', importance: 'LOW', label: 'Delegate', color: 'amber' },
  { urgency: 'LOW', importance: 'LOW', label: 'Eliminate', color: 'gray' },
];

interface EisenhowerMatrixProps {
  items: CommitItem[];
  onEdit?: (item: CommitItem) => void;
  onDelete?: (itemId: string) => void;
  readOnly?: boolean;
}

export function EisenhowerMatrix({ items, onEdit, onDelete, readOnly }: EisenhowerMatrixProps) {
  return (
    <div role="grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
      {QUADRANTS.map((q) => {
        const quadrantItems = items.filter(
          (item) => item.urgency === q.urgency && item.importance === q.importance,
        );

        return (
          <div
            key={q.label}
            role="gridcell"
            aria-label={q.label}
            style={{
              backgroundColor: `var(--color-${q.color}-tint, #f5f5f5)`,
              padding: '12px',
              borderRadius: '8px',
            }}
          >
            <h3>{q.label}</h3>
            <ul>
              {quadrantItems.map((item) => (
                <li key={item.id}>
                  <span>{item.title}</span>
                  {item.carriedFromId && <span> ↻ carried</span>}
                  {!readOnly && (
                    <>
                      {onEdit && (
                        <button type="button" onClick={() => onEdit(item)}>
                          Edit
                        </button>
                      )}
                      {onDelete && (
                        <button type="button" onClick={() => onDelete(item.id)}>
                          Delete
                        </button>
                      )}
                    </>
                  )}
                </li>
              ))}
            </ul>
          </div>
        );
      })}
    </div>
  );
}
