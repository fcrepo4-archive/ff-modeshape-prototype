package org.fcrepo.ffmodeshapeprototype.foxml;

import java.io.IOException;

import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.modeshape.jcr.api.nodetype.NodeTypeManager;
import org.modeshape.jcr.api.sequencer.Sequencer;

public class FOXMLSequencer extends Sequencer {

	private FOXMLParser parser;

	public String description;

	@Override
	public boolean execute(Property inputProperty, Node outputNode,
			Context context) throws Exception {

		getLogger().debug(
				"Now sequencing FOXML from: " + inputProperty.getPath());
		try {
			parser.parse(inputProperty.getBinary().getStream(), outputNode);
			return true;
		} catch (Exception e) {
			getLogger().error(e, "Failed to sequence {}",
					inputProperty.getPath());
			return false;
		}

	}

	@Override
	public void initialize(NamespaceRegistry registry,
			NodeTypeManager nodeTypeManager) throws RepositoryException,
			IOException {
		// registerDefaultMimeTypes("text/xml");
		getLogger().debug(
				"Initializing " + getClass().getCanonicalName() + "["
						+ getName() + "]");
		parser = new FOXMLParser();
	}

}
