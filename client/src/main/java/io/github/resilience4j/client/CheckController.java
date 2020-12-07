package io.github.resilience4j.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CheckController {

    public CheckController(CheckService checkService) {
        this.checkService = checkService;
    }

    private final CheckService checkService;


    @GetMapping("/check")
    public Map<String, Object> check() {
        return checkService.tryTest();
    }

}
