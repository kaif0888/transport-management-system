"use client";

import { useState, useMemo, useCallback, useRef } from "react";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/components/atom/Title";
import { Table } from "@/Components/atom/Table";
import { toast } from "react-toastify";
import { getAllFeedback } from "@/service/feedback";
import { deleteFeedback } from "@/service/feedback";
import { Popconfirm, Button } from "antd";
import { DeleteOutlined } from "@ant-design/icons";
import { Input } from "antd";
import { SearchOutlined } from "@ant-design/icons";
export default function ShowFeedback() {
  const tableRef = useRef(null);
  const [search, setSearch] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const [filterPayload, setFilterPayload] = useState({
    limit: 0,
    filters: [],
  });

  const feedbackQuery = useMemo(
    () => ({
      queryKey: ["feedbackList", filterPayload],
      queryFn: () => getAllFeedback(filterPayload),
      onError: (error) => {
        toast.error("Failed to load feedback list: " + error.message);
      },
    }),
    [filterPayload]
  );

  const handleSearchChange = useCallback(
    (e) => {
      const value = e.target.value;
      setSearch(value);

      const newFilters = { ...filterPayload };

      if (value.length >= 2) {
        newFilters.filters = [
          {
            attribute: "feedbackModule",
            operation: "CONTAINS",
            value,
          },
        ];
      } else {
        newFilters.filters = [];
      }

      setFilterPayload(newFilters);
    },
    [filterPayload]
  );
const handleDelete = async (feedBackId) => {
  try {
    await deleteFeedback(feedBackId);
    tableRef.current?.Refetch();
    toast.success("Feedback deleted successfully");
  } catch (error) {
    toast.error("Failed to delete feedback");
  }
};
  const formatTableData = useCallback((data) => {
    return (
      data?.map((item) => ({
        ...item,
        key: item.feedBackId,
      })) || []
    );
  }, []);

  const handlePaginationChange = useCallback((page, size) => {
    setCurrentPage(page);
    setPageSize(size);
  }, []);

 const columns = useMemo(
  () => [
    {
      title: "Id",
      dataIndex: "feedBackId",
      align: "center",
    },
    {
      title: "Message",
      dataIndex: "feedBackMessage",
    },
    {
      title: "Module",
      dataIndex: "feedbackModule",
      align: "center",
    },
    {
      title: "Rating",
      dataIndex: "feedbackRating",
      align: "center",
    },
    {
      title: "Type",
      dataIndex: "feedbackType",
      align: "center",
    },{
      title: "UserName",
      dataIndex: "username",
      align: "center",
    },
    {
      title: "Action",
      align: "center",
      render: (_, record) => (
        <Popconfirm
          title="Delete Feedback"
          description="Are you sure you want to delete this feedback?"
          onConfirm={() => handleDelete(record.feedBackId)}
          okText="Yes"
          cancelText="No"
        >
          <Button
            danger
            icon={<DeleteOutlined />}
            size="small"
          />
        </Popconfirm>
      ),
    },
  ],
  [handleDelete]
);

  const paginationSettings = useMemo(
    () => ({
      pageSize,
      current: currentPage,
      showSizeChanger: true,
      pageSizeOptions: ["5", "10", "20", "50"],
      onChange: handlePaginationChange,
      onShowSizeChange: handlePaginationChange,
    }),
    [currentPage, pageSize, handlePaginationChange]
  );

 const extraHeaderContent = (
  <Input
    placeholder="Search feedback Module"
    value={search}
    onChange={handleSearchChange}
    prefix={<SearchOutlined />}
    allowClear
    style={{ width: 300 }}
  />
);

  return (
    <PageWrapper>
      <Title title="Feedback" className="text-center" />

      <Table
        ref={tableRef}
        query={feedbackQuery}
        exportEnabled={true}
        extraHeaderContent={extraHeaderContent}
        formatData={formatTableData}
        columns={columns}
        pagination={paginationSettings}
      />
    </PageWrapper>
  );
}
