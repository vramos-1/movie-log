---
applyTo: '**/*'
---

# Developer Coding Standards & AI Guidelines

## Core Principles
- **Code Quality > Speed**: Write maintainable, testable code
- **DRY (Don't Repeat Yourself)**: Extract reusable logic into modules
- **Separation of Concerns**: Keep layers independent and focused
- **Security First**: Never compromise on security fundamentals
- **Performance Matters**: Optimize database queries and API responses
- **Documentation**: Comment complex logic and maintain up-to-date docs

## AI Usage Guidelines
- **Use AI for Assistance, Not Replacement**: AI can help with code suggestions, but always review and understand the code it generates.
- **Security Review**: Ensure AI-generated code adheres to security best practices and does not introduce vulnerabilities.
- **Maintain Code Quality**: AI-generated code should meet the same quality standards as human-written code, including readability and maintainability.
- **Test AI-Generated Code**: Always write tests for AI-generated code to ensure it functions correctly and does not introduce bugs.
- **Ethical Considerations**: Avoid using AI to generate code that could be harmful, unethical, or violate privacy standards.

## Architecture Standards
- **Layered Architecture**: Follow a clear separation of concerns between layers (e.g., controllers, services, repositories).
- **Modularity**: Organize code into modules that encapsulate related functionality.
- **Dependency Injection**: Use dependency injection to manage dependencies and improve testability.
- **Error Handling**: Implement consistent error handling strategies across the application.
- **Logging**: Use structured logging to facilitate debugging and monitoring.
- **Configuration Management**: Use environment variables or configuration files to manage application settings securely. 
- **API Design**: Follow RESTful principles for API design and ensure consistent naming conventions.
- **Database Design**: Use normalized database schemas and optimize queries for performance.

### Layer Responsibilities
- **Controllers**: Handle HTTP requests, validate input, and delegate to services.
- **Services**: Contain business logic and coordinate between controllers and repositories.
- **Repositories**: Handle data access and interactions with the database.
- **Models**: Define data structures and validation rules.

**Frontend Layer (React/TypeScript):**
- UI components and user interactions only
- State management (React Query, Context API)
- Form validation and user feedback
- NO business logic or direct database access

**Backend Layer (Spring Boot/Java):**
- Business logic and validation
- Authentication/authorization
- Data transformation and aggregation
- API endpoint definitions
- Database queries via proper ORM/query builders

**Database Layer:**
- Data persistence
- Relationships and constraints
- Triggers and stored procedures (when necessary)
- NO business logic

### API Design Standards

**RESTful conventions (Spring Boot):**
- `GET /api/events` - List all events
- `GET /api/events/{id}` - Get single event
- `POST /api/events` - Create event
- `PUT /api/events/{id}` - Update event (full)
- `PATCH /api/events/{id}` - Update event (partial)
- `DELETE /api/events/{id}` - Delete event

**Consistent response format (Spring Boot):**
```java
public record ApiResponse<T>(
    boolean success,
    T data,
    Meta meta,
    ApiError error
) {}

public record Meta(int page, int limit, long total) {}
public record ApiError(String code, String message, Object details) {}

@GetMapping("/api/events")
public ResponseEntity<ApiResponse<List<EventDto>>> listEvents() {
    List<EventDto> data = eventService.findAll();
    return ResponseEntity.ok(new ApiResponse<>(true, data, null, null));
}

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        ApiError error = new ApiError("VALIDATION_ERROR", "Invalid input data", ex.getBindingResult().getFieldErrors());
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, null, error));
    }
}
```

## Security Standards (CRITICAL)

### Authentication & Authorization

**JWT best practices (Spring Security):**
```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

- Use short-lived access tokens.
- Keep JWT secrets in environment variables or secure external config.
- Pin and validate JWT algorithms.
- Validate token claims (`iss`, `aud`, `exp`) before trusting identity.

**Password security (Spring Security):**
```java
@Bean
PasswordEncoder passwordEncoder() {
  return new BCryptPasswordEncoder(12);
}

String hash = passwordEncoder.encode(rawPassword);
boolean valid = passwordEncoder.matches(rawPassword, storedHash);
```

### Input Validation

**Always validate and sanitize (Bean Validation):**
```java
public record CreateEventRequest(
    @NotBlank @Size(min = 3, max = 100) String title,
    @Size(max = 500) String description,
    @NotNull @FutureOrPresent Instant date,
    @NotNull @Positive Long userId
) {}

@PostMapping("/api/events")
public ResponseEntity<ApiResponse<EventDto>> createEvent(@Valid @RequestBody CreateEventRequest request) {
    EventDto created = eventService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse<>(true, created, null, null));
}
```

## Forbidden Practices

### NEVER:
- Put business logic in UI components
- Access database directly from controllers/endpoints
- Use `SELECT *` in queries
- Hardcode API URLs or secrets
- Commit `.env` files
- Use ad-hoc console logging in production
- Ignore SQL injection risks
- Skip input validation
- Run containers as root
- Use `latest` tag in production
- Store passwords in plain text
- Trust user input
- Skip error handling
- Mix concerns across layers