package com.yokudlela.kitchen.business.service

import com.yokudlela.kitchen.application.AspectLogger
import com.yokudlela.kitchen.business.exception.BusinessException
import com.yokudlela.kitchen.business.exception.ErrorType
import com.yokudlela.kitchen.business.handler.LocationHandler
import com.yokudlela.kitchen.business.model.Location
import com.yokudlela.kitchen.business.service.converter.LocationConverter
import com.yokudlela.kitchen.client.persistence.repository.LocationRepository
import org.modelmapper.ModelMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@AspectLogger
class LocationService(
    private val locationRepository: LocationRepository,
    private val modelMapper: ModelMapper,
    private val locationConverter: LocationConverter,
) : LocationHandler {

    /**
     * Save
     * @param location Location to be saved
     * @return Location saved
     */
    @Transactional
    override fun saveLocation(location: Location): Location {
        val loc = modelMapper.map(location, com.yokudlela.kitchen.client.persistence.entity.Location::class.java)
        return locationConverter.convert(locationRepository.save(loc))
    }

    /**
     * Load
     * @param locationId Int id
     * @return Location loaded
     */
    @Transactional
    override fun loadLocation(locationId: Int): Location {
        val location =
            locationRepository.findByIdOrNull(locationId) ?: throw BusinessException(ErrorType.MISSING_ENTITY)
        return locationConverter.convert(location)
    }

    /**
     * Delete
     * @param locationId Int id
     */
    @Transactional(rollbackFor = [BusinessException::class])
    override fun deleteLocation(locationId: Int) {
        try {
            locationRepository.deleteById(locationId)
        } catch (e: EmptyResultDataAccessException) {
            throw BusinessException(ErrorType.MISSING_ENTITY)
        } catch (e: Exception) {
            throw BusinessException(ErrorType.UNKNOWN_ERROR)
        }
    }

    /**
     * Load all
     * @param pageNumber Int number of the page
     * @param pageSize Int size of the page
     * @return List<Location> found
     */
    @Transactional
    override fun loadLocations(pageNumber: Int, pageSize: Int): List<Location> {
        val pageable = PageRequest.of(pageNumber, pageSize)
        val page = locationRepository.findAll(pageable).content
        return page.map { locationConverter.convert(it) }
    }
}