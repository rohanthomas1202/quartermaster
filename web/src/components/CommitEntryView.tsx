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

  useEffect(() => { fetchCurrentWeek(); }, []); // eslint-disable-line react-hooks/exhaustive-deps

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
