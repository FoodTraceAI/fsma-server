// ----------------------------------------------------------------------------
// Copyright Kaleidoscope, Inc. or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.auth.JwtService
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.service.*
import com.foodtraceai.service.cte.*
import com.foodtraceai.service.supplier.SupShipCteService
import com.foodtraceai.util.BadRequestException
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

    //    fun getFsaUser(id: Long, authPrincipal: FsmaUser): FsaUser {
//        val fsaUser = fsaUserService.findById(id)
//            ?: throw EntityNotFoundException("FsaUser not found: $id")
//
//        if (authPrincipal.isParentResellerAdminOnly() &&
//            (
//                authPrincipal.resellerId == fsaUser.resellerId ||
//                    authPrincipal.resellerId == fsaUser.reseller.parentReseller?.id
//                )
//        ) {
//            return fsaUser
//        } else if (!authPrincipal.isParentResellerAdminOnly()) {
//            assertResellerClientMatchesToken(authPrincipal, fsaUser.resellerId, fsaUser.client.id)
//        }
//
//        return fsaUser
//    }
//
//    fun getAddress(id: Long, authPrincipal: FsmaUser): Address {
//        val address = addressService.findById(id)
//            ?: throw EntityNotFoundException("Address not found: $id")
//        assertResellerClientMatchesToken(authPrincipal, address.resellerId)
//        return address
//    }
//
//    fun getCustomer(id: Long, authPrincipal: FsmaUser): Customer {
//        val customer = customerService.findById(id)
//            ?: throw EntityNotFoundException("Customer not found: $id")
//        assertResellerClientMatchesToken(authPrincipal, customer.resellerId, customer.client.id)
//        return customer
//    }
//
//    fun getScheduleItem(id: Long, authPrincipal: FsmaUser): ScheduleItem {
//        val scheduleItem = scheduleItemService.findById(id)
//            ?: throw EntityNotFoundException("ScheduleItem not found: $id")
//        assertResellerClientMatchesToken(
//            authPrincipal,
//            scheduleItem.workRequest.reseller.id,
//            scheduleItem.workRequest.client.id
//        )
//        return scheduleItem
//    }
//
//    fun getServLoc(id: Long, authPrincipal: FsmaUser): ServLoc {
//        val servLoc = servLocService.findById(id)
//            ?: throw EntityNotFoundException("ServLoc not found: $id")
//        assertResellerClientMatchesToken(authPrincipal, servLoc.reseller.id, servLoc.client.id)
//        return servLoc
//    }
//
//    fun getWorkRequest(id: Long, authPrincipal: FsmaUser): WorkRequest {
//        val workRequest = workRequestService.findById(id)
//            ?: throw EntityNotFoundException("WorkRequest not found: $id")
//        assertResellerClientMatchesToken(authPrincipal, workRequest.reseller.id, workRequest.client.id)
//        return workRequest
//    }
//
//    fun getWorkTypeItem(id: Long, authPrincipal: FsmaUser): WorkTypeItem {
//        val workTypeItem = workTypeItemService.findById(id)
//            ?: throw EntityNotFoundException("WorkTypeItem not found: $id")
//        assertResellerClientMatchesToken(authPrincipal, workTypeItem.reseller.id, workTypeItem.client.id)
//        return workTypeItem
//    }
//
//    fun getWorkRequestHistory(id: Long, authPrincipal: FsmaUser): WorkRequestHistory {
//        val workRequestHistory = workRequestHistoryService.findById(id)
//            ?: throw EntityNotFoundException("WorkType not found: $id")
//        assertResellerClientMatchesToken(authPrincipal, workRequestHistory.reseller.id, workRequestHistory.client.id)
//        return workRequestHistory
//    }
//
//    fun getWorkType(id: Long, authPrincipal: FsmaUser): WorkType {
//        val workType = workTypeService.findById(id)
//            ?: throw EntityNotFoundException("WorkType not found: $id")
//        assertResellerClientMatchesToken(authPrincipal, workType.reseller.id, workType.client.id)
//        return workType
//    }
//
//    fun getInvoice(id: Long, authPrincipal: FsmaUser): Invoice {
//        val invoice = invoiceService.findById(id)
//            ?: throw EntityNotFoundException("Invoice not found: $id")
//        assertResellerClientMatchesToken(authPrincipal, invoice.reseller.id, invoice.client.id)
//        return invoice
//    }
//
//    fun getEstimate(id: Long, authPrincipal: FsmaUser): Estimate {
//        val estimate = estimateService.findById(id)
//            ?: throw EntityNotFoundException("Estimate not found: $id")
//        assertResellerClientMatchesToken(authPrincipal, estimate.reseller.id, estimate.client.id)
//        return estimate
//    }
//
//    // ----------------------------
//
//    fun getDatastore(id: Long, authPrincipal: FsmaUser): Datastore {
//        val datastore = datastoreService.findById(id)
//            ?: throw EntityNotFoundException("Datastore not found: $id")
//        if (!authPrincipal.isRootAdmin()) {
//            if (datastore.clientId != null) {
//                assertResellerClientMatchesToken(authPrincipal, datastore.resellerId, datastore.clientId)
//            } else {
//                assertResellerMatchesToken(authPrincipal, datastore.resellerId)
//            }
//        }
//
//        return datastore
//    }
//
//    // ----------------------------
//
    protected fun assertFoodBusinessMatchesToken(
        authPrincipal: FsmaUser,
        modelFoodBusinessId: Long,
//    modelClientId: Long? = null
    ) {
        if (
            authPrincipal.isRootAdmin() || isFoodBusinessCheck(authPrincipal, modelFoodBusinessId)

        //TODO: remove
//            isResellerCheck(authPrincipal, modelResellerId) ||
//            isClientCheck(authPrincipal, modelResellerId, modelClientId)
        ) return

        // Permissions are wrong
        throw BadRequestException("Invalid request")
    }

    private fun isFoodBusinessCheck(authPrincipal: FsmaUser, modelFoodBusinessId: Long) =
        authPrincipal.isFoodBusAdmin() && modelFoodBusinessId == authPrincipal.foodBus.id
//
//    private fun isResellerCheck(authPrincipal: FsmaUser, modelResellerId: Long): Boolean {
//        return authPrincipal.isResellerAdmin() && modelResellerId == authPrincipal.reseller.id
//    }
//
//    private fun isClientCheck(authPrincipal: FsmaUser, modelResellerId: Long, modelClientId: Long?): Boolean {
//        return (authPrincipal.isClientAdmin() || authPrincipal.isCoordinator() || authPrincipal.isMobile()) &&
//            modelResellerId == authPrincipal.reseller.id && modelClientId != null && modelClientId == authPrincipal.client.id
//    }
//
//    protected fun assertResellerMatchesToken(
//        authPrincipal: FsmaUser,
//        modelResellerId: Long
//    ) {
//        if (
//            authPrincipal.isRootAdmin() ||
//            modelResellerId == authPrincipal.reseller.id
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
