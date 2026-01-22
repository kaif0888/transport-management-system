"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';
import { useParams } from 'next/navigation';

// Optimize dynamic import - disable SSR for client-heavy components
const ManifestView = dynamic(() => import("@/Components/pages/Manifest/ViewManifest"), {
  ssr: false, // Disable SSR for better performance
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading view manifest ...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function OrderViewPage() {
  const params = useParams();
  const slug = params.slug;
  return <ManifestView slug={slug} />;
}