// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.config

import org.springframework.boot.context.properties.ConfigurationProperties

//TODO: set up Dev, QA, Prod environments
@ConfigurationProperties("configuration")
data class FsmaProperties(val title: String, val banner: Banner) {
    data class Banner(val title: String? = null, val content: String)
}
