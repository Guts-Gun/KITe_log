package gutsandgun.kite_log.controller;

import gutsandgun.kite_log.service.Logging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("log")
public class LogController {
    private final Logging logging;
    @PostMapping(path = "/test")
    public String test(@RequestBody String msg) throws InterruptedException {
        logging.LogSave(msg);
        return "Clear";
    }
}
