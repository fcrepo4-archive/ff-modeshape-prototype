package org.fcrepo.modeshape;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class FedoraObjectsTest {

	int SERVER_PORT = 9999;
	
	@Test
	public void testIngest() throws Exception {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf");
		int status = client.executeMethod(method);
		assertEquals(201, status);
	}

	@Test
	public void testGetObjectInXML() throws Exception {
		HttpClient client = new HttpClient();
		PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/fdsa");
		client.executeMethod(pmethod);

		GetMethod method = new GetMethod("http://localhost:" + SERVER_PORT
				+ "/objects/fdsa");
		int status = client.executeMethod(method);
		assertEquals(200, status);
	}

	@Test
	public void testDeleteObject() throws Exception {
		HttpClient client = new HttpClient();
		PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf");
		client.executeMethod(pmethod);

		DeleteMethod method = new DeleteMethod("http://localhost:"
				+ SERVER_PORT + "/objects/asdf");
		int status = client.executeMethod(method);
		assertEquals(204, status);
	}

}
