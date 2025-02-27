package net.aubrecht.mandelbrot.picture.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

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
