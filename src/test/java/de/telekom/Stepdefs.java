package de.telekom;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.core.JsonPointer;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.node.ArrayNode;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.node.TextNode;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;

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
    public void theFollowingPayload(Map<String,String> responseFields) throws Exception {
        this.payload = turnMapIntoJSONFormat(responseFields);
        //System.out.println("the Following Payload: "+ convertWithIteration(responseFields));
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
        //System.out.println("response: " + response.prettyPrint());
    }

    @When("user sends this to {word}")
    public void send_payload(String service) {
        //System.out.println("sends payload to: "+ POST_URL.concat(service));
        response = request
                .when()
                .post(POST_URL.concat(service));
    }

    @Then("the status code is {int}")
    public void verify_status_code(int code) {
        //response = request.when().get(REQUEST_URL+id);
        responseCode = code;
        //System.out.println("status code check: "+code);
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
        System.out.println(" after query: " + convertWithIteration(map));
        for (Map.Entry<String, String> field : map.entrySet()) {
            System.out.println("key: " + field.getKey() + " value: " + field.getValue());
            System.out.println(response.asString());
            this.response = request.when().get(REQUEST_URL+id);
            if (responseCode == 201) json.body(field.getKey(), equalTo(field.getValue()));
        }
    }

    @And("user gets activityId")
    public void userGetsActivityId() {
        activityId = response.asString().substring(15,response.asString().length()-2);
        System.out.println("new activityId: " + activityId);
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
    private String turnMapIntoJSONFormat(Map<String, String> map) throws Exception {
        //TODO: don't make next person who looks at this code angry
        //WILL mutilate strings or payloads with slashes inside
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.putObject("");
        String result = "";

        //result = "/".concat(result.replace('.','/'));
        for (Map.Entry<String, String> field : map.entrySet()) {
            String[] res = field.getKey().split("\\.");
            //System.out.println("key: "+ field.getKey() +" value: "+field.getValue());
            for (int i =0; i < res.length;i++) {
                //System.out.println("array: on index "+ i + ": " + res[i]);
                objectNode1.putObject(res[i]);
            }
        }
        //System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode1));
        //System.out.println("manual: " + objectNode1.toString());
        // setJsonPointerValue(rootNode, JsonPointer.compile("/root/array/0/name"), new TextNode("John"));
        ObjectNode rootNode = mapper.createObjectNode();
        for (Map.Entry<String, String> field : map.entrySet()) {
            String path = "/".concat(field.getKey().replace('.','/'));
            setJsonPointerValue(rootNode, JsonPointer.compile(path), new TextNode(field.getValue()));
        }

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        //return "{\"externalProcessIdAtCarrier\":\"7854a\",\"inquiryLocation\": {\"address\":{\"city\":\"Berlin\",\"cityPart\":\"Spandau\",\"postcode\":\"13581\",\"streetName\":\"Bager_Str\",\"streetNr\":\"20\",\"streetNrSuffix\":\"A\"},\"klsId\":\"567\"}}";
    }
    private static final ObjectMapper mapper = new ObjectMapper();
    public void setJsonPointerValue(ObjectNode node, JsonPointer pointer, JsonNode value) {
        JsonPointer parentPointer = pointer.head();
        JsonNode parentNode = node.at(parentPointer);
        String fieldName = pointer.last().toString().substring(1);

        if (parentNode.isMissingNode() || parentNode.isNull()) {
            parentNode = StringUtils.isNumeric(fieldName) ? mapper.createArrayNode() : mapper.createObjectNode();
            setJsonPointerValue(node, parentPointer, parentNode); // recursively reconstruct hierarchy
        }

        if (parentNode.isArray()) {
            assert parentNode instanceof ArrayNode;
            ArrayNode arrayNode = (ArrayNode) parentNode;
            int index = Integer.parseInt(fieldName);
            // expand array in case index is greater than array size (like JavaScript does)
            for (int i = arrayNode.size(); i <= index; i++) {
                arrayNode.addNull();
            }
            arrayNode.set(index, value);
        } else if (parentNode.isObject()) {
            ((ObjectNode) parentNode).set(fieldName, value);
        } else {
            throw new IllegalArgumentException("`" + fieldName + "` can't be set for parent node `"
                    + parentPointer + "` because parent is not a container but " + parentNode.getNodeType().name());
        }
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
    private String convertWithIteration(Map<String, String> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        for (String key : map.keySet()) {
            //System.out.println("key: " + key + ", value: " + map.get(key));
            mapAsString.append("\"").append(key).append("\":\"").append(map.get(key)).append("\", ");
        }
        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
        return mapAsString.toString();
    }
}