"use client";

import { useRef, useState, useMemo, useCallback, memo, useEffect } from "react";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import {
  getAllOrderProducts,
  deleteOrderProducts,
} from "@/service/order-product";
import { Table } from "@/Components/atom/Table";
import { orderProductColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { IoFilter } from "react-icons/io5";
import { Modal, Tooltip } from "antd";
import { toast } from "react-toastify";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { SearchOutlined } from "@ant-design/icons";
import dynamic from "next/dynamic";

const OrderProductFilter = dynamic(() => import("./OrderProductFilter"), {
  ssr: false,
  loading: () => <div className="p-4">Loading filter...</div>,
});

const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="order.orderId"
    value={value}
    onChange={onChange}
    placeholder="Search by consignment Id..."
    name="order.orderId"
    className="h-[35px]"
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

export default function OrderProduct() {
  const tableRef = useRef(null);
  const [search, setSearch] = useState("");
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
      queryKey: ["orderProductList", filterPayload],
      queryFn: () => getAllOrderProducts(filterPayload),
      onError: (error) => {
        toast.error(
          "Failed to load consignment product list: " + error.message
        );
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
        const searchIndex = newFilters.filters.findIndex(
          (f) => f.attribute === "order.orderId"
        );

        if (searchIndex >= 0) {
          newFilters.filters[searchIndex].value = value;
        } else {
          newFilters.filters.push({
            attribute: "order.orderId",
            operation: "CONTAINS",
            value: value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "order.orderId"
        );
      }

      setFilterPayload(newFilters);
    },
    [filterPayload]
  );

  const handleApplyFilter = useCallback(
    (payload) => {
      const searchFilter = filterPayload.filters.find(
        (f) => f.attribute === "order.orderId"
      );

      if (searchFilter) {
        const searchIndex = payload.filters.findIndex(
          (f) => f.attribute === "order.orderId"
        );
        if (searchIndex >= 0) {
          payload.filters[searchIndex] = searchFilter;
        } else {
          payload.filters.push(searchFilter);
        }
      }

      const active = {};
      if (payload.filters && payload.filters.length > 0) {
        payload.filters.forEach((filter) => {
          if (filter.attribute === "productName") {
            active.productName = filter.value;
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
        attribute: "order.orderId",
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
      await deleteOrderProducts(selectedId);
      tableRef.current?.Refetch();
      toast.success("Product deleted successfully");
    } catch (error) {
      toast.error("Failed to delete consignment product: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const formatData = useCallback((data) => {
    return (
      data?.map((item) => ({
        ...item,
        key: item.orderProductId,
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
        title: "S.No",
        dataIndex: "serialNumber",
        key: "serialNumber",
        className: "px-2 text-center",
        align: "center",
        render: (_, __, index) => (currentPage - 1) * pageSize + index + 1,
      },
      ...orderProductColumn,

      {
        title: "Actions",
        dataIndex: "orderProductId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (orderProductId) => (
          <div className="flex gap-2 justify-center">
            <Tooltip title="Delete Consignment Product">
              <Button
                key="delete"
                className="font-semibold bg-[#750014]"
                type="primary"
                onClick={() => handleDeleteModal(orderProductId)}
                icon={<RiDeleteBin6Fill />}
              />
            </Tooltip>
          </div>
        ),
      },
    ],
    [handleDeleteModal, currentPage, pageSize]
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
      (f) => f.attribute !== "order.orderId"
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
        </div>
      </div>
    );
  }, [search, handleSearchChange, filterPayload, clearFilters]);

  const refetchTable = useCallback(() => {
    if (tableRef.current) {
      tableRef.current.Refetch();
    }
  }, []);

  // Refetch table data on page load
  useEffect(() => {
    refetchTable();
  }, [refetchTable]);

  return (
    <>
      <PageWrapper>
        <Title title="Consignment Product List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={tableQuery}
            exportEnabled={true}
            extraHeaderContent={extraHeaderContent}
            formatData={formatData}
            columns={columns}
            pagination={paginationSettings}
          />
        </div>
      </PageWrapper>

      {filterModal && (
        <Modal
          title={<h1 className="text-2xl">Filter Consignment Product</h1>}
          open={filterModal}
          onCancel={() => setFilterModal(false)}
          width="500px"
          destroyOnHidden={true}
          footer={null}
        >
          <OrderProductFilter
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
          Are you sure you want to delete this order product?
        </p>
      </Modal>
    </>
  );
}
