package lab1;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataReader {

    public static String accessToken;

    public static String getAccessToken() {
        DataReader dataReader = new DataReader();
        try {
            URL url = new URL("http://localhost:5000/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                JsonParser parser = new JsonParser();
                String data = dataReader.getUrlContents(conn);
                accessToken = parser.parse(data).getAsJsonObject().get("access_token").getAsString();

            } else {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public static String getUrlContents(HttpURLConnection httpURLConnection) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            while ((inputLine = input.readLine()) != null) {
                content.append(inputLine);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public ArrayList getRoutes(String data) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(data).getAsJsonObject().get("link");
        ArrayList<String> routes = new ArrayList();
        Gson gson = new Gson();
        String json = "";
        if (element != null) {
            json = element.toString();
        } else return null;
        Map<String, Object> map = new HashMap<String, Object>();
        map = (Map<String, Object>) gson.fromJson(json, map.getClass());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            routes.add("http://localhost:5000" + entry.getValue());
        }
        return routes;
    }

    public String getData(String data) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(data).getAsJsonObject().get("data");
        if (jsonElement != null) {
            return jsonElement.getAsString();
        } else return null;
    }

    public String getFileType(String data) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(data).getAsJsonObject().get("mime_type");
        if (jsonElement != null) {
            return jsonElement.getAsString();
        }
        return "json";
    }
}
