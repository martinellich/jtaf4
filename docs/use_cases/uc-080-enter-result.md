# Use Case: Enter result

## Overview

**Use Case ID:** UC-080
**Use Case Name:** Enter result
**Primary Actor:** Registered User
**Goal:** Capture an athlete's performance for one event of a competition; the system stores it together with the calculated points.
**Status:** Implemented

## Preconditions

- The user is signed in (`USER` or `ADMIN`).
- The user has navigated to `/resultcapturing/<competitionId>` from the dashboard "Enter Results" button.
- At least one athlete is enrolled in a category whose events the competition contests.

## Main Success Scenario

1. User opens the result-entry view.
2. System shows an initially empty athletes grid and a filter (UC-083); athletes appear once a filter value is entered.
3. User selects an athlete (or the system auto-selects the only filtered result).
4. System loads the events of the athlete's category in `position` order and renders a result/points pair per event.
5. System pre-populates existing `RESULT` rows for the athlete in this competition.
6. User types a value in a result field.
7. System invokes `ResultCalculator.calculatePoints` (UC-084) and renders the calculated points read-only.
8. System persists or updates the `RESULT` row immediately (auto-save).
9. Steps 6–8 repeat for each event.

## Alternative Flows

### A1: Athlete not yet selected

**Trigger:** Step 2 — no athlete is selected.
**Flow:**

1. The form area below the grid stays empty.

### A2: Filter resolves to zero athletes

**Trigger:** UC-083 returns no rows.
**Flow:**

1. The form is cleared; no result entry is possible until the filter changes.

### A3: Mark DNF

**Trigger:** User toggles the "DNF" checkbox.
**Flow:**

1. UC-081 is executed.

### A4: Remove all results

**Trigger:** User clicks "Remove results".
**Flow:**

1. UC-082 is executed.

## Postconditions

### Success Postconditions

- A `RESULT` row exists for the (athlete, competition, category, event) tuple with the captured value and computed points.

### Failure Postconditions

- On invalid input the system shows an "Invalid result" notification and no row is saved (see UC-084).

## Business Rules

### BR-047: Auto-save

Each value change persists the result; there is no separate "save" button.

### BR-048: Position equals event order

A new result inherits its `position` from the iteration index over the category's events.

### BR-049: Points read-only

Points are computed by the system and never editable by the user.
