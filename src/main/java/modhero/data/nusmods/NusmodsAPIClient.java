package modhero.data.nusmods;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Make HTTP GET requests to the NUSMods API
 */
public class NusmodsAPIClient {
    private static final Logger logger = Logger.getLogger(NusmodsAPIClient.class.getName());

    /**
     * Safely fetches module data by handling exceptions.
     * Logs any errors that occur during the fetch operation.
     *
     * @param acadYear The academic year in format "YYYY-YYYY" (e.g., "2025-2026").
     * @param moduleCode The module code (e.g., "CS2113").
     * @return The raw JSON response as a string, or null if an error occurs.
     */
    public String fetchModuleDataSafely(String acadYear, String moduleCode) {
        try {
            return fetchModuleData(acadYear, moduleCode);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to fetch module data", e);
            return null;
        }
    }

    /**
     * Fetches raw module data from the NUSMods API for a given academic year and module code.
     *
     * @param acadYear The academic year in format "YYYY-YYYY" (e.g., "2025-2026").
     * @param moduleCode The module code (e.g., "CS2113").
     * @return The raw JSON response as a string.
     * @throws Exception If the HTTP request fails, encounters network issues, or returns a non-200 status.
     */
    private String fetchModuleData(String acadYear, String moduleCode) throws Exception {
        String url = "https://api.nusmods.com/v2/" + acadYear + "/modules/" + moduleCode + ".json";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if (status != 200) {
            throw new Exception("Failed to fetch module data: HTTP " + status);
        }

        return response.body();
    }
}
