package org.fcrepo.ffmodeshapeprototype.foxml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.fcrepo.ffmodeshapeprototype.AbstractResource;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ConfigurationException;
import org.modeshape.jcr.api.JcrTools;

@Path("/foxml")
public class FedoraOXML extends AbstractResource {

	private final JcrTools jcrtools = new JcrTools();

	private final Logger logger = Logger.getLogger(FedoraOXML.class);

	public FedoraOXML() throws ConfigurationException, RepositoryException,
			IOException {
		super();
	}

	@PUT
	@Path("/{filename}")
	// @Consumes("text/xml")
	public Response addFOXML(@PathParam("filename") final String filename,
			InputStream foxml) throws RepositoryException, IOException {

		final Session session = ws.getSession();
		if (session.hasPermission("/foxml", "add_node")) {
			final String foxmlpath = "/foxml/" + filename;
			logger.debug("Adding or updating FOXML file at " + ws.getName()
					+ ":" + foxmlpath);
			final Node foxmlnode = jcrtools.uploadFile(session, foxmlpath,
					foxml);
			session.save();
			/*
			 * fseq.execute( foxmlnode.getNode("jcr:content")
			 * .getProperty("jcr:data"), session.getRootNode()
			 * .addNode(filename,"nt:folder"), null);
			 */
			return Response.created(URI.create(foxmlnode.getPath())).build();
		} else
			return four01;
	}

	@GET
	@Path("/{filename}")
	public Response getFOXML(@PathParam("filename") final String filename)
			throws RepositoryException {

		final Node foxmlfolder = ws.getSession().getNode("/foxml");

		if (foxmlfolder.hasNode(filename)) {
			final Node foxmlfile = foxmlfolder.getNode(filename);
			return Response.ok(
					foxmlfile.getNode("jcr:content").getProperty("jcr:data")
							.getBinary().getStream(), "text/xml").build();
		} else
			return four04;
	}

	@GET
	@Path("/")
	public Response getFOXMLs() throws RepositoryException {
		Session session = ws.getSession();
		Node foxml = session.getNode("/foxml");
		StringBuffer nodes = new StringBuffer();

		for (NodeIterator i = foxml.getNodes(); i.hasNext();) {
			Node n = i.nextNode();
			nodes.append("Name: " + n.getName() + ", Path:" + n.getPath()
					+ "\n");
			for (NodeIterator j = n.getNodes(); j.hasNext();) {
				Node n2 = j.nextNode();
				nodes.append("\t Path:" + n2.getPath() + "\n");
			}
		}

		return Response.ok().entity(nodes.toString()).build();

	}
}
