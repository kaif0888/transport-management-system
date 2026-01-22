"use client";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { Select } from "@/Components/atom/Select";
import { useMemo } from "react";
import { filterDispatchValidationSchema } from "@/Data/constant";
import { trackingStatusOptions } from "@/Data/impData";

export default function DispatchTrackingFilter({
  onApplyFilter,
  onCancel,
  initialFilters = {},
}) {
  const formik = useFormik({
    initialValues: {
      status: initialFilters.status || "",
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
    return formik.values.status !== (initialFilters.status || "");
  }, [formik.values, initialFilters]);

  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-1 gap-4">
      <Select
          id="status"
          name="status"
          label="Status"
          placeholder="Select status"
          fieldWidth="100%"
          value={formik.values.status}
          onChange={(value) => {
            formik.setFieldValue("status", value);
          }}
          onBlur={() => formik.setFieldTouched("status", true)}
          error={formik.errors.status}
          touched={formik.touched.status}
          required={true}
          options={trackingStatusOptions}
          noOptionMsg="No payment status available."
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