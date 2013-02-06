package org.fcrepo.modeshape;

import static java.util.regex.Pattern.compile;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class FedoraRepositoryTest {

	int SERVER_PORT = 9999;

	final private Logger logger = LoggerFactory
			.getLogger(FedoraRepositoryTest.class);

	@Test
	public void testDescribeModeshape() throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod("http://localhost:" + SERVER_PORT
				+ "/describe/modeshape");
		int status = client.executeMethod(method);
		assertEquals(200, status);
	}

	@Test
	public void testGetObjects() throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod("http://localhost:" + SERVER_PORT
				+ "/objects");
		int status = client.executeMethod(method);
		assertEquals(200, status);
	}

	@Test
	public void testDescribe() throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod("http://localhost:" + SERVER_PORT
				+ "/describe");
		method.addRequestHeader("Accept", TEXT_XML);
		int status = client.executeMethod(method);
		assertEquals(200, status);
		final String description = method.getResponseBodyAsString();
		logger.debug("Found a repository description:\n" + description);
		assertTrue("Failed to find a proper repo versiom",
				compile("<repositoryVersion>.*?</repositoryVersion>")
						.matcher(description).find());
	}
}
