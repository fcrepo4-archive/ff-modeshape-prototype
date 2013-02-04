package org.fcrepo.modeshape;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.DiscreteDomains.integers;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Ranges.closed;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import freemarker.template.TemplateException;

/**
 * JAX-RS Resource offering PID creation.
 * 
 * @author ajs6f
 *
 */
@Path("nextPID")
public class FedoraIdentifiers extends AbstractResource {

	/**
	 * @param numPids
	 * @return HTTP 200 with XML-formatted block of PIDs
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws TemplateException
	 */
	@POST
	@Produces("text/xml")
	public Response getNextPid(
			@QueryParam("numPids") @DefaultValue("1") Integer numPids)
			throws RepositoryException, IOException, TemplateException {

		ImmutableSet<String> pids = copyOf(transform(
				closed(1, numPids).asSet(integers()), makePid));

		return Response
				.ok()
				.entity(renderTemplate("nextPid.ftl",
						ImmutableMap.of("pids", (Object) pids))).build();
	}

	private Function<Integer, String> makePid = new Function<Integer, String>() {
		@Override
		public String apply(Integer slot) {
			return pidMinter.mintPid();
		}
	};
}
