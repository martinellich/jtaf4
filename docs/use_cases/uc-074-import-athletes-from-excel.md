# Use Case: Import athletes from Excel

## Overview

**Use Case ID:** UC-074
**Use Case Name:** Import athletes from Excel
**Primary Actor:** Registered User
**Goal:** Enrol a whole list of registrations into a series in one step: reuse the athletes the organization already knows, create the missing ones, and assign all of them to the matching category.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.
- The user has opened a series in `SeriesView` and switched to the "Athletes" tab.
- The registrations are available as an `.xlsx` file with the columns `Name` (family name), `Vorname` (given name), `Geb. Datum` (birth date or birth year) and `Geschlecht` in the first four columns.
- Categories for the participating birth years and genders exist in the series (UC-040 / UC-025), otherwise the athletes are created but not assigned.

## Main Success Scenario

1. User clicks "Import athletes" in the header of the athletes grid's action column.
2. System opens `ImportAthletesDialog`.
3. User uploads the `.xlsx` file.
4. `AthleteImportService` reads the first sheet: it looks for the header row (`Name` / `Vorname`), reads every following row until the first empty one, trims the names, derives the year of birth from the birth date, and maps `m`/`w` to the gender codes `M`/`F`.
5. For every readable row the system determines the status: `NO_CATEGORY` when no category of the series matches gender and birth year, `ALREADY_ASSIGNED` when the athlete is already enrolled in the series, `EXISTING` when the athlete is known to the organization, otherwise `NEW`.
6. System shows the rows with their status in the preview grid and a summary "X new, Y existing, Z without category". The grid starts out in the order of the file and can be sorted by every column — family name, given name, gender, year of birth and status — so the user can for example group all rows without a matching category. Nothing has been written so far.
7. User clicks "Import".
8. In one transaction the system creates the athletes marked `NEW` (without a club) and inserts a `CATEGORY_ATHLETE` row for every athlete that has a matching category and is not yet enrolled.
9. System shows a notification with the number of assigned, newly created and uncategorised athletes and closes the dialog.
10. `SeriesView` refreshes so the enrolled athletes appear in the athletes grid.

## Alternative Flows

### A1: No matching category

**Trigger:** Step 5 — no category of the series matches the gender and birth year of the row.
**Flow:**

1. The row is shown as "no matching category" in the preview.
2. On import the athlete is still created or reused in the organization, but no `CATEGORY_ATHLETE` row is written.
3. The athlete is listed in the "without category" count of the final notification and can be enrolled with UC-072 after the missing category has been created.

### A2: Athlete already enrolled in the series

**Trigger:** Step 5 — the athlete is already linked to a category of the series.
**Flow:**

1. The row is shown as "already assigned".
2. On import nothing is written for that row, so importing the same file twice does not create duplicates.

### A3: Unreadable row

**Trigger:** Step 4 — family name, given name, birth year or gender is missing or cannot be interpreted.
**Flow:**

1. The row is shown as "invalid row" in the preview and skipped on import.
2. The user corrects the file and imports again.

### A4: File without a header row

**Trigger:** Step 4 — no row starts with `Name` and `Vorname`.
**Flow:**

1. No rows are read, the preview stays empty and the "Import" button stays disabled.

## Postconditions

### Success Postconditions

- Every readable athlete of the file exists in the organization.
- Every readable athlete with a matching category is enrolled in that category of the series and is counted by `countAthletesBySeriesId`.

### Failure Postconditions

- The import runs in a single transaction; if it fails no athlete is created and no enrolment is written.

## Business Rules

### BR-074: Duplicate detection

An athlete is considered to be the same person when family name, given name, year of birth and gender match exactly within the active organization. Only when no such athlete exists is a new one created.

### BR-075: Imported athletes have no club

The registration file carries no club information, so imported athletes are created with `club_id = null`. The club can be maintained afterwards with UC-070.

### BR-076: Preview before write

The upload only analyses the file. Nothing is written until the user confirms with "Import", so the user can see beforehand which athletes are new and which rows cannot be assigned.
