package org.fcrepo.ffmodeshapeprototype;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.modeshape.jcr.ConfigurationException;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Path("/objects")
public class FedoraObjects extends AbstractResource {

	public FedoraObjects() throws ConfigurationException, RepositoryException {
		super();
	}

	@POST
	@Path("/{pid}")
	public Response ingest(@PathParam("pid") String pid)
			throws RepositoryException {
		Session session = ws.getSession();

		Node root = session.getRootNode();
		if (session.hasPermission("/" + pid, "add_node")) {
			Node obj = root.addNode(pid);
			obj.setProperty("ownerId", "Fedo Radmin");
			session.save();
			return Response.status(200).entity(obj.toString()).build();
		} else {
			return four01;
		}
	}

	@GET
	@Path("/{pid}")
	public Response getObject(@PathParam("pid") String pid)
			throws RepositoryException {
		return getResourceMetadata(pid);
	}

	@GET
	@Path("/{pid}")
	@Produces("text/xml")
	public Response getObjectInXML(@PathParam("pid") final String pid)
			throws RepositoryException, IOException, TemplateException {
		Session session = ws.getSession();
		final Node root = session.getRootNode();

		if (root.hasNode(pid)) {
			return Response
					.status(200)
					.entity(renderTemplate("objectProfile.ftl",
							root.getNode(pid))).build();
		} else {
			return four04;
		}
	}

	@DELETE
	@Path("/{pid}")
	public Response deleteObject(@PathParam("pid") String pid)
			throws RepositoryException {
		return deleteResource(pid);
	}
}
