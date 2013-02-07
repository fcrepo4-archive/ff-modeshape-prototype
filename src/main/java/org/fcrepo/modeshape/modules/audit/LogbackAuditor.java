package org.fcrepo.modeshape.modules.audit;

import static org.fcrepo.modeshape.utils.EventType.getEventName;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

import com.google.common.eventbus.Subscribe;

/**
 * A proof-of-concept Auditor implementation that uses Logback.
 * 
 * @author Edwin Shin
 * @author ajs6f
 */
public class LogbackAuditor implements Auditor {

	@Inject
	private Appender<ILoggingEvent> appender;

	Logger logger = (Logger) LoggerFactory.getLogger(LogbackAuditor.class);

	@PostConstruct
	public void setLoggerLevel() {
		logger.setLevel(Level.ALL);
		Iterator<Appender<ILoggingEvent>> i = logger.iteratorForAppenders();
		while (i.hasNext())
			logger.detachAppender(i.next().getName());
		logger.addAppender(appender);
	}

	@Override
	@Subscribe
	public void recordEvent(Event e) throws RepositoryException {
		logger.info(e.getUserID() + " " + getEventName(e.getType()) + " "
				+ e.getPath());
	}
	
	public LogbackAuditor() {
	}

	@Inject
	public LogbackAuditor(Appender<ILoggingEvent> appender) {
		this.appender = appender;
	}

}
