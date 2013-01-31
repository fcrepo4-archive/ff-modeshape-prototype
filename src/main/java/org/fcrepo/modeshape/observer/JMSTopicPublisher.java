package org.fcrepo.modeshape.observer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Text.Type;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
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

	final static private Abdera abdera = new Abdera();

	final private static Map<Integer, String> operationsMappings = ImmutableMap
			.of(Event.NODE_ADDED, "ingest");

	final private Logger logger = LoggerFactory
			.getLogger(JMSTopicPublisher.class);

	@Subscribe
	public void publishJCREvent(Event jcrEvent) throws JMSException,
			RepositoryException, IOException {
		Entry entry = abdera.newEntry();
		entry.addCategory("fedora-types:pid", jcrEvent.getPath(), "xsd:string");
		entry.setTitle(operationsMappings.get(jcrEvent.getType()), Type.TEXT);
		entry.setBaseUri("http://localhost:8080/rest");
		StringWriter writer = new StringWriter();
		entry.writeTo(writer);
		String atomMessage = writer.toString();
		producer.send(session.createTextMessage(atomMessage));

		logger.debug("Put event: \n" + atomMessage + "\n onto JMS.");
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
