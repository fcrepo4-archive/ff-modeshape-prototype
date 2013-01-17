<?xml version="1.0" encoding="UTF-8"?>
<namespaceRegistry xmlns="http://www.fedora.info/definitions/1/0/management/">
	 <#list namespaces?keys as prefix>
	<namespace prefix="${prefix}" URI="${namespaces[prefix]}"/>
	</#list>
</namespaceRegistry>
