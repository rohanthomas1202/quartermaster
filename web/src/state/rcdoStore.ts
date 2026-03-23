import { create } from 'zustand';
import { api } from '../api/client';
import type { RallyCry, DefiningObjective, Outcome } from '../api/types';

interface RcdoState {
  rallyCries: RallyCry[];
  objectives: Record<string, DefiningObjective[]>;
  outcomes: Record<string, Outcome[]>;
  fetchRallyCries: () => Promise<void>;
  fetchObjectives: (rallyCryId: string) => Promise<void>;
  fetchOutcomes: (objectiveId: string) => Promise<void>;
}

export const useRcdoStore = create<RcdoState>((set, get) => ({
  rallyCries: [],
  objectives: {},
  outcomes: {},

  fetchRallyCries: async () => {
    const rallyCries = await api.listRallyCries();
    set({ rallyCries });
  },

  fetchObjectives: async (rallyCryId: string) => {
    const list = await api.listObjectives(rallyCryId);
    set({ objectives: { ...get().objectives, [rallyCryId]: list } });
  },

  fetchOutcomes: async (objectiveId: string) => {
    const list = await api.listOutcomes(objectiveId);
    set({ outcomes: { ...get().outcomes, [objectiveId]: list } });
  },
}));
