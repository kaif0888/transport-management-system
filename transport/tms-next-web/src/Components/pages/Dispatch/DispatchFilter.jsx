"use client";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { Select } from "@/Components/atom/Select";
import { useMemo } from "react";
import { filterDispatchValidationSchema } from "@/Data/constant";

export default function DispatchFilter({
  onApplyFilter,
  onCancel,
  initialFilters = {},
}) {
  const formik = useFormik({
    initialValues: {
      status: initialFilters.status || "",
      dispatchType: initialFilters.dispatchType || null,
    },
    validationSchema: filterDispatchValidationSchema,
    onSubmit: (values) => {
      const filters = [];

      if (values.status && values.status.trim() !== "") {
        filters.push({
          attribute: "status",
          operation: "CONTAINS",
          value: values.status.trim(),
        });
      }

      if (values.dispatchType) {
        filters.push({
          attribute: "dispatchType",
          operation: "EQUALS",
          value: values.dispatchType,
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
    return (
      formik.values.status !== (initialFilters.status || "") ||
      formik.values.dispatchType !== (initialFilters.dispatchType || null) 
    );
  }, [formik.values, initialFilters]);

  const statusOptions = [
    { label: "Pending", value: "PENDING" },
    { label: "In Progress", value: "IN_PROGRESS" },
    { label: "Completed", value: "COMPLETED" },
    { label: "Cancelled", value: "CANCELLED" }
  ];

  const dispatchTypeMap = {
    STANDARD: "Standard",
    EXPRESS: "Express",
    SAME_DAY: "Same Day",
    BULK: "Bulk",
    RETURN: "Return"
  };

  const dispatchTypeOptions = Object.entries(dispatchTypeMap).map(([key, value]) => ({
    label: value,
    value: key
  }));

  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          id="status"
          name="status"
          label="Status"
          placeholder="Enter status"
          value={formik.values.status}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.status && formik.errors.status}
          touched={formik.touched.status}
          fieldWidth="100%"
        />

        <Select
          id="dispatchType"
          name="dispatchType"
          label="Dispatch Type"
          placeholder="Select dispatch type"
          value={formik.values.dispatchType}
          onChange={(value) => formik.setFieldValue("dispatchType", value)}
          onBlur={() => formik.setFieldTouched("dispatchType", true)}
          options={dispatchTypeOptions}
          allowClear
          fieldWidth="100%"
          error={formik.touched.dispatchType && formik.errors.dispatchType}
          touched={formik.touched.dispatchType}
          noOptionMsg="No dispatch types available"
        />
      </div>

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