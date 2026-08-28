# Use Case: Generate categories sheet

## Overview

**Use Case ID:** UC-099
**Use Case Name:** Generate categories sheet
**Primary Actor:** Registered User
**Goal:** Print a one-page overview of all categories of a series with their birth year range and events, e.g. to hand out to judges or to hang up at the registration desk.
**Status:** Implemented

## Preconditions

- The user is signed in and is editing a saved series in `SeriesView`.
- The "Categories" tab is active.

## Main Success Scenario

1. User clicks the "Categories sheet" link in the header of the categories grid.
2. System triggers a download.
3. `CategoriesReportService.createCategoriesSheet` loads the series name and all of its categories (ordered by abbreviation) together with each category's events (ordered by `position`).
4. System renders `CategoriesReport` in the user's locale: one A4 page (more if needed) with the columns abbreviation, name, gender, year from, year to and events.
5. Browser downloads `categories<seriesId>.pdf`.

## Alternative Flows

### A1: Series not found

**Trigger:** Step 3 — the series id is unknown.
**Flow:**

1. The series lookup (`orElseThrow`) raises an exception and the browser shows an error.

### A2: Series has no categories

**Trigger:** Step 3 — no categories exist yet.
**Flow:**

1. The PDF is generated with the header row only.

## Postconditions

### Success Postconditions

- A PDF listing every category of the series with its birth year range and its events is delivered.

### Failure Postconditions

- Unknown series: no file is downloaded; the user sees an error. PDF-generation errors are swallowed and yield an empty (0-byte) download.

## Business Rules

### BR-073: Open birth year bounds are printed empty

A category whose `year_from` is the open lower bound (`CategoryYears.OPEN_FROM`, 1900) or whose `year_to` is the open upper bound (`CategoryYears.OPEN_TO`, 9999) is printed with an empty "year from" / "year to" cell instead of the sentinel value.

### BR-074: Events in category order

The events of a category are listed in the order defined by `category_event.position`, i.e. the same order as on the result sheets (UC-097, UC-098).
