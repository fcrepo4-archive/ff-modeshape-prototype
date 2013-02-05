package org.fcrepo.modeshape.jaxb.responses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fedoraRepository", namespace = "http://www.fedora.info/definitions/1/0/access/")
public class DescribeRepository {
	@XmlElement
	protected String repositoryVersion = "4.0-modeshape-candidate";

}
