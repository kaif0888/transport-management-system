"use client";
import { useFormik } from "formik";
import { Input } from "@/Components/atom/Input";
import { Button } from "@/Components/atom/Button";
import { toast } from "react-toastify";
import { paymentValidationSchema } from "@/Data/constant";
import { paymentModeOptions } from "@/Data/impData";
export default function PaymentForm({
  onSubmit,
  onCancel,
  orderId,
  dueAmount,
  paidAmount,
  totalAmount,  
  customerId,
  isSubmitting = false,
}) {
  // Custom validation function
  const validatePaymentAmount = (value) => {
    if (!value) return "Payment amount is required";
    if (parseFloat(value) <= 0) return "Payment amount must be greater than 0";
    if (parseFloat(value) > parseFloat(dueAmount)) {
      return `Payment amount cannot exceed due amount of ₹${dueAmount}`;
    }
    return undefined;
  };

  const formik = useFormik({
    initialValues: {
      totalAmount: totalAmount,
      advancePayment: "",
      paymentMode: "",
    },
    validationSchema: paymentValidationSchema,
    validate: (values) => {
      const errors = {};
      
      // Custom validation for advancePayment
      const paymentError = validatePaymentAmount(values.advancePayment);
      if (paymentError) {
        errors.advancePayment = paymentError;
      }
      
      return errors;
    },
    validateOnChange: true,
    validateOnBlur: true,
    onSubmit: async (values) => {
      try {
        // Additional validation before submission
        if (parseFloat(values.advancePayment) > parseFloat(dueAmount)) {
          toast.error(`Payment amount cannot exceed due amount of ₹${dueAmount}`);
          return;
        }

        const paymentData = {
          orderId,
          customerId,
          totalAmount: parseFloat(values.totalAmount),
          advancePayment: parseFloat(values.advancePayment),
          paymentMethod: values.paymentMode
        };

        await onSubmit(paymentData);
      } catch (error) {
        console.error("Payment submission error:", error);
        toast.error(error.message || "Payment processing failed");
      }
    },
  });

  const remainingAmount =
    formik.values.totalAmount && formik.values.advancePayment
      ? (
          parseFloat(formik.values.totalAmount) -
          parseFloat(formik.values.advancePayment)
        ).toFixed(2)
      : "0.00";

  return (
    <form className="space-y-4">
      <div className="bg-gray-50 p-4 rounded-lg mb-4">
        <h3 className="text-sm font-semibold mb-2 text-gray-700">
          Payment Information
        </h3>
        <div className="space-y-1 text-sm">
          <div className="flex justify-between">
            <span className="text-gray-600">Total Amount:</span>
            <span className="font-medium text-gray-800">
              ₹{totalAmount}
            </span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-600">Advance Payment (Paid):</span>
            <span className="font-medium text-gray-800">
              ₹{paidAmount}
            </span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-600">Remaining Amount (Due):</span>
            <span className="font-medium text-red-600 font-semibold">
              ₹{dueAmount}
            </span>
          </div>
        </div>
      </div>

      <Input
        id="advancePayment"
        name="advancePayment"
        type="number"
        label="Payment Amount"
        step="0.01"
        min="0"
        max={dueAmount}
        placeholder={`Enter payment amount (Max: ₹${dueAmount})`}
        value={formik.values.advancePayment}
        onChange={formik.handleChange}
        onBlur={formik.handleBlur}
        error={formik.errors.advancePayment}
        touched={formik.touched.advancePayment}
      />

      {/* Due Amount Warning */}
      {formik.values.advancePayment && parseFloat(formik.values.advancePayment) > parseFloat(dueAmount) && (
        <div className="bg-red-50 border border-red-200 p-3 rounded-lg">
          <p className="text-red-600 text-sm font-medium">
            ⚠️ Payment amount cannot exceed remaining due amount of ₹{dueAmount}
          </p>
        </div>
      )}

      {/* New Due Amount Calculation Display */}
      {formik.values.advancePayment && parseFloat(formik.values.advancePayment) <= parseFloat(dueAmount) && (
        <div className="bg-green-50 p-3 rounded-lg border border-green-200">
          <div className="flex justify-between items-center">
            <span className="text-sm font-medium text-green-700">
              Amount After This Payment:
            </span>
            <span className="text-lg font-semibold text-green-800">
              ₹{(parseFloat(dueAmount) - parseFloat(formik.values.advancePayment)).toFixed(2)}
            </span>
          </div>
        </div>
      )}

      {/* Payment Mode */}
      <div>
        <label className="flex items-center gap-2 text-sm font-medium text-gray-700 mb-2">
        <span>Payment Mode</span>
        </label>
        <div className="grid grid-cols-2 gap-3">
          {paymentModeOptions.map((option) => (
            <button
              key={option.value}
              type="button"
              onClick={() => formik.setFieldValue("paymentMode", option.value)}
              className={`p-3 rounded-lg border-2 transition-all duration-200 text-center font-medium ${
                formik.values.paymentMode === option.value
                  ? "border-[#750014] bg-[#750014] text-white"
                  : "border-gray-200 bg-white text-gray-700 hover:border-gray-300"
              }`}
            >
              {option.label}
            </button>
          ))}
        </div>
        {formik.touched.paymentMode && formik.errors.paymentMode && (
          <p className="text-red-500 text-sm mt-1">
            {formik.errors.paymentMode}
          </p>
        )}
      </div>

      {/* Action Buttons */}
      <div className="flex gap-4 justify-end pt-4 border-t">
        <Button type="button" onClick={onCancel} disabled={isSubmitting}>
          Cancel
        </Button>
        <Button
          onClick={formik.handleSubmit}
          disabled={!formik.isValid || isSubmitting}
          className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]!"
        >
          {isSubmitting ? "Processing..." : "Process Payment"}
        </Button>
      </div>
    </form>
  );
}