package com.polls_example.legacy

import java.time.LocalDateTime
import java.time.ZoneOffset

val LocalDateTime.timeInMillis: Long?
    get() = toInstant(ZoneOffset.UTC)?.toEpochMilli()