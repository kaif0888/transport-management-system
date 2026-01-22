import React from "react";
import "./TextArea.css";
import { Input } from "antd";
import { cn } from "@/lib/util";

const { TextArea: AntTextArea } = Input;

export const TextArea = ({
  label,
  labelProps = {},
  id,
  className,
  error,
  touched,
  required,
  ...rest
}) => {
  return (
    <div className="w-full mb-[6px]!">
      {label && (
        <label
          htmlFor={id}
          {...labelProps}
          className={cn(
            "block text-sm font-semibold text-gray-700! mb-1",
            labelProps.className || ""
          )}
        >
          {label}
          {required && <span className="text-[#750014] ml-1">*</span>}
        </label>
      )}
      <AntTextArea
        id={id}
        className={cn(
          "mit_text_area p-2 border rounded-md w-full focus:outline-none focus:border-blue-500",
          error && touched ? "border-red-500!" : "border-gray-300",
          className
        )}
        {...rest}
      />
      {error && touched && (
        <p className="text-[#750014]! text-xs mt-1">{error}</p>
      )}
    </div>
  );
};