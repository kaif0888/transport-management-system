"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Preload the CustomerList component
const CustomerList = dynamic(() => import("@/Components/pages/Customer/CustomerList"), {
  ssr: true, // Enable SSR for faster initial load
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

// Preload the component
CustomerList.preload = () => import("@/Components/pages/Customer/CustomerList");

export default function Page() {
  return <CustomerList />;
}