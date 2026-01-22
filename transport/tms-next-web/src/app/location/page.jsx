"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Use dynamic import with no SSR to reduce initial load time
const Location = dynamic(() => import("@/Components/pages/Location/LocationList"), {
  ssr: false,
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function Page(){
  return <Location />;
}