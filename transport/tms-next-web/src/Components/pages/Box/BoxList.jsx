"use client";
import { useState, useRef, useCallback, useMemo, memo } from "react";
import { Modal, Tooltip, Tag } from "antd";
import { toast } from "react-toastify";
import { useRouter } from "next/navigation";
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

// Import the AddEditBoxModel component
const AddBox = dynamic(() => import("@/Components/pages/Box/AddEditBoxModel"), {
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
      queryFn: async () => {
        console.log(" Fetching boxes with filters:", filterPayload);
        try {
          const data = await getAllBox(filterPayload);
          console.log(" Boxes fetched:", data?.length || 0);
          return data;
        } catch (error) {
          console.error(" Error fetching boxes:", error);
          toast.error("Failed to load boxes");
          throw error;
        }
      },
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
    console.log(" Opening edit modal for box:", id);
    setSelectedId(id);
    setIsModalOpen(true);
  }, []);

  const showModal = useCallback(() => {
    console.log(" Opening add box modal");
    setIsModalOpen(true);
  }, []);

  const handleCancel = useCallback(() => {
    console.log(" Closing modal");
    setIsModalOpen(false);
    setTimeout(() => setSelectedId(null), 300);
  }, []);

  /* ================= NAVIGATION TO MANAGE PRODUCTS ================= */
  const handleManageProducts = useCallback(
    (boxId) => {
      console.log(" Navigating to manage products for box:", boxId);
      // Try different path variations based on your routing structure
      try {
        router.push(`/Box/manage-products/${boxId}`);
      } catch (error) {
        console.error(" Navigation error:", error);
        toast.error("Failed to open manage products page");
      }
    },
    [router]
  );

  /* ================= DELETE ================= */
  const handleDeleteModal = useCallback((id = null) => {
    console.log(" Toggle delete modal for box:", id);
    setSelectedId(id);
    setDeleteModal((prev) => !prev);
  }, []);

  const confirmDelete = useCallback(async () => {
    console.log(" Confirming delete for box:", selectedId);
    try {
      await deleteBox(selectedId);
      tableRef.current?.Refetch();
      toast.success("Box deleted successfully");
    } catch (error) {
      console.error(" Delete error:", error);
      toast.error(error?.message || "Failed to delete box");
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  /* ================= REFETCH TABLE ================= */
  const refetchTable = useCallback(() => {
    console.log(" Refetching table data");
    if (tableRef.current) {
      tableRef.current.Refetch();
    }
  }, []);

  /* ================= HANDLE SUCCESSFUL BOX CREATION ================= */
  const handleBoxCreated = useCallback(
    (newBoxId) => {
      console.log(" Box created successfully:", newBoxId);
      refetchTable();
      handleCancel();
      
      // Show success message with option to add products
      Modal.confirm({
        title: "Box Created Successfully!",
        content: "Would you like to add products to this box now?",
        okText: "Add Products",
        cancelText: "Later",
        onOk: () => {
          console.log(" User chose to add products");
          handleManageProducts(newBoxId);
        },
        onCancel: () => {
          console.log(" User chose to add products later");
          toast.success("You can add products later from the Manage Products button");
        },
      });
    },
    [refetchTable, handleCancel, handleManageProducts]
  );

  /* ================= HELPERS ================= */
  const formatData = useCallback(
    (data) => {
      console.log(" Formatting data:", data?.length || 0, "boxes");
      return Array.isArray(data)
        ? data.map((item) => ({ ...item, key: item.boxId }))
        : [];
    },
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
        width: 70,
        render: (_, __, index) => (currentPage - 1) * pageSize + index + 1,
      },
      { 
        title: "Box Material", 
        dataIndex: "boxCode",
        width: 120,
      },
      { 
        title: "Box Type", 
        dataIndex: "boxName",
        width: 150,
      },
      { 
        title: "HSN Code", 
        dataIndex: "hsnCode",
        width: 100,
      },
      {
        title: "Stroage Capacity ",
        dataIndex: "totalValue",
        align: "right",
        width: 130,
        render: (v) => `₹${v?.toFixed(2) || "0.00"}`,
      },
      {
        title: "Status",
        dataIndex: "status",
        align: "center",
        width: 100,
        render: (s) => <Tag color={getStatusColor(s)}>{s}</Tag>,
      },
      {
        title: "Actions",
        align: "center",
        width: 180,
        fixed: 'right',
        render: (_, record) => {
          const isLocked = ["IN_TRANSIT", "DELIVERED"].includes(record.status);
          return (
            <div className="flex gap-2 justify-center">
              <Tooltip title={isLocked ? "Cannot manage products - Box is in transit or delivered" : "Manage Products"}>
                <Button
                  icon={<MdInventory />}
                  className="bg-[#28a745] text-white"
                  onClick={() => {
                    console.log(" Manage products clicked for:", record.boxId);
                    handleManageProducts(record.boxId);
                  }}
                  disabled={isLocked}
                />
              </Tooltip>
              <Tooltip title={isLocked ? "Cannot edit - Box is in transit or delivered" : "Edit Box"}>
                <Button
                  icon={<CiEdit />}
                  onClick={() => openUpdateModal(record.boxId)}
                  disabled={isLocked}
                />
              </Tooltip>
              <Tooltip title={isLocked ? "Cannot delete - Box is in transit or delivered" : "Delete Box"}>
                <Button
                  icon={<RiDeleteBin6Fill />}
                  type="primary"
                  className="bg-[#750014]"
                  onClick={() => handleDeleteModal(record.boxId)}
                  disabled={isLocked}
                />
              </Tooltip>
            </div>
          );
        },
      },
    ],
    [currentPage, pageSize, handleManageProducts, openUpdateModal, handleDeleteModal]
  );

  const paginationSettings = {
    pageSize,
    current: currentPage,
    showSizeChanger: true,
    pageSizeOptions: ["5", "10", "20", "50"],
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
          exportEnabled={true}
        />
      </PageWrapper>

      {isModalOpen && (
        <Modal
          open={isModalOpen}
          footer={null}
          width={800}
          onCancel={handleCancel}
          destroyOnClose
          title={<h1 className="text-2xl">{selectedId ? "Update Box" : "Add Box"}</h1>}
        >
          <AddBox
            handleCancel={handleCancel}
            ListDataRefetch={refetchTable}
            selectedId={selectedId}
            onBoxCreated={handleBoxCreated}
          />
        </Modal>
      )}

      <Modal
        title="Confirm Deletion"
        open={deleteModal}
        onCancel={handleDeleteModal}
        onOk={confirmDelete}
        okText="Delete"
        okButtonProps={{
          className: "bg-[#750014]",
          danger: true,
        }}
      >
        <p className="text-base text-gray-700">
          Are you sure you want to delete this box? This action cannot be undone.
        </p>
      </Modal>
    </>
  );
}