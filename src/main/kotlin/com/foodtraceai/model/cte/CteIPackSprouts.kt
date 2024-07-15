// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model.cte

import com.foodtraceai.model.FoodBus
import com.foodtraceai.model.Location
import com.foodtraceai.model.TraceLotCode
import com.foodtraceai.util.CteType
import com.foodtraceai.util.FtlItem
import com.foodtraceai.util.ReferenceDocumentType
import com.foodtraceai.util.UnitOfMeasure
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

/**
https://producetraceability.org/wp-content/uploads/2024/02/PTI-FSMA-204-Implementation-Guidance-FINAL-2.12.24.pdf
Look at p.24

https://www.ecfr.gov/current/title-21/chapter-I/subchapter-A/part-1/subpart-S/subject-group-ECFRbfe98fb65ccc9f7/section-1.1330
§ 1.1330 What records must I keep when I am performing the initial packing of a raw
agricultural commodity (other than a food obtained from a fishing vessel) on the
Food Traceability List?
 **/

@Entity
@Table(name = "cte_ipack_sprouts")
data class CteIPackSprouts(
    @Id @GeneratedValue override val id: Long = 0,

    @Enumerated(EnumType.STRING)
    override val cteType: CteType = CteType.InitPackSprouts,

    // Location for this CTE
    @ManyToOne @JoinColumn
    override val location: Location,

    // Not required but likely useful to save associated Cte Harvest. See p.22 in
    // https://producetraceability.org/wp-content/uploads/2024/02/PTI-FSMA-204-Implementation-Guidance-FINAL-2.12.24.pdf
    @ManyToOne(cascade = [CascadeType.ALL]) @JoinColumn
    val cteHarvest: CteHarvest? = null,

    // ************** KDEs *************
    // (a) Except as specified in paragraph (c) of this section, for each traceability
    // lot of a raw agricultural commodity (other than a food obtained from a fishing vessel)
    // on the Food Traceability List you initially pack, you must maintain records
    // containing the following information and linking this information to the
    // traceability lot:

    // (a)(1) The commodity and, if applicable, variety of the food;
    @Enumerated(EnumType.STRING)
    override val ftlItem: FtlItem, // or commodity
    override val variety: String,
    override val foodDesc: String,  // not required for this CTE

    // (a)(2) The date you received the food;
    val receiveDate: LocalDate,
    val receiveTime: OffsetDateTime,    // Not required but useful

    // (a)(3) The quantity and unit of measure of the food received (e.g., 75 bins, 200 pounds);
    val receiveQuantity: Double,
    @Enumerated(EnumType.STRING)
    val receiveUnitOfMeasure: UnitOfMeasure,

    // (a)(4) The location description for the farm where the food was harvested;
    @ManyToOne @JoinColumn
    val harvestLocation: Location,

    // (a)(5) For produce, the name of the field or other growing area from which the
    // food was harvested (which must correspond to the name used by the grower),
    // or other information identifying the harvest location at least as precisely
    // as the field or other growing area name;
    val fieldName: String,
    val fieldDesc: String,

    // (a)(6) For aquacultured food, the name of the container
    // (e.g., pond, pool, tank, cage) from which the food was harvested
    // (which must correspond to the container name
    // used by the aquaculture farmer) or other information identifying the harvest
    // location at least as precisely as the container name;
    val containerName: String,
    val containerDesc: String,

    // (a)(7) The business name and phone number for the harvester of the food
    @ManyToOne @JoinColumn
    val harvestFoodBus: FoodBus,

    // (a)(8) The date of harvesting;
    val harvestDate: LocalDate,

    // (a)(9) The location description for where the food was cooled
    // (if applicable);
    @ManyToOne @JoinColumn
    val coolLocation: Location? = null,

    // (a)(10) The date of cooling (if applicable);
    val coolDate: LocalDate? = null,

    // (a)(11) The traceability lot code you assigned;
    @ManyToOne @JoinColumn
    val packTlc: TraceLotCode,

    // (a)(12) The product description of the packed food;
    // TODO From PTI This is the description for the Case,
    //  not the saleable unit in the case.
    //  Product description should include:
    //      product name (including, if applicable,
    //      the brand name, commodity, and variety)
    //      packaging size
    //      packaging style
    val packFoodDesc: String,

    // Quantity packed and pack unit of measure.
    // (a)(13) The quantity and unit of measure of the packed food (e.g., 6 cases, 25 reusable plastic containers, 100 tanks, 200 pounds);
    override val quantity: Short,
    @Enumerated(EnumType.STRING)
    override val unitOfMeasure: UnitOfMeasure,

    // (a)(14) The location description for where you initially packed the food
    // (i.e., the traceability lot code source), and (if applicable) the traceability
    // lot code source reference;
    // Either the tlcSource or the tlcSourceReference should be null.
    // Only one of these should be populated in production
    @ManyToOne @JoinColumn
    val packTlcSource: Location? = null,    // i.e., the packLocation since TLC is created at this CTE
    val packTlcSourceReference: String? = null,

    // (a)(15) The date of initial packing; and
    val packDate: LocalDate,

    // (a)(16) The reference document type and reference document number.
    @Enumerated(EnumType.STRING)
    override val referenceDocumentType: ReferenceDocumentType,
    override val referenceDocumentNum: String,

    // Part (b) for Sprouts.
    // https://producetraceability.org/wp-content/uploads/2024/02/PTI-FSMA-204-Implementation-Guidance-FINAL-2.12.24.pdf
    // look at p.23

    // (b) For each traceability lot of sprouts (except soil- or substrate-grown
    // sprouts harvested without their roots) you initially pack, you must also
    // maintain records containing the following information and linking this
    // information to the traceability lot:

    // (b)(1) The location description for the grower of seeds for sprouting and the
    // date of seed harvesting, if either is available;
    @ManyToOne @JoinColumn
    val seedGrowerLocation: Location? = null,
    val seedHarvestingDate: LocalDate? = null,

    // (b)(2) The location description for the seed conditioner or processor,
    // the associated seed lot code, and the date of conditioning or processing;
    @ManyToOne @JoinColumn
    val seedConditionerLocation: Location,  // or seed processor
    @ManyToOne @JoinColumn
    val seedTlc: TraceLotCode,
    val seedConditioningDate: LocalDate,

    // (b)(3) The location description for the seed packinghouse (including any repackers),
    // the date of packing (and of repacking, if applicable), and any associated
    // seed lot code assigned by the seed packinghouse;
    @ManyToOne @JoinColumn
    val seedPackingHouseLocation: Location, // TODO: convert into a list for repackers
    val seedRepackingDate: LocalDate? = null, // TODO: convert into a list for repackers
    @ManyToOne @JoinColumn
    val seedPackingHouseTlc: TraceLotCode? = null,

    // (b)(4) The location description for the seed supplier, any seed lot code
    // assigned by the seed supplier (including the master lot and sub-lot codes),
    // and any new seed lot code assigned by the sprouter;
    @ManyToOne @JoinColumn
    val seedSupplierLocation: Location, // TODO: convert into a list for repackers
    @ManyToOne @JoinColumn
    val seedSupplierTlc: TraceLotCode? = null,

    // (b)(5) A description of the seeds, including the seed type or taxonomic name,
    // growing specifications, type of packaging, and (if applicable) antimicrobial
    // treatment;
    val seedDesc: String,
    val seedTaxonomicName: String,
    val seedGrowingSpecs: String,
    val seedTypeOfPacking: String,
    val seedAntimicrobialTreatment: String? = null,

    // (b)(6) The date of receipt of the seeds by the sprouter; and
    val seedReceiveDate: LocalDate,
    val seedReceiveTime: OffsetDateTime,    // Not required but useful

    // (b)(7) The reference document type and reference document number.
    @Enumerated(EnumType.STRING)
    val seedReferenceDocumentType: ReferenceDocumentType,
    val seedReferenceDocumentNum: String,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : CteBase<CteIPackSprouts>()

data class CteIPackSproutsDto(
    val id: Long,
    val cteType: CteType,
    val locationId: Long,
    val cteHarvestId: Long?,
    val ftlItem: FtlItem,
    val variety: String,
    val foodDesc: String,
    val receiveDate: LocalDate,
    val receiveTime: OffsetDateTime,
    val receiveQuantity: Double,
    val receiveUnitOfMeasure: UnitOfMeasure,
    val harvestLocationId: Long,
    val fieldName: String,
    val fieldDesc: String,
    val containerName: String,
    val containerDesc: String,
    val harvestBusinessId: Long,
    val harvestDate: LocalDate,
    val coolLocationId: Long?,
    val coolDate: LocalDate?,
    val packTlcId: Long,
    val packFoodDesc: String,
    val quantity: Short,
    val unitOfMeasure: UnitOfMeasure,
    val packTlcSourceId: Long?,
    val packTlcSourceReference: String? = null,
    val packDate: LocalDate,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,

    // Seeds - part (b)
    val seedGrowerLocationId: Long?,
    val seedHarvestingDate: LocalDate?,
    val seedConditionerLocationId: Long,
    val seedTlcId: Long,
    val seedConditioningDate: LocalDate,
    val seedPackingHouseLocationId: Long,
    val seedRepackingDate: LocalDate?,
    val seedPackingHouseTlcId: Long?,
    val seedSupplierLocationId: Long,
    val seedSupplierTlcId: Long?,
    val seedDesc: String,
    val seedTaxonomicName: String,
    val seedGrowingSpecs: String,
    val seedTypeOfPacking: String,
    val seedAntimicrobialTreatment: String?,
    val seedReceiveDate: LocalDate,
    val seedReceiveTime: OffsetDateTime,    // Not required but useful
    val seedReferenceDocumentType: ReferenceDocumentType,
    val seedReferenceDocumentNum: String,
    val dateCreated: OffsetDateTime,
    val dateModified: OffsetDateTime,
    val isDeleted: Boolean,
    val dateDeleted: OffsetDateTime?,
)

fun CteIPackSprouts.toCteIPackSproutsDto() = CteIPackSproutsDto(
    id = id,
    cteType = cteType,
    locationId = location.id,
    cteHarvestId = cteHarvest?.id,
    ftlItem = ftlItem,
    variety = variety,
    foodDesc = foodDesc,
    receiveDate = receiveDate,
    receiveTime = receiveTime,
    receiveQuantity = receiveQuantity,
    receiveUnitOfMeasure = receiveUnitOfMeasure,
    harvestLocationId = harvestLocation.id,
    fieldName = fieldName,
    fieldDesc = fieldDesc,
    containerName = containerName,
    containerDesc = containerDesc,
    harvestBusinessId = harvestFoodBus.id,
    harvestDate = harvestDate,
    coolLocationId = coolLocation?.id,
    coolDate = coolDate,
    packTlcId = packTlc.id,
    packFoodDesc = packFoodDesc,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    packTlcSourceId = packTlcSource?.id,
    packTlcSourceReference = packTlcSourceReference,
    packDate = packDate,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    // Seeds - part (b)
    seedGrowerLocationId = seedGrowerLocation?.id,
    seedHarvestingDate = seedHarvestingDate,
    seedConditionerLocationId = seedConditionerLocation.id,
    seedTlcId = seedTlc.id,
    seedConditioningDate = seedConditioningDate,
    seedPackingHouseLocationId = seedPackingHouseLocation.id,
    seedRepackingDate = seedRepackingDate,
    seedPackingHouseTlcId = seedPackingHouseTlc?.id,
    seedSupplierLocationId = seedSupplierLocation.id,
    seedSupplierTlcId = seedSupplierTlc?.id,
    seedDesc = seedDesc,
    seedTaxonomicName = seedTaxonomicName,
    seedGrowingSpecs = seedGrowingSpecs,
    seedTypeOfPacking = seedTypeOfPacking,
    seedAntimicrobialTreatment = seedAntimicrobialTreatment,
    seedReceiveDate = seedReceiveDate,
    seedReceiveTime = seedReceiveTime,
    seedReferenceDocumentType = seedReferenceDocumentType,
    seedReferenceDocumentNum = seedReferenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun CteIPackSproutsDto.toCteIPackSprouts(
    location: Location,
    cteHarvest: CteHarvest?,
    harvestLocation: Location,
    harvestFoodBus: FoodBus,
    coolLocation: Location?,
    packTlc: TraceLotCode,
    packTlcSource: Location?,
    seedGrowerLocation: Location?,
    seedConditionerLocation: Location,
    seedTlc: TraceLotCode,
    seedPackingHouseLocation: Location,
    seedPackingHouseTlc: TraceLotCode?,
    seedSupplierLocation: Location,
    seedSupplierTlc: TraceLotCode?,
) = CteIPackSprouts(
    id = id,
    cteType = cteType,
    location = location,
    cteHarvest = cteHarvest,
    ftlItem = ftlItem,
    variety = variety,
    foodDesc = foodDesc,
    receiveDate = receiveDate,
    receiveTime = receiveTime,
    receiveQuantity = receiveQuantity,
    receiveUnitOfMeasure = receiveUnitOfMeasure,
    harvestLocation = harvestLocation,
    fieldName = fieldName,
    fieldDesc = fieldDesc,
    containerName = containerName,
    containerDesc = containerDesc,
    harvestFoodBus = harvestFoodBus,
    harvestDate = harvestDate,
    coolLocation = coolLocation,
    coolDate = coolDate,
    packTlc = packTlc,
    packFoodDesc = packFoodDesc,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    packTlcSource = packTlcSource,
    packTlcSourceReference = packTlcSourceReference,
    packDate = packDate,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    // Seeds - part (b)
    seedGrowerLocation = seedGrowerLocation,
    seedHarvestingDate = seedHarvestingDate,
    seedConditionerLocation = seedConditionerLocation,
    seedTlc = seedTlc,
    seedConditioningDate = seedConditioningDate,
    seedPackingHouseLocation = seedPackingHouseLocation,
    seedRepackingDate = seedRepackingDate,
    seedPackingHouseTlc = seedPackingHouseTlc,
    seedSupplierLocation = seedSupplierLocation,
    seedSupplierTlc = seedSupplierTlc,
    seedDesc = seedDesc,
    seedTaxonomicName = seedTaxonomicName,
    seedGrowingSpecs = seedGrowingSpecs,
    seedTypeOfPacking = seedTypeOfPacking,
    seedAntimicrobialTreatment = seedAntimicrobialTreatment,
    seedReceiveDate = seedReceiveDate,
    seedReceiveTime = seedReceiveTime,
    seedReferenceDocumentType = seedReferenceDocumentType,
    seedReferenceDocumentNum = seedReferenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)