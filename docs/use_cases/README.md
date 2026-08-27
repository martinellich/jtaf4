# Use Case Index

The use case catalogue below is reverse-engineered from the JTAF source code (Vaadin views, dialogs, DAOs, and service classes). The matching use case overview diagram lives at [`../use_cases.puml`](../use_cases.puml); the corresponding data model lives at [`../entity_model.md`](../entity_model.md).

## Account

| ID     | Name              | Primary Actor   |
|--------|-------------------|-----------------|
| UC-001 | [Register account](uc-001-register-account.md)   | Visitor         |
| UC-002 | [Confirm e-mail](uc-002-confirm-email.md)        | Visitor         |
| UC-003 | [Sign in](uc-003-sign-in.md)                     | Visitor         |
| UC-004 | [Sign out](uc-004-sign-out.md)                   | Registered User |
| UC-005 | [Switch language](uc-005-switch-language.md)     | Visitor         |

## Organization

| ID     | Name                                | Primary Actor   |
|--------|-------------------------------------|-----------------|
| UC-010 | [Create organization](uc-010-create-organization.md)             | Registered User |
| UC-011 | [Edit organization](uc-011-edit-organization.md)                 | Registered User |
| UC-012 | [Delete organization](uc-012-delete-organization.md)             | Registered User |
| UC-013 | [Select active organization](uc-013-select-active-organization.md) | Registered User |

## Series

| ID     | Name                                          | Primary Actor   |
|--------|-----------------------------------------------|-----------------|
| UC-020 | [List series](uc-020-list-series.md)                          | Registered User |
| UC-021 | [Create series](uc-021-create-series.md)                      | Registered User |
| UC-022 | [Edit series](uc-022-edit-series.md)                          | Registered User |
| UC-023 | [Upload series logo](uc-023-upload-series-logo.md)            | Registered User |
| UC-024 | [Delete series](uc-024-delete-series.md)                      | Registered User |
| UC-025 | [Copy categories from another series](uc-025-copy-categories.md) | Registered User |

## Competition

| ID     | Name                                  | Primary Actor   |
|--------|---------------------------------------|-----------------|
| UC-030 | [Create competition](uc-030-create-competition.md) | Registered User |
| UC-031 | [Edit competition](uc-031-edit-competition.md)     | Registered User |
| UC-032 | [Delete competition](uc-032-delete-competition.md) | Registered User |

## Categories & Events

| ID     | Name                                              | Primary Actor   |
|--------|---------------------------------------------------|-----------------|
| UC-040 | [Manage category](uc-040-manage-category.md)                       | Registered User |
| UC-041 | [Assign event to category](uc-041-assign-event-to-category.md)     | Registered User |
| UC-042 | [Remove event from category](uc-042-remove-event-from-category.md) | Registered User |
| UC-050 | [Manage event (IAAF coefficients)](uc-050-manage-event.md)         | Registered User |

## Clubs & Athletes

| ID     | Name                                                  | Primary Actor   |
|--------|-------------------------------------------------------|-----------------|
| UC-060 | [Manage club](uc-060-manage-club.md)                                   | Registered User |
| UC-070 | [Manage athlete](uc-070-manage-athlete.md)                             | Registered User |
| UC-071 | [Search athletes](uc-071-search-athletes.md)                           | Registered User |
| UC-072 | [Assign athlete to series](uc-072-assign-athlete-to-series.md)         | Registered User |
| UC-073 | [Remove athlete from series](uc-073-remove-athlete-from-series.md)     | Registered User |
| UC-074 | [Import athletes from Excel](uc-074-import-athletes-from-excel.md)     | Registered User |

## Results

| ID     | Name                                                          | Primary Actor   |
|--------|---------------------------------------------------------------|-----------------|
| UC-080 | [Enter result](uc-080-enter-result.md)                                            | Registered User |
| UC-081 | [Mark athlete DNF](uc-081-mark-athlete-dnf.md)                                    | Registered User |
| UC-082 | [Remove athlete results](uc-082-remove-athlete-results.md)                        | Registered User |
| UC-083 | [Filter athletes for result entry](uc-083-filter-athletes-for-result-entry.md)    | Registered User |
| UC-084 | [Calculate IAAF points](uc-084-calculate-iaaf-points.md)                          | System          |

## Reports

| ID     | Name                                                              | Primary Actor   |
|--------|-------------------------------------------------------------------|-----------------|
| UC-090 | [View dashboard](uc-090-view-dashboard.md)                                         | Visitor         |
| UC-091 | [Download series ranking](uc-091-download-series-ranking.md)                       | Visitor         |
| UC-092 | [Download club ranking](uc-092-download-club-ranking.md)                           | Visitor         |
| UC-093 | [Download competition ranking](uc-093-download-competition-ranking.md)             | Visitor         |
| UC-094 | [Download diplomas](uc-094-download-diplomas.md)                                   | Registered User |
| UC-095 | [Download event ranking](uc-095-download-event-ranking.md)                         | Registered User |
| UC-096 | [Generate athlete numbers](uc-096-generate-athlete-numbers.md)                     | Registered User |
| UC-097 | [Generate result sheets](uc-097-generate-result-sheets.md)                         | Registered User |
| UC-098 | [Generate empty result sheets per category](uc-098-generate-empty-result-sheets.md) | Registered User |
