package org.fcrepo.ffmodeshapeprototype;

import java.io.IOException;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ConfigurationException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author cabeer
 * @author ajs6f
 */

@Path("/")
public class FedoraRepository extends AbstractResource {

	private final Logger logger = Logger.getLogger(FedoraRepository.class);

	public FedoraRepository() throws ConfigurationException,
			RepositoryException {
		super();
	}

	@GET
	@Path("/describe")
	public Response describe() throws JsonGenerationException,
			JsonMappingException, IOException {
		Repository repo = ws.getSession().getRepository();
		logger.debug("Repository name: "
				+ repo.getDescriptor(Repository.REP_NAME_DESC));
		Builder<String, String> b = ImmutableMap.builder();
		for (String key : repo.getDescriptorKeys()) {
			if (repo.getDescriptor(key) != null)
				b.put(key, repo.getDescriptor(key));
		}
		return Response
				.ok()
				.entity(mapper.writerWithType(Map.class).writeValueAsString(
						b.build())).build();
	}

	@GET
	@Path("/objects")
	public Response getObjects() throws RepositoryException {
		Session session = ws.getSession();
		Node root = session.getRootNode();
		StringBuffer nodes = new StringBuffer();

		for (NodeIterator i = root.getNodes(); i.hasNext();) {
			Node n = i.nextNode();
			nodes.append("Name: " + n.getName() + ", Path:" + n.getPath()
					+ "\n");
		}

		return Response.ok().entity(nodes.toString()).build();

	}

}
