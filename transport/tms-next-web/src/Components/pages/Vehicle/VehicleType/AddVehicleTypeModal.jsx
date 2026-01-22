"use client";

import { Input } from "@/Components/atom/Input";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { locationValidationSchema } from "@/Data/constant";
import {
  AddVehicleType,
  // UpdateExpenseType,
  // getExpenseTypeById
} from "@/service/vehicle";
import { useQuery } from "@tanstack/react-query";
import { useEffect } from "react";
import { toast } from "react-toastify";

export default function AddVehicleTypeModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {
  const initialValues = {
    vehicleTypeName: "",
  };

  const formik = useFormik({
    initialValues,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true, // This will reinitialize form when initialValues change
    onSubmit: async (values, { resetForm }) => {
      try {
        if (selectedId === null) {
          await AddVehicleType(values);
          toast.success("Vehicle Type added successfully");
        } else {
          const updatePayload = {
            vehicleTypeId: selectedId,
            vehicleTypeName:values
          }
            await UpdateLocation(updatePayload);
          toast.success("Vehicle updated successfully");
        }

        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save vehicle type");
      }
    },
  });


  const handleModalClose = () => {
    formik.resetForm();
    handleCancel();
  };



  return (
    <>
      <form className="space-y-4">
        <Input
          label="Vehicle Type Name"
          id="vehicleTypeName"
          name="vehicleTypeName"
          required
          type="text"
          placeholder="Enter Vehicle Type Name"
          value={formik.values.vehicleTypeName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.vehicleTypeName && formik.errors.vehicleTypeName}
          touched={formik.touched.vehicleTypeName}
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
