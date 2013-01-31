<?xml version="1.0" encoding="UTF-8"?>
<datastreamProfile xmlns="http://www.fedora.info/definitions/1/0/management/"
 pid="${obj.getName()}" dsID="${ds.getName()}">
    <dsLabel>${ds.getName()}</dsLabel>
    <dsVersionID></dsVersionID>
    <dsCreateDate></dsCreateDate>
    <dsState>A</dsState>
    <dsMIME>${ds.getProperty("fedora:contentType").getString()}</dsMIME>
    <dsFormatURI>http://www.oxygenxml.com/</dsFormatURI>
    <dsControlGroup>M</dsControlGroup>
    <dsSize>0</dsSize>
    <dsVersionable>true</dsVersionable>
    <dsInfoType></dsInfoType>
    <dsLocation>${ds.getPath()}</dsLocation>
    <dsLocationType></dsLocationType>
    <dsChecksumType></dsChecksumType>
    <dsChecksum></dsChecksum>
</datastreamProfile>