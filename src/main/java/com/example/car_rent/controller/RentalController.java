package com.example.car_rent.controller;

import com.example.car_rent.dto.RentalRequest;
import com.example.car_rent.model.Rental;
import com.example.car_rent.service.RentalService;
import com.example.car_rent.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;
    private Logger logger;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
        this.logger = Logger.getLogger("test_rental_logger");
    }

    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalService.findAll();
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(@RequestBody RentalRequest rentalRequest) {
        if (rentalRequest.vehicleId == null || rentalRequest.userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Rental rental = rentalService.rent(rentalRequest.vehicleId, rentalRequest.userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(rental);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/return")
    public ResponseEntity<Boolean> returnVehicle(@RequestBody RentalRequest rentalRequest) {
        if (rentalRequest.vehicleId == null || rentalRequest.userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            if (rentalService.returnRental(rentalRequest.vehicleId, rentalRequest.userId)) {
                return ResponseEntity.status(HttpStatus.OK).body(true); // można coś innego, żeby bardziej sensowne to było, ale na chile obecną raczej git
            }
            else return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
