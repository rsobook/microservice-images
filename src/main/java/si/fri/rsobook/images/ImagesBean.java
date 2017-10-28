package si.fri.rsobook.images;

import si.fri.rsobook.images.config.ImagesProperties;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;


@RequestScoped
public class ImagesBean {


    private Logger log = LogManager.getLogger(ImagesBean.class.getName());

    @Inject
    private ImagesProperties imagesProperties;


    @Inject
    private ImagesBean customersBean;




    public String getMaxImageSizeDescription() {
        return String.format("{'description': 'Max image size is set to: %d'}", imagesProperties.getImagesMaxsize());
    }

}
