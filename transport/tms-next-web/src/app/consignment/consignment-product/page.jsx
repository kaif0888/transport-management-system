"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Optimize dynamic import - disable SSR for client-heavy components
const OrderProduct = dynamic(() => import("@/Components/pages/OrderProduct/OrderProductList"), {
  ssr: false, // Disable SSR for better performance
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading consignment product...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function OrderProductPage() {
  return <OrderProduct />;
}