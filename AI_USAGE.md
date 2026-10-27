# AI Usage Disclosure

## Tools Used

ChatGPT was used as an AI assistance tool during development.

## Areas Where AI Assistance Was Used

- Dockerfile and Docker Compose setup for running the Spring Boot application with MySQL.
- Troubleshooting Docker image builds, container startup, port conflicts, and database connectivity.
- Guidance on Flyway migrations and verifying database schema creation.
- Assistance with README documentation and explaining implementation decisions.
- Guidance on unit testing and identifying missing assignment requirements.

## Review and Corrections

AI-generated suggestions were reviewed and adapted to the project's configuration. Docker image names, Java version, database credentials, datasource settings, and port mappings were adjusted for this project. The containerized application was started and its journal-entry listing endpoint was tested.

## Testing

Three automated tests currently pass:
- Rejecting unbalanced journal entries.
- Returning an existing entry for a sequential duplicate idempotency key.
- Verifying tenant isolation when retrieving journal entries.

## Known Limitations

- The duplicate payment-notification test and payment webhook functionality were not implemented.
- Concurrent duplicate-request handling has not been fully verified.
- Additional automated tests and production-hardening improvements remain future work.

## Developer Responsibility

The developer is responsible for reviewing, adapting, and validating the implementation and for understanding the final code submitted.
