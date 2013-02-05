package org.fcrepo.modeshape.jaxb.responses;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fcrepo.modeshape.jaxb.FedoraObjectStates;

@XmlRootElement(name = "objectProfile", namespace = "http://www.fedora.info/definitions/1/0/access/")
public class ObjectProfile {

	@XmlElement
	public String objLabel;

	@XmlElement
	public String objOwnerId;

	@XmlElement
	public List<String> objModels;

	@XmlElement
	public String objCreateDate;

	@XmlElement
	public String objLastModDate;

	@XmlElement
	public URI objDissIndexViewURL;

	@XmlElement
	public URI objItemIndexViewURL;

	@XmlElement
	public FedoraObjectStates objState;

}
