package net.ixdarklord.packmger.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static net.ixdarklord.packmger.util.WebUtils.requestWebsiteData;

public class CurseAPI {
    private final int PROJECT_ID;
    private final String IDENTIFIER;

    public CurseAPI(int ProjectID, String Identifier) {
        this.PROJECT_ID = ProjectID;
        this.IDENTIFIER = Identifier;
    }

    public boolean isProjectSlugValid(String Identifier) {
        return IDENTIFIER.equals(Identifier);
    }

    public List<String> getProjectVersions(String minecraftVer, String modpackVer) {
        List<String> versionsList = new ArrayList<>();
        Map<Integer, JsonObject> filesList = getProjectRecentFiles();
        if (filesList != null) {
            for (var entry : filesList.entrySet()) {
                String gameVersions = entry.getValue().getAsJsonArray("gameVersions").toString().toLowerCase();
                String fileName = entry.getValue().get("fileName").getAsString();
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));

                String version;
                version = fileName.replaceAll(".*?((?<!\\wv)v\\d+([.-]\\d)*(\\w+)*([ /.-]\\w+)*).*", "$1");
                if (version.equals(fileName)) {
                    version = fileName.replaceAll(".*?((?<!\\w)\\d+([.-]\\d)*(\\w+)*([ /.-]\\w+)*).*", "$1");
                }

                if (gameVersions.contains(minecraftVer)) {
                    //System.out.printf("Original: %s | Edited: %s\n", fileName, version, gameVersions);
                    versionsList.add(version);
                    if (modpackVer.equals(version)) break;
                }
            }
        } else {
            return new ArrayList<>(Collections.singletonList("0.0.0"));
        }
        return versionsList;
    }

    public String getProjectSlug() {
        String JSONString = requestWebsiteData(CurseAPIWebsiteURL(PROJECT_ID, null));
        JsonObject JSONData = null;
        if (JSONString != null) {
            JSONData = JsonParser.parseString(JSONString).getAsJsonObject().getAsJsonObject("data");
        }
        if (JSONData == null) return "null";
        return JSONData.get("slug").getAsString();
    }
    public Map<Integer, JsonObject> getProjectRecentFiles() {
        String JSONString = requestWebsiteData(CurseAPIWebsiteURL(PROJECT_ID, "/files"));
        JsonArray JSONData = null;
        if (JSONString != null) {
            JSONData = JsonParser.parseString(JSONString).getAsJsonObject().getAsJsonArray("data");
        }

        Map<Integer, JsonObject> filesList = new HashMap<>();
        if (JSONData != null) {
            for (int i = 0; i < JSONData.size(); i++) {
                JsonObject file = JSONData.get(i).getAsJsonObject();
                filesList.put(i, file);
            }
        }
        if (filesList.isEmpty()) return null;

        return filesList;
    }

    private String CurseAPIWebsiteURL(int ProjectID, String URLExtends) {
        String URL = String.format("https://api.curse.tools/v1/cf/mods/%s", ProjectID);
        if (URLExtends != null && !URLExtends.isEmpty())
            URL = URL + URLExtends;
        return URL;
    }
}
