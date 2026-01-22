import React from "react";
import { Input as AntInput } from "antd";
import { cn } from "@/lib/util";

export function Input({ 
  label, 
  labelProps, 
  id, 
  className, 
  error, 
  touched, 
  required = false,
  startIcon,
  endIcon,
  type = "text",
  // OTP specific props
  length,
  formatter,
  mask,
  separator,
  variant,
  ...rest 
}) {
  const showError = error && touched;
  
  // Common props for all input types
  const commonProps = {
    id,
    status: showError ? "error" : "",
    required,
    ...rest
  };
  
  // OTP specific props
  const otpProps = {
    length,
    formatter,
    mask,
    separator,
    variant,
    className: cn(
      "otp-input",
      showError 
        ? "border-[#750014] focus:border-[#750014]" 
        : "border-gray-300 focus:border-blue-500",
      className
    ),
    ...commonProps
  };
  
  // Regular input props
  const inputProps = {
    className: cn(
      "nl2_text_area p-2 border rounded-md w-full focus:outline-none",
      startIcon ? "pl-10!" : "",
      endIcon ? "pr-10!" : "",
      showError 
        ? "border-[#750014] focus:border-[#750014]" 
        : "border-gray-300 focus:border-blue-500",
      className
    ),
    ...commonProps
  };
  
  // Choose the appropriate input component based on type
  let InputComponent;
  let componentProps;
  
  if (type === "otp") {
    InputComponent = AntInput.OTP;
    componentProps = otpProps;
  } else if (type === "password") {
    InputComponent = AntInput.Password;
    componentProps = inputProps;
  } else {
    InputComponent = AntInput;
    componentProps = inputProps;
  }
  
  return (
    <div className="w-full mb-2">
      {label && (
        <label
          htmlFor={id}
          {...(labelProps || {})}
          className={cn(
            "text-sm font-semibold text-gray-700 mb-1 flex items-center",
            (labelProps && labelProps.className) || ""
          )}
        >
          {label}
          {required && (
            <span className="text-[#750014] ml-1 text-sm">*</span>
          )}
        </label>
      )}
      
      {type === "otp" ? (
        // OTP input doesn't need the wrapper div with icons
        <InputComponent {...componentProps} />
      ) : (
        // Regular inputs with icon support
        <div className="relative flex items-center">
          {startIcon && (
            <div className="absolute left-3 z-10 flex items-center justify-center text-gray-500">
              {startIcon}
            </div>
          )}
          <InputComponent {...componentProps} />
          {endIcon && type !== "password" && (
            <div className="absolute right-3 z-10 flex items-center justify-center text-gray-500">
              {endIcon}
            </div>
          )}
        </div>
      )}
      
      {showError && (
        <div className="text-[#750014] text-xs mt-1">{error}</div>
      )}
    </div>
  );
}