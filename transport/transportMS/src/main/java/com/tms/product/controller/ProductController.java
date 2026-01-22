package com.tms.product.controller;

import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.product.bean.ProductBean;
import com.tms.product.service.ProductService;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @PostMapping("/filterProducts")
    public ResponseEntity<List<ProductBean>> filterProducts(@RequestBody FilterRequest request) {
        try {
            int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
            List<ProductBean> filterProducts = productService.filterProducts(request.getFilters(), limit);
            return ResponseEntity.ok(filterProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PostMapping("/createProduct")
    public ProductBean createProduct(@RequestBody ProductBean bean) {
        return productService.createProduct(bean);
    }

    @GetMapping("/getProductById/{id}")
    public ProductBean getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/getAllProducts")
    public List<ProductBean> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/getProductsByCategoryId/{categoryId}")
    public List<ProductBean> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @ApiOperation(value = "Update product by productId", response = ProductBean.class, notes = "Update an existing product by productId.")
    @PutMapping("/updateByProductId/{id}")
    public ResponseEntity<ProductBean> updateByProductId(@PathVariable("id") Long id, @RequestBody ProductBean bean) {
        try {
            bean.setProductId(id);  // Set the ID from path variable
            ProductBean updated = productService.updateProduct(bean);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/deleteProduct/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
