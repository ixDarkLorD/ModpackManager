package net.ixdarklord.packmger.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.ixdarklord.packmger.util.WebUtils.requestWebsiteData;

public class ManagerUtils {
    public static List<String> getManifestValue(String key, String path) {
        String JSONString = requestWebsiteData(key);
        List<String> PATH = Arrays.asList(path.split("\\s*\\.\\s*"));
        assert JSONString != null : "JSONString is null!";
        JsonObject JSONData = JsonParser.parseString(JSONString).getAsJsonObject();
        JsonElement value = null;
        for (int i = 0; i < PATH.size(); i++) {
            switch (i) {
                case 0 -> JSONData = JSONData.get(PATH.get(i)).getAsJsonObject();
                case 1 -> value = JSONData.get(PATH.get(i));
            }
        }
        assert value != null : "Value is null!";
        return Collections.singletonList(value.getAsString());
    }
}
