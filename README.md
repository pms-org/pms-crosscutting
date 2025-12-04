# PMS CrossRef Service

A Spring Boot microservice for consuming and querying lifecycle events from Kafka.

## Features

- Consumes lifecycle events from Kafka topic `lifecycle.event`
- Stores events in PostgreSQL database
- Provides REST API for querying events by traceId and portfolioId
- Includes Swagger/OpenAPI documentation
- Containerized with Docker

## Quick Start

### Local Development

1. Start dependencies:
```bash
docker-compose -f docker-compose.dev.yml up postgres kafka zookeeper
```

2. Run the application:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Full Stack

```bash
docker-compose -f docker-compose.dev.yml up
```

## API Endpoints

- `GET /api/v1/lifecycle/{traceId}` - Get events by trace ID
- `GET /api/v1/lifecycle/portfolio/{portfolioId}` - Get events by portfolio ID
- `GET /actuator/health` - Health check
- `GET /swagger-ui.html` - API documentation

## Configuration

See `application.yml` and `application-dev.yml` for configuration options.