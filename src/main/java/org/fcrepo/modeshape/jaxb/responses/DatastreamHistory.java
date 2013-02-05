package org.fcrepo.modeshape.jaxb.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "datastreamHistory", namespace = "http://www.fedora.info/definitions/1/0/management/")
public class DatastreamHistory {

	@XmlElement
	List<DatastreamProfile> datastreamProfiles;

	public DatastreamHistory() {
	}

	public DatastreamHistory(List<DatastreamProfile> datastreamProfiles) {
		this.datastreamProfiles = datastreamProfiles;
	}
}
