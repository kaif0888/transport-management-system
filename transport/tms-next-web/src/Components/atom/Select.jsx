"use client";
import React from "react";
import { Select as AntSelect } from "antd";

export function Select({
  id,
  label,
  defaultValue,
  fieldWidth = "120px",
  required = false,
  error,
  touched,
  className = "",
  noOptionMsg = "",
  multiple = false,
  ...rest
}) {
  return (
    <div className={`w-full ${className} mb-2`}>
      <label
        htmlFor={id}
        className="block text-sm font-semibold text-gray-700 mb-1"
      >
        {label} {required && <span className="text-[#750014]">*</span>}
      </label>
      <AntSelect
        id={id}
        defaultValue={defaultValue}
        style={{ width: fieldWidth }}
        className={`${touched && error ? "border-[#750014]" : ""}`}
        mode={multiple ? "multiple" : undefined}
        {...rest}
      />
      {touched && error ? (
        <div className="text-[#750014] text-xs mt-1">{error}</div>
      ) : null}

      {rest.options && rest.options.length === 0 && (
        <p className="block text-sm font-semibold text-[#750014]">
          {noOptionMsg}
        </p>
      )}
    </div>
  );
}