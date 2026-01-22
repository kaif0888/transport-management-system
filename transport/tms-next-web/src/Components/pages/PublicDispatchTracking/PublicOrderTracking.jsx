// "use client";

// import { useQuery } from "@tanstack/react-query";
// import { Card, Timeline } from "antd";
// import moment from "moment";
// import Title from "@/Components/atom/Title";
// import PageWrapper from "@/Data/PageWrapper";

// import {
//   ClockCircleOutlined,
//   CheckCircleOutlined,
//   TruckOutlined,
//   ShoppingCartOutlined,
//   SendOutlined,
// } from "@ant-design/icons";

// import {
//   getPublicOrderSummary,
//   getPublicOrderTimeline,
// } from "@/service/publicTracking";

// const STATUS_ICONS = {
//   PENDING: <ClockCircleOutlined />,
//   CREATED: <ShoppingCartOutlined />,
//   CONFIRM: <CheckCircleOutlined />,
//   DISPATCHED: <SendOutlined />,
//   "IN-TRANSIT": <TruckOutlined />,
//   DELIVERED: <CheckCircleOutlined />,
// };

// const mapTimeline = (data = []) =>
//   data.map((item) => ({
//     dot: STATUS_ICONS[item.status],
//     color: item.color,
//     children: (
//       <div>
//         <b>
//           {item.displayStatus} –{" "}
//           {moment(item.timestamp).format("MMM DD, YYYY hh:mm A")}
//         </b>
//         {item.description && (
//           <div className="text-gray-500 text-sm">
//             {item.description}
//           </div>
//         )}
//       </div>
//     ),
//   }));

// export default function PublicOrderTracking({ orderId }) {
//   const { data: summary, isLoading } = useQuery({
//     queryKey: ["public-order-summary", orderId],
//     queryFn: () => getPublicOrderSummary(orderId),
//     enabled: !!orderId,
//   });

//   const { data: timeline } = useQuery({
//     queryKey: ["public-order-timeline", orderId],
//     queryFn: () => getPublicOrderTimeline(orderId),
//     enabled: !!orderId,
//   });

//   if (isLoading) {
//     return (
//       <PageWrapper>
//         <Title title="Track Consignment" className="text-center" />
//         <div className="text-center mt-10">Loading tracking details…</div>
//       </PageWrapper>
//     );
//   }

//   return (
//     <PageWrapper>
//       <Title title="Track Consignment" className="text-center" />

//       <div className="flex gap-4 mt-6">
//         <Card className="w-1/2" title="Consignment Information">
//           <p><b>Consignment ID:</b> {summary?.orderId}</p>
//           <p><b>Status:</b> {summary?.status}</p>
//           <p><b>Customer:</b> {summary?.customerName}</p>
//           <p><b>Receiver:</b> {summary?.receiverName}</p>
//           <p><b>Origin:</b> {summary?.origin}</p>
//           <p><b>Destination:</b> {summary?.destination}</p>
//           <p><b>Payment Status:</b> {summary?.paymentStatus}</p>
//           <p><b>Remaining Amount:</b> ₹ {summary?.remainingPayment}</p>
//         </Card>

//         <Card className="w-1/2" title="Consignment Tracking">
//           {timeline?.length ? (
//             <Timeline items={mapTimeline(timeline)} />
//           ) : (
//             <div className="text-gray-500 text-center">
//               No tracking information available
//             </div>
//           )}
//         </Card>
//       </div>
//     </PageWrapper>
//   );
// }


"use client";

import { useQuery } from "@tanstack/react-query";
import { Card, Timeline } from "antd";
import moment from "moment";
import Title from "@/Components/atom/Title";
import PageWrapper from "@/Data/PageWrapper";

import {
  ClockCircleOutlined,
  CheckCircleOutlined,
  TruckOutlined,
  ShoppingCartOutlined,
  SendOutlined,
} from "@ant-design/icons";

import {
  getPublicOrderSummary,
  getPublicOrderTimeline,
} from "@/service/publicTracking";

const STATUS_ICONS = {
  PENDING: <ClockCircleOutlined />,
  CREATED: <ShoppingCartOutlined />,
  CONFIRM: <CheckCircleOutlined />,
  DISPATCHED: <SendOutlined />,
  "IN-TRANSIT": <TruckOutlined />,
  DELIVERED: <CheckCircleOutlined />,
};

const InfoRow = ({ label, value }) => (
  <div className="flex gap-2">
    <p className="text-gray-700 text-lg font-semibold min-w-[160px]">
      {label}
    </p>
    <p className="text-gray-700 text-base capitalize">
      {value ?? "N/A"}
    </p>
  </div>
);

const mapTimeline = (data = []) =>
  data.map((item) => ({
    dot: STATUS_ICONS[item.status],
    color: item.color,
    children: (
      <div>
        <div style={{ fontWeight: "bold", marginBottom: 4 }}>
          {item.displayStatus} –{" "}
          {moment(item.timestamp).format("MMM DD, YYYY hh:mm A")}
        </div>
        {item.description && (
          <div className="text-gray-500 text-sm">
            {item.description}
          </div>
        )}
      </div>
    ),
  }));

export default function PublicOrderTracking({ orderId }) {
  const { data: summary, isLoading } = useQuery({
    queryKey: ["public-order-summary", orderId],
    queryFn: () => getPublicOrderSummary(orderId),
    enabled: !!orderId,
  });

  const { data: timeline } = useQuery({
    queryKey: ["public-order-timeline", orderId],
    queryFn: () => getPublicOrderTimeline(orderId),
    enabled: !!orderId,
  });

  if (isLoading) {
    return (
      <PageWrapper>
        <Title title="Track Consignment" className="text-center" />
        <div className="text-center mt-10">
          Loading tracking details…
        </div>
      </PageWrapper>
    );
  }

  return (
    <PageWrapper>
      <Title title="Track Consignment" className="text-center" />

      <div className="flex gap-4 mt-6 w-full">
        {/* LEFT CARD */}
        <Card
          className="cardShadow border-0 w-1/2"
          title="Consignment Information"
          styles={{
            header: { backgroundColor: "#f8f9fa", fontWeight: "bold" },
          }}
        >
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <InfoRow label="Consignment Id:" value={summary?.orderId} />
            <InfoRow label="Status:" value={summary?.status} />
            <InfoRow label="Customer:" value={summary?.customerName} />
            <InfoRow label="Receiver:" value={summary?.receiverName} />
            <InfoRow label="Origin:" value={summary?.originLocationName} />
            <InfoRow label="Dispatch Date:" value={summary?.dispatchDate} />
            <InfoRow label="Destination:" value={summary?.destinationLocationName} />
            <InfoRow label="Delivery Date:" value={summary?.deliveryDate} />
            <InfoRow label="Payment Status:" value={summary?.paymentStatus} />
            <InfoRow
              label="Remaining Amount:"
              value={`₹ ${summary?.remainingPayment ?? 0}`}
            />
          </div>
        </Card>

        {/* RIGHT CARD */}
        <Card
          className="cardShadow border-0 w-1/2"
          title="Consignment Tracking"
          styles={{
            header: { backgroundColor: "#f8f9fa", fontWeight: "bold" },
          }}
        >
          {timeline?.length ? (
            <Timeline items={mapTimeline(timeline)} />
          ) : (
            <div className="text-gray-500 text-center">
              No tracking information available
            </div>
          )}
        </Card>
      </div>
    </PageWrapper>
  );
}

