package com.polls_example.legacy.cache

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

abstract class SimpleCacheProvider(config: Config) {

    val invalidateAt = config.invalidateAt

    abstract suspend fun getCache(key: String): Any?

    abstract suspend fun setCache(key: String, content: Any, invalidateAt: Duration? = null)

    open class Config protected constructor() {

        var invalidateAt: Duration = 1.minutes
    }
}