package org.fcrepo.modeshape;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class FedoraIdentifiersTest {

	final private Logger logger = LoggerFactory
			.getLogger(FedoraIdentifiersTest.class);

	int SERVER_PORT = 9999;

	final private HttpClient client = new HttpClient();

	@Test
	public void testGetNextPidResponds() throws Exception {
		PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/nextPID");
		method.addRequestHeader("Accepts", "text/xml");
		int status = client.executeMethod(method);
		logger.debug("Executed testGetNextPidResponds()");
		assertEquals(HttpServletResponse.SC_OK, status);
	}

	@Test
	public void testGetNextHasAPid() throws HttpException, IOException {
		PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/nextPID?numPids=1");
		method.addRequestHeader("Accepts", "text/xml");
		client.executeMethod(method);
		logger.debug("Executed testGetNextHasAPid()");
		String response = method.getResponseBodyAsString(Integer.MAX_VALUE);
		logger.debug("Only to find:\n" + response);
		assertTrue("Didn't find a single dang PID!",
				compile("<pid>.*?</pid>", DOTALL).matcher(response).find());
	}

	@Test
	public void testGetNextHasTwoPids() throws HttpException, IOException {
		PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/nextPID?numPids=2");
		method.addRequestHeader("Accepts", "text/xml");
		client.executeMethod(method);
		logger.debug("Executed testGetNextHasTwoPids()");
		String response = method.getResponseBodyAsString(Integer.MAX_VALUE);
		logger.debug("Only to find:\n" + response);
		assertTrue(
				"Didn't find a two dang PIDs!",
				compile("<pid>.*?</pid>.*?<pid>.*?</pid>", DOTALL).matcher(
						response).find());
	}
}
