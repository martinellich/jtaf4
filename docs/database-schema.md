# JTAF Database Schema

This document contains the entity relationship diagram for the JTAF (Track and Field) application database.

## Database Schema Overview

The JTAF database is designed to support multi-tenant track and field competition management with the following key entities:

- **Organizations** - Top-level entities that own competitions and define events
- **Series** - Collections of related competitions within an organization
- **Competitions** - Individual track and field events with specific dates
- **Categories** - Age/gender-based groupings for athletes
- **Athletes** - Participants with club affiliations
- **Events** - Specific disciplines (100m sprint, long jump, shot put, etc.)
- **Results** - Performance data with automatic point calculations using IAAF formulas

## Entity Relationship Diagram

```mermaid
erDiagram
    organization {
        bigint id PK
        varchar organization_key UK
        varchar name
        varchar owner
    }
    
    security_user {
        bigint id PK
        varchar first_name
        varchar last_name
        varchar email UK
        varchar secret
        varchar confirmation_id
        boolean confirmed
    }
    
    security_group {
        bigint id PK
        varchar name UK
    }
    
    series {
        bigint id PK
        varchar name
        bytea logo
        boolean hidden
        boolean locked
        bigint organization_id FK
    }
    
    competition {
        bigint id PK
        varchar name
        date competition_date
        boolean always_first_three_medals
        int medal_percentage
        boolean locked
        bigint series_id FK
    }
    
    category {
        bigint id PK
        varchar abbreviation
        varchar name
        char gender
        int year_from
        int year_to
        bigint series_id FK
    }
    
    event {
        bigint id PK
        varchar abbreviation
        varchar name
        char gender
        varchar event_type
        double a
        double b
        double c
        bigint organization_id FK
    }
    
    club {
        bigint id PK
        varchar abbreviation
        varchar name
        bigint organization_id FK
    }
    
    athlete {
        bigint id PK
        varchar first_name
        varchar last_name
        char gender
        int year_of_birth
        bigint club_id FK
        bigint organization_id FK
    }
    
    result {
        bigint id PK
        int position
        varchar result
        int points
        bigint athlete_id FK
        bigint category_id FK
        bigint competition_id FK
        bigint event_id FK
    }
    
    category_athlete {
        bigint category_id FK
        bigint athlete_id FK
        boolean dnf
    }
    
    category_event {
        bigint category_id FK
        bigint event_id FK
        int position
    }
    
    organization_user {
        bigint organization_id FK
        bigint user_id FK
    }
    
    user_group {
        bigint user_id FK
        bigint group_id FK
    }

    %% Relationships
    organization ||--o{ series : "owns"
    organization ||--o{ event : "defines"
    organization ||--o{ club : "contains"
    organization ||--o{ athlete : "registers"
    organization ||--o{ organization_user : "has users"
    
    security_user ||--o{ organization_user : "belongs to orgs"
    security_user ||--o{ user_group : "has roles"
    security_group ||--o{ user_group : "assigned to users"
    
    series ||--o{ competition : "contains"
    series ||--o{ category : "defines"
    
    competition ||--o{ result : "records"
    
    category ||--o{ result : "participates in"
    category ||--o{ category_athlete : "includes"
    category ||--o{ category_event : "has events"
    
    event ||--o{ result : "measured in"
    event ||--o{ category_event : "part of categories"
    
    club ||--o{ athlete : "represents"
    
    athlete ||--o{ result : "achieves"
    athlete ||--o{ category_athlete : "competes in"
```

## Key Tables Description

### Core Competition Tables
- **organization**: Multi-tenant support with unique organization keys
- **series**: Groups related competitions (e.g., annual championship series)
- **competition**: Individual track and field events with dates and medal settings
- **category**: Age/gender groupings (e.g., "Boys U12", "Girls U14")
- **event**: Track and field disciplines with IAAF scoring coefficients (a, b, c)

### Participant Tables
- **athlete**: Competition participants with club affiliations
- **club**: Sports clubs that athletes represent
- **result**: Performance records linking athletes, events, categories, and competitions

### Security Tables
- **security_user**: Application users with authentication details
- **security_group**: Role-based access control groups
- **organization_user**: Many-to-many relationship for user-organization membership
- **user_group**: Many-to-many relationship for user-role assignments

### Junction Tables
- **category_athlete**: Links athletes to categories with DNF (Did Not Finish) flag
- **category_event**: Links events to categories with positioning information

## IAAF Scoring System

The `event` table contains coefficients (a, b, c) used in IAAF scoring formulas to convert raw performance results into standardized points:

- **RUN**: Short distance events - `points = a * ((b - time_in_centiseconds) / 100)^c`
- **RUN_LONG**: Long distance events - `points = a * ((b - time_in_centiseconds) / 100)^c`
- **JUMP_THROW**: Field events - `points = a * ((distance_in_centimeters - b) / 100)^c`

This ensures fair comparison across different disciplines in multi-event competitions.