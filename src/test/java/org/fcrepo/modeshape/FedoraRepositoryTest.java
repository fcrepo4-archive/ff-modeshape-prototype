package org.fcrepo.modeshape;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class FedoraRepositoryTest {


	int SERVER_PORT = 9999;

	@Test
	public void testDescribeModeshape() throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod("http://localhost:" + SERVER_PORT
				+ "/describe/modeshape");
		int status = client.executeMethod(method);
		assertEquals(200, status);
	}

	@Test
	public void testDescribe() throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod("http://localhost:" + SERVER_PORT
				+ "/describe");
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
}
