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

    //    @Autowired
    //    private lateinit var tenantIdentifierResolver: TenantIdentifierResolver

    override fun run(args: ApplicationArguments?) {
        deleteAllData()
        addAddresses()
        addResellers()
        addFoodBusinesses()
        addLocations()
        addFsmaUsers()
        addTlcs()
        addCteReceives()
        addSupShipCtes()
    }

    @Suppress("LongMethod")
    fun deleteAllData() {
        jdbcTemplate.execute("DELETE FROM address CASCADE;")
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS address_seq RESTART;")

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
    }

    fun addAddresses() {
        val resellerId = 1L
        var addressDto = AddressDto(
            street = "1413 Durness Ct.",
            city = "Naperville",
            state = UsaCanadaState.IL,
            postalCode = "60565",
            country = Country.USA,
            lat = 35.1268133,
            lng = -90.0087413
        )

        var address = addressDto.toAddress(resellerId)
        addressList.add(addressService.insert(address))


        addressDto = AddressDto(
            street = "1622 Central Ave",
            city = "Memphis",
            state = UsaCanadaState.TN,
            postalCode = "38104-5064",
            country = Country.USA,
            lat = 35.1268133,
            lng = -90.0087413
        )

        address = addressDto.toAddress(resellerId)
        addressList.add(addressService.insert(address))

        addressDto = AddressDto(
            street = "630 N. Main",
            city = "Naperville",
            state = UsaCanadaState.IL,
            postalCode = "60563",
            country = Country.USA,
            lat = 35.1268133,
            lng = -90.0087413
        )

        address = addressDto.toAddress(resellerId)
        addressList.add(addressService.insert(address))
    }

    fun addResellers() {
        var resellerDto = ResellerDto(
            addressDto = addressList[0].toAddressDto(),
            accountRep = "Steve",
            businessName = "FoodTraceAI",
            firstName = "Stephen",
            lastName = "Eick",
            mainContactName = "mainContactName",
            mainContactPhone = "mainContactPhone",
            mainContactEmail = "mainContactEmail",
            billingContactName = "billingContactName",
            billingContactPhone = "billingContactPhone",
            billingContactEmail = "billingContactEmail",
            billingAddressDto = addressList[0].toAddressDto(),
        )
        val resellerId = 1L // for testing
        var reseller = resellerDto.toReseller(resellerId)
        resellerList.add(resellerService.insert(reseller))
    }

    fun addFoodBusinesses() {
        var foodBus = FoodBus(
            reseller = resellerList[0],
            mainAddress = addressList[0],
            foodBusName = "FoodTraceAI",
            contactName = "Stephen Eick",
            contactPhone = "630-561-7897",
            foodBusType = FoodBusType.Restaurant
        )
        foodBusList.add(foodBusService.insert(foodBus))

        foodBus = FoodBus(
            reseller = resellerList[0],
            mainAddress = addressList[0],
            foodBusName = "KaleidoscopeInc",
            contactName = "Joe Smith",
            contactPhone = "800-555-1212",
            foodBusType = FoodBusType.Restaurant
        )
        foodBusList.add(foodBusService.insert(foodBus))

        foodBus = FoodBus(
            reseller = resellerList[0],
            mainAddress = addressList[0],
            foodBusName = "630 N. Main",
            contactName = "Ted Podolak",
            contactPhone = "800-555-1212",
            foodBusType = FoodBusType.Restaurant
        )
        foodBusList.add(foodBusService.insert(foodBus))
    }

    fun addLocations() {
        var location = Location(
            foodBus = foodBusList[0],
            contactName = foodBusList[0].contactName,
            contactPhone = foodBusList[0].contactPhone,
            contactEmail = foodBusList[0].contactEmail,
            address = foodBusList[0].mainAddress
        )
        val response = locationService.insert(location)
        val retrieve = locationService.findById(response.id)
        locationList.add(retrieve!!)

        location = Location(
            foodBus = foodBusList[1],
            contactName = foodBusList[1].contactName,
            contactPhone = foodBusList[1].contactPhone,
            contactEmail = foodBusList[1].contactEmail,
            address = foodBusList[1].mainAddress
        )
        locationList.add(locationService.insert(location))

        location = Location(
            foodBus = foodBusList[2],
            contactName = foodBusList[2].contactName,
            contactPhone = foodBusList[2].contactPhone,
            contactEmail = foodBusList[2].contactEmail,
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
            gtin = "10333830000016",
            batch = "187",
            tlcDate = LocalDate.of(2023, 7, 12),
            tlcDateType = TlcDateType.PackDate,
        )
        tlcList.add(tlcService.insert(tlc))

        tlc = TraceLotCode(
            tlcVal = "TraceLotCode2",
            gtin = "10333830000016",
            batch = "188",
            tlcDate = LocalDate.of(2023, 7, 13),
            tlcDateType = TlcDateType.PackDate,
        )
        tlcList.add(tlcService.insert(tlc))

        tlc = TraceLotCode(
            tlcVal = "TraceLotCode3",
            gtin = "10333830000016",
            batch = "123456",
            tlcDate = LocalDate.of(2023, 7, 15),
            tlcDateType = TlcDateType.PackDate,
        )
        tlcList.add(tlcService.insert(tlc))
    }

    fun addCteReceives() {
//        var previousLoc = locationService.findById(locationList[0].id)
//            ?: throw Exception("bad previousLoc find")
//        var curLoc = locationService.findById(locationList[1].id)
//        ?: throw Exception("bad curLoc find")

        var prevLoc = locationList[0]
        var curLoc = locationList[1]
        var cteReceive = CteReceive(
            location = curLoc,
            traceLotCode = tlcList[0],
            quantity = 15,
            unitOfMeasure = UnitOfMeasure.Case,
            ftlItem = FtlItem.LeafyGreens,
            foodDesc = "Iceberg Lettuce Wrapped - 24 heads",
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
            traceLotCode = tlcList[1],
            quantity = 10,
            unitOfMeasure = UnitOfMeasure.Case,
            ftlItem = FtlItem.LeafyGreens,
            foodDesc = "Iceberg Lettuce Wrapped - 24 heads",
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
            traceLotCode = tlcList[2],
            quantity = 5,
            unitOfMeasure = UnitOfMeasure.Case,
            ftlItem = FtlItem.LeafyGreens,
            foodDesc = "Iceburg Lettuce Whole - Georgia Grown",
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
            cteReceive = null,
            tlc = tlcList[0],
            quantity = 5,
            unitOfMeasure = UnitOfMeasure.Carton,
            ftlItem = FtlItem.Fruits,
            variety = "Variety of Fruits",
            foodDesc = "Food Description goes Here",
            shipToLocation = locationList[0],
            shipFromLocation = locationList[1],
            shipDate = LocalDate.of(2026, 1, 20),
            tlcSource = locationList[2],
        )
        supShipCteList.add(supShipCteService.insert(supShipCte))

        supShipCte = SupShipCte(
            supCteStatus = SupCteStatus.Pending,
            cteReceive = null,
            tlc = tlcList[1],
            quantity = 10,
            unitOfMeasure = UnitOfMeasure.Carton,
            ftlItem = FtlItem.Cucumbers,
            variety = "Cucumbers",
            foodDesc = "Cucumbers goes Here",
            shipToLocation = locationList[2],
            shipFromLocation = locationList[1],
            shipDate = LocalDate.of(2026, 1, 21),
            tlcSource = locationList[2],
        )
        supShipCteList.add(supShipCteService.insert(supShipCte))

        supShipCte = SupShipCte(
            supCteStatus = SupCteStatus.Pending,
            cteReceive = null,
            tlc = tlcList[1],
            quantity = 15,
            unitOfMeasure = UnitOfMeasure.Carton,
            ftlItem = FtlItem.DeliSalads,
            variety = "Deli Salads",
            foodDesc = "Description of Deli Salad goes Here",
            shipToLocation = locationList[2],
            shipFromLocation = locationList[1],
            shipDate = LocalDate.of(2026, 1, 22),
            tlcSource = locationList[2],
        )
        supShipCteList.add(supShipCteService.insert(supShipCte))
    }
}