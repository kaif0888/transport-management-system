"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Optimize dynamic import - disable SSR for client-heavy components
const OrderConfirm = dynamic(() => import("@/Components/pages/OrderConfirm/OrderConfirm"), {
  ssr: false, // Disable SSR for better performance
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading consignment confirm...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function OrderConfirmPage() {
  return <OrderConfirm />;
}