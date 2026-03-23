import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { WeekSelector } from './WeekSelector';
import { ReconciliationItem } from './ReconciliationItem';
import type { CompletionStatus } from '../api/types';

function navigateWeek(currentDate: string, offsetDays: number): string {
  const date = new Date(currentDate);
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().slice(0, 10);
}

export function ReconciliationView() {
  const { currentWeek, fetchCurrentWeek, fetchWeek, reconcileItem, submitReconciliation } =
    useWeeklyCommitStore();

  if (!currentWeek) {
    return null;
  }

  const items = currentWeek.items;

  const completedCount = items.filter((i) => i.completionStatus === 'COMPLETED').length;
  const partialCount = items.filter((i) => i.completionStatus === 'PARTIAL').length;
  const notDoneCount = items.filter((i) => i.completionStatus === 'NOT_DONE').length;
  const totalCount = items.length;

  const handleReconcile = (itemId: string, status: CompletionStatus, notes: string) => {
    reconcileItem(currentWeek.weekStartDate, itemId, {
      completionStatus: status,
      completionNotes: notes || undefined,
    });
  };

  const handleSubmit = () => {
    submitReconciliation(currentWeek.weekStartDate);
  };

  return (
    <div>
      <WeekSelector
        weekStartDate={currentWeek.weekStartDate}
        onPrev={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, -7))}
        onNext={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, 7))}
        onCurrent={() => fetchCurrentWeek()}
      />

      <h2>RECONCILING</h2>

      {items.map((item) => (
        <ReconciliationItem key={item.id} item={item} onReconcile={handleReconcile} />
      ))}

      <div>
        Completed: {completedCount} | Partial: {partialCount} | Not Done: {notDoneCount} | Total: {totalCount}
      </div>

      <button type="button" onClick={handleSubmit}>
        Submit Reconciliation
      </button>

      <p>
        Items marked as Not Done or Partial may be carried forward to next week.
      </p>
    </div>
  );
}
