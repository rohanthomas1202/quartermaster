import { create } from 'zustand';
import { api } from '../api/client';
import type {
  WeeklyCommitResponse,
  CreateCommitItemRequest,
  ReconcileRequest,
} from '../api/types';

interface WeeklyCommitState {
  currentWeek: WeeklyCommitResponse | null;
  loading: boolean;
  error: string | null;
  fetchCurrentWeek: () => Promise<void>;
  fetchWeek: (weekStart: string) => Promise<void>;
  addItem: (weekStart: string, data: CreateCommitItemRequest) => Promise<void>;
  updateItem: (weekStart: string, itemId: string, data: CreateCommitItemRequest) => Promise<void>;
  deleteItem: (weekStart: string, itemId: string) => Promise<void>;
  reconcileItem: (weekStart: string, itemId: string, data: ReconcileRequest) => Promise<void>;
  submitReconciliation: (weekStart: string) => Promise<void>;
}

export const useWeeklyCommitStore = create<WeeklyCommitState>((set, get) => ({
  currentWeek: null,
  loading: false,
  error: null,

  fetchCurrentWeek: async () => {
    set({ loading: true, error: null });
    try {
      const week = await api.getCurrentWeek();
      set({ currentWeek: week, loading: false });
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },

  fetchWeek: async (weekStart: string) => {
    set({ loading: true, error: null });
    try {
      const week = await api.getWeek(weekStart);
      set({ currentWeek: week, loading: false });
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },

  addItem: async (weekStart: string, data: CreateCommitItemRequest) => {
    try {
      await api.addItem(weekStart, data);
      await get().fetchWeek(weekStart);
    } catch (e) {
      set({ error: (e as Error).message });
    }
  },

  updateItem: async (weekStart: string, itemId: string, data: CreateCommitItemRequest) => {
    try {
      await api.updateItem(weekStart, itemId, data);
      await get().fetchWeek(weekStart);
    } catch (e) {
      set({ error: (e as Error).message });
    }
  },

  deleteItem: async (weekStart: string, itemId: string) => {
    try {
      await api.deleteItem(weekStart, itemId);
      await get().fetchWeek(weekStart);
    } catch (e) {
      set({ error: (e as Error).message });
    }
  },

  reconcileItem: async (weekStart: string, itemId: string, data: ReconcileRequest) => {
    try {
      await api.reconcileItem(weekStart, itemId, data);
      await get().fetchWeek(weekStart);
    } catch (e) {
      set({ error: (e as Error).message });
    }
  },

  submitReconciliation: async (weekStart: string) => {
    try {
      await api.submitReconciliation(weekStart);
      await get().fetchWeek(weekStart);
    } catch (e) {
      set({ error: (e as Error).message });
    }
  },
}));
