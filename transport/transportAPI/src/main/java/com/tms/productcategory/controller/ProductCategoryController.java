package com.tms.productcategory.controller;

import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.location.bean.LocationBean;
import com.tms.productcategory.bean.ProductCategoryBean;
import com.tms.productcategory.service.ProductCategoryService;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService categoryService;

    @PostMapping("/createCategory")
    public ProductCategoryBean createCategory(@RequestBody ProductCategoryBean bean) {
        return categoryService.createCategory(bean);
    }
    
    @PostMapping("/filteredProductCategorys")
    public ResponseEntity<List<ProductCategoryBean>> filterBranchs(@RequestBody FilterRequest request) {
        try {
            int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
            List<ProductCategoryBean> filteredProductCategorys = categoryService.filteredProductCategorys(request.getFilters(), limit);
            return ResponseEntity.ok(filteredProductCategorys);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
	

    @GetMapping("/getCategoryById/{id}")
    public ProductCategoryBean getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/getAllCategories")
    public List<ProductCategoryBean> getAllCategories() {
        return categoryService.getAllCategories();
    }
//
//    @ApiOperation(value = "Update category by categoryId", response = ProductCategoryBean.class, notes = "Update an existing product category by categoryId.")
//    @PutMapping("/updateByCategoryId/{id}")
//    public ResponseEntity<ProductCategoryBean> updateByCategoryId(@PathVariable("id") String id, @RequestBody ProductCategoryBean bean) {
//        try {
//            // Set the id from path variable into the bean
//            bean.setCategoryId(id);
//
//            ProductCategoryBean updated = categoryService.updateCategory(bean);
//            return ResponseEntity.ok(updated);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//    }
    
    @ApiOperation(value = "Update category by categoryId", response = ProductCategoryBean.class, notes = "Update an existing product category by categoryId.")
    @PutMapping("/updateByCategoryId/{id}")
    public ResponseEntity<ProductCategoryBean> updateByCategoryId(@PathVariable("id") String id, @RequestBody ProductCategoryBean productCategoryBean) {
        try {
            // Set the id from path variable to the bean
        	productCategoryBean.setCategoryId(id);
            
        	 ProductCategoryBean productCategoryBean1 = categoryService.updateCategorybyId(productCategoryBean);
            return ResponseEntity.ok(productCategoryBean1);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/deleteCategory/{id}")
    public void deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
    }
}
