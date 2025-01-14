package br.com.framemaker.rest;

import br.com.framemaker.service.FrameMakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/frame-maker")
public class FrameMakerRestService {

    @Autowired
    private FrameMakerService service;

    @PostMapping
    public void createFrame() {
        service.createFrame("videos/test.mp4", "frames", "frames/output.zip");
    }

}
