package org.fcrepo.modeshape.observer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javax.inject.Inject;
import javax.jcr.*;
import javax.jcr.observation.Event;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/eventing.xml")
public class SimpleObserverTest {

	private Integer messageCount = 0;

	@Inject
	private Repository repository;

	@Inject
	private SimpleObserver o;

	@Inject
	private EventBus eventBus;

	@Test
	public void TestSimpleIntegration() throws RepositoryException {

		eventBus.register(this);

		Session se = repository.login();
		Node testnode = se.getRootNode().addNode("/object1");
		testnode.addMixin("fedora:object");
		Node testnode2 = se.getRootNode().addNode("/object2");
		testnode2.addMixin("fedora:object");
		se.save();
		se.logout();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Should be two messages, for each time
		// each node becomes a Fedora object

		assertEquals("Where are my messages!?", (Integer) 2, messageCount);
	}

	@Subscribe
	public void countMessages(Event e) {
		messageCount++;
	}

}
