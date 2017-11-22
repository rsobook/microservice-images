package si.fri.rsobook.images.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import si.fri.rsobook.images.metrics.ImagesMetrics;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class MetricsHealthCheck  implements HealthCheck {

    @Inject
    private ImagesMetrics imagesMetrics;

    @Override
    public HealthCheckResponse call() {

        if(!imagesMetrics.isHealthy()){
            return HealthCheckResponse.named(MetricsHealthCheck.class.getSimpleName()).down().build();
        }

        return HealthCheckResponse.named(MetricsHealthCheck.class.getSimpleName()).up().build();
    }

}
