"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';
import { useParams } from 'next/navigation';

// Optimize dynamic import - disable SSR for client-heavy components
const OrderTrackingPage = dynamic(() => import("@/Components/pages/Order/OrderTracking"), {
  ssr: false, // Disable SSR for better performance
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading order tracking...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function Page() {
  const params = useParams();
  const slug = params.id;
  return <OrderTrackingPage slug={slug} />;
}