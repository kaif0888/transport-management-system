import React, { useState, useEffect } from "react";
import { Slider as AntSlider } from "antd";
import { cn } from "@/lib/util";

export function RangeSlider({
  label,
  labelProps,
  id,
  name,
  className,
  error,
  touched,
  required = false,
  min = 0,
  max = 100,
  step = 1,
  value,
  onChange,
  onBlur,
  disabled = false,
  unit = "kg",
  showMarks = true,
  ...rest
}) {
  const showError = error && touched;
  
  // Generate marks based on min and max values
  const marks = {};
  if (showMarks) {
    // Add min mark
    marks[min] = {
      label: <span>{min}</span>
    };
    
    // Add max mark
    marks[max] = {
      label: <span>{max}</span>
    };
    
    
  }
  
  const handleChange = (val) => {
    if (onChange) {
      onChange(val);
    }
  };
  
  const handleAfterChange = () => {
    if (onBlur) {
      onBlur({ target: { name } });
    }
  };
  
  const sliderProps = {
    id,
    min,
    max,
    step,
    marks,
    disabled,
    range: true,
    value: value || [min, max],
    onChange: handleChange,
    onAfterChange: handleAfterChange,
    className: cn(
      "w-full",
      showError ? "ant-slider-error" : "",
      className
    ),
    ...rest
  };
  
  return (
    <div className="w-full mb-2 pr-4">
      {label && (
        <label
          htmlFor={id}
          {...(labelProps || {})}
          className={cn(
            "block text-sm font-semibold text-gray-700 mb-1 flex items-center",
            (labelProps && labelProps.className) || ""
          )}
        >
          {label} ({unit}) &nbsp;
          {required && (
            <span className="text-[#750014] ml-1 text-sm">*</span>
          )}
           {Array.isArray(value) && value.length === 2 && (
        <div className="flex justify-between border border-solid border-[#ccc] px-3 py-1 rounded text-sm font-medium text-gray-700">
          <span>{value[0]} - {value[1]} {unit}</span> 
        </div>
      )}
        </label>
      )}
      
      <div className="relative mt-4">
        <style jsx global>{`
          .ant-slider .ant-slider-track {
            background-color: #750014 !important;
          }
          
          .ant-slider .ant-slider-handle:focus {
            box-shadow: 0 0 0 5px rgba(117, 0, 20, 0.12);
          }
          
          .ant-slider .ant-slider-handle {
            border-color: #750014 !important;
            background-color: #750014 !important;
          }
          
          .ant-slider .ant-slider-handle:hover {
            border-color: #900020 !important;
          }
          
          .ant-slider-error .ant-slider-rail {
            background-color: rgba(117, 0, 20, 0.1) !important;
          }
          
     
          
         
        `}</style>
        
        <AntSlider {...sliderProps} />
      </div>
      
      {showError && (
        <div className="text-[#750014] text-xs mt-1">{error}</div>
      )}
      
     
    </div>
  );
}