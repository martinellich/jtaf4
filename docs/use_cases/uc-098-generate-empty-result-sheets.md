# Use Case: Generate empty result sheets per category

## Overview

**Use Case ID:** UC-098
**Use Case Name:** Generate empty result sheets per category
**Primary Actor:** Registered User
**Goal:** Print blank result sheets for a category, useful for late-registered athletes that don't yet have a bib.
**Status:** Implemented

## Preconditions

- The user is signed in and is editing a series in `SeriesView`.
- The "Categories" tab is active and at least one category exists.

## Main Success Scenario

1. User clicks the "Sheets" link on a category row.
2. System triggers a download.
3. `NumberAndSheetsService.createEmptySheets` builds a single dummy athlete with the category's name and its events (ordered by `position`).
4. System renders `SheetsReport` in the user's locale, using the series logo.
5. Browser downloads `sheet<categoryId>.pdf`.

## Alternative Flows

### A1: Category not found

**Trigger:** Step 3 — the category id is unknown.
**Flow:**

1. The category lookup (`orElseThrow`) raises an exception and the browser shows an error.

## Postconditions

### Success Postconditions

- A PDF with one blank A5 sheet for the category, listing all of its events, is delivered ready to be filled in by hand. The competition header row is left blank.

### Failure Postconditions

- Unknown category: no file is downloaded; the user sees an error. PDF-generation errors are swallowed and yield an empty (0-byte) download.

## Business Rules

### BR-072: Single dummy athlete

The empty sheet uses a placeholder "athlete" carrying only the category context, so judges can write the athlete's identity manually.
