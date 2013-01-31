package org.fcrepo.modeshape.observer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.jcr.observation.Event;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class JMSTopicPublisher {

	@Inject
	EventBus eventBus;

	@Inject
	private ActiveMQConnectionFactory connectionFactory;

	private Connection connection;
	private Session session;
	private MessageProducer producer;

	final private Logger logger = LoggerFactory
			.getLogger(JMSTopicPublisher.class);

	@Subscribe
	public void publishJCREvent(Event jcrEvent) throws JMSException {
		logger.debug("Putting event: " + jcrEvent.toString() + "onto JMS.");
		producer.send(session.createTextMessage(jcrEvent.toString()));
	}

	@PostConstruct
	public void acquireConnections() throws JMSException {
		logger.debug("Initializing " + this.getClass().getCanonicalName());
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(session.createTopic("fedora"));
		eventBus.register(this);
	}

	@PreDestroy
	public void releaseConnections() throws JMSException {
		producer.close();
		session.close();
		connection.close();
		eventBus.unregister(this);
	}
}
