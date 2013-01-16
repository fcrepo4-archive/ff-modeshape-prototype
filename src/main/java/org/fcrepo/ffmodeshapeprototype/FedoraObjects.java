package org.fcrepo.ffmodeshapeprototype;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.modeshape.jcr.ConfigurationException;

@Path("/objects")
public class FedoraObjects extends AbstractResource {

	public FedoraObjects() throws Exception, ConfigurationException,
			RepositoryException {
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
			return Response.status(401).entity("NO!").build();
		}
	}

	@GET
	@Path("/{pid}")
	public Response getObject(@PathParam("pid") String pid)
			throws RepositoryException {
		Session session = ws.getSession();
		Node root = session.getRootNode();

		if (root.hasNode(pid)) {
			return Response.status(200).entity(pid).build();
		} else {
			return Response.status(404).entity("404").build();
		}

	}
}
