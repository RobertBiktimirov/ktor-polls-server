package com.polls_example.feature.survey.domain.exception

/**
 * Попытка получить информацию о прохождениях опроса не его создателем
 */
class SurveyForbiddenException : IllegalArgumentException()

/**
 * Попытка удалить приглашение юзера в опрос, когда юзер уже его прошел
 */
class SurveyDeleteInvitationException: RuntimeException()

/**
 *  Общая ошибка с информацией
 */
open class SurveyExceptions(override val message: String): IllegalArgumentException(message)