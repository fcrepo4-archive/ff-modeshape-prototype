package org.fcrepo.ffmodeshapeprototype.identifiers;

public class UUIDPidMinter implements PidMinter {

	@Override
	public String mintPid() {
		return java.util.UUID.randomUUID().toString();
	}

}
