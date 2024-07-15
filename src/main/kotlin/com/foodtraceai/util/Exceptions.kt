// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.util

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

// ----------------------------------------------------------------------------
// -- Possibly applicable HttpStatus codes:
// -    HttpStatus.OK                     [200 - success]
// -    HttpStatus.CREATED                [201 - success]
// -    HttpStatus.ACCEPTED               [202 - success]
// -    HttpStatus.BAD_REQUEST            [400]
// -    HttpStatus.UNAUTHORIZED           [401]
// -    HttpStatus.FORBIDDEN              [403]
// -    HttpStatus.NOT_FOUND              [404]
// -    HttpStatus.METHOD_NOT_ALLOWED     [405]
// -    HttpStatus.NOT_ACCEPTABLE         [406]
// -    HttpStatus.CONFLICT               [409]
// -    HttpStatus.GONE                   [410]
// -    HttpStatus.UNSUPPORTED_MEDIA_TYPE [415] for attachments
// -    HttpStatus.I_AM_A_TEAPOT          [418]
// -    HttpStatus.LOCKED                 [423]
// -    HttpStatus.INTERNAL_SERVER_ERROR  [500]
// -    HttpStatus.NOT_IMPLEMENTED        [501]
// -    HttpStatus.INSUFFICIENT_STORAGE   [507]
// -    HttpStatus.[EXPIRED_TOKEN]        [498] not defined in HttpStatus
// -    HttpStatus.[TOKEN_REQUIRED]       [499] not defined in HttpStatus
// -    HttpStatus.[LOGIN_TIMEOUT]        [440] not defined in HttpStatus
// ----------------------------------------------------------------------------

// ------------------------------------

class EntityNotFoundException : EntityException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.NOT_FOUND, th)
    constructor(m: String) : this(m, null)
}

class EntityExistsException : EntityException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.FORBIDDEN, th)
    constructor(m: String) : this(m, null)
}

open class EntityException : FsaException {
    constructor(m: String, h: HttpStatus, th: Throwable?) : super(m, h, th)
    constructor(m: String, th: Throwable?) : this(m, HttpStatus.BAD_REQUEST, th)
    constructor(m: String) : this(m, null)
}

open class ExceedsTechnicianLicenseException : FsaException {
    constructor(m: String, h: HttpStatus, th: Throwable?) : super(m, h, th)
    constructor(m: String, th: Throwable?) : this(m, HttpStatus.BAD_REQUEST, th)
    constructor(m: String) : this(m, null)
}

open class DuplicateEquipmentIdentifierException : FsaException {
    constructor(m: String, h: HttpStatus, th: Throwable?) : super(m, h, th)
    constructor(m: String, th: Throwable?) : this(m, HttpStatus.BAD_REQUEST, th)
    constructor(m: String) : this(m, null)
}

// ------------------------------------

open class BadRequestException : FsaException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.BAD_REQUEST, th)
    constructor(m: String) : this(m, null)
}

// ------------------------------------

class AuthorizationException : FsaException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.UNAUTHORIZED, th)
    constructor(m: String) : this(m, null)
}

// --

class UnauthorizedRequestException : FsaException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.FORBIDDEN, th)
    constructor(m: String) : this(m, null)
}

// --

class DataFileNotFoundException : FsaException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.NOT_FOUND, th)
    constructor(m: String) : this(m, null)
}

class DataBytesInvalidException : FsaException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.BAD_REQUEST, th)
    constructor(m: String) : this(m, null)
}

// --

class InternalException : FsaException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.INTERNAL_SERVER_ERROR, th)
    constructor(m: String) : this(m, null)
}

// ------------------------------------

// -- https://www.rfc-editor.org/rfc/rfc6750
// -    ... in response to a protected resource request with an
// -    authentication attempt using an expired access token:
// -      HTTP/1.1 401 Unauthorized
// -      WWW-Authenticate: Bearer realm="example",
// -                        error="invalid_token",
// -                        error_description="The access token expired"
class ExpiredTokenException : FsaException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.UNAUTHORIZED, th) {
        // -- https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Expose-Headers
        // addHttpHeader(HttpHeaders.VARY, HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS)
        addHttpHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.WWW_AUTHENTICATE)
        addHttpHeader(HttpHeaders.WWW_AUTHENTICATE, "error=\"invalid_token\"")
    }

    constructor(m: String) : this(m, null)
}

class InvalidTokenException : FsaException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.BAD_REQUEST, th)
    constructor(m: String) : this(m, null)
}

// ------------------------------------

open class FsaException(
    val msg_: String,
    val httpCode: HttpStatus,
    val th: Throwable?
) : Exception(msg_, th) {
    var httpHdrs: HttpHeaders = HttpHeaders().apply {
        add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
    }

    fun setHttpHeaders(hh: HttpHeaders): FsaException = apply { httpHdrs = hh }
    fun addHttpHeader(h: String, v: String): FsaException = apply { httpHdrs.add(h, v) }
    fun getHttpHeaders(): HttpHeaders = httpHdrs
    fun getHttpStatus(): HttpStatus = httpCode
}

// ------------------------------------

open class ResourceException : FsaException {
    constructor(m: String, th: Throwable?) : super(m, HttpStatus.INTERNAL_SERVER_ERROR, th)
    constructor(m: String) : this(m, null)
}

// ------------------------------------

fun Throwable.getRootCause(): Throwable {
    var c = this
    while (c.cause != null) c = c.cause!!
    return c
}

fun Throwable.getRootMessage(): String? =
    getRootCause().message

fun Throwable.logError(printStackTrace: Boolean = false, msg: String? = null) {
    val c = this::class.qualifiedName // class name from class
    val m = if (msg != null) " [$msg]" else ""
    println("**** $c: ${message}$m")
    if (printStackTrace) printStackTrace()
}

fun Throwable.log(stackTrace: Boolean = false, msg: String? = null) =
    logError(stackTrace, msg)

fun Throwable.log(msg: String) =
    logError(false, msg)
