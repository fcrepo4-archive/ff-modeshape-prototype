package org.fcrepo.modeshape.storage;

import org.infinispan.Cache;
import org.infinispan.container.entries.InternalCacheEntry;
import org.infinispan.loaders.*;
import org.infinispan.marshall.StreamingMarshaller;

import java.io.File;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URI;
import java.util.Set;


@CacheLoaderMetadata(configurationClass = DurableFileCacheStoreConfig.class)
public class DurableFileCacheStore extends AbstractCacheStore {

    DurableFileCacheStoreConfig config;
    File root;
    /**
     * @return root directory where all files for this {@link org.infinispan.loaders.CacheStore CacheStore} are written.
     */
    public File getRoot() {
        return root;
    }

    @Override
    public void init(CacheLoaderConfig config, Cache<?, ?> cache, StreamingMarshaller m) throws CacheLoaderException {
        super.init(config, cache, m);
        this.config = (DurableFileCacheStoreConfig) config;
    }

    @Override
    public Class<? extends CacheLoaderConfig> getConfigurationClass() {
        return DurableFileCacheStoreConfig.class;
    }

    @Override
    // Loads an entry mapped to by a given key. Should return null if the entry does not exist. Expired entries are not returned.
    public InternalCacheEntry load(Object key) throws CacheLoaderException {
        return null;
    }

    @Override
    // Loads all entries in the loader. Expired entries are not returned.
    public Set<InternalCacheEntry> loadAll() throws CacheLoaderException {
        return null;
    }

    @Override
    // Loads up to a specific number of entries. There is no guarantee as to order of entries loaded. The set returned would contain up to a maximum of numEntries entries, and no more.
    public Set<InternalCacheEntry> load(int numEntries) throws CacheLoaderException {
        return null;
    }

    @Override
    // Loads a set of all keys, excluding a filter set.
    public Set<Object> loadAllKeys(Set<Object> objects) throws CacheLoaderException {
        return null;
    }

    @Override
    protected void purgeInternal() throws CacheLoaderException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    // Stores an entry
    public void store(InternalCacheEntry internalCacheEntry) throws CacheLoaderException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    // Writes contents of the stream to the store. Implementations should expect that the stream contains data in an implementation-specific format, typically generated using toStream(java.io.ObjectOutput). While not a requirement, it is recommended that implementations make use of the StreamingMarshaller when dealing with the stream to make use of efficient marshalling.
    public void fromStream(ObjectInput objectInput) throws CacheLoaderException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    // Loads the entire state into a stream, using whichever format is most efficient for the cache loader implementation. Typically read and parsed by fromStream(java.io.ObjectInput).
    public void toStream(ObjectOutput objectOutput) throws CacheLoaderException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    // Clears all entries in the store
    public void clear() throws CacheLoaderException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    // Removes an entry in the store.
    public boolean remove(Object key) throws CacheLoaderException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
