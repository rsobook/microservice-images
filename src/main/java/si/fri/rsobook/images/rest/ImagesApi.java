package si.fri.rsobook.images.rest;


import com.kumuluz.ee.discovery.annotations.RegisterService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@RegisterService
@ApplicationPath("/api/v1/")
public class ImagesApi extends Application {

}