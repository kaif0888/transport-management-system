"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';

// Use dynamic import with no SSR to reduce initial load time
const ExpenseType = dynamic(() => import("@/Components/pages/Expense/ExpenseType/ExpenseTypeList"), {
  ssr: false,
  loading: () => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
});

export default function Page(){
  return <ExpenseType />;
}