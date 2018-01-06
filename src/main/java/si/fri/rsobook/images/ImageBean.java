package si.fri.rsobook.images;


import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rsobook.images.config.ImagesProperties;
import si.fri.rsobook.images.exception.UploadException;
import si.fri.rsobook.images.models.Image;
import si.fri.rsobook.images.utils.ImageUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.InternalServerErrorException;
import java.io.*;
import java.util.List;
import java.util.UUID;

@RequestScoped
@Log
public class ImageBean {

    @Inject
    private EntityManager em;

    @Inject
    private ImageBean imageBean;

    @Inject
    private ImagesProperties imagesProperties;

    private Logger log = LogManager.getLogger(ImageBean.class.getName());

    public Image getImage(String id) {
        Image img = em.find(Image.class, id);
        log.info("returning image " + img.toString());
        return img;
    }

    public List<Image> getAllImages() {
        List<Image> list =  ((TypedQuery<Image>) em.createQuery("SELECT i FROM image i")).getResultList();
        log.info("returning " + list.size() + " images");
        return list;
    }


    public Image createImage(Part part) throws UploadException {

        System.out.println("imagesProperties: " + imagesProperties.toString());

        if (part.getSize() > imagesProperties.getImagesMaxsize()) {
            throw new UploadException("Uploaded file is too big", HttpServletResponse.SC_BAD_REQUEST);

        } else {
            String uuid = UUID.randomUUID().toString();
            File tmpFile = new File(uuid + "_" + ImageUtils.extractFileName(part));
            try {
                tmpFile.createNewFile();

                InputStream stream = part.getInputStream();
                OutputStream out = new FileOutputStream(tmpFile);
                int read = 0;
                final byte[] bytes = new byte[1024];

                while ((read = stream.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                out.flush();
                out.close();
                stream.close();

                part.write(tmpFile.getName());
                System.out.println("Full path: " + tmpFile.getAbsolutePath());

                Image image = imageBean.uploadThenDelete(uuid, tmpFile);
                try {
                    em.getTransaction().begin();
                    em.persist(image);
                    em.getTransaction().commit();
                } catch (Exception e) {
                    log.error(e);
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                }

                tmpFile.delete(); // delete the temp file after if has been uploaded

                log.info("created image " + image.toString());

                return image;

            } catch (IOException e) {
                log.error(e);
                e.printStackTrace();
                imagesProperties.setIoExceptions(true); // mark service as not healthy, probably storage problems
                throw new UploadException("Tmp image file could not have been created", HttpServletResponse.SC_BAD_REQUEST);

            }
        }
    }

    /*
    * Upload the given file to cloud storage
    * */
    @CircuitBreaker(requestVolumeThreshold = 2)
    @Fallback(fallbackMethod = "uploadFallback")
    @Timeout(value = 5000)
    public Image uploadThenDelete(String uuid, File file) {
        // https://console.bluemix.net/docs/services/cloud-object-storage/libraries/java.html#java
        SDKGlobalConfiguration.IAM_ENDPOINT = "https://iam.bluemix.net/oidc/token";

        AmazonS3 client = ImageUtils.getS3client(
                imagesProperties.getStorageApiKey(),
                imagesProperties.getStorageInstanceID(),
                imagesProperties.getStorageEndpoint(),
                imagesProperties.getStorageLocation()
        );

        try {
            client.putObject(imagesProperties.getStorageBucketName(), file.getName(), file);
        } catch (Exception e) {
            System.err.println(e.getClass() + " " + e.getMessage());
            System.out.println("TRIGGERING InternalServerErrorException");
            System.out.println("Hystrix should call the fallback method");
            log.error(e);

            throw new InternalServerErrorException(e);
        }

        Image image = new Image(uuid, file.getName(), ImageUtils.getUrl(imagesProperties, file.getName()));
        return image;
    }

    public Image uploadFallback(String uuid, File file) {


        System.out.println("*******************************************");
        System.out.println("  FALLBACK METHOD  ");
        System.out.println("*******************************************");

        // we could have an upload to a different provider (Amazon S3) here
        // here we just return null

        Image fakeImage =  new Image(uuid, file.getName(), "https://rms.sexy/img/p1010266.jpg");
        log.info("created fake image in fallback method " + fakeImage.toString());
        return fakeImage;

    }

}
