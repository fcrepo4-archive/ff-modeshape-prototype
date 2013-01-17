package org.fcrepo.ffmodeshapeprototype;

import java.io.IOException;
import java.util.Map;

import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ConfigurationException;
import org.modeshape.jcr.api.nodetype.NodeTypeManager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import freemarker.template.TemplateException;

/**
 * 
 * @author cabeer
 * @author ajs6f
 */

@Path("/")
public class FedoraRepository extends AbstractResource {

	private final Logger logger = Logger.getLogger(FedoraRepository.class);

	public FedoraRepository() throws ConfigurationException,
			RepositoryException, IOException {
		super();
	}

        
	@GET
	@Path("/describe/modeshape")
	public Response describeModeshape() throws JsonGenerationException,
			JsonMappingException, IOException, RepositoryException {

		// start with repo configuration properties
		final Repository repo = ws.getSession().getRepository();
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
		
		return Response
				.ok()
				.entity(mapper.writerWithType(Map.class).writeValueAsString(
						repoproperties.build())).build();
        }
        
	@GET
	@Path("/describe")
	public Response describe() throws RepositoryException,
    IOException, TemplateException {
        ImmutableMap.Builder<String, Object> b = ImmutableMap.builder();
		return Response.ok().entity(renderTemplate("describeRepository.ftl",ImmutableMap.of("asdf", (Object)"asdf"))).build();
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
