<?xml version="1.0" encoding="UTF-8"?>
<datastreamProfile xmlns="http://www.fedora.info/definitions/1/0/management/"
  pid="${obj.getName()}" dsID="${ds.getName()}">
    <dsLabel>${ds.getName()}</dsLabel>
    <dsVersionID></dsVersionID>
    <dsCreateDate>${ds.getProperty("jcr:created").getString()}</dsCreateDate>
    <dsState>A</dsState>
    <dsMIME></dsMIME>
    <dsFormatURI></dsFormatURI>
    <dsControlGroup></dsControlGroup>
    <dsSize></dsSize>
    <dsVersionable></dsVersionable>
    <dsInfoType></dsInfoType>
    <dsLocation>${ds.getPath()}</dsLocation>
    <dsLocationType></dsLocationType>
    <dsChecksumType></dsChecksumType>
    <dsChecksum></dsChecksum>
</datastreamProfile>
