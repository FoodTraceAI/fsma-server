// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.auth.JwtService
import com.foodtraceai.model.*
import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.model.SupShipCte
import com.foodtraceai.service.*
import com.foodtraceai.service.cte.*
import com.foodtraceai.util.BadRequestException
import com.foodtraceai.util.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

// -- @RequestParam date format (ISO8601)
const val REQ_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

private const val HEADER_Page = "Page"
private const val HEADER_PageSize = "Page-Size"
private const val HEADER_TotalPages = "Total-Pages"
private const val HEADER_TotalElements = "Total-Elements"

@Component
class BaseController {

//    internal val logger by LoggerDelegate()

    @Autowired
    protected lateinit var addressService: AddressService

    @Autowired
    protected lateinit var contactService: ContactService

    @Autowired
    protected lateinit var cteFirstLandService: CteFirstLandService

    @Autowired
    protected lateinit var cteCoolService: CteCoolService

    @Autowired
    protected lateinit var cteHarvestService: CteHarvestService

    @Autowired
    protected lateinit var cteIPackExemptService: CteIPackExemptService

    @Autowired
    protected lateinit var cteIPackProdService: CteIPackProdService

    @Autowired
    protected lateinit var cteIPackSproutsService: CteIPackSproutsService

    @Autowired
    protected lateinit var cteReceiveService: CteReceiveService

    @Autowired
    protected lateinit var cteReceiveExemptService: CteReceiveExemptService

    @Autowired
    protected lateinit var cteShipService: CteShipService

    @Autowired
    protected lateinit var cteTransService: CteTransService

    @Autowired
    protected lateinit var foodBusService: FoodBusService

    @Autowired
    protected lateinit var franchisorService: FranchisorService

    @Autowired
    protected lateinit var fsmaUserService: FsmaUserService

    @Autowired
    protected lateinit var jwtService: JwtService

    @Autowired
    protected lateinit var locationService: LocationService

    @Autowired
    protected lateinit var resellerService: ResellerService

    @Autowired
    protected lateinit var spreadsheetService: SpreadsheetService

    @Autowired
    protected lateinit var supShipCteService: SupShipCteService

    @Autowired
    protected lateinit var traceLotCodeService: TraceLotCodeService

    @Autowired
    protected lateinit var tracePlanService: TracePlanService

    fun getAddress(id: Long, fsmaUser: FsmaUser): Address {
        val address = addressService.findById(id)
            ?: throw EntityNotFoundException("Address not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, address.resellerId)
        return address
    }

    fun getContact(id: Long, fsmaUser: FsmaUser): Contact {
        val contact = contactService.findById(id)
            ?: throw EntityNotFoundException("Contact not found: $id")
//        assertContactClientMatchesToken(fsaUser, business.contactId)
        return contact
    }

    fun getCteReceive(id: Long, fsmaUser: FsmaUser): CteReceive {
        val cteReceive = cteReceiveService.findById(id)
            ?: throw EntityNotFoundException("CteReceive not found: $id")
        assertFsmaUserLocationMatchessToken(fsmaUser, cteReceive.location.id)
//        assertContactClientMatchesToken(fsaUser, business.contactId)
        return cteReceive
    }

    fun getFoodBus(id: Long, fsmaUser: FsmaUser): FoodBus {
        val foodBus = foodBusService.findById(id)
            ?: throw EntityNotFoundException("FoodBus not found: $id")
//        assertContactClientMatchesToken(fsaUser, business.contactId)
        return foodBus
    }

    fun getFranchisor(id: Long, fsmaUser: FsmaUser): Franchisor {
        val franchisor = franchisorService.findById(id)
            ?: throw EntityNotFoundException("Franchisor not found: $id")
//        assertContactClientMatchesToken(fsaUser, business.contactId)
        return franchisor
    }

    fun getLocation(id: Long, fsmaUser: FsmaUser): Location {
        val location = locationService.findById(id)
            ?: throw EntityNotFoundException("Location not found: $id")
        assertFsmaUserLocationMatchessToken(fsmaUser, location.id)
//        assertContactClientMatchesToken(fsaUser, business.contactId)
        return location
    }

    fun getReseller(id: Long, fsmaUser: FsmaUser): Reseller {
        val reseller = resellerService.findById(id)
            ?: throw EntityNotFoundException("Reseller not found: $id")
//        assertContactClientMatchesToken(fsaUser, business.contactId)
        return reseller
    }

    fun getSupShipCte(id: Long, fsmaUser: FsmaUser): SupShipCte {
        val supShipCte = supShipCteService.findById(id)
            ?: throw EntityNotFoundException("SupShipCte not found: $id")
        assertFsmaUserLocationMatchessToken(fsmaUser, supShipCte.shipToLocation.id)
//        assertContactClientMatchesToken(fsaUser, business.contactId)
        return supShipCte
    }

    fun getTraceLotCode(id: Long, fsmaUser: FsmaUser): TraceLotCode {
        val traceLotCode = traceLotCodeService.findById(id)
            ?: throw EntityNotFoundException("TraceLotCode not found: $id")
//        assertContactClientMatchesToken(fsaUser, business.contactId)
        return traceLotCode
    }

    protected fun assertFsmaUserLocationMatchessToken(
        fsmaUser: FsmaUser,
        modelLocationId: Long,
    ) {
        if (
            fsmaUser.isRootAdmin() ||
            isLocationCheck(fsmaUser, modelLocationId)

        //TODO: remove
//            isResellerCheck(fsmaUser, modelResellerId) ||
//            isClientCheck(fsmaUser, modelResellerId, modelClientId)
        ) return


        // Permissions are wrong
        throw BadRequestException("Invalid request")
    }

    private fun isLocationCheck(fsmaUser: FsmaUser, modelLocationId: Long) =
        modelLocationId == fsmaUser.location.id

    //    fun getFsaUser(id: Long, fsmaUser: FsmaUser): FsaUser {
//        val fsaUser = fsaUserService.findById(id)
//            ?: throw EntityNotFoundException("FsaUser not found: $id")
//
//        if (fsmaUser.isParentResellerAdminOnly() &&
//            (
//                fsmaUser.resellerId == fsaUser.resellerId ||
//                    fsmaUser.resellerId == fsaUser.reseller.parentReseller?.id
//                )
//        ) {
//            return fsaUser
//        } else if (!fsmaUser.isParentResellerAdminOnly()) {
//            assertResellerClientMatchesToken(fsmaUser, fsaUser.resellerId, fsaUser.client.id)
//        }
//
//        return fsaUser
//    }
//
//    fun getAddress(id: Long, fsmaUser: FsmaUser): Address {
//        val address = addressService.findById(id)
//            ?: throw EntityNotFoundException("Address not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, address.resellerId)
//        return address
//    }
//
//    fun getCustomer(id: Long, fsmaUser: FsmaUser): Customer {
//        val customer = customerService.findById(id)
//            ?: throw EntityNotFoundException("Customer not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, customer.resellerId, customer.client.id)
//        return customer
//    }
//
//    fun getScheduleItem(id: Long, fsmaUser: FsmaUser): ScheduleItem {
//        val scheduleItem = scheduleItemService.findById(id)
//            ?: throw EntityNotFoundException("ScheduleItem not found: $id")
//        assertResellerClientMatchesToken(
//            fsmaUser,
//            scheduleItem.workRequest.reseller.id,
//            scheduleItem.workRequest.client.id
//        )
//        return scheduleItem
//    }
//
//    fun getServLoc(id: Long, fsmaUser: FsmaUser): ServLoc {
//        val servLoc = servLocService.findById(id)
//            ?: throw EntityNotFoundException("ServLoc not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, servLoc.reseller.id, servLoc.client.id)
//        return servLoc
//    }
//
//    fun getWorkRequest(id: Long, fsmaUser: FsmaUser): WorkRequest {
//        val workRequest = workRequestService.findById(id)
//            ?: throw EntityNotFoundException("WorkRequest not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, workRequest.reseller.id, workRequest.client.id)
//        return workRequest
//    }
//
//    fun getWorkTypeItem(id: Long, fsmaUser: FsmaUser): WorkTypeItem {
//        val workTypeItem = workTypeItemService.findById(id)
//            ?: throw EntityNotFoundException("WorkTypeItem not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, workTypeItem.reseller.id, workTypeItem.client.id)
//        return workTypeItem
//    }
//
//    fun getWorkRequestHistory(id: Long, fsmaUser: FsmaUser): WorkRequestHistory {
//        val workRequestHistory = workRequestHistoryService.findById(id)
//            ?: throw EntityNotFoundException("WorkType not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, workRequestHistory.reseller.id, workRequestHistory.client.id)
//        return workRequestHistory
//    }
//
//    fun getWorkType(id: Long, fsmaUser: FsmaUser): WorkType {
//        val workType = workTypeService.findById(id)
//            ?: throw EntityNotFoundException("WorkType not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, workType.reseller.id, workType.client.id)
//        return workType
//    }
//
//    fun getInvoice(id: Long, fsmaUser: FsmaUser): Invoice {
//        val invoice = invoiceService.findById(id)
//            ?: throw EntityNotFoundException("Invoice not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, invoice.reseller.id, invoice.client.id)
//        return invoice
//    }
//
//    fun getEstimate(id: Long, fsmaUser: FsmaUser): Estimate {
//        val estimate = estimateService.findById(id)
//            ?: throw EntityNotFoundException("Estimate not found: $id")
//        assertResellerClientMatchesToken(fsmaUser, estimate.reseller.id, estimate.client.id)
//        return estimate
//    }
//
//    // ----------------------------
//
//    fun getDatastore(id: Long, fsmaUser: FsmaUser): Datastore {
//        val datastore = datastoreService.findById(id)
//            ?: throw EntityNotFoundException("Datastore not found: $id")
//        if (!fsmaUser.isRootAdmin()) {
//            if (datastore.clientId != null) {
//                assertResellerClientMatchesToken(fsmaUser, datastore.resellerId, datastore.clientId)
//            } else {
//                assertResellerMatchesToken(fsmaUser, datastore.resellerId)
//            }
//        }
//
//        return datastore
//    }
//
//    // ----------------------------

    protected fun assertFoodBusinessMatchesToken(
        fsmaUser: FsmaUser,
        modelFoodBusinessId: Long,
//    modelClientId: Long? = null
    ) {
        if (
            fsmaUser.isRootAdmin() || isFoodBusinessCheck(fsmaUser, modelFoodBusinessId)

        //TODO: remove
//            isResellerCheck(fsmaUser, modelResellerId) ||
//            isClientCheck(fsmaUser, modelResellerId, modelClientId)
        ) return

        // Permissions are wrong
        throw BadRequestException("Invalid request")
    }

    private fun isFoodBusinessCheck(fsmaUser: FsmaUser, modelFoodBusinessId: Long) =
        fsmaUser.isFoodBusAdmin() && modelFoodBusinessId == fsmaUser.foodBus.id
//
//    private fun isResellerCheck(fsmaUser: FsmaUser, modelResellerId: Long): Boolean {
//        return fsmaUser.isResellerAdmin() && modelResellerId == fsmaUser.reseller.id
//    }
//
//    private fun isClientCheck(fsmaUser: FsmaUser, modelResellerId: Long, modelClientId: Long?): Boolean {
//        return (fsmaUser.isClientAdmin() || fsmaUser.isCoordinator() || fsmaUser.isMobile()) &&
//            modelResellerId == fsmaUser.reseller.id && modelClientId != null && modelClientId == fsmaUser.client.id
//    }
//
//    protected fun assertResellerMatchesToken(
//        fsmaUser: FsmaUser,
//        modelResellerId: Long
//    ) {
//        if (
//            fsmaUser.isRootAdmin() ||
//            modelResellerId == fsmaUser.reseller.id
//        ) return
//
//        // Permissions are wrong
//        throw BadRequestException("Invalid request")
//    }
//
//    protected fun generatePaginationHeaders(
//        page: Int,
//        pageSize: Int,
//        totalPages: Int,
//        totalElements: Long
//    ): HttpHeaders {
//        return HttpHeaders().apply {
//            add(
//                HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
//                "$HEADER_Page, $HEADER_PageSize, $HEADER_TotalPages, $HEADER_TotalElements"
//            )
//            add(HEADER_Page, page.toString())
//            add(HEADER_PageSize, pageSize.toString())
//            add(HEADER_TotalPages, totalPages.toString())
//            add(HEADER_TotalElements, totalElements.toString())
//        }
//    }
}
