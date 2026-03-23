import { Routes, Route } from 'react-router-dom';
import { CommitEntryView } from './components/CommitEntryView';
import { ReconciliationView } from './components/ReconciliationView';
import { ManagerDashboard } from './components/ManagerDashboard';

export default function WeeklyCommitsApp() {
  return (
    <div className="min-h-screen">
      <Routes>
        <Route path="/" element={<CommitEntryView />} />
        <Route path="/reconcile" element={<ReconciliationView />} />
        <Route path="/manager" element={<ManagerDashboard />} />
      </Routes>
    </div>
  );
}
