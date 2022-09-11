package com.guciowons.shoppingbasket.Basket;

import com.guciowons.shoppingbasket.Exception.*;
import com.guciowons.shoppingbasket.Product.ProductService;
import org.springframework.stereotype.Service;


@Service
public class BasketService {
    private final BasketSummarizer basketSummarizer;
    private final BasketRepository basketRepository;
    private final ProductService productService;

    public BasketService(BasketSummarizer basketSummarizer, BasketRepository basketRepository, ProductService productService) {
        this.basketSummarizer = basketSummarizer;
        this.basketRepository = basketRepository;
        this.productService = productService;
    }

    public Basket createBasket() {
        Basket newBasket = new Basket(basketRepository.findAll().size());
        return basketRepository.save(newBasket);
    }


    public Basket addProductToBasket(int basketId, int productId, int quantity) throws NoExternalConnectionException, NoProductException, NoBasketException{
        return basketRepository.findById(basketId)
                .map(basket -> addProductIfExists(basket, productId, quantity))
                .orElseThrow(() -> new NoBasketException("No such basket"));
    }

    private Basket addProductIfExists(Basket basket, int productId, int quantity){
        return productService.getProductById(productId)
                .map(product -> {
                    basket.addProduct(product.getId(), quantity);
                    return basketRepository.save(basket);
                })
                .orElseThrow(() -> new NoProductException("No such product!"));
    }


    public Basket removeProductFromBasket(int basketId, int productId, int quantity) throws NoExternalConnectionException, NoProductInBasketException, NoProductException, NoBasketException {
        return basketRepository.findById(basketId)
                .map(basket -> removeProductIfExists(basket, productId, quantity))
                .orElseThrow(() -> new NoBasketException("No such basket"));
    }

    private Basket removeProductIfExists(Basket basket, int productId, int quantity){
        return productService.getProductById(productId)
                .map(product -> {
                    basket.removeProduct(product.getId(), quantity);
                    return basketRepository.save(basket);
                })
                .orElseThrow(() -> new NoProductException("No such product!"));
    }


    public BasketSummarized summarizeBasket(int basketId) throws NoBasketException, NoExternalConnectionException{
        return basketRepository.findById(basketId).map(
                basket -> basketSummarizer.summarizeBasket(basket.getContent(), productService.getProducts())
        ).orElseThrow(() -> new NoBasketException("No such basket"));
    }
}
