package org.fcrepo.modeshape.modules.audit;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

/**
 * A proof-of-concept Auditor implementation that uses Logback.
 *
 * @author Edwin Shin
 */
public class LogbackAuditor implements Auditor {

    Logger logger = LoggerFactory.getLogger(LogbackAuditor.class);

    /**
     * 
     */
    @Override
    @Subscribe
    public void recordEvent(Event e) throws RepositoryException {
        logger.info(e.getUserID() + " " + getEventName(e.getType()) + " " +
                e.getPath());
    }

    /**
     * Static utility method to retrieve a String representation of the type
     * defined by a {@link javax.jcr.observation.Event JCR event}.
     *
     * @param jcrEvent
     * @return Event name of the given JCR event type.
     * @throws IllegalArgumentException if the given int does not represent a
     * valid type constants as defined by {@link Event}.<br>
     */
    private static String getEventName(int jcrEvent) {
        String eventName;
        switch (jcrEvent) {
            case Event.NODE_ADDED:
                eventName = "node added";
                break;
            case Event.NODE_REMOVED:
                eventName = "node removed";
                break;
            case Event.PROPERTY_ADDED:
                eventName = "property added";
                break;
            case Event.PROPERTY_CHANGED:
                eventName = "property changed";
                break;
            case Event.PROPERTY_REMOVED:
                eventName = "property removed";
                break;
            case Event.NODE_MOVED:
                eventName = "node moved";
                break;
            case Event.PERSIST:
                eventName = "persist";
                break;
            default: // no default
                throw new IllegalArgumentException("Invalid JCR event type: " +
                        jcrEvent);
        }
        return eventName;
    }
}
