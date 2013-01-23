The amazing rebirth of Fedora Commons in the JCR world.

[![Build
Status](https://travis-ci.org/futures/ff-modeshape-prototype.png?branch=master)](undefined)

```bash
$ mvn clean jetty:run
$ curl "http://localhost:8080/rest/fedora/describe"
```
to create an object, try:

```
$ curl "http://localhost:8080/rest/objects/myobject" -X POST
```
and to retrieve it:

```
$ curl "http://localhost:8080/rest/objects/myobject" -H "Accept: text/xml"
```

To import FOXML, try:
```
$ curl "http://localhost:8080/rest/foxml/myobject" --data-ascii @myfoxmlfile.xml -H "Content-type: text/xml"
```
and you should see an object appear at:
```
$ curl "http://localhost:8080/rest/objects/myobject"
```

Before creating fedora-like namespaced nodes, you need to register a namespace:

```bash
curl "http://localhost:8080/rest/namespaces/asdf" -X POST
```
