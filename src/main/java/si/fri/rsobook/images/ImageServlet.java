package si.fri.rsobook.images;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.ibm.oauth.BasicIBMOAuthCredentials;
import si.fri.rsobook.images.config.ImagesProperties;
import si.fri.rsobook.images.models.Image;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.UUID;

@WebServlet(name = "ImageServlet", urlPatterns = "/v1/images")
@RequestScoped
@MultipartConfig
public class ImageServlet extends HttpServlet {

    @Inject
    private ImagesProperties properties;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO: pogruntaj zakaj tistle @Inject ne dela
        if (properties.getStorageApiKey() == null)
            properties.setAll(
                    2000000,
                    "nepovem",
                    "nepovem",
                    "rsobook-images-eu",
                    "eu",
                    "https://s3.fra-eu-geo.objectstorage.softlayer.net"
            );

        System.out.println("properties: " + properties.toString());
/*
        AmazonS3 client = getS3client(
                properties.getStorageApiKey(),
                properties.getStorageInstanceID(),
                properties.getStorageEndpoint(),
                properties.getStorageLocation()
        );

        listObjects(properties.getStorageBucketName(), client);
*/
        Image img = uploadImage(request.getPart("image"));
        System.out.println(img);

    }

    public Image uploadImage(Part part) throws IOException {
        Image image = null;

        if (part.getSize() > properties.getImagesMaxsize()) {
            //TODO: error, file too large
        } else {
            String uuid = UUID.randomUUID().toString();
            File tmpFile = new File(uuid + "_" + extractFileName(part));
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

            AmazonS3 client = getS3client(
                    properties.getStorageApiKey(),
                    properties.getStorageInstanceID(),
                    properties.getStorageEndpoint(),
                    properties.getStorageLocation()
            );

            try {
                client.putObject(properties.getStorageBucketName(), tmpFile.getName(), tmpFile);
            } catch (Exception e) {
                e.printStackTrace();
                //TODO: error, problem with uploading
            }

            //tmpFile.delete();

            image = new Image(uuid, tmpFile.getName(), getUrl(properties, tmpFile.getName()));
            //TODO persist

        }

        return image;
    }

    private static AmazonS3 getS3client(String api_key, String service_instance_id, String endpoint_url, String location) {
        AWSCredentials credentials;
        if (endpoint_url.contains("objectstorage.softlayer.net")) {
            credentials = new BasicIBMOAuthCredentials(api_key, service_instance_id);
        } else {
            String access_key = api_key;
            String secret_key = service_instance_id;
            credentials = new BasicAWSCredentials(access_key, secret_key);
        }
        ClientConfiguration clientConfig = new ClientConfiguration().withRequestTimeout(5000);
        clientConfig.addHeader("x-amz-acl", "public-read");
        clientConfig.setUseTcpKeepAlive(true);

        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint_url, location))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfig)
                .build();

        return client;
    }

    private static String getUrl(ImagesProperties props, String name) {
        return props.getStorageEndpoint() + "/" + props.getStorageBucketName() + "/" + name;
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }


    public static void listObjects(String bucketName, AmazonS3 s3Client)
    {
        System.out.println("Listing objects in bucket " + bucketName);
        ObjectListing objectListing = s3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
        }
        System.out.println();
    }
}
