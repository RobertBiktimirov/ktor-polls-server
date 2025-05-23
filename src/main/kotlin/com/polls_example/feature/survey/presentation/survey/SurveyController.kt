package com.polls_example.feature.survey.presentation.survey

import com.polls_example.feature.login.data.repository.UserRepository
import com.polls_example.feature.profile.presentation.grouping.dto.UserInGroupDto
import com.polls_example.feature.survey.data.repository.SurveyRepository
import com.polls_example.feature.survey.domain.models.*
import com.polls_example.feature.survey.presentation.survey.dto.*

class SurveyController(
    private val surveyRepository: SurveyRepository,
    private val userRepository: UserRepository,
) {

    suspend fun getSurveysInvitation(userId: Int): List<SurveysModel> {
        return surveyRepository.getSurveysInvitation(userId)
    }

    suspend fun getSurveysUser(userId: Int): List<SurveysModel> {
        return surveyRepository.getSurveysUser(userId)
    }

    suspend fun getSurveyUserResponsesInfo(userId: Int, surveyId: Int): SurveyResponsesInfoModel {
        val surveyInfoModel = surveyRepository.getSurveyInfo(userId, surveyId)
        val responses = surveyRepository.getSurveysResponses(surveyId)

        return SurveyResponsesInfoModel(
            model = surveyInfoModel,
            responses = responses
        )
    }

    suspend fun getSurveyReceivedInfo(userId: Int, surveyId: Int): SurveyReceivedInfoModel {
        val surveyInfoModel = surveyRepository.getSurveyInfo(userId, surveyId, false)
        val questions = surveyRepository.getSurveyQuestions(surveyId)
        return SurveyReceivedInfoModel(surveyInfoModel, questions)
    }

    suspend fun getSurveysByQuery(userId: Int, query: String): List<SurveysModel> =
        surveyRepository.getUserAllSurveysByQuery(userId, query)

    suspend fun saveSurvey(userId: Int, survey: SurveyRequestDto): SurveyInfoModel {
        val requestModel = SurveyUpdate.SurveySaveModel(
            title = survey.title,
            description = survey.description,
            image = survey.image,
            questions = survey.questions
        )
        return surveyRepository.saveSurvey(userId, requestModel)
    }

    suspend fun inviteUsersInSurvey(requestDto: SurveyInvitationsRequestDto) {
        surveyRepository.inviteUsersToSurvey(
            usersId = requestDto.listUserId.toIntArray(),
            surveyId = requestDto.surveyId,
        )
    }

    suspend fun deleteInviteUserInSurvey(requestDto: SurveyInvitationDeleteRequestDto) {
        surveyRepository.deleteInviteUserToSurvey(requestDto.userId, requestDto.surveyId)
    }

    suspend fun getInvitationsUsersInSurvey(surveyId: Int): List<UserInGroupDto> {
        val userIds = surveyRepository.getInvitationsIdsSurvey(surveyId = surveyId)
        val usersModel = userIds.map { userRepository.userById(it) }
        return usersModel
            .filterNotNull()
            .map {
                UserInGroupDto(
                    id = it.id,
                    email = it.email,
                    name = it.name,
                    imageUrl = it.image
                )
            }
    }

    suspend fun activateSurvey(surveyId: Int) {
        surveyRepository.activateSurvey(surveyId = surveyId)
    }

    suspend fun blockSurvey(surveyId: Int) {
        surveyRepository.blockSurvey(surveyId = surveyId)
    }

    suspend fun deleteSurvey(surveyId: Int) {
        surveyRepository.deleteSurvey(surveyId = surveyId)
    }

    suspend fun updateSurveyInfo(requestDto: SurveyUpdateInfoRequestDto): SurveyInfoModel {
        val infoModel = surveyRepository.updateSurvey(
            requestDto.id,
            SurveyUpdate.SurveyUpdateModel(
                title = requestDto.title,
                description = requestDto.description,
                image = requestDto.image
            )
        )
        return infoModel
    }

    suspend fun passSurvey(userId: Int, dto: PassSurveyDto) {
        surveyRepository.passSurvey(userId, dto)
    }
}