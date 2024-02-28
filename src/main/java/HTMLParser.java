import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class HTMLParser {
    static String loadExistingHTMLContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    static String appendToExistingHTML(String existingHTMLContent, JsonObject bpiObject) {
        Date timestamp = new Date();
        Document document = Jsoup.parse(existingHTMLContent);
        Element table = document.selectFirst("tbody");

        for (Map.Entry<String, JsonElement> entry : bpiObject.entrySet()) {
            String currency = entry.getKey();
            String rate = entry.getValue().getAsJsonObject().get("rate").getAsString();
            Element trEntry = document.createElement("tr");
            trEntry.appendChild(createTdElement(document, currency));
            trEntry.appendChild(createTdElement(document, rate));
            table.appendChild(trEntry);
        }

        document.selectFirst("p").text("Last Updated: " + formatDate(timestamp));

        return document.outerHtml();
    }

    static String generateHTML(JsonObject bpiObject) {
        Date timestamp = new Date();
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Bitcoin Price Report</title><link href=\"https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css\" rel=\"stylesheet\"></head><body><div class=\"container mx-auto\"><h1 class=\"text-3xl font-bold my-4\">Bitcoin Price Report</h1><p>Last Updated: " + formatDate(timestamp) + "</p>");
        htmlContent.append("<table class=\"table-auto w-full\"><thead><tr><th class=\"border px-4 py-2\">Currency</th><th class=\"border px-4 py-2\">Rate</th></tr></thead><tbody>");

        for (Map.Entry<String, JsonElement> entry : bpiObject.entrySet()) {
            String currency = entry.getKey();
            String rate = entry.getValue().getAsJsonObject().get("rate").getAsString();
            htmlContent.append("<tr><td class=\"border px-4 py-2\">").append(currency).append("</td><td class=\"border px-4 py-2\">").append(rate).append("</td></tr>");
        }

        htmlContent.append("</tbody></table></div></body></html>");
        return htmlContent.toString();
    }

    static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    static Element createTdElement(Document document, String text) {
        Element td = document.createElement("td");
        td.addClass("border px-4 py-2");
        td.text(text);
        return td;
    }

    static void writeHTMLToFile(String content, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
            System.out.println("HTML report generated successfully: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
