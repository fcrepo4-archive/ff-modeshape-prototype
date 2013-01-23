package org.fcrepo.ffmodeshapeprototype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.fcrepo.ffmodeshapeprototype.identifiers.PidMinter;
import org.fcrepo.ffmodeshapeprototype.identifiers.UUIDPidMinter;
import org.modeshape.jcr.ConfigurationException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractResource {

	static final ObjectMapper mapper = new ObjectMapper();

	protected static final Response four01 = Response.status(404).entity("401")
			.build();
	protected static final Response four04 = Response.status(404).entity("404")
			.build();

	static protected Configuration freemarker = null;
	static protected Workspace ws = null;
	protected static PidMinter pidMinter = new UUIDPidMinter();

	public AbstractResource() throws ConfigurationException,
			RepositoryException, IOException {
		if (ws == null) {
			ws = Bootstrap.getWorkspace();
		}
		if (freemarker == null) {
			freemarker = Bootstrap.getFreemarker();
		}
	}

	protected Response deleteResource(final String path)
			throws RepositoryException {
		final Session session = ws.getSession();
		final Node root = session.getRootNode();
		if (root.hasNode(path)) {
			if (session.hasPermission("/" + path, "remove")) {
				root.getNode(path).remove();
				session.save();
				return Response.status(204).build();
			} else {
				return four01;
			}
		} else {
			return four04;
		}
	}

	protected InputStream renderTemplate(final String templatename,
			final Map<String, ?> map) throws RepositoryException, IOException,
			TemplateException {

		final Template template = freemarker.getTemplate(templatename);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		template.process(map, new OutputStreamWriter(out));
		InputStream in = new ByteArrayInputStream(out.toByteArray());
		out.close();
		return in;
	}

}
