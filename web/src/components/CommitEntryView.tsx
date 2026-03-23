import { useEffect, useState } from 'react';
import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { WeekSelector } from './WeekSelector';
import { EisenhowerMatrix } from './EisenhowerMatrix';
import { CommitItemForm } from './CommitItemForm';
import { LockedView } from './LockedView';
import { ReconciliationView } from './ReconciliationView';
import type { CreateCommitItemRequest } from '../api/types';

function navigateWeek(currentDate: string, offsetDays: number): string {
  const date = new Date(currentDate);
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().slice(0, 10);
}

export function CommitEntryView() {
  const { currentWeek, loading, error, fetchCurrentWeek, fetchWeek, addItem, deleteItem } =
    useWeeklyCommitStore();
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    fetchCurrentWeek();
  }, [fetchCurrentWeek]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div role="alert">Error: {error}</div>;
  }

  if (!currentWeek) {
    return <div>No data available</div>;
  }

  if (currentWeek.status === 'LOCKED') {
    return <LockedView />;
  }

  if (currentWeek.status === 'RECONCILING' || currentWeek.status === 'RECONCILED') {
    return <ReconciliationView />;
  }

  const handleAddItem = async (data: CreateCommitItemRequest) => {
    await addItem(currentWeek.weekStartDate, data);
    setShowForm(false);
  };

  const handleDeleteItem = async (itemId: string) => {
    await deleteItem(currentWeek.weekStartDate, itemId);
  };

  return (
    <div>
      <WeekSelector
        weekStartDate={currentWeek.weekStartDate}
        onPrev={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, -7))}
        onNext={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, 7))}
        onCurrent={() => fetchCurrentWeek()}
      />

      <button type="button" onClick={() => setShowForm(!showForm)}>
        {showForm ? 'Cancel' : 'Add Commit'}
      </button>

      {showForm && (
        <CommitItemForm onSubmit={handleAddItem} onCancel={() => setShowForm(false)} />
      )}

      <EisenhowerMatrix items={currentWeek.items} onDelete={handleDeleteItem} />
    </div>
  );
}
