package pl.lodz.p.it.ssbd2020;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("healthz")
public class HealthEndpoint {

    public static boolean health = true;

    @GET
    public Response isHealth() {
        if(health) {
            return Response.ok().build();
        } else {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("change")
    public Response changeHealth() {
        health = !health;
        return Response.ok().build();
    }
}