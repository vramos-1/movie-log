# ADR 0002 - Movie Data Source

Status: Accepted (Phase 1)

Use a local seeded movie catalog for Milestone 3, then move to an external movie API adapter in a later phase.

## Rationale
- Delivers search/details quickly without introducing provider keys or API rate limits.
- Keeps implementation deterministic for local development and integration tests.

## Next Steps
- Implement adapter interface so seed-based data can be swapped with external providers.
- Choose provider and API client strategy for Phase 2.
- Define caching and rate limit handling before external integration.
