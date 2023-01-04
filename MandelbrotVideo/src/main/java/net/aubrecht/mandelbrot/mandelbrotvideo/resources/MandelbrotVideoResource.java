package net.aubrecht.mandelbrot.mandelbrotvideo.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.aubrecht.mandelbrot.mandelbrotvideo.service.MandelbrotVideoService;
import net.aubrecht.mandelbrot.mandelbrotvideo.service.VideosCache;
import net.aubrecht.mandelbrot.mandelbrotvideo.service.model.ListOfImagesCalculation;

/**
 * Generate video of Mandelbrot image.
 *
 * @author aubi
 */
@Path("mandelbrot")
public class MandelbrotVideoResource {

    @EJB
    private MandelbrotVideoService mandelbrotVideoService;

    @EJB
    private VideosCache videosCache;

    @Resource
    private ManagedExecutorService mes;
    
    @GET
    @Produces("video/mp4")
    public Response generateVideo(
            @QueryParam("xMin") double xMin,
            @QueryParam("xMax") double xMax,
            @QueryParam("yMin") double yMin,
            @QueryParam("yMax") double yMax,
            @QueryParam("dimension") @DefaultValue("1000") int dimension,
            @QueryParam("iterations") @DefaultValue("500") int iterations,
            @QueryParam("bailout") @DefaultValue("4") int bailout) throws IOException, InterruptedException, ExecutionException {
        File image = mandelbrotVideoService.generateMandelbrotVideo(xMin, xMax, yMin, yMax, dimension, iterations, bailout).getFinalProcessing().get();
        return Response.ok(new FileInputStream(image)).build();
    }

    @Path("asynch")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void generateVideoAsync(
            @QueryParam("xMin") double xMin,
            @QueryParam("xMax") double xMax,
            @QueryParam("yMin") double yMin,
            @QueryParam("yMax") double yMax,
            @QueryParam("dimension") @DefaultValue("1000") int dimension,
            @QueryParam("iterations") @DefaultValue("500") int iterations,
            @QueryParam("bailout") @DefaultValue("4") int bailout,
            @Suspended AsyncResponse ar) throws IOException, InterruptedException, ExecutionException {
        // attempt to start asynchronous execution, return the http thread to the pool
        mes.submit(() -> {
            ListOfImagesCalculation processing = mandelbrotVideoService.generateMandelbrotVideo(xMin, xMax, yMin, yMax, dimension, iterations, bailout);
            videosCache.add(processing);
            ar.resume(Response.ok(generateProcessingJson(processing)).build());
        });
    }

    @Path("/cache/{id}")
    @DELETE
    public Response deleteCache(@PathParam("id") long id) {
        ListOfImagesCalculation calculation = videosCache.get(id);
        if (calculation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        videosCache.delete(id);
        return Response.noContent().build();
    }

    @Path("/cache/{id}/state")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getStatus(@PathParam("id") long id) {
        ListOfImagesCalculation calculation = videosCache.get(id);
        if (calculation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(generateProcessingJson(calculation)).build();
    }

    @Path("/cache/{id}/video")
    @GET
    @Produces("video/mp4")
    public Response getVideo(@PathParam("id") long id) throws InterruptedException, ExecutionException {
        ListOfImagesCalculation calculation = videosCache.get(id);
        if (calculation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!calculation.getFinalProcessing().isDone()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        try {
            return Response.ok(new FileInputStream(calculation.getFinalProcessing().get())).build();
        } catch (FileNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private JsonObject generateProcessingJson(ListOfImagesCalculation processing) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("id", processing.getId());
        json.add("finalFinished", processing.getFinalProcessing().isDone());
        json.add("renderingInfo", "Processed image #" + processing.getRenderedImageProgress() + " into video");
        json.add("renderedImageProgress", processing.getRenderedImageProgress());
        JsonArrayBuilder imagesList = Json.createArrayBuilder();
        int total = 0;
        int done = 0;
        for (int i = 0; i < processing.getImageFutures().size(); i++) {
            Future<byte[]> imageFuture = processing.getImageFutures().get(i);
            // for (Future<byte[]> imageFuture : processing.getImageFutures()) {
            boolean isDone = imageFuture.isDone();
            imagesList.add(isDone ? (i <= processing.getRenderedImageProgress() ? "invideo" : "rendered") : "inprocess");
            total++;
            done += isDone ? 1 : 0;
        }
        json.add("status", done + " / " + total);
        json.add("images", imagesList);
        return json.build();
    }
}
