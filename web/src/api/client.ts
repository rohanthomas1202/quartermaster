import type {
  RallyCry,
  DefiningObjective,
  Outcome,
  WeeklyCommitResponse,
  CommitItem,
  CreateCommitItemRequest,
  ReconcileRequest,
  TeamSummary,
  RcdoAlignment,
} from './types';

const BASE_URL =
  typeof window !== 'undefined' ? '/api/wc' : 'http://localhost/api/wc';

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const url = `${BASE_URL}${path}`;
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  });

  if (!response.ok) {
    throw new ApiError(
      `Request failed: ${response.status} ${response.statusText}`,
      response.status,
    );
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export const api = {
  // RCDO
  listRallyCries: () => request<RallyCry[]>('/rally-cries'),

  createRallyCry: (data: Partial<RallyCry>) =>
    request<RallyCry>('/rally-cries', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  listObjectives: (rallyCryId: string) =>
    request<DefiningObjective[]>(`/rally-cries/${rallyCryId}/objectives`),

  createObjective: (rallyCryId: string, data: Partial<DefiningObjective>) =>
    request<DefiningObjective>(`/rally-cries/${rallyCryId}/objectives`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  listOutcomes: (objectiveId: string) =>
    request<Outcome[]>(`/objectives/${objectiveId}/outcomes`),

  createOutcome: (objectiveId: string, data: Partial<Outcome>) =>
    request<Outcome>(`/objectives/${objectiveId}/outcomes`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // Weekly Commits
  getCurrentWeek: () =>
    request<WeeklyCommitResponse>('/weekly-commits/current'),

  getWeek: (weekStart: string) =>
    request<WeeklyCommitResponse>(`/weekly-commits/${weekStart}`),

  addItem: (weekStart: string, data: CreateCommitItemRequest) =>
    request<CommitItem>(`/weekly-commits/${weekStart}/items`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  updateItem: (weekStart: string, itemId: string, data: CreateCommitItemRequest) =>
    request<CommitItem>(`/weekly-commits/${weekStart}/items/${itemId}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  deleteItem: (weekStart: string, itemId: string) =>
    request<void>(`/weekly-commits/${weekStart}/items/${itemId}`, {
      method: 'DELETE',
    }),

  // Reconciliation
  reconcileItem: (weekStart: string, itemId: string, data: ReconcileRequest) =>
    request<void>(`/weekly-commits/${weekStart}/items/${itemId}/reconcile`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  submitReconciliation: (weekStart: string) =>
    request<void>(`/weekly-commits/${weekStart}/submit-reconciliation`, {
      method: 'POST',
    }),

  // Manager
  getTeamSummary: (weekStart: string) =>
    request<TeamSummary>(`/manager/team-summary?weekStart=${weekStart}`),

  getRcdoAlignment: (weekStart: string) =>
    request<RcdoAlignment>(`/manager/rcdo-alignment?weekStart=${weekStart}`),
};
