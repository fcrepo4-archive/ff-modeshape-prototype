package org.fcrepo.ffmodeshapeprototype;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.ws.rs.core.Response;

import org.infinispan.schematic.document.ParsingException;
import org.modeshape.common.SystemFailureException;
import org.modeshape.common.collection.Problems;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ConfigurationException;
import org.modeshape.jcr.JcrRepository;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.RepositoryConfiguration;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractResource {

	static final Response four01 = Response.status(404).entity("401").build();
	static final Response four04 = Response.status(404).entity("404").build();

	private final Logger logger = Logger.getLogger(AbstractResource.class);

	static protected Configuration freemarker = null;
	static protected Workspace ws = null;

	public AbstractResource() throws ConfigurationException,
			RepositoryException {
		if (ws == null) {
			RepositoryConfiguration repository_config = null;
			try {
				repository_config = RepositoryConfiguration
						.read("my_repository.json");
				Problems problems = repository_config.validate();

				if (problems.hasErrors()) {
					throw new ConfigurationException(problems,
							"Problems starting the engine.");
				}

			} catch (ParsingException ex) {
				logger.error(ex, null, (Object) null);
			} catch (FileNotFoundException ex) {
				logger.error(ex, null, (Object) null);
			}

			ModeShapeEngine engine = new ModeShapeEngine();

			if (engine == null || repository_config == null) {
				throw new SystemFailureException("Missing engine");
			}
			logger.debug("Started ModeShape engine.\n");
			engine.start();
			JcrRepository repository = engine.deploy(repository_config);
			logger.debug("Deployed repository.");
			ws = repository.login().getWorkspace();
			ws.createWorkspace("fedora");
			logger.debug("Created 'fedora' workspace.\n");
		}

		if (freemarker == null) {
			freemarker = new Configuration();
			freemarker.setObjectWrapper(new DefaultObjectWrapper());
			// Specify the data source where the template files come from.
			freemarker.setClassForTemplateLoading(this.getClass(),
					"/freemarker");
		}
	}

	protected Response getResourceMetadata(String path)
			throws RepositoryException {
		Session session = ws.getSession();
		Node root = session.getRootNode();

		if (root.hasNode(path)) {
			return Response.status(200).entity(root.getNode(path).toString())
					.build();
		} else {
			return four04;
		}
	}

	protected Response deleteResource(String path) throws RepositoryException {
		Session session = ws.getSession();
		Node root = session.getRootNode();
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
			final Node node) throws RepositoryException, IOException {

		final Template template = freemarker.getTemplate(templatename);
		final PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out = new PipedOutputStream(in);
		new Thread(new Runnable() {
			public void run() {
				try {
					template.process(new HashMap<String, Node>() {
						{
							put("node", node);
						}
					}, new OutputStreamWriter(out));
					out.close();
				} catch (TemplateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}).start();
		return in;

	}
}
