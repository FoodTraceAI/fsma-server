// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.util

import com.foodtraceai.auth.AuthService
import com.foodtraceai.model.*
import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.service.*
import com.foodtraceai.service.cte.CteReceiveService
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
    private lateinit var foodTraceAddress: Address
    private lateinit var freshProdDistAddress: Address
    private lateinit var happyRestAddress: Address
    private lateinit var pepiProcessorAddress: Address

    @Autowired
    private lateinit var contactService: ContactService
    private val contactList: MutableList<Contact> = mutableListOf()
    private lateinit var freshProdDistContact: Contact
    private lateinit var happyRestContact: Contact
    private lateinit var pepiProcessorContact: Contact
    private lateinit var billingContact: Contact
    private lateinit var mainContact: Contact
    private lateinit var newContact: Contact
    private lateinit var tracePlanContact: Contact

    @Autowired
    private lateinit var cteReceiveService: CteReceiveService
    private val cteReceiveList: MutableList<CteReceive> = mutableListOf()

    @Autowired
    private lateinit var foodBusService: FoodBusService
    private val foodBusList: MutableList<FoodBus> = mutableListOf()
    private lateinit var freshProdDistBus: FoodBus
    private lateinit var happyRestBus: FoodBus
    private lateinit var pepiProcessorBus: FoodBus

    @Autowired
    private lateinit var fsmaUserService: FsmaUserService
    private val fsmaUserList: MutableList<FsmaUser> = mutableListOf()

    @Autowired
    private lateinit var locationService: LocationService
    private val locationList: MutableList<Location> = mutableListOf()
    private lateinit var freshProdDistLocation: Location
    private lateinit var happyRestaurantLocation: Location
    private lateinit var pepiLocation: Location

    @Autowired
    private lateinit var resellerService: ResellerService
    private val resellerList: MutableList<Reseller> = mutableListOf()
    private lateinit var foodTraceReseller: Reseller
    private lateinit var freshProduceReseller: Reseller

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
        var addressRequestDto = AddressRequestDto(
            street = "1 FoodTrace Street",
            city = "FoodTrace City",
            state = UsaCanadaState.IL,
            postalCode = "60563",
            country = Country.USA,
            lat = 35.1268133,
            lon = -90.0087413
        )
        foodTraceAddress = addressRequestDto.toAddress(id = 0)
        addressList.add(addressService.insert(foodTraceAddress))

        addressRequestDto = AddressRequestDto(
            street = "100 Fresh Street",
            city = "Fresh City",
            state = UsaCanadaState.IL,
            postalCode = "60563",
            country = Country.USA,
            lat = 35.1268133,
            lon = -90.0087413
        )
        freshProdDistAddress = addressRequestDto.toAddress(id = 0)
        addressList.add(addressService.insert(freshProdDistAddress))

        addressRequestDto = AddressRequestDto(
            street = "200 Happy Ave",
            city = "Happy City",
            state = UsaCanadaState.TN,
            postalCode = "38104-5064",
            country = Country.USA,
            lat = 35.1268133,
            lon = -90.0087413
        )
        happyRestAddress = addressRequestDto.toAddress(id = 0)
        addressList.add(addressService.insert(happyRestAddress))

        addressRequestDto = AddressRequestDto(
            street = "300 Processor Lane",
            city = "Processor City",
            state = UsaCanadaState.IL,
            postalCode = "60565",
            country = Country.USA,
            lat = 35.1268133,
            lon = -90.0087413
        )
        pepiProcessorAddress = addressRequestDto.toAddress(id = 0)
        addressList.add(addressService.insert(pepiProcessorAddress))
    }

    fun addContacts() {
        var contact = Contact(
            firstname = "FreshFirstName",
            lastname = "FreshLastName",
            phone = "1-800-555-1212",
            email = "FirstName.Lastname@gmail.com"
        )
        freshProdDistContact = contactService.insert(contact)
        contactList.add(freshProdDistContact)

        contact = Contact(
            firstname = "Happy",
            lastname = "Restaurant",
            email = "happy.restaurant@gmail.com",
            phone = "800-555-1212",
        )
        happyRestContact = contactService.insert(contact)
        contactList.add(happyRestContact)

        contact = Contact(
            firstname = "FirstName FoodProcessor",
            lastname = "LastName FoodProcesssor",
            phone = "1-800-555-1212",
            email = "food.processor@gmail.com"
        )
        pepiProcessorContact = contactService.insert(contact)
        contactList.add(pepiProcessorContact)

        contact = Contact(
            firstname = "billingContactFirstName",
            lastname = "billingContactLastName",
            phone = "billingContactPhone",
            email = "billingContactEmail"
        )
        billingContact = contactService.insert(contact)
        contactList.add(billingContact)

        contact = Contact(
            firstname = "mainContactFirstName",
            lastname = "mainContactLastName",
            phone = "mainContactPhone",
            email = "mainContactEmail"
        )
        mainContact = contactService.insert(contact)
        contactList.add(mainContact)

        contact = Contact(
            firstname = "NewContactFirst",
            lastname = "NewContactLast",
            phone = "1-800-555-1212",
            email = "NewContact@gmail.com"
        )
        newContact = contactService.insert(contact)
        contactList.add(newContact)

        contact = Contact(
            firstname = "trace",
            lastname = "PlanContact",
            phone = "800-555-1212",
            email = "trace@gmail.com"
        )
        tracePlanContact = contactService.insert(contact)
        contactList.add(tracePlanContact)
    }

    fun addResellers() {
        var resellerResponseDto = ResellerRequestDto(
            addressResponseDto = addressList[0].toAddressResponseDto(),
            accountRep = "FoodTraceAI Account Rep",
            businessName = "FoodTraceAI",
            mainContactId = mainContact.id,
            billingContactId = billingContact.id,
            billingAddressDto = addressList[0].toAddressResponseDto(),
            resellerType = ResellerType.Distributor,
        )
        foodTraceReseller = resellerResponseDto.toReseller(id = 0, mainContact, billingContact)
        resellerList.add(resellerService.insert(foodTraceReseller))

        resellerResponseDto = ResellerRequestDto(
            addressResponseDto = addressList[1].toAddressResponseDto(),
            accountRep = "Account Rep",
            businessName = "Fresh Produce",
            mainContactId = mainContact.id,
            billingContactId = billingContact.id,
            billingAddressDto = addressList[1].toAddressResponseDto(),
            resellerType = ResellerType.Distributor,
        )
        freshProduceReseller = resellerResponseDto.toReseller(id = 0, mainContact, billingContact)
        resellerList.add(resellerService.insert(freshProduceReseller))
    }

    fun addFoodBusinesses() {
        var foodBus = FoodBus(
            reseller = freshProduceReseller,
            mainAddress = freshProdDistAddress,
            foodBusName = "Fresh Produce Distributor",
            foodBusContact = freshProdDistContact,
            foodBusDesc = "Distributor"
        )
        freshProdDistBus = foodBusService.insert(foodBus)
        foodBusList.add(freshProdDistBus)

        foodBus = FoodBus(
            reseller = freshProduceReseller,
            mainAddress = happyRestAddress,
            foodBusName = "Happy Restaurant",
            foodBusContact = happyRestContact,
            foodBusDesc = "Restaurant"
        )
        happyRestBus = foodBusService.insert(foodBus)
        foodBusList.add(happyRestBus)

        foodBus = FoodBus(
            reseller = null,
            mainAddress = pepiProcessorAddress,
            foodBusName = "Pepi Processor",
            foodBusContact = pepiProcessorContact,
            foodBusDesc = "Processor"
        )
        pepiProcessorBus = foodBusService.insert(foodBus)
        foodBusList.add(pepiProcessorBus)
    }

    fun addLocations() {
        var location = Location(
            foodBus = freshProdDistBus,
            locationContact = freshProdDistContact,
            address = freshProdDistAddress,
        )
        var response = locationService.insert(location)
        freshProdDistLocation = locationService.findById(response.id)!!
        locationList.add(freshProdDistLocation)

        location = Location(
            foodBus = happyRestBus,
            locationContact = happyRestContact,
            address = happyRestAddress
        )
        response = locationService.insert(location)
        happyRestaurantLocation = locationService.findById(response.id)!!
        locationList.add(happyRestaurantLocation)

        location = Location(
            foodBus = pepiProcessorBus,
            locationContact = pepiProcessorContact,
            address = pepiProcessorAddress
        )
        response = locationService.insert(location)
        pepiLocation = locationService.findById(response.id)!!
        locationList.add(pepiLocation)
    }

    fun addFsmaUsers() {
        val rootDto = FsmaUserRequestDto(
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

        var fsmaUserRequestDto = FsmaUserRequestDto(
            foodBusId = foodBusList[1].id,
            locationId = 2,
            email = "User0@foodtraceai.com",
            password = "123",
            roles = listOf(Role.RootAdmin),
            firstname = "Root",
            lastname = "User0",
        )
        resDto = authService.createNewFsmaUser(fsmaUserRequestDto)
        fmsaUser = fsmaUserService.findById(resDto.fsmaUserId)
            ?: throw Exception("Failed to create FsmaUser: ${fsmaUserRequestDto.email}")
        fsmaUserList.add(fmsaUser)

        fsmaUserRequestDto = FsmaUserRequestDto(
            foodBusId = foodBusList[2].id,
            locationId = 3,
            email = "User1@foodtraceai.com",
            password = "123",
            roles = listOf(Role.FranchisorAdmin, Role.FoodBusinessUser),
            firstname = "Steve",
            lastname = "User1",
        )
        resDto = authService.createNewFsmaUser(fsmaUserRequestDto)
        fmsaUser = fsmaUserService.findById(resDto.fsmaUserId)
            ?: throw Exception("Failed to create FsmaUser: ${fsmaUserRequestDto.email}")
        fsmaUserList.add(fmsaUser)

        fsmaUserRequestDto = FsmaUserRequestDto(
            foodBusId = freshProdDistBus.id,
            locationId = locationList[0].id,
            email = "fresh@foodtraceai.com",
            password = "123",
            roles = listOf(Role.FoodBusinessUser),
            firstname = "Fresh",
            lastname = "UserFresh",
        )
        resDto = authService.createNewFsmaUser(fsmaUserRequestDto)
        fmsaUser = fsmaUserService.findById(resDto.fsmaUserId)
            ?: throw Exception("Failed to create FsmaUser: ${fsmaUserRequestDto.email}")
        fsmaUserList.add(fmsaUser)

        fsmaUserRequestDto = FsmaUserRequestDto(
            foodBusId = happyRestBus.id,
            locationId = locationList[1].id,
            email = "happy@foodtraceai.com",
            password = "123",
            roles = listOf(Role.FoodBusinessUser),
            firstname = "Happy",
            lastname = "UserHappy",
        )
        resDto = authService.createNewFsmaUser(fsmaUserRequestDto)
        fmsaUser = fsmaUserService.findById(resDto.fsmaUserId)
            ?: throw Exception("Failed to create FsmaUser: ${fsmaUserRequestDto.email}")
        fsmaUserList.add(fmsaUser)

        fsmaUserRequestDto = FsmaUserRequestDto(
            foodBusId = pepiProcessorBus.id,
            locationId = locationList[2].id,
            email = "pepi@foodtraceai.com",
            password = "123",
            roles = listOf(Role.FoodBusinessUser),
            firstname = "Pepi",
            lastname = "UserPepi",
        )
        resDto = authService.createNewFsmaUser(fsmaUserRequestDto)
        fmsaUser = fsmaUserService.findById(resDto.fsmaUserId)
            ?: throw Exception("Failed to create FsmaUser: ${fsmaUserRequestDto.email}")
        fsmaUserList.add(fmsaUser)
    }

    fun addTlcs() {
        var tlc = TraceLotCode(
            tlcVal = "TraceLotCode1",
            gtin = "10333830000016",
            sscc = "sscc1",
            batchLot = "187",
            packDate = LocalDate.of(2023, 7, 12),
            harvestDate = LocalDate.of(2024, 10, 12),
            tlcSource = pepiLocation,
        )
        val response = tlcService.insert(tlc)
        tlcList.add(response)

        tlc = TraceLotCode(
            tlcVal = "TraceLotCode2",
            gtin = "10333830000016",
            sscc = "sscc2",
            batchLot = "188",
            packDate = LocalDate.of(2023, 7, 12),
            harvestDate = LocalDate.of(2024, 10, 12),
            tlcSource = pepiLocation,
        )
        tlcList.add(tlcService.insert(tlc))

        tlc = TraceLotCode(
            tlcVal = "TraceLotCode3",
            gtin = "10333830000016",
            sscc = "sscc3",
            batchLot = "123456",
            packDate = LocalDate.of(2023, 7, 12),
            harvestDate = LocalDate.of(2024, 10, 12),
            tlcSource = pepiLocation,
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
        var tlc = tlcList[0]
        var supShipCte = SupShipCte(
            supCteStatus = SupCteStatus.Pending,
            sscc = tlc.sscc,
            logSerialNo = "serial1",
            cteReceive = null,
            tlc = tlc,
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

        tlc = tlcList[1]
        supShipCte = SupShipCte(
            supCteStatus = SupCteStatus.Pending,
            sscc = tlc.sscc,
            logSerialNo = "serial2",
            cteReceive = null,
            tlc = tlc,
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

        tlc = tlcList[2]
        supShipCte = SupShipCte(
            supCteStatus = SupCteStatus.Pending,
            sscc = tlc.sscc,
            logSerialNo = "serial3",
            cteReceive = null,
            tlc = tlc,
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
