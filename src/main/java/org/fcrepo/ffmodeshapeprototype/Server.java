/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fcrepo.ffmodeshapeprototype;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.logging.Level;

import org.modeshape.common.i18n.TextI18n;
import org.modeshape.common.logging.Logger;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.modeshape.common.collection.Problems;
import org.infinispan.schematic.document.ParsingException;
import org.modeshape.jcr.ConfigurationException;
import org.modeshape.jcr.JcrRepository;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.RepositoryConfiguration;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.modeshape.jcr.JcrSession;

/**
 * 
 * @author cabeer
 */

@Path("/")
public class Server {
	private ModeShapeEngine engine;
	private JcrRepository repository;
	private final Logger logger = Logger.getLogger(Server.class.getName());

	public Server() throws Exception, ConfigurationException,
			RepositoryException {
		RepositoryConfiguration repository_config = null;
		try {
			repository_config = RepositoryConfiguration
					.read("my_repository.json");
			Problems problems = repository_config.validate();

			if (problems.hasErrors()) {
				System.err.println("Problems starting the engine.");
				System.err.println(problems);
				throw new Exception("Problems starting the engine.");
			}

		} catch (ParsingException ex) {
			logger.error(ex, null, (Object) null);
		} catch (FileNotFoundException ex) {
			logger.error(ex, null, (Object) null);
		}

		this.engine = new ModeShapeEngine();

		if (this.engine == null || repository_config == null) {
			throw new Exception("Missing engine");
		}
		logger.debug("Started ModeShape engine.\n");
		engine.start();
		this.repository = engine.deploy(repository_config);
		logger.debug("Deployed repository {}.", repository.getName());
		Workspace ws = this.repository.login().getWorkspace();
		ws.createWorkspace("fedora");
		logger.debug("Created 'fedora' workspace.\n");
	}

	@GET
	@Path("/describe")
	public Response describe() {
		return Response.status(200).entity(this.repository.getName()).build();
	}

	@POST
	@Path("/objects/{pid}")
	public Response ingest(@PathParam("pid") String pid)
			throws RepositoryException {
		JcrSession session = this.repository.login("fedora");

		Node root = session.getRootNode();
		if (session.hasPermission("/" + pid, "add_node")) {
			Node obj = root.addNode(pid);
			obj.setProperty("ownerId", "Fedo Radmin");
			session.save();
			return Response.status(200).entity(obj.toString()).build();
		} else {
			return Response.status(401).entity("NO!").build();
		}
	}

	@GET
	@Path("/objects/{pid}")
	public Response getObject(@PathParam("pid") String pid)
			throws RepositoryException {
		JcrSession session = this.repository.login("fedora");
		Node root = session.getRootNode();

		if (root.hasNode(pid)) {
			return Response.status(200).entity(pid).build();
		} else {
			return Response.status(404).entity("404").build();
		}

	}

	@GET
	@Path("/objects")
	public Response getObjects() throws RepositoryException {
		JcrSession session = this.repository.login("fedora");
		Node root = session.getRootNode();

		StringBuffer nodes = new StringBuffer();
		NodeIterator i = root.getNodes();
		while (i.hasNext()) {
			Node n = i.nextNode();
			nodes.append("Name: " + n.getName() + ", Path:" + n.getPath()
					+ "\n");
		}
		root.getNodes();
		return Response.status(200).entity(nodes.toString()).build();

	}
}
