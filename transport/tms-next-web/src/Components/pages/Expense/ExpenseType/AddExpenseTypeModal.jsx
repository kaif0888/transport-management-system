"use client";

import { Input } from "@/Components/atom/Input";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { locationValidationSchema } from "@/Data/constant";
import {
  AddExpenseType,
  // UpdateExpenseType,
  // getExpenseTypeById
} from "@/service/expense";
import { useQuery } from "@tanstack/react-query";
import { useEffect } from "react";
import { toast } from "react-toastify";

export default function AddExpenseTypeModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {

  const initialValues = {
    expenseTypeName: "",
  };

  const formik = useFormik({
    initialValues,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true, // This will reinitialize form when initialValues change
    onSubmit: async (values, { resetForm }) => {
      try {
        if (selectedId === null) {
          await AddExpenseType(values);
          toast.success("Expense Type added successfully");
        } else {
          const updatePayload = {
            expenseTypeId: selectedId,
            expenseTypeName:values
          }
          //   await UpdateLocation(selectedId, values);
          toast.success("Expense Type updated successfully");
        }

        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save expense type");
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
          label="Expense Type  Name"
          id="expenseTypeName"
          name="expenseTypeName"
          required
          type="text"
          placeholder="Enter Expense Type Name"
          value={formik.values.expenseTypeName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.expenseTypeName && formik.errors.expenseTypeName}
          touched={formik.touched.expenseTypeName}
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
