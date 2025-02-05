// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.foodtraceai.email.SmtpCredentials
import com.foodtraceai.email.SmtpCredentialsConverter
import com.foodtraceai.util.ResellerType
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

    @ManyToOne @JoinColumn
    val mainContact: Contact,

    @ManyToOne @JoinColumn
    val billingContact: Contact? = null,

//    @OneToOne(cascade = [CascadeType.ALL])
    @OneToOne  //(cascade = [CascadeType.ALL])
    @JoinColumn
    val billingAddress: Address? = null,

    val resellerType: ResellerType,

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
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseModel<Reseller>() {
    val hasParent: Boolean
        get() = parentReseller != null
}

data class ResellerRequestDto(
    @JsonProperty("address")
    val addressResponseDto: AddressResponseDto,
    val accountRep: String?,
    val businessName: String,
    val mainContactId: Long,
    val billingContactId: Long? = null,
    @JsonProperty("billingAddress")
    val billingAddressDto: AddressResponseDto?,
    val resellerType: ResellerType,
    val password: String? = null,
    val smtpCredentials: SmtpCredentials? = null,
    val smsCredentials: SmsCredentials? = null,
    val subdomain: String? = null,
    val domain: String? = null,
    val parentResellerId: Long? = null,
    val isEnabled: Boolean = true,
)

data class ResellerResponseDto(
    override var id: Long,
    @JsonProperty("address")
    val addressResponseDto: AddressResponseDto,
    val accountRep: String?,
    val businessName: String,
    val mainContactId: Long,
    val billingContactId: Long? = null,
    @JsonProperty("billingAddress")
    val billingAddressDto: AddressResponseDto?,
    val resellerType: ResellerType,
    val password: String? = null,
    val smtpCredentials: SmtpCredentials? = null,
    val smsCredentials: SmsCredentials? = null,
    val subdomain: String? = null,
    val domain: String? = null,
    val parentResellerId: Long? = null,
    val isEnabled: Boolean = true,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<ResellerResponseDto>()

fun Reseller.toResellerResponseDto() = ResellerResponseDto(
    id = id,
    addressResponseDto = address.toAddressResponseDto(),
    accountRep = accountRep,
    businessName = businessName,
    mainContactId = mainContact.id,
    billingContactId = billingContact?.id,
    billingAddressDto = billingAddress?.toAddressResponseDto(),
    resellerType = resellerType,
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
    authUsername = authUsername,
)

fun ResellerRequestDto.toReseller(
    id: Long,
    mainContact: Contact,
    billingContact: Contact?,
): Reseller = Reseller(
    id = id,
    address = addressResponseDto.toAddress(),
    accountRep = accountRep,
    businessName = businessName,
    mainContact = mainContact,
    billingContact = billingContact,
    billingAddress = billingAddressDto?.toAddress(),
    resellerType = resellerType,
    smtpCredentials = smtpCredentials,
    smsCredentials = smsCredentials,
    subdomain = subdomain,
    domain = domain,
    isEnabled = isEnabled,
)
