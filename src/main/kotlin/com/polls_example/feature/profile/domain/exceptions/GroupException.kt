package com.polls_example.feature.profile.domain.exceptions

class GroupException403: Exception()

class GroupException404: Exception()

class GroupSampleException(message: String): Exception(message)