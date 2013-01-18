package org.fcrepo.ffmodeshapeprototype;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FedoraDatastreamsTest {

    private TJWSEmbeddedJaxrsServer server;
    int SERVER_PORT = 9999;

    @Before
    public void start() throws Exception {

        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(SERVER_PORT);
        server.getDeployment().getActualResourceClasses().add(FedoraObjects.class);
        server.getDeployment().getActualResourceClasses().add(FedoraDatastreams.class);
        server.start();

    }

    @After
    public void stop() {
        server.stop();
    }

    @Test
    public void testGetDatastreams() throws Exception {
        HttpClient client = new HttpClient();

        PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf");
        client.executeMethod(pmethod);

        GetMethod method = new GetMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams");
        int status = client.executeMethod(method);
        assertEquals(200, status);
    }

    @Test
    public void testAddDatastream() throws Exception {

        HttpClient client = new HttpClient();

        PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf");
        client.executeMethod(pmethod);

        PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/zxc");
        int status = client.executeMethod(method);
        assertEquals(201, status);
    }

    @Test
    public void testMutateDatastream() throws Exception {

        HttpClient client = new HttpClient();

        PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf");
        client.executeMethod(pmethod);

        PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/vcxz");
        int status = client.executeMethod(method);
        assertEquals(201, status);


        PutMethod method_2 = new PutMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/vcxz");
        status = client.executeMethod(method_2);
        assertEquals(200, status);
    }

    @Test
    public void testGetDatastream() throws Exception {

        HttpClient client = new HttpClient();

        PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf");
        client.executeMethod(pmethod);

        GetMethod method_test_get = new GetMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/poiu");
        int status = client.executeMethod(method_test_get);
        assertEquals(404, status);

        PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/poiu");
        status = client.executeMethod(method);
        assertEquals(201, status);


        GetMethod method_2 = new GetMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/poiu");
        status = client.executeMethod(method_2);
        assertEquals(200, status);
    }

    @Test
    public void testGetDatastreamContent() throws Exception {

    }

    @Test
    public void testDeleteDatastream() throws Exception {

        HttpClient client = new HttpClient();

        PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf");
        client.executeMethod(pmethod);


        PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/lkjh");
        int status = client.executeMethod(method);
        assertEquals(201, status);


        GetMethod method_2 = new GetMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/lkjh");
        status = client.executeMethod(method_2);
        assertEquals(200, status);

        DeleteMethod dmethod = new DeleteMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/lkjh");
        status = client.executeMethod(dmethod);
        assertEquals(204, status);

        GetMethod method_test_get = new GetMethod("http://localhost:" + SERVER_PORT + "/objects/asdf/datastreams/lkjh");
        status = client.executeMethod(method_test_get);
        assertEquals(404, status);
    }
}
