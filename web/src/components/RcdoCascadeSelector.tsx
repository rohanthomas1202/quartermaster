import { useEffect, useState } from 'react';
import { useRcdoStore } from '../state/rcdoStore';

interface RcdoCascadeSelectorProps {
  onSelect: (outcomeId: string) => void;
  initialOutcomeId?: string;
}

export function RcdoCascadeSelector({ onSelect, initialOutcomeId }: RcdoCascadeSelectorProps) {
  const { rallyCries, objectives, outcomes, fetchRallyCries, fetchObjectives, fetchOutcomes } =
    useRcdoStore();

  const [selectedRcId, setSelectedRcId] = useState('');
  const [selectedObjId, setSelectedObjId] = useState('');
  const [selectedOutcomeId, setSelectedOutcomeId] = useState(initialOutcomeId ?? '');

  useEffect(() => {
    void fetchRallyCries();
  }, [fetchRallyCries]);

  const handleRcChange = (rcId: string) => {
    setSelectedRcId(rcId);
    setSelectedObjId('');
    setSelectedOutcomeId('');
    if (rcId) {
      void fetchObjectives(rcId);
    }
  };

  const handleObjChange = (objId: string) => {
    setSelectedObjId(objId);
    setSelectedOutcomeId('');
    if (objId) {
      void fetchOutcomes(objId);
    }
  };

  const handleOutcomeChange = (outcomeId: string) => {
    setSelectedOutcomeId(outcomeId);
    if (outcomeId) {
      onSelect(outcomeId);
    }
  };

  const currentObjectives = selectedRcId ? objectives[selectedRcId] ?? [] : [];
  const currentOutcomes = selectedObjId ? outcomes[selectedObjId] ?? [] : [];

  return (
    <div className="flex gap-2">
      <select
        className="flex-1"
        aria-label="Rally Cry"
        value={selectedRcId}
        onChange={(e) => handleRcChange(e.target.value)}
      >
        <option value="">Select Rally Cry</option>
        {rallyCries.map((rc) => (
          <option key={rc.id} value={rc.id}>
            {rc.title}
          </option>
        ))}
      </select>

      <select
        className="flex-1 disabled:opacity-40"
        aria-label="Defining Objective"
        value={selectedObjId}
        onChange={(e) => handleObjChange(e.target.value)}
        disabled={!selectedRcId}
      >
        <option value="">Select Defining Objective</option>
        {currentObjectives.map((obj) => (
          <option key={obj.id} value={obj.id}>
            {obj.title}
          </option>
        ))}
      </select>

      <select
        className="flex-1 disabled:opacity-40"
        aria-label="Outcome"
        value={selectedOutcomeId}
        onChange={(e) => handleOutcomeChange(e.target.value)}
        disabled={!selectedObjId}
      >
        <option value="">Select Outcome</option>
        {currentOutcomes.map((out) => (
          <option key={out.id} value={out.id}>
            {out.title}
          </option>
        ))}
      </select>
    </div>
  );
}
