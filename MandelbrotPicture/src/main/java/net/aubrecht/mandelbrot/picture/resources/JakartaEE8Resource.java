package net.aubrecht.mandelbrot.picture.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Just a test REST.
 *
 * @author aubi
 */
@Path("ping")
public class JakartaEE8Resource {
    
    @GET
    public Response pingPong() {
        return Response
                .ok("pong")
                .build();
    }
}
