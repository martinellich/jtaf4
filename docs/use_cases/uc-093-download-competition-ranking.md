# Use Case: Download competition ranking

## Overview

**Use Case ID:** UC-093
**Use Case Name:** Download competition ranking
**Primary Actor:** Visitor
**Goal:** Download the per-category ranking for a single competition as a PDF.
**Status:** Implemented

## Preconditions

- The competition exists.
- The series is not hidden.

## Main Success Scenario

1. Visitor clicks "Competition Ranking" on the dashboard.
2. System triggers a download.
3. `CompetitionRankingService.getCompetitionRankingAsPdf` runs the jOOQ query that lists every category of the parent series and, per athlete, every event result with its points.
4. System renders `CompetitionRankingReport` in the visitor's locale.
5. Browser downloads `competition_ranking<competitionId>.pdf`.

## Alternative Flows

### A1: DNF athletes

**Trigger:** Step 3 — `CATEGORY_ATHLETE.DNF = true` for an athlete in the data set.
**Flow:**

1. The athlete is still listed but flagged as DNF in the report; medal computation excludes them.

### A2: No competition data

**Trigger:** Step 3 — competition id unknown.
**Flow:**

1. The optional is empty and the report invocation throws; the browser shows an error.

## Postconditions

### Success Postconditions

- A PDF is delivered listing every category, every athlete in the category, every event result, and the calculated points.

### Failure Postconditions

- No file is downloaded.

## Business Rules

### BR-063: Categories taken from series

The ranking enumerates all categories of the competition's parent series, even if no athlete contested some of them.

### BR-064: Medal indication

Medal awards are derived from `COMPETITION.always_first_three_medals` and `COMPETITION.medal_percentage`.
