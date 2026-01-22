"use client";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { useMemo } from "react";
import * as Yup from "yup";
import { DatePicker } from "@/Components/atom/DatePicker";

// Validation schema for expense filter
const filterExpenseValidationSchema = Yup.object({
  amount: Yup.number()
    .typeError("Amount must be a number")
    .nullable(),
  date: Yup.date()
    .nullable()
    .typeError("Please enter a valid date"),
});

export default function ExpenseFilter({
  onApplyFilter,
  onCancel,
  initialFilters = {},
}) {
  const formik = useFormik({
    initialValues: {
      amount: initialFilters.amount || null,
      date: initialFilters.date || null,
    },
    validationSchema: filterExpenseValidationSchema,
    onSubmit: (values) => {
      const filters = [];

      if (values.amount) {
        filters.push({
          attribute: "amount",
          operation: "EQUALS",
          value: values.amount,
        });
      }

      if (values.date) {
        filters.push({
          attribute: "date",
          operation: "EQUALS",
          value: values.date,
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
      formik.values.amount !== (initialFilters.amount || null) ||
      formik.values.date !== (initialFilters.date || null)
    );
  }, [formik.values, initialFilters]);

  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="flex flex-col">
          <label className="mb-2 text-sm font-medium text-gray-700">
            Amount
          </label>
          <Input
            id="amount"
            name="amount"
            type="number"
            placeholder="Enter expense amount"
            value={formik.values.amount || ""}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.amount && formik.errors.amount}
            touched={formik.touched.amount}
            className="w-full"
          />
        </div>

        <div className="flex flex-col">
          <DatePicker
            id="date"
            name="date"
            label="Date"
            placeholder="Select expense date"
            value={formik.values.date}
            onChange={(name, value) => formik.setFieldValue("date", value)}
            onBlur={formik.handleBlur}
            error={formik.errors.date}
            touched={formik.touched.date}
            className="w-full"
          />
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