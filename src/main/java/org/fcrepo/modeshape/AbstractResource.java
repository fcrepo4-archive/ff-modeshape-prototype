package org.fcrepo.modeshape;

import static com.google.common.collect.ImmutableSet.copyOf;
import static javax.ws.rs.core.Response.noContent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.fcrepo.modeshape.identifiers.PidMinter;
import org.modeshape.jcr.api.JcrTools;
import org.modeshape.jcr.api.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for Fedora JAX-RS Resources, providing convenience fields
 * and methods.
 * 
 * @author ajs6f
 * 
 */
public abstract class AbstractResource extends Constants {

	final private Logger logger = LoggerFactory
			.getLogger(AbstractResource.class);

	/**
	 * Useful for constructing URLs
	 */
	@Context
	UriInfo uriInfo;

	/**
	 * Jackson JSON mapper. Should eventually be replaced by the use of proper
	 * JAX-RS Providers.
	 */
	@Inject
	protected ObjectMapper mapper;

	/**
	 * The JCR repository at the heart of Fedora.
	 */
	@Inject
	protected Repository repo;

	/**
	 * A resource that can mint new Fedora PIDs.
	 */
	@Inject
	protected PidMinter pidMinter;

	static protected Workspace ws;

	/**
	 * A convenience object provided by ModeShape for acting against the JCR
	 * repository.
	 */
	final static protected JcrTools jcrTools = new JcrTools(true);

	@PostConstruct
	public void initialize() throws LoginException, NoSuchWorkspaceException,
			RepositoryException {

		final Session session = repo.login();
		session.getWorkspace().getNamespaceRegistry()
				.registerNamespace("test", "info:fedora/test");
		Node objects = jcrTools.findOrCreateNode(session, "/objects");
		objects.setProperty("size", 0L);
		session.save();
		session.logout();
	}

	protected synchronized Response deleteResource(final String path,
			Session session) throws RepositoryException {

		logger.debug("Attempting to delete resource at path: " + path);

		if (session.nodeExists(path)) {

			if (session.hasPermission(path, "remove")) {
				// ws.getLockManager().lock(path, true, true, Long.MAX_VALUE,
				// "");
				session.getNode(path).remove();
				session.save();
				session.logout();
				logger.debug("Finished deleting resource at path: " + path);
				return noContent().build();
			} else {
				return four03;
			}
		} else {
			return four04;
		}
	}

	public static Long getNodePropertySize(Node node)
			throws RepositoryException {
		Long size = 0L;
		PropertyIterator i = node.getProperties();
		while (i.hasNext()) {
			Property p = i.nextProperty();
			if (p.isMultiple()) {
				for (Value v : copyOf(p.getValues())) {
					size = size + v.getBinary().getSize();
				}
			} else {
				size = size + p.getBinary().getSize();
			}
		}
		return size;
	}

	protected void updateRepositorySize(Long change, Session session)
			throws PathNotFoundException, RepositoryException {
		Property sizeProperty = session.getNode("/objects").getProperty("size");
		Long previousSize = sizeProperty.getLong();
		sizeProperty.setValue(previousSize + change);
	}

	protected Long getRepositorySize(Session session)
			throws ValueFormatException, PathNotFoundException,
			RepositoryException {
		return session.getNode("/objects").getProperty("size").getLong();
	}
}
