package com.example.laba8;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class MatrixService {

    public CalculationResult calculateFromClient(double[][] A, double[][] B, String algorithm, int param) {
        return performCalculation(A, B, algorithm, param);
    }

    public CalculationResult calculateOnServer(int size, String algorithm, int param) {
        double[][] A = generateMatrix(size);
        double[][] B = generateMatrix(size);
        return performCalculation(A, B, algorithm, param);
    }

    private CalculationResult performCalculation(double[][] A, double[][] B, String algorithm, int param) {
        long startTime = System.nanoTime();


        if ("fox".equalsIgnoreCase(algorithm)) {
            FoxMatrixMultiplication.multiplyParallelFox(A, B, param);
        } else {
            throw new IllegalArgumentException("Невідомий алгоритм: " + algorithm);
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        return new CalculationResult(durationMs, "Успішно виконано алгоритм " + algorithm);
    }

    private double[][] generateMatrix(int size) {
        double[][] matrix = new double[size][size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rand.nextDouble() * 10;
            }
        }
        return matrix;
    }

}