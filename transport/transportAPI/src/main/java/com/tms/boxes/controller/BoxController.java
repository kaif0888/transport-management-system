package com.tms.boxes.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tms.boxes.bean.BoxBean;
import com.tms.boxes.entity.HSNCodeEntity;
import com.tms.boxes.repository.HSNCodeRepository;
import com.tms.boxes.service.BoxService;
import com.tms.boxes.service.OrderBoxService;
import com.tms.product.entity.ProductEntity;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/boxes")
@CrossOrigin(origins = "*")
public class BoxController {
 
    @Autowired
    private BoxService boxService;
    
    @Autowired
    private OrderBoxService orderBoxService;
    
    @Autowired
    private HSNCodeRepository hsnCodeRepository;
    
    @ApiOperation(value = "Create a new box", response = BoxBean.class)
    @PostMapping("/createBox")
    public ResponseEntity<BoxBean> createBox(@RequestBody BoxBean boxBean) {
        try {
            BoxBean created = boxService.createBox(boxBean);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @ApiOperation(value = "Get box by ID", response = BoxBean.class)
    @GetMapping("/{boxId}")
    public ResponseEntity<BoxBean> getBoxById(@PathVariable String boxId) {
        try {
            BoxBean box = boxService.getBoxById(boxId);
            return new ResponseEntity<>(box, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @ApiOperation(value = "Get all boxes", response = List.class)
   @PostMapping("/getAllBoxes")
    public ResponseEntity<List<ProductEntity>> getAllBoxes(@RequestBody Map<String, Object> filters) {
        try {
            List<ProductEntity> boxes = boxService.getAllBoxes(filters);
            return new ResponseEntity<>(boxes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
//    @ApiOperation(value = "Update box", response = BoxBean.class)
//    @PutMapping("/{boxId}")
//    public ResponseEntity<BoxBean> updateBox(
//            @PathVariable String boxId,
//            @RequestBody BoxBean boxBean) {
//        try {
//            BoxBean updated = boxService.updateBox(boxId, boxBean);
//            return new ResponseEntity<>(updated, HttpStatus.OK);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//    }
    
//    @ApiOperation(value = "Delete box")
//    @DeleteMapping("/{boxId}")
//    public ResponseEntity<String> deleteBox(@PathVariable String boxId) {
//        try {
//            boxService.deleteBox(boxId);
//            return new ResponseEntity<>("Box deleted successfully", HttpStatus.OK);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }
    
    @ApiOperation(value = "Add product to box")
    @PostMapping("/{boxId}/products")
    public ResponseEntity<BoxBean> addProductToBox(
            @PathVariable String boxId,
            @RequestParam String productId,
            @RequestParam Integer quantity) {
        try {
            BoxBean updated = boxService.addProductToBox(boxId, productId, quantity);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @ApiOperation(value = "Remove product from box")
    @DeleteMapping("/{boxId}/products/{productId}")
    public ResponseEntity<BoxBean> removeProductFromBox(
            @PathVariable String boxId,
            @PathVariable String productId) {
        try {
            BoxBean updated = boxService.removeProductFromBox(boxId, productId);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @ApiOperation(value = "Add box to order")
    @PostMapping("/orders/{orderId}/boxes/{boxId}")
    public ResponseEntity<String> addBoxToOrder(
            @PathVariable String orderId,
            @PathVariable String boxId) {
        try {
            orderBoxService.addBoxToOrder(orderId, boxId);
            return new ResponseEntity<>("Box added to order successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @ApiOperation(value = "Get boxes for order", response = List.class)
    @GetMapping("/orders/{orderId}/boxes")
    public ResponseEntity<List<BoxBean>> getBoxesForOrder(@PathVariable String orderId) {
        try {
            List<BoxBean> boxes = orderBoxService.getBoxesForOrder(orderId);
            return new ResponseEntity<>(boxes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @ApiOperation(value = "Get all HSN codes", response = List.class)
    @GetMapping("/hsn-codes")
    public ResponseEntity<List<HSNCodeEntity>> getAllHSNCodes() {
        try {
            List<HSNCodeEntity> hsnCodes = hsnCodeRepository.findByIsActiveTrue();
            return new ResponseEntity<>(hsnCodes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @ApiOperation(value = "Search HSN codes", response = List.class)
    @GetMapping("/hsn-codes/search")
    public ResponseEntity<List<HSNCodeEntity>> searchHSNCodes(@RequestParam String query) {
        try {
            List<HSNCodeEntity> hsnCodes = hsnCodeRepository.findByDescriptionContainingIgnoreCase(query);
            return new ResponseEntity<>(hsnCodes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}