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
public class FedoraRepository extends AbstractResource {
	
	public FedoraRepository() throws Exception, ConfigurationException,
			RepositoryException {
		super();
	}

	private final Logger logger = Logger.getLogger(FedoraRepository.class);

	@GET
	@Path("/describe")
	public Response describe() {
		return Response.status(200).entity(ws.getName()).build();
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
