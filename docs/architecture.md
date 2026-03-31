# Architecture Notes

## Frontend
- React + TypeScript
- Route-level feature modules
- API layer under src/api

## Backend
- Spring Boot with layered architecture
- Feature-oriented packages: auth, movie, review, rating, user
- Common API envelope and centralized exception handling

## Data
- Relational persistence model for users, movies, reviews, ratings
- H2 embedded database; default profile uses in-memory storage, `dev` profile uses file-based storage (`./data/movielog`) so data survives restarts
- Schema managed via `spring.jpa.hibernate.ddl-auto` (set per profile)
