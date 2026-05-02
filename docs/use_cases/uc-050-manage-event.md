# Use Case: Manage event (IAAF coefficients)

## Overview

**Use Case ID:** UC-050
**Use Case Name:** Manage event (IAAF coefficients)
**Primary Actor:** Registered User
**Goal:** Define the disciplines (track and field events) of the active organization and their IAAF scoring coefficients.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.

## Main Success Scenario

1. User opens the "Events" view from the drawer.
2. System lists the events of the active organization, sorted by gender and abbreviation.
3. User clicks "Add" or selects a row to edit.
4. System opens `EventDialog` with abbreviation, name, gender (M / F), event type (`RUN`, `RUN_LONG`, `JUMP_THROW`), and the coefficients A, B, C.
5. User enters the values and saves.
6. System persists the `EVENT` row stamped with the active `organization_id`.
7. System refreshes the events grid.

## Alternative Flows

### A1: Delete an event

**Trigger:** User triggers the row's delete action.
**Flow:**

1. System opens a confirm dialog.
2. After confirmation, the event is deleted.
3. If results or category assignments still reference the event the database raises a foreign-key error and the deletion is aborted.

### A2: Validation failure

**Trigger:** Step 5 — required text field empty or A/B/C not numeric.
**Flow:**

1. The respective validator/converter blocks the save.

## Postconditions

### Success Postconditions

- The `EVENT` row exists with the supplied IAAF coefficients.
- The event is selectable when assigning events to categories (UC-041).

### Failure Postconditions

- The grid and database are unchanged.

## Business Rules

### BR-036: Event type drives scoring

The `event_type` value determines which IAAF formula `ResultCalculator` applies — RUN/RUN_LONG for time, JUMP_THROW for distance.

### BR-037: Coefficients required

A, B, and C must be provided as numeric values; they are mandatory inputs to the scoring formula.

### BR-038: Organization scope

Events belong to one organization and are not shared across tenants.
