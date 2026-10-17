package com.andesstay.bff.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/bff")
public class BffReservationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${reservations.service.url:http://localhost:8081/api/reservations}")
    private String reservationsUrl;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "ms-andesstay-bff"));
    }

    // Listar reservas delegando al microservicio 8081
    @GetMapping("/reservations")
    public ResponseEntity<?> getReservations() {
        return restTemplate.getForEntity(reservationsUrl, Object.class);
    }

    // Crear reserva delegando al microservicio 8081
    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(@RequestBody Object payload) {
        return restTemplate.postForEntity(reservationsUrl, payload, Object.class);
    }

    // Actualizar estado (CONFIRMADA, etc.)
    @PutMapping("/reservations/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Object body) {
        String url = reservationsUrl + "/" + id + "/status";
        restTemplate.put(url, body);
        return ResponseEntity.ok().build();
    }
}