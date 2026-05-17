package com.example.laba8;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class PerformanceTesterClient {

    private static final String SERVER_URL = "http://localhost:8080/api/matrix";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {

        int[] sizes = {200, 400, 600, 1000, 1600};
        String algorithm = "fox";
        int param = 2;

        System.out.println("ЕТАП 1: Прогрів JVM та мережі (зачекайте кілька секунд)");
        int warmupIterations = 3;
        int warmupSize = 100;

        for (int i = 1; i <= warmupIterations; i++) {
            System.out.print("Прогрів ітерація " + i + "... ");
            testServerData(warmupSize, algorithm, param, false);
            testClientData(warmupSize, algorithm, param, false);
            System.out.println("Готово.");
        }
        System.out.println("Прогрів успішно завершено!\n");


        System.out.println("ЕТАП 2: Початок дослідження продуктивності");

        for (int size : sizes) {
            System.out.println("\nТестування для матриці " + size + "x" + size);

            testServerData(size, algorithm, param, true);
            testClientData(size, algorithm, param, true);
        }
    }

    private static void testServerData(int size, String algo, int param, boolean printOutput) throws Exception {
        ServerDataRequest reqObj = new ServerDataRequest(size, algo, param);
        String json = mapper.writeValueAsString(reqObj);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/server-data"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        long startNetTime = System.currentTimeMillis();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        long totalRoundTripTime = System.currentTimeMillis() - startNetTime;

        CalculationResult result = mapper.readValue(response.body(), CalculationResult.class);

        if (printOutput) {
            System.out.println("[Дані на сервері]");
            System.out.println("  Час обчислення на сервері: " + result.serverComputeTimeMs() + " мс");
            System.out.println("  Загальний час (з мережею): " + totalRoundTripTime + " мс");
        }
    }

    private static void testClientData(int size, String algo, int param, boolean printOutput) throws Exception {
        double[][] a = generateMatrix(size);
        double[][] b = generateMatrix(size);

        ClientDataRequest reqObj = new ClientDataRequest(a, b, algo, param);
        String json = mapper.writeValueAsString(reqObj);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/client-data"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        long startNetTime = System.currentTimeMillis();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        long totalRoundTripTime = System.currentTimeMillis() - startNetTime;

        CalculationResult result = mapper.readValue(response.body(), CalculationResult.class);

        if (printOutput) {
            System.out.println("[Дані на клієнті]");
            System.out.println("  Розмір JSON: " + (json.length() / 1024 / 1024.0) + " MB");
            System.out.println("  Час обчислення на сервері: " + result.serverComputeTimeMs() + " мс");
            System.out.println("  Загальний час (серіалізація + мережа + обчислення): " + totalRoundTripTime + " мс");
        }
    }

    private static double[][] generateMatrix(int size) {
        double[][] matrix = new double[size][size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rand.nextDouble();
            }
        }
        return matrix;
    }
}