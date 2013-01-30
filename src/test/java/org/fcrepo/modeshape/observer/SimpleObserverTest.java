package org.fcrepo.modeshape.observer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.jcr.*;

import static junit.framework.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/eventing.xml")
public class SimpleObserverTest {

    @Inject
    private Repository repository;

    @Inject
    private SimpleObserver o;

    @Test
    public void TestSimpleIntegration() throws RepositoryException {

        Session se = repository.login();
        Workspace ws = se.getWorkspace();

        Node n = se.getRootNode();
        n.addNode("/simple-integration-test");

        se.save();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Should be two messages:
        //    - add node
        //    - add property jcr:primaryType

        assertEquals(2, o.outQueue.size());
    }
}
