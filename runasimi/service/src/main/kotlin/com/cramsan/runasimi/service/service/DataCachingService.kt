package com.cramsan.runasimi.service.service

import com.cramsan.framework.logging.logI
import org.ehcache.Cache
import org.ehcache.PersistentCacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.MemoryUnit
import java.io.File

/**
 * A basic implementation of an on-disk caching.
 */
class DataCachingService {

    /**
     * Returns if there is a value associated with [key] in the cache.
     */
    fun contains(key: String): Boolean {
        logI(TAG, "Contains for key: $key")
        return getCachingManager().use {
            getDataCache(it).containsKey(key)
        }
    }

    /**
     * Returns the value associated with [key] in the cache. Returns null if there is no value associated with [key].
     */
    fun get(key: String): ByteArray? {
        logI(TAG, "Get for key: $key")
        return getCachingManager().use {
            getDataCache(it).get(key)
        }
    }

    /**
     * Stores [data] in the cache and associates it with [key].
     */
    fun put(key: String, data: ByteArray) {
        logI(TAG, "Put for key: $key")
        getCachingManager().use {
            getDataCache(it).put(key, data)
        }
    }

    @Suppress("MagicNumber")
    private fun getCachingManager(): PersistentCacheManager {
        val cacheLocation = File(CACHE_FOLDER)
        cacheLocation.mkdirs()

        return newCacheManagerBuilder()
            .with(CacheManagerBuilder.persistence(File(cacheLocation.absolutePath, CACHE_FILE)))
            .withCache(
                ALIAS,
                newCacheConfigurationBuilder(
                    String::class.java,
                    ByteArray::class.java,
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .disk(200, MemoryUnit.MB, true),
                ),
            ).build(true)
    }

    private fun getDataCache(persistentCacheManager: PersistentCacheManager): Cache<String, ByteArray> {
        return persistentCacheManager.getCache(
            ALIAS,
            String::class.java,
            ByteArray::class.java,
        )
    }

    companion object {
        private const val CACHE_FOLDER = ".caching"
        private const val CACHE_FILE = "cache"
        private const val ALIAS = "diskCachingLayer"
        private const val TAG = "DataCachingService"
    }
}
