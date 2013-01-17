<?xml version="1.0" encoding="UTF-8"?>
<objectProfile xmlns="http://www.fedora.info/definitions/1/0/access/">
    <objLabel>${obj.getName()}</objLabel>
    <objOwnerId>${obj.getProperty("fedora:ownerId").getString()}</objOwnerId>
    <objModels>
    </objModels>
    <#-- <objCreateDate>${obj.getProperty("jcr:created").getString()}</objCreateDate> -->
    <#-- <objLastModDate>${obj.getProperty("jcr:lastModified").getString()}</objLastModDate> -->
    <objDissIndexViewURL>http://example.com</objDissIndexViewURL>
    <objItemIndexViewURL>${obj.getPath()}</objItemIndexViewURL>
    <objState>A</objState>
</objectProfile>
