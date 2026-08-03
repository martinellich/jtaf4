# Use Case: Create competition

## Overview

**Use Case ID:** UC-030
**Use Case Name:** Create competition
**Primary Actor:** Registered User
**Goal:** Add a new competition (a single event date) to a series.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The user has opened a series in `SeriesView`.
- The "Competitions" tab is active.

## Main Success Scenario

1. User clicks the "+" / Add action in the competitions grid.
2. System opens `CompetitionDialog` with `medal_percentage = 0` and `series_id` set to the current series.
3. User enters a name and a date and (optionally) checks "Always first three medals".
4. User saves.
5. System persists the competition and refreshes the grid.

## Alternative Flows

### A1: Required fields missing

**Trigger:** Step 4 — name is blank.
**Flow:**

1. Validator blocks the save until a name is provided.

## Postconditions

### Success Postconditions

- A new `COMPETITION` row exists, linked to the series.
- The competition is listed in the "Competitions" grid and offered with sheet/number download links.

### Failure Postconditions

- No competition is created.

## Business Rules

### BR-026: Default medal percentage

A newly created competition starts with `medal_percentage = 0`; either it must be raised or `always_first_three_medals` must be enabled before diplomas are meaningful.

### BR-027: Series binding

A competition always belongs to exactly one series.
