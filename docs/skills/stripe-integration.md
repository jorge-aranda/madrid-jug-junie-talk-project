# Skill: Stripe Integration — `payments` Domain

> **Type:** Integration guide (documentation only — no code implementation).
>
> This document describes how to integrate [Stripe](https://stripe.com) into the project as a new
> `payments` domain following the hexagonal architecture established by the `tasks` reference
> implementation.

---

## 1. Prerequisites — Stripe Configuration

Before starting the implementation, gather the following configuration values from the
[Stripe Dashboard](https://dashboard.stripe.com):

| Parameter | Description | Example |
|---|---|---|
| **Publishable Key** | Client-side key used in frontend integrations | `pk_test_51Abc...` |
| **Secret Key** | Server-side key for Stripe API calls | `sk_test_51Abc...` |
| **Webhook Secret** | Secret used to verify incoming webhook signatures | `whsec_Abc123...` |
| **Default Currency** | ISO 4217 currency code for payments | `EUR`, `USD` |
| **Payment Mode** | Type of payments to support | `one-time`, `subscription`, or `both` |
| **Stripe API Version** | Pinned API version for stability | `2024-12-18.acacia` |

> ⚠️ **Never commit real keys.** Store them in environment variables or a secrets manager.

### Application Configuration

Add the following to `application.yml`:

```yaml
stripe:
  secret-key: ${STRIPE_SECRET_KEY}
  publishable-key: ${STRIPE_PUBLISHABLE_KEY}
  webhook-secret: ${STRIPE_WEBHOOK_SECRET}
  default-currency: ${STRIPE_DEFAULT_CURRENCY:eur}
  api-version: ${STRIPE_API_VERSION:2024-12-18.acacia}
```

### Gradle Dependency

Add the Stripe Java SDK to `build.gradle.kts`:

```kotlin
implementation("com.stripe:stripe-java:28.2.0")
```

---

## 2. Domain Design — `payments`

### Package Structure

```
com.bank.api.payments
├── api
│   ├── controller
│   │   ├── PaymentController.kt          # REST endpoints for payments
│   │   └── StripeWebhookController.kt    # Webhook receiver
│   └── model
│       ├── CreatePaymentRequestDto.kt    # Inbound DTO
│       ├── PaymentResponseDto.kt         # Outbound DTO
│       └── WebhookEventDto.kt            # Webhook payload DTO
├── application
│   ├── usecase
│   │   ├── CreatePaymentIntentUseCase.kt
│   │   ├── ConfirmPaymentUseCase.kt
│   │   ├── GetPaymentDetailUseCase.kt
│   │   ├── ListUserPaymentsUseCase.kt
│   │   └── HandleWebhookEventUseCase.kt
│   └── model
│       ├── CreatePaymentRequest.kt       # Application-level command
│       └── WebhookEvent.kt              # Application-level webhook model
├── domain
│   ├── model
│   │   ├── Payment.kt                   # Domain entity
│   │   └── PaymentStatus.kt            # Enum: PENDING, SUCCEEDED, FAILED, REFUNDED
│   ├── service
│   │   └── PaymentService.kt           # Domain business logic
│   └── repository
│       ├── PaymentRepository.kt         # Port (interface) — persistence
│       └── PaymentGateway.kt           # Port (interface) — Stripe operations
└── infrastructure
    ├── repository
    │   ├── PaymentRepositoryDbo.kt      # MongoRepository + PaymentDocument
    │   └── impl
    │       └── PaymentRepositoryImpl.kt # Adapter: implements PaymentRepository
    ├── gateway
    │   └── impl
    │       └── StripePaymentGatewayImpl.kt  # Adapter: implements PaymentGateway
    └── config
        └── StripeConfig.kt              # @Configuration: initializes Stripe SDK
```

### Key Design Decisions

1. **Two domain ports:** Unlike `tasks` (which only has a repository port), `payments` needs a
   second port — `PaymentGateway` — to abstract Stripe API calls. This keeps the domain layer
   free of any Stripe SDK dependency.

2. **Webhook handling:** Stripe communicates payment results asynchronously via webhooks. A
   dedicated `StripeWebhookController` receives these events and delegates to
   `HandleWebhookEventUseCase`.

3. **Idempotency:** Stripe supports idempotency keys natively. The `CreatePaymentIntentUseCase`
   should use the payment's UUIDv4 as the idempotency key to guarantee safe retries.

---

## 3. Layer-by-Layer Implementation Guide

### 3.1 Domain Layer

#### `Payment` (domain entity)

```kotlin
data class Payment(
    val id: String,            // UUIDv4
    val userId: String,        // From Spring Security Principal
    val amount: Long,          // Amount in smallest currency unit (e.g. cents)
    val currency: String,      // ISO 4217
    val status: PaymentStatus,
    val stripePaymentIntentId: String?,
    val description: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

#### `PaymentGateway` (domain port — interface only)

```kotlin
interface PaymentGateway {
    fun createPaymentIntent(payment: Payment): String   // Returns Stripe PaymentIntent ID
    fun retrievePaymentIntent(stripeId: String): Payment
    fun cancelPaymentIntent(stripeId: String)
}
```

#### `PaymentRepository` (domain port — interface only)

```kotlin
interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findById(id: String): Payment?
    fun findByUserId(userId: String): List<Payment>
    fun findByStripePaymentIntentId(stripeId: String): Payment?
}
```

#### `PaymentService` (domain logic)

- Validates amounts (must be > 0).
- Validates currency codes.
- Enforces business rules (e.g. cannot cancel a succeeded payment).
- Delegates persistence to `PaymentRepository` and external calls to `PaymentGateway`.

### 3.2 Application Layer

#### Use Cases

| Use Case | Trigger | Description |
|---|---|---|
| `CreatePaymentIntentUseCase` | `PUT /api/payments` | Creates a `Payment`, calls `PaymentGateway.createPaymentIntent`, persists result |
| `ConfirmPaymentUseCase` | Webhook `payment_intent.succeeded` | Updates payment status to `SUCCEEDED` |
| `GetPaymentDetailUseCase` | `GET /api/payments/{id}` | Returns payment details for the authenticated user |
| `ListUserPaymentsUseCase` | `GET /api/payments` | Lists all payments for the authenticated user |
| `HandleWebhookEventUseCase` | `POST /api/payments/webhook` | Routes webhook events to the appropriate use case |

Each use case follows the single `execute(...)` method pattern, annotated with `@Component`.

### 3.3 API Layer

#### REST Endpoints

| Method | Path | Description |
|---|---|---|
| `PUT` | `/api/payments` | Create a payment intent (idempotent) |
| `GET` | `/api/payments` | List current user's payments |
| `GET` | `/api/payments/{paymentId}` | Get payment detail |
| `POST` | `/api/payments/webhook` | Stripe webhook receiver (no auth) |

> The webhook endpoint must be **excluded from Spring Security authentication** since Stripe
> calls it directly. Instead, verify the `Stripe-Signature` header using the webhook secret.

#### Security Configuration Update

Add to `SecurityConfig.kt`:

```kotlin
.requestMatchers(HttpMethod.POST, "/api/payments/webhook").permitAll()
```

### 3.4 Infrastructure Layer

#### `StripeConfig`

A `@Configuration` class that reads `stripe.*` properties and initializes the Stripe SDK:

```kotlin
@Configuration
class StripeConfig(
    @Value("\${stripe.secret-key}") private val secretKey: String,
    @Value("\${stripe.api-version}") private val apiVersion: String
) {
    @PostConstruct
    fun init() {
        Stripe.apiKey = secretKey
        Stripe.apiVersion = apiVersion
    }
}
```

#### `StripePaymentGatewayImpl`

A `@Component` that implements `PaymentGateway` by calling the Stripe Java SDK:

- `createPaymentIntent` → `PaymentIntent.create(params)` with amount, currency, and
  idempotency key.
- `retrievePaymentIntent` → `PaymentIntent.retrieve(stripeId)`.
- `cancelPaymentIntent` → `PaymentIntent.retrieve(stripeId).cancel()`.

#### `PaymentRepositoryImpl`

A `@Component` that implements `PaymentRepository` by delegating to a Spring Data
`MongoRepository<PaymentDocument, String>`, mapping between `Payment` (domain) and
`PaymentDocument` (infrastructure).

#### Webhook Signature Verification

In `StripeWebhookController`, verify the webhook payload before processing:

```kotlin
val event = Webhook.constructEvent(
    payload,
    sigHeader,
    webhookSecret
)
```

---

## 4. Visibility Rules Compliance

| Layer | Can see | Cannot see |
|---|---|---|
| `domain` | Only itself | `application`, `api`, `infrastructure`, Stripe SDK |
| `application` | `domain` | `api`, `infrastructure`, Stripe SDK |
| `api` | `application` (and limited `domain`) | `infrastructure`, Stripe SDK |
| `infrastructure` | `domain`, Stripe SDK | `application`, `api` |

> The Stripe SDK is **only** imported in the `infrastructure` layer. The domain defines the
> `PaymentGateway` port; the infrastructure provides the Stripe adapter.

---

## 5. Testing Strategy

| Layer | Test Type | Approach |
|---|---|---|
| Domain | Unit tests | Test `PaymentService` with a `FakePaymentRepository` and `FakePaymentGateway` |
| Application | Unit tests | Test each use case with fakes for both ports |
| API | Integration tests | Use `@WebMvcTest` with mocked use cases |
| Infrastructure | Integration tests | Use Testcontainers (MongoDB) + Stripe mock server or `stripe-mock` |

> Use the same `Mother` / `Fake` patterns established in the `tasks` domain tests.

---

## 6. Checklist

- [ ] Gather Stripe keys and configure `application.yml` with environment variables.
- [ ] Add `com.stripe:stripe-java` dependency to `build.gradle.kts`.
- [ ] Create the full `com.bank.api.payments` package structure.
- [ ] Implement domain layer: `Payment`, `PaymentStatus`, `PaymentGateway`, `PaymentRepository`,
      `PaymentService`.
- [ ] Implement application layer: all use cases.
- [ ] Implement infrastructure layer: `StripeConfig`, `StripePaymentGatewayImpl`,
      `PaymentRepositoryImpl`, `PaymentRepositoryDbo`.
- [ ] Implement API layer: `PaymentController`, `StripeWebhookController`, DTOs.
- [ ] Update `SecurityConfig` to allow unauthenticated webhook access.
- [ ] Add domain-specific `AGENTS.md` at `src/main/kotlin/com/bank/api/payments/AGENTS.md`.
- [ ] Register the domain in `docs/DOMAINS.md`.
- [ ] Update `README.md` with the new endpoints.
- [ ] Write unit and integration tests following the `tasks` test patterns.
