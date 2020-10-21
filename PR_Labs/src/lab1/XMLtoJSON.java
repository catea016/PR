package lab1;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class XMLtoJSON {
    public String getJSONfromXML(String xml) {
        int PRETTY_PRINT_INDENT_FACTOR = 4;
        try {
            JSONObject xmlJSONObj = XML.toJSONObject(xml);
            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            return jsonPrettyPrintString;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;

        }
    }
}
