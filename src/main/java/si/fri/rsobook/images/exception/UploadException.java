package si.fri.rsobook.images.exception;


public class UploadException extends Exception {

    public int errorCode;

    public UploadException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
