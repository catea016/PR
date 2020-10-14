package Lab_1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParseJson {

    ArrayList<String> getValuesForGivenKey(List<String> jsonList, String key) {
        JSONArray jsonArray = null;
        ArrayList<String> jsonValueList = new ArrayList<>();
        for (String jsonArrayStr: jsonList) {
            try {
                //Convert string with index in brackets to JSON array
                jsonArrayStr = jsonArrayStr.substring(jsonArrayStr.indexOf("["), jsonArrayStr.indexOf("]") + 1);
                jsonArray = new JSONArray(jsonArrayStr);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    if (!jsonObject.optString(key).equals(""))
                        jsonValueList.add(jsonObject.optString(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        Collections.sort(jsonValueList);
        return jsonValueList;
    }

    ArrayList<String> getJsonForGivenValue(List<String> jsonList, String key, String value) {
        JSONArray jsonArray = null;
        ArrayList<String> jsonValueList = new ArrayList<>();
        boolean hasAsterisks = value.endsWith("*");
        if (hasAsterisks) {
            value = value.substring(0, value.length() - 1);
        }
        for (String jsonArrayStr : jsonList) {
            try {
                jsonArrayStr = jsonArrayStr.substring(jsonArrayStr.indexOf("["), jsonArrayStr.indexOf("]") + 1);
                jsonArray = new JSONArray(jsonArrayStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (hasAsterisks) {
                        if (jsonObject.optString(key).startsWith(value)) {
                            jsonValueList.add(jsonObject.toString());
                        }
                    } else {
                        if (jsonObject.optString(key).equals(value)) {
                            jsonValueList.add(jsonObject.toString());
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        Collections.sort(jsonValueList);
        return jsonValueList;
    }
}
