import React from "react";

import { Checkbox as AntCheckbox, ConfigProvider } from "antd";


export function Checkbox({ ...rest }) {
  return (
    <ConfigProvider theme={{ token: { colorPrimary: "#750014" } }}>
      <AntCheckbox {...rest} />
    </ConfigProvider>
  );
}
