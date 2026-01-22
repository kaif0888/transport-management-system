"use client";
import { Button } from "@/Components/atom/Button";
import { Select } from "@/Components/atom/Select";
import { RangeSlider } from "@/Components/atom/RangeSlider";
import { filterVehicleValidationSchema } from "@/Data/constant";
import { getAllVehicleCompany, getAllVehicleModels } from "@/service/vehicle";
import { vehicleStatus } from "@/Data/impData";
import { useQuery } from "@tanstack/react-query";
import { useFormik } from "formik";

export default function VehicleFilter({
  onApplyFilter,
  onCancel,
  initialFilters = {},
}) {
  const { data: vehicleModelData, isLoading: vehicleModelLoading } = useQuery({
    queryKey: ["vehicleModal"],
    queryFn: getAllVehicleModels,
  });

  const { data: vehicleCompanyData, isLoading: vehicleCompanyLoading } =
    useQuery({
      queryKey: ["vehicleCompany"],
      queryFn: getAllVehicleCompany,
    });

  const modelOptions =
    !vehicleModelLoading && vehicleModelData
      ? vehicleModelData.map((model) => ({
          value: model,
          label: model,
        }))
      : [];

  const companyOptions =
    !vehicleCompanyLoading && vehicleCompanyData
      ? vehicleCompanyData.map((company) => ({
          value: company,
          label: company,
        }))
      : [];

  // Rental status options
  const rentalStatusOptions = [
    { value: true, label: "Rented" },
    { value: false, label: "Not Rented" },
  ];

  const formik = useFormik({
    initialValues: {
      model: initialFilters.model || null,
      company: initialFilters.company || null,
      capacity: initialFilters.capacity || [1000, 14000],
      isRented: initialFilters.isRented || null,
      status: initialFilters.status || null,
    },
    validationSchema: filterVehicleValidationSchema,
    onSubmit: (values) => {
      const filters = [];

      if (values.model) {
        filters.push({
          attribute: "model",
          operation: "EQUALS",
          value: values.model,
        });
      }

      if (values.company) {
        filters.push({
          attribute: "company",
          operation: "EQUALS",
          value: values.company,
        });
      }

      if (
        values.capacity &&
        Array.isArray(values.capacity) &&
        values.capacity.length === 2
      ) {
        filters.push({
          attribute: "capacity",
          operation: "GREATER_THAN_OR_EQUAL",
          value: values.capacity[0].toString(),
        });

        filters.push({
          attribute: "capacity",
          operation: "LESS_THAN_OR_EQUAL",
          value: values.capacity[1].toString(),
        });
      }

      if (values.isRented !== null) {
        filters.push({
          attribute: "isRented",
          operation: "EQUALS",
          value: values.isRented.toString(),
        });
      }

      if (values.status) {
        filters.push({
          attribute: "status",
          operation: "EQUALS",
          value: values.status,
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
        <Select
          id="model"
          name="model"
          label="Vehicle Model"
          placeholder="Select Model"
          fieldWidth="100%"
          loading={vehicleModelLoading}
          options={modelOptions}
          value={formik.values.model}
          onChange={(value) => formik.setFieldValue("model", value)}
          onBlur={formik.handleBlur}
          error={formik.errors.model}
          touched={formik.touched.model}
          allowClear
          noOptionMsg="No vehicle models available"
        />

        <Select
          id="status"
          name="status"
          label="Vehicle Status"
          placeholder="Select Status"
          fieldWidth="100%"
          options={vehicleStatus}
          value={formik.values.status}
          onChange={(value) => formik.setFieldValue("status", value)}
          onBlur={formik.handleBlur}
          error={formik.errors.status}
          touched={formik.touched.status}
          allowClear
          noOptionMsg="No status options available"
        />

        {/* <Select
          id="company"
          name="company"
          label="Vehicle Company"
          placeholder="Select Company"
          fieldWidth="100%"
          loading={vehicleCompanyLoading}
          options={companyOptions}
          value={formik.values.company}
          onChange={(value) => formik.setFieldValue("company", value)}
          onBlur={formik.handleBlur}
          error={formik.errors.company}
          touched={formik.touched.company}
          allowClear
          noOptionMsg="No vehicle companies available"
        /> */}

        <Select
          id="isRented"
          name="isRented"
          label="Rental Status"
          placeholder="Select Rental Status"
          fieldWidth="100%"
          options={rentalStatusOptions}
          value={formik.values.isRented}
          onChange={(value) => formik.setFieldValue("isRented", value)}
          onBlur={formik.handleBlur}
          error={formik.errors.isRented}
          touched={formik.touched.isRented}
          allowClear
          noOptionMsg="No rental status options available"
        />

        <RangeSlider
          label="Vehicle Capacity"
          id="capacity"
          name="capacity"
          min={1000}
          max={20000}
          step={1000}
          unit="kg"
          value={formik.values.capacity}
          onChange={(value) => formik.setFieldValue("capacity", value)}
          onBlur={formik.handleBlur}
          error={formik.errors.capacity}
          touched={formik.touched.capacity}
        />
      </div>

      <div className="flex justify-end gap-2 mt-4">
        <Button onClick={handleResetFilter}>Reset</Button>
        <Button onClick={onCancel}>Cancel</Button>
        <Button
          type="primary"
          className="bg-[#750014]! text-white! border-none! hover:bg-[#750014]!"
          onClick={formik.handleSubmit}
        >
          Apply Filter
        </Button>
      </div>
    </div>
  );
}