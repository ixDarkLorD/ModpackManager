package net.ixdarklord.packmger.util;

import net.ixdarklord.packmger.core.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class WebUtils {
    public static String requestWebsiteData(String URL) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.addRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception ignored) {}
        return null;
    }
    public static boolean isValidURL(String link) {
        if (link != null && (link.startsWith("http://") || link.startsWith("https://"))) {
            try {
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.addRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestMethod("GET");
                con.setConnectTimeout(5000);
                int code = con.getResponseCode();
                if (code == 200) return true;
            } catch (Exception ignored) {
                System.out.println("Unable to check for valid link via GET request!");
                System.out.println("Trying alternative method..");
                try {
                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.addRequestProperty("User-Agent", "Chrome/105.0.0.0");
                    con.setConnectTimeout(5000);
                    int code = con.getResponseCode();
                    if (code == 200) return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    public static boolean isInternetReachable() {
        boolean isInternetReachable = false;
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            isInternetReachable = address.isReachable(5000);
        } catch (IOException ignored) {}
        return isInternetReachable;
    }
}
