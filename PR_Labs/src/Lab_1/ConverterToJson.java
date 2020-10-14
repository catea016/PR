package Lab_1;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.*;
import java.util.*;

public class ConverterToJson {

    String convertXMLtoJSON(String xml) {
        int INDENT_FACTOR = 4;
        try {
            JSONObject jsonObj = XML.toJSONObject(xml);
            return jsonObj.toString(INDENT_FACTOR);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    String convertCSVtoJSON(String csv) {
        try {
            CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
            CsvMapper csvMapper = new CsvMapper();
            List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(csv).readAll();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    String convertYamlToJson(String yamlString) {
        try {
            ObjectMapper yamlReader;
            yamlReader = new ObjectMapper(new YAMLFactory());
            yamlReader.findAndRegisterModules();
            Object obj = yamlReader.readValue(yamlString, Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();

            return jsonWriter.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
