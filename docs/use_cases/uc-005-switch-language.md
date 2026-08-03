# Use Case: Switch language

## Overview

**Use Case ID:** UC-005
**Use Case Name:** Switch language
**Primary Actor:** Visitor
**Goal:** Change the UI and report language between English and German.
**Status:** Implemented

## Preconditions

- The application is loaded.

## Main Success Scenario

1. User clicks the language toggle button in the drawer footer ("DE" or "EN").
2. System sets the Vaadin session locale to the opposite language.
3. System reloads the current page so all translatable texts switch.

## Alternative Flows

_None._

## Postconditions

### Success Postconditions

- All subsequent views and PDF reports use the newly selected locale.

### Failure Postconditions

- The locale is unchanged.

## Business Rules

### BR-008: Supported locales

Only English and German are offered as runtime alternatives via the toggle button. (Resource bundles also exist for French.)

### BR-009: Locale-bound reports

PDF generation passes the active locale so column headers and labels are translated.
