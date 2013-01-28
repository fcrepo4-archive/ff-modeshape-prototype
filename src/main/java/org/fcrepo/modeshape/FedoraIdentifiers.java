package org.fcrepo.modeshape;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import freemarker.template.TemplateException;

@Path("/")
public class FedoraIdentifiers extends AbstractResource {

	@POST
	@Path("/nextPID")
	@Produces("text/xml")
	public Response getNextPid(
			@QueryParam("numPids") @DefaultValue("1") Integer numPids)
			throws RepositoryException, IOException, TemplateException {

		ImmutableSet.Builder<String> b = ImmutableSet.builder();
		for (int i = 0; i < numPids; i++) {
			b.add(pidMinter.mintPid());
		}
		return Response
				.ok()
				.entity(renderTemplate("nextPid.ftl",
						ImmutableMap.of("pids", (Object) b.build()))).build();
	}
}
