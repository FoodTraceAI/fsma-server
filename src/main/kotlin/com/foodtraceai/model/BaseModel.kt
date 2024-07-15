// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.security.core.context.SecurityContextHolder
import java.time.OffsetDateTime

//@MappedSuperclass
//@EntityListeners(AuditingEntityListener::class)
abstract class BaseAuditModel {

    @CreatedDate
    @Column(updatable = false)
    var dateCreated: OffsetDateTime? = null

    @CreatedBy
    @ManyToOne
    @JoinColumn
    var createdBy: FsmaUser? = null

    @LastModifiedDate
    var dateModified: OffsetDateTime? = null

    @LastModifiedBy
    @ManyToOne
    @JoinColumn
    var lastModifiedBy: FsmaUser? = null

    var dateDeleted: OffsetDateTime? = null
}

@MappedSuperclass
abstract class BaseIdModel : BaseAuditModel() {
    @Id
    @GeneratedValue
    var id: Long = 0
}

/**
 *** Base superclass of DB Entity objects
 **/
open class BaseModel<T> {
    open val id: Long = 0L

    open var dateCreated: OffsetDateTime = OffsetDateTime.now()
    open var dateModified: OffsetDateTime = OffsetDateTime.now()
    open var isDeleted: Boolean = false
    open var dateDeleted: OffsetDateTime? = null
    open var authUsername: String? = null

    open fun preSoftDelete() {
        dateModified = OffsetDateTime.now()
        dateDeleted = dateModified
        authUsername = SecurityContextHolder.getContext()?.authentication?.name ?: ""
    }
}

/**
 *** Base superclass of Reseller Entity objects
 **/
abstract class BaseResellerModel<T> : BaseModel<T>() {
    abstract val reseller: Reseller
}

abstract class BaseFoodBusModel<T> : BaseModel<T>() {
    abstract val foodBus: FoodBus
    val reseller: Reseller
        get() = foodBus.reseller
    val resellerId: Long
        get() = reseller.id
}

abstract class BaseLocationModel<T> : BaseModel<T>() {
    abstract val location: Location
    val foodBus: FoodBus
        get() = location.foodBus
    val foodBusId: Long
        get() = foodBus.id
    val reseller: Reseller
        get() = foodBus.reseller
    val resellerId: Long
        get() = reseller.id
}

///**
// *** Base superclass of Client Entity objects
// **/
//abstract class BaseClientModel<T> : BaseModel<T>() {
//    abstract val client: Client
//    val reseller: Reseller
//        get() = client.reseller
//    val resellerId: Long
//        get() = reseller.id
//}
//
///**
// *** Base superclass of Customer Entity objects
// **/
//abstract class BaseCustomerModel<T> : BaseModel<T>() {
//    abstract val customer: Customer
//    val client: Client
//        get() = customer.client
//    val clientId: Long
//        get() = client.id
//    val reseller: Reseller
//        get() = client.reseller
//    val resellerId: Long
//        get() = reseller.id
//}
//
///**
// *** Base superclass of SerLoc Entity objects
// **/
//abstract class BaseServLocModel<T> : BaseModel<T>() {
//    abstract val servLoc: ServLoc
//    val customer: Customer
//        get() = servLoc.customer
//    val customerId: Long
//        get() = customer.id
//    val client: Client
//        get() = customer.client
//    val clientId: Long
//        get() = client.id
//    val reseller: Reseller
//        get() = client.reseller
//    val resellerId: Long
//        get() = reseller.id
//}
//
///**
// *** Base superclass of WorkRequest Entity objects
// **/
//abstract class BaseWorkRequestModel<T> : BaseModel<T>() {
//    abstract val workRequest: WorkRequest
//    val servLoc: ServLoc
//        get() = workRequest.servLoc
//    val customer: Customer
//        get() = servLoc.customer
//    val customerId: Long
//        get() = customer.id
//    val client: Client
//        get() = customer.client
//    val clientId: Long
//        get() = client.id
//    val reseller: Reseller
//        get() = client.reseller
//    val resellerId: Long
//        get() = reseller.id
//}