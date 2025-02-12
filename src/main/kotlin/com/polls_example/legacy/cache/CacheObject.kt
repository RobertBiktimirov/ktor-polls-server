package com.polls_example.legacy.cache

import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

internal data class SimpleMemoryCacheObject(
    val content: Any,
    val duration: Duration = 1.minutes,
    val start: LocalDateTime = LocalDateTime.now()
) {

    val isExpired: Boolean
        get() = LocalDateTime.now().isAfter(start.plusSeconds(duration.inWholeSeconds))
}