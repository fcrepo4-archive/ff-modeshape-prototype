package org.fcrepo.ffmodeshapeprototype;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.modeshape.jcr.ConfigurationException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import freemarker.template.TemplateException;

@Path("/")
public class FedoraIdentifiers extends AbstractResource {

	public FedoraIdentifiers() throws ConfigurationException,
			RepositoryException, IOException {
		super();
	}

	@POST
	@Path("/nextPID")
	@Produces("text/xml")
	public Response getNextPid(
			@QueryParam("numPids") @DefaultValue("1") Integer numPids)
			throws RepositoryException, IOException, TemplateException {

		ImmutableSet.Builder<String> b = new Builder<String>();
		for (int i = 0; i < numPids; i++) {
			b.add(pidMinter.mintPid());
		}
		return Response
				.ok()
				.entity(renderTemplate("nextPid.ftl",
						ImmutableMap.of("pids", (Object) b.build()))).build();
	}
}
