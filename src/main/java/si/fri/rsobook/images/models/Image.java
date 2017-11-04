package si.fri.rsobook.images.models;


import javax.persistence.*;

@Entity(name = "image")
public class Image {

    @Id
    private String id;

    @Column(name = "filename")
    private String filename;

    @Column(name = "url")
    private String url;

    public Image(String id, String filename, String url) {
        this.id = id;
        this.filename = filename;
        this.url = url;
    }

    public Image() {
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
