package de.mindlessbloom.suffixtree.neo4j;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Neo4jKlient {

	public static final String SERVER_WURZEL_URI = "http://localhost:7474/db/data/";
	public static final String SERVER_KNOTEN_URI = SERVER_WURZEL_URI + "node";

	public static void main(String[] args) {
		Neo4jKlient instanz = new Neo4jKlient();
		instanz.test();
	}

	public void test() {

		WebResource resource = Client.create().resource(SERVER_WURZEL_URI);
		ClientResponse response = resource.get(ClientResponse.class);

		//System.out.println(String.format("GET on [%s], status code [%d]", SERVER_WURZEL_URI, response.getStatus()));
		int status = response.getStatus();
		response.close();

		if (status != 200) {
			System.out.println("Keine Verbindung zum Server -- beende.");
			System.exit(1);
		}

		// Knoten erstellen
		URI firstNode = erstelleKnoten();
		eigenschaftHinzufuegen(firstNode, "name", "Joe Strummer");
		URI secondNode = erstelleKnoten();
		eigenschaftHinzufuegen(secondNode, "band", "The Clash");

		try {
			URI relationshipUri = addRelationship(firstNode, secondNode,
					"singer", "{ \"from\" : \"1976\", \"until\" : \"1986\" }");
			addMetadataToProperty(relationshipUri, "stars", "5");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
	
	public URI erstelleKnoten(String name){
		URI uri = this.erstelleKnoten();
		try {
			this.addMetadataToProperty(uri, "name", name);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}

	public URI erstelleKnoten() {

		WebResource noderesource = Client.create().resource(SERVER_KNOTEN_URI);
		// POST {} to the node entry point URI
		ClientResponse noderesponse = noderesource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity("{}")
				.post(ClientResponse.class);

		final URI location = noderesponse.getLocation();
		// System.out.println(String.format("POST to [%s], status code [%d], location header [%s]",SERVER_KNOTEN_URI, noderesponse.getStatus(),location.toString()));
		noderesponse.close();

		return location;
	}

	public void eigenschaftHinzufuegen(URI knotenUri,
			String eigenschaftsBezeichner, String eigenschaftsWert) {
		String propertyUri = knotenUri.toString() + "/properties/"
				+ eigenschaftsBezeichner;
		// http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

		WebResource resource = Client.create().resource(propertyUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.entity("\"" + eigenschaftsWert + "\"")
				.put(ClientResponse.class);

		// System.out.println(String.format("PUT to [%s], status code [%d]",propertyUri, response.getStatus()));
		response.close();
	}

	public URI addRelationship(URI startNode, URI endNode,
			String relationshipType, String jsonAttributes)
			throws URISyntaxException {
		URI fromUri = new URI(startNode.toString() + "/relationships");
		String relationshipJson = generateJsonRelationship(endNode,
				relationshipType, jsonAttributes);

		WebResource resource = Client.create().resource(fromUri);
		// POST JSON to the relationships URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(relationshipJson)
				.post(ClientResponse.class);

		final URI location = response.getLocation();
		// System.out.println(String.format("POST to [%s], status code [%d], location header [%s]",fromUri, response.getStatus(), location.toString()));

		response.close();
		return location;
	}

	private String generateJsonRelationship(URI endNode,
			String relationshipType, String... jsonAttributes) {
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"to\" : \"");
		sb.append(endNode.toString());
		sb.append("\", ");

		sb.append("\"type\" : \"");
		sb.append(relationshipType);
		if (jsonAttributes == null || jsonAttributes.length < 1) {
			sb.append("\"");
		} else {
			sb.append("\", \"data\" : ");
			for (int i = 0; i < jsonAttributes.length; i++) {
				sb.append(jsonAttributes[i]);
				if (i < jsonAttributes.length - 1) { // Miss off the final comma
					sb.append(", ");
				}
			}
		}

		sb.append(" }");
		return sb.toString();
	}

	public void addMetadataToProperty(URI relationshipUri, String name,
			String value) throws URISyntaxException {
		URI propertyUri = new URI(relationshipUri.toString() + "/properties");
		String entity = toJsonNameValuePairCollection(name, value);
		WebResource resource = Client.create().resource(propertyUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(entity)
				.put(ClientResponse.class);

		// System.out.println(String.format("PUT [%s] to [%s], status code [%d]",entity, propertyUri, response.getStatus()));
		response.close();
	}

	private String toJsonNameValuePairCollection(String name, String value) {
		return String.format("{ \"%s\" : \"%s\" }", name, value);
	}

}
