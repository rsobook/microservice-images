package si.fri.rsobook.images.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
@ConfigBundle("properties")
public class ImagesProperties {

    @ConfigValue(value = "maxsize", watch = true)
    private long imagesMaxsize;

    @ConfigValue(value = "storageapikey", watch = true)
    private String storageApiKey;

    @ConfigValue(value = "storageinstanceid", watch = true)
    private String storageInstanceID;

    @ConfigValue(value = "storagekeyname", watch = true)
    private String storageKeyName;

    @ConfigValue(value = "storagebucket", watch = true)
    private String storageBucketName;

    @ConfigValue(value = "storagelocation", watch = true)
    private String storageLocation;

    @ConfigValue(value = "storageendpoint", watch = true)
    private String storageEndpoint;

    public long getImagesMaxsize() {
        return imagesMaxsize;
    }

    public String getStorageApiKey() {
        return storageApiKey;
    }

    public String getStorageInstanceID() {
        return storageInstanceID;
    }

    public String getStorageBucketName() {
        return storageBucketName;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getStorageEndpoint() {
        return storageEndpoint;
    }

    public void setAll(long imagesMaxsize, String storageApiKey, String storageInstanceID, String storageBucketName, String storageLocation, String storageEndpoint) {
        this.imagesMaxsize = imagesMaxsize;
        this.storageApiKey = storageApiKey;
        this.storageInstanceID = storageInstanceID;
        this.storageBucketName = storageBucketName;
        this.storageLocation = storageLocation;
        this.storageEndpoint = storageEndpoint;
    }

    @Override
    public String toString() {
        return "ImagesProperties{" +
                "imagesMaxsize=" + imagesMaxsize +
                ", storageApiKey='" + storageApiKey + '\'' +
                ", storageInstanceID='" + storageInstanceID + '\'' +
                ", storageBucketName='" + storageBucketName + '\'' +
                ", storageLocation='" + storageLocation + '\'' +
                ", storageEndpoint='" + storageEndpoint + '\'' +
                '}';
    }
}
