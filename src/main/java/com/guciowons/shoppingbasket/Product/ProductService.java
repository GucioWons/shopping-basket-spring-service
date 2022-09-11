package com.guciowons.shoppingbasket.Product;

import feign.FeignException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductClient productClient;
    private final ProductRepository productRepository;

    public ProductService(ProductClient productClient, ProductRepository productRepository) {
        this.productClient = productClient;
        this.productRepository = productRepository;
    }

    @Scheduled(fixedDelay = 360000)
    public void insertProducts(){
        try {
            if (productRepository.findAll().isEmpty()) {
                productRepository.saveAll(productClient.getProducts());
            }
        }catch (FeignException e){
            System.out.println("Error connecting to external api. Products will be downloaded in one hour");
        }
    }

    public List<Product> getProducts() {
       return productRepository.findAll();
    }

    public Optional<Product> getProductById(String productId){
        return productRepository.findById(productId);
    }
}
