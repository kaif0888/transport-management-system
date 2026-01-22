"use client";

import { useFormik } from "formik";
import { Input } from "@/Components/atom/Input";
import { Button } from "@/Components/atom/Button";
import {
  CreateExpense,
  UpdateExpense,
  getExpenseById,
  getAllExpenseType,
} from "@/service/expense";
import { getVehicleByNumber } from "@/service/vehicle";
import { uploadDocument } from "@/service/document";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { FileUpload } from "@/Components/atom/UploadFile";

export default function AddExpense({
  handleCancel,
  expenseListRefetch,
  selectedId,
}) {
  const [vehicleNumber, setVehicleNumber] = useState("");
  const [isUploading, setIsUploading] = useState(false);

  // Fetch expense data for edit
  const { data: expenseData, isLoading } = useQuery({
    queryKey: ["expenseDetail", selectedId],
    queryFn: () => (selectedId ? getExpenseById(selectedId) : null),
    enabled: !!selectedId,
  });

  // Fetch all expense types
  const { data: expenseTypes = [] } = useQuery({
    queryKey: ["expenseTypes"],
    queryFn: () => getAllExpenseType(),
  });

  const uploadFilesSequentially = async (files) => {
    const uploadedIds = [];

    for (const file of files) {
      const formData = new FormData();
      formData.append("file", file.originFileObj || file);
      formData.append(
        "documentName",
        `expense-document-${Date.now()}-${Math.random()}`
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
    link.download = file.name || "expense-document";
    link.target = "_blank";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    toast.success("Download started");
  };

  const initialValues = {
    expenseId: "",
    vehicleId: "",
    expenseTypeId: "",
    amount: 0,
    vehiclNumber: "",
    date: new Date().toISOString().split("T")[0],
    description: "",
    documentIds: [], // ✅ NEW
  };

  const formik = useFormik({
    initialValues,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        setIsUploading(true);

        // ✅ payload (documentIds will be added after upload)
        const payload = {
          ...values,
        };

        // ✅ Upload docs (same as Driver)
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

        // ✅ Create or Update
        if (!selectedId) {
          await CreateExpense(payload);
          toast.success("Expense added successfully");
        } else {
          await UpdateExpense(selectedId, payload);
          toast.success("Expense updated successfully");
        }

        if (typeof expenseListRefetch === "function") {
          await expenseListRefetch();
        }

        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error:", error);
        toast.error(error.message || "Failed to save expense");
      } finally {
        setIsUploading(false);
      }
    },
  });

  // Populate form values in edit mode
  useEffect(() => {
    if (expenseData) {
      // ✅ if backend returns documents like driverData.documentIds
      const existingFiles =
        expenseData.documentIds?.map((doc, index) => ({
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
        ...initialValues,
        ...expenseData,
        date: expenseData.date || initialValues.date,
        documentIds: existingFiles, // ✅ NEW
      });

      if (expenseData.vehiclNumber) {
        setVehicleNumber(expenseData.vehiclNumber);
      }
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [expenseData, selectedId]);

  // Handle vehicle number blur to fetch vehicleId
  const handleVehicleNumberBlur = async () => {
    if (vehicleNumber.trim()) {
      try {
        const vehicle = await getVehicleByNumber(vehicleNumber.trim());
        if (vehicle?.vehicleId) {
          formik.setFieldValue("vehicleId", vehicle.vehicleId);
          toast.success("Vehicle found");
        } else {
          toast.error("Vehicle not found");
        }
      } catch (error) {
        toast.error("Failed to fetch vehicle");
      }
    }
  };

  const handleModalClose = () => {
    formik.resetForm();
    handleCancel();
  };

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading expense data...</div>;
  }

  return (
    <form className="space-y-4">
      {/* Vehicle Number */}
      <Input
        label="Vehicle Number"
        id="vehicleNumber"
        name="vehicleNumber"
        type="text"
        placeholder="Enter vehicle number"
        value={vehicleNumber}
        onChange={(e) => setVehicleNumber(e.target.value)}
        onBlur={handleVehicleNumberBlur}
      />

      {/* Expense Type Dropdown */}
      <div className="flex flex-col">
        <label htmlFor="expenseTypeId" className="mb-1 font-medium">
          Expense Type <span className="text-red-500">*</span>
        </label>

        <select
          id="expenseTypeId"
          name="expenseTypeId"
          className="border rounded px-3 py-2"
          value={formik.values.expenseTypeId}
          onChange={formik.handleChange}
          required
        >
          <option value="">Select Expense Type</option>
          {expenseTypes.map((type) => (
            <option key={type.expenseTypeId} value={type.expenseTypeId}>
              {type.expenseTypeName}
            </option>
          ))}
        </select>
      </div>

      <Input
        label="Amount"
        id="amount"
        name="amount"
        type="number"
        required
        placeholder="Enter amount"
        value={formik.values.amount}
        onChange={formik.handleChange}
      />

      <Input
        label="Date"
        id="date"
        name="date"
        type="date"
        required
        value={formik.values.date}
        onChange={formik.handleChange}
      />

      <Input
        label="Description"
        id="description"
        name="description"
        type="text"
        placeholder="Enter description"
        value={formik.values.description}
        onChange={formik.handleChange}
      />

      {/* ✅ Upload Documents (NEW) */}
      <div className="w-full">
        <FileUpload
          label="Upload Documents"
          acceptedTypes={[".pdf", ".jpg", ".png", ".doc", ".docx"]}
          fileList={formik.values.documentIds}
          onChange={(info) => formik.setFieldValue("documentIds", info.fileList)}
          required={false}
          multiple={true}
          showDownload={!!selectedId}
          onDownload={handleDownload}
        />
      </div>

      <div className="flex gap-4 justify-end pt-4">
        <Button onClick={handleModalClose} disabled={isUploading}>
          Cancel
        </Button>

        <Button
          onClick={formik.handleSubmit}
          disabled={!formik.isValid || formik.isSubmitting || isUploading}
          className="bg-[#750014]! disabled:opacity-50! text-white! border-none! hover:bg-[#750014]!"
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
