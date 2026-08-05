package com.kfg.gateway.web;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class AccessProbeController {

    @GetMapping("/customer/ping")
    Map<String, String> customerPing() {
        return Map.of("audience", "customer", "status", "ok");
    }

    @GetMapping("/employee/ping")
    Map<String, String> employeePing() {
        return Map.of("audience", "employee", "status", "ok");
    }
}
