# Use Case: Download club ranking

## Overview

**Use Case ID:** UC-092
**Use Case Name:** Download club ranking
**Primary Actor:** Visitor
**Goal:** Get the cumulative ranking of clubs across all competitions of a series as a PDF.
**Status:** Implemented

## Preconditions

- The series exists and is not hidden.
- At least one result is associated with an athlete that has a club.

## Main Success Scenario

1. Visitor clicks "Club Ranking" for a series on the dashboard.
2. System triggers a download.
3. `SeriesRankingService.getClubRankingAsPdf` runs the jOOQ query that sums all `RESULT.points` grouped by `athlete.club.NAME` for the series.
4. System renders `ClubRankingReport` in the visitor's locale.
5. Browser downloads `club_ranking<seriesId>.pdf`.

## Alternative Flows

### A1: Athlete without club

**Trigger:** Step 3 — `RESULT.athlete().club().NAME` is null.
**Flow:**

1. The athlete's points still appear under a null/empty club row in the report.

### A2: No results yet

**Trigger:** Step 3 — the series has no results.
**Flow:**

1. The optional resolves but the report shows an empty list.

## Postconditions

### Success Postconditions

- A PDF is delivered to the visitor.

### Failure Postconditions

- No file is downloaded; the visitor sees an error.

## Business Rules

### BR-061: Aggregate over the whole series

Club ranking sums points from every competition in the series.

### BR-062: Public access

Club rankings are downloadable without authentication.
