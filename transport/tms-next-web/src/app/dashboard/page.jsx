"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Use dynamic import with SSR enabled for better initial load
const Dashboard = dynamic(() => import("@/Components/pages/Dashboard/Dashboard"), {
  ssr: true, // Enable SSR for faster initial load
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function Page(){
  return <Dashboard />;
}