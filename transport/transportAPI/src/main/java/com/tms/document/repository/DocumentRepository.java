package com.tms.document.repository;

import com.tms.document.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByDocumentIdStartingWith(String prefix);

    // For finding by document ID
    Optional<DocumentEntity> findByDocumentId(String documentId);
}
