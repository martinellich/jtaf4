# Use Case: Correct athlete during result entry

## Overview

**Use Case ID:** UC-086
**Use Case Name:** Correct athlete during result entry
**Primary Actor:** Registered User
**Goal:** Fix a misspelled last or first name of the athlete whose results are being entered or checked, without leaving the result-entry view and without losing the selection.
**Status:** Implemented

## Preconditions

- The user is in the result-entry view (UC-080).
- An athlete is selected in the grid — typically after filtering by athlete number or name (UC-083).

## Main Success Scenario

1. User notices that the selected athlete's name is misspelled (e.g. while checking the result sheet).
2. User clicks "Edit Athlete" next to the filter; the button is enabled only while an athlete is selected.
3. System opens `AthleteNameDialog` with the athlete's current last name and first name; no other fields are offered (BR-057).
4. User corrects last name and/or first name and clicks "Save".
5. System validates that both names are non-empty and updates the `ATHLETE` row.
6. System closes the dialog, sets the filter to the athlete's number and refreshes the grid, which now shows the corrected name.
7. The athlete is the only match, so the athlete stays selected and the result form remains open (UC-083, BR-053). The user continues with UC-080.

## Alternative Flows

### A1: Cancel

**Trigger:** Step 4 — the user clicks "Cancel" or closes the dialog.
**Flow:**

1. System discards the changes; the athlete, the grid and the selection stay as they were.

### A2: Validation fails

**Trigger:** Step 5 — last name or first name is empty.
**Flow:**

1. The dialog stays open and marks the invalid field; nothing is stored.

### A3: No athlete selected

**Trigger:** Step 2 — the grid has no selection (empty filter or no match).
**Flow:**

1. The "Edit Athlete" button is disabled; the user filters for the athlete first (UC-083) or enrols the athlete (UC-085).

## Postconditions

### Success Postconditions

- The `ATHLETE` row carries the corrected name; the change is visible everywhere the athlete appears (rankings, sheets, athlete list).
- The athlete is still selected in the result-entry grid and the result form is still shown.

### Failure Postconditions

- The `ATHLETE` row is unchanged.

## Business Rules

### BR-057: Only the name is editable from result entry

The dialog offers last name and first name only. Gender, year of birth and club are not editable here because gender and year determine the category the athlete is enrolled in (UC-072) and the club feeds the club ranking; such changes are made in the athlete list (UC-070) and, if the category changes, by re-enrolling the athlete (UC-073, UC-072).

### BR-058: Editing does not change the enrolment

The `CATEGORY_ATHLETE` row and the existing `RESULT` rows are kept; only the name columns of `ATHLETE` change.
