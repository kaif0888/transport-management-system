"use client";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { Checkbox } from "@/Components/atom/Checkbox";
import { useMemo } from "react";
import { filterDriverValidationSchema } from "@/Data/constant";

export default function DriverFilter({
  onApplyFilter,
  onCancel,
  initialFilters = {},
}) {
  const formik = useFormik({
    initialValues: {
      licenseNumber: initialFilters.licenseNumber || "",
      contactNumber: initialFilters.contactNumber || "",
      unassignedOnly: initialFilters.unassignedOnly || false,
    },
    validationSchema: filterDriverValidationSchema,
    onSubmit: (values) => {
      const filters = [];

      if (values.licenseNumber && values.licenseNumber.trim() !== "") {
        filters.push({
          attribute: "licenseNumber",
          operation: "CONTAINS",
          value: values.licenseNumber.trim(),
        });
      }

      if (values.contactNumber && values.contactNumber.trim() !== "") {
        filters.push({
          attribute: "contactNumber",
          operation: "CONTAINS",
          value: values.contactNumber.trim(),
        });
      }

      if (values.unassignedOnly) {
        filters.push({
          attribute: "assignedVehicleId",
          operation: "EQUALS",
          value: null,
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
      formik.values.licenseNumber !== (initialFilters.licenseNumber || "") ||
      formik.values.contactNumber !== (initialFilters.contactNumber || "") ||
      formik.values.unassignedOnly !== (initialFilters.unassignedOnly || false)
    );
  }, [formik.values, initialFilters]);

  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          id="licenseNumber"
          name="licenseNumber"
          label="License Number"
          placeholder="Enter license number"
          value={formik.values.licenseNumber}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.errors.licenseNumber}
          touched={formik.touched.licenseNumber}
          className="w-full"
        />

        <Input
          id="contactNumber"
          name="contactNumber"
          label="Contact Number"
          placeholder="Enter contact number"
          value={formik.values.contactNumber}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.errors.contactNumber}
          touched={formik.touched.contactNumber}
          className="w-full"
        />

        <div className="flex flex-col md:col-span-2">
          <label className="mb-2 text-sm font-medium text-gray-700">
            Vehicle Assignment
          </label>
          <div className="mt-2">
            <Checkbox
              id="unassignedOnly"
              name="unassignedOnly"
              checked={formik.values.unassignedOnly}
              onChange={(e) =>
                formik.setFieldValue("unassignedOnly", e.target.checked)
              }
            >
              Show only unassigned drivers
            </Checkbox>
          </div>
        </div>
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
