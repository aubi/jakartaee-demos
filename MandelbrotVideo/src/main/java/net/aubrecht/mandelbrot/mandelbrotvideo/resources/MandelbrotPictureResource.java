package net.aubrecht.mandelbrot.mandelbrotvideo.resources;

import java.io.ByteArrayInputStream;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import net.aubrecht.mandelbrot.mandelbrotvideo.service.MandelbrotPictureClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Forward the request to paint a piece of Mandelbrot image.
 *
 * @author aubi
 */
@Path("v1/mandelbrot/picture")
public class MandelbrotPictureResource {

    @Inject
    @RestClient
    private MandelbrotPictureClient mandelbrotPictureClient;

    @GET
    @Produces("image/png")
    public Response draw(
            @QueryParam("xMin") double xMin,
            @QueryParam("xMax") double xMax,
            @QueryParam("yMin") double yMin,
            @QueryParam("yMax") double yMax,
            @QueryParam("dimension") @DefaultValue("1000") int dimension,
            @QueryParam("iterations") @DefaultValue("500") int iterations,
            @QueryParam("bailout") @DefaultValue("4") int bailout) {
        return Response.ok(new ByteArrayInputStream(mandelbrotPictureClient.draw(xMin, xMax, yMin, yMax, dimension, iterations, bailout))).build();
    }
}
