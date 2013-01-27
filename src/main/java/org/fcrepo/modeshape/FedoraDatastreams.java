package org.fcrepo.modeshape;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import freemarker.template.TemplateException;

@Path("/objects/{pid}/datastreams")
public class FedoraDatastreams extends AbstractResource {

	@GET
	@Path("")
	@Produces("text/xml")
	public Response getDatastreams(@PathParam("pid") final String pid)
			throws RepositoryException, IOException, TemplateException {

		final Node root = ws.getSession().getRootNode();

		if (root.hasNode(pid)) {

			@SuppressWarnings("unchecked")
			final Builder<Node> datastreams = new Builder<Node>().addAll(root
					.getNode(pid).getNodes());
			final Map<String, ImmutableSet<Node>> map = ImmutableMap.of(
					"datastreams", datastreams.build());
			return Response.ok()
					.entity(renderTemplate("listDatastreams.ftl", map)).build();
		} else {
			return four04;
		}
	}

	@POST
	@Path("/{dsid}")
	public Response addDatastream(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid,
			@HeaderParam("Content-Type") MediaType contentType,
			InputStream requestBodyStream) throws RepositoryException,
			IOException {
		final Session session = ws.getSession();

		contentType = contentType != null ? contentType
				: MediaType.APPLICATION_OCTET_STREAM_TYPE;
		String dspath = "/" + pid + "/" + dsid;

		if (session.hasPermission(dspath, "add_node")) {
			if (!session.nodeExists(dspath)) {
				return Response
						.status(Response.Status.CREATED)
						.entity(addDatastreamNode(dspath, contentType,
								requestBodyStream, session).toString()).build();
			} else {
				if (session.hasPermission(dspath, "remove")) {
					session.getNode(dspath).remove();
					session.save();
					return Response
							.ok()
							.entity(addDatastreamNode(dspath, contentType,
									requestBodyStream, session).toString())
							.build();

				} else
					return four01;
			}
		} else {
			return four01;
		}
	}

	@PUT
	@Path("/{dsid}")
	public Response modifyDatastream(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid,
			@HeaderParam("Content-Type") MediaType contentType,
			InputStream requestBodyStream) throws RepositoryException,
			IOException {
		final Session session = ws.getSession();

		contentType = contentType != null ? contentType
				: MediaType.APPLICATION_OCTET_STREAM_TYPE;
		String dspath = "/" + pid + "/" + dsid;

		if (session.hasPermission(dspath, "add_node")) {

			return Response
					.status(Response.Status.CREATED)
					.entity(addDatastreamNode(dspath, contentType,
							requestBodyStream, session).toString()).build();

		} else {
			return four01;
		}
	}

	private Node addDatastreamNode(final String dspath,
			final MediaType contentType, final InputStream requestBodyStream,
			final Session session) throws RepositoryException, IOException {

		Boolean created = false;
		if (!session.nodeExists(dspath)) {
			created = true;
		}
		final Node ds = jcrTools.uploadFile(session, dspath, requestBodyStream);
		ds.addMixin("fedora:datastream");
		ds.setProperty("fedora:contentType", contentType.toString());

		ds.addMixin("fedora:owned");
		ds.setProperty("fedora:ownerId", "Fedo Radmin");
		if (created) {
			ds.setProperty("fedora:created", Calendar.getInstance());
		}
		ds.setProperty("jcr:lastModified", Calendar.getInstance());

		session.save();
		return ds;
	}

	@GET
	@Path("/{dsid}")
	@Produces("text/xml")
	public Response getDatastream(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException,
			IOException, TemplateException {

		final Node obj = ws.getSession().getNode("/" + pid);

		if (obj.hasNode(dsid)) {
			Node ds = obj.getNode(dsid);
			PropertyIterator i = ds.getProperties();
			ImmutableMap.Builder<String, String> b = new ImmutableMap.Builder<String, String>();
			while (i.hasNext()) {
				Property p = i.nextProperty();
				b.put(p.getName(), p.toString());
			}
			return Response
					.ok()
					.entity(renderTemplate("datastreamProfile.ftl",
							ImmutableMap.of("ds", ds, "properties", b.build())))
					.build();
		} else {
			return four04;
		}
	}

	@GET
	@Path("/{dsid}/content")
	public Response getDatastreamContent(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException {

		final Node root = ws.getSession().getRootNode();

		if (root.hasNode(pid + "/" + dsid)) {
			final Node ds = root.getNode(pid + "/" + dsid);
			final String mimeType = ds.hasProperty("fedora:contentType") ? ds
					.getProperty("fedora:contentType").getString()
					: "application/octet-stream";
			return Response.ok(
					ds.getNode("jcr:content").getProperty("jcr:data")
							.getBinary().getStream(), mimeType).build();
		} else {
			return four04;
		}
	}

	@GET
	@Path("/{dsid}/versions")
	@Produces("text/xml")
	public Response getDatastreamHistory(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException,
			IOException, TemplateException {
		final Node root = ws.getSession().getRootNode();
		if (root.hasNode(pid + "/" + dsid)) {
			final Node ds = root.getNode(pid + "/" + dsid);
			return Response
					.ok()
					.entity(renderTemplate("datastreamHistory.ftl",
							ImmutableMap.of("ds", ds))).build();
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
