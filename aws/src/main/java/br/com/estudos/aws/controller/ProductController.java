package br.com.estudos.aws.controller;


import br.com.estudos.aws.enums.EventType;
import br.com.estudos.aws.model.Product;
import br.com.estudos.aws.repository.ProductRepository;
import br.com.estudos.aws.service.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/products")
public class ProductController {

    public static final String USERNAME = "matilde";
    private ProductPublisher productPublisher;

    private ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductPublisher productPublisher) {
        this.productRepository = productRepository;
        this.productPublisher = productPublisher;
    }

    @GetMapping
    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product productCreated = productRepository.save(product);
        productPublisher.publishProductEvent(product, EventType.PRODUCT_CREATED, USERNAME);
        return new ResponseEntity<>(productCreated, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @RequestBody Product product, @PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        product.setId(id);
        Product saved = productRepository.save(product);
        productPublisher.publishProductEvent(product, EventType.PRODUCT_UPDATE, USERNAME);

        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product finalProduct = product.get();
            productRepository.delete(finalProduct);
            productPublisher.publishProductEvent(finalProduct, EventType.PRODUCT_DELETED, USERNAME);

            return new ResponseEntity<>(finalProduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> product = productRepository.findByCode(code);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
