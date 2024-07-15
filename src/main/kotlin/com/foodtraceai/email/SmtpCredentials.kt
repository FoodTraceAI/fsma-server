// ----------------------------------------------------------------------------
// Copyright Kaleidoscope, Inc. or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
// Description:
//  This contains everything needed to authenticate with a specific SMTP provider
//  (will eventually need additional fields to support OAuth2)
// ----------------------------------------------------------------------------
package com.foodtraceai.email

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@JsonInclude(JsonInclude.Include.NON_NULL)
class SmtpCredentials(
    // -- SMTP service provider required
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val from: String,
    // -- (optional) authentication
    @Enumerated(EnumType.STRING)
    val tls: TLSMode? = null,
    @Enumerated(EnumType.STRING)
    val ssl: SSLMode? = null,
    @Enumerated(EnumType.STRING)
    val authMethod: AthenticationMethod? = null,
    // -- (optional) email connection/session timeout
    val timeoutMS: Long? = null,
    // -- (optional) true to not fail if one destination address is bad
    val sendPartial: Boolean? = null,
    // -- (optional) additional To/Cc/Bcc addresses for every email sent
    val to: List<String>? = null,
    val cc: List<String>? = null,
    val bcc: List<String>? = null,
    // -- (optional) set to number of retry-on-error attempts required
    val retryCount: Int? = null, // does not include initial attempt
    // -- (optional) set true to enable displaying additional debug information
    val debug: Boolean? = null
    // -- (optional) additional fields below for future OAuth2 token support
) {

    enum class AthenticationMethod {
        None,
        NormalPassword,
        OAuth2, // (pending) not yet fully supported
        EncryptedPassword, // not yet supported
        Kerberos, // (/GSSAPI) not yet supported
        NTLM // (Microsoft) not yet supported
    }

    enum class TLSMode {
        Disabled,
        Enabled,
        Only
    }

    enum class SSLMode {
        Disabled,
        Enabled,
        Only
    }

    @JsonIgnore
    fun isBlank(): Boolean = host == "" && port == 0 && user == "" && from == ""

    @JsonIgnore
    fun isNotBlank(): Boolean = !isBlank()

    companion object {
        fun blankCredentials() = SmtpCredentials(
            host = "",
            port = 0,
            user = "",
            password = "",
            from = ""
        )
    }

    //
}

@Converter(autoApply = true)
class SmtpCredentialsConverter : AttributeConverter<SmtpCredentials, String> {
    override fun convertToDatabaseColumn(smtpCred: SmtpCredentials?): String {
        if (smtpCred != null) {
            try {
                return jacksonObjectMapper().writeValueAsString(smtpCred)
            } catch (th: Throwable) {
                // -- catch any errors to prevent not being able to save record
                println("*** Error converting Object to JSON String: ")
            }
        }
        return ""
    }

    override fun convertToEntityAttribute(smtpCred: String?): SmtpCredentials? {
        if (!smtpCred.isNullOrEmpty()) {
            try {
                val typeRef = object : TypeReference<SmtpCredentials>() {}
                return jacksonObjectMapper().readValue(smtpCred, typeRef)
            } catch (th: Throwable) {
                // -- catch any errors to prevent not being able to load record
                println("*** Error converting JSON String to Object: $smtpCred")
            }
        }
        return null
    }
}
