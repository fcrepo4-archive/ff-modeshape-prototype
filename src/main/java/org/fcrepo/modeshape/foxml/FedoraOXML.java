package org.fcrepo.modeshape.foxml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.fcrepo.modeshape.AbstractResource;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.api.JcrTools;

import com.google.common.collect.ImmutableMap;

@Path("/foxml")
public class FedoraOXML extends AbstractResource {

	private final JcrTools jcrtools = new JcrTools();

	private final Logger logger = Logger.getLogger(FedoraOXML.class);

	@PUT
	@Path("/{filename}")
	@Consumes("text/xml")
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
	public Response getFOXMLs() throws RepositoryException,
			JsonGenerationException, JsonMappingException, IOException {

		Node foxml = ws.getSession().getNode("/foxml");

		ImmutableMap.Builder<String, String> b = ImmutableMap.builder();
		for (NodeIterator i = foxml.getNodes(); i.hasNext();) {
			Node n = i.nextNode();
			b.put(n.getName(), n.getPath());
		}

		return Response
				.ok()
				.entity(mapper.writerWithType(Map.class).writeValueAsString(
						b.build())).build();

	}
}
