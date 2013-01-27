package org.fcrepo.modeshape;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class FedoraDatastreamsTest {

    int SERVER_PORT = 9999;

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
        assertEquals(201, status);
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
