package br.com.estudos.aws.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @GetMapping("/dog/{name}")
    public ResponseEntity dogTest(@PathVariable String name) {
      log.info("Test controller - name {}", name);

      return ResponseEntity.ok("Name " + name);
    }

    @PostMapping("/dog/{name}")
    public ResponseEntity postDogTest(@PathVariable String name) {
        log.info("Persistindo o cachorrinho - name {}", name);

        return ResponseEntity.ok("Cachorrinho persistido " + name);
    }
}
