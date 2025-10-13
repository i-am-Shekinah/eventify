# Eventify - Event Management REST API

## Overview

Eventify is a Spring Boot REST API for managing events and their participants. It allows organizations to create events, search for events using flexible filters, and add participants via CSV uploads. This project uses Spring Data JPA with an H2 in-memory database for persistence.

## Features

### Event Management

* Create, update, delete events.
* Search events by title, description, location, and date range using flexible filters.
* Event search uses an AND-based filter approach.

### Participant Management

* Add participants to events by uploading CSV files.
* CSV supports participant details: `firstname`, `lastname`, `email`, `phone`, `status`.
* Duplicate participants (same email for the same event) are skipped.
* Returns a summary including number of added participants and skipped duplicates.
* Retrieve all participants for a specific event.

## Technology Stack

* Java 25
* Spring Boot
* Spring Data JPA
* H2 Database
* Lombok
* Apache Commons CSV

## Project Structure

Feature-based project structure:

```
src/main/java/com/codewithmike/eventify
├── auth/
|   ├── AuthController.java
├── config/
|   ├── SwaggerConfig.java
├── event/
│   ├── Event.java
│   ├── EventController.java
│   ├── EventDto.java
│   ├── EventMapper.java
│   └── EventRepository.java
|   └── EventService.java
|   └── EventSpecifications.java
├── participant/
|   ├── InvitationStatus.java
│   ├── Participant.java
│   ├── ParticipantController.java
│   ├── ParticipantRepository.java
│   └── ParticipantService.java
├── security/
|   ├── JwtAuthenticationFilter.java
|   ├── JwtUtil.java
|   ├── SecurityConfig.java
|   ├── SecurityUtil.java
├── user/
|   ├── dto/
|   |   ├── UserCreateRequestDto.java
|   |   ├── UserDto.java
|   ├── User.java
|   ├── UserMapper.java
|   ├── UserRepository.java
└── EventifyApplication.java
```

## API Endpoints

### Event Endpoints

* `GET /api/events` - Get all events
* `POST /api/events` - Create a new event
* `PUT /api/events/{id}` - Update an event
* `DELETE /api/events/{id}` - Delete an event
* `GET /api/events/search` - Search events

    * Query parameters: `title`, `description`, `location`, `startDate`, `endDate`

### Participant Endpoints

* `POST /api/participants/upload/{eventId}` - Upload participants via CSV
* `GET /api/participants/event/{eventId}` - Get participants for an event

## Assumptions

* No user authentication or login system is implemented.
* Event IDs and Participant IDs use UUIDs.
* Duplicate participants for the same event are determined by email.
* Participant status defaults to `PENDING` if not specified in the CSV.
* CSV files are expected to have headers: `firstname,lastname,email,phone,status` (case-insensitive, order not strict).
* SearchEvents uses an AND-based filter; only filters provided by the user are applied.
* The database is in-memory H2 for simplicity; switching to another DB is possible.

## CSV Format Example

```
firstname,lastname,email,phone,status
John,Doe,john@example.com,1234567890,PENDING
Jane,Smith,jane@example.com,0987654321,CONFIRMED
```

## Running the Application

1. Clone the repository.
2. Build the project with Maven: `mvn clean install`
3. Run the application: `mvn spring-boot:run`
4. Use Postman or cURL to interact with the API endpoints.

## Future Enhancements

* Add user authentication and role-based access control.
* Add email invitations and confirmation for participants.
* Support bulk participant deletion.
* Add pagination for event and participant listings.
* Switch to a production-grade database (PostgreSQL/MySQL).


## Week 2 — Security, Ownership & Invitation Tracking

### New features:
- JWT-based authentication (signup/login).
- Event ownership: each event belongs to a user; users can only manage their own events and participants.
- Invitation tracking for participants (PENDING | ACCEPTED | DECLINED).
- Pagination support for event and participant listings (page, size, sort).
- Swagger docs updated to support Bearer token authorization.

### Testing:
1. POST /api/auth/signup -> create user
2. POST /api/auth/login -> receive { "token": "..." }
3. Add header: Authorization: Bearer <token> for subsequent requests
4. Create events, upload participants, list events with `?page=0&size=10`

### Assumptions:
- JWT signing key is generated on startup for dev; in production store a stable secret.
- Duplicate participants detection: email + event.
- Deleting an event cascades participants (JPA cascade configuration preserved).
- Only three invitation statuses allowed: PENDING, ACCEPTED, DECLINED.
- Users can only access, update, or delete their own events.

## 🎯 Eventify API Endpoints

### Base URL

```
/api/events
```

---

## 📘 Event Management Endpoints

| HTTP Method | Endpoint             | Description                                                                   | Auth Required | Request Body / Params                                                                                                                                                                       | Returns                      |
| ----------- | -------------------- | ----------------------------------------------------------------------------- |---------------| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------- |
| **GET**     | `/api/events`        | Fetch all events belonging to the currently authenticated user.               | Yes           | Pagination params: `?page=0&size=10&sort=title,asc`                                                                                                                                         | `Page<EventDto>`             |
| **POST**    | `/api/events`        | Create a new event for the authenticated user.                                | Yes           | JSON body: `{ "title": "...", "description": "...", "location": "...", "date": "..." }`                                                                                                     | `EventDto`                   |
| **PUT**     | `/api/events/{id}`   | Update (replace) an existing event by its ID (only if owned by current user). | Yes           | JSON body: `{ "title": "...", "description": "...", "location": "...", "date": "..." }`                                                                                                     | `EventDto`                   |
| **PATCH**   | `/api/events/{id}`   | Partially update an event (only change provided fields).                      | Yes           | JSON body: `{ "title": "...", "location": "..." }`                                                                                                                                          | `EventDto`                   |
| **DELETE**  | `/api/events/{id}`   | Delete an event owned by the current user.                                    | Yes           | —                                                                                                                                                                                           | `204 No Content` or `200 OK` |
| **GET**     | `/api/events/search` | Search the authenticated user's events using filters.                         | Yes           | Query params:<br>• `title` (optional)<br>• `description` (optional)<br>• `location` (optional)<br>• `startDate` (optional)<br>• `endDate` (optional)<br>• `page`, `size`, `sort` (optional) | `Page<EventDto>`             |

---

## 🔐 Ownership Rules

All endpoints automatically scope queries to the authenticated user:

```java
WHERE owner_id = currentUser.id
```

Users can only access, update, or delete their own events.

---

## 🧭 Example Requests

### 1️⃣ Get all events for current user

```bash
GET /api/events?page=0&size=5&sort=date,desc
Authorization: Bearer <JWT_TOKEN>
```

### 2️⃣ Search events

```bash
GET /api/events/search?title=workshop&location=lagos&startDate=2025-10-01T00:00:00
Authorization: Bearer <JWT_TOKEN>
```

### 3️⃣ Create an event

```bash
POST /api/events
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "title": "Tech Meetup",
  "description": "Developers gathering",
  "location": "Lagos",
  "date": "2025-11-01T18:00:00"
}
```
