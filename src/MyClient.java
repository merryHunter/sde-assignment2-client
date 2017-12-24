import java.net.URI;    
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;

public class MyClient {
    public static void main(String[] args) {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget service = client.target(getBaseURI());

        // // GET BASEURL/rest/salutation
        // // Accept: text/plain
    	try {
//	        Response resp0 = service.path("person/3").request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
//	        String response0 = resp0.readEntity(String.class);
	        
    		Object entity4 = "{\"lastname\":\"Holmes\",\"birthdate\":1510780525483}";
    
            Response responsePost = service.path("person").request()
            					.accept(MediaType.APPLICATION_JSON)
            					.header("Content-type","application/json")
            					.post(Entity.json(entity4));

	        System.out.println(responsePost);
    	}
    	catch(Exception e) {
    		System.out.print(e.getMessage());
    	}
//        JSONArray array_response0 = new JSONArray(response0);
        
        
//        System.out.println(service.path("person").request().accept(MediaType.TEXT_PLAIN).get().readEntity(String.class));
//        // // Get plain text
//        System.out.println(service.path("salutation")
//                .request().accept(MediaType.TEXT_PLAIN).get().readEntity(String.class));
//        // Get XML 
//        System.out.println(service.path("salutation")
//                .request()
//                .accept(MediaType.TEXT_XML).get().readEntity(String.class));
//        // // The HTML
//        System.out.println(service.path("salutation").request()
//                .accept(MediaType.TEXT_HTML).get().readEntity(String.class));

    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri(
                "http://localhost:5901/sdelab/").build();
    }
    
}