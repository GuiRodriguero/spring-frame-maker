package br.com.framemaker.service;

import br.com.framemaker.util.exception.VideoNotFoundException;
import br.com.framemaker.util.exception.CreateZipException;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FrameMakerService {

    public void createFrame(String videoFilePath, String outputDir, String zipFilePath) {
        if (videoExists(videoFilePath)) {
            createFrameFolderIfDoesNotExist(outputDir);
            extractFrames(videoFilePath, outputDir, 30);
            createZip(outputDir, zipFilePath);
        } else {
            throw new VideoNotFoundException(videoFilePath);
        }
    }

    private boolean videoExists(String videoFilePath) {
        return new File(videoFilePath).exists();
    }

    private void createFrameFolderIfDoesNotExist(String outputDir) {
        File dir = new File(outputDir);
        if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Failed to create output directory: " + outputDir);
        }
    }

    private static void extractFrames(String videoFilePath, String outputDir, int intervalSeconds) {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFilePath);

        try {
            frameGrabber.start();

            Java2DFrameConverter converter = new Java2DFrameConverter();
            int frameRate = (int) frameGrabber.getFrameRate();
            int frameInterval = frameRate * intervalSeconds;

            int frameNumber = 0;
            Frame frame;
            while ((frame = frameGrabber.grabImage()) != null) {
                if (frameNumber % frameInterval == 0) {
                    BufferedImage bufferedImage = converter.convert(frame);
                    File outputFile = new File(outputDir, "frame_" + frameNumber + ".jpg");
                    ImageIO.write(bufferedImage, "jpg", outputFile);
                }
                frameNumber++;
            }

            frameGrabber.stop();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract frames from video: " + videoFilePath);
        }
    }

    private static void createZip(String outputDir, String zipFilePath) {
        File dir = new File(outputDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jpg"));

        if (files != null && files.length > 0) {
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
                for (File file : files) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zos.putNextEntry(zipEntry);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                    }
                    deleteFile(file);
                }
            } catch (IOException e) {
                throw new CreateZipException("Failed to create ZIP file: " + zipFilePath);
            }
        }
    }

    private static void deleteFile(File file) {
        if (!file.delete()) {
            throw new RuntimeException("Failed to delete file: " + file.getAbsolutePath());
        }
    }

}
