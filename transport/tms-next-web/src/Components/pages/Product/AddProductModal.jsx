"use client";
import { Input } from "@/Components/atom/Input";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { AddProduct, UpdateProduct, getProductById } from "@/service/product";
import { useQuery } from "@tanstack/react-query";
import { useEffect } from "react";
import { toast } from "react-toastify";
import { getAllHsnCodes } from "@/service/product";
import { Select } from "antd";
 

export default function AddProductModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {
  const { data: productData, isLoading } = useQuery({
    queryKey: ["productDetail", selectedId],
    queryFn: () => getProductById(selectedId),
    enabled: !!selectedId,
  });

  const formik = useFormik({
    initialValues: {
      productId: "",
      branchIds: "",
      boxCode: "",
      boxName: "",
      hsnCode: "",
      Weight: "",
      height: "",
      width: "",
      length: "",
      totalValue: "",
      status: "",
      storageCondition: "",
    },
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        if (selectedId) {
          await UpdateProduct(selectedId, values);
          console.log(values + "updating");
          toast.success("Consignment Box updated successfully");
        } else {
          await AddProduct(values);
          console.log(values);
          toast.success("Consignment Box added successfully");
        }
        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        toast.error(error.message || "Failed to save Consignment Box");
      }
    },
  });

  useEffect(() => {
    if (productData) {
      formik.setValues({
        productId: productData.productId || "",
        branchIds: productData.branchIds || "",
        boxCode: productData.boxCode || "",
        boxName: productData.boxName || "",
        hsnCode: productData.hsnCode || "",
       storageCondition: productData.storageCondition || "",
        maxWeight: productData.maxWeight || "",
        actualWeight: productData.actualWeight || "",
        dimensions: productData.dimensions || "",
        totalValue: productData.totalValue || "",
        status: productData.status || "",
      });
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [productData, selectedId]);

  const handleModalClose = () => {
    formik.resetForm();
    handleCancel();
  };

  

const storageConditionOptions = [
  {
    label: "Dry",
    options: [
      { label: "Dry Goods", value: "DRY_GOODS" },
      { label: "Non-Perishable Items", value: "NON_PERISHABLE" },
      { label: "General Cargo", value: "GENERAL_CARGO" },
    ],
  },
  {
    label: "Cool",   // ✅ FIXED
    options: [
      { label: "Cool Storage (8°C – 15°C)", value: "COOL_STORAGE" },
      { label: "Fresh Produce", value: "FRESH_PRODUCE" },
      { label: "Dairy Products", value: "DAIRY_PRODUCTS" },
    ],
  },
  {
    label: "Temperature-Controlled",
    options: [
      { label: "Frozen (-18°C)", value: "FROZEN" },
      { label: "Pharmaceuticals", value: "PHARMA" },
      { label: "Vaccines", value: "VACCINES" },
      { label: "Chemicals (Temp Controlled)", value: "CHEMICALS_TEMP" },
    ],
  },
];

  const { data: hsnCodes = [], isLoading: hsnLoading } = useQuery({
  queryKey: ["hsnCodes"],
  queryFn: getAllHsnCodes,
});

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading Consignment Box data...</div>;
  }

  return (
    <form className="space-y-4">
      <div className="grid grid-cols-2 gap-x-4 gap-y-2">
        {/* <Input
          label="Consignment Box ID"
          id="productId"
          name="productId"
          type="text"
          placeholder="Enter Consignment Box ID"
          value={formik.values.productId}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.productId && formik.errors.productId}
          touched={formik.touched.productId}
        />
        <Input
          label="Branch IDs"
          id="branchIds"
          name="branchIds"
          type="text"
          placeholder="Enter Branch IDs"
          value={formik.values.branchIds}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.branchIds && formik.errors.branchIds}
          touched={formik.touched.branchIds}
        /> */}
        <Input
          label="Box Material"
          id="boxCode"
          name="boxCode"
          type="text"
          placeholder="Enter Consignment Box Material"
          value={formik.values.boxCode}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.boxCode && formik.errors.boxCode}
          touched={formik.touched.boxCode}
        />
        <Input
          label="Box Type"
          id="boxName"
          name="boxName"
          type="text"
          placeholder="Enter Consignment Box Type"
          value={formik.values.boxName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.boxName && formik.errors.boxName}
          touched={formik.touched.boxName}
        />
        {/* <Input
          label="HSN Code"
          id="hsnCode"
          name="hsnCode"
          type="text"
          placeholder="Enter HSN Code"
          value={formik.values.hsnCode}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.hsnCode && formik.errors.hsnCode}
          touched={formik.touched.hsnCode}
        /> */}

  <div className="flex flex-col gap-1">
  <label className="text-sm font-medium">Storage Condition</label>
<Select
  id="storageCondition"
  name="storageCondition"
  placeholder="Select Storage Type"
  value={formik.values.storageCondition || undefined}
  onChange={(value) => formik.setFieldValue("storageCondition", value)}
  onBlur={() => formik.setFieldTouched("storageCondition", true)}
  options={storageConditionOptions}
  style={{ width: "100%" }}
/>

{formik.touched.storageCondition && formik.errors.storageCondition && (
  <p className="text-red-500 text-sm mt-1">
    {formik.errors.storageCondition}
  </p>
)}

{formik.touched.storageCondition && formik.errors.storageCondition && (
  <p className="text-red-500 text-sm mt-1">
    {formik.errors.storageCondition}
  </p>
)}
</div>
        <Input
          label="Weight"
          id="Weight"
          name="Weight"
          type="number"
          placeholder="Enter Weight"
          value={formik.values.maxWeight}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.maxWeight && formik.errors.maxWeight}
          touched={formik.touched.maxWeight}
        />
 
        <Input
          label="height"
          id="height"
          name="height"
          type="text"
          placeholder="Enter height"
          value={formik.values.dimensions}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.dimensions && formik.errors.dimensions}
          touched={formik.touched.dimensions}
        />
          <Input
          label="width"
          id="width"
          name="width"
          type="text"
          placeholder="Enter width"
          value={formik.values.dimensions}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.dimensions && formik.errors.dimensions}
          touched={formik.touched.dimensions}
        />
          <Input
          label="length"
          id="length"
          name="length"
          type="text"
          placeholder="Enter length"
          value={formik.values.dimensions}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.dimensions && formik.errors.dimensions}
          touched={formik.touched.dimensions}
        />
        <Input
          label="Total Capacity Value"
          id="totalValue"
          name="totalValue"
          type="number"
          placeholder="Enter Capacity Value"
          value={formik.values.totalValue}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.totalValue && formik.errors.totalValue}
          touched={formik.touched.totalValue}
        />
        <Input
          label="Status"
          id="status"
          name="status"
          type="text"
          placeholder="Enter Status"
          value={formik.values.status}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.status && formik.errors.status}
          touched={formik.touched.status}
        />
      </div>
      <div className="flex gap-4 justify-end pt-4 w-full">
        <Button onClick={handleModalClose}>Cancel</Button>
        <Button
          onClick={formik.handleSubmit}
          disabled={!formik?.isValid || formik.isSubmitting}
          className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]!"
        >
          {formik.isSubmitting ? "Saving..." : "Save"}
        </Button>
      </div>
    </form>
  );
}
