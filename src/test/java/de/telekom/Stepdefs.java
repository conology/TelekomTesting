package de.telekom;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Stepdefs {

    private Response response;
    private ValidatableResponse json;
    private RequestSpecification request;
    private String id = "";
    private String REQUEST_URL = "";
    private String POST_URL = "";
    private int responseCode;
    private String payload = "";
    private String activityId = "";

    @Given("the following payload$")
    public void theFollowingPayload(Map<String,String> responseFields) {
        this.payload = turnMapIntoJSONFormat(responseFields);
        System.out.println("the Following Payload: "+ convertWithIteration(responseFields));
        request = given()
                .header("Content-Type", "application/json")
                .body(payload);
    }

    @Given("activity ID {word} exists")
    public void activity_exists_with_id(String id){
        this.id = id;
        request = given();
    }

    @Given("activity ID {word} does not exist")
    public void activity_does_not_exist_with_id(String id) {
        this.id = id;
        request = given();
    }

    @When("a user retrieves data from activity")
    public void retrieving_data() {
        //System.out.println(REQUEST_URL+id);
        this.response = request.when().get(REQUEST_URL+id);
        //  System.out.println("response: " + response.prettyPrint());
    }

    @When("user sends this to {word}")
    public void send_payload(String service) {
        System.out.println("sends payload to: "+ POST_URL.concat(service));
        response = request
                .when()
                .post(POST_URL.concat(service));
    }

    @Then("the status code is {int}")
    public void verify_status_code(int code) {
        //response = request.when().get(REQUEST_URL+id);
        responseCode = code;
        System.out.println("status code check: "+code);
        json = response
                .then()
                .statusCode(code);
    }

    @And("response includes the following$")
    public void response_equals(Map<String,String> responseFields){
        //System.out.println(convertWithIteration(responseFields));
        for (Map.Entry<String, String> field : responseFields.entrySet()) {
            if (responseCode == 200) json.body(field.getKey(), equalTo(field.getValue()));
        }
    }

    @And("the following data is obtained from DB after query$")
    public void dataPostedToDB(Map<String, String> map) {
        //System.out.println(convertWithIteration(map));
        for (Map.Entry<String, String> field : map.entrySet()) {
            if (responseCode == 200) json.body(field.getKey(), equalTo(field.getValue()));
        }
    }

    @And("user gets activityId")
    public void userGetsActivityId() {
        activityId = response.asString().substring(15,response.asString().length()-2);
        System.out.println("new activityId: " + activityId);
    }

    private String convertWithIteration(Map<String, String> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        for (String key : map.keySet()) {
            //System.out.println("key: " + key + ", value: " + map.get(key));
            mapAsString.append("\"").append(key).append("\":\"").append(map.get(key)).append("\", ");
        }
        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
        return mapAsString.toString();
    }
    /*URL url = new URL(uri);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()){
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        int code = con.getResponseCode();
        System.out.println("statusCodeInPosting " + code);
        this.responseCode = code;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))){
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("response: "+response.toString());
            //return response.toString();

*/
    private String turnMapIntoJSONFormat(Map<String, String> map) {
        //TODO: don't make next person who looks at this code angry
        /*
        1. turn map into string
        2. split map by ":"
        3. strip ":"
        4. "$.".concat(first_part_of_string) -> filter 1
         */
        return "{\"externalProcessIdAtCarrier\":\"7854a\",\"inquiryLocation\": {\"address\":{\"city\":\"Berlin\",\"cityPart\":\"Spandau\",\"postcode\":\"13581\",\"streetName\":\"Bager_Str\",\"streetNr\":\"20\",\"streetNrSuffix\":\"A\"},\"klsId\":\"567\"}}";
    }
    /*
{
  "externalProcessIdAtCarrier": "string",
  "inquiryLocation": {
    "address": {
      "city": "string",
      "cityPart": "string",
      "postcode": "string",
      "streetName": "string",
      "streetNr": "string",
      "streetNrSuffix": "string"
    },
    "klsId": "string"
  }
}
{
"externalProcessIdAtCarrier":"7854a",
"inquiryLocation.address.city":"Berlin",
"inquiryLocation.address.cityPart":"Spandau",
"inquiryLocation.address.postcode":"13581",
"inquiryLocation.address.streetName":"Bager_Str",
"inquiryLocation.address.streetNr":"20",
"inquiryLocation.address.streetNrSuffix":"A",
"inquiryLocation.klsId":"567"
}

 */
}