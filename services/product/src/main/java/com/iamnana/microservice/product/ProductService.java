package com.iamnana.microservice.product;

import com.iamnana.microservice.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Integer createProduct(ProductRequest request) {
        var product = mapper.toProduct(request);
        return repository.save(product).getId();
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
        // first we need to get all products id
        var productIds = request.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();

        // we need to check if all the ids are stored in the db
        var storedProducts = repository.findAllByIdInOrderById(productIds);

        // check if the products and stored productsIds are same
        // so for example: if client purchase product with id: 1,2 3, 4,
        // and in the database is only 1,2 then it should return back
        if(productIds.size() != storedProducts.size()){
            throw new ProductPurchaseException("One or more products does not exits.");
        }

        var storedRequests = request.stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();
        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();
        for(int i = 0; i < storedProducts.size(); i++){
            var product = storedProducts.get(i);
            var productRequest = storedRequests.get(i);

            if(product.getAvailableQuantity() < productRequest.quantity()){
                throw new ProductPurchaseException("Insufficient stock quantity for the product with ID:: "+ productRequest.productId());
            }
            // we need to update the available product
            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            repository.save(product);
            purchasedProducts.add(mapper.toProductPurchaseResponse(product, productRequest.quantity()));
        }
        return purchasedProducts;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId)
                .map(mapper::toProductResponse)
                .orElseThrow(()-> new EntityNotFoundException("Product not found with the ID:: " + productId));
    }

    public List<ProductResponse> findAllProducts() {
            return repository.findAll()
                    .stream()
                    .map(mapper::toProductResponse)
                    .collect(Collectors.toList());
    }
}
