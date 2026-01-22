"use client";
import { useFormik } from "formik";
import { Input } from "@/Components/atom/Input";
import { Button } from "@/Components/atom/Button";
import { Select } from "@/Components/atom/Select";
import { vehicleValidationSchema } from "@/Data/constant";
import { Checkbox } from "@/Components/atom/Checkbox";
import { DatePicker } from "@/Components/atom/DatePicker";
import moment from "moment";
import {
  createVehicleWithDocuments,
  UpdateVehcile,
  getVehicleById,
  getAllVehicleType,
} from "@/service/vehicle";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { vehicleStatus } from "@/Data/impData";
import { FileUpload } from "@/Components/atom/UploadFile";

export default function AddVehicle({
  handleCancel,
  vehicleListDataRefetch,
  selectedId,
}) {
  const [vehicleTypeOptions, setVehicleTypeOptions] = useState([]);
  const [selectedVehicleType, setSelectedVehicleType] = useState(null);
  const [isUploading, setIsUploading] = useState(false);

  // Fetch vehicle data if editing
  const { data: vehicleData, isLoading, refetch } = useQuery({
    queryKey: ["vehicleDetail", selectedId],
    queryFn: () => (selectedId ? getVehicleById(selectedId) : null),
    enabled: !!selectedId,
  });

  useEffect(() => {
    if (selectedId) {
      refetch();
    }
  }, [refetch, selectedId]);

  // Fetch vehicle types
  const { data: vehicleTypeData, isLoading: vehicleTypeLoading } = useQuery({
    queryKey: ["vehicleTypeData"],
    queryFn: () => getAllVehicleType(),
  });

  useEffect(() => {
    if (vehicleTypeData) {
      const options = vehicleTypeData.map((item) => ({
        label: `${item.vehicleTypeName || item.typeName || item.name}`,
        value: item.vehicleTypeId || item.typeId || item.id,
        data: item,
      }));
      setVehicleTypeOptions(options);
    }
  }, [vehicleTypeData]);

  const createFileFromUrl = (doc) => ({
    uid: `existing-${doc.documentId}`,
    id: doc.documentId,
    name: doc.documentName || 'document',
    status: "done",
    url: doc.fileUrl,
    thumbUrl: doc.fileUrl,
    type: doc.contentType || "application/octet-stream", 
  });
  
    // Helper: Convert base64 to File
    const toBase64 = (file) =>
      new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result.split(',')[1]);
        reader.onerror = (error) => reject(error);
      });
  
    const formik = useFormik({
      initialValues: {
        registrationNumber: "",
        vehicleTypeId: null,
        vehicleNumber: "",
        model: "",
        capacity: 1,
        isRented: false,
        status: null,
        ownerName: "",
        rentalVendorName: "",
        rentalStartDate: null,
        rentalEndDate: null,
        rentalAmountPerMonth: 0,
        rentalAgreementNumber: "",
        rentalAgreementDocumentFiles: [],
        rcNumber: "",
        rcExpiryDate: null,
        rcDocumentFiles: [],
        insurancePolicyNumber: "",
        insuranceExpiryDate: null,
        insuranceDocumentFiles: [],
        fitnessCertificateNumber: "",
        fitnessExpiryDate: null,
        fitnessDocumentFiles: [],
        permitNumber: "",
        permitExpiryDate: null,
        permitDocumentFiles: [],
        pollutionCertificateNumber: "",
        pollutionExpiryDate: null,
        pollutionDocumentFiles: [],
        taxValidUpto: null,
        roadTaxReceiptNumber: "",
        roadTaxDocumentFiles: [],
      },
      validateOnChange: true,
      validateOnBlur: true,
      enableReinitialize: true,
      onSubmit: async (values, { resetForm }) => {
        try {
          setIsUploading(true);
  
          // This helper will create a document payload for either a new file or an existing one
          const getDocumentPayload = async (fileList, docType, number, expiryDate, additionalFields = {}) => {
            const file = fileList[0];
            if (!file) return null;
  
            // If it's a new file, upload it
            if (file.originFileObj) {
              return {
                documentName: file.name,
                documentType: docType,
                documentStatus: "ACTIVE",
                fileBase64: await toBase64(file.originFileObj),
                contentType: file.originFileObj.type,
                number: number,
                expiryDate: expiryDate ? moment(expiryDate).format("YYYY-MM-DD") : null,
                ...additionalFields,
              };
            }
  
            // If it's an existing file, just send its ID and the updated metadata
            if (file.id) {
              return {
                documentId: file.id,
                number: number,
                expiryDate: expiryDate ? moment(expiryDate).format("YYYY-MM-DD") : null,
                ...additionalFields,
              };
            }
            
            return null;
          };
  
          // Base payload
          const payload = {
              registrationNumber: values.registrationNumber,
              vehicleNumber: values.vehicleNumber,
              model: values.model,
              capacity: values.capacity,
              isRented: values.isRented,
              status: values.status,
              ownerName: values.ownerName,
              vehicleType: {
                vehicleTypeId: values.vehicleTypeId,
              },
          };
  
          // Rental payload
          if (values.isRented) {
              payload.rental = {
                vendorName: values.rentalVendorName,
                startDate: values.rentalStartDate ? moment(values.rentalStartDate).format("YYYY-MM-DD") : null,
                endDate: values.rentalEndDate ? moment(values.rentalEndDate).format("YYYY-MM-DD") : null,
                amountPerMonth: values.rentalAmountPerMonth,
                agreementNumber: values.rentalAgreementNumber,
              };
              const rentalFile = values.rentalAgreementDocumentFiles[0];
              if (rentalFile?.originFileObj) {
                payload.rental.document = {
                    documentName: rentalFile.name,
                    documentType: "RENTAL_AGREEMENT",
                    documentStatus: "ACTIVE",
                    fileBase64: await toBase64(rentalFile.originFileObj),
                    contentType: rentalFile.originFileObj.type,
                };
              } else if (rentalFile?.id) {
                payload.rental.document = { documentId: rentalFile.id };
              }
          }
  
          // Documents payload
          const documents = {};
          const docInfo = [
            { key: 'rc', type: 'RC', fileList: values.rcDocumentFiles, number: values.rcNumber, expiry: values.rcExpiryDate },
            { key: 'insurance', type: 'INSURANCE', fileList: values.insuranceDocumentFiles, number: values.insurancePolicyNumber, expiry: values.insuranceExpiryDate },
            { key: 'fitness', type: 'FITNESS', fileList: values.fitnessDocumentFiles, number: values.fitnessCertificateNumber, expiry: values.fitnessExpiryDate },
            { key: 'permit', type: 'PERMIT', fileList: values.permitDocumentFiles, number: values.permitNumber, expiry: values.permitExpiryDate },
            { key: 'pollution', type: 'POLLUTION', fileList: values.pollutionDocumentFiles, number: values.pollutionCertificateNumber, expiry: values.pollutionExpiryDate },
            { key: 'roadTax', type: 'ROAD_TAX', fileList: values.roadTaxDocumentFiles, number: values.roadTaxReceiptNumber, expiry: values.taxValidUpto, additional: { receiptNumber: values.roadTaxReceiptNumber, validUpto: values.taxValidUpto ? moment(values.taxValidUpto).format("YYYY-MM-DD") : null } },
          ];

          for (const doc of docInfo) {
            const file = doc.fileList[0];
            let docPayload = null;

            if (file?.originFileObj) { // New file upload
              docPayload = {
                documentName: file.name,
                documentType: doc.type,
                documentStatus: "ACTIVE",
                fileBase64: await toBase64(file.originFileObj),
                contentType: file.originFileObj.type,
                number: doc.number,
                expiryDate: doc.expiry ? moment(doc.expiry).format("YYYY-MM-DD") : null,
                ...(doc.additional || {}),
              };
            } else if (file?.id) { // Existing file still there
              docPayload = {
                documentId: file.id,
                number: doc.number,
                expiryDate: doc.expiry ? moment(doc.expiry).format("YYYY-MM-DD") : null,
                ...(doc.additional || {}),
              };
            } else if (doc.number || doc.expiry) { // No file in UI, but number/date exists
              if (selectedId) {
                const originalDocId = vehicleData?.documents?.[doc.key]?.documentId;
                if (originalDocId) {
                  docPayload = {
                    documentId: originalDocId,
                    number: doc.number,
                    expiryDate: doc.expiry ? moment(doc.expiry).format("YYYY-MM-DD") : null,
                    ...(doc.additional || {}),
                  };
                } else { // No original doc, create new one
                  docPayload = {
                    documentType: doc.type,
                    number: doc.number,
                    expiryDate: doc.expiry ? moment(doc.expiry).format("YYYY-MM-DD") : null,
                    ...(doc.additional || {}),
                  };
                }
              } else { // Create mode
                docPayload = {
                  documentType: doc.type,
                  number: doc.number,
                  expiryDate: doc.expiry ? moment(doc.expiry).format("YYYY-MM-DD") : null,
                  ...(doc.additional || {}),
                };
              }
            }

            if (docPayload) {
              documents[doc.key] = docPayload;
            }
          }
          if (Object.keys(documents).length > 0) {
            payload.documents = documents;
          }
  
          if (selectedId) {
            await UpdateVehcile(selectedId, payload);
            toast.success("Vehicle updated successfully");
          } else {
            await createVehicleWithDocuments(payload);
            toast.success("Vehicle added successfully");
          }
  
          await vehicleListDataRefetch();
          resetForm();
          handleCancel();
        } catch (error) {
          console.error("Error submitting form:", error);
          toast.error(error.message || "Failed to save vehicle");
        } finally {
          setIsUploading(false);
        }
      },
    });
  
    // SIMPLIFIED: Load data directly from vehicleData - no additional API calls
    useEffect(() => {
      if (!vehicleData) return;
  
      // Get documents directly from vehicle data
      const docs = vehicleData.documents || {};
      const rental = vehicleData.rental || {};
  
      // Create file lists from URLs
      const rcFiles = docs.rc?.fileUrl ? [createFileFromUrl(docs.rc)] : [];
      const insuranceFiles = docs.insurance?.fileUrl ? [createFileFromUrl(docs.insurance)] : [];
      const fitnessFiles = docs.fitness?.fileUrl ? [createFileFromUrl(docs.fitness)] : [];
      const permitFiles = docs.permit?.fileUrl ? [createFileFromUrl(docs.permit)] : [];
      const pollutionFiles = docs.pollution?.fileUrl ? [createFileFromUrl(docs.pollution)] : [];
      const taxFiles = docs.roadTax?.fileUrl ? [createFileFromUrl(docs.roadTax)] : [];
      const rentalFiles = rental.document?.fileUrl ? [createFileFromUrl(rental.document)] : [];
  
      formik.setValues({
        registrationNumber: vehicleData.registrationNumber || "",
        vehicleTypeId: vehicleData.vehicleType?.vehicleTypeId || null,
        vehicleNumber: vehicleData.vehicleNumber || vehicleData.vehiclNumber || "",
        model: vehicleData.model || "",
        capacity: vehicleData.capacity || 1,
        isRented: vehicleData.isRented || false,
        status: vehicleData.status || "",
        ownerName: vehicleData.ownerName || "",
        
        // Rental details
        rentalVendorName: rental.vendorName || vehicleData.rentalVendorName || "",
        rentalStartDate: (rental.startDate || vehicleData.rentalStartDate)
          ? moment(rental.startDate || vehicleData.rentalStartDate)
          : null,
        rentalEndDate: (rental.endDate || vehicleData.rentalEndDate)
          ? moment(rental.endDate || vehicleData.rentalEndDate)
          : null,
        rentalAmountPerMonth: rental.amountPerMonth || vehicleData.rentalAmountPerMonth || 0,
        rentalAgreementNumber: rental.agreementNumber || vehicleData.rentalAgreementNumber || "",
        rentalAgreementDocumentFiles: rentalFiles,
        
        // RC Details
        rcNumber: docs.rc?.number || vehicleData.rcNumber || "",
        rcExpiryDate: (docs.rc?.expiryDate || vehicleData.rcExpiryDate)
          ? moment(docs.rc?.expiryDate || vehicleData.rcExpiryDate)
          : null,
        rcDocumentFiles: rcFiles,
        
        // Insurance Details
        insurancePolicyNumber: docs.insurance?.number || vehicleData.insurancePolicyNumber || "",
        insuranceExpiryDate: (docs.insurance?.expiryDate || vehicleData.insuranceExpiryDate)
          ? moment(docs.insurance?.expiryDate || vehicleData.insuranceExpiryDate)
          : null,
        insuranceDocumentFiles: insuranceFiles,
        
        // Fitness Details
        fitnessCertificateNumber: docs.fitness?.number || vehicleData.fitnessCertificateNumber || "",
        fitnessExpiryDate: (docs.fitness?.expiryDate || vehicleData.fitnessExpiryDate)
          ? moment(docs.fitness?.expiryDate || vehicleData.fitnessExpiryDate)
          : null,
        fitnessDocumentFiles: fitnessFiles,
        
        // Permit Details
        permitNumber: docs.permit?.number || vehicleData.permitNumber || "",
        permitExpiryDate: (docs.permit?.expiryDate || vehicleData.permitExpiryDate)
          ? moment(docs.permit?.expiryDate || vehicleData.permitExpiryDate)
          : null,
        permitDocumentFiles: permitFiles,
        
        // Pollution Details
        pollutionCertificateNumber: docs.pollution?.number || vehicleData.pollutionCertificateNumber || "",
        pollutionExpiryDate: (docs.pollution?.expiryDate || vehicleData.pollutionExpiryDate)
          ? moment(docs.pollution?.expiryDate || vehicleData.pollutionExpiryDate)
          : null,
        pollutionDocumentFiles: pollutionFiles,
        
        // Tax Details
        taxValidUpto: (docs.roadTax?.validUpto || vehicleData.taxValidUpto)
          ? moment(docs.roadTax?.validUpto || vehicleData.taxValidUpto)
          : null,
        roadTaxReceiptNumber: docs.roadTax?.number || vehicleData.roadTaxReceiptNumber || "",
        roadTaxDocumentFiles: taxFiles,
      });
    }, [vehicleData]);

  useEffect(() => {
    if (!formik.values.vehicleTypeId) {
      setSelectedVehicleType(null);
      return;
    }

    const selectedOption = vehicleTypeOptions.find(
      (option) => option.value === formik.values.vehicleTypeId
    );
    setSelectedVehicleType(selectedOption?.data || null);
  }, [formik.values.vehicleTypeId, vehicleTypeOptions]);

  const getVehicleTypeLabel = () =>
    selectedVehicleType
      ? `${selectedVehicleType.vehicleTypeName || selectedVehicleType.typeName || selectedVehicleType.name}`
      : null;

  const handleDownload = (file) => {
    if (!file.url) {
      toast.error("File URL not available");
      return;
    }

    const cacheBustedUrl = file.url + (file.url.includes('?') ? '&' : '?') + `t=${new Date().getTime()}`;

    const link = document.createElement("a");
    link.href = cacheBustedUrl;
    link.download = file.name || "vehicle-document";
    link.target = "_blank";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    toast.success("Download started");
  };

  const handlePreview = async (file) => {
  const previewUrl = file.url || file.thumbUrl;

  if (!previewUrl) {
    toast.error("Preview not available");
    return;
  }

  window.open(previewUrl, "_blank");
};

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading vehicle data...</div>;
  }

  return (
    <form className="space-y-6 max-h-[70vh] overflow-y-auto px-2">
      {/* Basic Vehicle Details Section */}
      <div className="border-b pb-4">
        <h3 className="text-lg font-semibold mb-3 text-gray-800">Basic Vehicle Details</h3>
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Vehicle Number"
            id="vehicleNumber"
            name="vehicleNumber"
            required
            type="text"
            placeholder="Enter vehicle number"
            value={formik.values.vehicleNumber}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.vehicleNumber && formik.errors.vehicleNumber}
            touched={formik.touched.vehicleNumber}
          />
          <Input
            label="Registration Number"
            id="registrationNumber"
            name="registrationNumber"
            required
            type="text"
            placeholder="Enter vehicle registration number"
            value={formik.values.registrationNumber}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.registrationNumber && formik.errors.registrationNumber}
            touched={formik.touched.registrationNumber}
          />

          <Select
            id="vehicleTypeId"
            name="vehicleTypeId"
            label="Vehicle Type"
            placeholder="Select a vehicle type"
            fieldWidth="100%"
            value={formik.values.vehicleTypeId}
            onChange={(value) => formik.setFieldValue("vehicleTypeId", value)}
            onBlur={() => formik.setFieldTouched("vehicleTypeId", true)}
            error={formik.errors.vehicleTypeId}
            touched={formik.touched.vehicleTypeId}
            required
            loading={vehicleTypeLoading}
            options={vehicleTypeOptions}
            labelRender={getVehicleTypeLabel()}
            noOptionMsg="No vehicle type available. Please add vehicle type first."
            disabled={vehicleTypeLoading || vehicleTypeOptions.length === 0}
          />
          <Input
            label="Vehicle Model"
            id="model"
            name="model"
            required
            type="text"
            placeholder="Enter vehicle model"
            value={formik.values.model}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.model && formik.errors.model}
            touched={formik.touched.model}
          />
          <Input
            label="Capacity (KG)"
            id="capacity"
            name="capacity"
            type="number"
            required
            placeholder="Enter vehicle capacity"
            value={formik.values.capacity}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.capacity && formik.errors.capacity}
            touched={formik.touched.capacity}
          />
          <Select
            id="status"
            name="status"
            label="Status"
            placeholder="Select status"
            fieldWidth="100%"
            value={formik.values.status}
            onChange={(value) => formik.setFieldValue("status", value)}
            onBlur={() => formik.setFieldTouched("status", true)}
            error={formik.errors.status}
            touched={formik.touched.status}
            required
            options={vehicleStatus}
            noOptionMsg="No payment status available."
          />
          <Input
            label="Owner Name"
            id="ownerName"
            name="ownerName"
            type="text"
            placeholder="Enter owner name"
            value={formik.values.ownerName}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.ownerName && formik.errors.ownerName}
            touched={formik.touched.ownerName}
          />
        </div>
      </div>

      {/* Rental Details Section */}
      <div className="border-b pb-4">
        <Checkbox
          id="isRented"
          name="isRented"
          checked={formik.values.isRented}
          className="text-sm font-semibold text-gray-700 mb-3"
          onChange={(e) => formik.setFieldValue("isRented", e.target.checked)}
        >
          Is Rented
        </Checkbox>

        {formik.values.isRented && (
          <div className="grid grid-cols-2 gap-4 mt-3">
            <Input
              label="Rental Vendor Name"
              id="rentalVendorName"
              name="rentalVendorName"
              type="text"
              placeholder="Enter rental vendor name"
              value={formik.values.rentalVendorName}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.rentalVendorName && formik.errors.rentalVendorName}
              touched={formik.touched.rentalVendorName}
            />
            <Input
              label="Rental Amount Per Month (₹)"
              id="rentalAmountPerMonth"
              name="rentalAmountPerMonth"
              type="number"
              placeholder="Enter rental amount per month"
              value={formik.values.rentalAmountPerMonth}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.rentalAmountPerMonth && formik.errors.rentalAmountPerMonth}
              touched={formik.touched.rentalAmountPerMonth}
            />
            <DatePicker
              label="Rental Start Date"
              id="rentalStartDate"
              name="rentalStartDate"
              placeholder="Select start date"
              value={formik.values.rentalStartDate || null}
              onChange={(name, value) => formik.setFieldValue(name, value)}
              onBlur={formik.handleBlur}
              error={formik.errors.rentalStartDate}
              touched={formik.touched.rentalStartDate}
            />
            <DatePicker
              label="Rental End Date"
              id="rentalEndDate"
              name="rentalEndDate"
              placeholder="Select end date"
              value={formik.values.rentalEndDate || null}
              onChange={(name, value) => formik.setFieldValue(name, value)}
              onBlur={formik.handleBlur}
              error={formik.errors.rentalEndDate}
              touched={formik.touched.rentalEndDate}
            />
            <Input
              label="Rental Agreement Number"
              id="rentalAgreementNumber"
              name="rentalAgreementNumber"
              type="text"
              placeholder="Enter agreement number"
              value={formik.values.rentalAgreementNumber}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.rentalAgreementNumber && formik.errors.rentalAgreementNumber}
              touched={formik.touched.rentalAgreementNumber}
            />
            <div className="col-span-2 mt-2">
              <FileUpload
                label="Upload Rental Agreement"
                acceptedTypes={[".pdf", ".jpg", ".png"]}
                fileList={formik.values.rentalAgreementDocumentFiles}
                onChange={(info) =>
                  formik.setFieldValue("rentalAgreementDocumentFiles", info.fileList)
                }
                onPreview={handlePreview}
                multiple={false}
                showDownload={!!selectedId}
                onDownload={handleDownload}
              />
            </div>
          </div>
        )}
      </div>

      {/* RC Details Section */}
      <div className="border-b pb-4">
        <h3 className="text-lg font-semibold mb-3 text-gray-800">RC Details</h3>
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="RC Number"
            id="rcNumber"
            name="rcNumber"
            type="text"
            placeholder="Enter RC number"
            value={formik.values.rcNumber}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.rcNumber && formik.errors.rcNumber}
            touched={formik.touched.rcNumber}
          />
          <DatePicker
            label="RC Expiry Date"
            id="rcExpiryDate"
            name="rcExpiryDate"
            placeholder="Select expiry date"
            value={formik.values.rcExpiryDate || null}
            onChange={(name, value) => formik.setFieldValue(name, value)}
            onBlur={formik.handleBlur}
            error={formik.errors.rcExpiryDate}
            touched={formik.touched.rcExpiryDate}
          />
        </div>
        <div className="mt-4">
          <FileUpload
            label="Upload RC Document"
            acceptedTypes={[".pdf", ".jpg", ".png"]}
            fileList={formik.values.rcDocumentFiles}
            onChange={(info) =>
              formik.setFieldValue("rcDocumentFiles", info.fileList)
            }
            onPreview={handlePreview}
            error={formik.errors.rcDocumentFiles}
            touched={formik.touched.rcDocumentFiles}
            multiple={false}
            showDownload={!!selectedId}
            onDownload={handleDownload}
          />
        </div>
      </div>

      {/* Insurance Details Section */}
      <div className="border-b pb-4">
        <h3 className="text-lg font-semibold mb-3 text-gray-800">Insurance Details</h3>
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Insurance Policy Number"
            id="insurancePolicyNumber"
            name="insurancePolicyNumber"
            type="text"
            placeholder="Enter policy number"
            value={formik.values.insurancePolicyNumber}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.insurancePolicyNumber && formik.errors.insurancePolicyNumber}
            touched={formik.touched.insurancePolicyNumber}
          />
          <DatePicker
            label="Insurance Expiry Date"
            id="insuranceExpiryDate"
            name="insuranceExpiryDate"
            placeholder="Select expiry date"
            value={formik.values.insuranceExpiryDate || null}
            onChange={(name, value) => formik.setFieldValue(name, value)}
            onBlur={formik.handleBlur}
            error={formik.errors.insuranceExpiryDate}
            touched={formik.touched.insuranceExpiryDate}
          />
        </div>
        <div className="mt-4">
          <FileUpload
            label="Upload Insurance Document"
            acceptedTypes={[".pdf", ".jpg", ".png"]}
            fileList={formik.values.insuranceDocumentFiles}
            onChange={(info) =>
              formik.setFieldValue("insuranceDocumentFiles", info.fileList)
            }
            onPreview={handlePreview}
            multiple={false}
            showDownload={!!selectedId}
            onDownload={handleDownload}
          />
        </div>
      </div>

      {/* Fitness Details */}
      <div className="border-b pb-4">
        <h3 className="text-lg font-semibold mb-3 text-gray-800">Fitness Details</h3>
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Fitness Certificate Number"
            name="fitnessCertificateNumber"
            value={formik.values.fitnessCertificateNumber}
            onChange={formik.handleChange}
          />
          <DatePicker
            label="Fitness Expiry Date"
            name="fitnessExpiryDate"
            value={formik.values.fitnessExpiryDate}
            onChange={(name, value) => formik.setFieldValue(name, value)}
          />
        </div>
        <div className="mt-4">
          <FileUpload
            label="Upload Fitness Certificate"
            acceptedTypes={[".pdf", ".jpg", ".png"]}
            fileList={formik.values.fitnessDocumentFiles}
            onChange={(info) =>
              formik.setFieldValue("fitnessDocumentFiles", info.fileList)
            }
            onPreview={handlePreview}
            
            multiple={false}
            showDownload={!!selectedId}
            onDownload={handleDownload}
          />
        </div>
      </div>

      {/* Permit Details */}
      <div className="border-b pb-4">
        <h3 className="text-lg font-semibold mb-3 text-gray-800">Permit Details</h3>
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Permit Number"
            name="permitNumber"
            value={formik.values.permitNumber}
            onChange={formik.handleChange}
          />
          <DatePicker
            label="Permit Expiry Date"
            name="permitExpiryDate"
            value={formik.values.permitExpiryDate}
            onChange={(name, value) => formik.setFieldValue(name, value)}
          />
        </div>
        <div className="mt-4">
          <FileUpload
            label="Upload Permit Document"
            acceptedTypes={[".pdf", ".jpg", ".png"]}
            fileList={formik.values.permitDocumentFiles}
            onChange={(info) =>
              formik.setFieldValue("permitDocumentFiles", info.fileList)
            }
            onPreview={handlePreview}
            
            multiple={false}
            showDownload={!!selectedId}
            onDownload={handleDownload}
          />
        </div>
      </div>

      {/* Pollution Details */}
      <div className="border-b pb-4">
        <h3 className="text-lg font-semibold mb-3 text-gray-800">Pollution Certificate</h3>
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Pollution Certificate Number"
            name="pollutionCertificateNumber"
            value={formik.values.pollutionCertificateNumber}
            onChange={formik.handleChange}
          />
          <DatePicker
            label="Pollution Expiry Date"
            name="pollutionExpiryDate"
            value={formik.values.pollutionExpiryDate}
            onChange={(name, value) => formik.setFieldValue(name, value)}
          />
        </div>
        <div className="mt-4">
          <FileUpload
            label="Upload Pollution Certificate"
            acceptedTypes={[".pdf", ".jpg", ".png"]}
            fileList={formik.values.pollutionDocumentFiles}
            onChange={(info) =>
              formik.setFieldValue("pollutionDocumentFiles", info.fileList)
            }
            onPreview={handlePreview}
            
            multiple={false}
            showDownload={!!selectedId}
            onDownload={handleDownload}
          />
        </div>
      </div>

      {/* Road Tax Details */}
      <div className="border-b pb-4">
        <h3 className="text-lg font-semibold mb-3 text-gray-800">Road Tax Details</h3>
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Road Tax Receipt Number"
            name="roadTaxReceiptNumber"
            value={formik.values.roadTaxReceiptNumber}
            onChange={formik.handleChange}
          />
          <DatePicker
            label="Tax Valid Upto"
            name="taxValidUpto"
            value={formik.values.taxValidUpto}
            onChange={(name, value) => formik.setFieldValue(name, value)}
          />
        </div>
        <div className="mt-4">
          <FileUpload
            label="Upload Road Tax Receipt"
            acceptedTypes={[".pdf", ".jpg", ".png"]}
            fileList={formik.values.roadTaxDocumentFiles}
            onChange={(info) =>
              formik.setFieldValue("roadTaxDocumentFiles", info.fileList)
            }
            onPreview={handlePreview}
            
            multiple={false}
            showDownload={!!selectedId}
            onDownload={handleDownload}
          />
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex justify-end gap-3 pt-4">
        <Button
          type="button"
          variant="outline"
          onClick={handleCancel}
          disabled={isUploading}
        >
          Cancel
        </Button>
        <Button
          type="submit"
          onClick={formik.handleSubmit}
          loading={isUploading}
        >
          {selectedId ? "Update Vehicle" : "Add Vehicle"}
        </Button>
      </div>
    </form>
  );
}