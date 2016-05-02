package no.haagensoftware.photoalbum.data;


public class Photo {
    private String photoName;
    private String photoFilename;

    public Photo() {
    }

    public Photo(String photoName, String photoFilename) {
        this.photoName = photoName;
        this.photoFilename = photoFilename;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoFilename() {
        return photoFilename;
    }

    public void setPhotoFilename(String photoFilename) {
        this.photoFilename = photoFilename;
    }
}
