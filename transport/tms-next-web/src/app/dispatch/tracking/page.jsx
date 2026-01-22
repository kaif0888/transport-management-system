"use client"

import dynamic from 'next/dynamic';
import { Spin } from 'antd';


const DispatchTrackingList  = dynamic(()=>import("@/Components/pages/DispatchTracking/DispatchTrackingList"),{
  ssr:false,
  loading:() => (
    <div className="flex justify-center items-center h-screen w-full">
      <Spin size="large" tip="Loading Dispatch Tracking...">
        <div className="p-12 bg-transparent" />
      </Spin>
    </div>
  )
})

export default function Page(){
  return(<DispatchTrackingList/>)
}