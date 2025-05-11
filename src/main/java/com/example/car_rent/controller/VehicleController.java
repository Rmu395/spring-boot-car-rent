package com.example.car_rent.controller;

import com.example.car_rent.model.Vehicle;
import com.example.car_rent.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    private Logger logger;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
        this.logger = Logger.getLogger("test_vehicle_logger");
    }

    @GetMapping // Mapowanie GET na główny URL /api/vehicles
    public List<Vehicle> getAllVehicles() {
        return vehicleService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        logger.info("Request received for vehicle with ID: " + id);
        logger.info("Returned thing: " + vehicleService.findById(id));
        return vehicleService.findById(id)
                .map(vehicle -> {
                    return ResponseEntity.ok(vehicle);  // 200 OK z obiektem Vehicle
                })
                .orElseGet(() -> {
                    return ResponseEntity.notFound().build();   //404
                });
    }

    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        try {
            Vehicle savedVehicle = vehicleService.save(vehicle);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
