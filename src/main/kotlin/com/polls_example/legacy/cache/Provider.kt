package com.polls_example.legacy.cache

import kotlinx.coroutines.sync.Mutex
import kotlin.time.Duration

class SimpleRedisCacheProvider(config: Config) : SimpleCacheProvider(config) {

    private val cache = mutableMapOf<String, SimpleMemoryCacheObject>()

    private val mutex = Mutex()

    override suspend fun getCache(key: String): Any? {
        mutex.lock()

        val `object` = cache[key]

        mutex.unlock()
        return if (`object` == null || `object`.isExpired) null else `object`.content
    }

    override suspend fun setCache(key: String, content: Any, invalidateAt: Duration?) {
        mutex.lock()
        cache[key] = SimpleMemoryCacheObject(content, invalidateAt ?: this.invalidateAt)
        mutex.unlock()
    }

    data class Config (
        var host: String = "localhost",
        var port: Int = 6379,
        var ssl: Boolean = false
    ) : SimpleCacheProvider.Config()
}