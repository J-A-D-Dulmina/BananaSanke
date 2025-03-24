package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import model.Game;

/**
 * Handles fetching game puzzles from an external API.
 */
public class BananaAPI {
    private static final String API_URL = "https://marcconrad.com/uob/banana/api.php?out=csv";

    /**
     * Fetches a random game puzzle.
     * @return A Game object containing the puzzle image URL and solution.
     * @throws MalformedURLException If the API response is invalid.
     */
    public Game getRandomGame() throws MalformedURLException {
        String dataRaw = readUrl(API_URL);
        System.out.println("API Response: " + dataRaw);

        if (dataRaw == null || dataRaw.isEmpty()) {
            System.out.println("Error: Empty API response");
            throw new MalformedURLException("API response is empty or null.");
        }

        String[] data = dataRaw.split(",");

        try {
            URL questionImageUrl = new URL(data[0].trim());
            int solution = Integer.parseInt(data[1].trim());
            System.out.println("Successfully parsed API response:");
            System.out.println("Image URL: " + questionImageUrl);
            System.out.println("Solution: " + solution);
            return new Game(questionImageUrl, solution);
        } catch (Exception e) {
            System.out.println("Error parsing API response: " + e.getMessage());
            throw new MalformedURLException("Error parsing API response: " + e.getMessage());
        }
    }

    /**
     * Reads a URL and returns its response.
     * @param urlString The URL to read from.
     * @return The API response as a String.
     */
    private String readUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("HTTP Error: " + responseCode);
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            }
        } catch (Exception e) {
            System.err.println("Error reading from URL: " + urlString);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
