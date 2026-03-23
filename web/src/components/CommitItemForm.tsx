import { useState } from 'react';
import type { CreateCommitItemRequest, Priority } from '../api/types';
import { RcdoCascadeSelector } from './RcdoCascadeSelector';

interface CommitItemFormProps {
  onSubmit: (item: CreateCommitItemRequest) => void;
  onCancel?: () => void;
  initialValues?: Partial<CreateCommitItemRequest>;
}

export function CommitItemForm({ onSubmit, onCancel, initialValues }: CommitItemFormProps) {
  const [title, setTitle] = useState(initialValues?.title ?? '');
  const [description, setDescription] = useState(initialValues?.description ?? '');
  const [outcomeId, setOutcomeId] = useState(initialValues?.outcomeId ?? '');
  const [urgency, setUrgency] = useState<Priority>(initialValues?.urgency ?? 'LOW');
  const [importance, setImportance] = useState<Priority>(initialValues?.importance ?? 'LOW');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !outcomeId) return;

    onSubmit({
      title: title.trim(),
      description: description.trim() || undefined,
      outcomeId,
      urgency,
      importance,
      sortOrder: initialValues?.sortOrder ?? 0,
    });

    setTitle('');
    setDescription('');
    setOutcomeId('');
    setUrgency('LOW');
    setImportance('LOW');
  };

  const isSubmitDisabled = !title.trim() || !outcomeId;

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        aria-label="Commit title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        required
      />

      <textarea
        aria-label="Commit description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />

      <RcdoCascadeSelector
        onSelect={setOutcomeId}
        initialOutcomeId={initialValues?.outcomeId}
      />

      <div>
        <button
          type="button"
          aria-pressed={urgency === 'HIGH'}
          onClick={() => setUrgency(urgency === 'HIGH' ? 'LOW' : 'HIGH')}
        >
          Urgent
        </button>
        <button
          type="button"
          aria-pressed={importance === 'HIGH'}
          onClick={() => setImportance(importance === 'HIGH' ? 'LOW' : 'HIGH')}
        >
          Important
        </button>
      </div>

      <button type="submit" disabled={isSubmitDisabled}>
        Submit
      </button>

      {onCancel && (
        <button type="button" onClick={onCancel}>
          Cancel
        </button>
      )}
    </form>
  );
}
