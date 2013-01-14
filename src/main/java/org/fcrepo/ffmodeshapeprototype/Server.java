/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fcrepo.ffmodeshapeprototype;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.modeshape.common.collection.Problems;
import org.infinispan.schematic.document.ParsingException;
import org.modeshape.jcr.ConfigurationException;
import org.modeshape.jcr.JcrRepository;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.RepositoryConfiguration;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author cabeer
 */
 
 
@Path("/")
public class Server {
    private ModeShapeEngine engine;
    private JcrRepository repository;
   
    public Server() throws Exception, ConfigurationException, RepositoryException {
                RepositoryConfiguration repository_config = null;
        try {
            repository_config = RepositoryConfiguration.read("{ \"name\": \"My Prototype Repository\" }");
            Problems problems = repository_config.validate();
            
            if (problems.hasErrors()) {
                System.err.println("Problems starting the engine.");
                System.err.println(problems);
            throw new Exception("Problems starting the engine.");
            }
            
        } catch (ParsingException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        this.engine = new ModeShapeEngine();
        
        if(this.engine == null || repository_config == null) {
            throw new Exception("Missing engine");
        }
        
        engine.start();
        this.repository = engine.deploy(repository_config);
    }
    
    @GET
    @Path("/describe")
    public Response describe() {
        return Response.status(200).entity(this.repository.getName()).build();
    } 
    
    
}
