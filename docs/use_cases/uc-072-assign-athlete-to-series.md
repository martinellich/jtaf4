# Use Case: Assign athlete to series

## Overview

**Use Case ID:** UC-072
**Use Case Name:** Assign athlete to series
**Primary Actor:** Registered User
**Goal:** Enrol an athlete into the matching category of a series so the athlete can be scored.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.
- The user has opened a series in `SeriesView` and switched to the "Athletes" tab.
- A category for the athlete's gender and birth year exists in the series.

## Main Success Scenario

1. User clicks "Assign Athlete" in the header of the athletes grid's action column.
2. System opens `SearchAthleteDialog`. The grid is empty until a filter is typed; it then lists matching athletes of the active organization that are not yet enrolled in the series.
3. User filters and clicks the per-row "Assign Athlete" button (clicking the row itself opens the athlete edit form instead). Alternatively, the user clicks "Add" in the dialog header, fills in the athlete form and saves — the newly created athlete is assigned right away (A4).
4. System resolves the matching category by `series_id`, the athlete's `gender`, and `year_from <= year_of_birth <= year_to`.
5. System inserts a new `CATEGORY_ATHLETE` row linking the athlete to that category; `dnf` defaults to `false` via the column default.
6. System closes the dialog and refreshes the series athletes grid.

## Alternative Flows

### A1: No matching category

**Trigger:** Step 4 — no category in the series matches the athlete's gender and birth year.
**Flow:**

1. `CategoryAthleteDAO.createCategoryAthlete` returns an empty result and inserts nothing.
2. System shows the notification "No matching category".
3. User must create a suitable category (UC-040) before retrying.

### A2: Athlete already enrolled

**Trigger:** Step 2 — `findByOrganizationIdAndSeriesId` excludes already-enrolled athletes, so the athlete does not appear in the search.
**Flow:**

1. No assignment is required.

### A3: Overlapping categories

**Trigger:** Step 4 — more than one category of the series matches the athlete's gender and birth year.
**Flow:**

1. The category lookup (`fetchOneInto`) throws a `TooManyRowsException`, which is not handled; the assignment fails with an internal error. Category year ranges per gender should not overlap.

### A4: New athlete

**Trigger:** Step 3 — the athlete does not exist in the organization yet.
**Flow:**

1. User clicks "Add" in the dialog and fills in last name, first name, gender, year of birth and optionally the club.
2. System stores the athlete with the active organization and continues with step 4 for the new athlete.
3. Editing an existing athlete from the search grid only refreshes the grid; it does not assign the athlete.

## Postconditions

### Success Postconditions

- A `CATEGORY_ATHLETE` row links the athlete to the matching category.
- The athlete is shown in the series athletes grid and counted in `countAthletesBySeriesId`.

### Failure Postconditions

- No enrolment is created.

## Business Rules

### BR-044: Automatic categorisation

The system auto-selects the category by gender and birth year; the user does not pick it manually.

### BR-045: One category per series

Within a single series an athlete is enrolled in exactly one category.
