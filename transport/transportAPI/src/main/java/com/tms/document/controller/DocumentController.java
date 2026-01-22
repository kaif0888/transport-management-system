package com.tms.document.controller;

import com.tms.document.bean.DocumentBean;
import com.tms.document.service.DocumentService;
import com.tms.error.ErrorBean;
import com.tms.filter.criteria.bean.FilterRequest;
import io.swagger.v3.oas.annotations.Operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
 
@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private  ErrorBean errorBean ;
    
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
    	    "application/pdf",
    	    "image/jpeg",
    	    "image/png",
    	    "image/webp"
    	);
    
    @PostMapping("/filterDocuments")
    public ResponseEntity<List<DocumentBean>> filterDocuments(@RequestBody FilterRequest request) {
        try {
            int limit = request.getLimit() != null ? request.getLimit() : 100; 
            List<DocumentBean> filterDocuments = documentService.filterDocuments(request.getFilters(), limit);
            return ResponseEntity.ok(filterDocuments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(summary = "Upload a document with document name")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(@RequestPart("file") MultipartFile file,
                                                       @RequestPart("documentName") String documentName,
                                                       @RequestPart(value = "documentStatus",required =  false) String documentStatus) {
        try {
        	  if (file.isEmpty() || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
        		  Map<String, String> error = new HashMap<>();
        		  error.put("errorMessage", "The media type is not supported. Allowed types: PDF, JPEG, PNG, WEBP");
                  return ResponseEntity
                      .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                      .body(error);
              }
            DocumentBean saved = documentService.saveDocument(file, documentName,documentStatus); 
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @Operation(summary = "Get document by document Id")
    @GetMapping(value = "/getDocumentById/{documentId}")
    public ResponseEntity<?> getDocumentById(@PathVariable String documentId) {
        try {
            DocumentBean bean = documentService.getDocumentById(documentId);
            return new ResponseEntity<>(bean, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            errorBean.setSuccess(false);
            errorBean.setMessage(e.getReason()); // This gives you just the message
            return new ResponseEntity<>(errorBean, e.getStatusCode());
        } catch (Exception e) {
           
            errorBean.setSuccess(false);
            errorBean.setMessage("Internal Server Error");
            return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    
    @Operation(summary = "Update a document by documentId with document name")
    @PutMapping(value = "/updateByDocid/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentBean> updateDocumentByDocId(@PathVariable String documentId,
                                                               @RequestPart("file") MultipartFile file,
                                                               @RequestPart("documentName") String documentName,
                                                               @RequestPart(value = "documentStatus",required =  false) String documentStatus) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            DocumentBean updated = documentService.updateDocument(documentId, file, documentName,documentStatus); 
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
