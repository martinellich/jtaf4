# Use Case: Upload series logo

## Overview

**Use Case ID:** UC-023
**Use Case Name:** Upload series logo
**Primary Actor:** Registered User
**Goal:** Attach (or replace) a logo image that is used in PDF reports for the series.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The series exists and is loaded in `SeriesView`.

## Main Success Scenario

1. User clicks "Logo upload" or drops a file onto the upload widget.
2. Vaadin streams the file to the in-memory upload handler.
3. System sets the binary on the current `SERIES` record's `logo` column and saves the whole record (any pending form edits are persisted along with it).
4. Subsequent reports and the dashboard listing render the new logo.

## Alternative Flows

### A1: Multiple files dropped

**Trigger:** Step 1 — more than one file is selected.
**Flow:**

1. Upload component enforces `maxFiles = 1` and rejects additional files.

## Postconditions

### Success Postconditions

- The series `logo` blob is updated.
- Reports for this series render with the new logo.

### Failure Postconditions

- The previous logo (or absence thereof) remains.

## Business Rules

### BR-021: One logo per series

Each series stores a single logo image; uploading replaces the previous one.

### BR-022: Logo fallback

When generating reports the series logo is used if present; the report generators guard against a missing logo and render without one.
