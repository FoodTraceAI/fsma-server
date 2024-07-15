// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.BaseModel
import com.foodtraceai.repository.BaseRepository
import com.foodtraceai.util.EntityException
import com.foodtraceai.util.UnauthorizedRequestException
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime

abstract class BaseService<T : BaseModel<T>>(
    private val repo: BaseRepository<T>,
    private val entityName: String
) {

    open fun findById(id: Long): T? = repo.findByIdOrNull(id)

    open fun findAll(): List<T> = repo.findAll()

    open fun save(entity: T): T = repo.save(entity)

    // --------------------------------

    open fun validateInsert(entity: T) {
        if (repo.existsById(entity.id))
            throw EntityExistsException("$entityName already exists: ${entity.id}")
        if (entity.id != 0L)
            throw UnauthorizedRequestException("$entityName Id must be '0' (zero) on create")
    }

    open fun insert(entity: T): T {
        validateInsert(entity)
        return repo.save(entity)
    }

    // --------------------------------

    open fun validateUpdate(entity: T) {
        if (!repo.existsById(entity.id))
            throw EntityNotFoundException("$entityName not found: ${entity.id}")
    }

    open fun update(entity: T): T {
        validateUpdate(entity)
        return repo.save(entity)
    }

    @Transactional
    open fun bulkUpsert(entities: List<T>): List<T> {
        return entities.map {
            upsert(it)
        }
    }

    open fun upsert(entity: T): T {
        if (entity.id == 0L) {
            validateInsert(entity)
        } else {
            validateUpdate(entity)
        }

        return repo.save(entity)
    }

    // --------------------------------

    open fun validateDelete(entity: T) {
        if (!repo.existsById(entity.id))
            throw EntityNotFoundException("$entityName not found: ${entity.id}")
        if (entity.isDeleted)
            throw EntityException("$entityName already deleted: ${entity.id}")
    }

    open fun delete(entity: T): T { // hard delete
        validateDelete(entity)
        repo.deleteById(entity.id)
        return entity
    }

    open fun deleteSoft(entity: T): T { // soft delete
        validateDelete(entity)
        entity.isDeleted = true
        entity.dateDeleted = OffsetDateTime.now()
        entity.preSoftDelete()
        return repo.save(entity)
    }

    @Transactional
    open fun bulkSoftDelete(entities: List<T>) {
        entities.map {
            deleteSoft(it)
        }
    }
}
