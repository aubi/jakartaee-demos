package net.aubrecht.mandelbrot.picture.resources;

import java.io.ByteArrayInputStream;
import javax.ejb.EJB;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import net.aubrecht.mandelbrot.picture.service.MandelbrotPictureService;

/**
 * Paint a piece of Mandelbrot image.
 * @author aubi
 */
@Path("mandelbrot")
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
        byte[] image = mandelbrotService.paintManderlbrotToPNG(xMin, xMax, yMin, yMax, dimension, iterations, bailout);
        return Response.ok(new ByteArrayInputStream(image)).build();
    }
}
