package org.fcrepo.modeshape;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.ok;
import static org.fcrepo.modeshape.jaxb.responses.ObjectProfile.ObjectStates.A;

import java.io.IOException;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.fcrepo.modeshape.jaxb.responses.ObjectProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/objects")
public class FedoraObjects extends AbstractResource {

	private static final Logger logger = LoggerFactory
			.getLogger(FedoraObjects.class);

	@GET
	public Response getObjects() throws RepositoryException {
		final Session session = repo.login();
		Node root = session.getRootNode();
		StringBuffer nodes = new StringBuffer();

		for (NodeIterator i = root.getNodes(); i.hasNext();) {
			Node n = i.nextNode();
			nodes.append("Name: " + n.getName() + ", Path:" + n.getPath()
					+ "\n");
		}
		session.logout();
		return ok(nodes.toString()).build();

	}

	@POST
	@Path("/new")
	public Response ingestAndMint() throws RepositoryException {
		return ingest(pidMinter.mintPid());
	}

	@POST
	@Path("/{pid}")
	public Response ingest(@PathParam("pid") final String pid)
			throws RepositoryException {

		logger.debug("Attempting to ingest with pid: " + pid);

		final Session session = repo.login();

		if (session.hasPermission("/" + pid, "add_node")) {
			final Node obj = jcrTools.findOrCreateNode(session, "/" + pid,
					"nt:folder");
			obj.addMixin("fedora:object");
			obj.addMixin("fedora:owned");
			obj.setProperty("fedora:ownerId", "Fedo Radmin");
			obj.setProperty("jcr:lastModified", Calendar.getInstance());
			session.save();
			session.logout();
			logger.debug("Finished ingest with pid: " + pid);
			return created(uriInfo.getAbsolutePath()).build();
		} else {
			session.logout();
			return four03;
		}
	}

	@GET
	@Path("/{pid}")
	@Produces({TEXT_XML, APPLICATION_JSON})
	public Response getObject(@PathParam("pid") final String pid)
			throws RepositoryException, IOException {

		final Session session = repo.login();

		if (session.nodeExists("/" + pid)) {

			final Node obj = session.getNode("/" + pid);
			final ObjectProfile objectProfile = new ObjectProfile();

			objectProfile.objLabel = obj.getName();
			objectProfile.objOwnerId = obj.getProperty("fedora:ownerId")
					.getString();
			objectProfile.objCreateDate = obj.getProperty("jcr:created")
					.getString();
			objectProfile.objLastModDate = obj.getProperty("jcr:lastModified")
					.getString();
			objectProfile.objItemIndexViewURL = uriInfo
					.getAbsolutePathBuilder().path("datastreams").build();
			objectProfile.objState = A;

			session.logout();
			return ok(objectProfile).build();
		} else {
			session.logout();
			return four04;
		}
	}

	@DELETE
	@Path("/{pid}")
	public Response deleteObject(@PathParam("pid") final String pid)
			throws RepositoryException {
		return deleteResource("/" + pid);
	}

}
