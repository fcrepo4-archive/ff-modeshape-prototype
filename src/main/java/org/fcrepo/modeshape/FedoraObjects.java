package org.fcrepo.modeshape;

import java.io.IOException;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ConfigurationException;

import com.google.common.collect.ImmutableMap;

import freemarker.template.TemplateException;

@Path("/objects")
public class FedoraObjects extends AbstractResource {

	private static final Logger logger = Logger.getLogger(FedoraObjects.class);

	public FedoraObjects() throws ConfigurationException, RepositoryException,
			IOException {
		super();
	}

	@POST
	@Path("/new")
	public Response ingestAndMint() throws RepositoryException {
		return ingest(pidMinter.mintPid());
	}

	@POST
	@Path("/{pid}")
	public synchronized Response ingest(@PathParam("pid") final String pid)
			throws RepositoryException {

		logger.debug("Attempting to ingest with pid: " + pid);

		final Session session = ws.getSession();

		// final Node root = session.getRootNode();
		if (session.hasPermission("/" + pid, "add_node")) {
			final Node obj = jcrTools.findOrCreateNode(session, "/" + pid,
					"nt:folder");
			obj.addMixin("fedora:object");
			// ws.getLockManager().lock("/" + pid, false, true, Long.MAX_VALUE,
			// "");
			obj.addMixin("fedora:owned");
			obj.setProperty("fedora:ownerId", "Fedo Radmin");
			obj.setProperty("jcr:lastModified", Calendar.getInstance());
			session.save();
			return Response.status(Response.Status.CREATED).entity(pid).build();
		} else {
			return four01;
		}
	}

	@GET
	@Path("/{pid}")
	@Produces("text/xml")
	public Response getObjectInXML(@PathParam("pid") final String pid)
			throws RepositoryException, IOException, TemplateException {

		final Session session = ws.getSession();
		logger.debug("Working in repository: "
				+ session.getRepository().getDescriptor("custom.rep.name"));
		logger.debug("Working in workspace: " + ws.getName());

		if (session.nodeExists("/" + pid)) {
			final Node obj = session.getNode("/" + pid);
			PropertyIterator i = obj.getProperties();
			ImmutableMap.Builder<String, String> b = ImmutableMap.builder();
			while (i.hasNext()) {
				Property p = i.nextProperty();
				b.put(p.getName(), p.toString());
			}
			return Response
					.ok()
					.entity(renderTemplate("objectProfile.ftl", ImmutableMap
							.of("obj", obj, "properties", b.build()))).build();
		} else {
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
