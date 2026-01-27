package com.cramsan.templatereplaceme.server.service.models

import com.cramsan.templatereplaceme.lib.model.UserId

/**
 * Domain model representing a user.
 */
data class User(val id: UserId, val firstName: String, val lastName: String)
