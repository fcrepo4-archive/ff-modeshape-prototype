package org.fcrepo.ffmodeshapeprototype;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jcr.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.modeshape.jcr.ConfigurationException;

import freemarker.template.TemplateException;

@Path("/objects/{pid}/datastreams")
public class FedoraDatastreams extends AbstractResource {

	public FedoraDatastreams() throws ConfigurationException,
			RepositoryException {
		super();
	}

	@GET
	@Path("/")
	@Produces("text/xml")
	public Response getDatastreams(@PathParam("pid") final String pid)
			throws RepositoryException, IOException, TemplateException {
		Session session = ws.getSession();
		final Node root = session.getRootNode();
		StringBuffer nodes = new StringBuffer();

		if (root.hasNode(pid)) {
			Set<Node> datastreams = new HashSet<Node>();
			NodeIterator i = root.getNode(pid).getNodes();
			while (i.hasNext()) {
				datastreams.add(i.nextNode());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("datastreams", datastreams);

			InputStream in = renderTemplate("listDatastreams.ftl", map);
			return Response.status(200).entity(in).build();
		} else {
			return four04;
		}
	}

	@POST
	@Path("/{dsid}")
	public Response addOrMutateDatastream(@PathParam("pid") String pid,
			@PathParam("dsid") String dsid,
			@HeaderParam("Content-Type") MediaType contentType,
			InputStream requestBodyStream) throws RepositoryException {
		Session session = ws.getSession();
		Node root = session.getRootNode();
		if (session.hasPermission("/" + pid + "/" + dsid, "add_node")) {
			Node ds = root.addNode(pid + "/" + dsid);
			ds.setProperty("ownerId", "Fedo Radmin");
			ds.setProperty("contentType", contentType.toString());
			ds.setProperty("content",
					session.getValueFactory().createBinary(requestBodyStream));
			session.save();
			return Response.status(200).entity(ds.toString()).build();
		} else {
			return four01;
		}
	}

	@GET
	@Path("/{dsid}")
	public Response getDatastream(@PathParam("pid") String pid,
			@PathParam("dsid") String dsid) throws RepositoryException {
		return getResourceMetadata(pid + "/" + dsid);
	}

	@GET
	@Path("/{dsid}/content")
	public Response getDatastreamContent(@PathParam("pid") String pid,
			@PathParam("dsid") String dsid) throws RepositoryException {
		Session session = ws.getSession();
		Node root = session.getRootNode();

		if (root.hasNode(pid + "/" + dsid)) {
			Node ds = root.getNode(pid + "/" + dsid);
			Property p = ds.getProperty("contentType");

			String mimeType;

			if (ds.hasProperty("contentType")) {
				mimeType = ds.getProperty("contentType").getValue().getString();
			} else {
				mimeType = "application/octet-stream";
			}
			Response.ResponseBuilder responseBuilder = Response.ok(ds
					.getProperty("content").getBinary().getStream(), mimeType);
			responseBuilder.header("FedoraDatastreamId", dsid);
			return responseBuilder.build();
		} else {
			return four04;
		}
	}

	@DELETE
	@Path("/{dsid}")
	public Response deleteDatastream(@PathParam("pid") String pid,
			@PathParam("dsid") String dsid) throws RepositoryException {
		return deleteResource(pid + "/" + dsid);
	}
}
