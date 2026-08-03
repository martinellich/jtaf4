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
3. `CompetitionRankingService.getEventRankingAsPdf` runs the jOOQ query that lists every event of the organization and, per event, the distinct results from all categories of the series.
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

- No file is downloaded; the user sees an error.

## Business Rules

### BR-067: Cross-category view

Event ranking groups by event regardless of category, useful for spotting overall best performances.

### BR-068: Authenticated only

The event-ranking button is hidden for anonymous visitors.
