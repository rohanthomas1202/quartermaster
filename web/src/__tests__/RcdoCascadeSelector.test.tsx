import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useRcdoStore } from '../state/rcdoStore';
import { RcdoCascadeSelector } from '../components/RcdoCascadeSelector';

describe('RcdoCascadeSelector', () => {
  beforeEach(() => {
    useRcdoStore.setState({
      rallyCries: [
        { id: 'rc-1', title: 'Rally Cry 1', description: null, orgId: 'org-1', active: true, createdAt: null, updatedAt: null },
      ],
      objectives: {
        'rc-1': [
          { id: 'obj-1', rallyCryId: 'rc-1', title: 'Objective 1', description: null, ownerId: null, createdAt: null, updatedAt: null },
        ],
      },
      outcomes: {
        'obj-1': [
          { id: 'out-1', definingObjectiveId: 'obj-1', title: 'Outcome 1', description: null, measurableTarget: null, currentValue: null, ownerId: null, createdAt: null, updatedAt: null },
        ],
      },
      fetchRallyCries: vi.fn(),
      fetchObjectives: vi.fn(),
      fetchOutcomes: vi.fn(),
    });
  });

  it('renders three dropdowns', () => {
    render(<RcdoCascadeSelector onSelect={vi.fn()} />);

    expect(screen.getByLabelText('Rally Cry')).toBeInTheDocument();
    expect(screen.getByLabelText('Defining Objective')).toBeInTheDocument();
    expect(screen.getByLabelText('Outcome')).toBeInTheDocument();
  });

  it('cascades selections and calls onSelect', async () => {
    const user = userEvent.setup();
    const onSelect = vi.fn();

    render(<RcdoCascadeSelector onSelect={onSelect} />);

    await user.selectOptions(screen.getByLabelText('Rally Cry'), 'rc-1');
    await user.selectOptions(screen.getByLabelText('Defining Objective'), 'obj-1');
    await user.selectOptions(screen.getByLabelText('Outcome'), 'out-1');

    expect(onSelect).toHaveBeenCalledWith('out-1');
  });
});
