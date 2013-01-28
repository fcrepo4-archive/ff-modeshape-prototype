package org.fcrepo.modeshape;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class FedoraIdentifiersTest {

	int SERVER_PORT = 9999;

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
