"use client";
import { DatePicker as AntDatePicker } from "antd";
import moment from "moment";
import { useState, useEffect } from "react";

export const DatePicker = ({
  label,
  id,
  name,
  placeholder = "Select date",
  value,
  onChange,
  onBlur,
  error,
  touched,
  format = "YYYY-MM-DD",
  disabled = false,
  className = "",
  required = false,
}) => {
  // Convert string date to moment object if it exists
  const [dateValue, setDateValue] = useState(null);

  useEffect(() => {
    if (value) {
      const momentDate = moment(value);
      setDateValue(momentDate.isValid() ? momentDate : null);
    } else {
      setDateValue(null);
    }
  }, [value]);

  // Handle date change
  const handleDateChange = (date, dateString) => {
    onChange(name, dateString);
  };

  return (
    <div className={`mb-2 ${className}`}>
      {label && (
        <label htmlFor={id} className="block text-sm font-medium text-gray-700 mb-1">
          {label} {required && <span className="text-[#750014]">*</span>}
        </label>
      )}
      <AntDatePicker
        id={id}
        name={name}
        placeholder={placeholder}
        style={{ width: "100%", height: "32px" }}
        value={dateValue}
        onChange={handleDateChange}
        onBlur={() => onBlur && onBlur({ target: { name } })}
        format={format}
        disabled={disabled}
        className={`${
          touched && error
            ? "border-[#750014] focus:ring-[#750014] focus:border-[#750014]"
            : ""
        }`}
      />
      {touched && error ? (
        <div className="text-[#750014] text-xs mt-1">{error}</div>
      ) : null}

      
    </div>
  );
};
