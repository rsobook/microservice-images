package si.fri.rsobook.images;



import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
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
import java.io.*;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class ImageBean {

    @Inject
    private EntityManager em;

    @Inject
    private ImageBean imageBean;

    @Inject
    private ImagesProperties imagesProperties;


    public Image getImage(String id) {
        Image img = em.find(Image.class, id);

        return img;
    }

    public List<Image> getAllImages() {
        return ((TypedQuery<Image>) em.createQuery("SELECT i FROM image i")).getResultList();
    }


    public Image createImage(Part part) throws IOException, UploadException {

        System.out.println("imagesProperties: " + imagesProperties.toString());
        Image image = null;

        if (part.getSize() > imagesProperties.getImagesMaxsize()) {
            throw new UploadException("Uploaded file is too big", HttpServletResponse.SC_BAD_REQUEST);

        } else {
            String uuid = UUID.randomUUID().toString();
            File tmpFile = new File(uuid + "_" + ImageUtils.extractFileName(part));
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

            //TODO: check if file is really an image?
            // https://console.bluemix.net/docs/services/cloud-object-storage/libraries/java.html#java

            SDKGlobalConfiguration.IAM_ENDPOINT = "https://iam.bluemix.net/oidc/token";

            AmazonS3 client = ImageUtils.getS3client(
                    imagesProperties.getStorageApiKey(),
                    imagesProperties.getStorageInstanceID(),
                    imagesProperties.getStorageEndpoint(),
                    imagesProperties.getStorageLocation()
            );

            try {
                client.putObject(imagesProperties.getStorageBucketName(), tmpFile.getName(), tmpFile);
            } catch (Exception e) {
                e.printStackTrace();
                throw new UploadException("Problem uploading (" + e.getMessage() + ")", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            image = new Image(uuid, tmpFile.getName(), ImageUtils.getUrl(imagesProperties, tmpFile.getName()));
            try {
                em.getTransaction().begin();
                em.persist(image);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            }

            tmpFile.delete(); // delete the temp file after if has been uploaded
        }

        return image;
    }


}
