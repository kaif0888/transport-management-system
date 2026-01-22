"use client";

import { useRef, useState, useMemo, useCallback, memo, useEffect } from "react";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { getAllBranch, deleteBranch } from "@/service/branch";
import { Table } from "@/Components/atom/Table";
import { branchColumn } from "@/Data/TableColumn";
import { CiEdit } from "react-icons/ci";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { IoFilter } from "react-icons/io5";
import { Modal } from "antd";
import AddBranchModal from "./AddBranchModal";
import BranchFilter from "./BranchFilter";
import { toast } from "react-toastify";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { SearchOutlined } from "@ant-design/icons";
import { PlusOutlined } from "@ant-design/icons";
import dynamic from "next/dynamic";

// Dynamic imports for better performance
const BranchFilterDynamic = dynamic(() => import("./BranchFilter"), {
  ssr: true,
  loading: () => <div className="p-4">Loading filter...</div>,
});

// Memoize the search input component
const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="branchName"
    value={value}
    onChange={onChange}
    placeholder="Search by Branch Name"
    name="branchName"
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

export default function BranchList() {
  const tableRef = useRef(null);
  const [branchName, setBranchName] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [filterModal, setFilterModal] = useState(false);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  const [activeFilters, setActiveFilters] = useState({});
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const branchQuery = useMemo(
    () => ({
      queryKey: ["branchList", filterPayload],
      queryFn: () => getAllBranch(filterPayload),
      onError: (error) => {
        toast.error("Failed to load branch list: " + error.message);
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
  
  const handleBranchNameChange = useCallback(
    (e) => {
      const value = e.target.value;
      setBranchName(value);

      const newFilters = { ...filterPayload };

      if (value.length >= 2) {
        const branchIndex = newFilters.filters.findIndex(
          (f) => f.attribute === "branchName"
        );

        if (branchIndex >= 0) {
          newFilters.filters[branchIndex].value = value;
        } else {
          newFilters.filters.push({
            attribute: "branchName",
            operation: "CONTAINS",
            value: value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "branchName"
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

  const handleApplyFilter = useCallback(
    (payload) => {
      const branchFilter = filterPayload.filters.find(
        (f) => f.attribute === "branchName"
      );

      if (branchFilter) {
        const branchIndex = payload.filters.findIndex(
          (f) => f.attribute === "branchName"
        );
        if (branchIndex >= 0) {
          payload.filters[branchIndex] = branchFilter;
        } else {
          payload.filters.push(branchFilter);
        }
      }

      const active = {};
      if (payload.filters && payload.filters.length > 0) {
        payload.filters.forEach((filter) => {
          if (filter.attribute === "locationName") {
            active.locationName = filter.value;
          } else if (filter.attribute === "locationAddress") {
            active.locationAddress = filter.value;
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

    if (branchName.length >= 2) {
      newFilters.filters.push({
        attribute: "branchName",
        operation: "CONTAINS",
        value: branchName,
      });
    }

    setFilterPayload(newFilters);
    setActiveFilters({});
  }, [branchName]);

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
      await deleteBranch(selectedId);
      tableRef.current?.Refetch();
      toast.success("Branch deleted successfully");
    } catch (error) {
      toast.error("Failed to delete branch: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const formatBranchData = useCallback((data) => {
    return (
      data?.map((branch) => ({
        ...branch,
        key: branch.branchId,
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
      ...branchColumn,

      {
        title: "Actions",
        dataIndex: "branchId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (branchId) => (
          <div className="flex gap-4">
            <Button
              key={"edit"}
              onClick={() => openUpdateModal(branchId)}
              icon={<CiEdit />}
            />
            <Button
              key={"delete"}
              className="font-semibold bg-[#750014]"
              type="primary"
              onClick={() => handleDeleteModal(branchId)}
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
  const extraHeaderContent = useMemo(() => {
    const hasActiveAdvancedFilters = filterPayload.filters.some(
      (f) => f.attribute !== "branchName"
    );

    return (
      <div className="flex justify-between items-center w-full">
        <div className="flex gap-2 w-[400px]">
          <SearchInput value={branchName} onChange={handleBranchNameChange} />
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
            Add Branch
          </Button>
        </div>
      </div>
    );
  }, [
    branchName,
    handleBranchNameChange,
    showModal,
    filterPayload,
    clearFilters,
  ]);



  return (
    <>
      <PageWrapper>
        <Title title="Branch & Hub List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={branchQuery}
            exportEnabled={true}
            extraHeaderContent={extraHeaderContent}
            formatData={formatBranchData}
            columns={columns}
            pagination={paginationSettings}
          />
        </div>
      </PageWrapper>

      {isModalOpen && (
        <Modal
          title={
            <h1 className="text-2xl">
              {selectedId !== null ? "Update Branch" : "Add Branch"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={handleCancel}
          width="800px"
          destroyOnHidden={true}
        >
          <AddBranchModal
            handleCancel={handleCancel}
            ListDataRefetch={refetchTable}
            selectedId={selectedId}
          />
        </Modal>
      )}

      {filterModal && (
        <Modal
          title={<h1 className="text-2xl">Filter Branches</h1>}
          open={filterModal}
          onCancel={() => setFilterModal(false)}
          width="800px"
          destroyOnHidden={true}
          footer={null}
        >
          <BranchFilterDynamic
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
          className: "font-semibold bg-[#750014]",
          type: "primary",
        }}
        okText="Delete"
      >
        <p className="text-base text-gray-700">
          Are you sure you want to delete this branch?
        </p>
      </Modal>
    </>
  );
}
