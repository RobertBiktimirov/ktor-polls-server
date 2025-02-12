package com.polls_example.legacy

import com.polls_example.EmailConfig
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail

private const val CONFIRMATION_CODE = "Код подтверждения"

class EmailService(
    private val emailConfig: EmailConfig,
) {

    fun setConfirmEmailMessage(userEmail: String, code: Int): Boolean {
        return try {
            SimpleEmail().apply {
                hostName = emailConfig.hostName
                setSmtpPort(emailConfig.port)
                setAuthenticator(DefaultAuthenticator(emailConfig.address, emailConfig.password))
                isSSLOnConnect = true
                setFrom(emailConfig.from)
                subject = CONFIRMATION_CODE
                setMsg("Ваш код: $code")
                addTo(userEmail)
            }.also { it.send() }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}