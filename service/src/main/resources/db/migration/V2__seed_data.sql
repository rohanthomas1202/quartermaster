-- Seed data for local development

-- Org settings
INSERT INTO org_settings (org_id, timezone) VALUES
    ('org-1', 'America/New_York');

-- Rally Cries
INSERT INTO rally_cries (id, title, description, org_id) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'Increase Customer Retention', 'Reduce churn and increase lifetime value across all segments', 'org-1'),
    ('a0000000-0000-0000-0000-000000000002', 'Accelerate Product-Led Growth', 'Drive adoption through self-serve onboarding and viral loops', 'org-1'),
    ('a0000000-0000-0000-0000-000000000003', 'Build World-Class Engineering', 'Invest in platform reliability, developer experience, and technical excellence', 'org-1');

-- Defining Objectives under "Increase Customer Retention"
INSERT INTO defining_objectives (id, rally_cry_id, title, description, owner_id) VALUES
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Reduce Churn Rate', 'Cut monthly churn from 5% to 3%', 'user-1'),
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'Improve Onboarding Completion', 'Get 80% of new users through onboarding in first week', 'user-1');

-- Defining Objectives under "Accelerate Product-Led Growth"
INSERT INTO defining_objectives (id, rally_cry_id, title, description, owner_id) VALUES
    ('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000002', 'Launch Self-Serve Tier', 'Ship free tier with upgrade path', 'user-1'),
    ('b0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000002', 'Build Referral Program', 'In-app referral flow with incentives', 'user-1');

-- Defining Objectives under "Build World-Class Engineering"
INSERT INTO defining_objectives (id, rally_cry_id, title, description, owner_id) VALUES
    ('b0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000003', 'Achieve 99.9% Uptime', 'Platform reliability and observability', 'user-1'),
    ('b0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000003', 'Reduce CI/CD Cycle Time', 'From commit to production in under 15 minutes', 'user-1');

-- Outcomes under "Reduce Churn Rate"
INSERT INTO outcomes (id, defining_objective_id, title, description, measurable_target, current_value, owner_id) VALUES
    ('c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'Launch win-back email campaign', 'Automated re-engagement for churned users', '200 reactivations/month', '0', 'user-1'),
    ('c0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'Ship health score dashboard', 'Predictive churn indicators visible to CS team', 'Dashboard live by Q2', 'Not started', 'user-1');

-- Outcomes under "Improve Onboarding Completion"
INSERT INTO outcomes (id, defining_objective_id, title, description, measurable_target, current_value, owner_id) VALUES
    ('c0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 'Redesign onboarding wizard', 'Step-by-step interactive guide', '80% completion rate', '52%', 'user-1'),
    ('c0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', 'Add contextual tooltips', 'In-app guidance for key features', '30% reduction in support tickets', 'Baseline TBD', 'user-1');

-- Outcomes under "Launch Self-Serve Tier"
INSERT INTO outcomes (id, defining_objective_id, title, description, measurable_target, current_value, owner_id) VALUES
    ('c0000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000003', 'Build pricing page', 'Public pricing with plan comparison', 'Live on marketing site', 'In design', 'user-1'),
    ('c0000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000003', 'Implement usage-based billing', 'Stripe integration with metered billing', 'Processing payments by Q2', 'Not started', 'user-1');

-- Outcomes under "Build Referral Program"
INSERT INTO outcomes (id, defining_objective_id, title, description, measurable_target, current_value, owner_id) VALUES
    ('c0000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000004', 'Ship referral link system', 'Unique referral links with tracking', '500 referrals/month', '0', 'user-1'),
    ('c0000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000004', 'Design referral incentives', 'Credits or extended trial for referrers', '15% of new signups via referral', '0%', 'user-1');

-- Outcomes under "Achieve 99.9% Uptime"
INSERT INTO outcomes (id, defining_objective_id, title, description, measurable_target, current_value, owner_id) VALUES
    ('c0000000-0000-0000-0000-000000000009', 'b0000000-0000-0000-0000-000000000005', 'Set up PagerDuty alerts', 'Automated incident detection and routing', 'MTTR < 15 min', '45 min avg', 'user-1'),
    ('c0000000-0000-0000-0000-000000000010', 'b0000000-0000-0000-0000-000000000005', 'Add circuit breakers to API', 'Resilience patterns for external dependencies', 'Zero cascading failures', '2 incidents/month', 'user-1');

-- Outcomes under "Reduce CI/CD Cycle Time"
INSERT INTO outcomes (id, defining_objective_id, title, description, measurable_target, current_value, owner_id) VALUES
    ('c0000000-0000-0000-0000-000000000011', 'b0000000-0000-0000-0000-000000000006', 'Parallelize test suite', 'Split tests across 4 runners', '< 5 min test time', '18 min', 'user-1'),
    ('c0000000-0000-0000-0000-000000000012', 'b0000000-0000-0000-0000-000000000006', 'Implement canary deployments', 'Gradual rollout with auto-rollback', '< 15 min to production', '45 min', 'user-1');
