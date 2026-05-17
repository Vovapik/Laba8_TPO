package com.example.laba8;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matrix")
public class MatrixController {

    private final MatrixService matrixService;

    public MatrixController(MatrixService matrixService) {
        this.matrixService = matrixService;
    }

    @PostMapping("/client-data")
    public CalculationResult withClientData(@RequestBody ClientDataRequest request) {
        return matrixService.calculateFromClient(request.a(), request.b(), request.algorithm(), request.param());
    }

    @PostMapping("/server-data")
    public CalculationResult withServerData(@RequestBody ServerDataRequest request) {
        return matrixService.calculateOnServer(request.size(), request.algorithm(), request.param());
    }
}