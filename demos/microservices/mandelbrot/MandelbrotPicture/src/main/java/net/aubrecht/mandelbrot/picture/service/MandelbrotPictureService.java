package net.aubrecht.mandelbrot.picture.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.ejb.Stateless;
import javax.imageio.ImageIO;

/**
 * Draw an image of Mandelbrot fractal.
 *
 * @author aubi
 */
@Stateless
public class MandelbrotPictureService {

    Logger log = Logger.getLogger(MandelbrotPictureService.class.getName());

    public byte[] paintManderlbrotToPNG(double xMin, double xMax, double yMin, double yMax, int dimension, int iterations, int bailout) throws IOException {
        BufferedImage image = this.paintManderlbrot(xMin, xMax, yMin, yMax, dimension, iterations, bailout);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
        } catch (IOException ex) {
            Logger.getLogger(MandelbrotPictureService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IOException("Unable to save image to PNG format: " + ex.getMessage(), ex);
        }
        return os.toByteArray();
    }

    public BufferedImage paintManderlbrot(double xMin, double xMax, double yMin, double yMax, int dimension, int iterations, int bailout) {
        log.severe(() -> "painting mandelbrot " + xMin + " - " + xMax + " x " + yMin + " - " + yMax + " started");
        double dX = xMax - xMin;
        double dY = yMax - yMin;
        BufferedImage image = new BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_RGB);
        double min = Double.MAX_VALUE;
        for (int px = 0; px < dimension; px++) {
            double x = dX * px / dimension + xMin;
            for (int py = 0; py < dimension; py++) {
                double y = dY * py / dimension + yMin;
                double iters = calcMandelPoint(x, y, iterations, bailout);
                min = Math.min(min, iters);
            }
        }
        for (int px = 0; px < dimension; px++) {
            double x = dX * px / dimension + xMin;
            for (int py = 0; py < dimension; py++) {
                double y = dY * py / dimension + yMin;
                double iters = calcMandelPoint(x, y, iterations, bailout) - min;
                int color = Math.min(255, (int) (iters * 2550 / iterations));
//                int color = Math.min(255, (int) (Math.sqrt(iterations * EMPHASIZE / ((double) max - min)) * 255));
                image.setRGB(px, py, (255 - color) * 256 * 256 + (255 - color) * 256 + 255);
            }
        }
        log.severe(() -> "painting mandelbrot " + xMin + " - " + xMax + " x " + yMin + " - " + yMax + " finished");
        return image;
    }

    public double calcMandelPoint(double x, double y, int iterations, int bailout) {
        double oldZX = 0;
        double oldZY = 0;
        for (int iter = 0; iter < iterations; iter++) {
            double newZS = (oldZX * oldZX) - (oldZY * oldZY) + x;
            double newZY = (2 * oldZX * oldZY) + y;
            oldZX = newZS;
            oldZY = newZY;
            double size = Math.pow(oldZX, 2) + Math.pow(oldZY, 2);
            if (size > bailout) {
                return iter + bailout / size;
            }
        }
        return iterations;
    }
}
