# Use Case: Remove event from category

## Overview

**Use Case ID:** UC-042
**Use Case Name:** Remove event from category
**Primary Actor:** Registered User
**Goal:** Detach an event from a category.
**Status:** Implemented

## Preconditions

- The user is signed in and editing a category in `CategoryDialog`.
- The event is currently assigned to the category.

## Main Success Scenario

1. User clicks "Remove" on an event row inside the category dialog.
2. System opens the "Are you sure?" confirm dialog.
3. User confirms.
4. System deletes the matching `CATEGORY_EVENT` row.
5. System refreshes the events grid in the dialog.

## Alternative Flows

### A1: User cancels

**Trigger:** Step 3 — user clicks "Cancel".
**Flow:**

1. Dialog closes; the assignment remains.

### A2: Existing results reference the event

**Trigger:** Step 4 — `RESULT` rows reference the event/category combination through other tables.
**Flow:**

1. The deletion of `CATEGORY_EVENT` does not cascade to results; results remain in the database but the event is no longer rendered in the result-entry form.

## Postconditions

### Success Postconditions

- The `CATEGORY_EVENT` row is gone.

### Failure Postconditions

- The assignment remains in place.

## Business Rules

### BR-035: Removing an event does not delete results

Detaching an event from a category leaves any previously captured results untouched in the database.
