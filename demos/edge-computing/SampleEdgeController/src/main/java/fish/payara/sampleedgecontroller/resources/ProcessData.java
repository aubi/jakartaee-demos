package fish.payara.sampleedgecontroller.resources;

import fish.payara.sampleedgecontroller.model.DataRow;
import fish.payara.sampleedgecontroller.service.DataProcessor;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Timed;

/**
 * REST endpoint for data processing.
 *
 * @author Petr Aubrecht
 */
@Path("data")
@RequestScoped
public class ProcessData {

    private static final Logger log = Logger.getLogger(ProcessData.class.getName());

    @EJB
    private DataProcessor dataProcessor;

    @Inject
    @ConfigProperty(name = "dataMineURL")
    private String dataMineURL;

    @Resource
    private ManagedExecutorService mes;

    @GET
    @Path("ping")
    @Timed
    public Response ping() {
        return Response
                .ok("pong, amount of data: %,d, data minings: %,d of %,d, size of cache: %,d".formatted(
                        dataProcessor.getCounterData(), dataProcessor.getCounterDataMiningFinished(),
                        dataProcessor.getCounterDataMiningStarted(), dataProcessor.getCacheSize()))
                .build();
    }

    // FIXME: this should be @DELETE, the @GET is only for demo purpose!!!
    @GET
    @Path("clear")
    public Response clear() {
        dataProcessor.clear();

        return Response
                .ok("All data deleted")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Response processData(DataRow data) {
        long localCounter = dataProcessor.counterDataIncrementAndGet();
        if (localCounter % 10_000 == 0) {
            log.info(() -> "Processing data #%,d, source id %,d".formatted(localCounter, data.getSourceId()));
        }
        dataProcessor.storeDataToCache(data);
        return Response
                .ok("Data cached for " + data.getSourceId())
                .build();
    }

    @POST
    @Path("process")
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response processDataMining(Integer sourceId) {
        Response response;
        List<DataRow> data = dataProcessor.cacheRemove(sourceId);
        if (data == null) {
            response = Response
                    .status(Response.Status.NOT_FOUND.getStatusCode(), "Data not found for " + sourceId)
                    .build();
        } else {
            mes.submit(() -> dataProcessor.sendDataToMiner(dataMineURL, data));

            long started = dataProcessor.counterDataMiningStartedIncrementAndGet();
            String msg = ">>> Data mining #%,d for sourceId #%d submitted".formatted(started, sourceId);
            log.info(msg);
            response = Response.ok(msg).build(); // TODO improve
        }
        return response;
    }

    @Gauge(name = "CacheSize", unit = MetricUnits.NONE)
    public int getCacheSize() {
        return dataProcessor.getCacheSize();
    }

    @Gauge(name = "DataReceived", unit = MetricUnits.NONE)
    public long getDataReceived() {
        return dataProcessor.getCounterData();
    }

    @Gauge(name = "DataMiningStarted", unit = MetricUnits.NONE)
    public long getDataMiningStarted() {
        return dataProcessor.getCounterDataMiningStarted();
    }

    @Gauge(name = "DataMiningFinished", unit = MetricUnits.NONE)
    public long getDataMiningFinished() {
        return dataProcessor.getCounterDataMiningFinished();
    }

}
