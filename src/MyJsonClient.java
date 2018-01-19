import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MyJsonClient {
	
	private static URI getBaseURI() {
        return UriBuilder.fromUri(
                "http://localhost:5901/").build();
    }
	
	/**
	 * 
	 * @param requestNumber
	 * @param result
	 * @param responce
	 * @param entity
	 * @throws IOException
	 */
	private static void printResponce(int requestNumber, 
									String result,
									Response responce, 
									String entity,
									String path) throws IOException {
		MessageFormat form = new MessageFormat(
   		     "\nRequest #{0}:\nHeader:\n"
        		+ "GET {5}" + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON\n"
        		+ "=> Result: {1}\n" 
        		+ "=> HTTP Status: {2} {3}\n"
        		+ "Body:\n{4}\n");
			 ObjectMapper objMapper = new ObjectMapper();
			 Object json = objMapper.readValue(entity, Object.class);
			 String entityJson = objMapper.writerWithDefaultPrettyPrinter()
					 							.writeValueAsString(json);

			Object[] myargs = {requestNumber, result,
						responce.getStatus(),
						responce.getStatusInfo(),
						entityJson, path};

			System.out.println(form.format(myargs));
	}
	
    public static void main(String[] args)  {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget service = client.target(getBaseURI());
        // Step 1
        System.out.println("Server adress: " + getBaseURI() ); 
        
        String result;
        int requestNumber;
    	try {
    		// Request #1 
	        // Step 3.1
    		String path = "person";
	        Response resp = service.path(path)
	        		.request()
	        		.accept(MediaType.APPLICATION_JSON)
	        		.header("Content-type","application/json")
	        		.get();
	        String responseStr = resp.readEntity(String.class);
	        JSONArray peopleArray = new JSONArray(responseStr);
	        int peopleCount = peopleArray.length();
	        if(peopleCount > 4) {
	        	result = "OK";
	        }else {
	        	result = "ERROR";
        	}
//	        System.out.println(responseStr);
	        requestNumber = 1;
	        printResponce(requestNumber, result, resp, responseStr, path);
	         		
	        
	        int idFirstPerson = (Integer) peopleArray.getJSONObject(0).get("idPerson");
	        int idLastPerson = (Integer) peopleArray.getJSONObject(peopleCount - 1)
        															.get("idPerson");
	        // Request 2 
	        // Step 3.2
	        path = "person/" + Integer.toString(idFirstPerson);
	        resp = service.path(path).request().accept(MediaType.APPLICATION_JSON)
	        						.header("Content-type","application/json").get();
	        responseStr = resp.readEntity(String.class);
	        JSONObject personInfo = new JSONObject(responseStr);
	        if (resp.getStatus() == 202 || resp.getStatus() == 200) {
	        	result = "OK";
	        } else {
	        	result ="ERROR";
	        }
//	        System.out.println(responseStr);
	        requestNumber = 2;
	        printResponce(requestNumber, result, resp, responseStr, path);
	        
	        
	        
//    		Object entity4 = "{\"lastname\":\"Holmes\",\"birthdate\":1510780525483}";
    
//            Response responsePost = service.path("person").request()
//            					.accept(MediaType.APPLICATION_JSON)
//            					.header("Content-type","application/json")
//            					.post(Entity.json(entity4));
//
//	        System.out.println(responsePost);
    		
    		// # 3.6. Request #6
	        path = "activity_types";
	        Response resp6 = service.path(path)
	        		.request()
	        		.accept(MediaType.APPLICATION_JSON)
	        		.header("Content-type","application/json").get();
	        String response6 = resp6.readEntity(String.class);

	        JSONArray array6 = new JSONArray(response6);
	        int activityTypeCount = array6.length();
	      
	        List<String> activityTypesList = new ArrayList<String>();
	        for (int i=0;i<activityTypeCount;i++) {
	        	activityTypesList.add(array6.get(i).toString());
	        }
	        if(activityTypeCount>2) {
	        	result = "OK";
	        }else {
	        	result = "ERROR";
	        }
	        requestNumber = 6;
	        printResponce(requestNumber, result, resp6, response6, path);
	        System.out.println("List of activity types in the system:" + activityTypesList);
	        
	        
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		System.out.print(e.getMessage());
    	}
    }
    
}