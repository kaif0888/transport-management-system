"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';
import { useParams, useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

const AnalysisAndProfit = dynamic(() => import("@/Components/pages/Order/AnalysisAndProfit"), {
  ssr: false,
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading analysis...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function Page() {
  const params = useParams();
  const router = useRouter();
  const slug = params.slug;
  const [isRedirecting, setIsRedirecting] = useState(false);

  useEffect(() => {
    // Redirect to consignment list if no slug is provided
    if (!slug) {
      setIsRedirecting(true);
      router.push('/consignment');
    }
  }, [slug, router]);

  // If redirecting, don't render anything
  if (isRedirecting || !slug) {
    return null;
  }

  return <AnalysisAndProfit slug={slug} />;
}
