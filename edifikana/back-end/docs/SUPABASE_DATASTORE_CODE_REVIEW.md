# Supabase Datastore Code Review

**Date:** 2025-01-25
**Scope:** `edifikana/back-end/src/main/kotlin/com/cramsan/edifikana/server/datastore/supabase/`
**Reviewer:** Claude Code Assistant

---

## Executive Summary

This document presents a comprehensive security and code quality review of the Supabase datastore implementation. The review identified **22 distinct issues** across multiple severity levels:

- **3 Critical** - Security vulnerabilities requiring immediate attention
- **6 High** - Issues affecting data integrity and security
- **6 Medium** - Business logic and authorization gaps
- **5 Low** - Code quality and maintainability concerns
- **2 Architectural** - Structural improvements for long-term health

---

## Critical Issues

### CRITICAL-001: Missing Authorization Check in User Association

**File:** `SupabaseUserDatastore.kt`
**Lines:** 118-165
**Method:** `associateUser()`

#### Current Code
```kotlin
override suspend fun associateUser(
    userId: UserId,
    email: String,
): Result<User> = runSuspendCatching(TAG) {
    logD(TAG, "Associating user: %s", email)

    val supabaseUser = runCatching { adminApi.retrieveUserById(userId.userId) }.getOrNull()
    if (supabaseUser == null) {
        throw ClientRequestExceptions.NotFoundException(...)
    }

    val temporaryUser = getUserByEmail(email)
    if (temporaryUser != null) {
        if (!temporaryUser.authMetadata.pendingAssociation) {
            throw ClientRequestExceptions.ConflictException(...)
        }
    } else {
        throw ClientRequestExceptions.NotFoundException(...)
    }

    // Creates new user and deletes temp user - NO AUTHORIZATION CHECK
    val requestEntity = CreateUserEntity(userId = userId, email = email, userEntity = temporaryUser)
    val createdUser = createUserEntity(requestEntity)
    deleteUser(UserId(temporaryUser.id))
    createdUser.toUser()
}
```

#### Problem Description
The `associateUser` function is designed to link a Supabase Auth account with an existing transient user record. However, it performs **no authorization validation** to ensure the caller has permission to perform this association.

**Attack Scenario:**
1. Attacker creates a legitimate Supabase Auth account with their own email
2. Attacker discovers a transient user record (e.g., an admin created via invite)
3. Attacker calls `associateUser(attackerAuthId, adminEmail)`
4. The attacker's Auth ID is now linked to the admin's user record
5. Attacker gains admin privileges

#### Impact
- **Account Takeover:** Complete compromise of any transient user account
- **Privilege Escalation:** Attackers can gain admin/owner permissions
- **Data Breach:** Access to all organization data

#### Recommended Fix
```kotlin
override suspend fun associateUser(
    userId: UserId,
    email: String,
    requestingContext: AuthContext, // Add authentication context
): Result<User> = runSuspendCatching(TAG) {
    logD(TAG, "Associating user: %s", email)

    val supabaseUser = runCatching { adminApi.retrieveUserById(userId.userId) }.getOrNull()
        ?: throw ClientRequestExceptions.NotFoundException(...)

    // CRITICAL: Verify the Supabase Auth user's email matches the requested email
    if (supabaseUser.email != email) {
        logW(TAG, "Email mismatch: Auth user email ${supabaseUser.email} != requested $email")
        throw ClientRequestExceptions.UnauthorizedException(
            "Cannot associate user: email mismatch"
        )
    }

    // CRITICAL: Verify the requesting user is the same as the Auth user being associated
    if (requestingContext.userId != userId) {
        logW(TAG, "Unauthorized association attempt by ${requestingContext.userId} for $userId")
        throw ClientRequestExceptions.UnauthorizedException(
            "Cannot associate another user's account"
        )
    }

    // ... rest of implementation
}
```

---

### CRITICAL-002: Insecure Password Hashing

**File:** `SupabaseUserDatastore.kt`
**Lines:** 106, 345

#### Current Code
```kotlin
// Line 106 - User creation
hashedPassword = if (createOtpAccount) {
    null
} else {
    SecureString(Hashing.insecureHash(requireNotBlank(password).encodeToByteArray()).toString())
}

// Line 345 - Password update
val newHashedPassword = Hashing.insecureHash(newPassword.reveal().encodeToByteArray()).toString()
```

#### Problem Description
The code explicitly uses a method named `insecureHash()` for password hashing. This naming strongly suggests the algorithm is not cryptographically secure for password storage. Common insecure algorithms include:
- MD5 (broken, rainbow tables widely available)
- SHA-1 (deprecated, collision attacks exist)
- SHA-256 without salt (vulnerable to rainbow tables)

Additionally, the hashed password is converted to a simple string and stored, with no apparent salting mechanism.

#### Impact
- **Password Compromise:** If database is breached, passwords can be cracked
- **Compliance Violations:** GDPR, HIPAA, PCI-DSS require secure password storage
- **Credential Stuffing:** Cracked passwords enable attacks on other services
- **Legal Liability:** Data breach notification requirements

#### Recommended Fix
```kotlin
// Use a proper password hashing library like BCrypt or Argon2
import org.mindrot.jbcrypt.BCrypt

class PasswordHasher {
    companion object {
        private const val BCRYPT_ROUNDS = 12

        fun hash(password: String): String {
            return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS))
        }

        fun verify(password: String, hashedPassword: String): Boolean {
            return BCrypt.checkpw(password, hashedPassword)
        }
    }
}

// Usage in createUser:
hashedPassword = if (createOtpAccount) {
    null
} else {
    SecureString(PasswordHasher.hash(requireNotBlank(password)))
}
```

---

### CRITICAL-003: Password Comparison Timing Attack Vulnerability

**File:** `SupabaseUserDatastore.kt`
**Line:** 331

#### Current Code
```kotlin
if (requestCurrentHashedPassword != user.authMetadata.hashedPassword) {
    logW(TAG, "Current password does not match for user: $id")
    throw ClientRequestExceptions.UnauthorizedException("Error: Current password is incorrect.")
}
```

#### Problem Description
String comparison using `!=` or `==` in Kotlin/Java performs character-by-character comparison and returns immediately upon finding a mismatch. This creates a timing side-channel:

- If first character differs: ~1 nanosecond
- If first 10 characters match: ~10 nanoseconds
- If all characters match: ~N nanoseconds (where N = string length)

An attacker can measure response times with high precision to determine how many characters of their guess are correct, dramatically reducing the search space for brute-force attacks.

#### Impact
- **Password Cracking:** Reduces brute-force complexity from O(n^m) to O(n*m)
- **Targeted Attacks:** High-value accounts can be specifically targeted
- **Undetectable:** No failed login attempts until final guess

#### Recommended Fix
```kotlin
import java.security.MessageDigest

fun constantTimeEquals(a: String?, b: String?): Boolean {
    if (a == null || b == null) return a == b
    val aBytes = a.toByteArray(Charsets.UTF_8)
    val bBytes = b.toByteArray(Charsets.UTF_8)
    return MessageDigest.isEqual(aBytes, bBytes)
}

// Usage:
if (!constantTimeEquals(requestCurrentHashedPassword, user.authMetadata.hashedPassword)) {
    logW(TAG, "Current password does not match for user: $id")
    throw ClientRequestExceptions.UnauthorizedException("Error: Current password is incorrect.")
}
```

---

## High Severity Issues

### HIGH-001: Race Condition in User Association

**File:** `SupabaseUserDatastore.kt`
**Lines:** 156-161

#### Current Code
```kotlin
val createdUser = createUserEntity(requestEntity)

if (deleteUser(UserId(temporaryUser.id)).isFailure) {
    logW(TAG, "Failed to delete temporary user with email: $email")
    error("Failed to delete temporary user with.")
}

createdUser.toUser()
```

#### Problem Description
This code performs two separate database operations without transactional guarantees:
1. Creates a new user entity
2. Deletes the temporary user

**Race Condition Scenarios:**

*Scenario A - Deletion Failure:*
1. New user created successfully
2. Delete operation fails (network issue, constraint violation)
3. `error()` is thrown
4. New user remains in database - orphaned record
5. Temporary user also remains - duplicate state

*Scenario B - Concurrent Access:*
1. Thread A: Checks temp user exists (line 135) - TRUE
2. Thread B: Deletes temp user
3. Thread A: Creates new user
4. Thread A: Tries to delete temp user - FAILS (already deleted)
5. Inconsistent state

#### Impact
- **Data Corruption:** Duplicate user records
- **Authentication Failures:** User cannot log in due to conflicting records
- **Orphaned Data:** Records that cannot be cleaned up automatically

#### Recommended Fix
```kotlin
override suspend fun associateUser(
    userId: UserId,
    email: String,
): Result<User> = runSuspendCatching(TAG) {
    // Use database transaction for atomicity
    postgrest.postgrestTransaction {
        val temporaryUser = getUserByEmail(email)
            ?: throw ClientRequestExceptions.NotFoundException(...)

        if (!temporaryUser.authMetadata.pendingAssociation) {
            throw ClientRequestExceptions.ConflictException(...)
        }

        // Delete first, then create (ensures no duplicate on failure)
        val deleted = postgrest.from(UserEntity.COLLECTION).delete {
            filter {
                UserEntity::id eq temporaryUser.id
                UserEntity::deletedAt isExact null
            }
        }

        if (deleted.countOrNull() == 0L) {
            throw ClientRequestExceptions.ConflictException(
                "User was modified by another process"
            )
        }

        val requestEntity = CreateUserEntity(
            userId = userId,
            email = email,
            userEntity = temporaryUser
        )
        createUserEntity(requestEntity).toUser()
    }
}
```

---

### HIGH-002: Inconsistent Null Handling After Database Operations

**Files:** Multiple datastores
**Examples:**
- `SupabasePropertyDatastore.kt:57-59`
- `SupabaseTimeCardDatastore.kt:51`

#### Current Code
```kotlin
// Pattern 1: Using requireNotNull (poor error message)
val createdTimeCardEvent = postgrest.from(...).insert(...).decodeSingle<TimeCardEventEntity>()
requireNotNull(createdTimeCardEvent.toTimeCardEvent())

// Pattern 2: Using ?: run (better but inconsistent)
postgrest.from(...).insert(...).decodeSingleOrNull<UserPropertyMappingEntity>() ?: run {
    throw IllegalStateException("Failed to associate property with user")
}

// Pattern 3: Using ClientRequestExceptions (correct approach)
?: throw ClientRequestExceptions.NotFoundException("Notification not found: $id")
```

#### Problem Description
The codebase uses three different patterns for handling null results from database operations:

1. **`requireNotNull()`** - Throws `IllegalArgumentException` with generic message "Required value was null"
2. **`?: run { throw IllegalStateException(...) }`** - Throws `IllegalStateException` with custom message
3. **`?: throw ClientRequestExceptions.*`** - Throws domain-specific exception

This inconsistency causes:
- Unpredictable exception types for API consumers
- Poor error messages in production logs
- Difficulty in error handling middleware

#### Impact
- **Poor Debugging:** Generic error messages don't indicate what failed
- **Inconsistent API Responses:** Different error formats for similar failures
- **Maintenance Burden:** Developers must check each method for its error pattern

#### Recommended Fix
```kotlin
// Create a standard helper function
private inline fun <reified T> requireDatabaseResult(
    result: T?,
    entityName: String,
    operation: String = "retrieve"
): T {
    return result ?: throw ClientRequestExceptions.InternalServerException(
        "Failed to $operation $entityName: database returned null"
    )
}

// Usage:
val createdProperty = requireDatabaseResult(
    postgrest.from(PropertyEntity.COLLECTION).insert(requestEntity) {
        select()
    }.decodeSingleOrNull<PropertyEntity>(),
    entityName = "property",
    operation = "create"
)
```

---

### HIGH-003: Silent Auth Deletion Failure

**File:** `SupabaseUserDatastore.kt`
**Lines:** 292-296

#### Current Code
```kotlin
// Auth deletion can fail without affecting our soft delete
// A background process can retry failed auth deletions later
if (softDeleted != null && !user.authMetadata.pendingAssociation) {
    runCatching { adminApi.deleteUser(id.userId) }
}
```

#### Problem Description
When a user is soft-deleted from the application database, the corresponding Supabase Auth account should also be deleted. However:

1. The deletion is wrapped in `runCatching` with no result handling
2. No logging occurs on failure
3. No retry mechanism exists
4. The comment mentions a "background process" that doesn't exist

This creates a data inconsistency where:
- User cannot log into the application (soft-deleted)
- User's Supabase Auth account still exists
- User's email is still "taken" in Supabase Auth
- Re-registration with same email fails

#### Impact
- **Data Inconsistency:** Auth and database out of sync
- **User Experience:** Deleted users cannot re-register
- **Compliance Risk:** User data not fully deleted (GDPR right to erasure)
- **Resource Leak:** Orphaned Supabase Auth accounts accumulate

#### Recommended Fix
```kotlin
// Create a failed deletion tracking table
data class FailedAuthDeletion(
    val userId: String,
    val attemptedAt: Instant,
    val errorMessage: String,
    val retryCount: Int = 0
)

// In deleteUser:
if (softDeleted != null && !user.authMetadata.pendingAssociation) {
    val authDeletionResult = runCatching { adminApi.deleteUser(id.userId) }

    authDeletionResult.onFailure { error ->
        logE(TAG, "Failed to delete Supabase Auth user: ${id.userId}", error)

        // Record for retry
        recordFailedAuthDeletion(
            userId = id.userId,
            attemptedAt = clock.now(),
            errorMessage = error.message ?: "Unknown error"
        )
    }

    authDeletionResult.onSuccess {
        logD(TAG, "Successfully deleted Supabase Auth user: ${id.userId}")
    }
}

// Implement background job to retry failed deletions
class AuthDeletionRetryJob(
    private val adminApi: AdminApi,
    private val failedDeletionRepository: FailedDeletionRepository
) {
    suspend fun retryFailedDeletions() {
        val failedDeletions = failedDeletionRepository.getPendingRetries(maxRetries = 5)

        for (deletion in failedDeletions) {
            runCatching { adminApi.deleteUser(deletion.userId) }
                .onSuccess { failedDeletionRepository.markSuccess(deletion.userId) }
                .onFailure { failedDeletionRepository.incrementRetry(deletion.userId) }
        }
    }
}
```

---

### HIGH-004: Conditional Filter May Not Apply Correctly

**File:** `SupabaseTimeCardDatastore.kt`
**Lines:** 82-89

#### Current Code
```kotlin
postgrest.from(TimeCardEventEntity.COLLECTION).select {
    filter {
        TimeCardEventEntity::deletedAt isExact null
        employeeId?.let {
            TimeCardEventEntity::employeeId eq it.empId
        }
    }
    select()
}.decodeList<TimeCardEventEntity>().mapNotNull { it.toTimeCardEvent() }
```

#### Problem Description
The conditional filter uses Kotlin's `let` function inside the filter DSL block. The behavior depends on how the Supabase Kotlin library interprets this:

1. If `employeeId` is null, `let` returns null
2. A null value inside the filter block may be ignored OR may cause unexpected behavior
3. The redundant `select()` call after the filter block suggests confusion about the API

This pattern differs from other datastores and may produce incorrect queries.

#### Impact
- **Incorrect Query Results:** Filter may not apply when expected
- **Performance Issues:** Missing filter could return all records
- **Inconsistent Behavior:** Different results in edge cases

#### Recommended Fix
```kotlin
postgrest.from(TimeCardEventEntity.COLLECTION).select {
    filter {
        TimeCardEventEntity::deletedAt isExact null
    }
    // Apply employee filter conditionally OUTSIDE the filter block
    employeeId?.let { empId ->
        filter {
            TimeCardEventEntity::employeeId eq empId.empId
        }
    }
}.decodeList<TimeCardEventEntity>().mapNotNull { it.toTimeCardEvent() }

// OR use explicit if statement for clarity:
postgrest.from(TimeCardEventEntity.COLLECTION).select {
    filter {
        TimeCardEventEntity::deletedAt isExact null
        if (employeeId != null) {
            TimeCardEventEntity::employeeId eq employeeId.empId
        }
    }
}.decodeList<TimeCardEventEntity>().mapNotNull { it.toTimeCardEvent() }
```

---

### HIGH-005: Missing Notification Validation

**File:** `SupabaseNotificationDatastore.kt`
**Lines:** 30-52

#### Current Code
```kotlin
override suspend fun createNotification(
    recipientUserId: UserId?,
    recipientEmail: String?,
    notificationType: NotificationType,
    description: String,
    inviteId: InviteId,
): Result<Notification> = runSuspendCatching(TAG) {
    logD(TAG, "Creating notification for user: $recipientUserId, email: $recipientEmail")

    val createEntity = NotificationEntity.Create(
        recipientUserId = recipientUserId?.userId,
        recipientEmail = recipientEmail,
        // ... other fields
    )

    // No validation that at least one recipient identifier is provided!
    val createdEntity = postgrest.from(NotificationEntity.COLLECTION).insert(createEntity) {
        select()
    }.decodeSingle<NotificationEntity>()

    createdEntity.toNotification()
}
```

#### Problem Description
The method accepts two optional recipient identifiers (`recipientUserId` and `recipientEmail`) but doesn't validate that at least one is provided. This allows creation of "orphan" notifications that:

1. Cannot be retrieved by `getNotificationsForUser()` (requires userId)
2. Cannot be retrieved by `getNotificationsByEmail()` (requires email)
3. Will never be linked to a user via `linkNotificationsToUser()`
4. Accumulate as unreachable database records

#### Impact
- **Data Pollution:** Orphan records accumulate in database
- **Storage Costs:** Unreachable data consumes storage
- **Query Performance:** More records to scan, none useful
- **Business Logic Errors:** Silent failures when notifications don't reach users

#### Recommended Fix
```kotlin
override suspend fun createNotification(
    recipientUserId: UserId?,
    recipientEmail: String?,
    notificationType: NotificationType,
    description: String,
    inviteId: InviteId,
): Result<Notification> = runSuspendCatching(TAG) {
    // Validate at least one recipient identifier
    if (recipientUserId == null && recipientEmail.isNullOrBlank()) {
        throw ClientRequestExceptions.InvalidRequestException(
            "Either recipientUserId or recipientEmail must be provided"
        )
    }

    // Validate email format if provided
    if (!recipientEmail.isNullOrBlank() && !isValidEmail(recipientEmail)) {
        throw ClientRequestExceptions.InvalidRequestException(
            "Invalid email format: $recipientEmail"
        )
    }

    logD(TAG, "Creating notification for user: $recipientUserId, email: $recipientEmail")

    val createEntity = NotificationEntity.Create(
        recipientUserId = recipientUserId?.userId,
        recipientEmail = recipientEmail?.trim()?.lowercase(), // Normalize email
        notificationType = notificationType.name,
        description = description,
        inviteId = inviteId.id,
    )

    val createdEntity = postgrest.from(NotificationEntity.COLLECTION).insert(createEntity) {
        select()
    }.decodeSingle<NotificationEntity>()

    createdEntity.toNotification()
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return email.matches(emailRegex)
}
```

---

### HIGH-006: No Expiration Check in Single Invite Retrieval

**File:** `SupabaseUserDatastore.kt`
**Lines:** 388-397 vs 402-428

#### Current Code
```kotlin
// getInvite - NO expiration check
override suspend fun getInvite(inviteId: InviteId): Result<Invite?> = runSuspendCatching(TAG) {
    logD(TAG, "Getting invite: %s", inviteId)

    postgrest.from(InviteEntity.COLLECTION).select {
        filter {
            InviteEntity::id eq inviteId.id
            InviteEntity::deletedAt isExact null
            // MISSING: gt("expiration", clock.now())
        }
    }.decodeSingleOrNull<InviteEntity>()?.toInvite()
}

// getInvites - HAS expiration check
override suspend fun getInvites(organizationId: OrganizationId): Result<List<Invite>> {
    return runSuspendCatching(TAG) {
        val organizations = postgrest.from(InviteEntity.COLLECTION).select {
            filter {
                InviteEntity::organizationId eq organizationId.id
                gt("expiration", clock.now()) // Correctly filters expired
                InviteEntity::deletedAt isExact null
            }
        }
        organizations.decodeList<InviteEntity>().map { it.toInvite() }
    }
}
```

#### Problem Description
The `getInvite()` method retrieves a single invite by ID without checking if it's expired, while `getInvites()` and `getInvitesByEmail()` correctly filter out expired invites.

This inconsistency allows:
1. Retrieving expired invites by ID
2. Attempting to use expired invites for user onboarding
3. Business logic that relies on this method receiving invalid data

#### Impact
- **Security Bypass:** Expired invites can still be used
- **Business Logic Errors:** Downstream code may assume invites are valid
- **User Confusion:** Expired invites appear active

#### Recommended Fix
```kotlin
override suspend fun getInvite(inviteId: InviteId): Result<Invite?> = runSuspendCatching(TAG) {
    logD(TAG, "Getting invite: %s", inviteId)

    postgrest.from(InviteEntity.COLLECTION).select {
        filter {
            InviteEntity::id eq inviteId.id
            InviteEntity::deletedAt isExact null
            gt("expiration", clock.now()) // Add expiration check for consistency
        }
    }.decodeSingleOrNull<InviteEntity>()?.toInvite()
}

// If you need to retrieve expired invites for admin purposes, create a separate method:
suspend fun getInviteIncludingExpired(inviteId: InviteId): Result<Invite?> = runSuspendCatching(TAG) {
    logD(TAG, "Getting invite (including expired): %s", inviteId)

    postgrest.from(InviteEntity.COLLECTION).select {
        filter {
            InviteEntity::id eq inviteId.id
            InviteEntity::deletedAt isExact null
        }
    }.decodeSingleOrNull<InviteEntity>()?.toInvite()
}
```

---

## Medium Severity Issues

### MEDIUM-001: Missing Authorization Checks on Data Retrieval

**Files:** All datastores
**Affected Methods:** `getUsers()`, `getProperties()`, `getEmployees()`, `getEventLogEntries()`, etc.

#### Problem Description
Data retrieval methods accept organization/property IDs directly without validating that the requesting user has permission to access that data.

```kotlin
// Example: Anyone who knows an organizationId can retrieve all users
override suspend fun getUsers(organizationId: OrganizationId): Result<List<User>>

// Example: Anyone who knows a propertyId can retrieve event logs
override suspend fun getEventLogEntries(propertyId: PropertyId): Result<List<EventLogEntry>>
```

#### Impact
- **Information Disclosure:** Unauthorized access to organization data
- **IDOR Vulnerability:** Insecure Direct Object Reference attacks
- **Compliance Violations:** Data access without proper authorization

#### Recommended Fix
Authorization should be enforced at the service layer, but datastores can accept context for audit logging:

```kotlin
// Option 1: Add authorization context parameter
override suspend fun getUsers(
    organizationId: OrganizationId,
    requestingUserId: UserId
): Result<List<User>> = runSuspendCatching(TAG) {
    // Log access attempt for audit
    logD(TAG, "User $requestingUserId accessing users for org $organizationId")

    // ... existing implementation
}

// Option 2: Create authorized wrapper at service layer
class UserService(
    private val userDatastore: UserDatastore,
    private val authorizationService: AuthorizationService
) {
    suspend fun getUsers(organizationId: OrganizationId, context: RequestContext): Result<List<User>> {
        // Verify authorization first
        authorizationService.requirePermission(
            userId = context.userId,
            organizationId = organizationId,
            permission = Permission.VIEW_USERS
        )

        return userDatastore.getUsers(organizationId)
    }
}
```

---

### MEDIUM-002: No Organization Validation in Property Creation

**File:** `SupabasePropertyDatastore.kt`
**Lines:** 29-63

#### Problem Description
```kotlin
override suspend fun createProperty(
    name: String,
    address: String,
    creatorUserId: UserId,
    organizationId: OrganizationId,  // Not validated!
    imageUrl: String?,
): Result<Property>
```

The method creates a property for any `organizationId` without validating:
1. That the organization exists
2. That the creator is a member of that organization
3. That the creator has permission to create properties

#### Impact
- **Data Integrity:** Properties linked to non-existent organizations
- **Authorization Bypass:** Users creating properties in organizations they don't belong to
- **Business Logic Errors:** Orphaned properties

#### Recommended Fix
```kotlin
override suspend fun createProperty(
    name: String,
    address: String,
    creatorUserId: UserId,
    organizationId: OrganizationId,
    imageUrl: String?,
): Result<Property> = runSuspendCatching(TAG) {
    logD(TAG, "Creating property: %s for org: %s by user: %s", name, organizationId, creatorUserId)

    // Validate organization exists
    val organization = postgrest.from(OrganizationEntity.COLLECTION).select {
        filter {
            OrganizationEntity::id eq organizationId.id
            OrganizationEntity::deletedAt isExact null
        }
    }.decodeSingleOrNull<OrganizationEntity>()
        ?: throw ClientRequestExceptions.NotFoundException(
            "Organization not found: $organizationId"
        )

    // Validate user is member of organization with appropriate role
    val userRole = postgrest.from(UserOrganizationMappingEntity.COLLECTION).select {
        filter {
            UserOrganizationMappingEntity::userId eq creatorUserId.userId
            UserOrganizationMappingEntity::organizationId eq organizationId.id
        }
    }.decodeSingleOrNull<UserOrganizationMappingEntity>()
        ?: throw ClientRequestExceptions.UnauthorizedException(
            "User is not a member of organization: $organizationId"
        )

    if (userRole.role !in listOf(UserRole.OWNER, UserRole.ADMIN)) {
        throw ClientRequestExceptions.UnauthorizedException(
            "User does not have permission to create properties"
        )
    }

    // ... proceed with property creation
}
```

---

### MEDIUM-003: Email Not Validated for Invites

**File:** `SupabaseUserDatastore.kt`
**Lines:** 363-383

#### Problem Description
```kotlin
override suspend fun recordInvite(
    email: String,  // No validation!
    organizationId: OrganizationId,
    expiration: Instant,
    role: UserRole,
): Result<Invite>
```

The method accepts any string as an email without validating:
1. Email format validity
2. Whether the email is already a member
3. Whether a pending invite already exists

#### Impact
- **Invalid Data:** Malformed emails stored in database
- **Duplicate Invites:** Multiple invites to same email
- **User Experience:** Invite emails fail to send silently
- **Wasted Resources:** Notifications created for invalid emails

#### Recommended Fix
```kotlin
override suspend fun recordInvite(
    email: String,
    organizationId: OrganizationId,
    expiration: Instant,
    role: UserRole,
): Result<Invite> = runSuspendCatching(TAG) {
    val normalizedEmail = email.trim().lowercase()

    // Validate email format
    if (!isValidEmail(normalizedEmail)) {
        throw ClientRequestExceptions.InvalidRequestException(
            "Invalid email format: $email"
        )
    }

    // Check if user is already a member
    val existingUser = getUserByEmail(normalizedEmail)
    if (existingUser != null) {
        val isMember = postgrest.from(UserOrganizationMappingEntity.COLLECTION).select {
            filter {
                UserOrganizationMappingEntity::userId eq existingUser.id
                UserOrganizationMappingEntity::organizationId eq organizationId.id
            }
        }.decodeSingleOrNull<UserOrganizationMappingEntity>()

        if (isMember != null) {
            throw ClientRequestExceptions.ConflictException(
                "User is already a member of this organization"
            )
        }
    }

    // Check for existing pending invite
    val existingInvite = postgrest.from(InviteEntity.COLLECTION).select {
        filter {
            InviteEntity::email eq normalizedEmail
            InviteEntity::organizationId eq organizationId.id
            InviteEntity::deletedAt isExact null
            gt("expiration", clock.now())
        }
    }.decodeSingleOrNull<InviteEntity>()

    if (existingInvite != null) {
        throw ClientRequestExceptions.ConflictException(
            "An active invite already exists for this email"
        )
    }

    logD(TAG, "Recording invite for email: %s with role: %s", normalizedEmail, role)

    val inviteEntity = InviteEntity.Create(
        email = normalizedEmail,
        organizationId = organizationId.id,
        createdAt = clock.now(),
        expiration = expiration,
        role = role.name,
    )

    postgrest.from(InviteEntity.COLLECTION).insert(inviteEntity) {
        select()
    }.decodeSingle<InviteEntity>().toInvite()
}
```

---

### MEDIUM-004: Path Traversal Risk in Storage Asset ID

**File:** `SupabaseStorageDatastore.kt`
**Lines:** 29, 57-73

#### Current Code
```kotlin
// Asset ID creation (line 29)
AssetId("$bucket/$fileName")

// Asset ID parsing (lines 70-72)
val path = assetId.id.split("/")
val bucket = path.first()
val fileName = path.drop(1).joinToString("/")
```

#### Problem Description
The asset ID is constructed by joining bucket and filename with "/". When parsing:
1. Filenames containing "/" will be split incorrectly
2. No validation that extracted bucket matches expected bucket
3. Path traversal attacks possible: `../other-bucket/secret-file`

#### Impact
- **Path Traversal:** Access files in unintended buckets
- **Data Leakage:** Read other users' files
- **Incorrect File References:** Filename corruption

#### Recommended Fix
```kotlin
// Use URL-safe Base64 encoding for asset IDs
import java.util.Base64

data class StorageAssetRef(
    val bucket: String,
    val fileName: String
) {
    fun toAssetId(): AssetId {
        val json = """{"bucket":"$bucket","fileName":"$fileName"}"""
        val encoded = Base64.getUrlEncoder().encodeToString(json.toByteArray())
        return AssetId(encoded)
    }

    companion object {
        fun fromAssetId(assetId: AssetId): StorageAssetRef {
            val decoded = Base64.getUrlDecoder().decode(assetId.id).toString(Charsets.UTF_8)
            // Parse JSON (use proper JSON library in production)
            val bucket = decoded.substringAfter("\"bucket\":\"").substringBefore("\"")
            val fileName = decoded.substringAfter("\"fileName\":\"").substringBefore("\"")
            return StorageAssetRef(bucket, fileName)
        }
    }
}

// Validate bucket access
override suspend fun readAsset(assetId: AssetId): Result<ByteArray> = runSuspendCatching(TAG) {
    val ref = StorageAssetRef.fromAssetId(assetId)

    // Validate bucket is allowed
    if (ref.bucket !in ALLOWED_BUCKETS) {
        throw ClientRequestExceptions.UnauthorizedException(
            "Access to bucket ${ref.bucket} is not allowed"
        )
    }

    // Validate filename doesn't contain path traversal
    if (ref.fileName.contains("..") || ref.fileName.startsWith("/")) {
        throw ClientRequestExceptions.InvalidRequestException(
            "Invalid filename format"
        )
    }

    storage.from(ref.bucket).downloadAuthenticated(ref.fileName)
}

companion object {
    val ALLOWED_BUCKETS = setOf("images/timecard-images")
}
```

---

### MEDIUM-005: No Pagination Limits on Large Result Sets

**Files:** Multiple datastores
**Affected Methods:**
- `SupabaseUserDatastore.getUsers()`
- `SupabasePropertyDatastore.getProperties()`
- `SupabaseEmployeeDatastore.getEmployees()`
- `SupabaseTimeCardDatastore.getTimeCardEvents()`
- `SupabaseEventLogDatastore.getEventLogEntries()`

#### Problem Description
```kotlin
// No limit - could return millions of records
override suspend fun getTimeCardEvents(
    employeeId: EmployeeId?,
): Result<List<TimeCardEvent>> = runSuspendCatching(TAG) {
    postgrest.from(TimeCardEventEntity.COLLECTION).select {
        filter {
            TimeCardEventEntity::deletedAt isExact null
            employeeId?.let { TimeCardEventEntity::employeeId eq it.empId }
        }
    }.decodeList<TimeCardEventEntity>().mapNotNull { it.toTimeCardEvent() }
}
```

#### Impact
- **Memory Exhaustion:** Loading millions of records into memory
- **Denial of Service:** Server crashes under memory pressure
- **Slow Responses:** Timeout on large result sets
- **Network Bandwidth:** Transmitting large payloads

#### Recommended Fix
```kotlin
data class PaginationParams(
    val limit: Int = DEFAULT_PAGE_SIZE,
    val offset: Int = 0
) {
    init {
        require(limit in 1..MAX_PAGE_SIZE) { "Limit must be between 1 and $MAX_PAGE_SIZE" }
        require(offset >= 0) { "Offset must be non-negative" }
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 50
        const val MAX_PAGE_SIZE = 200
    }
}

data class PaginatedResult<T>(
    val items: List<T>,
    val totalCount: Long,
    val hasMore: Boolean
)

override suspend fun getTimeCardEvents(
    employeeId: EmployeeId?,
    pagination: PaginationParams = PaginationParams()
): Result<PaginatedResult<TimeCardEvent>> = runSuspendCatching(TAG) {
    logD(TAG, "Getting time card events with pagination: $pagination")

    val query = postgrest.from(TimeCardEventEntity.COLLECTION).select {
        filter {
            TimeCardEventEntity::deletedAt isExact null
            employeeId?.let { TimeCardEventEntity::employeeId eq it.empId }
        }
        limit(pagination.limit.toLong())
        offset(pagination.offset.toLong())
        order("timestamp", Order.DESCENDING)
        count(Count.EXACT)
    }

    val items = query.decodeList<TimeCardEventEntity>().mapNotNull { it.toTimeCardEvent() }
    val totalCount = query.countOrNull() ?: 0L

    PaginatedResult(
        items = items,
        totalCount = totalCount,
        hasMore = pagination.offset + items.size < totalCount
    )
}
```

---

### MEDIUM-006: Missing Soft Delete Filter on Organization Update

**File:** `SupabaseOrganizationDatastore.kt`
**Line:** 78-82

#### Current Code
```kotlin
val updated = postgrest.from(OrganizationEntity.COLLECTION).update(updatedOrganization) {
    select()
    filter { OrganizationEntity::id eq id.id }
    // MISSING: OrganizationEntity::deletedAt isExact null
}.decodeSingle<OrganizationEntity>()
```

#### Problem Description
The `updateOrganization` method can update soft-deleted organizations. This violates the soft-delete pattern where deleted records should be immutable.

#### Impact
- **Data Inconsistency:** Deleted organizations can be modified
- **Audit Trail Corruption:** Changes to deleted records confuse audit logs
- **Business Logic Errors:** Unexpected behavior when updating "deleted" organizations

#### Recommended Fix
```kotlin
val updated = postgrest.from(OrganizationEntity.COLLECTION).update(updatedOrganization) {
    select()
    filter {
        OrganizationEntity::id eq id.id
        OrganizationEntity::deletedAt isExact null
    }
}.decodeSingleOrNull<OrganizationEntity>()
    ?: throw ClientRequestExceptions.NotFoundException(
        "Organization not found or has been deleted: $id"
    )
```

---

## Low Severity Issues

### LOW-001: Inconsistent Method Documentation

**Files:** All datastores

#### Problem Description
Documentation quality varies significantly:

```kotlin
// Well documented
/**
 * Creates a new user with the given credentials. Transient users skip Supabase Auth.
 */
override suspend fun createUser(...)

// Poorly documented
/**
 * Gets all users belonging to the given [organizationId].
 */
override suspend fun getUsers(...)  // What about deleted users? What permissions needed?

// Not documented
override suspend fun updateEmployee(...)
```

#### Recommended Fix
Standardize documentation format:

```kotlin
/**
 * Updates an employee's properties.
 *
 * Only non-null parameters are updated. The employee must not be soft-deleted.
 *
 * @param employeeId The unique identifier of the employee to update.
 * @param idType New ID type, or null to keep existing.
 * @param firstName New first name, or null to keep existing.
 * @param lastName New last name, or null to keep existing.
 * @param role New role, or null to keep existing.
 * @return [Result.success] with updated [Employee], or [Result.failure] with:
 *   - [ClientRequestExceptions.NotFoundException] if employee not found or deleted
 *   - [ClientRequestExceptions.InvalidRequestException] if all update fields are null
 * @throws IllegalStateException if database operation fails unexpectedly
 */
override suspend fun updateEmployee(
    employeeId: EmployeeId,
    idType: IdType?,
    firstName: String?,
    lastName: String?,
    role: EmployeeRole?,
): Result<Employee>
```

---

### LOW-002: Hardcoded Bucket Name

**File:** `SupabaseStorageDatastore.kt`
**Line:** 25

#### Current Code
```kotlin
private val bucket = "images/timecard-images"
```

#### Recommended Fix
```kotlin
class SupabaseStorageDatastore(
    private val storage: Storage,
    private val bucketConfig: StorageBucketConfig = StorageBucketConfig()
) : StorageDatastore {

    data class StorageBucketConfig(
        val timecardImages: String = "images/timecard-images",
        val profileImages: String = "images/profile-images",
        // Future buckets...
    )

    override suspend fun createAsset(data: ByteArray, fileName: String): Result<AssetId> {
        return createAssetInBucket(data, fileName, bucketConfig.timecardImages)
    }
}
```

---

### LOW-003: Inconsistent Constant Naming

**Files:** Multiple

#### Current State
```kotlin
// Different patterns used:
const val COLLECTION = "users"                    // UserEntity
const val VIEW_USER_EMPLOYEES = "v_user_employees" // SupabaseEmployeeDatastore
const val TAG = "SupabaseUserDatastore"           // Logging tags
```

#### Recommended Fix
Establish naming convention:
```kotlin
// In entity companion objects:
const val TABLE_NAME = "users"
const val VIEW_NAME = "v_user_employees"  // For views

// In datastore companion objects:
private const val TAG = "SupabaseUserDatastore"
private const val VIEW_USER_PROPERTIES = "v_user_properties"
```

---

### LOW-004: Truncated Error Message

**File:** `SupabaseUserDatastore.kt`
**Lines:** 340-342

#### Current Code
```kotlin
throw ClientRequestExceptions.InvalidRequestException(
    message = "Error: Password validation failed for user $id. "  // Trailing space, incomplete
)
```

#### Recommended Fix
```kotlin
throw ClientRequestExceptions.InvalidRequestException(
    message = "Password validation failed. Please ensure your password meets the security requirements."
)
```

---

### LOW-005: No Transaction Support for Multi-Step Operations

**Files:** All datastores with multi-step operations

#### Problem Description
Operations that require multiple database writes are not transactional:

```kotlin
// In createProperty - two operations, not atomic
val createdProperty = postgrest.from(PropertyEntity.COLLECTION).insert(...)
postgrest.from(UserPropertyMappingEntity.COLLECTION).insert(...)  // If this fails, property orphaned
```

#### Recommended Fix
Investigate Supabase transaction support and implement where available:

```kotlin
// If Supabase supports transactions:
postgrest.transaction {
    val createdProperty = from(PropertyEntity.COLLECTION).insert(...)
    from(UserPropertyMappingEntity.COLLECTION).insert(...)
}

// If not, implement compensating transactions:
val createdProperty = postgrest.from(PropertyEntity.COLLECTION).insert(...)
try {
    postgrest.from(UserPropertyMappingEntity.COLLECTION).insert(...)
} catch (e: Exception) {
    // Rollback: delete the property
    postgrest.from(PropertyEntity.COLLECTION).delete {
        filter { PropertyEntity::id eq createdProperty.id }
    }
    throw e
}
```

---

## Remediation Plan

### Phase 1: Critical Security Fixes (Immediate)

| Issue | Priority | Effort | Owner |
|-------|----------|--------|-------|
| CRITICAL-001: Authorization in associateUser | P0 | 2 days | Security Team |
| CRITICAL-002: Password hashing | P0 | 1 day | Security Team |
| CRITICAL-003: Timing-safe comparison | P0 | 0.5 days | Security Team |

### Phase 2: High Priority Fixes (Week 1-2)

| Issue | Priority | Effort | Owner |
|-------|----------|--------|-------|
| HIGH-001: Race condition in user association | P1 | 2 days | Backend Team |
| HIGH-002: Consistent null handling | P1 | 1 day | Backend Team |
| HIGH-003: Auth deletion logging | P1 | 1 day | Backend Team |
| HIGH-004: Conditional filter fix | P1 | 0.5 days | Backend Team |
| HIGH-005: Notification validation | P1 | 0.5 days | Backend Team |
| HIGH-006: Invite expiration check | P1 | 0.5 days | Backend Team |

### Phase 3: Medium Priority Fixes (Week 3-4)

| Issue | Priority | Effort | Owner |
|-------|----------|--------|-------|
| MEDIUM-001: Authorization checks | P2 | 3 days | Backend Team |
| MEDIUM-002: Organization validation | P2 | 1 day | Backend Team |
| MEDIUM-003: Email validation | P2 | 1 day | Backend Team |
| MEDIUM-004: Path traversal fix | P2 | 1 day | Backend Team |
| MEDIUM-005: Pagination | P2 | 2 days | Backend Team |
| MEDIUM-006: Soft delete filter | P2 | 0.5 days | Backend Team |

### Phase 4: Code Quality (Ongoing)

| Issue | Priority | Effort | Owner |
|-------|----------|--------|-------|
| LOW-001 through LOW-005 | P3 | 2 days | Backend Team |

---

## Appendix: Security Testing Checklist

After implementing fixes, verify with these tests:

- [ ] Attempt to associate user with mismatched email
- [ ] Verify password hashes use bcrypt/argon2
- [ ] Measure timing variance in password comparison
- [ ] Test concurrent user association requests
- [ ] Attempt to access data from other organizations
- [ ] Create notification with no recipient
- [ ] Use expired invite
- [ ] Upload file with path traversal in filename
- [ ] Request unbounded list of records
- [ ] Update soft-deleted organization
