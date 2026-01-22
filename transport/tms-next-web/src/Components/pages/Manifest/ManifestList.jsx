// Fixed ManifestList Component
"use client";
import { useState, useRef, useCallback, useMemo, memo, useEffect } from "react";
import { Modal, Tooltip } from "antd";
import { toast } from "react-toastify";
import { manifestcolumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { getAllManifest, deleteManifest } from "@/service/manifest";
import { Table } from "@/Components/atom/Table";
import { Input } from "@/Components/atom/Input";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { useRouter } from "next/navigation";
import { CiEdit } from "react-icons/ci";
import { IoFilter } from "react-icons/io5";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";
import { FaRegEye } from "react-icons/fa";
import dynamic from "next/dynamic";

const AddManifest = dynamic(
  () => import("@/Components/pages/Manifest/AddManifestModal"),
  {
    ssr: false,
    loading: () => <div className="p-4">Loading form...</div>,
  }
);

const ManifestFilter = dynamic(
  () => import("@/Components/pages/Manifest/ManifestFilter"),
  {
    ssr: false,
    loading: () => <div className="p-4">Loading filter...</div>,
  }
);

const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    value={value}
    onChange={onChange}
    placeholder="Search by Vehicle Number..."
    className="h-[35px]"
    startIcon={<SearchOutlined style={{ color: "#666", fontSize: "18px" }} />}
  />
));

export default function ManifestList() {
  const tableRef = useRef(null);
  const router = useRouter();
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

  // Debounce search to avoid too many API calls
  const [debouncedSearch, setDebouncedSearch] = useState("");

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 300); // 300ms debounce

    return () => clearTimeout(timer);
  }, [search]);

  // Update filter payload when debounced search changes
  useEffect(() => {
    setFilterPayload((prevPayload) => {
      const newPayload = { ...prevPayload };

      // Remove existing search filter
      newPayload.filters = newPayload.filters.filter(
        (f) => f.attribute !== "dispatch.dispatchId"
      );

      // Add new search filter if search term is long enough
      if (debouncedSearch.trim().length >= 2) {
        newPayload.filters.push({
          attribute: "dispatch.vehiclNumber",
          operation: "CONTAINS",
          value: debouncedSearch.trim(),
        });
      }

      return newPayload;
    });
  }, [debouncedSearch]);

  const tableQuery = useMemo(
    () => ({
      queryKey: ["getAllManifest", filterPayload],
      queryFn: () => getAllManifest(filterPayload), // Pass filterPayload to API
      onError: (error) => {
        toast.error("Failed to load manifest list: " + error.message);
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

  const handleSearchChange = useCallback((e) => {
    const value = e.target.value;
    setSearch(value);
  }, []);

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
      // Preserve search filter when applying other filters
      const searchFilter = filterPayload.filters.find(
        (f) => f.attribute === "dispatch.dispatchId"
      );

      if (searchFilter) {
        const searchIndex = payload.filters.findIndex(
          (f) => f.attribute === "dispatch.dispatchId"
        );
        if (searchIndex >= 0) {
          payload.filters[searchIndex] = searchFilter;
        } else {
          payload.filters.push(searchFilter);
        }
      }

      // Build active filters for display
      const active = {};
      if (payload.filters && payload.filters.length > 0) {
        payload.filters.forEach((filter) => {
          if (filter.attribute === "deliveryDate") {
            active.deliveryDate = filter.value;
          }
          // Add other filter attributes as needed
        });
      }

      setActiveFilters(active);
      setFilterPayload(payload);
      setFilterModal(false);
    },
    [filterPayload.filters]
  );

  const clearFilters = useCallback(() => {
    const newFilters = { limit: 0, filters: [] };

    // Keep search filter when clearing other filters
    if (debouncedSearch.trim().length >= 2) {
      newFilters.filters.push({
        attribute: "dispatch.vehiclNumber",
        operation: "CONTAINS",
        value: debouncedSearch.trim(),
      });
    }

    setFilterPayload(newFilters);
    setActiveFilters({});
  }, [debouncedSearch]);

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
      await deleteManifest(selectedId);
      tableRef.current?.Refetch();
      toast.success("Manifest deleted successfully");
    } catch (error) {
      toast.error("Failed to delete manifest: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const formatData = useCallback((data) => {
    return (
      data?.map((item) => ({
        ...item,
        key: item.manifestId,
      })) || []
    );
  }, []);

  const handleViewManifest = useCallback(
    (id) => {
      router.push(`/manifest/${id}`);
    },
    [router]
  );

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
      ...manifestcolumn,

      {
        title: "Actions",
        dataIndex: "manifestId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (manifestId) => (
          <div className="flex gap-2 justify-center ">
            <Tooltip title="Edit Manifest">
              <Button
                key={"edit"}
                onClick={() => openUpdateModal(manifestId)}
                icon={<CiEdit />}
              />
            </Tooltip>
            <Tooltip title="Delete Manifest">
              <Button
                key={"delete"}
                className="font-semibold bg-[#750014]"
                type="primary"
                onClick={() => handleDeleteModal(manifestId)}
                icon={<RiDeleteBin6Fill />}
              />
            </Tooltip>
            <Tooltip title="View Manifest">
              <Button
                key={"manifest"}
                className="font-semibold "
                onClick={() => handleViewManifest(manifestId)}
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
      handleViewManifest,
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
      (f) => f.attribute !== "dispatch.dispatchId"
    );

    return (
      <div className="flex justify-between items-center w-full">
        <div className="flex gap-2 w-[400px]">
          <SearchInput value={search} onChange={handleSearchChange} />
        </div>
        <div className="flex gap-2">
          {/* <Button
            icon={<IoFilter />}
            className={`font-semibold ${
              hasActiveAdvancedFilters ? "bg-[#750014] text-white" : ""
            }`}
            onClick={() => setFilterModal(true)}
          >
            Filter {hasActiveAdvancedFilters ? "(Active)" : ""}
          </Button> */}
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
            Add Manifest
          </Button>
        </div>
      </div>
    );
  }, [
    search,
    handleSearchChange,
    filterPayload.filters,
    clearFilters,
    showModal,
  ]);

  return (
    <>
      <PageWrapper>
        <Title title="Manifest List" className="text-center" />
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

      {isModalOpen && (
        <Modal
          title={
            <h1 className="text-2xl">
              {selectedId !== null ? "Update Manifest" : "Add Manifest"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={() => handleCancel()}
          width="800px"
          destroyOnHidden={true}
        >
          <AddManifest
            handleCancel={() => handleCancel()}
            ListDataRefetch={() => tableRef.current?.Refetch()}
            selectedId={selectedId}
          />
        </Modal>
      )}

      {filterModal && (
        <Modal
          title={<h1 className="text-2xl">Filter Manifest</h1>}
          open={filterModal}
          onCancel={() => setFilterModal(false)}
          width="500px"
          destroyOnHidden={true}
          footer={null}
        >
          <ManifestFilter
            onApplyFilter={handleApplyFilter}
            onCancel={() => setFilterModal(false)}
            initialFilters={activeFilters}
          />
        </Modal>
      )}

      {deleteModal && (
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
            Are you sure you want to delete this manifest?
          </p>
        </Modal>
      )}
    </>
  );
}
