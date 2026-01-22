"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';
import { useParams } from 'next/navigation';

// Use dynamic import with no SSR to reduce initial load time
const PaymentHistory = dynamic(() => import("@/Components/pages/PaymentHistory/PaymentHistory"), {
  ssr: false,
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading Payment History...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function Page(){
  const params = useParams();
  const slug = params.slug;
  return <PaymentHistory slug={slug} />;
}