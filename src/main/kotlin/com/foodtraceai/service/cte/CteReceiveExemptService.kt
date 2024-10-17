// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.cte

import com.foodtraceai.model.cte.CteReceiveExempt
import com.foodtraceai.repository.cte.CteReceiveExemptRepository
import com.foodtraceai.service.BaseService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CteReceiveExemptService(
    private val cteReceiveExemptRepository: CteReceiveExemptRepository
) : BaseService<CteReceiveExempt>(cteReceiveExemptRepository, "CteReceiveExempt") {

    fun findAllByOptionalParams(
        tlcVal: String? = null,
        locationId: Long? = null,
        ipsLocationId: Long? = null,
        dayFrom: LocalDate? = null,
        dayTo: LocalDate? = null,
    ): List<CteReceiveExempt> {
        return cteReceiveExemptRepository.findAllByOptionalParams(
            tlcVal,
            locationId,
            ipsLocationId,
            dayFrom,
            dayTo,
        )
    }
}
