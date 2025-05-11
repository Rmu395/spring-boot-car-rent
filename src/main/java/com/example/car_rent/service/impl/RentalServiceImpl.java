package com.example.car_rent.service.impl;

import com.example.car_rent.model.Rental;
import com.example.car_rent.model.User;
import com.example.car_rent.model.Vehicle;
import com.example.car_rent.repository.RentalRepository;
import com.example.car_rent.repository.UserRepository;
import com.example.car_rent.repository.VehicleRepository;
import com.example.car_rent.service.RentalService;
import com.example.car_rent.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RentalServiceImpl implements RentalService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleService vehicleService;

    @Autowired
    public RentalServiceImpl(VehicleRepository vehicleRepository,
                             RentalRepository rentalRepository,
                             UserRepository userRepository,
                             VehicleService vehicleService) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleService = vehicleService;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.existsByVehicleIdAndReturnDateIsNull(vehicleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByVehicleId(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
    }

    @Override
    @Transactional
    public Rental rent(String vehicleId, String userId) {
        if (!vehicleService.isAvailable(vehicleId)) {
            throw new IllegalStateException("Vehicle " + vehicleId + " is not available for rent.");
        }
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle consistency error. ID: " + vehicleId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    return new EntityNotFoundException("User not found with ID: " + userId);
                });
        Rental newRental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .user(user)
                .rentDate(LocalDateTime.now())
                .returnDate(null)
                .build();
        Rental savedRental = rentalRepository.save(newRental);
        return savedRental;
    }

    @Override
    @Transactional
    public boolean returnRental(String vehicleId, String userId) {
        Optional<Rental> returningRental = rentalRepository.findByVehicleIdAndUserIdAndReturnDateIsNull(vehicleId, userId);
        if (returningRental.isPresent()) {
            returningRental.get().setReturnDate(LocalDateTime.now());
            return true;
        } else return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
}
