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
}
