package com.agf.projetobase.service.impl;

import com.agf.projetobase.service.LocationService;
import com.agf.projetobase.domain.Location;
import com.agf.projetobase.repository.LocationRepository;
import com.agf.projetobase.repository.search.LocationSearchRepository;
import com.agf.projetobase.service.dto.LocationDTO;
import com.agf.projetobase.service.mapper.LocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Location}.
 */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    private final LocationSearchRepository locationSearchRepository;

    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper, LocationSearchRepository locationSearchRepository) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.locationSearchRepository = locationSearchRepository;
    }

    /**
     * Save a location.
     *
     * @param locationDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public LocationDTO save(LocationDTO locationDTO) {
        log.debug("Request to save Location : {}", locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        LocationDTO result = locationMapper.toDto(location);
        locationSearchRepository.save(location);
        return result;
    }

    /**
     * Get all the locations.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO> findAll() {
        log.debug("Request to get all Locations");
        return locationRepository.findAll().stream()
            .map(locationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one location by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LocationDTO> findOne(Long id) {
        log.debug("Request to get Location : {}", id);
        return locationRepository.findById(id)
            .map(locationMapper::toDto);
    }

    /**
     * Delete the location by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Location : {}", id);

        locationRepository.deleteById(id);
        locationSearchRepository.deleteById(id);
    }

    /**
     * Search for the location corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO> search(String query) {
        log.debug("Request to search Locations for query {}", query);
        return StreamSupport
            .stream(locationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(locationMapper::toDto)
        .collect(Collectors.toList());
    }
}
