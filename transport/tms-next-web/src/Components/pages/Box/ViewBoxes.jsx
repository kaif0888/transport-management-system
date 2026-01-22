"use client";
import { useRef, useMemo, useCallback, useState } from "react";
import Title from "@/Components/atom/Title";
import PageWrapper from "@/Data/PageWrapper";
import { Card } from "antd";
import { Table } from "@/Components/atom/Table";
import { getOrderById } from "@/service/order";
import { getBoxesForOrder } from "@/service/box";
import { useQuery } from "@tanstack/react-query";

export default function ViewConsignmentBoxes({ slug }) {
  const tableRef = useRef(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const { data: orderData, isLoading: isOrderLoading } = useQuery({
    queryKey: ["orderDetail", slug],
    queryFn: () => getOrderById(slug),
    enabled: !!slug,
  });

  const { data: boxesData, isLoading: isBoxesLoading } = useQuery({
    queryKey: ["orderBoxes", slug],
    queryFn: () => getBoxesForOrder(slug),
    enabled: !!slug,
  });

  const columns = useMemo(() => [
    {
      title: "S.No",
      align: "center",
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1,
    },
    { title: "Box Code", dataIndex: "boxCode" },
    { title: "Box Name", dataIndex: "boxName" },
    { title: "HSN Code", dataIndex: "hsnCode" },
    {
      title: "Total Value (₹)",
      dataIndex: "totalValue",
      render: (v) => `₹${v?.toFixed(2) || "0.00"}`,
    },
    { title: "Status", dataIndex: "status" },
  ], [currentPage, pageSize]);

  if (isOrderLoading || isBoxesLoading) {
    return <PageWrapper><div>Loading...</div></PageWrapper>;
  }

  return (
    <PageWrapper>
      <Title title="View Consignment Boxes" className="text-center" />
      
      <Card title="Consignment Information">
        <p>Consignment ID: {slug}</p>
        <p>Customer: {orderData?.customerName}</p>
        {/* Add more fields */}
      </Card>

      <Card title="Boxes in Consignment" className="mt-6">
        <Table
          dataSource={boxesData}
          columns={columns}
          rowKey="boxId"
        />
      </Card>
    </PageWrapper>
  );
}