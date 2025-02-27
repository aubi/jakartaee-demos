package net.aubrecht.mandelbrot.picture.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import net.aubrecht.mandelbrot.picture.service.MandelbrotPictureService;

/**
 * Paint a piece of Mandelbrot image.
 * @author aubi
 */
@Path("v1/mandelbrot/picture")
public class MandelbrotPictureResource {

    @EJB
    private MandelbrotPictureService mandelbrotService;
    
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
        try {
            byte[] image = mandelbrotService.paintManderlbrotToPNG(xMin, xMax, yMin, yMax, dimension, iterations, bailout);
            return Response.ok(new ByteArrayInputStream(image)).build();
        } catch (IOException ex) {
            return Response.status(500, "Internal error: " + ex.getMessage()).build();
        }
    }
}
