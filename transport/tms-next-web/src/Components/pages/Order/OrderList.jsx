"use client";
import { useState, useRef, useCallback, useMemo, memo, useEffect } from "react";
import { Modal, Tooltip } from "antd";
import { toast } from "react-toastify";
import { useRouter } from "next/navigation";
import { orderColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { getAllOrder, deleteOrder } from "@/service/order";
import { Table } from "@/Components/atom/Table";
import { Input } from "@/Components/atom/Input";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { IoFilter } from "react-icons/io5";
import { FaRegEye } from "react-icons/fa";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";
import { IoMdAdd } from "react-icons/io";
import dynamic from "next/dynamic";
import { LuLogs } from "react-icons/lu";
import { BiLineChart } from "react-icons/bi";

const AddOrder = dynamic(
  () => import("@/Components/pages/Order/AddOrderModal"),
  {
    ssr: false,
    loading: () => <div className="p-4">Loading form...</div>,
  }
);

const OrderFilter = dynamic(
  () => import("@/Components/pages/Order/OrderFilter"),
  {
    ssr: false,
    loading: () => <div className="p-4">Loading filter...</div>,
  }
);

// Memoize the search input component
const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="search"
    value={value}
    onChange={onChange}
    placeholder="Search By ID...."
    name="search"
    className={"h-[35px]"}
    startIcon={
      <SearchOutlined
        style={{
          color: "#666",
          fontSize: "18px",
          marginTop: "2px",
        }}
      />
    }
  />
));

export default function OrderList() {
  const router = useRouter();
  const tableRef = useRef(null);
  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [filterModal, setFilterModal] = useState(false);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  const [activeFilters, setActiveFilters] = useState({});
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const tableQuery = useMemo(
    () => ({
      queryKey: ["consignmentList", filterPayload],
      queryFn: () => getAllOrder(filterPayload),
      onError: (error) => {
        toast.error("Failed to load consignment list: " + error.message);
      },
    }),
    [filterPayload]
  );
  const refetchTable = useCallback(() => {
    if (tableRef.current) {
      tableRef.current.Refetch();
    }
  }, []);

  // Refetch table data on page load
  useEffect(() => {
    refetchTable();
  }, [refetchTable]);
  const handleSearchChange = useCallback(
    (e) => {
      const value = e.target.value;
      setSearch(value);

      const newFilters = { ...filterPayload };

      if (value.length >= 2) {
        const productIndex = newFilters.filters.findIndex(
          (f) => f.attribute === "orderId"
        );

        if (productIndex >= 0) {
          newFilters.filters[productIndex].value = value;
        } else {
          newFilters.filters.push({
            attribute: "orderId",
            operation: "CONTAINS",
            value: value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "orderId"
        );
      }

      setFilterPayload(newFilters);
    },
    [filterPayload]
  );

  const openUpdateModal = useCallback((id) => {
    setSelectedId(id);
    setIsModalOpen(true);
  }, []);

  const showModal = useCallback(() => {
    setIsModalOpen(true);
  }, []);

  const handleCancel = useCallback(() => {
    setIsModalOpen(false);
    setTimeout(() => {
      setSelectedId(null);
    }, 300);
  }, []);

  const handleAddOrderProduct = (orderId) => {
    router.push(`/consignment/consignment-product/${orderId}`);
  };
//   const handleAddOrderProduct = (orderId) => {
//   router.push(`/consignment/add-box/${orderId}`);
// };
// const handleAddOrderProduct = (orderId) => {
//   // Changed from /consignment/consignment-product/${orderId}
//   router.push(`/consignment/add-Box/${orderId}`);
// };


  const handlePaymentLog = (orderId) => {
    router.push(`/consignment/payment/${orderId}`);
  };

  const handleAnalysisAndProfit = (orderId) => {
    router.push(`/consignment/analysis/${orderId}`);
  };

  const handleApplyFilter = useCallback(
    (payload) => {
      const productFilter = filterPayload.filters.find(
        (f) => f.attribute === "productName"
      );

      if (productFilter) {
        const productIndex = payload.filters.findIndex(
          (f) => f.attribute === "productName"
        );
        if (productIndex >= 0) {
          payload.filters[productIndex] = productFilter;
        } else {
          payload.filters.push(productFilter);
        }
      }

      const active = {};
      if (payload.filters && payload.filters.length > 0) {
        payload.filters.forEach((filter) => {
          if (filter.attribute === "paymentStatus") {
            active.categoryName = filter.value;
          } else if (filter.attribute === "status") {
            active.categoryName = filter.value;
          }
        });
      }

      setActiveFilters(active);
      setFilterPayload(payload);
      setFilterModal(false);
    },
    [filterPayload]
  );

  const clearFilters = useCallback(() => {
    const newFilters = { limit: 0, filters: [] };

    if (search.length >= 2) {
      newFilters.filters.push({
        attribute: "orderId",
        operation: "CONTAINS",
        value: search,
      });
    }

    setFilterPayload(newFilters);
    setActiveFilters({});
  }, [search]);

  const handleDeleteModal = useCallback(
    (id = null) => {
      setSelectedId(id);
      setDeleteModal(!deleteModal);
      if (!deleteModal === false) {
        setTimeout(() => setSelectedId(null), 300);
      }
    },
    [deleteModal]
  );

  const confirmDelete = useCallback(async () => {
    try {
      await deleteOrder(selectedId);
      tableRef.current?.Refetch();
      toast.success("Consignment deleted successfully");
    } catch (error) {
      toast.error("Failed to delete consignment: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const formatData = useCallback((data) => {
    return (
      data?.map((item) => ({
        ...item,
        key: item.orderId,
      })) || []
    );
  }, []);
  function handleView(orderId) {
    router.push(`/consignment/track/${orderId}`);
  }

  const handlePaginationChange = useCallback((page, size) => {
    setCurrentPage(page);
    setPageSize(size);
  }, []);

  const columns = useMemo(
    () => [
      {
        title: "S.No",
        dataIndex: "serialNumber",
        key: "serialNumber",
        className: "px-2 text-center",
        align: "center",
        render: (_, __, index) => (currentPage - 1) * pageSize + index + 1,
      },

      ...orderColumn,

      {
        title: "Actions",
        dataIndex: "orderId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (orderId, record) => (
          <div className="flex gap-2">
            <Tooltip title="Add Consignment Box">
              <Button
                key={"add-product"}
                className="font-semibold bg-[#28a745] text-white"
                onClick={() => handleAddOrderProduct(orderId)}
                disabled={[
                  "confirm",
                  "dispatched",
                  "delivered",
                  "in-transit",
                ]?.includes(record.status?.toLowerCase())}
                icon={<IoMdAdd />}
              />
            </Tooltip>
            <Tooltip title="View Payment log">
              <Button
                key={"payment-log"}
                className="font-semibold bg-[#28a745] text-white"
                onClick={() => handlePaymentLog(orderId)}
                disabled={["pending"]?.includes(record.status?.toLowerCase())}
                icon={<LuLogs />}
              />
            </Tooltip>
            <Tooltip title="Analysis & Profit">
              <Button
                key={"analysis-profit"}
                className="font-semibold bg-[#0066cc] text-white"
                onClick={() => handleAnalysisAndProfit(orderId)}
                icon={<BiLineChart />}
              />
            </Tooltip>
            <Tooltip title="View Order">
              <Button
                key={"track"}
                className="font-semibold"
                onClick={() => handleView(orderId)}
                icon={<FaRegEye className="text-lg" />}
              />
            </Tooltip>
          </div>
        ),
      },
    ],
    [
      openUpdateModal,
      handleDeleteModal,
      handleAddOrderProduct,
      currentPage,
      pageSize,
    ]
  );

  const paginationSettings = useMemo(
    () => ({
      pageSize: pageSize,
      current: currentPage,
      showSizeChanger: true,
      pageSizeOptions: ["5", "10", "20", "50"],
      onChange: handlePaginationChange,
      onShowSizeChange: handlePaginationChange,
    }),
    [currentPage, pageSize, handlePaginationChange]
  );

  const extraHeaderContent = useMemo(() => {
    const hasActiveAdvancedFilters = filterPayload.filters.some(
      (f) => f.attribute !== "productName"
    );

    return (
      <div className="flex justify-between items-center w-full">
        <div className="flex gap-2 w-[400px]">
          <SearchInput value={search} onChange={handleSearchChange} />
        </div>
        <div className="flex gap-2">
          <Button
            icon={<IoFilter />}
            className={`font-semibold ${
              hasActiveAdvancedFilters ? "bg-[#750014] text-white" : ""
            }`}
            onClick={() => setFilterModal(true)}
          >
            Filter {hasActiveAdvancedFilters ? "(Active)" : ""}
          </Button>
          {hasActiveAdvancedFilters && (
            <Button className="font-semibold" onClick={clearFilters}>
              Clear Filters
            </Button>
          )}
          <Button
            className="font-semibold bg-[#750014]"
            type="primary"
            onClick={showModal}
            icon={<PlusOutlined />}
          >
            Add Consignment
          </Button>
        </div>
      </div>
    );
  }, [search, handleSearchChange, showModal, filterPayload, clearFilters]);

 

  return (
    <>
      <PageWrapper>
        <Title title="Consignment List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={tableQuery}
            exportEnabled={true}
            extraHeaderContent={extraHeaderContent}
            formatData={formatData}
            columns={columns}
            scrollx={800}
            pagination={paginationSettings}
          />
        </div>
      </PageWrapper>

      {isModalOpen && (
        <Modal
          title={
            <h1 className="text-2xl">
              {selectedId !== null ? "Update Consignment" : "Add Consignment"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={handleCancel}
          width="800px"
          destroyOnHidden={true}
        >
          <AddOrder
            handleCancel={handleCancel}
            ListDataRefetch={refetchTable}
            selectedId={selectedId}
          />
        </Modal>
      )}

      {filterModal && (
        <Modal
          title={<h1 className="text-2xl">Filter Consignment</h1>}
          open={filterModal}
          onCancel={() => setFilterModal(false)}
          width="800px"
          destroyOnHidden={true}
          footer={null}
        >
          <OrderFilter
            onApplyFilter={handleApplyFilter}
            onCancel={() => setFilterModal(false)}
            initialFilters={activeFilters}
          />
        </Modal>
      )}

      <Modal
        title="Confirm Deletion"
        onCancel={() => handleDeleteModal()}
        onOk={confirmDelete}
        open={deleteModal}
        okButtonProps={{
          className: "font-semibold bg-[#750014]!",
          type: "primary",
        }}
        okText="Delete"
      >
        <p className="text-base text-gray-700">
          Are you sure you want to delete this consignment?
        </p>
      </Modal>
    </>
  );
}
