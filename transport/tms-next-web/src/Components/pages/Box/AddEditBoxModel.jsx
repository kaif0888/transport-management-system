"use client";
import { Input } from "@/Components/atom/Input";
import { Select } from "@/Components/atom/Select";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import * as Yup from "yup";
import { AddBox, UpdateBox, getBoxById } from "@/service/box";
import { getAllHSNCodes, searchHSNCodes } from "@/service/box";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useMemo, useState } from "react";
import { toast } from "react-toastify";

const boxValidationSchema = Yup.object().shape({
  boxName: Yup.string()
    .required("Box name is required")
    .min(2, "Box name must be at least 2 characters"),
  boxCode: Yup.string()
    .required("Box code is required")
    .min(2, "Box code must be at least 2 characters"),
  hsnCode: Yup.string().required("HSN code is required"),
  description: Yup.string(),
  maxWeight: Yup.number()
    .positive("Weight must be positive")
    .required("Max weight is required")
    .min(0.1, "Weight must be greater than 0"),
  length: Yup.number()
    .positive("Length must be positive")
    .min(0.1, "Length must be greater than 0"),
  width: Yup.number()
    .positive("Width must be positive")
    .min(0.1, "Width must be greater than 0"),
  height: Yup.number()
    .positive("Height must be positive")
    .min(0.1, "Height must be greater than 0"),
});

export default function AddBoxModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
  onBoxCreated, // New callback prop
}) {
  const [hsnSearchQuery, setHsnSearchQuery] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { data: boxData, isLoading } = useQuery({
    queryKey: ["boxDetail", selectedId],
    queryFn: () => getBoxById(selectedId),
    enabled: !!selectedId,
  });

  const { data: hsnCodeData, isLoading: hsnLoading } = useQuery({
    queryKey: ["hsnCodeData"],
    queryFn: getAllHSNCodes,
  });

  const { data: hsnSearchResults } = useQuery({
    queryKey: ["hsnSearch", hsnSearchQuery],
    queryFn: () => searchHSNCodes(hsnSearchQuery),
    enabled: hsnSearchQuery.length >= 2,
  });

  const hsnOptions = useMemo(() => {
    const data = hsnSearchQuery.length >= 2 ? hsnSearchResults : hsnCodeData;
    return (
      data?.map((item) => ({
        label: `${item.hsnCode} - ${item.description}`,
        value: item.hsnCode,
        data: item,
      })) || []
    );
  }, [hsnCodeData, hsnSearchResults, hsnSearchQuery]);

  const formik = useFormik({
    initialValues: {
      boxName: "",
      boxCode: "",
      hsnCode: "",
      description: "",
      maxWeight: "",
      length: "",
      width: "",
      height: "",
    },
    validationSchema: boxValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      if (isSubmitting) return;
      
      setIsSubmitting(true);
      console.log("📝 Form submission started with values:", values);
      
      try {
        // Validate all required fields
        if (!values.boxName?.trim()) {
          throw new Error("Box name is required");
        }
        if (!values.boxCode?.trim()) {
          throw new Error("Box code is required");
        }
        if (!values.hsnCode?.trim()) {
          throw new Error("HSN code is required");
        }
        if (!values.maxWeight || parseFloat(values.maxWeight) <= 0) {
          throw new Error("Valid max weight is required");
        }

        const payload = {
          boxName: values.boxName.trim(),
          boxCode: values.boxCode.trim(),
          hsnCode: values.hsnCode.trim(),
          description: values.description?.trim() || "",
          maxWeight: parseFloat(values.maxWeight),
          dimensions: {
            length: parseFloat(values.length) || 0,
            width: parseFloat(values.width) || 0,
            height: parseFloat(values.height) || 0,
          },
        };

        console.log("📦 Submitting payload:", payload);

        let result;
        if (selectedId) {
          result = await UpdateBox(selectedId, payload);
          toast.success("Box updated successfully");
          console.log("✅ Box updated:", result);
        } else {
          result = await AddBox(payload);
          toast.success("Box created successfully");
          console.log("✅ Box created:", result);
        }
        
        console.log("🔄 Refreshing list");
        await ListDataRefetch();
        resetForm();
        
        // If this is a new box and callback is provided, call it with the new box ID
        if (!selectedId && onBoxCreated && result?.boxId) {
          console.log("📞 Calling onBoxCreated callback with:", result.boxId);
          onBoxCreated(result.boxId);
        } else {
          // If update or no callback, just close the modal
          handleCancel();
        }
      } catch (error) {
        console.error(" Error saving box:", error);
        const errorMessage = typeof error === 'string' ? error : error.message || "Failed to save box";
        toast.error(errorMessage);
      } finally {
        setIsSubmitting(false);
      }
    },
  });

  useEffect(() => {
    if (boxData) {
      console.log("📦 Loading box data for edit:", boxData);
      formik.setValues({
        boxName: boxData.boxName || "",
        boxCode: boxData.boxCode || "",
        hsnCode: boxData.hsnCode || "",
        description: boxData.description || "",
        maxWeight: boxData.maxWeight || "",
        length: boxData.dimensions?.length || "",
        width: boxData.dimensions?.width || "",
        height: boxData.dimensions?.height || "",
      });
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [boxData, selectedId]);

  const selectedHsnCode = useMemo(
    () => hsnOptions.find((option) => option.value === formik.values.hsnCode)?.data,
    [formik.values.hsnCode, hsnOptions]
  );

  const handleHsnChange = (value) => {
    console.log("📋 HSN Code selected:", value);
    formik.setFieldValue("hsnCode", value);
  };

  const handleModalClose = () => {
    formik.resetForm();
    handleCancel();
  };

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading box data...</div>;
  }

  return (
    <form className="space-y-4" onSubmit={(e) => {
      e.preventDefault();
      console.log("🚀 Form submit triggered");
      formik.handleSubmit();
    }}>
      <div className="grid grid-cols-2 gap-x-4 gap-y-2">
        <Input
          label="Box Name"
          id="boxName"
          name="boxName"
          required
          type="text"
          placeholder="Enter box name"
          value={formik.values.boxName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.boxName && formik.errors.boxName}
          touched={formik.touched.boxName}
        />
        <Input
          label="Box Code"
          id="boxCode"
          name="boxCode"
          required
          type="text"
          placeholder="Enter box code"
          value={formik.values.boxCode}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.boxCode && formik.errors.boxCode}
          touched={formik.touched.boxCode}
        />

        <div className="col-span-2">
          <Select
            id="hsnCode"
            name="hsnCode"
            label="HSN Code"
            placeholder="Search and select HSN code"
            fieldWidth="100%"
            value={formik.values.hsnCode}
            onChange={handleHsnChange}
            onBlur={() => formik.setFieldTouched("hsnCode", true)}
            error={formik.errors.hsnCode}
            touched={formik.touched.hsnCode}
            required
            loading={hsnLoading}
            options={hsnOptions}
            labelRender={
              selectedHsnCode
                ? `${selectedHsnCode.hsnCode} - ${selectedHsnCode.description}`
                : null
            }
            showSearch
            filterOption={false}
            onSearch={(value) => setHsnSearchQuery(value)}
            noOptionMsg="No HSN code available. Type to search."
            disabled={hsnLoading}
            notFoundContent={hsnLoading ? "Loading HSN codes..." : "Type to search HSN codes"}
          />
          {selectedHsnCode && (
            <div className="mt-2 p-2 bg-gray-50 rounded text-sm">
              <p className="text-gray-600">
                <strong>Category:</strong> {selectedHsnCode.category || "N/A"}
              </p>
              <p className="text-gray-600">
                <strong>GST Rate:</strong> {selectedHsnCode.gstRate || "N/A"}%
              </p>
            </div>
          )}
        </div>

        <Input
          label="Max Weight (Kg)"
          id="maxWeight"
          name="maxWeight"
          required
          type="number"
          step="0.01"
          min="0.01"
          placeholder="Enter max weight"
          value={formik.values.maxWeight}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.maxWeight && formik.errors.maxWeight}
          touched={formik.touched.maxWeight}
        />

        <div className="col-span-2">
          <label className="block text-sm font-medium mb-2">
            Length (cm) <span className="text-red-500">*</span>
          </label>
          <div className="grid grid-cols-3 gap-2">
            <Input
              id="length"
              name="length"
              type="number"
              step="0.01"
              min="0.01"
              placeholder="Length"
              value={formik.values.length}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.length && formik.errors.length}
              touched={formik.touched.length}
            />
            <Input
              id="width"
              name="width"
              type="number"
              step="0.01"
              min="0.01"
              placeholder="Width"
              value={formik.values.width}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.width && formik.errors.width}
              touched={formik.touched.width}
            />
            <Input
              id="height"
              name="height"
              type="number"
              step="0.01"
              min="0.01"
              placeholder="Height"
              value={formik.values.height}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.height && formik.errors.height}
              touched={formik.touched.height}
            />
          </div>
        </div>

        <div className="col-span-2">
          <Input
            label="Description"
            id="description"
            name="description"
            type="textarea"   
            placeholder="Enter box description"
            value={formik.values.description}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.description && formik.errors.description}
            touched={formik.touched.description}
          />
        </div>
      </div>

      <div className="flex gap-4 justify-end pt-4 w-full">
        <Button onClick={handleModalClose} type="button">Cancel</Button>
        {/* <Button
          type="submit"
          disabled={!formik.isValid || isSubmitting}
          className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]!"
        >
          {isSubmitting ? "Saving..." : selectedId ? "Update" : "Save"}
        </Button> */}
        <Button
  onClick={formik.handleSubmit}   // ✅ FORCE SUBMIT
  disabled={!formik.isValid || isSubmitting}
  className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white!"
>
  {isSubmitting ? "Saving..." : selectedId ? "Update" : "Save"}
</Button>

      </div>
    </form>
  );
}