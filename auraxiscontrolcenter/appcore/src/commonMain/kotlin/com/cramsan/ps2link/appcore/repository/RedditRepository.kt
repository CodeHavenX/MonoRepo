package com.cramsan.ps2link.appcore.repository

import com.cramsan.ps2link.core.models.RedditPage
import com.cramsan.ps2link.core.models.RedditPost

/**
 * @Author cramsan
 * @created 1/30/2021
 */

interface RedditRepository {

    suspend fun getPosts(
        redditPage: RedditPage,
    ): List<RedditPost>
}