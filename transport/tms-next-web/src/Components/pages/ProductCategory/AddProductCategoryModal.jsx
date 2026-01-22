"use client";

import { Input } from "@/Components/atom/Input";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { productCategoryValidationSchema } from "@/Data/constant";
import {
  AddProductCategory,
  UpdateProductCategory,
  getProductCategoryById,
} from "@/service/productCategory";
import { useQuery } from "@tanstack/react-query";
import { useEffect } from "react";
import { toast } from "react-toastify";

export default function AddProductCategoryModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {
  const { data: categoryData, isLoading } = useQuery({
    queryKey: ["categoryDetail", selectedId],
    queryFn: () => (selectedId ? getProductCategoryById(selectedId) : null),
    enabled: !!selectedId,
  });

  const initialValues = {
    categoryName: "",
  };

  const formik = useFormik({
    initialValues,
    validationSchema: productCategoryValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true, // This will reinitialize form when initialValues change
    onSubmit: async (values, { resetForm }) => {
      try {
        if (selectedId === null) {
          await AddProductCategory(values);
          toast.success("Product category added successfully");
        } else {
          await UpdateProductCategory(selectedId, values);
          toast.success("Product category updated successfully");
        }

        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save category");
      }
    },
  });

  useEffect(() => {
    if (categoryData) {
      formik.setValues({
        categoryName: categoryData.categoryName || "",
        description: categoryData.description || "",
      });
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [categoryData, selectedId]);

  const handleModalClose = () => {
    formik.resetForm();
    handleCancel();
  };

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading category data...</div>;
  }

  return (
    <>
      <form className="space-y-4">
        <Input
          label="Product category Name"
          id="categoryName"
          name="categoryName"
          required
          type="text"
          placeholder="Enter category name"
          value={formik.values.categoryName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.categoryName && formik.errors.categoryName}
          touched={formik.touched.categoryName}
        />
        <Input
          label="Description"
          id="description"
          name="description"
          required
          type="text"
          placeholder="Enter Description Name"
          value={formik.values.description}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.description && formik.errors.description}
          touched={formik.touched.description}
          />
        <div className="flex gap-4 justify-end pt-4">
          <Button onClick={handleModalClose}>Cancel</Button>
          <Button
            onClick={formik.handleSubmit}
            disabled={!formik.isValid || formik.isSubmitting}
            className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]! "
          >
            {formik.isSubmitting ? "Saving..." : "Save"}
          </Button>
        </div>
      </form>
    </>
  );
}
