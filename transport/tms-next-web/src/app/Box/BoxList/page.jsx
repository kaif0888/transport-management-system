"use client";
import { useState, useRef, useCallback, useMemo, memo } from "react";
//import { Modal, Tooltip } from "antd";
import { Modal, Tooltip, Tag } from "antd";
import { toast } from "react-toastify";
import { useRouter } from "next/navigation";
//import { useRouter } from "next/router";
import { Button } from "@/Components/atom/Button";
import { getAllBox, deleteBox } from "@/service/box";
import { Table } from "@/Components/atom/Table";
import { Input } from "@/Components/atom/Input";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";
import { CiEdit } from "react-icons/ci";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { MdInventory } from "react-icons/md";
import dynamic from "next/dynamic";

const AddBox = dynamic(() => import("./AddBoxModal"), {
  ssr: false,
  loading: () => <div className="p-4">Loading form...</div>,
});

const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    value={value}
    onChange={onChange}
    placeholder="Search by Box Name"
    className="h-[35px]"
    startIcon={<SearchOutlined style={{ color: "#666", fontSize: 18 }} />}
  />
));
SearchInput.displayName = "SearchInput";

export default function BoxList() {
  const router = useRouter();
  const tableRef = useRef(null);

  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  /* ================= TABLE QUERY ================= */
  const tableQuery = useMemo(
    () => ({
      queryKey: ["boxList", filterPayload],
      queryFn: () => getAllBox(filterPayload),
      retry: 1,
    }),
    [filterPayload]
  );

  /* ================= SEARCH ================= */
  const handleSearchChange = useCallback(
    (e) => {
      const value = e.target.value;
      setSearch(value);

      const newFilters = {
        ...filterPayload,
        filters: [...filterPayload.filters],
      };

      if (value.length >= 2) {
        const index = newFilters.filters.findIndex(
          (f) => f.attribute === "boxName"
        );

        if (index >= 0) {
          newFilters.filters[index] = {
            ...newFilters.filters[index],
            value,
          };
        } else {
          newFilters.filters.push({
            attribute: "boxName",
            operation: "CONTAINS",
            value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "boxName"
        );
      }

      setFilterPayload(newFilters);
    },
    [filterPayload]
  );

  /* ================= MODALS ================= */
  const openUpdateModal = useCallback((id) => {
    setSelectedId(id);
    setIsModalOpen(true);
  }, []);

  const showModal = useCallback(() => {
    setIsModalOpen(true);
  }, []);

  const handleCancel = useCallback(() => {
    setIsModalOpen(false);
    setTimeout(() => setSelectedId(null), 300);
  }, []);

  /* ================= NAVIGATION ================= */
  const handleManageProducts = useCallback(
    (boxId) => {
      router.push(`/Box/manage-products/${boxId}`);
    },
    [router]
  );

  /* ================= DELETE ================= */
  const handleDeleteModal = useCallback((id = null) => {
    setSelectedId(id);
    setDeleteModal((prev) => !prev);
  }, []);

  const confirmDelete = useCallback(async () => {
    try {
      await deleteBox(selectedId);
      tableRef.current?.Refetch();
      toast.success("Box deleted successfully");
    } catch (error) {
      toast.error(error?.message || "Failed to delete box");
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  /* ================= HELPERS ================= */
  const formatData = useCallback(
    (data) =>
      Array.isArray(data)
        ? data.map((item) => ({ ...item, key: item.boxId }))
        : [],
    []
  );

  const getStatusColor = (status) => {
    const colors = {
      EMPTY: "default",
      PACKED: "blue",
      IN_TRANSIT: "orange",
      DELIVERED: "green",
    };
    return colors[status] || "default";
  };

  /* ================= COLUMNS ================= */
  const columns = useMemo(
    () => [
      {
        title: "S.No",
        align: "center",
        render: (_, __, index) =>
          (currentPage - 1) * pageSize + index + 1,
      },
      { title: "Box Code", dataIndex: "boxCode" },
      { title: "Box Name", dataIndex: "boxName" },
      { title: "HSN Code", dataIndex: "hsnCode" },
      {
        title: "Total Value (₹)",
        dataIndex: "totalValue",
        align: "right",
        render: (v) => `₹${v?.toFixed(2) || "0.00"}`,
      },
      {
        title: "Status",
        dataIndex: "status",
        align: "center",
        render: (s) => <Tag color={getStatusColor(s)}>{s}</Tag>,
      },
      {
        title: "Actions",
        align: "center",
        render: (_, record) => (
          <div className="flex gap-2 justify-center">
            <Tooltip title="Manage Products">
              <Button
                icon={<MdInventory />}
                onClick={() => handleManageProducts(record.boxId)}
                disabled={["IN_TRANSIT", "DELIVERED"].includes(record.status)}
              />
            </Tooltip>
            <Tooltip title="Edit Box">
              <Button
                icon={<CiEdit />}
                onClick={() => openUpdateModal(record.boxId)}
                disabled={["IN_TRANSIT", "DELIVERED"].includes(record.status)}
              />
            </Tooltip>
            <Tooltip title="Delete Box">
              <Button
                icon={<RiDeleteBin6Fill />}
                type="primary"
                className="bg-[#750014]"
                onClick={() => handleDeleteModal(record.boxId)}
                disabled={["IN_TRANSIT", "DELIVERED"].includes(record.status)}
              />
            </Tooltip>
          </div>
        ),
      },
    ],
    [currentPage, pageSize, handleManageProducts, openUpdateModal, handleDeleteModal]
  );

  const paginationSettings = {
    pageSize,
    current: currentPage,
    showSizeChanger: true,
    onChange: (p, s) => {
      setCurrentPage(p);
      setPageSize(s);
    },
  };

  const extraHeaderContent = (
    <div className="flex justify-between items-center w-full">
      <SearchInput value={search} onChange={handleSearchChange} />
      <Button
        type="primary"
        className="bg-[#750014]"
        icon={<PlusOutlined />}
        onClick={showModal}
      >
        Add Box
      </Button>
    </div>
  );

  return (
    <>
      <PageWrapper>
        <Title title="Box Management" className="text-center" />
        <Table
          ref={tableRef}
          query={tableQuery}
          columns={columns}
          formatData={formatData}
          pagination={paginationSettings}
          extraHeaderContent={extraHeaderContent}
        />
      </PageWrapper>

      {isModalOpen && (
        <Modal
          open={isModalOpen}
          footer={null}
          width={800}
          onCancel={handleCancel}
          destroyOnClose
          title={selectedId ? "Update Box" : "Add Box"}
        >
          <AddBox
            handleCancel={handleCancel}
            ListDataRefetch={() => tableRef.current?.Refetch()}
            selectedId={selectedId}
          />
        </Modal>
      )}

      <Modal
        title="Confirm Deletion"
        open={deleteModal}
        onCancel={handleDeleteModal}
        onOk={confirmDelete}
        okText="Delete"
      >
        Are you sure you want to delete this box?
      </Modal>
    </>
  );
}
