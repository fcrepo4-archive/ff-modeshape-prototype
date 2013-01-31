package org.fcrepo.modeshape.storage;


import org.infinispan.loaders.AbstractCacheStoreConfig;

public class DurableFileCacheStoreConfig extends AbstractCacheStoreConfig {

    private String location = "Infinispan-FileCacheStore";

    public String getLocation() {
        return location;
    }

    private void setLocation(String location) {
        testImmutability("location");
        this.location = location;
    }

    public DurableFileCacheStoreConfig location(String location) {
        setLocation(location);
        return this;
    }

}
