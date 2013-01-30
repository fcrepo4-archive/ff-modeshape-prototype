package org.fcrepo.modeshape.observer;

import com.google.common.base.Predicate;

import javax.jcr.observation.Event;

public class DefaultFilter implements Predicate<Event> {
    @Override
    public boolean apply(Event input) {
        return true;
    }
}
