"use client";
import { useRef, useMemo, useCallback } from "react";
import { useQuery } from "@tanstack/react-query";
import { Card,Tooltip } from "antd";
import Title from "@/Components/atom/Title";
import PageWrapper from "@/Data/PageWrapper";
import { Table } from "@/Components/atom/Table";
import { Button } from "@/Components/atom/Button";
import { getManifestById } from "@/service/manifest";
import { FaRegEye } from "react-icons/fa";
import { orderColumn } from "@/Data/TableColumn";
import { useRouter } from "next/navigation";

const CardValue = ({ field, value }) => (
  <div className="flex gap-2">
    <p className="text-gray-700 text-lg font-semibold">{field}</p>
    <p className="text-gray-700 text-base pt-[4px] capitalize">
      {value || "N/A"}
    </p>
  </div>
);

export default function ViewManifest({ slug }) {
  const tableRef = useRef(null);
const router = useRouter();
  const {
    data: manifestData,
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: ["manifestViewDetail", slug],
    queryFn: () => getManifestById(slug),
    enabled: !!slug,
  });


  // Create a separate query for the table orders data
  const ordersQuery = useMemo(
    () => ({
      queryKey: ["manifestOrders", slug],
      queryFn: async () => {
        if (!manifestData?.orders) return [];
        return manifestData.orders;
      },
      enabled: !!manifestData?.orders,
    }),
    [manifestData?.orders, slug]
  );

  const formatData = useCallback((data) => {
    return (
      data?.map((item, index) => ({
        ...item,
        key: item.orderId || index,
      })) || []
    );
  }, []);

  const paginationSettings = useMemo(
    () => ({
      pageSize: 4,
      showSizeChanger: false,
      hideOnSinglePage: true,
      showQuickJumper: false,
    }),
    []
  );

  const cardDetail = useMemo(
    () => [
      { field: "Manifest ID:", value: slug },
      { field: "Dispatch ID:", value: manifestData?.dispatchId },
      { field: "Origin Location:", value: manifestData?.startLocationName },
      { field: "Destination Location:", value: manifestData?.endLocationName },
      { field: "Delivery Date:", value: manifestData?.deliveryDate },
    ],
    [slug, manifestData]
  );

  if (!slug) {
    return (
      <PageWrapper>
        <Title title="View Manifest Detail" className="text-center" />
        <div className="text-center text-red-500 mt-4">
          Manifest ID is required to view manifest details.
        </div>
      </PageWrapper>
    );
  }

  if (isLoading) {
    return (
      <PageWrapper>
        <Title title="View Manifest Detail" className="text-center" />
        <div className="flex justify-center items-center mt-8">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto mb-4"></div>
            <div className="text-gray-500">Loading manifest details...</div>
          </div>
        </div>
      </PageWrapper>
    );
  }

  if (isError || !manifestData) {
    return (
      <PageWrapper>
        <Title title="View Manifest Detail" className="text-center" />
        <div className="text-center mt-8">
          <div className="text-red-500 mb-4">Failed to load manifest data.</div>
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
  function handleView(orderId){
    router.push(`/manifest/${slug}/${orderId}`)

  }
  const columns = [
    ...orderColumn,
    {
      title: "Actions",
      dataIndex: "orderId",
      key: "actions",
      className: "px-2 text-center",
      align: "center",
      render: (orderId) => (
        <div className="flex gap-2 justify-center ">
          <Tooltip title="View Order">
            <Button
              key={"manifest"}
              className="font-semibold "
              onClick={() => handleView(orderId)}
              icon={<FaRegEye className="text-lg" />}
            />
          </Tooltip>
        </div>
      ),
    },
  ];
  return (
    <PageWrapper>
      <Title title="View Manifest Detail" className="text-center" />

      <div className="flex flex-col gap-4 mt-6">
        <Card
          className="cardShadow border-0"
          title="Manifest Information"
          headStyle={{ backgroundColor: "#f8f9fa", fontWeight: "bold" }}
        >
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
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
          className="cardShadow border-0"
          title="Order Items"
          headStyle={{ backgroundColor: "#f8f9fa", fontWeight: "bold" }}
        >
          <Table
            ref={tableRef}
            query={ordersQuery}
            formatData={formatData}
            columns={columns}
            pagination={paginationSettings}
            scroll={{ x: 800 }}
            size="middle"
            dependentLoader={isLoading}
          />
        </Card>
      </div>
    </PageWrapper>
  );
}
