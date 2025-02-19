package com.chornobuk.trades_api.services;

import com.chornobuk.trades_api.entities.Product;
import com.chornobuk.trades_api.repositories.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductsService {
    private final ProductsRepository repository;

    public Optional<Product> getById(Long id) {
        String value = repository.getValue(id);
        if( value != null) {
            Product product = new Product(id, value);
            return Optional.of(product);
        }
        return Optional.empty();
    }
}
