package org.fcrepo.modeshape;

import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

public class FedoraObjectsTest extends AbstractResourceTest {

	@Test
	public void testIngest() throws Exception {
		PostMethod method = new PostMethod(serverAddress + "rest/objects/asdf");
		int status = client.executeMethod(method);
		assertEquals(201, status);
	}

	@Test
	public void testGetObjectInXML() throws Exception {
		PostMethod createObjMethod = new PostMethod(serverAddress
				+ "rest/objects/fdsa");
		client.executeMethod(createObjMethod);

		GetMethod getObjMethod = new GetMethod(serverAddress
				+ "rest/objects/fdsa");
		int status = client.executeMethod(getObjMethod);
		assertEquals(200, status);
		String response = getObjMethod.getResponseBodyAsString();
		logger.debug("Retrieved object profile:\n" + response);
		assertTrue("Object had wrong PID!",
				compile("pid=\"fdsa\"").matcher(response).find());
	}

	@Test
	public void testDeleteObject() throws Exception {
		PostMethod createObjmethod = new PostMethod(serverAddress
				+ "rest/objects/asdf");
		client.executeMethod(createObjmethod);

		DeleteMethod delMethod = new DeleteMethod(serverAddress
				+ "rest/objects/asdf");
		int status = client.executeMethod(delMethod);
		assertEquals(204, status);

		GetMethod getMethod = new GetMethod(serverAddress + "rest/objects/asdf");
		status = client.executeMethod(getMethod);
		assertEquals("Object wasn't really deleted!", 404, status);
	}

}
