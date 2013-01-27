package org.fcrepo.modeshape;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.modeshape.jcr.ConfigurationException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import freemarker.template.TemplateException;

@Path("/namespaces")
public class FedoraNamespaces extends AbstractResource {

	public FedoraNamespaces() throws ConfigurationException,
			RepositoryException, IOException {
		super();
	}

	@POST
	@Path("/{ns}")
	public Response registerObjectNamespace(@PathParam("ns") final String ns)
			throws RepositoryException {
		final Session session = ws.getSession();
		Workspace w = session.getWorkspace();
		NamespaceRegistry r = w.getNamespaceRegistry();

		r.registerNamespace(ns, "info:fedora/" + ns);

		return Response.ok().entity(ns).build();
	}

	@GET
	@Path("/{ns}")
	@Produces("application/json")
	public Response retrieveObjectNamespace(@PathParam("ns") final String prefix)
			throws RepositoryException {

		final NamespaceRegistry r = ws.getSession().getWorkspace()
				.getNamespaceRegistry();

		if (ImmutableSet.copyOf(r.getPrefixes()).contains(prefix)) {
			return Response
					.ok()
					.entity("{ \"" + prefix + "\":\"" + r.getURI(prefix)
							+ "\" }").build();
		} else
			return four04;
	}

	@POST
	@Path("")
	@Consumes("application/json")
	public Response registerObjectNamespaceJSON(final InputStream message)
			throws RepositoryException, JsonParseException,
			JsonMappingException, IOException {

		final NamespaceRegistry r = ws.getSession().getWorkspace()
				.getNamespaceRegistry();

		@SuppressWarnings("unchecked")
		final Map<String, String> nses = mapper.readValue(message, Map.class);
		for (final Map.Entry<String, String> entry : nses.entrySet()) {
			r.registerNamespace(entry.getKey(), entry.getValue());
		}

		return Response.ok().entity(nses).build();
	}

	@GET
	@Path("")
	@Produces("text/plain")
	public Response getObjectNamespaces() throws RepositoryException {
		final Session session = ws.getSession();
		Workspace w = session.getWorkspace();
		NamespaceRegistry r = w.getNamespaceRegistry();

		StringBuffer out = new StringBuffer();
		String[] uris = r.getURIs();
		String[] prefixes = r.getPrefixes();
		for (int i = 0; i < uris.length; i++) {
			out.append(prefixes[i] + " : " + uris[i] + "\n");
		}

		return Response.ok().entity(out.toString()).build();
	}

	@GET
	@Path("")
	@Produces("text/xml")
	public Response getObjectNamespacesInXML() throws RepositoryException,
			IOException, TemplateException {
		final NamespaceRegistry reg = ws.getNamespaceRegistry();
		final ImmutableMap.Builder<String, Object> b = ImmutableMap.builder();
		for (final String prefix : reg.getPrefixes()) {
			b.put(prefix, reg.getURI(prefix));
		}
		return Response
				.ok()
				.entity(renderTemplate("namespaceRegistry.ftl",
						ImmutableMap.of("namespaces", (Object) b.build())))
				.build();
	}

}
