package org.fcrepo.modeshape;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class FedoraDatastreamsTest {

	private static final String faulkner1 = "The past is never dead. It's not even past.";
	int SERVER_PORT = 9999;

	final private Logger logger = LoggerFactory
			.getLogger(FedoraDatastreamsTest.class);
	final private HttpClient client = new HttpClient();

	@Test
	public void testGetDatastreams() throws Exception {
		PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf");
		client.executeMethod(pmethod);

		GetMethod method = new GetMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf/datastreams");
		int status = client.executeMethod(method);
		assertEquals(200, status);
	}

	@Test
	public void testAddDatastream() throws Exception {
		PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf");
		client.executeMethod(pmethod);

		PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf/datastreams/zxc");
		int status = client.executeMethod(method);
		assertEquals(201, status);
	}

	@Test
	public void testMutateDatastream() throws Exception {
		PostMethod createObjectMethod = new PostMethod("http://localhost:"
				+ SERVER_PORT + "/objects/asdf2");
		Integer status = client.executeMethod(createObjectMethod);
		assertEquals("Couldn't create an object!", (Integer) 201, status);

		PostMethod createDataStreamMethod = new PostMethod("http://localhost:"
				+ SERVER_PORT + "/objects/asdf2/datastreams/vcxz");
		status = client.executeMethod(createDataStreamMethod);
		assertEquals("Couldn't create a datastream!", (Integer) 201, status);

		PutMethod mutateDataStreamMethod = new PutMethod("http://localhost:"
				+ SERVER_PORT + "/objects/asdf2/datastreams/vcxz");
		mutateDataStreamMethod.setRequestEntity(new StringRequestEntity(
				faulkner1, "text/plain", "UTF-8"));
		status = client.executeMethod(mutateDataStreamMethod);
		assertEquals("Couldn't mutate a datastream!", (Integer) 201, status);

		GetMethod retrieveMutatedDataStreamMethod = new GetMethod(
				"http://localhost:" + SERVER_PORT
						+ "/objects/asdf2/datastreams/vcxz/content");
		client.executeMethod(retrieveMutatedDataStreamMethod);
		String response = retrieveMutatedDataStreamMethod
				.getResponseBodyAsString();
		logger.debug("Retrieved mutated datastream content: " + response);
		assertTrue("Datastream didn't accept mutation!", compile(faulkner1)
				.matcher(response).find());
	}

	@Test
	public void testGetDatastream() throws Exception {
		PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf");
		client.executeMethod(pmethod);

		GetMethod method_test_get = new GetMethod("http://localhost:"
				+ SERVER_PORT + "/objects/asdf/datastreams/poiu");
		int status = client.executeMethod(method_test_get);
		assertEquals(404, status);

		PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf/datastreams/poiu");
		status = client.executeMethod(method);
		assertEquals(201, status);

		GetMethod method_2 = new GetMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf/datastreams/poiu");
		status = client.executeMethod(method_2);
		assertEquals(200, status);
	}

	@Test
	public void testDeleteDatastream() throws Exception {
		PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf");
		client.executeMethod(pmethod);

		PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf/datastreams/lkjh");
		int status = client.executeMethod(method);
		assertEquals(201, status);

		GetMethod method_2 = new GetMethod("http://localhost:" + SERVER_PORT
				+ "/objects/asdf/datastreams/lkjh");
		status = client.executeMethod(method_2);
		assertEquals(200, status);

		DeleteMethod dmethod = new DeleteMethod("http://localhost:"
				+ SERVER_PORT + "/objects/asdf/datastreams/lkjh");
		status = client.executeMethod(dmethod);
		assertEquals(204, status);

		GetMethod method_test_get = new GetMethod("http://localhost:"
				+ SERVER_PORT + "/objects/asdf/datastreams/lkjh");
		status = client.executeMethod(method_test_get);
		assertEquals(404, status);
	}

	@Test
	public void testGetDatastreamContent() throws Exception {
		final PostMethod createObjMethod = new PostMethod("http://localhost:"
				+ SERVER_PORT + "/objects/testfoo");
		client.executeMethod(createObjMethod);
		assertEquals(201, client.executeMethod(createObjMethod));

		final PostMethod createDSMethod = new PostMethod("http://localhost:"
				+ SERVER_PORT + "/objects/testfoo/datastreams/testfoozle");
		createDSMethod.setRequestEntity(new StringRequestEntity(
				"marbles for everyone", null, null));
		assertEquals(201, client.executeMethod(createDSMethod));
		GetMethod method_test_get = new GetMethod("http://localhost:"
				+ SERVER_PORT
				+ "/objects/testfoo/datastreams/testfoozle/content");
		assertEquals(200, client.executeMethod(method_test_get));
		assertEquals("Got the wrong content back!", "marbles for everyone",
				method_test_get.getResponseBodyAsString());
	}

	@Test
	public void testMultipleDatastreams() throws Exception {
		final PostMethod createObjMethod = new PostMethod("http://localhost:"
				+ SERVER_PORT + "/objects/testfoo");
		client.executeMethod(createObjMethod);
		assertEquals(201, client.executeMethod(createObjMethod));

		final PostMethod createDS1Method = new PostMethod("http://localhost:"
				+ SERVER_PORT + "/objects/testfoo/datastreams/testfoozle");
		createDS1Method.setRequestEntity(new StringRequestEntity(
				"marbles for everyone", null, null));
		assertEquals(201, client.executeMethod(createDS1Method));
		final PostMethod createDS2Method = new PostMethod("http://localhost:"
				+ SERVER_PORT + "/objects/testfoo/datastreams/testfoozle2");
		createDS2Method.setRequestEntity(new StringRequestEntity(
				"marbles for no one", null, null));
		assertEquals(201, client.executeMethod(createDS2Method));

		final GetMethod getDSesMethod = new GetMethod("http://localhost:"
				+ SERVER_PORT + "/objects/testfoo/datastreams");
		assertEquals(200, client.executeMethod(getDSesMethod));
		final String response = getDSesMethod.getResponseBodyAsString();
		assertTrue("Didn't find the first datastream!",
				compile("dsid=\"testfoozle\"", DOTALL).matcher(response).find());
		assertTrue("Didn't find the second datastream!",
				compile("dsid=\"testfoozle2\"", DOTALL).matcher(response)
						.find());
	}
}
