package br.com.framemaker.service;

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
        try {
            extractFrames(videoFilePath, outputDir, 30);
            createZip(outputDir, zipFilePath);
            System.out.println("Frames extracted and ZIP file created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractFrames(String videoFilePath, String outputDir, int intervalSeconds) throws Exception {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFilePath);
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
    }

    private static void createZip(String outputDir, String zipFilePath) throws IOException {
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
            }
        }
    }

    private static void deleteFile(File file) {
        if (!file.delete()) {
            System.err.println("Failed to delete file: " + file.getAbsolutePath());
        }
    }

}
