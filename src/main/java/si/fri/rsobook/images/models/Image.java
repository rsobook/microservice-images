package si.fri.rsobook.images.models;


import javax.persistence.Id;

public class Image {

    @Id
    private String id;

    private String filename;
    private String url;

    public Image(String id, String filename, String url) {
        this.id = id;
        this.filename = filename;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", filename='" + filename + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
