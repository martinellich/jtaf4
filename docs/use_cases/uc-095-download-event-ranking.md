# Use Case: Download event ranking

## Overview

**Use Case ID:** UC-095
**Use Case Name:** Download event ranking
**Primary Actor:** Registered User
**Goal:** Get a per-event ranking PDF for a competition listing the best raw performances across all categories.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The competition exists with results.

## Main Success Scenario

1. User clicks "Event Ranking" on the dashboard for a competition (button only visible to authenticated users).
2. System triggers a download.
3. `CompetitionRankingService.getEventRankingAsPdf` runs the jOOQ query that lists every event of the organization and, per event, the distinct results of **this competition** across all categories. Results with empty values are excluded; ordering is ascending for runs and descending for jumps/throws.
4. System renders `EventsRankingReport` in the user's locale.
5. Browser downloads `event_ranking<competitionId>.pdf`.

## Alternative Flows

### A1: Event not contested

**Trigger:** Step 3 — an event has no results.
**Flow:**

1. The event still appears in the report with an empty result list.

## Postconditions

### Success Postconditions

- A PDF is delivered grouping results by event.

### Failure Postconditions

- Unknown competition: no file is downloaded; the user sees an error. PDF-generation errors are swallowed and yield an empty (0-byte) download.

## Business Rules

### BR-067: Cross-category view

Event ranking groups the competition's results by event regardless of category, useful for spotting overall best performances.

### BR-068: Authenticated only

The event-ranking button is hidden for anonymous visitors.
