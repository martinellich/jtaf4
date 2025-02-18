# JTAF 4 Development Guidelines

## Project Overview
JTAF (Track And Field) is a web application for managing track and field competitions. It's built with modern Java and web technologies, focusing on reliability and user experience.

## Tech Stack
- **Backend**: Java 21, Spring Boot 3.4
- **Frontend**: Vaadin 24
- **Database**: PostgreSQL with jOOQ
- **Security**: Spring Security
- **Testing**: JUnit, TestContainers, Playwright, ArchUnit
- **Build Tools**: Maven
- **CI/CD**: GitHub Actions, SonarCloud

## Project Structure
```
├── src/
│   ├── main/
│   │   ├── java/         # Java backend code
│   │   ├── frontend/     # TypeScript/Vaadin frontend
│   │   └── resources/    # Configuration files
│   └── test/             # Test files
├── .github/              # GitHub Actions workflows
├── scripts/              # Utility scripts
└── pom.xml              # Maven configuration
```

## Build and Run
1. **Prerequisites**:
   - JDK 21
   - Maven
   - Node.js
   - Docker (for local database)

2. **Local Development**:
   ```bash
   # Start database
   docker compose up -d
   
   # Build and run
   ./mvnw spring-boot:run
   ```

## Testing
- **Unit Tests**: `./mvnw test`
- **Integration Tests**: `./mvnw verify`
- **UI Tests**: Playwright tests in test directory
- **Coverage**: JaCoCo reports generated during verify phase

## Development Guidelines
1. **Code Style**:
   - Follow Spring Java Format
   - Use provided .editorconfig settings

2. **Git Workflow**:
   - Feature branches from 'develop'
   - Pull requests for all changes
   - CI must pass before merge

3. **Database**:
   - Use jOOQ for database access
   - Run migrations through Spring Boot

## Best Practices
1. Write tests for new features
2. Keep components focused and small
3. Follow security best practices
4. Document API changes
5. Use type-safe queries with jOOQ
6. Keep frontend components reusable
7. Regular dependency updates via Dependabot
8. Never change pom.xml without asking
9. Never change KaribuTest class
