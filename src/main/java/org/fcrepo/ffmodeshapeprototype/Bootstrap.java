package org.fcrepo.ffmodeshapeprototype;


import org.infinispan.schematic.document.ParsingException;
import org.modeshape.common.SystemFailureException;
import org.modeshape.common.collection.Problems;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ConfigurationException;
import org.modeshape.jcr.JcrRepository;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.RepositoryConfiguration;

import javax.jcr.RepositoryException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileNotFoundException;

public class Bootstrap implements ServletContextListener{
    ServletContext context;
    private static JcrRepository repository;

    private static final Logger logger = Logger.getLogger(AbstractResource.class);

    public void contextInitialized(ServletContextEvent contextEvent) {
        System.out.println("Context Created");
        context = contextEvent.getServletContext();
        try {
            initializeEngine();
        } catch (RepositoryException e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent contextEvent) {
    }

    public static JcrRepository getRepository() throws RepositoryException {
        if(repository == null) {
            initializeEngine();
        }

        return repository;
    }


    private static JcrRepository initializeEngine() throws RepositoryException {
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


        logger.debug("Deployed repository.");

        return repository;
    }
}
