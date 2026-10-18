package com.andesstay.bff.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    // Método auxiliar para inyectar el token en la cabecera
    private HttpEntity<Object> createHttpEntityWithAuth(Object body, String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null) {
            headers.set("Authorization", authHeader);
        }
        return new HttpEntity<>(body, headers);
    }

    // Listar reservas delegando al microservicio 8081 con el token
    @GetMapping("/reservations")
    public ResponseEntity<?> getReservations(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        HttpEntity<Object> entity = createHttpEntityWithAuth(null, authHeader);
        return restTemplate.exchange(reservationsUrl, HttpMethod.GET, entity, Object.class);
    }

    // Crear reserva delegando al microservicio 8081 con el token
    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(@RequestBody Object payload, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        HttpEntity<Object> entity = createHttpEntityWithAuth(payload, authHeader);
        return restTemplate.exchange(reservationsUrl, HttpMethod.POST, entity, Object.class);
    }

    // Actualizar estado delegando al microservicio 8081 con el token
    @PutMapping("/reservations/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Object body, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String url = reservationsUrl + "/" + id + "/status";
        HttpEntity<Object> entity = createHttpEntityWithAuth(body, authHeader);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
    }
}