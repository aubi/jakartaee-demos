/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.aubrecht.mandelbrot.mandelbrotvideo.service;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.Singleton;
import net.aubrecht.mandelbrot.mandelbrotvideo.service.model.ListOfImagesCalculation;

/**
 * Cache for generated videos.
 *
 * @author aubi
 */
@Singleton
public class VideosCache {
    private Map<Long, ListOfImagesCalculation> cache = new HashMap<>();

    public VideosCache() {
    }

    public void add(ListOfImagesCalculation processing) {
        cache.put(processing.getId(), processing);
    }

    public ListOfImagesCalculation get(long id) {
        return cache.get(id);
    }

    public void delete(long id) {
        cache.remove(id);
    }
}
