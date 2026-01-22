"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Optimize dynamic import - disable SSR for client-heavy components
const VehicleTypeList = dynamic(() => import("@/Components/pages/Vehicle/VehicleType/VehicleTypeList"), {
  ssr: false, // Disable SSR for better performance
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading vehicles Type...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function VehicleTypePage() {
  return <VehicleTypeList />;
}