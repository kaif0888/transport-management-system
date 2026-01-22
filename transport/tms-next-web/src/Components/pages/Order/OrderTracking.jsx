"use client";
import { useRef, useMemo, useCallback } from "react";
import { useQuery } from "@tanstack/react-query";
import { Card, Tooltip, Timeline } from "antd";
import Title from "@/Components/atom/Title";
import PageWrapper from "@/Data/PageWrapper";
import { Table } from "@/Components/atom/Table";
import { Button } from "@/Components/atom/Button";
import { getorderstatus } from "@/service/dispatchTracking";
import { getOrderById } from "@/service/order";
import moment from "moment";
import {
  ClockCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  TruckOutlined,
  ShoppingCartOutlined,
  SendOutlined,
} from "@ant-design/icons";

const CardValue = ({ field, value }) => (
  <div className="flex gap-2">
    <p className="text-gray-700 text-lg font-semibold">{field}</p>
    <p className="text-gray-700 text-base pt-[4px] capitalize">
      {value || "N/A"}
    </p>
  </div>
);

const convertToTimelineItems = (jsonData) => {
  // Icon mapping based on status
  const STATUS_ICONS = {
    PENDING: <ClockCircleOutlined style={{ fontSize: "16px" }} />,
    CREATED: <ShoppingCartOutlined style={{ fontSize: "16px" }} />,
    CONFIRM: <CheckCircleOutlined style={{ fontSize: "16px" }} />,
    DISPATCHED: <SendOutlined style={{ fontSize: "16px" }} />,
    "IN-TRANSIT": <TruckOutlined style={{ fontSize: "16px" }} />,
    // Add more status icons as needed
    DELIVERED: <CheckCircleOutlined style={{ fontSize: "16px" }} />,
  };

  if (!Array.isArray(jsonData)) return [];

  return jsonData.map((item) => {
    // Use the icon based on status, fallback to default
    const icon = STATUS_ICONS[item.status] || STATUS_ICONS.default;
    
    // Format timestamp - handle both formats (with/without timezone)
    const formattedTime = moment(item.timestamp).format("MMM DD, YYYY hh:mm A");

    return {
      children: (
        <div>
          <div style={{ fontWeight: "bold", marginBottom: "4px" }}>
            {item.displayStatus} - {formattedTime}
          </div>
          {item.description && (
            <div style={{ color: "#666", fontSize: "14px" }}>
              {item.description}
            </div>
          )}
        </div>
      ),
      color: item.color, // Use color directly from API response
      dot: icon,
    };
  });
};

export default function OrderTracking({ slug }) {
  const {
    data: orderDetailData,
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: ["orderDetailData", slug],
    queryFn: () => getOrderById(slug),
    enabled: !!slug,
  });
  
  const {
    data: orderTrackData,
    isLoading: isOrderTrackLoading,
    isError: isOrderTrackError,
    error: errorOrderTrack,
  } = useQuery({
    queryKey: ["orderTrackDetail", slug],
    queryFn: () => getorderstatus(slug),
    enabled: !!slug,
  });


  const cardDetail = useMemo(
    () => [
      { field: "Consignment Id:", value: slug },
      { field: "Status:", value: orderDetailData?.status },
      { field: "Customer:", value: orderDetailData?.customerName },
      { field: "Receiver:", value: orderDetailData?.receiverName },
      {
        field: "Origin:",
        value: orderDetailData?.originLocationName,
      },
      { field: "Dispatch Date:", value: orderDetailData?.dispatchDate },
      {
        field: "Destination:",
        value: orderDetailData?.destinationLocationName,
      },
      { field: "Delivery Date:", value: orderDetailData?.deliveryDate },
      { field: "Payment Status:", value: orderDetailData?.paymentStatus },
      { field: "Remaining Amount:", value: `₹ ${orderDetailData?.remainingPayment}` },
    ],
    [slug, orderDetailData]
  );

  if (!slug) {
    return (
      <PageWrapper>
        <Title title="Track Order" className="text-center" />
        <div className="text-center text-red-500 mt-4">
          Order ID is required to view order details.
        </div>
      </PageWrapper>
    );
  }

  if (isLoading) {
    return (
      <PageWrapper>
        <Title title="Track Consignment" className="text-center" />
        <div className="flex justify-center items-center mt-8">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto mb-4"></div>
            <div className="text-gray-500">Loading consignment details...</div>
          </div>
        </div>
      </PageWrapper>
    );
  }

  if (isError || !orderDetailData) {
    return (
      <PageWrapper>
        <Title title="Track Consignment" className="text-center" />
        <div className="text-center mt-8">
          <div className="text-red-500 mb-4">Failed to load consignment data.</div>
          {error && (
            <div className="text-sm text-gray-600">
              Error: {error.message || "Unknown error occurred"}
            </div>
          )}
          <button
            onClick={() => window.location.reload()}
            className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
          >
            Retry
          </button>
        </div>
      </PageWrapper>
    );
  }

  // Use actual API data instead of sample data
  const timelineItems = convertToTimelineItems(orderTrackData || []);

  return (
    <>
      <PageWrapper>
        <Title title="Track Consignment" className="text-center" />

        <div className="flex gap-4 mt-6 w-full">
          <Card
            className="cardShadow border-0 w-1/2"
            title="Consignment Information"
            styles={{
              header: { backgroundColor: "#f8f9fa", fontWeight: "bold" },
            }}
          >
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {cardDetail.map((item, index) => (
                <CardValue
                  key={`${item.field}-${index}`}
                  field={item.field}
                  value={item.value}
                />
              ))}
            </div>
          </Card>
          
          <Card
            className="cardShadow border-0 w-1/2"
            title="Consignment Tracking"
            styles={{
              header: { backgroundColor: "#f8f9fa", fontWeight: "bold" },
            }}
          >
            {isOrderTrackLoading ? (
              <div className="flex justify-center items-center py-8">
                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-500 mr-2"></div>
                <span className="text-gray-500">Loading tracking information...</span>
              </div>
            ) : isOrderTrackError ? (
              <div className="text-center py-8">
                <div className="text-red-500 mb-2">Failed to load tracking data</div>
                <div className="text-sm text-gray-600">
                  {errorOrderTrack?.message || "Unable to fetch tracking information"}
                </div>
              </div>
            ) : timelineItems.length > 0 ? (
              <Timeline
                mode="left"
                items={timelineItems}
                style={{ marginTop: "20px" }}
              />
            ) : (
              <div className="text-center py-8 text-gray-500">
                No tracking information available
              </div>
            )}
          </Card>
        </div>
      </PageWrapper>
    </>
  );
}