package updateapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class GitHubReleaseGUI {

    private static String downloadUrl = "";
    private static String releasePageUrl = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GitHubReleaseGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GitHub Release Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JTextField repoField = new JTextField("https://github.com/marcheloBM/ActualizacionAPP");
        JButton fetchButton = new JButton("Consultar Release");
        JButton downloadButton = new JButton("Descargar archivo");
        JButton openGitHubButton = new JButton("Ver en GitHub");

        downloadButton.setEnabled(false);
        openGitHubButton.setEnabled(false);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        fetchButton.addActionListener((ActionEvent e) -> {
            String repoUrl = repoField.getText().trim();
            outputArea.setText("Consultando...\n");
            downloadButton.setEnabled(false);
            openGitHubButton.setEnabled(false);
            downloadUrl = "";
            releasePageUrl = "";

            new Thread(() -> {
                String result = fetchReleaseInfo(repoUrl);
                SwingUtilities.invokeLater(() -> {
                    outputArea.setText(result);
                    downloadButton.setEnabled(!downloadUrl.isEmpty());
                    openGitHubButton.setEnabled(!releasePageUrl.isEmpty());
                });
            }).start();
        });

        downloadButton.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URL(downloadUrl).toURI());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Error al abrir el enlace de descarga.");
            }
        });

        openGitHubButton.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URL(releasePageUrl).toURI());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Error al abrir la página del release.");
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(repoField, BorderLayout.CENTER);
        topPanel.add(fetchButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(openGitHubButton);
        bottomPanel.add(downloadButton);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static String fetchReleaseInfo(String repoUrl) {
        try {
            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) return "❌ URL inválida.";

            String owner = parts[0];
            String repo = parts[1];
            String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases";

            URL url = new URL(apiUrl);
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
            if (jsonText.startsWith("[]")) return "⚠️ El repositorio no tiene releases publicados.";

            String tag = extractValue(jsonText, "\"tag_name\":\"");
            String description = extractValue(jsonText, "\"body\":\"");
            String date = extractValue(jsonText, "\"published_at\":\"");
            String htmlUrl = extractValue(jsonText, "\"html_url\":\"");

            releasePageUrl = htmlUrl;

            StringBuilder result = new StringBuilder();
            result.append("Repositorio: ").append(repo).append("\n");
            result.append("Versión: ").append(tag.isEmpty() ? "—" : tag).append("\n");
            result.append("Descripción: ").append(description.isEmpty() ? "Sin descripción" : description).append("\n");
            result.append("Fecha: ").append(date.isEmpty() ? "—" : date).append("\n");
            result.append("Release: ").append(htmlUrl.isEmpty() ? "—" : htmlUrl).append("\n");

            int assetBlockStart = jsonText.indexOf("\"assets\":[");
            if (assetBlockStart != -1) {
                String assetName = extractValue(jsonText, "\"name\":\"");
                String assetUrl = extractValue(jsonText, "\"browser_download_url\":\"");
                if (!assetUrl.isEmpty()) {
                    result.append("Archivo disponible: ").append(assetName).append("\n");
                    result.append("Descarga directa: ").append(assetUrl).append("\n");
                    downloadUrl = assetUrl;
                } else {
                    result.append("No hay archivos disponibles para descarga.\n");
                }
            } else {
                result.append("No se encontraron assets en el release.\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    private static String extractValue(String json, String key) {
        int start = json.indexOf(key);
        if (start == -1) return "";
        start += key.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }
}
