"use client";
import { useFormik } from "formik";
import { Input } from "@/Components/atom/Input";
import { Button } from "@/Components/atom/Button";
import { AddDriver, UpdateDriver, getDriverById } from "@/service/driver";
import { uploadDocument } from "@/service/document";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { driverValidationSchema } from "@/Data/constant";
import { DatePicker } from "@/Components/atom/DatePicker";
import { toast } from "react-toastify";
import { FileUpload } from "@/Components/atom/UploadFile";

export default function AddDriverModal({
  handleCancel,
  driverListDataRefetch,
  selectedId,
}) {
  const [isUploading, setIsUploading] = useState(false);

  const {
    data: driverData,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["driverDetail", selectedId],
    queryFn: () => getDriverById(selectedId),
    enabled: !!selectedId,
  });

  useEffect(() => {
    refetch();
  }, []);

  const uploadFilesSequentially = async (files) => {
    const uploadedIds = [];

    for (const file of files) {
      const formData = new FormData();
      formData.append("file", file.originFileObj || file);
      formData.append(
        "documentName",
        `driver-document-${Date.now()}-${Math.random()}`
      );
      formData.append("documentStatus", "active");

      const response = await uploadDocument(formData);
      uploadedIds.push(response.documentId);
    }

    return uploadedIds;
  };

  const handleDownload = (file) => {
    if (!file.url) {
      toast.error("File URL not available");
      return;
    }

    const link = document.createElement("a");
    link.href = file.url;
    link.download = file.name || "driver-document";
    link.target = "_blank";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    toast.success("Download started");
  };

  const formik = useFormik({
    initialValues: {
      name: "",
      licenseNumber: "",
      licenseExpiry: "",
      contactNumber: "",
      documentIds: [],
    },
    enableReinitialize: true,
    validationSchema: driverValidationSchema,
    onSubmit: async (values, { resetForm }) => {
      try {
        setIsUploading(true);

        const payload = {
          name: values.name,
          licenseNumber: values.licenseNumber,
          licenseExpiry: values.licenseExpiry,
          contactNumber: values.contactNumber,
        };

        if (values.documentIds?.length > 0) {
          const newFiles = values.documentIds.filter(
            (file) =>
              file.originFileObj ||
              (file.uid && !file.uid.startsWith("existing-"))
          );

          const existingFiles = values.documentIds.filter((file) =>
            file.uid?.startsWith("existing-")
          );

          let allDocumentIds = existingFiles.map(
            (file) => file.documentId || file.uid.replace("existing-", "")
          );

          if (newFiles.length > 0) {
            const newDocumentIds = await uploadFilesSequentially(newFiles);
            allDocumentIds = [...allDocumentIds, ...newDocumentIds];
          }

          payload.documentIds = allDocumentIds;
        }

        if (selectedId) {
          await UpdateDriver(selectedId, payload);
          toast.success("Driver updated successfully");
        } else {
          await AddDriver(payload);
          toast.success("Driver added successfully");
        }

        await driverListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        toast.error(error.message || "Failed to save driver");
      } finally {
        setIsUploading(false);
      }
    },
  });

  useEffect(() => {
    if (!driverData) return;

    const existingFiles =
      driverData.documentIds?.map((doc, index) => ({
        uid: `existing-${doc.documentId || index}`,
        name:
          doc.fileName ||
          doc.fileUrl?.split("/").pop() ||
          `document-${index + 1}`,
        status: "done",
        url: doc.fileUrl,
        documentId: doc.documentId,
      })) || [];

    formik.setValues({
      name: driverData.name || "",
      licenseNumber: driverData.licenseNumber || "",
      licenseExpiry: driverData.licenseExpiry
        ? new Date(driverData.licenseExpiry)
        : "",
      contactNumber: driverData.contactNumber || "",
      documentIds: existingFiles,
    });
  }, [driverData]);

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading Driver data...</div>;
  }

  return (
    <form className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-x-4">
        <Input
          label="Name"
          id="name"
          required
          name="name"
          type="text"
          placeholder="Enter name"
          value={formik.values.name}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.name && formik.errors.name}
          touched={formik.touched.name}
        />

        <Input
          label="License Number"
          id="licenseNumber"
          required
          name="licenseNumber"
          type="text"
          placeholder="Enter licence number"
          value={formik.values.licenseNumber}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.licenseNumber && formik.errors.licenseNumber}
          touched={formik.touched.licenseNumber}
        />

        <DatePicker
          label="License Expiry Date"
          id="licenseExpiry"
          name="licenseExpiry"
          placeholder="Select expiry date"
          value={formik.values.licenseExpiry}
          onChange={formik.setFieldValue}
          onBlur={formik.handleBlur}
          error={formik.errors.licenseExpiry}
          touched={formik.touched.licenseExpiry}
          required={true}
        />

        <Input
          label="Contact Number"
          id="contactNumber"
          required
          name="contactNumber"
          type="text"
          placeholder="Enter contact number"
          value={formik.values.contactNumber}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.contactNumber && formik.errors.contactNumber}
          touched={formik.touched.contactNumber}
        />
      </div>

      <div className="w-full">
        <FileUpload
          label="Upload Documents"
          acceptedTypes={[".pdf", ".jpg", ".png", ".doc", ".docx"]}
          fileList={formik.values.documentIds}
          onChange={(info) =>
            formik.setFieldValue("documentIds", info.fileList)
          }
          error={formik.errors.documentIds}
          touched={formik.touched.documentIds}
          required
          multiple={true}
          showDownload={!!selectedId}
          onDownload={handleDownload}
        />
      </div>

      <div className="flex gap-4 justify-end pt-4">
        <Button
          onClick={() => {
            formik.resetForm();
            handleCancel();
          }}
          disabled={isUploading}
        >
          Cancel
        </Button>
        <Button
          onClick={formik.handleSubmit}
          disabled={!formik.isValid || formik.isSubmitting || isUploading}
          className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]!"
        >
          {isUploading
            ? "Uploading..."
            : formik.isSubmitting
            ? "Saving..."
            : "Save"}
        </Button>
      </div>
    </form>
  );
}
