# Use Case: View fastest runners

## Overview

**Use Case ID:** UC-100
**Use Case Name:** View fastest runners
**Primary Actor:** Registered User
**Goal:** See, during a competition, the fastest runners across the 80 m and 60 m sprints — separately for men and women — to select the participants of the sprint finals.
**Status:** Implemented

## Preconditions

- The user is signed in (`USER` or `ADMIN`).
- The competition exists and has sprint results (80 m and/or 60 m).

## Main Success Scenario

1. User clicks "Fastest Runners" on the dashboard for a competition (button only visible to authenticated users).
2. System navigates to `/fastestrunners/<competitionId>` (`FastestRunnersView`).
3. `CompetitionRankingService.getFastestRunners` loads every `RUN` result of **this competition** across all categories whose event is a 60 m or 80 m sprint (BR-070).
4. `FastestRunnersData` levels 60 m times to 80 m (BR-071) and builds one ranking per gender (BR-072).
5. System shows two grids — men and women — with rank, name, year of birth, category, club, event, measured time and levelled time (80 m).
6. User clicks "Refresh" to reload the ranking after further results have been entered.

## Alternative Flows

### A1: No sprint results

**Trigger:** Step 3 — the competition has no 60 m / 80 m results.
**Flow:**

1. Both grids are shown empty.

### A2: Empty or invalid result value

**Trigger:** Step 4 — a result is empty, zero or not numeric.
**Flow:**

1. The result is ignored; the athlete does not appear in the ranking.

## Postconditions

### Success Postconditions

- The ranking is displayed; nothing is persisted.

### Failure Postconditions

- Unknown competition: both grids are empty.

## Business Rules

### BR-070: Sprint event detection

An event counts as a sprint if its type is `RUN` and the leading digits of its abbreviation (fallback: name) are exactly `60` or `80`. `'60'`, `'60mini'`, `'80'`, `'80 m'` are accepted; `'600'`, `'600+20'` or `'100'` are not.

### BR-071: Levelling 60 m to 80 m

The levelled time is `time × 80 / distance`, i.e. a 60 m time is multiplied by 4/3 (9.00 s → 12.00 s). 80 m times are unchanged. Levelled times are rounded to centiseconds.

### BR-072: Separate ranking per gender

Runners are ranked by levelled time ascending, separately for athletes with gender `M` and `F`. Runners with the same levelled time share the rank (1, 1, 3). DNF flags are not considered because the individual sprint time counts.

### BR-073: Authenticated only

The button and the view are only available to signed-in users.
