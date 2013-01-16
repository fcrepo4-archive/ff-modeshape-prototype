package org.fcrepo.ffmodeshapeprototype;

import java.io.FileNotFoundException;

import javax.jcr.RepositoryException;
import javax.jcr.Workspace;

import org.infinispan.schematic.document.ParsingException;
import org.modeshape.common.SystemFailureException;
import org.modeshape.common.collection.Problems;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ConfigurationException;
import org.modeshape.jcr.JcrRepository;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.RepositoryConfiguration;

public abstract class AbstractResource {
	private final Logger logger = Logger.getLogger(AbstractResource.class);

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
	}
}
