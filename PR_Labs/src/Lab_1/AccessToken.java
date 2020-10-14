package Lab_1;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccessToken {
    private static String accessToken = "";
    private static final String serverRegisterURL = "http://localhost:5000/register";

    public String getAccessToken() {
        return accessToken;
    }

    void requestAccessToken() {
        RequestData requestData = new RequestData();
        try {
            String data = "";
            URL url = new URL(serverRegisterURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200)
                throw new RuntimeException("httpURLConnection: " + responseCode);
            else {
                data = requestData.readData(httpURLConnection);
                JsonParser parser = new JsonParser();
                accessToken = parser.parse(data).getAsJsonObject().get("access_token").getAsString();
                //System.out.println(accessToken);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
