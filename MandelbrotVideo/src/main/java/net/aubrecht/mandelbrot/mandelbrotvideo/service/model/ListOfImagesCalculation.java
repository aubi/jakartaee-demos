/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.aubrecht.mandelbrot.mandelbrotvideo.service.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Contains list of futures to complete whole video.
 *
 * @author aubi
 */
public class ListOfImagesCalculation {

    private static AtomicLong idGenerator = new AtomicLong(0);
    private long id;
    private List<Future<byte[]>> imageFutures = Collections.synchronizedList(new ArrayList<>());
    private Future<File> finalProcessing;


    public ListOfImagesCalculation() {
        id = idGenerator.getAndIncrement();
    }

    public void add(Future<byte[]> pictureFuture) {
        imageFutures.add(pictureFuture);
    }

    public List<Future<byte[]>> getImageFutures() {
        return imageFutures;
    }

    public void setFinalProcessing(Future<File> finalProcessing) {
        this.finalProcessing = finalProcessing;
    }

    public Future<File> getFinalProcessing() {
        return finalProcessing;
    }

    public long getId() {
        return id;
    }

}
