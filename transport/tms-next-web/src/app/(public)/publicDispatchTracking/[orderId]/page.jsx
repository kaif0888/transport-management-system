"use client";

import { useParams } from "next/navigation";
import PublicOrderTracking from "@/Components/pages/PublicDispatchTracking/PublicOrderTracking";

export default function Page() {
  const { orderId } = useParams();
  return <PublicOrderTracking orderId={orderId} />;
}