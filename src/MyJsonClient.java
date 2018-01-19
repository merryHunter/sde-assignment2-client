import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
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
									String path,
									String requestType) throws IOException {
		MessageFormat form = new MessageFormat(
   		     "\nRequest #{0}:\nHeader:\n"
        		+ "{6} {5}" + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON\n"
        		+ "=> Result: {1}\n" 
        		+ "=> HTTP Status: {2} {3}\n"
        		+ "Body:\n{4}\n");
		
		if (result == "ERROR"){
			entity = "[ ]";
		}
		ObjectMapper objMapper = new ObjectMapper();
		Object json = objMapper.readValue(entity, Object.class);
		String entityJson = objMapper.writerWithDefaultPrettyPrinter()
				 							.writeValueAsString(json);
		
		Object[] myargs = {requestNumber, result,
					responce.getStatus(),
					responce.getStatusInfo(),
					entityJson, path, requestType};

		System.out.println(form.format(myargs));
	}
	
    public static void main(String[] args)  {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget service = client.target(getBaseURI());
        // Step 1
        System.out.println("Server adress: " + getBaseURI() ); 
        
        String result;
        String requestType;
        int requestNumber;
    	try {
    		// Request #1 
	        // Step 3.1
    		String path = "person";
    		requestType = "GET";
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
	        requestNumber = 1;
	        printResponce(requestNumber, result, resp, responseStr, path, requestType);
	         		
	        int idFirstPerson = (Integer) peopleArray.getJSONObject(0).get("idPerson");
	        int idLastPerson = (Integer) peopleArray.getJSONObject(peopleCount - 1)
        															.get("idPerson");
	        // Request #2 
	        // Step 3.2
	        path = "person/" + Integer.toString(idFirstPerson);
	        resp = service.path(path).request().accept(MediaType.APPLICATION_JSON)
	        						.header("Content-type","application/json").get();
	        responseStr = resp.readEntity(String.class);
	        JSONObject personInfo;
	        String currentIvanName = "";
	        if (resp.getStatus() == 202 || resp.getStatus() == 200) {
	        	result = "OK";
	        	personInfo = new JSONObject(responseStr);
	        	currentIvanName = (String) personInfo.get("firstname"); // Ivan
	        } else {
	        	result ="ERROR";
	        }
	        requestNumber = 2;
	        printResponce(requestNumber, result, resp, responseStr, path, requestType);
	        
	        // Request #3 
	        // Step 3.3
	        Object newIvan = "{\"idPerson\":"+ idFirstPerson +
        		",\"firstname\":\"Yaroslav\",\"lastname\":\"Chernukha\",\"birthdate\":\"1995-07-03\"}";
	        requestType = "PUT";
	        Response responsePut = service.path(path).request()
					.accept(MediaType.APPLICATION_JSON)
					.header("Content-type","application/json")
					.put(Entity.json(newIvan));
	        resp = service.path(path).request().accept(MediaType.APPLICATION_JSON)
					.header("Content-type","application/json").get();
	        responseStr = resp.readEntity(String.class);
	        
	        personInfo = new JSONObject(responseStr);
	        String newIvanfirstName = (String) personInfo.get("firstname"); // Yaroslav
	        if (!newIvanfirstName.equals(currentIvanName)
	        		 && newIvanfirstName.equals("Yaroslav")) {
	        	result = "OK";
	        }else {
	        	result = "ERROR";
	        }
	        requestNumber = 3;
	        printResponce(requestNumber, result, responsePut, responseStr, path, requestType);
            
	        // Request #4 
	        // Step 3.4
	        path ="person";
	        requestType = "POST";
	        Object newPerson = "{\"firstname\":\"Leo\",\"lastname\":\"Da Vinci\",\"birthdate\":\"1994-04-14\","
	        		+ "\"activitypreference\":"
	        		+ "[{\"name\":\"Football\",\"description\":\"Playing for footbal club A.S. Roma\",\"place\":\"Rome\",\"type\":\"Sport\",\"startdate\":\"2010-10-10\"}]}";
	        
	        Response responsePost = service.path(path).request()
					.accept(MediaType.APPLICATION_JSON)
					.header("Content-type","application/json")
					.post(Entity.json(newPerson));
	        
	        int idNewPerson = -1;
	        if (resp.getStatus() == 200 || resp.getStatus() == 201 || resp.getStatus() == 202) {
	        	result = "OK";
	        	responseStr = responsePost.readEntity(String.class);
	        	personInfo = new JSONObject(responseStr);	  
	        	idNewPerson = (int) personInfo.get("idPerson"); 
	        }else {
	        	result = "ERROR";
	        	responseStr = "";
	        }
	        requestNumber = 4;
	        printResponce(requestNumber, result, responsePost, responseStr, path, requestType);
	        System.out.println("New person id: " + Integer.toString(idNewPerson));
	        
	        // Request #5 
	        // Step 3.5
	        path ="person/" + Integer.toString(idNewPerson);
	        requestType = "DELETE";
	        Response responseDelete = service.path(path).request()
					.accept(MediaType.APPLICATION_JSON)
					.header("Content-type","application/json")
					.delete();
	        responseDelete = service.path(path).request()
					.accept(MediaType.APPLICATION_JSON)
					.header("Content-type","application/json")
					.get();
	        responseStr = responseDelete.readEntity(String.class);
	        if (responseDelete.getStatus() == 404) {
	        	result = "OK";
	        	responseStr ="{}";
	        } else {
	        	result ="ERROR";
	        }
	        requestNumber = 5;
	        printResponce(requestNumber, result, responseDelete, responseStr, path, requestType);
	        
    		// Request #6
	        // Step 3.6
	        path = "activity_types";
	        requestType = "GET";
	        resp = service.path(path)
	        		.request()
	        		.accept(MediaType.APPLICATION_JSON)
	        		.header("Content-type","application/json").get();
	        responseStr = resp.readEntity(String.class);

	        JSONArray activitiesArray = new JSONArray(responseStr);
	        int activityTypeCount = activitiesArray.length();
	      
	        List<String> activity_types = new ArrayList<String>();
	        for (int i=0;i<activityTypeCount;i++) {
	        	activity_types.add(activitiesArray.get(i).toString());
	        }
	        if(activityTypeCount > 2) {
	        	result = "OK";
	        }else {
	        	result = "ERROR";
	        }
	        requestNumber = 6;
	        printResponce(requestNumber, result, resp, responseStr, path, requestType);
	        System.out.println("List of activity types in the system:" + activity_types);
	        
	        
	        // Request #6
	        // Step 3.6
	        int allActivitiesCount = 0;
	        int[] peopleIds = {idFirstPerson, idLastPerson};
	        int activity_id = -1;
	        String activity_type = "";
	        for (int id: peopleIds) {
		        for (String activity: activity_types) {
			        path = "person/" + Integer.toString(id) + '/' + activity;
			        requestType = "GET";
			        resp = service.path(path).request().accept(MediaType.APPLICATION_JSON)
			        		.header("Content-type","application/json").get();
			        responseStr = resp.readEntity(String.class);

			        JSONArray personActivitiesArray = new JSONArray(responseStr);
			        int activitiesCount = personActivitiesArray.length();
			        if(activitiesCount > 0) {
			        	allActivitiesCount += activitiesCount;
			        	result = "OK";
			        	activity_id = (int) personActivitiesArray.getJSONObject(0)
			        												.get("idActivity");
			        	activity_type = (String) personActivitiesArray.getJSONObject(0)
																	.get("type");
			        }else {
			        	result = "ERROR";
			        	responseStr = "{}";
		        	}
			        requestNumber = 6;
			        printResponce(requestNumber, result, resp, responseStr, path, requestType);
		        }
	        }
	        if (allActivitiesCount > 0) {
	        	System.out.println("Request 7 is OK, there were "
	        				+ Integer.toString(allActivitiesCount) + " activities.");
	        	System.out.println("Selected activities: id: " + Integer.toString(activity_id)
	        							+ " type: " + activity_type);
	        } else {
	        	System.out.println("Request 7 is ERROR!");
	        }
	        
	        // Request #7
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		System.out.print(e.getMessage());
    	}
    }
    
}