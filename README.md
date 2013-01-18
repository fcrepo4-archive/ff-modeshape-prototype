The amazing rebirth of Fedora Commons in the JCR world.

[![Build
Status](https://travis-ci.org/futures/ff-modeshape-prototype.png?branch=master)](undefined)

```bash
$ mvn clean jetty:run
$ curl "http://localhost:8080/ff/describe"
```

Before creating fedora-like namespaced nodes, you need to register a namespace:

```bash
curl "http://localhost:8080/ff/namespaces/asdf" -X POST
```
