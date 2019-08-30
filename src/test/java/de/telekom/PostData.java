package de.telekom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostData {
    //https://www.baeldung.com/httpurlconnection-post
    public static void main (String []args) throws IOException {
        URL url = new URL ("http://wholesaleordermanagement-tst-193.blw-02.eu-de.containers.appdomain.cloud/v1/gigabitAvailabilityInquiries");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String post = "{\"externalProcessIdAtCarrier\": \"12345f67890\",\"inquiryLocation\": {\"address\": {\"city\": \"TestCity\",\"cityPart\": \"GoodPart\",\"postcode\": \"110001\",\"streetName\": \"MyStreet\",\"streetNr\": \"1919\",\"streetNrSuffix\": \"d\"},\"klsId\": \"123\"}}";

        try(OutputStream os = con.getOutputStream()){
            byte[] input = post.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        int code = con.getResponseCode();
        System.out.println(code);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))){
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
    }
}
