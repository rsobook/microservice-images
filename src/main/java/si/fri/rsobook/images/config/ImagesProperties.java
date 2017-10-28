package si.fri.rsobook.images.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
@ConfigBundle("properties")
public class ImagesProperties {

    @ConfigValue(value = "maxsize", watch = true)
    private long imagesMaxsize;

    public long getImagesMaxsize() {
        return imagesMaxsize;
    }

    public void setImagesMaxsize(long imagesMaxsize) {
        this.imagesMaxsize = imagesMaxsize;
    }
}
