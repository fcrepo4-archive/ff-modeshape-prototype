<?xml version="1.0" encoding="UTF-8"?>
<objectProfile xmlns="http://www.fedora.info/definitions/1/0/access/">
    <objLabel>${node.getName()}</objLabel>
    <objOwnerId>${node.getProperty("ownerId").getString()}</objOwnerId>
    <objModels>
    </objModels>
    <#-- <objCreateDate>${node.getProperty("jcr:created").getString()}</objCreateDate> -->
    <#-- <objLastModDate>${node.getProperty("jcr:lastModified").getString()}</objLastModDate> -->
    <objDissIndexViewURL>http://example.com</objDissIndexViewURL>
    <objItemIndexViewURL>${node.getPath()}</objItemIndexViewURL>
    <objState>A</objState>
</objectProfile>
