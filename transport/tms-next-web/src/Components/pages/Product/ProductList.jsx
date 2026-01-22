"use client";
import React, { useState } from "react";
import { Table, Space, Modal, Popconfirm } from "antd";
import { EditOutlined, DeleteOutlined } from "@ant-design/icons";
import { useQuery, useMutation } from "@tanstack/react-query";
import { getAllProduct, deleteProduct } from "@/service/product";
import AddProductModal from "./AddProductModal";
import { Button } from "@/Components/atom/Button";
import { toast } from "react-toastify";

export default function ProductList() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);

  const { data: productData, isLoading, refetch } = useQuery({
    queryKey: ["products"],
    queryFn: getAllProduct,
  });

  const deleteMutation = useMutation({
    mutationFn: deleteProduct,
    onSuccess: () => {
      toast.success("Consignment Box deleted successfully");
      refetch();
    },
    onError: (error) => {
      toast.error(error.message || "Failed to delete Consignment Box");
    },
  });

  const handleDelete = (id) => {
    deleteMutation.mutate(id);
  };

  const handleEdit = (id) => {
    setSelectedId(id);
    setIsModalOpen(true);
  };

  const handleAdd = () => {
    setSelectedId(null);
    setIsModalOpen(true);
  };

  const handleCancel = () => {
    setIsModalOpen(false);
    setSelectedId(null);
  };

  const columns = [
    {
      title: "Consignment Box ID",
      dataIndex: "productId",
      key: "productId",
    },
    {
      title: "Branch IDs",
      dataIndex: "branchIds",
      key: "branchIds",
    },
    {
      title: "Consignment Box Material",
      dataIndex: "boxCode",
      key: "boxCode",
    },
    {
      title: "Consignment Box Type",
      dataIndex: "boxName",
      key: "boxName",
    },
    {
      title: "HSN Code",
      dataIndex: "hsnCode",
      key: "hsnCode",
    },
    {
      title: "Storage Condition",
      dataIndex: "storageCondition",
      key: "storageCondition",
    },
    {
      title: "Weight",
      dataIndex: "Weight",
      key: "Weight",
    },
    {
      title: " Lenght",
      dataIndex: "length",
      key: "length",
    },
    {
      title: "Height",
      dataIndex: "height",
      key: "height",
    },
     {
      title: "Width",
      dataIndex: "width",
      key: "width",
    },
    {
      title: "Storage Capacity ",
      dataIndex: "totalValue",
      key: "totalValue",
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
    },
    {
      title: "Action",
      key: "action",
      render: (_, record) => (
        <Space size="middle">
          <Button
            onClick={() => handleEdit(record.productId)}
            className="bg-blue-500 text-white border-none hover:bg-blue-600"
          >
            <EditOutlined />
          </Button>
          <Popconfirm
            title="Delete the Consignment Box"
            description="Are you sure to delete this Consignment Box?"
            onConfirm={() => handleDelete(record.productId)}
            okText="Yes"
            cancelText="No"
          >
            <Button className="bg-red-500 text-white border-none hover:bg-red-600">
              <DeleteOutlined />
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">Consignment Box List</h1>
        <Button onClick={handleAdd}>Add Consignment Box</Button>
      </div>

      <Table
        columns={columns}
        dataSource={productData}
        loading={isLoading}
        rowKey="productId"
        scroll={{ x: true }}
        components={{
          header: {
            cell: ({ children, ...restProps }) => (
              <th
                {...restProps}
                className={`${restProps.className} bg-[#750014]! text-white!`}
              >
                {children}
              </th>
            ),
          },
        }}
      />

      <Modal
        title={selectedId ? "Edit Consignment Box" : "Add Consignment Box"}
        open={isModalOpen}
        onCancel={handleCancel}
        footer={null}
        width={800}
        // destroyOnClose
        destroyOnHidden
      >
        <AddProductModal
          handleCancel={handleCancel}
          ListDataRefetch={refetch}
          selectedId={selectedId}
        />
      </Modal>
    </div>
  );
}