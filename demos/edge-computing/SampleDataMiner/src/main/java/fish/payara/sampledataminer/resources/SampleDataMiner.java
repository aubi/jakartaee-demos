package fish.payara.sampledataminer.resources;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Duration;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample, no-op data miner, simulates work on a remote computer, thus sleeping
 * it close-enough approximation.
 *
 * @author Petr Aubrecht
 */
@Path("datamine")
@RequestScoped
public class SampleDataMiner {
    
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyze(/*List<DataRow> data*/) {
        try {
            // simulate data analysis
            //Thread.sleep(Duration.ofMillis(new Random().nextInt(100)));
            Thread.sleep(Duration.ofSeconds(new Random().nextInt(10)));
        } catch (InterruptedException ex) {
            Logger.getLogger(SampleDataMiner.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response
                .ok("""
                    {
                        "result": "resultdata analyzed"
                    }
                    """)
                .build();
    }
}
