"use client";
import { useState, useRef, useCallback, useMemo, useEffect, memo } from "react";
import { Modal } from "antd";
import { toast } from "react-toastify";
import { customerColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { getAllCustomer, deleteCustomer } from "@/service/customer";
import { Input } from "@/Components/atom/Input";
import PageWrapper from "@/Data/PageWrapper";
import dynamic from "next/dynamic";
import { Table } from "@/Components/atom/Table";
import { SearchOutlined } from "@ant-design/icons";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { CiEdit } from "react-icons/ci";
import Title from "@/Components/atom/Title";
import { PlusOutlined } from "@ant-design/icons";

const AddCustomer = dynamic(
  () => import("@/Components/pages/Customer/AddCustomerModal"),
  {
    ssr: true,
    loading: () => <div className="p-4">Loading form...</div>,
  }
);

const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="customerName"
    value={value}
    onChange={onChange}
    placeholder="Search by Customer Name"
    name="customerName"
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
export default function CustomerList() {
  const tableRef = useRef(null);
  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const customerQuery = useMemo(
    () => ({
      queryKey: ["customerList", filterPayload],
      queryFn: () => getAllCustomer(filterPayload),
      onError: (error) => {
        toast.error("Failed to load customer list: " + error.message);
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
        const regIndex = newFilters.filters.findIndex(
          (f) => f.attribute === "customerName"
        );

        if (regIndex >= 0) {
          newFilters.filters[regIndex].value = value;
        } else {
          newFilters.filters.push({
            attribute: "customerName",
            operation: "CONTAINS",
            value: value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "customerName"
        );
      }

      setFilterPayload(newFilters);
    },
    [filterPayload]
  );

  const formatTableData = useCallback((data) => {
    return (
      data?.map((customer) => ({
        ...customer,
        key: customer.customerId,
      })) || []
    );
  }, []);

  const showModal = useCallback(() => {
    setIsModalOpen(true);
  }, []);

  const openUpdateModal = useCallback((id) => {
    setSelectedId(id);
    setIsModalOpen(true);
  }, []);

  const handleCancel = useCallback(() => {
    setIsModalOpen(false);
    setTimeout(() => {
      setSelectedId(null);
    }, 300);
  }, []);

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
      await deleteCustomer(selectedId);
      tableRef.current?.Refetch();
      toast.success("Location deleted successfully");
    } catch (error) {
      toast.error("Failed to delete location: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

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
        responsive: ["lg", "xl", "xxl"],
      },
      ...customerColumn,
      {
        title: "Actions",
        dataIndex: "customerId",
        key: "actions",
        width: 150,
        className: "px-2 text-center",
        align: "center",
        render: (customerId) => (
          <div className="flex gap-4 w-full justify-center">
            <Button
              key={"edit"}
              onClick={() => openUpdateModal(customerId)}
              icon={<CiEdit />}
            />
            <Button
              key={"delete"}
              className="font-semibold bg-[#750014]"
              type="primary"
              onClick={() => handleDeleteModal(customerId)}
              icon={<RiDeleteBin6Fill />}
            />
          </div>
        ),
      },
    ],
    [openUpdateModal, handleDeleteModal, currentPage, pageSize]
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

  const extraHeaderContent = useMemo(
    () => (
      <div className="flex justify-between items-center w-full">
        <div className="flex gap-2 w-[400px]">
          <SearchInput value={search} onChange={handleSearchChange} />
        </div>
        <div className="flex gap-2">
          <Button
            className="font-semibold bg-[#750014]"
            type="primary"
            onClick={showModal}
            icon={<PlusOutlined />}
          >
            Add Customer
          </Button>
        </div>
      </div>
    ),
    [search, handleSearchChange, showModal]
  );

  const refetchTable = useCallback(() => {
    if (tableRef.current) {
      tableRef.current.Refetch();
    }
  }, []);

  return (
    <>
      <PageWrapper>
        <Title title="Customer List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={customerQuery}
            exportEnabled={true}
            extraHeaderContent={extraHeaderContent}
            formatData={formatTableData}
            columns={columns}
            pagination={paginationSettings}
          />
        </div>
      </PageWrapper>

      {isModalOpen && (
        <Modal
          title={
            <h1 className="text-2xl">
              {selectedId !== null ? "Update Customer" : "Add Customer"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={handleCancel}
          width="800px"
          destroyOnHidden={true}
        >
          <AddCustomer
            handleCancel={handleCancel}
            ListDataRefetch={refetchTable}
            selectedId={selectedId}
          />
        </Modal>
      )}

      <Modal
        title="Confirm Deletion"
        onCancel={() => handleDeleteModal()}
        onOk={confirmDelete}
        open={deleteModal}
        okButtonProps={{
          className: "font-semibold bg-[#750014]",
          type: "primary",
        }}
        okText="Delete"
      >
        <p className="text-base text-gray-700">
          Are you sure you want to delete this customer?
        </p>
      </Modal>
    </>
  );
}
