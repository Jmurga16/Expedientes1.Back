package com.gestionexpedientes.CRUD.service;

import com.gestionexpedientes.CRUD.entity.Product;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.utils.Operations;
import com.gestionexpedientes.CRUD.dto.ProductDto;
import com.gestionexpedientes.CRUD.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getOne(int id) throws ResourceNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("not found"));
    }

    public Product save(ProductDto dto) throws AttributeException {
        if(productRepository.existsByName(dto.getName()))
            throw new AttributeException("name already in use");
        int id = Operations.autoIncrement(productRepository.findAll());
        Product product = new Product(id, dto.getName(), dto.getPrice());
        return productRepository.save(product);
    }

    public Product update(int id, ProductDto dto) throws ResourceNotFoundException, AttributeException {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("not found"));
        if(productRepository.existsByName(dto.getName()) && productRepository.findByName(dto.getName()).get().getId() != id)
            throw new AttributeException("name already in use");
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        return productRepository.save(product);
    }

    public Product delete(int id) throws ResourceNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("not found"));;
        productRepository.delete(product);
        return product;
    }

    // private methods


}
