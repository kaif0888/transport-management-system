"use client";

import { useParams, useRouter } from "next/navigation";
import { useMemo, useCallback } from "react";
import { Table } from "@/Components/atom/Table";
import { Button } from "@/Components/atom/Button";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { getAllBox } from "@/service/box";
// import axios from "@/service/axios";
import { toast } from "react-toastify";
import { addToOrderBox } from "@/service/box";

export default function AddBoxToConsignment() {
  const { orderId } = useParams();     // 👈 URL se orderId
  const router = useRouter();

  /* ================= BOX LIST QUERY ================= */
  const tableQuery = useMemo(() => ({
    queryKey: ["boxListForConsignment", orderId],
    queryFn: async () => {
      const result = await getAllBox({ 
        limit: 0, 
        filters: [
          { attribute: "orderId", operation: "EQUALS", value: orderId }
        ] 
      });
      console.log("getAllBox result:", result);
      return result;
    },
  }), [orderId]);

  /* ================= ADD BOX TO ORDER ================= */
  const handleAddBox = useCallback(
    async (boxId) => {
      try {
        // await axios.post(
        //   `/api/boxes/orders/${orderId}/boxes/${boxId}`
        // );
        addToOrderBox(orderId, boxId);
        toast.success("Box added to consignment successfully");
        router.back(); // pichla page (consignment list)
      } catch (error) {
        toast.error(
          error?.response?.data || "Failed to add box to consignment"
        );
      }
    },
    [orderId, router]
  );

  /* ================= TABLE COLUMNS ================= */
  const columns = [
    {
      title: "Box Material",
      dataIndex: "boxCode",
    },
    {
      title: "Box Type",
      dataIndex: "boxName",
    },
    {
      title: "Status",
      dataIndex: "status",
    },
    {
      title: "Total Value (₹)",
      dataIndex: "totalValue",
      align: "right",
      render: (v) => `₹${v?.toFixed(2) || "0.00"}`,
    },
    {
      title: "Action",
      align: "center",
      render: (_, record) => (
        <Button
          className="bg-[#28a745] text-white"
          disabled={record.status !== "PACKED"}
          onClick={() => handleAddBox(record.boxId)}
        >
          Add Box
        </Button>
      ),
    },
  ];

  return (
    <PageWrapper>
      <Title
        title={`Add Box to Consignment (${orderId})`}
        className="text-center"
      />

      <Table
        query={tableQuery}
        columns={columns}
        formatData={(data) =>
          data?.map((b) => ({ ...b, key: b.boxId })) || []
        }
      />
    </PageWrapper>
  );
}
