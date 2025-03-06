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

        if (dataRaw == null || dataRaw.isEmpty()) {
            throw new MalformedURLException("API response is empty or null.");
        }

        System.out.println("API Response: " + dataRaw);

        String[] data = dataRaw.split(",");
        if (data.length < 2) {
            throw new MalformedURLException("Invalid response format from API.");
        }

        try {
            URL questionImageUrl = new URL(data[0].trim());
            int solution = Integer.parseInt(data[1].trim());

            return new Game(questionImageUrl, solution);
        } catch (Exception e) {
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
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString().trim();
        } catch (Exception e) {
            System.out.println("Error fetching data from API: " + e.getMessage());
            return null;
        }
    }
}
