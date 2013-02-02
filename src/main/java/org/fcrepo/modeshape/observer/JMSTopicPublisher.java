package org.fcrepo.modeshape.observer;

import static com.google.common.collect.Iterables.any;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
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

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class JMSTopicPublisher {

	@Inject
	EventBus eventBus;

	@Inject
	private Repository repo;

	@Inject
	private ActiveMQConnectionFactory connectionFactory;

	private Connection connection;
	private Session session;
	private MessageProducer producer;

	final static private Abdera abdera = new Abdera();

	final private Logger logger = LoggerFactory
			.getLogger(JMSTopicPublisher.class);

	private OperationsMappings operationsMappings;

	@Subscribe
	public void publishJCREvent(Event jcrEvent) throws JMSException,
			RepositoryException, IOException {
		Entry entry = abdera.newEntry();
		entry.setTitle(operationsMappings.getFedoraMethodType(jcrEvent),
				Type.TEXT).setBaseUri("http://localhost:8080/rest");
		entry.addCategory("xsd:string", jcrEvent.getPath(), "fedora-types:pid");
		StringWriter writer = new StringWriter();
		entry.writeTo(writer);
		String atomMessage = writer.toString();
		producer.send(session.createTextMessage(atomMessage));

		logger.debug("Put event: \n" + atomMessage + "\n onto JMS.");
	}

	@PostConstruct
	public void acquireConnections() throws JMSException, LoginException,
			RepositoryException {
		logger.debug("Initializing " + this.getClass().getCanonicalName());

		operationsMappings = new OperationsMappings();

		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(session.createTopic("fedora"));
		eventBus.register(this);
	}

	@PreDestroy
	public void releaseConnections() throws JMSException {
		operationsMappings.session.logout();
		producer.close();
		session.close();
		connection.close();
		eventBus.unregister(this);
	}

	final private class OperationsMappings {

		// this actor will never mutate the state of the repo,
		// so we keep the session live for efficiency
		private javax.jcr.Session session;

		public String getFedoraMethodType(Event jcrEvent)
				throws PathNotFoundException, RepositoryException {

			// first determine if this is an object or a datastream
			Set<NodeType> nodeTypes = Sets.newHashSet(session.getNode(
					jcrEvent.getPath()).getMixinNodeTypes());
			Boolean isObject = any(nodeTypes, isObjectNodeType);
			Boolean isDatastream = any(nodeTypes, isDatastreamNodeType);

			// Now we can select from the combination of JCR Event type
			// and resource type to determine a Fedora Classic API method
			Integer eventType = jcrEvent.getType();
			if (isObject) {
				if (eventType == Event.NODE_ADDED)
					return "ingest";
				if (eventType == Event.NODE_REMOVED)
					return "purgeObject";
				if ((eventType == Event.PROPERTY_ADDED
						|| eventType == Event.PROPERTY_CHANGED || eventType == Event.PROPERTY_REMOVED))
					return "modifyObject";
			}
			if (isDatastream) {
				if (eventType == Event.NODE_ADDED)
					return "addDatastream";
				if (eventType == Event.NODE_REMOVED)
					return "purgeDatastream";
				if ((eventType == Event.PROPERTY_ADDED
						|| eventType == Event.PROPERTY_CHANGED || eventType == Event.PROPERTY_REMOVED))
					return "modifyDatstream";
			}
			return null;
		}

		private Predicate<NodeType> isObjectNodeType = new Predicate<NodeType>() {
			@Override
			public boolean apply(NodeType type) {
				return type.getName().equals("fedora:object");
			}
		};

		private Predicate<NodeType> isDatastreamNodeType = new Predicate<NodeType>() {
			@Override
			public boolean apply(NodeType type) {
				return type.getName().equals("fedora:datastream");
			}
		};

		OperationsMappings() throws LoginException, RepositoryException {
			session = repo.login();
		}

	}

}
