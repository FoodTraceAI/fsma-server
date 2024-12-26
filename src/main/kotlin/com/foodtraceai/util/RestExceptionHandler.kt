// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.foodtraceai.logging.LoggerDelegate
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler
import org.springframework.security.web.firewall.RequestRejectedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class RestExceptionHandler {

    private val logger by LoggerDelegate()

    fun getRootCause(e: Throwable): Throwable {
        var c = e
        while (c.cause != null) c = c.cause!!
        return c
    }

    @ExceptionHandler(FsmaException::class)
    fun handleException(ex: FsmaException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = ex.getHttpStatus()
        val httpHdrs: HttpHeaders = ex.getHttpHeaders()
        val message = ex.message ?: "Error"
        val details = null // getRootCause(ex)?.message
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = HttpStatus.METHOD_NOT_ALLOWED
        val httpHdrs: HttpHeaders? = null
        val message = "Http method not supported"
        val details = "'${ex.method}' not supported for specified URL"
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleException(ex: DataIntegrityViolationException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
        val httpHdrs: HttpHeaders? = null
        val message = "Data integrity violation"
        val details = "Duplicate key value violates unique constraint"
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        logger.error(ex.message)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorModel> {
        val rootEx: Throwable = getRootCause(ex)
        when (rootEx) {
            is JsonParseException -> {
                // org.springframework.http.converter.HttpMessageNotReadableException:
                // JSON parse error:
                // Unexpected character ('"' (code 34)): was expecting comma to separate
                // Object entries;
                // nested exception is com.fasterxml.jackson.core.JsonParseException:
                // Unexpected character ('"' (code 34)): was expecting comma to separate
                // Object entries<EOL> at [Source:
                // (org.springframework.util.StreamUtils$NonClosingInputStream);
                // line: 1, column: 187]
                val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
                val httpHdrs: HttpHeaders? = null
                val message = "JSON Parse Error"
                val details = rootEx.message
                val error = ErrorModel(httpStat, httpStat.value(), message, details)
                logger.error(ex.message)
                return ResponseEntity(error, httpHdrs, httpStat)
            }

            is MismatchedInputException -> {
                // org.springframework.http.converter.HttpMessageNotReadableException:
                //    JSON parse error:
                //    Cannot deserialize value of type `java.util.TreeSet<java.lang.String>`
                //    from Object value (token `JsonToken.START_OBJECT`);
                //    nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException:
                //      Cannot deserialize value of type `java.util.TreeSet<java.lang.String>`
                //      from Object value (token `JsonToken.START_OBJECT`)<EOL> at
                //      [Source: (org.springframework.util.StreamUtils$NonClosingInputStream);
                //      line: 1, column: 196]
                //      (through reference chain: com.kscopeinc.model.EmployeeDto["skills"])
                val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
                val httpHdrs: HttpHeaders? = null
                val message = "JSON mismatched input"
                val details = rootEx.message
                val error = ErrorModel(httpStat, httpStat.value(), message, details)
                logger.error(ex.message)
                return ResponseEntity(error, httpHdrs, httpStat)
            }

            else -> {
                val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
                val httpHdrs: HttpHeaders? = null
                val message = "Http message error"
                val details = rootEx.message
                val error = ErrorModel(httpStat, httpStat.value(), message, details)
                logger.error(ex.message)
                return ResponseEntity(error, httpHdrs, httpStat)
            }
        }
    }

    @ExceptionHandler(NullPointerException::class)
    fun handleException(ex: NullPointerException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        val httpHdrs: HttpHeaders? = null
        val message = "Null pointer reference"
        val details = ex.message
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler
    fun handleException(ex: NoSuchMethodError): ResponseEntity<ErrorModel> {
        val rootEx: Throwable = getRootCause(ex)
        val httpStat: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        val httpHdrs: HttpHeaders? = null
        val message = "Method not found"
        val details = rootEx.message
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler
    fun handleException(ex: NumberFormatException): ResponseEntity<ErrorModel> {
        val rootEx: Throwable = getRootCause(ex)
        val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
        val httpHdrs: HttpHeaders? = null
        val message = "Error converting String to number"
        val details = rootEx.message
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        logger.error(ex.message)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleException(ex: MissingRequestHeaderException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
        val httpHdrs: HttpHeaders? = null
        val message = "Required Header not found"
        val details = ex.message
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        logger.error(ex.message)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    // -- unable to convert one data type to another
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
        val httpHdrs: HttpHeaders? = null
        val message = "Failed to convert datatype"
        val details = ex.message
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        logger.error(ex.message)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorModel> {
        val errors = mutableListOf<String>()
        ex.bindingResult.allErrors.forEach { error ->
            errors.add(error.getDefaultMessage() ?: "")
        }
        val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
        val httpHdrs: HttpHeaders? = null
        val message = "Invalid input"
        val details = errors.joinToString(", ")
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        logger.error(ex.message)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler
    fun handleException(ex: BadCredentialsException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = HttpStatus.UNAUTHORIZED
        val httpHdrs: HttpHeaders? = null
        val message = "Invalid username and/or password"
        val details = ex.message
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler
    fun handleException(ex: AccessDeniedException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = HttpStatus.UNAUTHORIZED
        val httpHdrs: HttpHeaders? = null
        val message = ex.message
        val details = "Not allowed to access resource"
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    @ExceptionHandler
    fun handleException(ex: DisabledException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = HttpStatus.FORBIDDEN
        val httpHdrs: HttpHeaders? = null
        val message = ex.message
        val details = "User account is disabled"
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    // -- uncaught exception
    // -  Errors encountered, but not yet explicitly caught
    // -    java.util.concurrent.CancellationException: Channel has been cancelled
    @ExceptionHandler // (Throwable::class)
    fun handleException(ex: Throwable): ResponseEntity<ErrorModel> {
        val rootEx: Throwable = getRootCause(ex)
        val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
        val httpHdrs: HttpHeaders? = null
        val message = "Unexpected Error"
        val details = rootEx.message
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        logger.error(ex.message)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    // ------------------------------------------------------------------------
    // -- These Exceptions do not appear to always be caught
    // -  (These exceptions occur while parsing the incoming URL and parameters)

    // -- Invalid request URL?
    @ExceptionHandler(RequestRejectedException::class)
    fun handleException(ex: RequestRejectedException): ResponseEntity<ErrorModel> {
        val httpStat: HttpStatus = HttpStatus.BAD_REQUEST
        val httpHdrs: HttpHeaders? = null
        val message = "Request Rejected"
        val details = ex.message
        val error = ErrorModel(httpStat, httpStat.value(), message, details)
        logger.error(ex.message)
        return ResponseEntity(error, httpHdrs, httpStat)
    }

    // ------------------------------------------------------------------------

    /* For now leave this as-is
    @Bean
    fun requestRejectedHandler() : RequestRejectedHandler {
        // -- https://stackoverflow.com/questions/51788764/how-to-intercept-a-requestrejectedexception-in-spring
        // -- There is a bug in Springboot that prevents "RequestRejectedException"
        // -  and "MethodArgumentTypeMismatchException"  from being intercepted in the
        // -  above ExceptionHandlers.  This bean is an attempt to at least partially
        // -  intercept this exception in order to remove the long stacktrace, and change
        // -  the status code to BAD_REQUEST
        return _RequestRejectedHandler()
    }
    */
}

open class RequestRejectedHandler : HttpStatusRequestRejectedHandler() {
    override
    fun handle(
        req: HttpServletRequest,
        resp: HttpServletResponse,
        rejEx: RequestRejectedException
    ) {
        resp.sendError(HttpStatus.BAD_REQUEST.value(), rejEx.message)
    }
}

data class ErrorModel(
    val error: HttpStatus,
    val status: Int,
    val message: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val details: String?
)
