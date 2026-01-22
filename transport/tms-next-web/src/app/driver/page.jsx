"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';


const Driver  = dynamic(()=>import("@/Components/pages/Driver/DriverList"),{
  ssr:false,
  loading:() => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
})

export default function Page(){
  return(<Driver/>)
}