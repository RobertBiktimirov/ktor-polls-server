package com.polls_example.surveys

import com.polls_example.feature.survey.data.repository.SurveyRepository
import com.polls_example.feature.survey.domain.models.SurveyInfoModel
import com.polls_example.feature.survey.domain.models.SurveyResponsesInfoModel
import com.polls_example.feature.survey.domain.models.SurveysModel
import com.polls_example.feature.survey.presentation.survey.SurveyController
import com.polls_example.feature.survey.presentation.survey.dto.SurveyInvitationsRequestDto
import com.polls_example.feature.survey.presentation.survey.dto.SurveyRequestDto
import com.polls_example.legacy.timeInMillis
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class SurveyControllerTest {

    private val surveyRepository = mockk<SurveyRepository>()
    private val surveyController = SurveyController(surveyRepository)

    @Test
    fun `test get surveys invitation returns list of surveys`() = runBlocking {
        val userId = 1
        val expectedSurveys =
            listOf(SurveysModel(id = 1, title = "Survey 1", isCompleted = false, image = null, isActive = true))

        // Настройка мока
        coEvery { surveyRepository.getSurveysInvitation(userId) } returns expectedSurveys

        // Вызов метода
        val result = surveyController.getSurveysInvitation(userId)

        // Проверка результата
        assertEquals(expectedSurveys, result)
        coVerify { surveyRepository.getSurveysInvitation(userId) }
    }

    @Test
    fun `test get surveys user returns list of surveys`() = runBlocking {
        val userId = 1
        val expectedSurveys =
            listOf(SurveysModel(id = 1, title = "Survey 2", isCompleted = true, image = null, isActive = false))

        coEvery { surveyRepository.getSurveysUser(userId) } returns expectedSurveys

        val result = surveyController.getSurveysUser(userId)

        assertEquals(expectedSurveys, result)
        coVerify { surveyRepository.getSurveysUser(userId) }
    }

    @Test
    fun `test get survey user responses info`() = runBlocking {
        val userId = 1
        val surveyId = 2
        val timeNow = LocalDateTime.now().timeInMillis ?: 0
        val expectedSurveyInfo = SurveyResponsesInfoModel(
            SurveyInfoModel(
                id = surveyId,
                "New Survey",
                "Description",
                null,
                true,
                null,
                timeNow,
            ),
            responses = emptyList()
        )

        coEvery {
            surveyRepository.getSurveyInfo(
                userId,
                surveyId
            )
        } returns SurveyInfoModel(
            id = surveyId,
            "New Survey",
            "Description",
            null,
            true,
            null,
            timeNow,
        )
        coEvery { surveyRepository.getSurveysResponses(surveyId) } returns listOf()

        val result = surveyController.getSurveyUserResponsesInfo(userId, surveyId)

        assertEquals(expectedSurveyInfo, result)
        coVerify {
            surveyRepository.getSurveyInfo(userId, surveyId)
            surveyRepository.getSurveysResponses(surveyId)
        }
    }

    @Test
    fun `test save survey`() = runBlocking {
        val userId = 1
        val surveyRequestDto = SurveyRequestDto(0, "New Survey", "Description", null, emptyList())
        val expectedSurveyInfo = SurveyInfoModel(
            0,
            "New Survey",
            "Description",
            null,
            true,
            null,
            LocalDateTime.now().timeInMillis ?: 0,
        )

        coEvery { surveyRepository.saveSurvey(userId, any()) } returns expectedSurveyInfo

        val result = surveyController.saveSurvey(userId, surveyRequestDto)

        assertEquals(expectedSurveyInfo, result)
        coVerify { surveyRepository.saveSurvey(userId, any()) }
    }

    @Test
    fun `test invite users in survey`() = runBlocking {
        val requestDto = SurveyInvitationsRequestDto(listUserId = listOf(1, 2, 3), surveyId = 1)

        coEvery { surveyRepository.inviteUsersToSurvey(1, 2, 3, surveyId = 1) } returns Unit

        surveyController.inviteUsersInSurvey(requestDto)

        coVerify { surveyRepository.inviteUsersToSurvey(1, 2, 3, surveyId = 1) }
    }
}