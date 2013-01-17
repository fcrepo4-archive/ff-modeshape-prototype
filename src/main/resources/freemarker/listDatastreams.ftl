<?xml version="1.0" encoding="UTF-8"?>
<access:objectDatastreams xmlns:apim="http://www.fedora.info/definitions/1/0/management/"
 xmlns:access="http://www.fedora.info/definitions/1/0/access/">
 
 <#list datastreams as ds>
    <access:datastream dsid="${ds.getName()}" label="" mimeType=""/>
</#list>
 
</access:objectDatastreams>