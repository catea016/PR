package lab1;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Request extends Thread {
    public static volatile int threads = 0;
    private static ArrayList<String> routes = new ArrayList<String>();
    private static int usedThreads = 0;
    private static ArrayList<String> dataFromFiles = new ArrayList<>();

    private String url;

    //static int var = 0;
    public Request(String url) {
        this.url = url;
        threads++;
        //var++;
        //System.out.println("nr of threads" + var);
    }

    public Request() {}

    @Override
    public void run() {
        DataReader dataReader = new DataReader();

        String result = "";
        int responseCode = 200;
        try {
            //create new http connection and put the access token in HTTP header under the key X-Access-Token
            URL siteURL = new URL(url);
            HttpURLConnection http = (HttpURLConnection) siteURL.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("X-Access-Token", dataReader.getAccessToken());
            http.setConnectTimeout(3000);
            http.connect();

            responseCode = http.getResponseCode();
            if (responseCode == 200) {
                String data = dataReader.getUrlContents(http);
                //begin to read all routes
                if (dataReader.getRoutes(data) != null)
                    routes.addAll(dataReader.getRoutes(data));
                result = "  Response code: " + responseCode;
                System.out.println(url + result);
                if (dataReader.getData(data) != null) {
                    convertToJSON(dataReader.getFileType(data), dataReader.getData(data));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //initialize new thread for each route in links list and then decrement the nrOfThreads
        if (routes != null) {
            for (int i = usedThreads; i < routes.size(); i++) {
                new Request(routes.get(i)).start();
                usedThreads++;
            }
        }
        threads--;

    }

    public void convertToJSON(String type, String data) {

        if (type == "application/xml") {
            XMLtoJSON xmlConverter = new XMLtoJSON();
            dataFromFiles.add(xmlConverter.getJSONfromXML(data));
        } else if (type == "text/csv") {
            CSVtoJSON csvConverter = new CSVtoJSON();
            dataFromFiles.add(csvConverter.getJSONfromCSV(data));
        } else if (type == "application/x-yaml") {
            YAMLtoJSON yamlConverter = new YAMLtoJSON();
            dataFromFiles.add(yamlConverter.getJSONfromYAML(data));
        } else if (type == "json") {
            dataFromFiles.add(data);
        }
    }
    public ArrayList<String> getDataFromFiles() {
        return dataFromFiles;
    }

}
