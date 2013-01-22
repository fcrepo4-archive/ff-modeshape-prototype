<?xml version="1.0" encoding="UTF-8"?>
<objectProfile xmlns="http://www.fedora.info/definitions/1/0/access/">
    <objLabel>${obj.getName()}</objLabel>
 	<objOwnerId>${properties.get("fedora:ownerId")}</objOwnerId>
    <objModels>
    </objModels>
    <objCreateDate>${properties.get("jcr:created")}</objCreateDate>
    <objLastModDate>${properties.get("jcr:lastModified")}</objLastModDate>
    <objDissIndexViewURL>http://example.com</objDissIndexViewURL>
    <objItemIndexViewURL>${obj.getPath()}</objItemIndexViewURL>
    <objState>A</objState>
</objectProfile>
