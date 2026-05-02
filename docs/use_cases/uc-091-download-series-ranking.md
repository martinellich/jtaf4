# Use Case: Download series ranking

## Overview

**Use Case ID:** UC-091
**Use Case Name:** Download series ranking
**Primary Actor:** Visitor
**Goal:** Get the cumulative ranking of athletes across all competitions of a series as a PDF.
**Status:** Implemented

## Preconditions

- The series exists and is not hidden.
- The series contains competitions and results.

## Main Success Scenario

1. Visitor clicks "Series Ranking" for a series on the dashboard.
2. System triggers an Anchor download targeted to a new browser tab.
3. `SeriesRankingService.getSeriesRankingAsPdf` runs the multiset jOOQ query that aggregates per category and athlete the sum of points per competition.
4. System renders the result with `SeriesRankingReport` in the visitor's locale.
5. Browser downloads `series_ranking<seriesId>.pdf`.

## Alternative Flows

### A1: No data

**Trigger:** Step 3 — the series has no competitions or results.
**Flow:**

1. `getSeriesRanking` returns an empty optional and the report invocation throws.
2. Browser receives an error response.

## Postconditions

### Success Postconditions

- A PDF is delivered to the visitor.

### Failure Postconditions

- No file is downloaded; the visitor sees an error.

## Business Rules

### BR-059: Eligibility

Only athletes with `CATEGORY_ATHLETE.DNF = false` are included in the series ranking.

### BR-060: Public access

Series rankings are downloadable without authentication.
