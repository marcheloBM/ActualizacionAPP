/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package updateapp;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;


/**
 *
 * @author march
 */
public class GitHubReleasePublic {

    public static void main(String[] args) {
        String repoUrl = "https://github.com/marcheloBM/ActualizacionAPP";

        // Extraer owner y repo
        String[] parts = repoUrl.replace("https://github.com/", "").split("/");
        String owner = parts[0];
        String repo = parts[1];

        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases";

        try {
            URI uri = new URI(apiUrl);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            String jsonText = json.toString();

            // Validar si hay releases
            if (jsonText.startsWith("[]")) {
                System.out.println("⚠️ El repositorio no tiene releases publicados.");
                return;
            }

            // Extraer campos directamente del JSON completo
            String tag = extractValue(jsonText, "\"tag_name\":\"");
            String description = extractValue(jsonText, "\"body\":\"");
            String date = extractValue(jsonText, "\"published_at\":\"");
            String htmlUrl = extractValue(jsonText, "\"html_url\":\"");

            System.out.println("Repositorio: " + repo);
            System.out.println("Versión: " + (tag.isEmpty() ? "—" : tag));
            System.out.println("Descripción: " + (description.isEmpty() ? "Sin descripción" : description));
            System.out.println("Fecha: " + (date.isEmpty() ? "—" : date));
            System.out.println("Release: " + (htmlUrl.isEmpty() ? "—" : htmlUrl));

            // Buscar assets
            int assetBlockStart = jsonText.indexOf("\"assets\":[");
            if (assetBlockStart != -1) {
                String assetName = extractValue(jsonText, "\"name\":\"");
                String assetUrl = extractValue(jsonText, "\"browser_download_url\":\"");

                if (!assetUrl.isEmpty()) {
                    System.out.println("Archivo disponible: " + assetName);
                    System.out.println("Descarga directa: " + assetUrl);
                } else {
                    System.out.println("No hay archivos disponibles para descarga.");
                }
            } else {
                System.out.println("No se encontraron assets en el release.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    // Método para extraer valores entre comillas
    private static String extractValue(String json, String key) {
        int start = json.indexOf(key);
        if (start == -1) return "";
        start += key.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }
}
