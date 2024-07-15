package com.kscopeinc.sms

data class SmsRequest(
    val sms: Sms,
    val smsCredentials: SmsCredentials
)
