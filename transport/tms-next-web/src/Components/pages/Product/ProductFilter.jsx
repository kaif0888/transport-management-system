"use client";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { RangeSlider } from "@/Components/atom/RangeSlider";
import { useFormik } from "formik";

export default function ProductFilter({
  onApplyFilter,
  onCancel,
  initialFilters = {},
}) {
  const formik = useFormik({
    initialValues: {
      categoryName: initialFilters.categoryName || "",
      price: initialFilters.price || [0, 150000],
    },
    onSubmit: (values) => {
      const filters = [];

      if (values.categoryName && values.categoryName.trim()) {
        filters.push({
          attribute: "categoryName",
          operation: "CONTAINS",
          value: values.categoryName.trim(),
        });
      }

      if (
        values.price &&
        Array.isArray(values.price) &&
        values.price.length === 2
      ) {
        filters.push({
          attribute: "price",
          operation: "GREATER_THAN_OR_EQUAL",
          value: values.price[0].toString(),
        });

        filters.push({
          attribute: "price",
          operation: "LESS_THAN_OR_EQUAL",
          value: values.price[1].toString(),
        });
      }

      const filterPayload = {
        limit: 0,
        filters: filters,
      };

      onApplyFilter(filterPayload);
    },
  });

  const handleResetFilter = () => {
    formik.resetForm();
  };

  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-1 gap-4 ">
        <Input
          id="categoryName"
          name="categoryName"
          label="Category Name"
          placeholder="Enter category name"
          type="text"
          value={formik.values.categoryName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.errors.categoryName}
          touched={formik.touched.categoryName}
        />

        <RangeSlider
          label="Price Range"
          id="price"
          name="price"
          min={0}
          max={150000}
          step={10000}
          unit="₹"
          value={formik.values.price}
          onChange={(value) => formik.setFieldValue("price", value)}
          onBlur={formik.handleBlur}
          error={formik.errors.price}
          touched={formik.touched.price}
        />
      </div>

      <div className="flex justify-end gap-2 mt-4">
        <Button onClick={handleResetFilter}>Reset</Button>
        <Button onClick={onCancel}>Cancel</Button>
        <Button
          type="primary"
          className="bg-[#750014] text-white border-none hover:bg-[#750014]"
          onClick={formik.handleSubmit}
        >
          Apply Filter
        </Button>
      </div>
    </div>
  );
}