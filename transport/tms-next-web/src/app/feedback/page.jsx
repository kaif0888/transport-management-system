"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Use dynamic import with no SSR to reduce initial load time
const Feedback = dynamic(() => import("@/Components/pages/Feedback/ShowFeedback"), {
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
  return <Feedback/>;
}