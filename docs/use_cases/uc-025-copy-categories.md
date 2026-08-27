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
- At least one other series exists in the organization (the combo box lists all other series, whether or not they contain categories).

## Main Success Scenario

1. User opens the current series in `SeriesView`.
2. User clicks "Copy categories".
3. System opens `CopyCategoriesDialog` and lists other series of the organization in a combo box.
4. User picks the source series (the "Copy" button is disabled until a source series is selected).
5. User optionally ticks "Increase years of birth" and sets the offset in years (the field is disabled
   while the checkbox is unticked and defaults to 1); the user then clicks "Copy".
6. For each category in the source series the system clones the row, points it at the current series,
   shifts `year_from` and `year_to` by the offset — leaving the open bounds 1900 and 9999 untouched —
   and persists it.
7. For each cloned category the system also clones every `CATEGORY_EVENT` row, preserving event assignments and positions.
8. System notifies the user with "Categories copied" and closes the dialog.
9. `SeriesView` refreshes its tabs so the new categories appear in the Categories grid.

## Alternative Flows

### A1: No source series available

**Trigger:** Step 3 — combo box is empty.
**Flow:**

1. The "Copy" button stays disabled; the user cancels the dialog and creates categories manually.

### A2: Current series already has categories

**Trigger:** Step 1 — the button is hidden.
**Flow:**

1. The user removes existing categories or accepts that no copy is offered.

### A3: Copy without a year shift

**Trigger:** Step 5 — the user leaves "Increase years of birth" unticked.
**Flow:**

1. The offset is 0 and `year_from` / `year_to` are copied unchanged. This is the default.

## Postconditions

### Success Postconditions

- The current series owns a copy of every category and category-event association of the source series,
  with the birth-year ranges shifted by the chosen offset.
- The "Copy categories" button hides itself after the operation.

### Failure Postconditions

- No partial copy is left behind (the whole copy runs in a single transaction).

## Business Rules

### BR-024: Copy is one-shot

The copy operation is offered only when the target series has zero categories, preventing accidental duplication.

### BR-025: Athletes are not copied

Only `CATEGORY` and `CATEGORY_EVENT` rows are copied; athlete enrolments (`CATEGORY_ATHLETE`) are not.

### BR-073: Year shift applies to the birth-year range only

The offset entered in the dialog is added to `year_from` and `year_to` of every copied category.
`abbreviation` and `name` describe the age bracket, not the birth year, and are copied unchanged.
The offset defaults to 0 so that copying without ticking the checkbox behaves exactly as before.

### BR-077: Open bounds are not shifted

`year_from = 1900` and `year_to = 9999` (`CategoryYears.OPEN_FROM` / `OPEN_TO`) are sentinels for the
open lower bound of the oldest and the open upper bound of the youngest category. They stay unchanged
so those categories keep catching every athlete outside the explicitly bracketed years.
