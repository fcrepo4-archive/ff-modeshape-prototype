package org.fcrepo.ffmodeshapeprototype;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ConfigurationException;

/**
 * 
 * @author cabeer
 * @author ajs6f
 */

@Path("/")
public class Server extends AbstractResource {
	
	public Server() throws Exception, ConfigurationException,
			RepositoryException {
		super();
	}

	private final Logger logger = Logger.getLogger(Server.class);

	@GET
	@Path("/describe")
	public Response describe() {
		return Response.status(200).entity(ws.getName()).build();
	}

	@POST
	@Path("/objects/{pid}")
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
	@Path("/objects/{pid}")
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

	@GET
	@Path("/objects")
	public Response getObjects() throws RepositoryException {
		Session session = ws.getSession();
		Node root = session.getRootNode();

		StringBuffer nodes = new StringBuffer();
		NodeIterator i = root.getNodes();
		while (i.hasNext()) {
			Node n = i.nextNode();
			nodes.append("Name: " + n.getName() + ", Path:" + n.getPath()
					+ "\n");
		}
		root.getNodes();
		return Response.status(200).entity(nodes.toString()).build();

	}
}
