package org.fcrepo.modeshape.observer;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
@ContextConfiguration({ "/spring-test/eventing.xml", "/spring-test/repo.xml" })
public class RSSTest {

	final private Logger logger = LoggerFactory.getLogger(RSSTest.class);
	final private HttpClient client = new HttpClient();

	private static final int SERVER_PORT = 8080;
	private static final String HOSTNAME = "localhost";
	private static final String serverAddress = "http://" + HOSTNAME + ":"
			+ SERVER_PORT + "/";

	@Test
	public void testRSS() throws Exception {
		PostMethod createObjMethod = new PostMethod(serverAddress
				+ "rest/objects/RSSTESTPID");
		assertEquals(201, client.executeMethod(createObjMethod));

		GetMethod getRSSMethod = new GetMethod(serverAddress + "/rss");
		assertEquals(200, client.executeMethod(getRSSMethod));
		String response = getRSSMethod.getResponseBodyAsString();
		logger.debug("Retrieved RSS feed:\n" + response);
		assertTrue("Didn't find the test PID in RSS!",
				compile("RSSTESTPID", DOTALL).matcher(response).find());

	}
}
