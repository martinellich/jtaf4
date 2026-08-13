# Use Case: Manage category

## Overview

**Use Case ID:** UC-040
**Use Case Name:** Manage category
**Primary Actor:** Registered User
**Goal:** Create, edit, or delete a category that defines an age/gender bracket inside a series.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The user has opened the series in `SeriesView` and switched to the "Categories" tab.

## Main Success Scenario

1. User clicks "Add" or selects an existing row to edit.
2. System opens `CategoryDialog` (1600 px wide) bound to the current series.
3. User enters or edits abbreviation, name, gender (M / F), `year_from`, and `year_to`.
4. User saves.
5. System persists the `CATEGORY` row and refreshes the grid.

## Alternative Flows

### A1: Delete a category

**Trigger:** User clicks the delete action on a category row.
**Flow:**

1. System opens a confirm dialog.
2. After confirmation, the system attempts to delete the `CATEGORY` row directly (child rows are **not** removed first).
3. If `CATEGORY_EVENT`, `CATEGORY_ATHLETE`, or `RESULT` rows still reference the category, the database raises a foreign-key error, which is shown as a notification; the category remains.
4. Grid refreshes.

### A2: Validation failure

**Trigger:** Step 4 — required field empty or year not numeric.
**Flow:**

1. The relevant validator (`NotEmptyValidator`, `JtafStringToIntegerConverter`) marks the field invalid and the save is blocked; the dialog stays open.
2. Exception: the gender select only carries a required indicator without a validator — an unset gender passes the UI check and fails at the database (`NOT NULL`), surfacing as an error notification.

## Postconditions

### Success Postconditions

- The category exists and is bound to the series.
- Athletes whose gender and birth year fall within the category can be auto-enrolled when assigned to the series (UC-072).

### Failure Postconditions

- The category is not created or modified.

## Business Rules

### BR-030: Gender bound

A category accepts athletes of exactly one gender (M or F).

### BR-031: Year range inclusive

`year_from` and `year_to` are both inclusive birth-year bounds.

### BR-032: Series scope

A category belongs to exactly one series.
