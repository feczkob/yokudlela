package com.yokudlela.kitchen.business.model

import com.yokudlela.kitchen.business.Constants
import com.yokudlela.kitchen.business.exception.BusinessException
import com.yokudlela.kitchen.business.handler.LocationHandler
import lombok.NoArgsConstructor
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import java.io.Serializable

@NoArgsConstructor
class Location @JvmOverloads constructor(
    @Transient
    private val locationHandler: LocationHandler,
    var id: Int? = null,
    var name: String = "",
    var details: String? = null,
    var orders: MutableList<Order>? = mutableListOf(),
) : Serializable {

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    /**
     * Add location
     * @return Location added
     */
    @Caching(
        put = [CachePut(cacheNames = [Constants.CACHE_LOCATION], key = "#result.id")],
        evict = [CacheEvict(cacheNames = [Constants.CACHE_LOCATION + Constants.CACHE_ALL], allEntries = true)]
    )
    fun addLocation(): Location {
        return save()
    }

    /**
     * Load location
     * @param locationId Int id
     * @return Location loaded
     */
    @Cacheable(cacheNames = [Constants.CACHE_LOCATION], key = "#locationId")
    fun loadLocation(locationId: Int): Location {
        // ! good to know that the return value of this method is a proxy
        return locationHandler.loadLocation(locationId)
    }

    /**
     * Change or add location
     * If the location is not present, it'll be added
     * @param locationId Int id
     * @return Int id of the modified / newly created location
     */
    @Caching(
        put = [CachePut(cacheNames = [Constants.CACHE_LOCATION], key = "#result.id")],
        evict = [CacheEvict(cacheNames = [Constants.CACHE_LOCATION + Constants.CACHE_ALL], allEntries = true)]
    )
    fun changeOrAddLocation(locationId: Int): Location {
        val locationFound: Location = try {
            loadLocation(locationId)
        } catch (e: BusinessException) {
            // TODO check if type is MISSING_ENTITY or not
            return addLocation()
        }
        mergeLocation(locationFound)
        return save()
    }

    fun modify(existing: Location) {
        mergeLocation(existing)
        save()
    }

    /**
     * Delete location
     * @param locationId Int id
     */
    @Caching(
        evict = [CacheEvict(cacheNames = [Constants.CACHE_LOCATION], key = "#locationId"),
            CacheEvict(cacheNames = [Constants.CACHE_LOCATION + Constants.CACHE_ALL], allEntries = true)]
    )
    fun deleteLocation(locationId: Int) {
        locationHandler.deleteLocation(locationId)
    }

    /**
     * Load locations
     * @param pageNumber Int number of the page
     * @param pageSize Int size of the page
     * @return List<Location> found
     */
    @Cacheable(cacheNames = [Constants.CACHE_LOCATION + Constants.CACHE_ALL])
    fun loadLocations(pageNumber: Int, pageSize: Int): List<Location> {
        return locationHandler.loadLocations(pageNumber, pageSize)
    }

    /**
     * Merging this location with another
     * @param old Location the old
     * @return Location saved location
     */
    private fun mergeLocation(old: Location) {
        this.id = old.id
        this.orders = old.orders
    }

    /**
     * Save
     * @return Location saved
     */
    private fun save(): Location {
        return locationHandler.saveLocation(this)
    }
}