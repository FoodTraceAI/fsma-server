// ----------------------------------------------------------------------------
// Copyright Kaleidoscope, Inc. or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
// Description:
//  MimeAttachments contains a list of MimeAttachment instances which are the
//  Images, documents, or other files that will be included in the email.
// ----------------------------------------------------------------------------
package com.foodtraceai.email

enum class MultipartType(val subtype: String) {
    Mixed("mixed"), // "multipart/mixed" (no HTML?)
    Alternative("alternative"), // "multipart/alternative" (HTML, Images, no Text?)
    Related("related"), // "multipart/related" (HTML, Text, no Images?)
    Report("report") // "multipart/report"
}

// -- holds a single Mime attachment
// -    name
// -    Datastore:
// -        datastoreId
// -    AwsS3/File:
// -        name
// -        bucketName
// -        keyName
// -    Fully specified:
// -        name
// -        mediaType
// -        dataBytes
class MimeAttachment(
    val datastoreId: Long? = null,
    val name: String? = null,
    val mediaType: String? = null,
    val dataBytes: ByteArray? = null,
)

// -- holds a list of Mime attachments
class MimeAttachments(
    val multipartType: MultipartType,
    val attachments: List<MimeAttachment>
) {

    class Builder {

        var multipartType: MultipartType = MultipartType.Mixed
        val attachments = mutableListOf<MimeAttachment>()

        // ----------------------------

        fun setMultipartType(mpType: MultipartType) = apply {
            multipartType = mpType
        }

        fun setAttachments(mAttList: List<MimeAttachment>) = apply {
            addAttachments(mAttList)
        }

        fun addAttachments(mAttList: List<MimeAttachment>) = apply {
            mAttList.forEach { addAttachment(it) }
        }

        fun addAttachment(att: MimeAttachment) = apply {
            // -- adds attachment to end of list
            if (att.dataBytes?.isNotEmpty() == true) {
                attachments.add(att)
            }
        }

        fun addFirstAttachment(att: MimeAttachment) = apply {
            // -- adds attachment to beginning of list
            if (att.dataBytes?.isNotEmpty() == true) {
                attachments.add(0, att)
            }
        }

        // ----------------------------

        fun build(): MimeAttachments {
            return MimeAttachments(multipartType, attachments)
        }

        //
    }

    //
}
