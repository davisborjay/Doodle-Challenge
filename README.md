# Doodle Mini Backend

This is a simple backend service for managing **slots** and **bookings** (meetings), implemented using **Spring Boot WebFlux**, **R2DBC with H2**, and fully reactive programming. The service is designed to be run locally with Docker Compose and includes metrics and API documentation.

---

## Table of Contents

* [Tech Stack](#tech-stack)
* [Requirements](#requirements)
* [Running the Service](#running-the-service)
* [API Endpoints](#api-endpoints)
* [Swagger UI](#swagger-ui)
* [Metrics](#metrics)
* [Prometheus & Grafana](#prometheus--grafana)
* [Testing](#testing)
* [How to Consume the Service](#how-to-consume-the-service)

---

## Tech Stack

* Java 21
* Spring Boot 3.3.2
* Spring WebFlux (Reactive)
* Spring Data R2DBC
* H2 Database (in-memory)
* Gradle build system
* Docker & Docker Compose
* Swagger (OpenAPI)
* Prometheus & Grafana
* Reactor (Mono & Flux)
* Lombok

---

## Requirements

* Docker Desktop installed
* Java 21
* Gradle
* Optional: Postman or HTTP client for testing endpoints

---

## Running the Service

1. Clone the repository:

```bash
git clone <repository-url>
cd doodle-mini-backend
```

2. Build the project:

```bash
./gradlew clean build
```

3. Run with Docker Compose:

```bash
docker-compose up --build
```

This will start the following services:

* `doodle-backend` on port **8080**
* `Prometheus` on port **9090**
* `Grafana` on port **3000**

---

## API Endpoints

### Slots

| Method | Endpoint                 | Description                                       |
| ------ | ------------------------ | ------------------------------------------------- |
| POST   | `/api/v1/slots`          | Create a slot (requires `Idempotency-Key` header) |
| GET    | `/api/v1/slots/{userId}` | Get all slots for a user                          |
| PUT    | `/api/v1/slots/{slotId}` | Update a slot                                     |
| DELETE | `/api/v1/slots/{slotId}` | Delete a slot                                     |

### Bookings

| Method | Endpoint                | Description                                          |
| ------ | ----------------------- | ---------------------------------------------------- |
| POST   | `/api/v1/bookings`      | Create a booking (requires `Idempotency-Key` header) |
| GET    | `/api/v1/bookings/{id}` | Get booking by ID                                    |
| DELETE | `/api/v1/bookings/{id}` | Delete booking by ID                                 |

---

## Swagger UI

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## Metrics

Spring Boot Actuator exposes metrics at:

```
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/health
http://localhost:8080/actuator/info
```

You can also scrape metrics with Prometheus.

---

## Prometheus & Grafana

### Prometheus

* URL: `http://localhost:9090`
* Metrics path: `/actuator/prometheus`
* Scrape interval: 5s

### Grafana

* URL: `http://localhost:3000`
* Default login: `admin` / `admin`
* Add Prometheus as data source: `http://prometheus:9090`
* Import dashboards or create custom charts to visualize metrics like:

    * Number of slots per user
    * Active bookings
    * API request counts
    * Latency

---

## Testing

* Run tests with:

```bash
./gradlew test
```

---

## How to Consume the Service
* Use **HTTP POST/GET/PUT/DELETE** requests.
* Include the `Idempotency-Key` header when creating slots or bookings to ensure safe retries.

### Sample Users
The database is preloaded (via `schema.sql`) with the following users:

| id | name           | email                     |
|----|----------------|---------------------------|
| 1  | Alice Müller   | alice.muller@example.com   |
| 2  | Bob Schmidt    | bob.schmidt@example.com    |
| 3  | Carlos García  | carlos.garcia@example.com  |

Use these `userId` values in your API requests.

---

### Example cURL Requests

> 💡 **Note:** The syntax for `curl` may vary depending on your operating system.
>
> - On **Linux/macOS**, line breaks use `\`
> - On **Windows (CMD)**, use everything in a single line
> - On **Windows (PowerShell)**, use backticks (`` ` ``) for line breaks

#### Create a new slot (example for user 1)

**Linux/macOS:**
```bash
curl -X POST http://localhost:8080/api/v1/slots \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: key123" \
  -d '{"userId":1,"startTime":"2025-12-05T11:30:00","endTime":"2025-12-05T12:45:00"}'
```
**Windows (CMD):**
```bash
curl -X POST http://localhost:8080/api/v1/slots -H "Content-Type: application/json" -H "Idempotency-Key: key123" -d "{\"userId\":1,\"startTime\":\"2025-12-05T11:30:00\",\"endTime\":\"2025-12-05T12:45:00\"}"
```

* GET all slots for user 1:

```bash
curl http://localhost:8080/api/v1/slots/1
```
---