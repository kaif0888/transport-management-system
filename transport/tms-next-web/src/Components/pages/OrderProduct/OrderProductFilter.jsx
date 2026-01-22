"use client";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { Select } from "@/Components/atom/Select";
import { useMemo, useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { getAllProduct } from "@/service/product";

export default function OrderProductFilter({
  onApplyFilter,
  onCancel,
  initialFilters = {},
}) {
  const [productOptions, setProductOptions] = useState([]);

  const { data: productData, isLoading: productLoading } = useQuery({
    queryKey: ["productData"],
    queryFn: () => getAllProduct({ limit: 0, filters: [] }),
  });

  useEffect(() => {
    if (productData) {
      const options = productData.map((item) => ({
        label: item.productName,
        value: item.productName,
      }));
      setProductOptions(options);
    }
  }, [productData]);
  const formik = useFormik({
    initialValues: {
      productName: initialFilters.productName || null,
    },
    onSubmit: (values) => {
      const filters = [];
      if (values.productName) {
        filters.push({
          attribute: "productName",
          operation: "EQUALS",
          value: values.productName,
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

  const hasChanges = useMemo(() => {
    return formik.values.productName !== (initialFilters.productName || null);
  }, [formik.values, initialFilters]);

  return (
    <div className="flex flex-col gap-4">
      <Select
        id="productName"
        name="productName"
        label="Box Name"
        placeholder="Select product"
        value={formik.values.productName}
        fieldWidth="100%"
        onChange={(value) => formik.setFieldValue("productName", value)}
        options={productOptions}
        loading={productLoading}
        disabled={productLoading}
        notFoundContent={
          productLoading ? "Loading..." : "No products found"
        }
      />

      <div className="flex justify-end gap-2 mt-4">
        <Button onClick={handleResetFilter} disabled={!hasChanges}>
          Reset
        </Button>
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