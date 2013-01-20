package org.fcrepo.ffmodeshapeprototype.foxml

import javax.jcr._
import java.io.InputStream
import xml.XML

class FOXMLParser {

  def parse(input: InputStream, objNode: Node) {

    val foxmlObj = XML.load(input) \ "digitalObject"

    objNode.setPrimaryType("fedora:object")

    val objProperties = foxmlObj \ "objectProperties" \ "property"
    val ownerProperty = objProperties filter (p => ((p.\("@NAME")) text) == "info:fedora/fedora-system:def/model#ownerId")
    val ownerId = (ownerProperty \ "@VALUE") text

    objNode.setProperty("fedora:ownerId", ownerId)

    for (datastream <- foxmlObj \\ "datastream") {
      var dsNode = objNode.addNode(datastream \ "@ID" text, "fedora:datastream")
      dsNode.setProperty("fedora:contentType",
        (datastream \ "datastreamVersion").head \ "@MIMETYPE" text)
    }

  }

}