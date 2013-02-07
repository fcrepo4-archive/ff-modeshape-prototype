package org.fcrepo.modeshape;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring-test/rest.xml", "/spring-test/repo.xml",
		"/spring-test/indexer.xml" })
public abstract class AbstractResourceTest {

	protected Logger logger;

	protected static final int SERVER_PORT = 8080;
	protected static final String HOSTNAME = "localhost";
	protected static final String serverAddress = "http://" + HOSTNAME + ":"
			+ SERVER_PORT + "/";

	protected final HttpClient client = new HttpClient();

	@Before
	public void setLogger() {
		logger = LoggerFactory.getLogger(this.getClass());
	}

	protected PostMethod postObjectMethod(final String pid) {
		return new PostMethod(serverAddress + "rest/objects/" + pid);
	}

	protected PostMethod postDSMethod(final String pid, final String ds) {
		return new PostMethod(serverAddress + "rest/objects/" + pid
				+ "/datastreams/" + ds);
	}

	protected PutMethod putDSMethod(final String pid, final String ds) {
		return new PutMethod(serverAddress + "rest/objects/" + pid
				+ "/datastreams/" + ds);
	}
}
