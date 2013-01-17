package org.fcrepo.ffmodeshapeprototype;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.modeshape.jcr.ConfigurationException;

import com.google.common.collect.ImmutableSet.Builder;

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

		if (root.hasNode(pid)) {
			Builder<Node> datastreams = new Builder<Node>();
			datastreams.addAll(root.getNode(pid).getNodes());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("datastreams", datastreams.build());
			return Response.ok()
					.entity(renderTemplate("listDatastreams.ftl", map)).build();
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
			return Response.ok().entity(ds.toString()).build();
		} else {
			return four01;
		}
	}

	@GET
	@Path("/{dsid}")
	@Produces("text/xml")
	public Response getDatastream(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException, IOException, TemplateException {
		Session session = ws.getSession();
		final Node root = session.getRootNode();
		if (root.hasNode(pid + "/" + dsid)) {
			return Response.ok()
					.entity(renderTemplate("datastreamProfile.ftl",  new HashMap<String,Object>() {
						{
							put("ds", root.getNode(pid+ "/" + dsid));
						}
					}))
					.build();
		} else {
			return four04;
		}
	}

	@GET
	@Path("/{dsid}/content")
	public Response getDatastreamContent(@PathParam("pid") String pid,
			@PathParam("dsid") String dsid) throws RepositoryException {
		Session session = ws.getSession();
		Node root = session.getRootNode();

		if (root.hasNode(pid + "/" + dsid)) {
			Node ds = root.getNode(pid + "/" + dsid);
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
