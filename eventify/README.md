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
├── event
│   ├── Event.java
│   ├── EventController.java
│   ├── EventDto.java
│   ├── EventMapper.java
│   └── EventRepository.java
|   └── EventService.java
|   └── EventSpecifications.java
├── participant
│   ├── Participant.java
│   ├── ParticipantController.java
│   ├── ParticipantRepository.java
│   └── ParticipantService.java
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
