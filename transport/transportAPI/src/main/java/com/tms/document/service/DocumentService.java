package com.tms.document.service;

import com.tms.document.bean.DocumentBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

	DocumentBean saveDocument(MultipartFile file, String documentName, @Nullable String documentStatus );

	DocumentBean updateDocument(String documentId, MultipartFile file, String documentName,String documentStatus);

	List<DocumentBean> filterDocuments(List<FilterCriteriaBean> filters, int limit);
	
	DocumentBean getDocumentById(String documentId);


}
