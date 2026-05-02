# Use Case: Delete competition

## Overview

**Use Case ID:** UC-032
**Use Case Name:** Delete competition
**Primary Actor:** Registered User
**Goal:** Remove a competition from a series.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The competition belongs to a series of the active organization.
- All `RESULT` rows for the competition have been removed (foreign key constraint).

## Main Success Scenario

1. User triggers the delete action on a competition row in the grid.
2. System confirms the deletion via the standard confirm dialog.
3. User confirms.
4. System deletes the `COMPETITION` row.
5. System refreshes the grid.

## Alternative Flows

### A1: Foreign-key violation

**Trigger:** Step 4 — results still reference the competition.
**Flow:**

1. The delete fails and an error notification appears.
2. User must clear results (UC-082) before retrying.

## Postconditions

### Success Postconditions

- The `COMPETITION` row is removed.

### Failure Postconditions

- The competition remains.

## Business Rules

### BR-029: Result preservation

Competitions cannot be deleted while their results exist; this prevents accidental loss of historical scoring data.
