package com.example.car_rent.service.impl;

import com.example.car_rent.model.Vehicle;
import com.example.car_rent.repository.RentalRepository;
import com.example.car_rent.repository.VehicleRepository;
import com.example.car_rent.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              RentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAll () {
        return vehicleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAllActive() {
        return vehicleRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findById(id);
    }

    @Override
    @Transactional
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
            vehicle.setActive(true);
        }
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return savedVehicle;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findByIsActiveTrueAndIdNotIn(rentalRepository.findRentedVehicleIds());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findRentedVehicles() {
        return vehicleRepository.findByIsActiveTrueAndIdIn(rentalRepository.findRentedVehicleIds());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(String vehicleId) {
        return vehicleRepository.findByIdAndIsActiveTrue(vehicleId).isPresent() &&
                (rentalRepository.existsByVehicleIdAndReturnDateIsNotNull(vehicleId) ||
                rentalRepository.findByVehicleId(vehicleId).isEmpty());
    }

    @Override
    @Transactional
    public Optional<Vehicle> deleteById(String id) { // should be soft delete
        Optional<Vehicle> toDelete = vehicleRepository.findById(id);
        if (toDelete.isPresent()) {
            toDelete.get().setActive(false);
            return toDelete;
        }
        else return Optional.empty();
//        toDelete.ifPresent(vehicle -> vehicle.setActive(false));
    }
}