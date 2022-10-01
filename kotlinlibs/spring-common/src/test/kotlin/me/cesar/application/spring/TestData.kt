package me.cesar.application.spring

import kotlinx.datetime.Instant
import me.cesar.application.common.model.Article
import me.cesar.application.common.model.Source
import me.cesar.application.common.model.SourceType
import me.cesar.application.spring.storage.mongodb.entity.ArticleEntity
import me.cesar.application.spring.storage.mongodb.entity.SourceEntity

/**
 * @author cramsan
 */

val articleEntity1 = ArticleEntity(
    id = "id1",
    title = "title 1",
    sourceId = "12345",
    content = "This is an example",
    lastUpdated = 100,
)

val articleEntity2 = ArticleEntity(
    id = "id2",
    title = "title 2",
    sourceId = "6789",
    content = "This is another example",
    lastUpdated = 200,
)

val articleEntity3 = ArticleEntity(
    id = "id3",
    title = "title 3",
    sourceId = "9999",
    content = "Kotlin test",
    lastUpdated = 300,
)

val article1 = Article(
    id = "id1",
    title = "title 1",
    sourceId = "12345",
    content = "This is an example",
    publishedTime = Instant.fromEpochSeconds(100),
)

val article2 = Article(
    id = "id2",
    title = "title 2",
    sourceId = "6789",
    content = "This is another example",
    publishedTime = Instant.fromEpochSeconds(200),
)

val article3 = Article(
    id = "id3",
    title = "title 3",
    sourceId = "9999",
    content = "Kotlin test",
    publishedTime = Instant.fromEpochSeconds(300),
)

val source1 = Source(
    id = "id1",
    title = "title 1",
    url = "https://1/",
    lastUpdated = Instant.fromEpochSeconds(100),
    sourceType = SourceType.RSS,
)

val source2 = Source(
    id = "id2",
    title = "title 2",
    url = "https://2/",
    lastUpdated = Instant.fromEpochSeconds(200),
    sourceType = SourceType.UNKNOWN,
)

val source1Entity = SourceEntity(
    id = "id1",
    title = "title 1",
    url = "https://1/",
    lastUpdated = 100,
    sourceType = SourceType.RSS,
)

val source2Entity = SourceEntity(
    id = "id2",
    title = "title 2",
    url = "https://2/",
    lastUpdated = 200,
    sourceType = SourceType.UNKNOWN,
)