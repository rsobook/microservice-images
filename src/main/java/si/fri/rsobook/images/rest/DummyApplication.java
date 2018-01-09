package si.fri.rsobook.images.rest;

import com.kumuluz.ee.discovery.annotations.RegisterService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@RegisterService
@ApplicationPath("/disc/api/v1/")
public class DummyApplication extends Application{
    // for discovery only
}
