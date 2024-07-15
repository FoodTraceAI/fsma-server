// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.cte

import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.repository.cte.CteReceiveRepository
import com.foodtraceai.service.BaseService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CteReceiveService(
    private val cteReceiveRepository: CteReceiveRepository
) : BaseService<CteReceive>(cteReceiveRepository, "CteReceive") {

    fun findAllByOptionalParams(
        tlcVal: String? = null,
        locationId: Long? = null,
        ipsLocationId: Long? = null,
        dayFrom: LocalDate? = null,
        dayTo: LocalDate? = null,
    ): List<CteReceive> {
        return cteReceiveRepository.findAllByOptionalParams(
            tlcVal,
            locationId,
            ipsLocationId,
            dayFrom,
            dayTo,
        )
    }
}
