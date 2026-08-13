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
- The `locked` flag is stored and displayed but has no behavioral consequences.

### Failure Postconditions

- No fields are persisted.

## Business Rules

### BR-019: Hidden series

Hidden series are excluded from the public dashboard listing.

### BR-020: Locked series

Locking a series indicates that the season is over. The flag is informational only: it is shown in the series list, but no feature currently consults it (the "Copy categories" visibility, for example, depends solely on the category count).
