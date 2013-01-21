package org.fcrepo.ffmodeshapeprototype;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.modeshape.jcr.ConfigurationException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet.Builder;

import freemarker.template.TemplateException;

@Path("/objects/{pid}/datastreams")
public class FedoraDatastreams extends AbstractResource {

	public FedoraDatastreams() throws ConfigurationException,
			RepositoryException, IOException {
		super();
	}

	@GET
	@Path("/")
	@Produces("text/xml")
	public Response getDatastreams(@PathParam("pid") final String pid)
			throws RepositoryException, IOException, TemplateException {

		final Node root = ws.getSession().getRootNode();

		if (root.hasNode(pid)) {

			@SuppressWarnings("unchecked")
			final Builder<Node> datastreams = new Builder<Node>().addAll(root
					.getNode(pid).getNodes());
			final Map<String, Object> map = ImmutableMap.of("datastreams",
					(Object) datastreams.build());
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
                                          InputStream requestBodyStream) throws RepositoryException {
        final Session session = ws.getSession();
        final Node root = session.getRootNode();
        final VersionManager v = ws.getVersionManager();

        contentType = contentType != null ? contentType
                : MediaType.APPLICATION_OCTET_STREAM_TYPE;
        String dspath = pid + "/" + dsid;

        if (session.hasPermission("/" + dspath, "add_node")) {
            if (!root.hasNode(dspath)) {
                return Response
                        .status(Response.Status.CREATED)
                        .entity(addDatastreamNode(dspath, contentType,
                                requestBodyStream, session).toString()).build();
            } else {
                if (session.hasPermission("/" + dspath, "remove")) {
                    root.getNode(dspath).remove();
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
                                  InputStream requestBodyStream) throws RepositoryException {
        final Session session = ws.getSession();
        final Node root = session.getRootNode();
        final VersionManager v = ws.getVersionManager();

        contentType = contentType != null ? contentType
                : MediaType.APPLICATION_OCTET_STREAM_TYPE;
        String dspath = pid + "/" + dsid;

        if (session.hasPermission("/" + dspath, "add_node")) {
            if (!root.hasNode(dspath)) {
                return Response
                        .status(Response.Status.CREATED)
                        .entity(addDatastreamNode(dspath, contentType,
                                requestBodyStream, session).toString()).build();
            } else {
                if (session.hasPermission("/" + dspath, "remove")) {
                    root.getNode(dspath).remove();
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

	private Node addDatastreamNode(final String dspath,
			final MediaType contentType, final InputStream requestBodyStream,
			final Session session) throws ItemExistsException,
			PathNotFoundException, NoSuchNodeTypeException, LockException,
			VersionException, ConstraintViolationException, RepositoryException {
		final Node ds = session.getRootNode().addNode(dspath,
                "fedora:datastream");
		ds.addMixin("fedora:owned");
        ds.addMixin("fedora:created");
		ds.setProperty("fedora:ownerId", "Fedo Radmin");
		ds.setProperty("fedora:contentType", contentType.toString());
		ds.setProperty("fedora:content", session.getValueFactory()
				.createBinary(requestBodyStream));
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

		final Node root = ws.getSession().getRootNode();

		if (root.hasNode(pid + "/" + dsid)) {
			return Response
					.ok()
					.entity(renderTemplate("datastreamProfile.ftl",
							ImmutableMap.of("ds",
									(Object) root.getNode(pid + "/" + dsid),
									"obj", (Object) root.getNode(pid))))
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
					ds.getNode("jcr:content").getProperty("jcr:data").getBinary().getStream(),
					mimeType).build();
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
							ImmutableMap.of("ds", (Object) ds))).build();
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
