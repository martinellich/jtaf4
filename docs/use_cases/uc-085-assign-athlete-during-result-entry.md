# Use Case: Assign athlete during result entry

## Overview

**Use Case ID:** UC-085
**Use Case Name:** Assign athlete during result entry
**Primary Actor:** Registered User
**Goal:** Enrol an athlete who is not yet part of the series — an existing athlete of the organization or a brand-new one — without leaving the result-entry view, and continue entering results for that athlete immediately.
**Status:** Implemented

## Preconditions

- The user is in the result-entry view (UC-080).
- The athlete's result sheet is at hand, but the filter (UC-083) does not find the athlete because the athlete is not enrolled in the series of the competition.

## Main Success Scenario

1. User types the athlete's name into the filter; the grid stays empty.
2. User clicks "Assign Athlete" next to the filter.
3. System resolves the series of the competition and its organization and opens `SearchAthleteDialog` (UC-072) with the filter text pre-filled; the dialog lists matching athletes of that organization that are not yet enrolled in the series.
4. User clicks the per-row "Assign Athlete" button.
5. System resolves the category of the series by the athlete's gender and year of birth and inserts the `CATEGORY_ATHLETE` row (UC-072).
6. System closes the dialog, sets the filter to the athlete's number and refreshes the grid.
7. The athlete is the only match, so the system auto-selects the athlete and focuses the first result field (UC-083, BR-053). The user continues with UC-080.

## Alternative Flows

### A1: New athlete

**Trigger:** Step 3 — the athlete does not exist in the organization.
**Flow:**

1. User clicks "Add" in the dialog and fills in the athlete form (last name, first name, gender, year of birth, optional club).
2. On save, the system stores the athlete in the organization of the series and continues with step 5 — the new athlete is enrolled and selected without any further click.

### A2: No matching category

**Trigger:** Step 5 — the series has no category for the athlete's gender and year of birth.
**Flow:**

1. System inserts nothing and shows the notification "No matching category".
2. The grid stays empty; the user creates the category (UC-040) and retries.

### A3: Filter contains an athlete number

**Trigger:** Step 2 — the filter text is numeric.
**Flow:**

1. The dialog opens with an empty filter, because a number is an athlete id of the series and is not useful as a name search.

## Postconditions

### Success Postconditions

- A `CATEGORY_ATHLETE` row links the athlete to the matching category of the series.
- The athlete is selected in the result-entry grid and the first result field has focus.

### Failure Postconditions

- No enrolment is created; the grid stays as it was.

## Business Rules

### BR-054: Enrolment from result entry uses the series of the competition

The series is derived from the competition of the view (`COMPETITION.SERIES_ID`) and the organization from the series (`SERIES.ORGANIZATION_ID`); neither depends on the active organization of the session, so the feature also works when the view was opened by URL.

### BR-055: Newly created athletes are enrolled immediately

An athlete created via "Add" in `SearchAthleteDialog` is enrolled right away (also in `SeriesView`); only editing an existing athlete leaves the enrolment untouched.

### BR-056: Idempotent enrolment

If the athlete is already enrolled in the series, no second `CATEGORY_ATHLETE` row is created.
