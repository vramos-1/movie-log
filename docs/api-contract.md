# API Contract (Draft)

Base URL: /api

## Auth
- POST /auth/register
- POST /auth/login
- GET /auth/me
- GET /me/log?sort=recent|title&page=1&limit=10

## Movies
- GET /movies/search?query={query}
- GET /movies/{id}

Movie search notes:
- Requires authentication header: Authorization Bearer token.
- query must be at least 2 characters.

Example movie search success payload:
- data: [{ id, title, releaseYear, posterUrl }]

Example movie details success payload:
- data: { id, title, releaseYear, synopsis, posterUrl }

## Ratings
- PUT /movies/{id}/rating
- GET /movies/{id}/rating/me

Rating notes:
- Requires authentication header: Authorization Bearer token.
- score is an integer from 1 to 5.
- GET /movies/{id}/rating/me returns success true with data null if the user has not rated the movie yet.

## Reviews
- POST /movies/{id}/reviews
- GET /movies/{id}/reviews
- PATCH /reviews/{reviewId}
- DELETE /reviews/{reviewId}

Review notes:
- Requires authentication header: Authorization Bearer token.
- reviewText must be 5 to 1000 characters.
- Only the review owner can update or delete a review.

## Response Envelope

All success responses should use:

- success: true
- data: payload
- meta: optional pagination metadata
- error: null

All error responses should use:

- success: false
- data: null
- meta: null
- error: { code, message, details }
