package org.fcrepo.modeshape.indexer;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring-test/rest.xml", "/spring-test/repo.xml", "/spring-test/indexer.xml" })
public class DublinCoreTest {
    private static final int SERVER_PORT = 8080;
    private static final String HOSTNAME = "localhost";
    private static final String serverAddress = "http://" + HOSTNAME + ":"
            + SERVER_PORT + "/";

    final private Logger logger = LoggerFactory
            .getLogger(DublinCoreTest.class);

    final private HttpClient client = new HttpClient();

    @Test
    public void testJcrPropertiesBasedOaiDc() throws Exception {
        PostMethod createObjMethod = new PostMethod(serverAddress
                + "rest/objects/fdsa");
        client.executeMethod(createObjMethod);

        GetMethod getWorstCaseOaiMethod = new GetMethod(serverAddress
                + "rest/objects/fdsa/oai_dc");
        int status = client.executeMethod(getWorstCaseOaiMethod);
        assertEquals(200, status);

        final String response = getWorstCaseOaiMethod.getResponseBodyAsString();
        assertTrue("Didn't find oai_dc!",
                compile("oai_dc", DOTALL).matcher(response).find());

        assertTrue("Didn't find dc:identifier!",
                compile("dc:identifier", DOTALL).matcher(response).find());
    }

    @Test
    public void testWellKnownPathOaiDc() throws Exception {
        PostMethod createObjMethod = new PostMethod(serverAddress
                + "rest/objects/lkjh");
        client.executeMethod(createObjMethod);


        PostMethod createDSMethod = new PostMethod(serverAddress
                + "rest/objects/lkjh/datastreams/DC");

        createDSMethod.setRequestEntity(new StringRequestEntity(
                "marbles for everyone", null, null));

        client.executeMethod(createDSMethod);

        GetMethod getWorstCaseOaiMethod = new GetMethod(serverAddress
                + "rest/objects/lkjh/oai_dc");
        int status = client.executeMethod(getWorstCaseOaiMethod);
        assertEquals(200, status);

        final String response = getWorstCaseOaiMethod.getResponseBodyAsString();
        assertTrue("Didn't find our datastream!",
                compile("marbles for everyone", DOTALL).matcher(response).find());
    }
}
