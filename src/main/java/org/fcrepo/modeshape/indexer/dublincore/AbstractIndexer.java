package org.fcrepo.modeshape.indexer.dublincore;


import javax.jcr.Node;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public abstract class AbstractIndexer {

    public AbstractIndexer() {
    }

    public abstract InputStream getStream(Node node);

}
