package com.cramsan.edifikana.lib.supa

import com.cramsan.edifikana.lib.UserPk
import kotlinx.serialization.Serializable

@Serializable
@SupabaseModel
data class User(
    val pk: String? = null,
    val id: String? = null,
) {
    companion object {
        const val COLLECTION = "users"

        private fun documentId(
            id: String?,
        ): UserPk {
            requireNotNull(id)
            require(id.isNotBlank())
            return UserPk(id)
        }

        fun create(id: String): User {
            return User(
                pk = documentId(id).documentPath,
                id = id,
            )
        }
    }
}
