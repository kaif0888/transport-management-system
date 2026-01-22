package com.tms.driver.been;

import com.tms.document.bean.DocumentBean;

public class DriverDocumentRequest {
    private DriverBean driver;
    private DocumentBean document;

    // Getters and Setters
    public DriverBean getDriver() {
        return driver;
    }

    public void setDriver(DriverBean driver) {
        this.driver = driver;
    }

    public DocumentBean getDocument() {
        return document;
    }

    public void setDocument(DocumentBean document) {
        this.document = document;
    }
}
