import org.quartz.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class CoinDeskSyncJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            File htmlFile = new File("bitcoin_price_report.html");
            boolean fileExists = htmlFile.exists();

            String existingHTMLContent = null;
            if (fileExists) {
                existingHTMLContent = HTMLParser.loadExistingHTMLContent(htmlFile);
            }

            URL url = new URL("https://api.coindesk.com/v1/bpi/currentprice.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonObject bpiObject = jsonObject.getAsJsonObject("bpi");

                String htmlContent;
                if (fileExists) {
                    htmlContent = HTMLParser.appendToExistingHTML(existingHTMLContent, bpiObject);
                } else {
                    htmlContent = HTMLParser.generateHTML(bpiObject);
                }

                HTMLParser.writeHTMLToFile(htmlContent, "bitcoin_price_report.html");
            } else {
                System.err.println("Failed to fetch data from CoinDesk API. Response code: " + responseCode);
            }

            connection.disconnect();

            System.out.println("Bitcoin price synchronization job completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
