# Use Case: Download diplomas

## Overview

**Use Case ID:** UC-094
**Use Case Name:** Download diplomas
**Primary Actor:** Registered User
**Goal:** Generate the printable diplomas (one per medal-winning athlete per category) for a competition.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The competition exists with results.

## Main Success Scenario

1. User clicks "Diploma" on the dashboard for a competition (button only visible to authenticated users).
2. System triggers a download.
3. `CompetitionRankingService.getDiplomasAsPdf` reuses the competition ranking dataset and renders `DiplomaReport` with the series logo.
4. Browser downloads `diploma<competitionId>.pdf`.

## Alternative Flows

### A1: No medals

**Trigger:** Step 3 — `medal_percentage = 0` and `always_first_three_medals = false`.
**Flow:**

1. No diploma pages exist, so the download is an empty (0-byte) file.

## Postconditions

### Success Postconditions

- A PDF with one diploma page per medal winner is delivered.

### Failure Postconditions

- Unknown competition: no file is downloaded; the user sees an error. PDF-generation errors are swallowed and yield an empty (0-byte) download.

## Business Rules

### BR-065: Medal eligibility

A diploma is generated only for athletes that qualify for a medal under the competition's medal scheme (UC-031 / BR-028).

### BR-066: Authenticated only

The diploma button is hidden for anonymous visitors.
