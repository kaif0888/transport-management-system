import React from "react";

import { Button as AntButton, ConfigProvider } from "antd";
import { TinyColor } from "@ctrl/tinycolor";

export function Button({ bgColor = "#750014", ...rest }) {
  return (
    <ConfigProvider
      theme={{
        components: {
          Button: {
            colorPrimary: bgColor,
            colorPrimaryHover: new TinyColor(bgColor).lighten(5).toString(),
            colorPrimaryActive: new TinyColor(bgColor).darken(5).toString(),
          },
        },
      }}
    >
      <AntButton {...rest} />
    </ConfigProvider>
  );
}
