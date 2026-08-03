# Use Case: Copy categories from another series

## Overview

**Use Case ID:** UC-025
**Use Case Name:** Copy categories from another series
**Primary Actor:** Registered User
**Goal:** Bootstrap the categories of a new series by copying them (with their event assignments) from an existing series of the same organization.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.
- The current series has been saved at least once (`copy categories` button is hidden until a series id exists).
- The current series has no categories yet (the button hides itself once `count > 0`).
- At least one other series in the organization contains categories.

## Main Success Scenario

1. User opens the current series in `SeriesView`.
2. User clicks "Copy categories".
3. System opens `CopyCategoriesDialog` and lists other series of the organization in a combo box.
4. User picks the source series and clicks "Copy".
5. For each category in the source series the system clones the row, points it at the current series, and persists it.
6. For each cloned category the system also clones every `CATEGORY_EVENT` row, preserving event assignments and positions.
7. System notifies the user with "Categories copied" and closes the dialog.
8. `SeriesView` refreshes its tabs so the new categories appear in the Categories grid.

## Alternative Flows

### A1: No source series available

**Trigger:** Step 3 — combo box is empty.
**Flow:**

1. The user cancels the dialog and creates categories manually.

### A2: Current series already has categories

**Trigger:** Step 1 — the button is hidden.
**Flow:**

1. The user removes existing categories or accepts that no copy is offered.

## Postconditions

### Success Postconditions

- The current series owns a copy of every category and category-event association of the source series.
- The "Copy categories" button hides itself after the operation.

### Failure Postconditions

- No partial copy is left behind (the operation is transactional per category).

## Business Rules

### BR-024: Copy is one-shot

The copy operation is offered only when the target series has zero categories, preventing accidental duplication.

### BR-025: Athletes are not copied

Only `CATEGORY` and `CATEGORY_EVENT` rows are copied; athlete enrolments (`CATEGORY_ATHLETE`) are not.
