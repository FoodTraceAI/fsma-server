// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.foodtraceai.util.Role
import com.foodtraceai.util.RoleToJsonConverter
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.OffsetDateTime

@Entity
@Table(
    indexes = [
        Index(columnList = "email"), Index(columnList = "food_bus_id")
    ]
)
data class FsmaUser(
    @Id @GeneratedValue
    override val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "food_bus_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    override val foodBus: FoodBus,

    // Home Location for this user
    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val location: Location,

    // email is the "username"
    @Column(unique = true)
    @Email
    internal val email: String,
    internal val password: String,

    internal val isAccountNonExpired: Boolean,
    internal val isAccountNonLocked: Boolean,
    internal val isCredentialsNonExpired: Boolean,
    internal var isEnabled: Boolean,

    @Convert(converter = RoleToJsonConverter::class)
    @Column(columnDefinition = "TEXT")
    val roles: List<Role>,

    // ************* User stuff goes here **************
    val firstname: String,
    val lastname: String,
    val notes: String?,
    val phone: String?,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
//) : BaseModel<FsmaUser>(), UserDetails {
) : BaseFoodBusModel<FsmaUser>(), UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        roles.map { SimpleGrantedAuthority(it.name) }.toMutableList()

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = isAccountNonExpired

    override fun isAccountNonLocked(): Boolean = isAccountNonLocked

    override fun isCredentialsNonExpired(): Boolean = isCredentialsNonExpired

    override fun isEnabled() = isEnabled && foodBus.isEnabled &&
            (!foodBus.isFranchisee || foodBus.franchisor!!.isEnabled)

    fun isRootAdmin() = roles.contains(Role.RootAdmin)

    fun isFranchisorAdmin() = roles.contains(Role.FranchisorAdmin)

    fun isFoodBusAdmin() = roles.contains(Role.FoodBusinessAdmin)

    fun isFoodBusUser() = isFoodBusAdmin() || roles.contains(Role.FoodBusinessUser)

    fun isMobile() = isFoodBusUser() || roles.contains(Role.Mobile)
}

data class FsmaUserDto(
    val id: Long = 0,
    val foodBusId: Long,
    val locationId: Long,
    @field:Email(message = "A valid email is required")
    val email: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val password: String = "",
    val isAccountNonExpired: Boolean = true,
    val isAccountNonLocked: Boolean = true,
    val isCredentialsNonExpired: Boolean = true,
    var isEnabled: Boolean = true,
    val roles: List<Role>,
    // ************* User stuff goes here **************
    val firstname: String,
    val lastname: String,
    val notes: String? = null,
    val phone: String? = null,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateModified: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime? = null,
)

fun FsmaUser.toFsmaUserDto() = FsmaUserDto(
    id = id,
    foodBusId = foodBus.id,
    locationId = location.id,
    email = email,
    password = password,
    isAccountNonExpired = isAccountNonExpired,
    isAccountNonLocked = isAccountNonLocked,
    isCredentialsNonExpired = isCredentialsNonExpired,
    isEnabled = isEnabled,
    roles = roles,
    firstname = firstname,
    lastname = lastname,
    notes = notes,
    phone = phone,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun FsmaUserDto.toFsmaUser(foodBus: FoodBus, location: Location) = FsmaUser(
    id = id,
    foodBus = foodBus,
    location = location,
    email = email,
    password = password,
    isAccountNonExpired = isAccountNonExpired,
    isAccountNonLocked = isAccountNonLocked,
    isCredentialsNonExpired = isCredentialsNonExpired,
    isEnabled = isEnabled,
    roles = roles,
    firstname = firstname,
    lastname = lastname,
    notes = notes,
    phone = phone,
)