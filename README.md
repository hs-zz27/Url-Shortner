# URL Shortener & Pastebin

A distributed, horizontally-scalable URL shortener and pastebin built with Java and Spring Boot. Users submit text or links and receive a short, shareable code, with optional expiration and view analytics.

## Features

- **Create** short codes for text/links (`POST /api/shorten`)
- **Retrieve** content by code (`GET /api/{code}`)
- **Delete** a paste (`DELETE /api/{code}`)
- **View analytics** — total and monthly visit stats (`GET /api/{code}/stats`)
- **Anonymous** — no accounts required
- **Content de-duplication** — identical content returns the existing code
- **Custom expiration (TTL)** — user-defined and optional (defaults to never)
- **Automatic cleanup** of expired pastes via a scheduled job
- **Distributed by design** — stateless app, shared state in Redis

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot (Web, Data JPA) |
| Database | PostgreSQL (Neon) |
| Cache / Store | Redis (Upstash) |
| ID generation | Snowflake → Base62 |
| Rate limiting | Redis + Lua (sliding window) |
| Distributed locking | ShedLock |
| Object mapping | MapStruct |
| Boilerplate | Lombok |
| Containerization | Docker, Docker Compose |

## Architecture

Request flow:

```
Client
  │
  ▼
Load Balancer (Nginx / cloud LB)
  │
  ▼
App instances (stateless, Spring Boot)  ── Snowflake IDs (no coordination)
  │
  ├──► PostgreSQL (Neon)   — source of truth, durable storage
  └──► Redis (Upstash)     — cache, rate limiting, view counters
```

Key design decisions:

- **Snowflake IDs + Base62** — each instance generates unique, roughly time-ordered IDs with no central coordination, then encodes them into short, URL-safe codes.
- **Stateless app tier** — all shared state (cache, rate-limit counters, view counts) lives in Redis, so any instance can serve any request and the app scales horizontally behind a load balancer.
- **Resilient caching** — reads are cached in Redis with a TTL; on Redis failure the app gracefully falls back to PostgreSQL instead of erroring.
- **Distributed sliding-window rate limiting** — implemented with a Redis sorted set and a Lua script for atomic, race-free enforcement across all instances.
- **Distributed-safe scheduled jobs** — cleanup and analytics-flush jobs use ShedLock so only one instance runs them at a time.
- **Read-optimized analytics** — views are counted in Redis on the hot path (asynchronously) and flushed to PostgreSQL in batches, keeping reads fast.

## Getting Started

### Prerequisites

- Docker & Docker Compose
- A Neon (PostgreSQL) database
- An Upstash (Redis) instance

### Configuration

Create a `.env` file in the project root:

```
NEON_URL=jdbc:postgresql://<endpoint>-pooler.neon.tech/urlshortener?sslmode=require
NEON_USER=<neon-user>
NEON_PASSWORD=<neon-password>

UPSTASH_HOST=<endpoint>.upstash.io
UPSTASH_PASSWORD=<upstash-password>
```

### Run with Docker Compose

```bash
docker-compose up --build
```

The API is available at `http://localhost:8080`.

### Run locally (without Docker)

```bash
./mvnw spring-boot:run
```

## API Reference

### Create a paste

```
POST /api/shorten
Content-Type: application/json

{
  "text": "your content or URL",
  "expiresInSeconds": 3600
}
```

`expiresInSeconds` is optional — omit it for a paste that never expires.

Response `201 Created` (or `200 OK` if the content already existed):

```json
{
  "shortCode": "b8Kx2p",
  "url": "http://localhost:8080/api/b8Kx2p",
  "newlyCreated": true
}
```

### Retrieve a paste

```
GET /api/{code}
```

Returns the stored content, or `404 Not Found` / `410 Gone` (expired).

### Delete a paste

```
DELETE /api/{code}
```

### Get statistics

```
GET /api/{code}/stats
```

```json
{
  "shortCode": "b8Kx2p",
  "totalViews": 2043,
  "monthlyViews": { "2026-06": 843, "2026-07": 1200 }
}
```

## Configuration Reference

| Variable | Description |
| --- | --- |
| `MACHINE_ID` | Unique ID per instance (0–1023) for Snowflake generation |
| `APP_BASE_URL` | Base URL used to build short links |
| `SPRING_DATASOURCE_URL` | PostgreSQL (Neon) JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `SPRING_DATA_REDIS_HOST` | Redis (Upstash) host |
| `SPRING_DATA_REDIS_PASSWORD` | Redis password |
| `SPRING_DATA_REDIS_SSL_ENABLED` | Must be `true` for Upstash |

## How It Works

**Short code generation** — A Snowflake generator produces a 64-bit ID from a timestamp, a per-instance machine ID, and a per-millisecond sequence counter, guaranteeing uniqueness across instances without any coordination. The ID is then Base62-encoded into a compact, URL-safe code.

**Rate limiting** — A sliding-window limiter backed by a Redis sorted set (scored by timestamp) enforces a per-client request cap. All steps run inside a Lua script so the check-and-record is atomic, preventing races across instances.

**Caching** — Reads are cached in Redis with a TTL. A custom cache error handler makes the cache optional: if Redis is unreachable, requests fall through to PostgreSQL rather than failing.

**Expiration & cleanup** — Users can set a TTL per paste. Expired pastes are rejected on read (`410 Gone`) and removed by a scheduled cleanup job, which uses ShedLock so only one instance runs it.

**Analytics** — Each view increments a monthly Redis counter asynchronously (off the read path). A scheduled, distributed-locked job flushes these counters into PostgreSQL for durable monthly history. The stats endpoint combines durable DB history with the live Redis counter for up-to-date totals.

## Scaling & Future Work

- **Horizontal scaling** — run multiple stateless instances (each with a unique `MACHINE_ID`) behind a load balancer.
- **Read replicas** — route reads to PostgreSQL replicas (managed by Neon) to scale read throughput.
- **Sharding** — if write volume or data size outgrows a single node, shard by hash of the short code, since lookups are key-based and require no cross-shard joins.
- **Multi-level caching** — add an in-process L1 cache in front of Redis for the hottest keys.
- **Deployment** — deployable on any container platform (Render, Railway, Fly.io) or Kubernetes with an Ingress load balancer and a CronJob for cleanup.
