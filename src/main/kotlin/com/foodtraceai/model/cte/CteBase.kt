// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model.cte

import com.foodtraceai.model.BaseLocationModel
import com.foodtraceai.util.CteType
import com.foodtraceai.util.FtlItem
import com.foodtraceai.util.ReferenceDocumentType
import com.foodtraceai.util.UnitOfMeasure

/**
 *** Base superclass of Critical Tracking Events
 **/
abstract class CteBase<T> : BaseLocationModel<T>() {
    abstract val cteType: CteType

    // Common to all CTEs.  For raw agricultural commodities use commodity name
    abstract val ftlItem: FtlItem  // or commodity
    abstract val variety: String
    abstract val foodDesc: String
    //TODO: Add product name, brand, commodity, and variety,
    // packaging size, packing style,
    // for fish: may include species and/or market name
    // TODO: Do it as a json text

    // quantity & unitOfMeasure is the amount after CTE is finished
    abstract val quantity: Short   // from Initial Packer or Transformer
    abstract val unitOfMeasure: UnitOfMeasure   // from Initial Packer or Transformer

    // TODO: need retain a list referenceDocuments for debugging
    abstract val referenceDocumentType: ReferenceDocumentType
    abstract val referenceDocumentNum: String
}
