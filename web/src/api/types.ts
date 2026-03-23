// RCDO types

export interface RallyCry {
  id: string;
  title: string;
  description: string | null;
  orgId: string;
  active: boolean;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface DefiningObjective {
  id: string;
  rallyCryId: string;
  title: string;
  description: string | null;
  ownerId: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface Outcome {
  id: string;
  definingObjectiveId: string;
  title: string;
  description: string | null;
  measurableTarget: string | null;
  currentValue: string | null;
  ownerId: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}

// Enum types

export type WeeklyCommitStatus = 'DRAFT' | 'LOCKED' | 'RECONCILING' | 'RECONCILED';

export type CompletionStatus = 'PENDING' | 'COMPLETED' | 'PARTIAL' | 'NOT_DONE';

export type Priority = 'HIGH' | 'LOW';

// Weekly commit types

export interface CommitItem {
  id: string;
  weeklyCommitId: string;
  title: string;
  description: string | null;
  outcomeId: string;
  urgency: Priority;
  importance: Priority;
  sortOrder: number;
  completionStatus: CompletionStatus | null;
  completionNotes: string | null;
  carriedFromId: string | null;
  version: number;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface WeeklyCommitResponse {
  id: string;
  userId: string;
  orgId: string;
  weekStartDate: string;
  weekEndDate: string;
  status: WeeklyCommitStatus;
  version: number;
  items: CommitItem[];
}

// Request types

export interface CreateCommitItemRequest {
  title: string;
  description?: string;
  outcomeId: string;
  urgency: Priority;
  importance: Priority;
  sortOrder: number;
}

export interface ReconcileRequest {
  completionStatus: CompletionStatus;
  completionNotes?: string;
}

// Manager dashboard types

export interface MemberSummary {
  userId: string;
  status: string;
  completedCount: number;
  partialCount: number;
  notDoneCount: number;
  totalItems: number;
}

export interface TeamSummary {
  totalMembers: number;
  reconciledCount: number;
  reconcilingCount: number;
  lockedCount: number;
  draftCount: number;
  avgCompletionPercent: number;
  members: MemberSummary[];
}

export interface RallyCryCommitCount {
  rallyCryId: string;
  rallyCryTitle: string;
  commitCount: number;
  percentage: number;
}

export interface RcdoAlignment {
  rallyCries: RallyCryCommitCount[];
  totalCommits: number;
}
