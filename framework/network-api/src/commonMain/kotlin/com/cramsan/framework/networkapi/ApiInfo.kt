package com.cramsan.framework.networkapi

/**
 * Metadata describing an API, surfaced as the top-level `info` object in the generated OpenAPI
 * documentation. Declared alongside the API contract (in the `:api` module) so it is versioned with
 * the operations it describes.
 *
 * @property title The human-readable name of the API.
 * @property version The version of the API document.
 * @property description An optional longer description of the API.
 */
data class ApiInfo(val title: String, val version: String, val description: String? = null)
