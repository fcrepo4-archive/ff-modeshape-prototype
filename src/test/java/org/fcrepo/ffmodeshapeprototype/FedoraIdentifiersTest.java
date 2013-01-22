package org.fcrepo.ffmodeshapeprototype;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA. User: cabeer Date: 1/17/13 Time: 14:11 To change
 * this template use File | Settings | File Templates.
 */
public class FedoraIdentifiersTest {

	private TJWSEmbeddedJaxrsServer server;
	int SERVER_PORT = 9999;

	@Before
	public void start() {

		server = new TJWSEmbeddedJaxrsServer();
		server.setPort(SERVER_PORT);
		server.getDeployment().getActualResourceClasses()
				.add(FedoraIdentifiers.class);
		server.start();
	}

	@After
	public void stop() {
		server.stop();
	}

	@Test
	public void testGetNextPid() throws Exception {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/nextPID");
		method.addRequestHeader("Accepts", "text/xml");
		int status = client.executeMethod(method);
		System.out.println("Executed testGetNextPid()");
		System.out.println(method.getResponseBodyAsString());
		assertEquals(HttpServletResponse.SC_OK, status);
	}
}
