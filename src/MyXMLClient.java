import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


public class MyXMLClient {
	
	private static URI getBaseURI() {
        return UriBuilder.fromUri(
                "http://localhost:5901/").build();
    }
	
	private static void printResponce(int requestNumber, 
			String result,
			Response responce, 
			String entity,
			String path,
			String requestType) throws Exception {
		MessageFormat form = new MessageFormat(
				"\nRequest #{0}:\nHeader:\n"
				+ "{6} {5}" + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML\n"
				+ "=> Result: {1}\n" 
				+ "=> HTTP Status: {2} {3}\n"
				+ "Body:\n{4}\n");
				
		if (result == "ERROR"){
			entity = "[ ]";
		}
		String entityXml = format(entity);
		Object[] myargs = {requestNumber, result,
					responce.getStatus(),
					responce.getStatusInfo(),
					entityXml, path, requestType};
		System.out.println(form.format(myargs));
}

	public static Document getXml(String xml) throws Exception
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource inputSrc = new InputSource(new StringReader(xml));
	    return builder.parse(inputSrc);
	}
	
	public static String format(String unformattedXml) throws Exception {
		System.out.println(unformattedXml);
		if (!unformattedXml.equals("") || !unformattedXml.isEmpty()){
	        final Document document = getXml(unformattedXml);
	        OutputFormat outputFormat = new OutputFormat(document);
	        outputFormat.setIndenting(true);
	        outputFormat.setIndent(4);
	        outputFormat.setLineWidth(100);
	        Writer writer = new StringWriter();
	        XMLSerializer s = new XMLSerializer(writer, outputFormat);
	        s.serialize(document);
	        return writer.toString();
		}
		else return "";
    }
	
	/**
	 * 
	 * @param idPerson
	 * @param key
	 * @return
	 */
	private static String createXMLObjectAsString(int idPerson, int key) {
		MessageFormat form = new MessageFormat("");
		Object[] args = {idPerson};
		if (key == 1) {
			form = new MessageFormat(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
					"<person>\n" + 
					"    <birthdate>1995-07-03</birthdate>\n" + 
					"    <firstname>Yaroslav</firstname>\n" + 
					"    <idPerson>{0}</idPerson>\n" + 
					"    <lastname>Chernukha</lastname>\n" + 
					"</person>");
			return form.format(args).toString();
		} else if (key == 2){
			return 
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
					"<person>\n" + 
					"    <birthdate>1994-04-14</birthdate>\n" + 
					"    <firstname>Leo</firstname>\n" + 
					"    <lastname>Da Vinci</lastname>\n" + 
					"	 <preferences>\n" + 
						" 	 <activitypreference>\n" + 
							"	<name>Football</name>\n" 	+
							"   <description>Playing for footbal club A.S. Roma</description>\n" +
							"   <place>Rome</place>\n" +
							"   <type>Sport</type>\n" + 
							"   <startdate>2010-10-10</startdate>\n" +
						" 	 </activitypreference>\n" +
					"	 </preferences>\n" + 
					"</person>\n";    
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget service = client.target(getBaseURI());
        String result;
		try {
	        // Request #1
	        // Step 3.1
			String path = "person";
			String requestType = "GET";
	        Response resp = service.path(path).request().accept(MediaType.APPLICATION_XML)
	        									.header("Content-type","application/xml").get();
	        String responseStr = resp.readEntity(String.class);
	        Document document = getXml(responseStr);
	        NodeList people = document.getElementsByTagName("idPerson");
	        int peopleCount = people.getLength();
	        int idFirstPerson = Integer.parseInt(people.item(0).getTextContent());
	        int idLastPerson =  Integer.parseInt(people.item(peopleCount - 1).getTextContent());
	        
	        if(peopleCount > 4) {
	        	result = "OK";
	        }else {
	        	result = "ERROR";
	        }
	        int requestNumber = 1;
	        printResponce(requestNumber, result, resp, responseStr, path, requestType);

	        // Request #2
	        // Step 3.2
			path = "person/" + Integer.toString(idFirstPerson);
			requestType = "GET";
	        resp = service.path(path).request().accept(MediaType.APPLICATION_XML)
	        									.header("Content-type","application/xml").get();
	        responseStr = resp.readEntity(String.class);
	        document = getXml(responseStr);
	        String currentIvanName = "";
	        if(resp.getStatus() == 200 || resp.getStatus() == 202) {
	        	result = "OK";
	        	currentIvanName = document.getElementsByTagName("firstname")
	        										.item(0)
	        										.getTextContent(); // Ivan
	        }else {
	        	result = "ERROR";
	        }
	        requestNumber = 2;
	        printResponce(requestNumber, result, resp, responseStr, path, requestType);
	        
	        // Request #3
	        // Step 3.3
			path = "person/" + Integer.toString(idFirstPerson);
			requestType = "PUT";
			Object newIvan = createXMLObjectAsString(idFirstPerson, 1);
			System.out.println(newIvan);
	        resp = service.path(path).request().accept(MediaType.APPLICATION_XML)
	        									.header("Content-type","application/xml")
	        									.put(Entity.xml(newIvan));
	        responseStr = resp.readEntity(String.class);
	        // confirm
	        requestType = "GET";
	        resp = service.path(path).request().accept(MediaType.APPLICATION_XML)
					.header("Content-type","application/xml").get();
	        responseStr = resp.readEntity(String.class);
	        document = getXml(responseStr);
	        String newName = document.getElementsByTagName("firstname")
											.item(0).getTextContent(); // should be Yaroslav
	        if(!newName.equals(currentIvanName) && newName.equals("Yaroslav")) {
	        	result = "OK";
	        }else {
	        	result = "ERROR";
	        }
	        System.out.print("Name has been changed to: " + newName);
	        requestNumber = 2;
	        printResponce(requestNumber, result, resp, responseStr, path, requestType);
	        
	        
	        // Request #4 
	        // Step 3.4
	        path ="person";
	        requestType = "POST";
	        Object newPerson = createXMLObjectAsString(-1, 2);
	        System.out.println(newPerson);
	        Response responsePost = service.path(path).request().accept(MediaType.APPLICATION_XML)
					.header("Content-type","application/xml").post(Entity.xml(newPerson));
	        
	        int idNewPerson = -1;
	        if (resp.getStatus() == 200 || resp.getStatus() == 201 || resp.getStatus() == 202) {
	        	result = "OK";
	        	responseStr = responsePost.readEntity(String.class);
	        	document = getXml(responseStr);
		        idNewPerson = Integer.parseInt(document.getElementsByTagName("idPerson")
												.item(0).getTextContent());
	        }else {
	        	result = "ERROR";
	        	responseStr = "";
	        }
	        requestNumber = 4;
	        printResponce(requestNumber, result, responsePost, responseStr, path, requestType);
	        System.out.println("New person id: " + Integer.toString(idNewPerson));
	        
	        // Request #5 
	        // Step 3.5
	        path = "person/" + Integer.toString(idNewPerson);
	        requestType = "DELETE";
	        Response responseDelete = service.path(path).request().accept(MediaType.APPLICATION_XML)
					.header("Content-type","application/xml").delete();
	        responseDelete = service.path(path).request().accept(MediaType.APPLICATION_XML)
					.header("Content-type","application/xml").get();
	        responseStr = responseDelete.readEntity(String.class);
	        if (responseDelete.getStatus() == 404) {
	        	result = "OK";
	        	responseStr ="";
	        } else {
	        	result ="ERROR";
	        }
	        requestNumber = 5;
	        printResponce(requestNumber, result, responseDelete, responseStr, path, requestType);
	        
	        // Request #6
	        // Step 3.6
	        path = "activity_types";
	        requestType = "GET";
	        resp = service.path(path).request().accept(MediaType.APPLICATION_XML)
	        		.header("Content-type","application/xml").get();
	        responseStr = resp.readEntity(String.class);

	        System.out.println("response for activity types");
	        System.out.println(responseStr);
	        document = getXml(responseStr);
	        
	        int activityTypeCount = document.getElementsByTagName("activityTypes").getLength();
	      
	        if(activityTypeCount > 2) {
	        	result = "OK";
	        }else {
	        	result = "ERROR";
	        }
	        requestNumber = 6;
	        printResponce(requestNumber, result, resp, responseStr, path, requestType);
	        
	        
	        
	        
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
