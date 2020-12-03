package pl.lodz.p.it.ssbd2020;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("healthz")
public class HealthEndpoint {

    public static boolean health = true;

    @GET
    public Response isHealth() {
        Logger.getGlobal().log(Level.INFO, "health probe {health: " + health +"}");
        if(health) {
            return Response.ok().build();
        } else {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("change")
    public Response changeHealth() {
        Logger.getGlobal().log(Level.INFO, "health change [health = " + health +"]");
        health = !health;
        return Response.ok().build();
    }
}