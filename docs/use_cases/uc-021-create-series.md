# Use Case: Create series

## Overview

**Use Case ID:** UC-021
**Use Case Name:** Create series
**Primary Actor:** Registered User
**Goal:** Add a new series to the active organization.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.

## Main Success Scenario

1. User clicks "Add" in the series list.
2. System navigates to `SeriesView` without a parameter.
3. System creates an empty `SERIES` record stamped with the active `organization_id`.
4. User enters a name and saves.
5. System persists the series and shows the "Series saved" notification.
6. The Competitions / Categories / Athletes tabs become available for editing in subsequent steps.

## Alternative Flows

### A1: Empty name

**Trigger:** Step 4 — name is blank.
**Flow:**

1. The `NotEmptyValidator` blocks the save.
2. User fills the field and resubmits.

## Postconditions

### Success Postconditions

- A new `SERIES` row exists with `hidden = false` and `locked = false` by default.
- The user can move on to define competitions, categories, and assign athletes.

### Failure Postconditions

- No series is persisted.

## Business Rules

### BR-017: Series naming

A series name cannot be empty.

### BR-018: Initial state

A newly created series is visible (`hidden = false`) and editable (`locked = false`).
