# Accounting Ledger Service

## Overview

A RESTful accounting ledger service built with Java, Spring Boot, Spring Data JPA, MySQL, and Flyway. It supports journal entries, entry reversals, account balance queries, pagination, and tenant isolation.

## Prerequisites

- Docker Desktop
- Git (if cloning the project)

## Run the Application

From the project root, run:

```bash
docker compose up -d --build
```

This starts the Spring Boot application and MySQL database. Flyway applies the database migrations automatically.

Check container status:

```bash
docker compose ps
```

View application logs:

```bash
docker compose logs app
```

Stop the containers:

```bash
docker compose down
```

The MySQL data is stored in a Docker volume named `ledger-data`, so stopping the containers does not normally delete the database data. Avoid `docker compose down -v` unless you intentionally want to delete the volume and its data.

## API Endpoints

All tenant-specific requests require the header:

`X-Tenant-Id: acme-corp`

Base URL: `http://localhost:8080`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/accounts` | Create an account |
| GET | `/api/v1/accounts/{accountCode}/balance` | Retrieve an account balance |
| POST | `/api/v1/journal-entries` | Create a journal entry |
| GET | `/api/v1/journal-entries` | List journal entries with pagination/date filtering |
| GET | `/api/v1/journal-entries/{id}` | Retrieve a journal entry |
| POST | `/api/v1/journal-entries/{id}/reverse` | Reverse a journal entry |

## Tests

Run the automated tests on Windows with the Maven Wrapper:

```powershell
.\mvnw.cmd test
```

On systems with Maven installed, you can also run:

```bash
mvn test
```

## Design Decisions

- **Money representation:** `BigDecimal` is used to represent monetary values accurately.
- **Double-entry validation:** Total debits must equal total credits for a journal entry.
- **Tenant isolation:** Queries are scoped by tenant ID.
- **Idempotency:** A repeated request with the same tenant and idempotency key returns the existing entry in the sequential case.
- **Reversals:** A reversal creates a corresponding entry with debit and credit amounts swapped rather than modifying the original entry.
- **Database migrations:** Flyway manages schema creation and versioning through SQL migration files.

## Assumptions and Limitations

- Account records must exist before journal entries can reference them.
- Payment webhook handling was not implemented.
- Idempotency has been tested for sequential duplicate requests; concurrency-safe handling has not been fully verified.
- Automated test coverage is limited to the scenarios currently implemented.

## Future Improvements

- Implement and test payment webhook handling with duplicate-notification protection.
- Strengthen idempotency under concurrent requests using database constraints and duplicate-key handling.
- Expand automated tests for reversals, balance calculations, tenant isolation, and error scenarios.
- Improve API error responses and documentation.