package org.fcrepo.modeshape.observer;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;

import org.modeshape.common.SystemFailureException;
import org.modeshape.jcr.api.Repository;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class DefaultFilter implements EventFilter {

	@Inject
	private Repository repository;

	// it's safe to keep the session around, because this code does not mutate
	// the state of the repository
	private Session session;

	@Override
	public boolean apply(Event event) {

		Predicate<NodeType> isFedoraNodeType = new Predicate<NodeType>() {
			@Override
			public boolean apply(NodeType type) {
				return type.getName().startsWith("fedora:");
			}
		};

		try {
			Node node = null;
			try {
				node = session.getNode(event.getPath());
			} catch (PathNotFoundException e) {
				return false; // not a node in the fedora workspace
			}
			Set<NodeType> types = Sets.newHashSet(node.getMixinNodeTypes());
			return Iterables.any(types, isFedoraNodeType);

		} catch (LoginException e) {
			throw new SystemFailureException(e);
		} catch (RepositoryException e) {
			throw new SystemFailureException(e);
		}
	}

	@PostConstruct
	public void acquireSession() throws LoginException, RepositoryException {
		session = repository.login();
	}

	@PreDestroy
	public void releaseSession() {
		session.logout();
	}
}