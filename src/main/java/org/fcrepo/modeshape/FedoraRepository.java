package org.fcrepo.modeshape;

import static com.google.common.collect.ImmutableSet.copyOf;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static javax.ws.rs.core.Response.ok;
import static org.fcrepo.modeshape.FedoraObjects.getObjectSize;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.jcr.LoginException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.fcrepo.modeshape.jaxb.responses.DescribeRepository;
import javax.jcr.Session;
import org.modeshape.jcr.api.nodetype.NodeTypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author cabeer
 * @author ajs6f
 */

@Path("")
public class FedoraRepository extends AbstractResource {

	private static final Logger logger = LoggerFactory
			.getLogger(FedoraRepository.class);

	@GET
	@Path("/describe/modeshape")
	public Response describeModeshape() throws JsonGenerationException,
			JsonMappingException, IOException, RepositoryException {

		logger.debug("Repository name: "
				+ repo.getDescriptor(Repository.REP_NAME_DESC));
		final Builder<String, Object> repoproperties = ImmutableMap.builder();
		for (final String key : repo.getDescriptorKeys()) {
			if (repo.getDescriptor(key) != null)
				repoproperties.put(key, repo.getDescriptor(key));
		}

		// add in node namespaces
		final NamespaceRegistry reg = ws.getNamespaceRegistry();
		final Builder<String, String> namespaces = ImmutableMap.builder();
		for (final String prefix : reg.getPrefixes()) {
			namespaces.put(prefix, reg.getURI(prefix));
		}
		repoproperties.put("node.namespaces", namespaces.build());

		// add in node types
		final NodeTypeManager ntmanager = (NodeTypeManager) ws
				.getNodeTypeManager();
		final Builder<String, String> nodetypes = ImmutableMap.builder();
		NodeTypeIterator i = ntmanager.getAllNodeTypes();
		while (i.hasNext()) {
			NodeType nt = i.nextNodeType();
			nodetypes.put(nt.getName(), nt.toString());
		}
		repoproperties.put("node.types", nodetypes.build());

		return ok(
				mapper.writerWithType(Map.class).writeValueAsString(
						repoproperties.build())).build();
	}

	@GET
	@Path("/describe")
	@Produces({ TEXT_XML, APPLICATION_JSON })
	public DescribeRepository describe() throws LoginException,
			RepositoryException {

		Long totalObjectSize = 0L;
		Session session = repo.login();
		@SuppressWarnings("unchecked")
		final Set<Node> objects = copyOf(session.getNode("/objects").getNodes());
		for (Node object : objects) {
			totalObjectSize = totalObjectSize + getObjectSize(object);
		}
		session.logout();
		DescribeRepository description = new DescribeRepository();
		description.repositorySize = totalObjectSize;
		return description;
	}

}
