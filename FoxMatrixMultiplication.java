package com.example.laba8;

public class FoxMatrixMultiplication {

    public static double[][] multiplyParallelFox(double[][] A, double[][] B, int q) {
        int n = A.length;
        double[][] C = new double[n][n];

        if (n % q != 0) {
            throw new IllegalArgumentException("Розмір матриці n повинен бути кратним q");
        }

        int m = n / q;
        int numThreads = q * q;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < q; i++) {
            for (int j = 0; j < q; j++) {
                final int rowBlock = i;
                final int colBlock = j;
                int threadIndex = i * q + j;

                threads[threadIndex] = new Thread(() -> {
                    for (int step = 0; step < q; step++) {
                        int k = (rowBlock + step) % q;

                        for (int r = 0; r < m; r++) {
                            for (int c = 0; c < m; c++) {
                                for (int inner = 0; inner < m; inner++) {
                                    int aRow = rowBlock * m + r;
                                    int aCol = k * m + inner;

                                    int bRow = k * m + inner;
                                    int bCol = colBlock * m + c;

                                    C[aRow][bCol] += A[aRow][aCol] * B[bRow][bCol];
                                }
                            }
                        }
                    }
                });
                threads[threadIndex].start();
            }
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return C;
    }
}
