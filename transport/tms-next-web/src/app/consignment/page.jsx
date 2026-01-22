"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Optimize dynamic import - disable SSR for client-heavy components
const ConsignmentList = dynamic(() => import("@/Components/pages/Order/OrderList"), {
  ssr: false, // Disable SSR for better performance
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" className='w-full' tip="Loading consignment...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function OrderPage() {
  return <ConsignmentList />;
}