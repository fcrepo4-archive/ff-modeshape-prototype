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
		se.getRootNode().addNode("/simple-integration-test");
		se.save();
		se.logout();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Should be two messages:
		// - add node
		// - add property jcr:primaryType

		assertEquals("Where are my messages!?", (Integer) 2, messageCount);
	}

	@Subscribe
	public void countMessages(Event e) {
		messageCount++;
	}

}
