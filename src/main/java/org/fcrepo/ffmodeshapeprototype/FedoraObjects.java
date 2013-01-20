package org.fcrepo.ffmodeshapeprototype;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.modeshape.jcr.ConfigurationException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import freemarker.template.TemplateException;

import java.util.Calendar;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

@Path("/objects")
public class FedoraObjects extends AbstractResource {

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
	public Response ingest(@PathParam("pid") final String pid)
			throws RepositoryException {
		final Session session = ws.getSession();
		final Node root = session.getRootNode();

		if (session.hasPermission("/" + pid, "add_node")) {
			final Node obj = root.addNode(pid, "nt:folder");
			obj.addMixin("fedora:object");
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

		final Node root = ws.getSession().getRootNode();

		if (root.hasNode(pid)) {
			return Response
					.ok()
					.entity(renderTemplate("objectProfile.ftl",
							ImmutableMap.of("obj", (Object) root.getNode(pid))))
					.build();
		} else {
			return four04;
		}
	}

	@DELETE
	@Path("/{pid}")
	public Response deleteObject(@PathParam("pid") final String pid)
			throws RepositoryException {
		return deleteResource(pid);
	}

}
