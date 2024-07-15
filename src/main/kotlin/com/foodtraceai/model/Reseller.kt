// ----------------------------------------------------------------------------
// Copyright Kaleidoscope, Inc. or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.foodtraceai.email.SmtpCredentials
import com.foodtraceai.email.SmtpCredentialsConverter
import com.kscopeinc.sms.SmsCredentials
import com.kscopeinc.sms.SmsCredentialsConverter
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(
    indexes = [
        Index(columnList = "subdomain")
    ]
)
data class Reseller(
    @Id @GeneratedValue
    override val id: Long = 0,

    //    @OneToOne(cascade = [CascadeType.ALL])
    @OneToOne  //(cascade = [CascadeType.ALL])
    @JoinColumn
    val address: Address,

    val accountRep: String? = null,
    val businessName: String,

    val firstName: String? = null,
    val lastName: String? = null,

    val mainContactName: String? = null,// "Operational Contact Name"
    val mainContactPhone: String? = null, // "Phone Number"
    val mainContactEmail: String? = null, // "Email Address"

    val billingContactName: String? = null,
    val billingContactPhone: String? = null,
    val billingContactEmail: String? = null,

//    @OneToOne(cascade = [CascadeType.ALL])
    @OneToOne  //(cascade = [CascadeType.ALL])
    @JoinColumn
    val billingAddress: Address? = null,

    // -- [TODO: move these to a separate 'Properties' entity]
    @Convert(converter = SmtpCredentialsConverter::class)
    val smtpCredentials: SmtpCredentials? = null,

    @Convert(converter = SmsCredentialsConverter::class)
    val smsCredentials: SmsCredentials? = null,

    val subdomain: String? = null,
    val domain: String? = null,
    val accessKey: Long? = null,
    val isEnabled: Boolean = true,

    @ManyToOne
    @JoinColumn
    val parentReseller: Reseller? = null,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : BaseModel<Reseller>() {
    val hasParent: Boolean
        get() = parentReseller != null
}

data class ResellerDto(
    val id: Long = 0,
    @JsonProperty("address")
    val addressDto: AddressDto,
    val accountRep: String?,
    val businessName: String,
    val firstName: String?,
    val lastName: String?,
    val mainContactName: String?,
    val mainContactPhone: String?,
    val mainContactEmail: String?,
    val password: String? = null,
    val billingContactName: String?,
    val billingContactPhone: String?,
    val billingContactEmail: String?,
    @JsonProperty("billingAddress")
    val billingAddressDto: AddressDto?,
    val smtpCredentials: SmtpCredentials? = null,
    val smsCredentials: SmsCredentials? = null,
    val subdomain: String? = null,
    val domain: String? = null,
    val parentResellerId: Long? = null,
    val isEnabled: Boolean = true,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateModified: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime? = null,
)

fun Reseller.toResellerDto() = ResellerDto(
    id = id,
    addressDto = address.toAddressDto(),
    accountRep = accountRep,
    businessName = businessName,
    firstName = firstName,
    lastName = lastName,
    mainContactName = mainContactName,
    mainContactPhone = mainContactPhone,
    mainContactEmail = mainContactEmail,
    billingContactName = billingContactName,
    billingContactPhone = billingContactPhone,
    billingContactEmail = billingContactEmail,
    billingAddressDto = billingAddress?.toAddressDto(),
    smtpCredentials = smtpCredentials,
    smsCredentials = smsCredentials,
    subdomain = subdomain,
    domain = domain,
    isEnabled = isEnabled,
    parentResellerId = parentReseller?.id,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun ResellerDto.toReseller(resellerId: Long): Reseller {
    val reseller = Reseller(
        id = id,
        address = addressDto.toAddress(id),
        accountRep = accountRep,
        businessName = businessName,
        firstName = firstName,
        lastName = lastName,
        mainContactName = mainContactName,
        mainContactPhone = mainContactPhone,
        mainContactEmail = mainContactEmail,
        billingContactName = billingContactName,
        billingContactPhone = billingContactPhone,
        billingContactEmail = billingContactEmail,
        billingAddress = billingAddressDto?.toAddress(id),
        smtpCredentials = smtpCredentials,
        smsCredentials = smsCredentials,
        subdomain = subdomain,
        domain = domain,
        isEnabled = isEnabled,
        dateCreated = dateCreated,
        dateModified = dateModified,
        isDeleted = isDeleted,
        dateDeleted = dateDeleted,
    )

    return reseller
}
