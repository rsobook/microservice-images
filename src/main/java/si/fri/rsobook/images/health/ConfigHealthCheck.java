package si.fri.rsobook.images.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import si.fri.rsobook.images.config.ImagesProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class ConfigHealthCheck implements HealthCheck {

    @Inject
    private ImagesProperties imagesProperties;

    @Override
    public HealthCheckResponse call() {

        System.out.println("Config Health Check");
        System.out.println("imageProperties:" + imagesProperties.toString());
        if(imagesProperties.getImagesMaxsize() <= 5000){
            return HealthCheckResponse.named(ConfigHealthCheck.class.getSimpleName()).down().build();
        }

        return HealthCheckResponse.named(ConfigHealthCheck.class.getSimpleName()).up().build();
    }

}
