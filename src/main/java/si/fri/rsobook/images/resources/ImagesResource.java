package si.fri.rsobook.images.resources;

import com.kumuluz.ee.logs.cdi.Log;
import si.fri.rsobook.images.ImagesBean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@RequestScoped
@Path("images")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Log
public class ImagesResource {

    @Inject
    private ImagesBean imagesBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Path("/test")
    public Response test() {
        String result = imagesBean.getMaxImageSizeDescription();


        return Response.status(Response.Status.OK).entity(result).build();
    }
}
