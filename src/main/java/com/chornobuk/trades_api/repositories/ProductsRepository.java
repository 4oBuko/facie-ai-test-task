package com.chornobuk.trades_api.repositories;

import com.chornobuk.trades_api.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductsRepository  {
    private final StringRedisTemplate template;

    public String getValue(Long key){
        return template.opsForValue().get(String.valueOf(key));
    }

    public void setValue(Long key, String value) {
        template.opsForValue().set(String.valueOf(key), value);
    }
}
