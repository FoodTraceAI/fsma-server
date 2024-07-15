// ----------------------------------------------------------------------------
// Copyright Kaleidoscope, Inc. or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
// Example Usage:
//  val email = Email.Builder()
//      .setTo(listOf("user1@example.com","user2@example.com"))
//      .setSubject("This is the subject")
//      .setBodyText("This is the email body text")
//      .build()
//  email.send(smtpProps)
// ----------------------------------------------------------------------------
package com.foodtraceai.email

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.MediaType

//  {
//      "to": [ "user1@example.com", "user2@example.com" ],
//      "subject": "Email subject",
//      "bodyText": "This is a message",
//      "attachments": {
//          "multipartType": "mixed",
//          "attachments": [
//              {
//                  "name": "image.png",
//                  "type": "image/png",
//                  "dataBytes": "BASE64_BYTES"
//              },
//              {
//                  "datastoreId": 1
//              },
//              ...
//          ]
//      }
//  }

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Email(
    val to: List<String>,
    val cc: List<String>? = null,
    val bcc: List<String>? = null,
    val subject: String,
    val bodyText: String,
    val attachments: MimeAttachments? = null,
    val smtpCredentials: SmtpCredentials? = null
) {

    // fun sendQueue(props: SmtpProperties, retryCount: Int? = null): String =
    //    // -- Note: 'retryCount' does not include first attempt
    //    SmtpSendEmail(props).sendEmailQueue(this, retryCount)

    // ------------------------------------------------------------------------

    class Builder {

        var to: List<String> = listOf()
        var cc: List<String>? = null
        var bcc: List<String>? = null
        var subject: String = ""
        var bodyText: String = ""
        var mpType: MultipartType? = null
        var mAttList = mutableListOf<MimeAttachment>()
        var smtpCred: SmtpCredentials? = null

        // ----------------------------
        fun setTo(TO: List<String>) = apply { to = TO }
        fun setTo(TO: String) = apply { setTo(TO.split(",").toList()) }

        // ----------------------------
        fun setCc(CC: List<String>) = apply { cc = CC }
        fun setCc(CC: String) = apply { setCc(CC.split(",").toList()) }

        // ----------------------------
        fun setBcc(BCC: List<String>) = apply { bcc = BCC }
        fun setBcc(BCC: String) = apply { setBcc(BCC.split(",").toList()) }

        // ----------------------------
        fun setSubject(subj: String) = apply { subject = subj }

        // ----------------------------
        fun setBodyText(text: String) = apply {
            if (isHtmlText(text)) {
                val htmlText = MimeAttachment(
                    name = "email.html",
                    mediaType = MediaType.TEXT_HTML_VALUE,
                    dataBytes = text.toByteArray()
                )
                addAttachment(htmlText, 0)
                bodyText = ""
            } else {
                bodyText = text
            }
        }

        private fun isHtmlText(text: String): Boolean {
            var p: Int = 0
            while (p < text.length && text[p].isWhitespace()) p++
            return (text.startsWith("<html", p, true) || text.startsWith("<!doctype html", p, true))
        }

        // ----------------------------

        fun setMultipartType(mpt: MultipartType) = apply { mpType = mpt }

        fun addAttachments(attachs: MimeAttachments) = apply {
            setMultipartType(attachs.multipartType)
            mAttList.forEach { addAttachment(it) }
        }

        fun addAttachment(attach: MimeAttachment, ndx: Int? = null) = apply {
            if (ndx == null) mAttList.add(attach) else mAttList.add(ndx, attach)
        }

        // ----------------------------

        fun setSmtpCredentials(cred: SmtpCredentials) = apply { smtpCred = cred }

        // ----------------------------

        fun build(): Email {
            val mAtt: MimeAttachments? = if (mAttList.isNotEmpty())
                MimeAttachments(mpType ?: MultipartType.Mixed, mAttList)
            else
                null
            return Email(to, cc, bcc, subject, bodyText, mAtt, smtpCred)
        }

        //
    }

    //
}
