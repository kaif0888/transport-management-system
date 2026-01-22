"use client";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { filterBranchValidationSchema } from "@/Data/constant";
import { useFormik } from "formik";

export default function BranchFilter({
  onApplyFilter,
  onCancel,
  initialFilters = {},
}) {
  const formik = useFormik({
    initialValues: {
      locationName: initialFilters.locationName || "",
      locationAddress: initialFilters.locationAddress || "",
    },
    validationSchema: filterBranchValidationSchema,
    onSubmit: (values) => {
      const filters = [];

      if (values.locationName && values.locationName.trim()) {
        filters.push({
          attribute: "locationName",
          operation: "CONTAINS",
          value: values.locationName.trim(),
        });
      }

      if (values.locationAddress && values.locationAddress.trim()) {
        filters.push({
          attribute: "locationAddress",
          operation: "CONTAINS",
          value: values.locationAddress.trim(),
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
      <div className="grid grid-cols-2 gap-4">
        <Input
          id="locationName"
          name="locationName"
          label="Location Name"
          placeholder="Enter location name"
          type="text"
          value={formik.values.locationName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.errors.locationName}
          touched={formik.touched.locationName}
        />

        <Input
          id="locationAddress"
          name="locationAddress"
          label="Location Address"
          placeholder="Enter location address"
          type="text"
          value={formik.values.locationAddress}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.errors.locationAddress}
          touched={formik.touched.locationAddress}
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