# Use Case: Edit competition

## Overview

**Use Case ID:** UC-031
**Use Case Name:** Edit competition
**Primary Actor:** Registered User
**Goal:** Update an existing competition's name, date, or medal settings.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The competition belongs to a series of the active organization.

## Main Success Scenario

1. User clicks the row of a competition in the competitions grid.
2. System opens `CompetitionDialog` populated with the current values.
3. User edits name, date, and/or medal settings (medal percentage, "Always first three medals").
4. User saves.
5. System persists the changes and refreshes the grid.

## Alternative Flows

### A1: Validation failure

**Trigger:** Step 4 — name, date, or medal percentage is missing, or the percentage is outside 0–100.
**Flow:**

1. Validation marks the affected fields and blocks the save; database errors are shown as a notification and the dialog stays open.

## Postconditions

### Success Postconditions

- The competition is updated.
- Subsequent rankings and diplomas reflect the new medal settings.

### Failure Postconditions

- No fields are changed.

## Business Rules

### BR-028: Medal scheme

`always_first_three_medals` only takes effect when `medal_percentage > 0`; it then guarantees at least the top three a medal. With `medal_percentage = 0` no medals are awarded at all.
