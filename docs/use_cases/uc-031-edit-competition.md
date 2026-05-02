# Use Case: Edit competition

## Overview

**Use Case ID:** UC-031
**Use Case Name:** Edit competition
**Primary Actor:** Registered User
**Goal:** Update an existing competition's name, date, lock state, or medal settings.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The competition belongs to a series of the active organization.

## Main Success Scenario

1. User clicks the row of a competition in the competitions grid.
2. System opens `CompetitionDialog` populated with the current values.
3. User edits name, date, and/or medal settings.
4. User saves.
5. System persists the changes and refreshes the grid.

## Alternative Flows

### A1: Validation failure

**Trigger:** Step 4 — name is blank.
**Flow:**

1. Validator blocks save until corrected.

## Postconditions

### Success Postconditions

- The competition is updated.
- Subsequent rankings and diplomas reflect the new medal settings.

### Failure Postconditions

- No fields are changed.

## Business Rules

### BR-028: Medal scheme

`always_first_three_medals` overrides the percentage rule by guaranteeing the top three a medal regardless of `medal_percentage`.
