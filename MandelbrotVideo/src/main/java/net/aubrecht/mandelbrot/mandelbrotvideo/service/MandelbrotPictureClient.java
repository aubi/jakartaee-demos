package net.aubrecht.mandelbrot.mandelbrotvideo.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author aubi
 */
@RegisterRestClient(baseUri = "http://localhost:8080/MandelbrotPicture/rest/")
@Path("")
public interface MandelbrotPictureClient {

    @GET
    @Produces("image/png")
    @Path("mandelbrot")
    byte[] draw(
            @QueryParam("xMin") double xMin,
            @QueryParam("xMax") double xMax,
            @QueryParam("yMin") double yMin,
            @QueryParam("yMax") double yMax,
            @QueryParam("dimension") @DefaultValue("1000") int dimension,
            @QueryParam("iterations") @DefaultValue("500") int iterations,
            @QueryParam("bailout") @DefaultValue("4") int bailout);
}
