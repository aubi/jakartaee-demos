package fish.payara.sampledatagenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Robot data generator.
 *
 * @author Petr Aubrecht <petr.aubrecht@payara.fish>
 */
public class SampleDataGenerator {
    private static final int CONNECTION_TIMEOUT = 3;

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private static final Random rnd = new Random();

    public static void main(String[] args) {
        System.out.println("Hi, I'm robot Karel and I'm producing data about my motor!");
        if (args.length < 2) {
            System.out.println("Usage java -jar SampleDataGenerator.jar WORKERS PATH URL");
            System.out.println("WORKERS number of threads to produce data");
            System.out.println("PATH should contain directories with testing CVS data (/robot-predictive-data/training_data)");
            System.out.println("URL is the edge processing url, e.g. https://localhost:8080/SampleEdgeController/api/");
        } else {
            createWorkers(Integer.parseInt(args[0]), args[1], args[2]);
        }
    }

    private static void createWorkers(int workers, String pathRoot, String baseUrl) {
//        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
        try (ExecutorService executor = Executors.newFixedThreadPool(workers)) {
            for (int i = 0; i < workers; i++) {
                final int workerId = i;
                executor.submit(() -> generateData(workerId, pathRoot, baseUrl));
                System.out.println("Robot #" + i + " started.");
            }
        }
    }

    private static void generateData(int workerId, String pathRoot, String baseUrl) {
        try {
            List<DataRow> data = loadData(pathRoot);
            for (int i = 0; i < 1_000_000; i++) {
                //System.out.printf("Worker #%3d, %6d: Sending data, round %,d%n", workerId, data.get(0).sourceId(), i);
                sendData(workerId, i, data, baseUrl);
            }
        } catch (IOException e) {
            System.err.println("Cannot generate data: " + e.getMessage());
        }
    }

    private static List<DataRow> loadData(String pathRoot) throws IOException {
        int sourceId = rnd.nextInt(1_000_000);
        // there is a list of directoris in the training data
        File[] dirs = new File(pathRoot)
                .listFiles(f -> f.isDirectory());
        // pick one directory
        File selectedDir = dirs[rnd.nextInt(dirs.length)];
        // read all CVS files
        File[] csvs = selectedDir.listFiles((File dir, String name) -> name.toLowerCase().endsWith("csv"));
        File selectedFile = csvs[rnd.nextInt(csvs.length)];
        try (Scanner scanner = new Scanner(selectedFile)) {
            List<DataRow> data = new ArrayList<>();
            scanner.nextLine(); // skip header line
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");
                DataRow dataRow = new DataRow(
                        sourceId,
                        Double.parseDouble(fields[0]),
                        Integer.parseInt(fields[1]),
                        Integer.parseInt(fields[2]),
                        Integer.parseInt(fields[3])
                );
                data.add(dataRow);
            }
            return data;
        } catch (FileNotFoundException ex) {
            throw new IOException("Unable to read file " + selectedFile.getAbsolutePath(), ex);
        }
    }

    private static void sendData(int workerId, int round, List<DataRow> data, String baseUrl) throws IOException {
        String urlPing = baseUrl + "/data/ping";
        String urlData = baseUrl + "/data";
        String urlProcess = urlData + "/process";
        try (HttpClient client = HttpClient.newBuilder()
        .executor(executor)
        .version(HttpClient.Version.HTTP_1_1)
                        .build()) {

            ObjectMapper objectMapper = new ObjectMapper();

            int sourceId = data.get(0).sourceId();

            // make the beginning of the jobs a little bit randomly distributed
            Thread.sleep(Duration.ofMillis(rnd.nextLong(10_000)));

            System.out.printf("#%3d, %6d: Checking availability of the server%n", workerId, sourceId);

            boolean ready = false;
            while (!ready) {
                HttpRequest pingRequest = HttpRequest.newBuilder()
                        .uri(new URI(urlPing))
                        .timeout(Duration.ofSeconds(CONNECTION_TIMEOUT))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();
                int pingStatus = -1;
                try {
                    pingStatus = client.send(pingRequest, HttpResponse.BodyHandlers.ofString())
                            .statusCode();
                } catch (ConnectException ex) {
                    // we don't care what exception
                }
                if (pingStatus == HttpURLConnection.HTTP_OK) {
                    System.out.printf("#%3d, %6d: Server available%n", workerId, sourceId);
                    ready = true;
                } else {
                    System.out.printf("#%3d, %6d: Server NOT available, waiting...%n", workerId, sourceId);
                    Thread.sleep(Duration.ofMillis(1_000));
                }
            }

            // send data
            System.out.printf("#%3d, %6d: Sending %d records, round %,d%n", workerId, sourceId, data.size(), round);
            int i = 0;
            for (DataRow dataRow : data) {
                i++;
                sourceId = dataRow.sourceId();
                String json = objectMapper.writeValueAsString(dataRow);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(urlData))
                        .header("Content-Type", "application/json") // Set the content type
                        .POST(HttpRequest.BodyPublishers.ofString(json)) // This is the default method
                        .build();

                // Send the request and get the response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                    System.err.printf("Error! Code: %d%n", response.statusCode());
                } else {
                    if (i % 10 == 0) {
                        System.out.printf("#%3d, %6d: Data %,5d/%,5d sent, round %,d%n", workerId, sourceId, i, data.size(), round);
                    }
                }
                // Print the response
                // System.out.println("Response Code: " + response.statusCode());
                //System.out.println("Response Body: " + response.body());

                // there is a delay between data sent
                //Thread.sleep(Duration.ofMillis(100 + rnd.nextLong(30)));
            }

            {
                // end up the block of data, send to analysis
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(urlProcess))
                        .header("Content-Type", "application/json") // Set the content type
                        .POST(HttpRequest.BodyPublishers.ofString(Integer.toString(sourceId))) // This is the default method
                        .build();

                // Send the request and get the response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                    System.err.printf("Error! Code: %d%n", response.statusCode());
                    // System.err.printf("Error! Code: %d, message: %s%n", response.statusCode(), response.body());
                } else {
                    System.out.printf("#%3d, %6d: Analysis request sent, round %,d%n", workerId, sourceId, round);
                }
            }

        } catch (IOException | InterruptedException | URISyntaxException e) {
            //throw new IOException("Unable to send data to " + url, e);
            System.err.println("Error: " + e.getMessage());
            try {
                Thread.sleep(Duration.ofSeconds(2));
            } catch (InterruptedException ex) {
                // ignoring
            }
        }
    }

    public static record DataRow(int sourceId, double time, int position, int temperature, int label) {

    }
}
