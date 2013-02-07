package org.fcrepo.modeshape;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;

import org.apache.commons.httpclient.HttpClient;
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
public class FedoraRepositoryTest {

	private static final int SERVER_PORT = 8080;
	private static final String HOSTNAME = "localhost";
	private static final String serverAddress = "http://" + HOSTNAME + ":"
			+ SERVER_PORT + "/";

	final private Logger logger = LoggerFactory
			.getLogger(FedoraRepositoryTest.class);

	final private HttpClient client = new HttpClient();

	@Test
	public void testDescribeModeshape() throws Exception {
		GetMethod method = new GetMethod(serverAddress
				+ "rest/describe/modeshape");
		int status = client.executeMethod(method);
		assertEquals(200, status);
	}

	@Test
	public void testGetObjects() throws Exception {
		GetMethod method = new GetMethod(serverAddress + "rest/objects");
		int status = client.executeMethod(method);
		assertEquals(200, status);
	}

	@Test
	public void testDescribe() throws Exception {
		GetMethod method = new GetMethod(serverAddress + "rest/describe");
		method.addRequestHeader("Accept", TEXT_XML);
		int status = client.executeMethod(method);
		assertEquals(200, status);
		final String description = method.getResponseBodyAsString();
		logger.debug("Found the repository description:\n" + description);
		assertTrue(
				"Failed to find a proper repo version",
				compile("<repositoryVersion>.*?</repositoryVersion>").matcher(
						description).find());
	}

	@Test
	public void testDescribeSize() throws Exception {
		GetMethod describeMethod = new GetMethod(serverAddress
				+ "rest/describe");
		describeMethod.addRequestHeader("Accept", TEXT_XML);
		int status = client.executeMethod(describeMethod);
		assertEquals(200, status);
		String description = describeMethod.getResponseBodyAsString();
		logger.debug("Found a repository description:\n" + description);
		Matcher check = compile("<repositorySize>([0-9]+)</repositorySize>",
				DOTALL).matcher(description);
		Long oldSize = null;
		while (check.find()) {
			oldSize = new Long(check.group(1));
		}

		PostMethod createObjMethod = new PostMethod(serverAddress
				+ "rest/objects/fdfgdgsa");
		assertEquals(201, client.executeMethod(createObjMethod));

		GetMethod newDescribeMethod = new GetMethod(serverAddress
				+ "rest/describe");
		newDescribeMethod.addRequestHeader("Accept", TEXT_XML);
		status = client.executeMethod(newDescribeMethod);
		assertEquals(200, status);
		String newDescription = newDescribeMethod.getResponseBodyAsString();
		logger.debug("Found another repository description:\n" + newDescription);
		Matcher newCheck = compile("<repositorySize>([0-9]+)</repositorySize>",
				DOTALL).matcher(newDescription);
		Long newSize = null;
		while (newCheck.find()) {
			newSize = new Long(newCheck.group(1));
		}
		logger.debug("Old size was: " + oldSize + " and new size was: "
				+ newSize);
		assertTrue("No increment in size occurred when we expected one!",
				oldSize < newSize);
	}
}
