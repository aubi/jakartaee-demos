package fish.payara.sampleedgecontroller.service;

import fish.payara.sampleedgecontroller.model.DataRow;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Service with data storage.
 *
 * @author Petr Aubrecht
 */
@Stateless
public class DataProcessor {
    private static final AtomicLong counterData = new AtomicLong(0);
    private static final AtomicLong counterDataMiningStarted = new AtomicLong(0);
    private static final AtomicLong counterDataMiningFinished = new AtomicLong(0);
    private static final Map<Integer, List<DataRow>> cache = new ConcurrentHashMap<>();
    private static final Logger log = Logger.getLogger(DataProcessor.class.getName());

    public void storeDataToCache(DataRow data) {
        List<DataRow> existingData = cache.get(data.getSourceId());
        if (existingData == null) {
            existingData = new ArrayList<>();
            cache.put(data.getSourceId(), existingData);
        }
        existingData.add(data);
    }

    public void sendDataToMiner(String dataMineURL, List<DataRow> data) {
        // send to data miner
        WebTarget target = ClientBuilder
                .newClient()
                .target(dataMineURL);

        Response analysisResponse = target
                .request()
                //.header("Authorization", "Bearer <token>")
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));

        if (analysisResponse.getStatus() == HttpURLConnection.HTTP_OK) {
            long localCounter = counterDataMiningFinished.incrementAndGet();
            log.info(() -> "<<< Data mining %,d successfully finished, %,d data sets in progress".formatted(localCounter, cache.size()));
        } else {
            log.severe("Error! Code: %d%n".formatted(analysisResponse.getStatus()));
        }
    }

    public void clear() {
        counterData.set(0);
        counterDataMiningStarted.set(0);
        counterDataMiningFinished.set(0);
        cache.clear();
    }

    public long counterDataIncrementAndGet() {
        return counterData.incrementAndGet();
    }

    public List<DataRow> cacheGet(int sourceId) {
        return cache.get(sourceId);
    }

    public void cachePut(int sourceId, List<DataRow> existingData) {
        cache.put(sourceId, existingData);
    }

    public List<DataRow> cacheRemove(Integer sourceId) {
        return cache.remove(sourceId);
    }

    public long counterDataMiningStartedIncrementAndGet() {
        return counterDataMiningStarted.incrementAndGet();
    }

    public int getCacheSize() {
        return cache.size();
    }

    public long getCounterData() {
        return counterData.get();
    }

    public long getCounterDataMiningStarted() {
        return counterDataMiningStarted.get();
    }

    public long getCounterDataMiningFinished() {
        return counterDataMiningFinished.get();
    }

}
