package fish.payara.sampledataminer.resources;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("ping")
@RequestScoped
public class JakartaEE10Resource {
    
    @GET
    public Response ping(){
        return Response
                .ok("pong")
                .build();
    }
}
