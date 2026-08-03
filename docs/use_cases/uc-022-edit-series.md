# Use Case: Edit series

## Overview

**Use Case ID:** UC-022
**Use Case Name:** Edit series
**Primary Actor:** Registered User
**Goal:** Update name and visibility/lock flags of an existing series.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The series belongs to the active organization.

## Main Success Scenario

1. User clicks a series row in the series list.
2. System navigates to `SeriesView/<id>` and loads the record into the form.
3. User updates the name, the "Hidden" checkbox, and/or the "Locked" checkbox.
4. User clicks "Save".
5. System persists the changes and shows a confirmation notification.

## Alternative Flows

### A1: Name cleared

**Trigger:** Step 3 — name field is emptied.
**Flow:**

1. Validator blocks save until a non-empty name is provided.

## Postconditions

### Success Postconditions

- The `SERIES` row reflects the new values.
- If `hidden = true`, the series no longer appears on the public dashboard (UC-090).
- If `locked = true`, the series is treated as final by downstream features (e.g. UC-025 hides "Copy categories").

### Failure Postconditions

- No fields are persisted.

## Business Rules

### BR-019: Hidden series

Hidden series are excluded from the public dashboard listing.

### BR-020: Locked series

Locking a series indicates that the season is over; UI affordances (e.g. category copy) become unavailable.
