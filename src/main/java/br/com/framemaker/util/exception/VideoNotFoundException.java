package br.com.framemaker.util.exception;

public class VideoNotFoundException extends RuntimeException {

    public VideoNotFoundException(String videoFilePath) {
        super("Video file not found: " + videoFilePath, null, false, false);
    }

}
