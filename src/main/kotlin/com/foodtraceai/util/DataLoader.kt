// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.util

import com.foodtraceai.auth.AuthService
import com.foodtraceai.model.*
import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.model.supplier.SupShipCte
import com.foodtraceai.service.*
import com.foodtraceai.service.cte.CteReceiveService
import com.foodtraceai.service.supplier.SupShipCteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Profile("!staging && !prod")
class DataLoader : ApplicationRunner {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var addressService: AddressService
    private val addressList: MutableList<Address> = mutableListOf()

    @Autowired
    private lateinit var contactService: ContactService
    private val contactList: MutableList<Contact> = mutableListOf()
    private lateinit var billingContact: Contact
    private lateinit var joeContact: Contact
    private lateinit var mainContact: Contact
    private lateinit var newContact: Contact
    private lateinit var steveContact: Contact
    private lateinit var tedContact: Contact
    private lateinit var tracePlanContact: Contact

    @Autowired
    private lateinit var cteReceiveService: CteReceiveService
    private val cteReceiveList: MutableList<CteReceive> = mutableListOf()

    @Autowired
    private lateinit var foodBusService: FoodBusService
    private val foodBusList: MutableList<FoodBus> = mutableListOf()

    @Autowired
    private lateinit var fsmaUserService: FsmaUserService
    private val fsmaUserList: MutableList<FsmaUser> = mutableListOf()

    @Autowired
    private lateinit var locationService: LocationService
    private val locationList: MutableList<Location> = mutableListOf()

    @Autowired
    private lateinit var resellerService: ResellerService
    private val resellerList: MutableList<Reseller> = mutableListOf()

    @Autowired
    private lateinit var supShipCteService: SupShipCteService
    private val supShipCteList: MutableList<SupShipCte> = mutableListOf()

    @Autowired
    private lateinit var tlcService: TraceLotCodeService
    private val tlcList: MutableList<TraceLotCode> = mutableListOf()

    @Autowired
    private lateinit var tracePlanService: TracePlanService
    private val tracePlanList: MutableList<TracePlan> = mutableListOf()

    //    @Autowired
    //    private lateinit var tenantIdentifierResolver: TenantIdentifierResolver

    override fun run(args: ApplicationArguments?) {
        deleteAllData()
        addAddresses()
        addContacts()
        addResellers()
        addFoodBusinesses()
        addLocations()
        addFsmaUsers()
        addTlcs()
        addCteReceives()
        addSupShipCtes()
        addTracePlans()
    }

    @Suppress("LongMethod")
    fun deleteAllData() {
        jdbcTemplate.execute("DELETE FROM address CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS address_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM contact CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS contact_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_cool CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_cool_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_first_land_receive CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_land_receive_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_harvest CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_harvest_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_ipack_exempt CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_ipack_exempt_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_ipack_prod CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_ipack_prod_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_ipack_sprouts CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_ipack_sprouts_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_receive CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_receive_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_receive_exempt CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_receive_exempt_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_ship CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_ship_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM cte_trans CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cte_trans_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM food_bus CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS food_bus_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM franchisor CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS franchisor_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM franchisor_property CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS franchisor_property_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM fsma_user CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS fsma_user_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM location CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS location_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM reseller CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS reseller_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM sup_ship_cte CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS sup_ship_cte_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM trace_lot_code CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS trace_lot_code_seq RESTART;")

        jdbcTemplate.execute("DELETE FROM trace_plan CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS trace_plan_seq RESTART;")
    }

    fun addAddresses() {
        var addressDto = AddressDto(
            street = "1413 Durness Ct.",
            city = "Naperville",
            state = UsaCanadaState.IL,
            postalCode = "60565",
            country = Country.USA,
            lat = 35.1268133,
            lon = -90.0087413
        )

        var address = addressDto.toAddress()
        addressList.add(addressService.insert(address))

        addressDto = AddressDto(
            street = "1622 Central Ave",
            city = "Memphis",
            state = UsaCanadaState.TN,
            postalCode = "38104-5064",
            country = Country.USA,
            lat = 35.1268133,
            lon = -90.0087413
        )

        address = addressDto.toAddress()
        addressList.add(addressService.insert(address))

        addressDto = AddressDto(
            street = "630 N. Main",
            city = "Naperville",
            state = UsaCanadaState.IL,
            postalCode = "60563",
            country = Country.USA,
            lat = 35.1268133,
            lon = -90.0087413
        )

        address = addressDto.toAddress()
        addressList.add(addressService.insert(address))
    }

    fun addContacts() {
        var contact = Contact(
            firstName = "billingContactFirstName",
            lastName = "billingContactLastName",
            phone = "billingContactPhone",
            email = "billingContactEmail"
        )
        billingContact = contactService.insert(contact)
        contactList.add(billingContact)

        contact = Contact(
            firstName = "Joe",
            lastName = "Smith",
            email = "joe.smith@gmail.com",
            phone = "800-555-1212",
        )
        joeContact = contactService.insert(contact)
        contactList.add(joeContact)

        contact = Contact(
            firstName = "mainContactFirstName",
            lastName = "mainContactLastName",
            phone = "mainContactPhone",
            email = "mainContactEmail"
        )
        mainContact = contactService.insert(contact)
        contactList.add(mainContact)

        contact = Contact(
            firstName = "NewContactFirst",
            lastName = "NewContactLast",
            phone = "1-800-555-1212",
            email = "NewContact@gmail.com"
        )
        newContact = contactService.insert(contact)
        contactList.add(newContact)

        contact = Contact(
            firstName = "Steve",
            lastName = "Eick",
            phone = "1-800-555-1212",
            email = "steve@gmail.com"
        )
        steveContact = contactService.insert(contact)
        contactList.add(steveContact)

        contact = Contact(
            firstName = "Steve",
            lastName = "Eick",
            phone = "1-800-555-1212",
            email = "steve@gmail.com"
        )
        tedContact = contactService.insert(contact)
        contactList.add(tedContact)

        contact = Contact(
            firstName = "tracePlanContact firstName",
            lastName = "tracePlanContact lastName",
            email = "tracePlanContact email",
            phone = "tracePlanContact 800-555-1212"
        )
        tracePlanContact = contactService.insert(contact)
        contactList.add(tracePlanContact)
    }

    fun addResellers() {
        val resellerDto = ResellerDto(
            addressDto = addressList[0].toAddressDto(),
            accountRep = "Steve",
            businessName = "FoodTraceAI",
            mainContactId = mainContact.id,
            billingContactId = billingContact.id,
            billingAddressDto = addressList[0].toAddressDto(),
            resellerType = ResellerType.Distributor,
        )
        val reseller = resellerDto.toReseller(mainContact, billingContact)
        resellerList.add(resellerService.insert(reseller))
    }

    fun addFoodBusinesses() {
        var foodBus = FoodBus(
            reseller = resellerList[0],
            mainAddress = addressList[0],
            foodBusName = "FoodTraceAI",
            foodBusContact = steveContact,
            foodBusDesc = "Restaurant"
        )
        foodBusList.add(foodBusService.insert(foodBus))

        foodBus = FoodBus(
            reseller = resellerList[0],
            mainAddress = addressList[0],
            foodBusName = "KaleidoscopeInc",
            foodBusContact = joeContact,
            foodBusDesc = "Restaurant"
        )
        foodBusList.add(foodBusService.insert(foodBus))

        foodBus = FoodBus(
            reseller = resellerList[0],
            mainAddress = addressList[0],
            foodBusName = "FB @ 630 N. Main",
            foodBusContact = tedContact,
            foodBusDesc = "Restaurant"
        )
        foodBusList.add(foodBusService.insert(foodBus))
    }

    fun addLocations() {
        var location = Location(
            foodBus = foodBusList[0],
            locationContact = foodBusList[0].foodBusContact,
            address = foodBusList[0].mainAddress
        )
        val response = locationService.insert(location)
        val retrieve = locationService.findById(response.id)
        locationList.add(retrieve!!)

        location = Location(
            foodBus = foodBusList[1],
            locationContact = foodBusList[1].foodBusContact,
            address = foodBusList[1].mainAddress
        )
        locationList.add(locationService.insert(location))

        location = Location(
            foodBus = foodBusList[2],
            locationContact = foodBusList[2].foodBusContact,
            address = foodBusList[2].mainAddress
        )
        locationList.add(locationService.insert(location))
    }

    fun addFsmaUsers() {
        val rootDto = FsmaUserDto(
            foodBusId = 1,
            locationId = 1,
            email = "root@foodtraceai.com",
            password = "123",
            roles = listOf(Role.RootAdmin),
            firstname = "Root",
            lastname = "Root",
        )
        var resDto = authService.createNewFsmaUser(rootDto)
        var fmsaUser = fsmaUserService.findById(resDto.fsmaUserId)
            ?: throw Exception("Failed to create FsmaUser: ${rootDto.email}")
        fsmaUserList.add(fmsaUser)

        var fsmaUserDto = FsmaUserDto(
            foodBusId = foodBusList[1].id,
            locationId = 2,
            email = "User0@foodtraceai.com",
            password = "123",
            roles = listOf(Role.RootAdmin),
            firstname = "Root",
            lastname = "User0",
        )
        resDto = authService.createNewFsmaUser(fsmaUserDto)
        fmsaUser = fsmaUserService.findById(resDto.fsmaUserId)
            ?: throw Exception("Failed to create FsmaUser: ${fsmaUserDto.email}")
        fsmaUserList.add(fmsaUser)

        fsmaUserDto = FsmaUserDto(
            foodBusId = foodBusList[2].id,
            locationId = 3,
            email = "User1@foodtraceai.com",
            password = "123",
            roles = listOf(Role.FranchisorAdmin, Role.FoodBusinessUser),
            firstname = "Steve",
            lastname = "User1",
        )
        resDto = authService.createNewFsmaUser(fsmaUserDto)
        fmsaUser = fsmaUserService.findById(resDto.fsmaUserId)
            ?: throw Exception("Failed to create FsmaUser: ${fsmaUserDto.email}")
        fsmaUserList.add(fmsaUser)
    }

    fun addTlcs() {
        var tlc = TraceLotCode(
            tlcVal = "TraceLotCode1",
            gtin = GTIN("10333830000016"),
            batchLot = BatchLot("187"),
            packDate = LocalDate.of(2023, 7, 12),
            harvestDate = LocalDate.of(2024, 10, 12),
        )
        tlcList.add(tlcService.insert(tlc))

        tlc = TraceLotCode(
            tlcVal = "TraceLotCode2",
            gtin = GTIN("10333830000016"),
            batchLot = BatchLot("188"),
            packDate = LocalDate.of(2023, 7, 12),
            harvestDate = LocalDate.of(2024, 10, 12),
        )
        tlcList.add(tlcService.insert(tlc))

        tlc = TraceLotCode(
            tlcVal = "TraceLotCode3",
            gtin = GTIN("10333830000016"),
            batchLot = BatchLot("123456"),
            packDate = LocalDate.of(2023, 7, 12),
            harvestDate = LocalDate.of(2024, 10, 12),
        )
        tlcList.add(tlcService.insert(tlc))
    }

    fun addCteReceives() {
        var prevLoc = locationList[0]
        var curLoc = locationList[1]
        var cteReceive = CteReceive(
            location = curLoc,
            tlc = tlcList[0],
            quantity = 15,
            unitOfMeasure = UnitOfMeasure.Case,
            ftlItem = FtlItem.LeafyGreens,
            prodDesc = "Iceberg Lettuce Wrapped - 24 heads",
            variety = "variety",
            ipsLocation = prevLoc,
            receiveDate = LocalDate.of(2023, 7, 17),
            tlcSource = prevLoc,
            tlcSourceReference = null,
            referenceDocumentType = ReferenceDocumentType.BOL,
            referenceDocumentNum = "INV-12005",
        )
        cteReceiveList.add(cteReceiveService.insert(cteReceive))

        cteReceive = CteReceive(
            location = curLoc,
            tlc = tlcList[1],
            quantity = 10,
            unitOfMeasure = UnitOfMeasure.Case,
            ftlItem = FtlItem.LeafyGreens,
            prodDesc = "Iceberg Lettuce Wrapped - 24 heads",
            variety = "variety",
            ipsLocation = prevLoc,
            receiveDate = LocalDate.of(2023, 7, 18),
            tlcSource = prevLoc,
            tlcSourceReference = null,
            referenceDocumentType = ReferenceDocumentType.BOL,
            referenceDocumentNum = "INV-12345",
        )
        cteReceiveList.add(cteReceiveService.insert(cteReceive))

        prevLoc = locationList[0]
        curLoc = locationList[1]
        cteReceive = CteReceive(
            location = curLoc,
            tlc = tlcList[2],
            quantity = 5,
            unitOfMeasure = UnitOfMeasure.Case,
            ftlItem = FtlItem.LeafyGreens,
            prodDesc = "Iceburg Lettuce Whole - Georgia Grown",
            variety = "variety",
            ipsLocation = prevLoc,
            receiveDate = LocalDate.of(2023, 7, 17),
            tlcSource = prevLoc,
            tlcSourceReference = null,
            referenceDocumentType = ReferenceDocumentType.BOL,
            referenceDocumentNum = "BOL-023",
        )
        cteReceiveList.add(cteReceiveService.insert(cteReceive))
    }

    fun addSupShipCtes() {

        var supShipCte = SupShipCte(
            supCteStatus = SupCteStatus.Pending,
            sscc = Sscc("sscc1"),
            serial = LogSerialNum("serial1"),
            cteReceive = null,
            tlc = tlcList[0],
            quantity = 5,
            unitOfMeasure = UnitOfMeasure.Carton,
            ftlItem = FtlItem.Fruits,
            variety = "Variety of Fruits",
            prodDesc = "Food Description goes Here",
            shipToLocation = locationList[0],
            shipFromLocation = locationList[1],
            shipDate = LocalDate.of(2026, 1, 20),
            tlcSource = locationList[2],
            referenceDocumentType = ReferenceDocumentType.BOL,
            referenceDocumentNum = "BOL-sscc1",
        )
        supShipCteList.add(supShipCteService.insert(supShipCte))

        supShipCte = SupShipCte(
            supCteStatus = SupCteStatus.Pending,
            sscc = Sscc("sscc2"),
            serial = LogSerialNum("serial2"),
            cteReceive = null,
            tlc = tlcList[1],
            quantity = 10,
            unitOfMeasure = UnitOfMeasure.Carton,
            ftlItem = FtlItem.Cucumbers,
            variety = "Cucumbers",
            prodDesc = "Cucumbers goes Here",
            shipToLocation = locationList[2],
            shipFromLocation = locationList[1],
            shipDate = LocalDate.of(2026, 1, 21),
            tlcSource = locationList[2],
            referenceDocumentType = ReferenceDocumentType.BOL,
            referenceDocumentNum = "BOL-sscc2",
        )
        supShipCteList.add(supShipCteService.insert(supShipCte))

        supShipCte = SupShipCte(
            supCteStatus = SupCteStatus.Pending,
            sscc = Sscc("sscc3"),
            serial = LogSerialNum("serial3"),
            cteReceive = null,
            tlc = tlcList[1],
            quantity = 15,
            unitOfMeasure = UnitOfMeasure.Carton,
            ftlItem = FtlItem.DeliSalads,
            variety = "Deli Salads",
            prodDesc = "Description of Deli Salad goes Here",
            shipToLocation = locationList[2],
            shipFromLocation = locationList[1],
            shipDate = LocalDate.of(2026, 1, 22),
            tlcSource = locationList[2],
            referenceDocumentType = ReferenceDocumentType.BOL,
            referenceDocumentNum = "BOL-sscc3",
        )
        supShipCteList.add(supShipCteService.insert(supShipCte))
    }

    fun addTracePlans() {
        val tracePlan = TracePlan(
            location = locationList[1],
            descProcRecordMaintenance = "(a)(1) A description of the procedures you use to maintain the records you are required" +
                    " to keep under this subpart, including the format and location of these records.",
            descProcIdentifyFoods = "(a)(2) A description of the procedures you use to identify foods on the Food" +
                    " TraceabilityList that you manufacture, process, pack, or hold",
            descAssignTraceLotCodes = "(a)(3) A description of how you assign traceability lot codes to foods on the" +
                    " Food Traceability List in accordance with § 1.1320, if applicable;",
            tracePlanContact = tracePlanContact,
        )
        tracePlanList.add(tracePlanService.insert(tracePlan))
    }
}
