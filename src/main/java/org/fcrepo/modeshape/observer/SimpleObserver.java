package org.fcrepo.modeshape.observer;

import com.google.common.base.Predicate;
import org.modeshape.jcr.api.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.Queue;

public class SimpleObserver implements EventListener {


    @Inject
    private Repository repository;

    private Session session;
    private ObservationManager observationManager;

    @Resource(name="fedoraInternalEventQueue") public Queue<Event> outQueue;

    @Resource(name="fedoraEventFilter") private Predicate<Event> eventPredicate;


    @PostConstruct
    public void buildListener() throws RepositoryException {
        session = repository.login("fedora");
        observationManager = session.getWorkspace().getObservationManager();

        observationManager.addEventListener(this, 63, "/", true, null, null, false);
    }

    @Override
    public void onEvent(EventIterator events) {
        while(events.hasNext()) {
            Event e = events.nextEvent();

            if(eventPredicate.apply(e)) {
                outQueue.add(e);
            }
        }

    }

}
