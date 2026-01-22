"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Optimize dynamic import - disable SSR for client-heavy components
const ManifestList = dynamic(() => import("@/Components/pages/Manifest/ManifestList"), {
  ssr: false, // Disable SSR for better performance
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading manifest list...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function OrderPage() {
  return <ManifestList />;
}