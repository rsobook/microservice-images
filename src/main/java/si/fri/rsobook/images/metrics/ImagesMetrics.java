package si.fri.rsobook.images.metrics;

import com.codahale.metrics.Counter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ImagesMetrics {

    /*@Inject
    @Metric(name = "users_returned")*/
    private Counter imagesUploaded;


    public void addUsersReturned(int count){
        //usersReturned.inc(count);
    }

    public boolean isHealthy(){
        return true;
    }

}
