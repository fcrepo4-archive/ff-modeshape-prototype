package org.fcrepo.modeshape.observer;

import org.modeshape.jcr.api.Repository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestObserver implements EventListener {


    @Inject
    private Repository repository;

    private Session session;
    private ObservationManager observationManager;


    @PostConstruct
    public void buildListener() throws RepositoryException {
        session = repository.login("fedora");
        observationManager = session.getWorkspace().getObservationManager();

        observationManager.addEventListener(this, 63, "/", true, null, null, false);
    }

    @Override
    public void onEvent(EventIterator events) {
        System.out.println(events.toString());

    }

}
