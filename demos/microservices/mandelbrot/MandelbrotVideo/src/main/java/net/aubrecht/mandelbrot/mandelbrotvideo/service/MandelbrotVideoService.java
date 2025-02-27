package net.aubrecht.mandelbrot.mandelbrotvideo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import net.aubrecht.mandelbrot.mandelbrotvideo.service.model.ListOfImagesCalculation;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Rational;

/**
 * Service for asynchronous generation of mandelbrot video.
 *
 * @author Petr Aubrecht
 */
@Stateless
public class MandelbrotVideoService {
    static final Logger log = Logger.getLogger(MandelbrotVideoService.class.getName());

    private static final double SCALING_BETWEEN_FRAMES = 1.05;

    @Inject
    @ConfigProperty(name = "processingThreadsCount", defaultValue = "5")
    private int processingThreadsCount;

    @Inject
    @RestClient
    private MandelbrotPictureClient mandelbrotPictureClient;

    @Resource
    ManagedExecutorService mes;

    @Resource
    ManagedThreadFactory mtf;

    public ListOfImagesCalculation generateMandelbrotVideo(double minX, double maxX, double minY, double maxY, int dimension, int iterations, int bailout) {
        double centerX = (maxX + minX) / 2;
        double dX = centerX - minX;
        double centerY = (maxY + minY) / 2;
        double dY = centerY - minY;
        ExecutorService threadPool = Executors.newFixedThreadPool(processingThreadsCount, mtf);
        ListOfImagesCalculation list = new ListOfImagesCalculation();
        int count = (int) (Math.log(4 / dX) / Math.log(SCALING_BETWEEN_FRAMES)) + 1; // scale roughly to -2..+2, e.g. delta=4
        for (int i = count; i >= 0; i--) {
            final int imageNum = i;
            log.log(Level.SEVERE, () -> "Generating image #" + imageNum + " task");
            double scale = Math.pow(SCALING_BETWEEN_FRAMES, i);
            // blind running of all rendering at once leads to OoM!
            // Future<byte[]> pictureFuture = mes.submit(() -> mandelbrotPictureClient.draw(centerX - scale * dX, centerX + scale * dX, centerY - scale * dY, centerY + scale * dY, dimension, iterations, bailout));
            Future<byte[]> pictureFuture = threadPool.submit(() -> mandelbrotPictureClient.draw(centerX - scale * dX, centerX + scale * dX, centerY - scale * dY, centerY + scale * dY, dimension, iterations, bailout));
            list.add(pictureFuture);
        }
        list.setFinalProcessing(mes.submit(() -> {
            try {
                FileChannelWrapper out = null;
                File filename = null;
                try {
                    File tempFile = File.createTempFile("mandelbrot", "");
                    filename = tempFile.getAbsoluteFile();
                    out = NIOUtils.writableChannel(tempFile);
                    // for Android use: AndroidSequenceEncoder
                    AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(25, 1));
                    int imageNum = 0;
                    for (int i = 0; i < list.getImageFutures().size(); i++) {
                        Future<byte[]> picture = list.getImageFutures().get(i);
                        int finalImageNum = imageNum;
                        list.setRenderedImageProgress(finalImageNum);
                        log.log(Level.SEVERE, () -> "Requesting image #" + finalImageNum);
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(picture.get()));
                        // Encode the image
                        log.log(Level.SEVERE, () -> "Encoding image #" + finalImageNum);
                        encoder.encodeImage(image);
                        log.log(Level.SEVERE, () -> "Finished image #" + finalImageNum);
                        imageNum++;
                        list.getImageFutures().set(i, CompletableFuture.completedFuture(null)); // remove cached picture with empty and completed future
                    }
                    // Finalize the encoding, i.e. clear the buffers, write the header, etc.
                    encoder.finish();
                } finally {
                    NIOUtils.closeQuietly(out);
                }
                System.out.println("Done");
                return filename;
            } catch (IOException e) {
                log.log(Level.SEVERE, "Unable to process video: " + e.getMessage(), e);
                return null;
            }
        }));
        return list;
    }
}
