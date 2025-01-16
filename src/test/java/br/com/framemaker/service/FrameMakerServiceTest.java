package br.com.framemaker.service;

import br.com.framemaker.util.exception.VideoNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrameMakerServiceTest {

    private final FrameMakerService frameMakerService = new FrameMakerService();

    @Test
    void should_create_frame() {
        File zip = new File("frames/output.zip");

        frameMakerService.createFrame("videos/test.mp4", "frames", "frames/output.zip");

        assertTrue(zip.exists());
    }

    @Test
    void should_create_frame_if_target_folder_does_not_exists() {
        File dir = new File("frames");
        File zip = new File("frames/output.zip");
        zip.delete();
        dir.delete();

        frameMakerService.createFrame("videos/test.mp4", "frames", "frames/output.zip");

        assertTrue(dir.exists());
        assertTrue(zip.exists());
    }

    @Test
    void should_throw_video_not_found_exception() {
        assertThrows(VideoNotFoundException.class, () -> frameMakerService.createFrame("videos/not_found.mp4",
                "frames", "frames/output.zip"));
    }
}
