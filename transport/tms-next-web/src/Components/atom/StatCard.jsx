// Optimized StatCard.jsx
import { memo } from 'react';
import { Card, Skeleton } from "antd";

const cn = (...classes) => classes.filter(Boolean).join(' ');

const StatCard = memo(function StatCard({
  icon: Icon,
  label,
  count,
  className,
  color,
  loading = false,
  onClick,
}) {
  const cleanColor = color ? color.replace(/[\[\]"']/g, "") : null;
  
  return (
    <Card
      className={cn(
        "min-w-[200px] min-h-[100px] border-l-6! cardShadow border-r-0 border-t-0 border-b-0",
        "transition-all duration-300 hover:scale-105 hover:shadow-lg",
        "cursor-pointer",
        className
      )}
      style={cleanColor ? { borderLeftColor: cleanColor } : {}}
      onClick={onClick}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          onClick?.();
        }
      }}
    >
      <div className="flex justify-between gap-2">
        <div className="flex flex-col gap-1">
          <p 
            className="text-xl font-bold"
            style={cleanColor ? { color: cleanColor } : {}}
          >
            {label}
          </p>
          {loading ? (
            <Skeleton.Input active size="small" className="w-20" />
          ) : (
            <p className="text-lg font-semibold text-gray-700">{count}</p>
          )}
        </div>
        {Icon && (
          <Icon
            className={cn(
              "text-3xl text-gray-700 mt-2",
              "transition-transform duration-300 hover:scale-110"
            )}
          />
        )}
      </div>
    </Card>
  );
});

export { StatCard };