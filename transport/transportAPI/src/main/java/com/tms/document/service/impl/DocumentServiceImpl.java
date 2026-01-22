package com.tms.document.service.impl;

import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.branch.entity.BranchEntity;
import com.tms.document.bean.DocumentBean;
import com.tms.document.entity.DocumentEntity;
import com.tms.document.repository.DocumentRepository;
import com.tms.document.service.DocumentService;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Value("${file.storage.path}")
    private String baseStoragePath;

    private final DocumentRepository documentRepository;
    
	@Autowired
	UserRepository  userRepository;
    
    @Autowired
    private FilterCriteriaService<DocumentEntity> filterCriteriaService;

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

  
    
    private String generateDocumentUniqueId() {
        String prefix = "DOC";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        List<DocumentEntity> todayDocs = documentRepository.findByDocumentIdStartingWith(fullPrefix);

        int maxSeq = todayDocs.stream()
            .map(doc -> doc.getDocumentId().substring(fullPrefix.length()))
            .mapToInt(seq -> {
                try {
                    return Integer.parseInt(seq);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max()
            .orElse(0);

        int nextSeq = maxSeq + 1;
        String formattedSeq = String.format("%03d", nextSeq);

        return fullPrefix + formattedSeq;
    }

    @Override
    public DocumentBean saveDocument(MultipartFile file,String documentName,@Nullable String documentStatus) {
        try {
            String documentId = generateDocumentUniqueId();

            LocalDate today = LocalDate.now();
            String datePath = today.getYear() + File.separator + today.getMonthValue() + File.separator + today.getDayOfMonth();
            String fullPath = baseStoragePath + File.separator + datePath;
            
            

            File dir = new File(fullPath);
            if (!dir.exists()) dir.mkdirs();

            // Create unique file name
            String uniqueFileName = documentId + "_" + System.nanoTime() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(fullPath, uniqueFileName);

            // Save file to disk
            Files.write(filePath, file.getBytes());

            // Save metadata to DB
            DocumentEntity entity = new DocumentEntity();
            entity.setDocumentId(documentId);
            entity.setName(file.getOriginalFilename());
            entity.setType(file.getContentType());
            entity.setFileUrl(filePath.toString());
            entity.setDocumentName(documentName);
            entity.setDocumentStatus(documentStatus);
            
            
//    		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    		entity.setCreatedBy(authentication.getName()); 
//    	    User currentUser = userRepository.findByEmail(authentication.getName())
//    	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
//    	    entity.setBranchIds(currentUser.getBranchIds());

            DocumentEntity savedEntity = documentRepository.save(entity);

            // Prepare response bean
            DocumentBean bean = new DocumentBean();
            bean.setId(savedEntity.getId());
            bean.setDocumentId(savedEntity.getDocumentId());
            bean.setName(savedEntity.getName());
            bean.setType(savedEntity.getType());
            bean.setFileUrl(savedEntity.getFileUrl());
            bean.setDocumentName(savedEntity.getDocumentName());
            bean.setDocumentStatus(savedEntity.getDocumentStatus());
            
            

            return bean;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save document: " + e.getMessage(), e);
        }
    }



    @Override
    public DocumentBean updateDocument(String documentId, MultipartFile file,String documentName,String documentStatus) {
        try {
            DocumentEntity existing = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with documentId: " + documentId));

            // Delete old file from disk
            Path oldPath = Paths.get(existing.getFileUrl());
            Files.deleteIfExists(oldPath);

            // Prepare new path based on current date
            LocalDate today = LocalDate.now();
            String datePath = today.getYear() + File.separator + today.getMonthValue() + File.separator + today.getDayOfMonth();
            String fullPath = baseStoragePath + File.separator + datePath;

            File dir = new File(fullPath);
            if (!dir.exists()) dir.mkdirs();

            // Create new file name
            String uniqueFileName = documentId + "_" + System.nanoTime() + "_" + file.getOriginalFilename();
            Path newPath = Paths.get(fullPath, uniqueFileName);
            Files.write(newPath, file.getBytes());

            // Update fields
            existing.setName(file.getOriginalFilename());
            existing.setType(file.getContentType());
            existing.setFileUrl(newPath.toString());
            existing.setDocumentName(documentName);
            existing.setDocumentStatus(documentStatus);
            
//    	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    	    existing.setCreatedBy(authentication.getName());
//    	 // Get branchId from current authenticated user
//    	    User currentUser = userRepository.findByEmail(authentication.getName())
//    	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
//    	    existing.setBranchIds(currentUser.getBranchIds());

            DocumentEntity saved = documentRepository.save(existing);

            // Convert to Bean
            DocumentBean bean = new DocumentBean();
            bean.setId(saved.getId());
            bean.setDocumentId(saved.getDocumentId());
            bean.setName(saved.getName());
            bean.setType(saved.getType());
            bean.setFileUrl(saved.getFileUrl());
            bean.setDocumentName(saved.getDocumentName());
            bean.setDocumentStatus(saved.getDocumentStatus());
            

            return bean;

        } catch (Exception e) {
            throw new RuntimeException("Failed to update document: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DocumentBean> filterDocuments(List<FilterCriteriaBean> filters, int limit) {
        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String username = authentication.getName();
//
//            User currentUser = userRepository.findByEmail(username)
//                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
//        	if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
//        	    // Remove any pre-existing branch filter (if present)
//        	    filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));
//
//        	    // Convert comma-separated branchIds string to comma-separated string for value
//        	    String branchIds = currentUser.getBranchIds(); // e.g., "BR001,BR002"
//
//        	    FilterCriteriaBean branchFilter = new FilterCriteriaBean();
//        	    branchFilter.setAttribute("branchIds");
//        	    branchFilter.setOperation(FilterOperation.AMONG);
//        	    branchFilter.setValue(branchIds);  // Still a comma-separated string
//        	    branchFilter.setValueType(String.class); // Optional
//
//        	    filters.add(branchFilter);
//        	}

        	
            @SuppressWarnings("unchecked")
            List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(
                    DocumentEntity.class, filters, limit);

            return filteredEntities.stream()
                    .map(entity -> convertToBean((DocumentEntity) entity))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error filtering documents: " + e.getMessage(), e);
        }
    }
    
 
    
    private DocumentBean convertToBean(DocumentEntity entity) {
        DocumentBean bean = new DocumentBean();
        bean.setId(entity.getId());
        bean.setDocumentId(entity.getDocumentId());
        bean.setName(entity.getName());
        bean.setType(entity.getType());
        bean.setFileUrl(entity.getFileUrl());
        bean.setDocumentName(entity.getDocumentName());
        bean.setDocumentStatus(entity.getDocumentStatus());
        return bean;
    }



    @Override
    public DocumentBean getDocumentById(String documentId) {
        DocumentEntity document = documentRepository.findByDocumentId(documentId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "The media is not present, upload the document again"
            ));
        
        return convertToBean(document);
    }



}
