package br.com.estudos.aws2.controller;

import br.com.estudos.aws2.model.ProductEventLogDto;
import br.com.estudos.aws2.repository.ProductEventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductEventLogController {

    private ProductEventLogRepository productEventLogRepository;

    @Autowired
    public ProductEventLogController(ProductEventLogRepository productEventLogRepository) {
        this.productEventLogRepository = productEventLogRepository;
    }

    @GetMapping("/events")
    public List<ProductEventLogDto> getAllEvents() {
        return StreamSupport
                .stream(productEventLogRepository.findAll().spliterator(), false)
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}")
    public List<ProductEventLogDto> getAllEventsByCode(@PathVariable String code) {
        return StreamSupport
                .stream(productEventLogRepository.findAllByPk(code).spliterator(), false)
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}/{event}")
    public List<ProductEventLogDto> getAllEventsByType(String code, String event) {
        return StreamSupport
                .stream(productEventLogRepository.findAllByPkAndSkStartsWith(code, event).spliterator(), false)
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }
}
