// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.util

enum class ColHeaderType {
    Required,
    PtiBestPractices,
}

// -- ISO-3166 3-Letter Country codes
enum class Country {
    USA,
    CAN,
    MEX,
}

enum class CteType {
    // For the CTEs that we create
    Cool,
    Harvest,
    InitPackExempt,
    InitPackProduce,
    InitPackSprouts,
    FirstLandReceive,
    Ship,
    Receive,
    Transform,
}

enum class FoodBusType(val cteTypes: List<CteType>) {
    Farm(listOf(CteType.Harvest)),
    Cooler(listOf(CteType.Cool)),
    Packer(listOf(CteType.InitPackProduce, CteType.Ship)),
    Processor(listOf(CteType.Receive, CteType.Transform, CteType.Ship)),
    Distributor(listOf(CteType.Receive, CteType.Ship)),
    RFE(listOf(CteType.Receive, CteType.Transform, CteType.Ship)),  // grocery, convenience, club stores, etc.
    Restaurant(listOf(CteType.Receive, CteType.Transform, CteType.Ship)),
}

enum class FtlItem {
    Bivalves,
    Cheeses,
    Cucumbers,
    DeliSalads,
    Finfish,
    Fruits,
    Herbs,
    LeafyGreens,
    Melons,
    MolluscanShellfish,
    NutButters,
    Peppers,
    ShellEggs,
    SmokedFinfish,
    Sprouts,
    Tomatoes,
    TropicalTreeFruits,
    Vegetables,
}

enum class TlcDateType {
    PackDate,
    ShipDate,
}

enum class ReferenceDocumentType {
    ASN,
    BOL,
    CTE,
    PO,
    WO,
    OTHER,
}

// SGE: The order of the Roles is important - highest permissions last
enum class Role {
    Mobile,
    FoodBusinessUser,
    FoodBusinessAdmin,
    FranchisorAdmin,
    RootAdmin,
}

fun maxRole(roles: List<Role>) = roles.maxBy { it.ordinal }

enum class SupCteStatus {
    Pending,
    Received,
    Cancelled,
}

// The quantity and unit of measure of the food (e.g., 75 bins, 200 pounds);
enum class UnitOfMeasure {
    Bin,
    Carton,
    Case,
    FieldBin,
    Kilo,
    Pound,
}

enum class UsaCanadaState(val stateName: String) {
    AL("Alabama"),
    AK("Alaska"),
    AR("Arkansas"),
    AZ("Arizona"),
    CA("California"),
    CO("Colorado"),
    CT("Connecticut"),
    DE("Delaware"),
    FL("Florida"),
    GA("Georgia"),
    HI("Hawaii"),
    IA("Iowa"),
    ID("Idaho"),
    IL("Illinois"),
    IN("Indiana"),
    KS("Kansas"),
    KY("Kentucky"),
    LA("Louisiana"),
    MA("Massachusetts"),
    MD("Maryland"),
    ME("Maine"),
    MI("Michigan"),
    MN("Minnesota"),
    MO("Missouri"),
    MS("Mississippi"),
    MT("Montana"),
    NC("NorthCarolina"),
    ND("NorthDakota"),
    NE("Nebraska"),
    NH("NewHampshire"),
    NJ("NewJersey"),
    NM("NewMexico"),
    NV("Nevada"),
    NY("NewYork"),
    OH("Ohio"),
    OK("Oklahoma"),
    OR("Oregon"),
    PA("Pennsylvania"),
    RI("RhodeIsland"),
    SC("SouthCarolina"),
    SD("SouthDakota"),
    TN("Tennessee"),
    TX("Texas"),
    UT("Utah"),
    VA("Virginia"),
    VT("Vermont"),
    WA("Washington"),
    WI("Wisconsin"),
    WV("WestVirginia"),
    WY("Wyoming"),

    // ****** Canada *******
    AB("Alberta"),
    BC("British Columbia"),
    MB("Manitoba"),
    NB("New Brunswick"),
    NL("Newfoundland and Labrador"),
    NS("Nova Scotia"),
    NT("Northwest Territories"),
    NU("Nunavut"),
    ON("Ontario"),
    PE("Prince Edward Island"),
    QC("Quebec"),
    SK("Saskatchewan"),
    YT("Yukon"),
}

inline fun <reified T : Enum<*>> enumValueOrNull(name: String?): T? =
    if (name != null) T::class.java.enumConstants.firstOrNull { it.name == name }
    else null

