package org.fcrepo.modeshape;

import javax.ws.rs.core.Response;

public abstract class Constants {
	
	protected static final Response four01 = Response.status(404).entity("401")
			.build();
	protected static final Response four04 = Response.status(404).entity("404")
			.build();
}
