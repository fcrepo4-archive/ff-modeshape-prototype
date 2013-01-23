package org.fcrepo.ffmodeshapeprototype;

import java.io.FileNotFoundException;
import java.util.Arrays;

import javax.jcr.RepositoryException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.infinispan.schematic.document.ParsingException;
import org.modeshape.common.SystemFailureException;
import org.modeshape.common.collection.Problems;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ConfigurationException;
import org.modeshape.jcr.JcrRepository;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.RepositoryConfiguration;
import org.modeshape.jcr.api.JcrTools;
import org.modeshape.jcr.api.Workspace;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;

public class Bootstrap implements ServletContextListener {
	ServletContext context;
	private static JcrRepository repository = null;

	private static Workspace ws = null;
	private static Configuration freemarker = null;
	private static Boolean initialized = false;

	private static final Logger logger = Logger.getLogger(Bootstrap.class);

	private static final JcrTools jcrTools = new JcrTools();

	public void contextInitialized(ServletContextEvent contextEvent) {
		logger.debug("Context Created");
		context = contextEvent.getServletContext();
		try {
			if (initialized == false)
				initializeEngine();
			initialized = true;
		} catch (RepositoryException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
	}

	public static JcrRepository getRepository() throws RepositoryException {
		if (repository == null)
			initializeEngine();
		return repository;
	}

	public static Workspace getWorkspace() throws RepositoryException {
		if (ws == null)
			initializeEngine();
		return ws;
	}

	public static Configuration getFreemarker() throws RepositoryException {
		if (freemarker == null)
			initializeEngine();
		return freemarker;
	}

	private static void initializeEngine() throws RepositoryException {
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

		final ModeShapeEngine engine = new ModeShapeEngine();

		if (engine == null || repository_config == null) {
			throw new SystemFailureException("Missing engine");
		}
		engine.start();
		repository = engine.deploy(repository_config);
		final JcrRepository repository = Bootstrap.getRepository();

		if (repository == null) {
			throw new RepositoryException("Missing repository?");
		}

		ws = repository.login().getWorkspace();

		String[] workspaceNames = ws.getAccessibleWorkspaceNames();

		if (!Arrays.asList(workspaceNames).contains("fedora")) {
			ws.createWorkspace("fedora");
			logger.debug("Created 'fedora' workspace.\n");
		}

		// switching to our new Fedora workspace
		ws = repository.login("fedora").getWorkspace();
		if (!Arrays.asList(ws.getNamespaceRegistry().getPrefixes()).contains(
				"test")) {
			ws.getNamespaceRegistry().registerNamespace("test", "test");
		}
		jcrTools.findOrCreateChild(ws.getSession().getRootNode(), "fedora");
		logger.debug("Deployed Fedora repository.");

		freemarker = new Configuration();
		logger.debug("Setting up Freemarker object wrapper");
		BeansWrapper objWrapper = new BeansWrapper();
		objWrapper.setExposureLevel(BeansWrapper.EXPOSE_ALL);
		freemarker.setObjectWrapper(objWrapper);
		// Specify the data source where the template files come from.
		freemarker.setClassForTemplateLoading(Bootstrap.class, "/freemarker");

	}
}
