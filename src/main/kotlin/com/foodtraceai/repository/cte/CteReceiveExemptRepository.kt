// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.repository.cte

import com.foodtraceai.model.cte.CteReceiveExempt
import com.foodtraceai.repository.BaseRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface CteReceiveExemptRepository : BaseRepository<CteReceiveExempt> {
    @Query(
        value = "select cte from CteReceiveExempt cte join tlc on cte.tlc.id = tlc.id " +
                "where (:tlcVal is null or tlc.tlcVal = :tlcVal) and " +
                "(:ipsLocationId is null or cte.ipsLocation.id = :ipsLocationId) and " +
                "(:locationId is null or cte.location.id = :locationId) and " +
                "(CAST(:dayFrom as date) is null or CAST(:dayTo as date) is null or " +
                "(cte.receiveDate between :dayFrom and :dayTo)) and " +
                "(cte.dateDeleted is null) " +
                "order by cte.receiveDate"
    )
    fun findAllByOptionalParams(
        @Param("tlcVal") tlcVal: String? = null,
        @Param("ipsLocationId") ipsLocationId: Long? = null,
        @Param("locationId") locationId: Long? = null,
        @Param("dayFrom") dayFrom: LocalDate? = null,
        @Param("dayTo") dayTo: LocalDate? = null,
    ): List<CteReceiveExempt>
}