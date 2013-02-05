package org.fcrepo.modeshape;

import static com.google.common.collect.ImmutableSet.builder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static org.fcrepo.modeshape.jaxb.responses.DatastreamProfile.DatastreamStates.A;
import static org.modeshape.jcr.api.JcrConstants.JCR_CONTENT;
import static org.modeshape.jcr.api.JcrConstants.JCR_DATA;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Collections;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
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

import org.fcrepo.modeshape.jaxb.responses.DatastreamHistory;
import org.fcrepo.modeshape.jaxb.responses.DatastreamProfile;
import org.fcrepo.modeshape.jaxb.responses.ObjectDatastreams;
import org.fcrepo.modeshape.jaxb.responses.ObjectDatastreams.Datastream;
import org.modeshape.jcr.api.Binary;
import org.modeshape.jcr.api.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet.Builder;

@Path("/objects/{pid}/datastreams")
public class FedoraDatastreams extends AbstractResource {

	final private Logger logger = LoggerFactory
			.getLogger(FedoraDatastreams.class);

	/**
	 * Returns a list of datastreams for the object
	 * 
	 * @param pid
	 *            persistent identifier of the digital object
	 * @return the list of datastreams
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws TemplateException
	 */

	@GET
	@Path("/")
	@Produces({ TEXT_XML, APPLICATION_JSON })
	public Response getDatastreams(@PathParam("pid") final String pid)
			throws RepositoryException, IOException {

		final Session session = repo.login();

		if (session.nodeExists("/" + pid)) {
			final ObjectDatastreams objectDatastreams = new ObjectDatastreams();
			final Builder<Datastream> datastreams = builder();

			NodeIterator i = session.getNode("/" + pid).getNodes();
			while (i.hasNext()) {
				final Node ds = i.nextNode();
				datastreams.add(new Datastream(ds.getName(), ds.getName(),
						getDSMimeType(ds)));
			}
			objectDatastreams.datastreams = datastreams.build();
			session.logout();
			return Response.ok().entity(objectDatastreams).build();
		} else {
			session.logout();
			return four04;
		}
	}

	/**
	 * Create a new datastream
	 * 
	 * @param pid
	 *            persistent identifier of the digital object
	 * @param dsid
	 *            datastream identifier
	 * @param contentType
	 *            Content-Type header
	 * @param requestBodyStream
	 *            Binary blob
	 * @return 201 Created
	 * @throws RepositoryException
	 * @throws IOException
	 */
	@POST
	@Path("/{dsid}")
	public Response addDatastream(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid,
			@HeaderParam("Content-Type") MediaType contentType,
			InputStream requestBodyStream) throws RepositoryException,
			IOException {
		final Session session = repo.login();

		contentType = contentType != null ? contentType
				: MediaType.APPLICATION_OCTET_STREAM_TYPE;
		String dspath = "/" + pid + "/" + dsid;

		if (!session.nodeExists("/" + pid)) {
			logger.debug("Tried to create a datastream for an object that doesn't exist, at resource path: "
					+ dspath);
			return Response.notAcceptable(null).build();
		}

		if (session.hasPermission(dspath, "add_node")) {
			if (!session.nodeExists(dspath)) {
				return Response.created(
						addDatastreamNode(dspath, contentType,
								requestBodyStream, session)).build();
			} else {
				if (session.hasPermission(dspath, "remove")) {
					session.getNode(dspath).remove();
					session.save();
					return Response.created(
							addDatastreamNode(dspath, contentType,
									requestBodyStream, session)).build();

				} else {
					session.logout();
					return four01;
				}
			}
		} else {
			session.logout();
			return four01;
		}
	}

	/**
	 * Modify an existing datastream's content
	 * 
	 * @param pid
	 *            persistent identifier of the digital object
	 * @param dsid
	 *            datastream identifier
	 * @param contentType
	 *            Content-Type header
	 * @param requestBodyStream
	 *            Binary blob
	 * @return 201 Created
	 * @throws RepositoryException
	 * @throws IOException
	 */
	@PUT
	@Path("/{dsid}")
	public Response modifyDatastream(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid,
			@HeaderParam("Content-Type") MediaType contentType,
			InputStream requestBodyStream) throws RepositoryException,
			IOException {
		final Session session = repo.login();

		contentType = contentType != null ? contentType
				: MediaType.APPLICATION_OCTET_STREAM_TYPE;
		String dspath = "/" + pid + "/" + dsid;

		if (session.hasPermission(dspath, "add_node")) {
			return Response.created(
					addDatastreamNode(dspath, contentType, requestBodyStream,
							session)).build();
		} else {
			session.logout();
			return four01;
		}
	}

	private URI addDatastreamNode(final String dsPath,
			final MediaType contentType, final InputStream requestBodyStream,
			final Session session) throws RepositoryException, IOException {

		logger.debug("Attempting to add datastream node at path: " + dsPath);
		Boolean created = false;
		if (!session.nodeExists(dsPath)) {
			created = true;
		}

		final Node ds = jcrTools.findOrCreateNode(session, dsPath,
				JcrConstants.NT_FILE);
		ds.addMixin("fedora:datastream");
		final Node contentNode = jcrTools.findOrCreateChild(ds,
				JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
		logger.debug("Created content node at path: " + contentNode.getPath());
		/*
		 * This next line of code deserves explanation. If we chose for the
		 * simpler line:
		 * 
		 * Property dataProperty = contentNode.setProperty("jcr:data",
		 * requestBodyStream);
		 * 
		 * then the JCR would not block on the stream's completion, and we would
		 * return to the requestor before the mutation to the repo had actually
		 * completed. So instead we use createBinary(requestBodyStream), because
		 * its contract specifies:
		 * 
		 * "The passed InputStream is closed before this method returns either
		 * normally or because of an exception."
		 * 
		 * which lets us block and not return until the job is done! The simpler
		 * code may still be useful to us for an asychronous method that we
		 * develop later.
		 */
		Property dataProperty = contentNode.setProperty(JcrConstants.JCR_DATA,
				session.getValueFactory().createBinary(requestBodyStream));
		logger.debug("Created data property at path: " + dataProperty.getPath());

		ds.setProperty("fedora:contentType", contentType.toString());

		ds.addMixin("fedora:owned");
		ds.setProperty("fedora:ownerId", "Fedo Radmin");
		if (created) {
			ds.setProperty("fedora:created", Calendar.getInstance());
		}
		ds.setProperty("jcr:lastModified", Calendar.getInstance());
		session.save();
		session.logout();
		logger.debug("Finished adding datastream node at path: " + dsPath);
		return uriInfo.getAbsolutePath();
	}

	/**
	 * Get the datastream profile of a datastream
	 * 
	 * @param pid
	 *            persistent identifier of the digital object
	 * @param dsid
	 *            datastream identifier
	 * @return 200
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws TemplateException
	 */
	@GET
	@Path("/{dsid}")
	@Produces("text/xml")
	public Response getDatastream(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException,
			IOException {

		Session session = repo.login();

		if (!session.getRootNode().hasNode(pid)) {
			return four04;
		}

		final Node obj = session.getNode("/" + pid);

		if (obj.hasNode(dsid)) {
			final Node ds = obj.getNode(dsid);
			DatastreamProfile dsProfile = new DatastreamProfile();
			dsProfile.dsID = dsid;
			dsProfile.pid = pid;
			dsProfile.dsLabel = dsid;
			dsProfile.dsState = A;
			dsProfile.dsMIME = getDSMimeType(ds);
			dsProfile.dsCreateDate = ds.getProperty("jcr:created").getString();
			session.logout();
			return Response.ok(dsProfile).build();
		} else {
			session.logout();
			return four04;
		}
	}

	/**
	 * Get the binary content of a datastream
	 * 
	 * @param pid
	 *            persistent identifier of the digital object
	 * @param dsid
	 *            datastream identifier
	 * @return Binary blob
	 * @throws RepositoryException
	 */
	@GET
	@Path("/{dsid}/content")
	public Response getDatastreamContent(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException {

		final Session session = repo.login();
		final String dsPath = "/" + pid + "/" + dsid;

		if (session.nodeExists(dsPath)) {
			final Node ds = session.getNode(dsPath);
			final String mimeType = ds.hasProperty("fedora:contentType") ? ds
					.getProperty("fedora:contentType").getString()
					: "application/octet-stream";
			final InputStream responseStream = ds
					.getNode(JcrConstants.JCR_CONTENT)
					.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
			session.logout();
			return Response.ok(responseStream, mimeType).build();
		} else {
			session.logout();
			return four04;
		}
	}

	/**
	 * Get previous version information for this datastream
	 * 
	 * @param pid
	 *            persistent identifier of the digital object
	 * @param dsid
	 *            datastream identifier
	 * @return 200
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws TemplateException
	 */
	@GET
	@Path("/{dsid}/versions")
	@Produces("text/xml")
	// TODO implement this after deciding on a versioning model
	public Response getDatastreamHistory(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException,
			IOException {

		final Session session = repo.login();
		final String dsPath = "/" + pid + "/" + dsid;

		if (session.nodeExists(dsPath)) {
			final Node ds = session.getNode(dsPath);
			session.logout();
			return Response
					.ok()
					.entity(new DatastreamHistory(Collections
							.singletonList(new DatastreamProfile()))).build();
		} else {
			session.logout();
			return four04;
		}
	}

	/**
	 * Get previous version information for this datastream. See
	 * /{dsid}/versions. Kept for compatibility with fcrepo <3.5 API.
	 * 
	 * @deprecated
	 * 
	 * @param pid
	 *            persistent identifier of the digital object
	 * @param dsid
	 *            datastream identifier
	 * @return 200
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws TemplateException
	 */
	@GET
	@Path("/{dsid}/history")
	@Produces("text/xml")
	@Deprecated
	public Response getDatastreamHistoryOld(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException,
			IOException {
		return getDatastreamHistory(pid, dsid);
	}

	/**
	 * Purge the datastream
	 * 
	 * @param pid
	 *            persistent identifier of the digital object
	 * @param dsid
	 *            datastream identifier
	 * @return 204
	 * @throws RepositoryException
	 */
	@DELETE
	@Path("/{dsid}")
	public Response deleteDatastream(@PathParam("pid") String pid,
			@PathParam("dsid") String dsid) throws RepositoryException {
		return deleteResource("/" + pid + "/" + dsid);
	}

	private String getDSMimeType(Node ds) throws ValueFormatException,
			PathNotFoundException, RepositoryException, IOException {
		Binary b = (Binary) ds.getNode(JCR_CONTENT).getProperty(JCR_DATA)
				.getBinary();
		return b.getMimeType();
	}
}
