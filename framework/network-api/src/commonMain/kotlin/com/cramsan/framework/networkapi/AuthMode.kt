package com.cramsan.framework.networkapi

/**
 * Type-level declaration of the authentication gate an [Operation] requires. The mode is carried as a
 * phantom type parameter on [Operation] (see [Operation]'s `AuthType`), so it is the single, declarative
 * source of truth for an endpoint's auth: the server selects the matching gate and handler-context type
 * at compile time, and it is impossible to register a handler whose gate disagrees with the declaration.
 *
 * It is intentionally neutral to client and server — it carries no server-only context types, so it can
 * live in the shared API definition consumed by both ends. Operations are tagged with a mode by the
 * factory used to declare them: [Api.operation] (secure by default) yields [Required], while
 * [Api.publicOperation] and [Api.optionalOperation] yield [Public] and [Optional].
 */
sealed interface AuthMode {
    /**
     * Authentication is required. A valid bearer token must be supplied; a missing or invalid token is
     * rejected with 401 before the handler runs. The handler receives an authenticated context.
     */
    data object Required : AuthMode

    /**
     * No authentication. The endpoint is publicly accessible and the handler receives an unauthenticated
     * context.
     */
    data object Public : AuthMode

    /**
     * Authentication is optional. A valid token identifies the caller, while a request with no token is
     * still served; the handler receives a context that may or may not be authenticated.
     */
    data object Optional : AuthMode
}
