package org.fcrepo.modeshape;

import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring-test/rest.xml", "/spring-test/repo.xml" })
public class FedoraObjectsTest {

	private static final int SERVER_PORT = 8080;
	private static final String HOSTNAME = "localhost";
	private static final String serverAddress = "http://" + HOSTNAME + ":"
			+ SERVER_PORT + "/";

	final private Logger logger = LoggerFactory
			.getLogger(FedoraObjectsTest.class);

	final private HttpClient client = new HttpClient();

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
