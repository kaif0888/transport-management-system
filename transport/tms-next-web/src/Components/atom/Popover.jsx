import React from "react";
import "./Popover.css";

import { Popover as AntPopover } from "antd";

import { cn } from "@/lib/utils";

export function Popover({ rootClassName, ...rest }) {
  return (
    <AntPopover rootClassName={cn("pe_popover", rootClassName)} {...rest} />
  );
}