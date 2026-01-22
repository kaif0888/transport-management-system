"use client";
import { useFormik } from "formik";
import { Input } from "@/Components/atom/Input";
import { Button } from "@/Components/atom/Button";
import {
  forgotPasswordValidationSchema,
  resetPasswordValidationSchema,
} from "@/Data/constant";
import { forgotPassword, resetPassword } from "@/service/auth";
import { toast } from "react-toastify";
import { useState } from "react";

export default function ForgotPassword({ handleCancel }) {
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const formik = useFormik({
    initialValues: { email: "" },
    validationSchema: forgotPasswordValidationSchema,
    onSubmit: async (values, { resetForm }) => {
      try {
        setIsLoading(true);
        await forgotPassword(values.email);
        toast.success("OTP sent to your email.");
        setIsOtpSent(true);
      } catch (error) {
        toast.error(error.message || "Something went wrong.");
      } finally {
        setIsLoading(false);
      }
    },
  });

  const resetFormik = useFormik({
    initialValues: { otp: "", newPassword: "", confirmPassword: "" },
    validationSchema: resetPasswordValidationSchema,
    onSubmit: async (values, { resetForm }) => {
      try {
        setIsLoading(true);
        const payload = {
          // Include email from first form
          token: parseInt(values.otp),
          newPassword: values.newPassword,
        };
        await resetPassword(payload);
        toast.success("Your password has been reset successfully.");
        handleCancel();
        resetForm();
        formik.resetForm(); // Reset the email form as well
      } catch (error) {
        toast.error(error.message || "Something went wrong.");
      } finally {
        setIsLoading(false);
      }
    },
  });
  return (
    <div className="space-y-4">
      <Input
        label="Email"
        id="email"
        name="email"
        type="email"
        required
        placeholder="Enter your email"
        value={formik.values.email}
        onChange={formik.handleChange}
        onBlur={formik.handleBlur}
        error={formik.touched.email && formik.errors.email}
        touched={formik.touched.email}
        disabled={isOtpSent || isLoading} // Disable email input after OTP is sent
      />

      {isOtpSent && (
        <>
          <Input
            label="OTP"
            id="otp"
            name="otp"
            type="otp"
            required
            length={6}
            value={resetFormik.values.otp}
            onChange={(e) => {
              resetFormik.setFieldValue("otp", e);
            }}
            onBlur={() => {
              resetFormik.setFieldTouched("otp", true);
            }}
            error={resetFormik.touched.otp && resetFormik.errors.otp}
            touched={resetFormik.touched.otp}
            disabled={isLoading}
          />
          <Input
            label="New Password"
            id="newPassword"
            name="newPassword"
            type="password"
            required
            placeholder="Enter your new password"
            value={resetFormik.values.newPassword}
            onChange={resetFormik.handleChange}
            onBlur={resetFormik.handleBlur}
            error={
              resetFormik.touched.newPassword && resetFormik.errors.newPassword
            }
            touched={resetFormik.touched.newPassword}
            disabled={isLoading}
          />
          <Input
            label="Confirm Password"
            id="confirmPassword"
            name="confirmPassword"
            type="password"
            required
            placeholder="Enter your confirm password"
            value={resetFormik.values.confirmPassword}
            onChange={resetFormik.handleChange}
            onBlur={resetFormik.handleBlur}
            error={
              resetFormik.touched.confirmPassword &&
              resetFormik.errors.confirmPassword
            }
            touched={resetFormik.touched.confirmPassword}
            disabled={isLoading}
          />
        </>
      )}

      <div className="flex gap-4 justify-end pt-4">
        <Button onClick={handleCancel} disabled={isLoading}>
          Cancel
        </Button>
        <Button
          onClick={isOtpSent ? resetFormik.handleSubmit : formik.handleSubmit}
          disabled={
            isLoading ||
            (isOtpSent
              ? !resetFormik.isValid || resetFormik.isSubmitting
              : !formik.isValid || formik.isSubmitting)
          }
          className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]!"
        >
          {isLoading ? (
            <span className="flex items-center gap-2">
              <svg className="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full" />
              {isOtpSent ? "Resetting..." : "Sending..."}
            </span>
          ) : isOtpSent ? (
            "Reset Password"
          ) : (
            "Send OTP"
          )}
        </Button>
      </div>
    </div>
  );
}
