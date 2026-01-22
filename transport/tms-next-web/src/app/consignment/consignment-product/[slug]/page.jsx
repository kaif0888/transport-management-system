"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';
import { useParams } from 'next/navigation';

// Optimize dynamic import - disable SSR for client-heavy components
const OrderProductAdd = dynamic(() => import("@/Components/pages/OrderProduct/AddOrderProduct"), {
  ssr: false, // Disable SSR for better performance
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading add consignment Box ...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function OrderProductSlugPage() {
  const params = useParams();
  const slug = params.slug;
  return <OrderProductAdd slug={slug} />;
}