import React, { useMemo } from "react";
import { Progress, ConfigProvider } from "antd";
import { TinyColor } from "@ctrl/tinycolor";

export function ProgressBar({ 
  totalAmount, 
  paidAmount, 
  title, 
  progressColor = "#750014",
  ...rest 
}) {
  // Calculate percentage based on total and paid amounts
  const percent = useMemo(() => {
    if (!totalAmount || totalAmount === 0) return 0;
    const calculatedPercent = Math.floor((paidAmount / totalAmount) * 100);
    return Math.min(100, Math.max(0, calculatedPercent));
  }, [totalAmount, paidAmount]);

 

  return (
    <ConfigProvider
      theme={{
        components: {
          Progress: {
            colorSuccess: progressColor,
            colorInfo: progressColor,
            circleTextColor: progressColor,
          },
        },
      }}
    >
      <div style={{ width: "100%" }}>
        <div className="flex justify-between items-center">
        {title && (
          <span className="text-gray-500 font-semibold ">
            {title}
          </span>
        )}
        <span className="text-gray-500 font-semibold ">{percent}% Complete</span>
        </div>
   
        
        {/* Progress Bar */}
        <Progress
          percent={percent}
          strokeColor={progressColor}
          trailColor="#f0f0f0"
          strokeWidth={12}
          showInfo={false}
          {...rest}
        />
      </div>
    </ConfigProvider>
  );
}