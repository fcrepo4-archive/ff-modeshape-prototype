package org.fcrepo.ffmodeshapeprototype;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: cabeer
 * Date: 1/17/13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class FedoraObjectsTest {

    private TJWSEmbeddedJaxrsServer server;
    int SERVER_PORT = 9999;

    @Before
    public void start() {

        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(SERVER_PORT);
        server.getDeployment().getActualResourceClasses().add(FedoraObjects.class);
        server.start();
    }

    @After
    public void stop() {
        server.stop();
    }

    @Test
    public void testIngest() throws Exception {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf");
        int status = client.executeMethod(method);
        assertEquals(201, status);
    }

    @Test
    public void testGetObjectInXML() throws Exception {
        HttpClient client = new HttpClient();
        PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/fdsa");
        client.executeMethod(pmethod);

        GetMethod method = new GetMethod("http://localhost:" + SERVER_PORT + "/objects/fdsa");
        int status = client.executeMethod(method);
        assertEquals(200, status);
    }

    @Test
    public void testDeleteObject() throws Exception {
        HttpClient client = new HttpClient();
        PostMethod pmethod = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/asdf");
        client.executeMethod(pmethod);

        DeleteMethod method = new DeleteMethod("http://localhost:" + SERVER_PORT + "/objects/asdf");
        int status = client.executeMethod(method);
        assertEquals(204, status);
    }

    @Test
    public void testGetNextPid() throws Exception {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod("http://localhost:" + SERVER_PORT + "/objects/nextPID");
        int status = client.executeMethod(method);
        assertEquals(200, status);
    }
}
