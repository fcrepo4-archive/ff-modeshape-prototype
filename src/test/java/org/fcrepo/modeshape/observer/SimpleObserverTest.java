package org.fcrepo.modeshape.observer;

import static junit.framework.Assert.assertEquals;

import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring/eventing.xml", "/spring/repo.xml" })
public class SimpleObserverTest implements MessageListener {

	private Integer eventBusMessageCount;
	private Integer jmsMessageCount;

	@Inject
	private Repository repository;

	@Inject
	private SimpleObserver o;

	@Inject
	private EventBus eventBus;

	@Inject
	private ActiveMQConnectionFactory connectionFactory;

	private Connection connection;
	private javax.jms.Session session;

	@Test
	public void TestEventBusPublishing() throws RepositoryException {

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

		assertEquals("Where are my messages!?", (Integer) 2,
				eventBusMessageCount);

	}

	@Test
	public void TestJMSPublishing() throws LoginException, RepositoryException {
		Session se = repository.login();
		Node testnode = se.getRootNode().addNode("/object3");
		testnode.addMixin("fedora:object");
		Node testnode2 = se.getRootNode().addNode("/object4");
		testnode2.addMixin("fedora:object");
		se.save();
		se.logout();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals("Where are my messages!?", (Integer) 2, jmsMessageCount);
	}

	@Subscribe
	public void countMessages(Event e) {
		eventBusMessageCount++;
	}

	@Override
	public void onMessage(Message message) {
		jmsMessageCount++;
	}

	@Before
	public void acquireConnections() throws JMSException {
		eventBusMessageCount = 0;
		jmsMessageCount = 0;
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false,
				javax.jms.Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(session
				.createTopic("fedora"));
		consumer.setMessageListener(this);

		eventBus.register(this);
	}

	@After
	public void releaseConnections() throws JMSException {
		session.close();
		connection.close();

		eventBus.unregister(this);
	}
}
