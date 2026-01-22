"use client";
import { useState, useRef, useCallback, useMemo, memo, useEffect } from "react";
import { Modal } from "antd";
import { toast } from "react-toastify";
import { VehicleColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { getAllVehicle } from "@/service/vehicle";
import { Input } from "@/Components/atom/Input";
import PageWrapper from "@/Data/PageWrapper";
import dynamic from "next/dynamic";
import { Table } from "@/Components/atom/Table";
import {
  SearchOutlined,
  UploadOutlined,
  PlusOutlined,
} from "@ant-design/icons";
import { IoFilter } from "react-icons/io5";
import { CiEdit } from "react-icons/ci";
import Title from "@/Components/atom/Title";

// Optimize dynamic imports with better lazy loading
const AddVehicle = dynamic(
  () => import("@/Components/pages/Vehicle/AddVehcile"),
  {
    ssr: false,
    loading: () => <div className="p-4 text-center">Loading form...</div>,
  }
);

const ImportVehicle = dynamic(
  () => import("@/Components/pages/Vehicle/ImportVehicle"),
  {
    ssr: false,
    loading: () => <div className="p-4 text-center">Loading uploader...</div>,
  }
);

const VehicleFilter = dynamic(
  () => import("@/Components/pages/Vehicle/VehicleFilter"),
  {
    ssr: false,
    loading: () => <div className="p-4 text-center">Loading filter...</div>,
  }
);

// Extract and memoize search component
const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="registrationNumber"
    value={value}
    onChange={onChange}
    placeholder="Search by Registration Number"
    name="registrationNumber"
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

// Memoize action button to prevent recreation
const ActionButton = memo(({ vehicleId, onClick }) => (
  <Button onClick={() => onClick(vehicleId)} icon={<CiEdit />} />
));

function VehicleList() {
  const tableRef = useRef(null);
  const [regNo, setRegNo] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportModalOpen, setIsImportModalOpen] = useState(false); // New state for import modal
  const [selectedId, setSelectedId] = useState(null);
  const [filterModal, setFilterModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 10, filters: [] });
  const [activeFilters, setActiveFilters] = useState({});
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // Optimize query with stable reference
  const vehicleQuery = useMemo(
    () => ({
      queryKey: ["vehicleList", JSON.stringify(filterPayload)], // Use JSON.stringify for stable comparison
      queryFn: () => getAllVehicle(filterPayload),
      onError: (error) => {
        toast.error("Failed to load vehicle list: " + error.message);
      },
      staleTime: 5 * 60 * 1000, // 5 minutes cache
      cacheTime: 10 * 60 * 1000, // 10 minutes cache
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

  // Debounce search to reduce API calls
  const handleRegNoChange = useCallback((e) => {
    const value = e.target.value;
    setRegNo(value);

    // Clear existing timeout
    if (handleRegNoChange.timeoutId) {
      clearTimeout(handleRegNoChange.timeoutId);
    }

    // Set new timeout for debounced search
    handleRegNoChange.timeoutId = setTimeout(() => {
      setFilterPayload((prevPayload) => {
        const newFilters = { ...prevPayload };

        if (value.length >= 2) {
          const regIndex = newFilters.filters.findIndex(
            (f) => f.attribute === "registrationNumber"
          );

          if (regIndex >= 0) {
            newFilters.filters[regIndex].value = value;
          } else {
            newFilters.filters.push({
              attribute: "registrationNumber",
              operation: "CONTAINS",
              value: value,
            });
          }
        } else {
          newFilters.filters = newFilters.filters.filter(
            (f) => f.attribute !== "registrationNumber"
          );
        }

        return newFilters;
      });
    }, 300); // 300ms debounce
  }, []);

  const formatVehicleData = useCallback((data) => {
    if (!data) return [];
    return data.map((vehicle) => ({
      ...vehicle,
      key: vehicle.vehicleId,
    }));
  }, []);

  const showModal = useCallback(() => {
    setIsModalOpen(true);
  }, []);

  const showImportModal = useCallback(() => {
    setIsImportModalOpen(true);
  }, []);

  const openUpdateModal = useCallback((id) => {
    setSelectedId(id);
    setIsModalOpen(true);
  }, []);

  const handleCancel = useCallback(() => {
    setIsModalOpen(false);
    // Use setTimeout to prevent state conflicts
    const timeoutId = setTimeout(() => {
      setSelectedId(null);
    }, 300);

    return () => clearTimeout(timeoutId);
  }, []);

  const handleImportModalCancel = useCallback(() => {
    setIsImportModalOpen(false);
  }, []);

  const handleApplyFilter = useCallback(
    (payload) => {
      const regFilter = filterPayload.filters.find(
        (f) => f.attribute === "registrationNumber"
      );

      if (regFilter) {
        const regIndex = payload.filters.findIndex(
          (f) => f.attribute === "registrationNumber"
        );
        if (regIndex >= 0) {
          payload.filters[regIndex] = regFilter;
        } else {
          payload.filters.push(regFilter);
        }
      }

      // Optimize active filters calculation
      const active = payload.filters.reduce((acc, filter) => {
        if (filter.attribute === "model") {
          acc.model = filter.value;
        } else if (filter.attribute === "company") {
          acc.company = filter.value;
        } else if (filter.attribute === "capacity") {
          if (!acc.capacity) acc.capacity = [];
          if (filter.operation === "GREATER_THAN_OR_EQUAL") {
            acc.capacity[0] = parseInt(filter.value);
          } else if (filter.operation === "LESS_THAN_OR_EQUAL") {
            acc.capacity[1] = parseInt(filter.value);
          }
        }
        return acc;
      }, {});

      setActiveFilters(active);
      setFilterPayload(payload);
      setFilterModal(false);
    },
    [filterPayload.filters]
  );

  const clearFilters = useCallback(() => {
    const newFilters = { limit: 0, filters: [] };

    if (regNo.length >= 2) {
      newFilters.filters.push({
        attribute: "registrationNumber",
        operation: "CONTAINS",
        value: regNo,
      });
    }

    setFilterPayload(newFilters);
    setActiveFilters({});
  }, [regNo]);

  // Handler for pagination changes
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
      ...VehicleColumn,
     
      {
        title: "Actions",
        dataIndex: "vehicleId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (vehicleId) => (
          <ActionButton vehicleId={vehicleId} onClick={openUpdateModal} />
        ),
      },
    ],
    [openUpdateModal, currentPage, pageSize]
  );

  const paginationSettings = useMemo(
    () => ({
      pageSize: pageSize,
      current: currentPage,
      showSizeChanger: true,
      pageSizeOptions: ["5","10", "20", "50"],
      onChange: handlePaginationChange,
      onShowSizeChange: handlePaginationChange,
    }),
    [currentPage, pageSize, handlePaginationChange]
  );

  const extraHeaderContent = useMemo(() => {
    const hasActiveAdvancedFilters = filterPayload.filters.some(
      (f) => f.attribute !== "registrationNumber"
    );

    return (
      <div className="flex justify-between items-center w-full">
        <div className="flex gap-2 w-[400px]">
          <SearchInput value={regNo} onChange={handleRegNoChange} />
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
            className="font-semibold bg-[#007bff]"
            type="primary"
            onClick={showImportModal}
            icon={<UploadOutlined />}
          >
            Import
          </Button>
          <Button
            className="font-semibold bg-[#750014]"
            type="primary"
            onClick={showModal}
            icon={<PlusOutlined />}
          >
            Add Vehicle
          </Button>
        </div>
      </div>
    );
  }, [
    regNo,
    handleRegNoChange,
    showModal,
    showImportModal,
    filterPayload.filters,
    clearFilters,
  ]);

  return (
    <PageWrapper>
      <Title title="Vehicle List" className="text-center" />
      <div className="flex flex-col gap-4">
        <Table
          ref={tableRef}
          query={vehicleQuery}
          exportEnabled={true}
          extraHeaderContent={extraHeaderContent}
          formatData={formatVehicleData}
          columns={columns}
          pagination={paginationSettings}
        />
      </div>

      {isModalOpen && (
        <Modal
          title={
            <h1 className="text-2xl">
              {selectedId !== null ? "Update Vehicle" : "Add Vehicle"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={() => handleCancel()}
          width="800px"
          destroyOnHidden={true}
        >
          <AddVehicle
            handleCancel={handleCancel}
            vehicleListDataRefetch={() => tableRef.current?.Refetch()}
            selectedId={selectedId}
          />
        </Modal>
      )}

      {isImportModalOpen && (
        <Modal
          title={<h1 className="text-2xl">Import Vehicles from CSV</h1>}
          open={isImportModalOpen}
          footer={null}
          onCancel={handleImportModalCancel}
          width="600px"
          destroyOnClose={true}
        >
          <ImportVehicle
            handleCancel={handleImportModalCancel}
            vehicleListDataRefetch={() => tableRef.current?.Refetch()}
          />
        </Modal>
      )}

      {filterModal && (
        <Modal
          title={<h1 className="text-2xl">Filter Vehicles</h1>}
          open={filterModal}
          onCancel={() => setFilterModal(false)}
          width="800px"
          destroyOnHidden={true}
          footer={null}
          maskClosable={false}
        >
          <VehicleFilter
            onApplyFilter={handleApplyFilter}
            onCancel={() => setFilterModal(false)}
            initialFilters={activeFilters}
          />
        </Modal>
      )}
    </PageWrapper>
  );
}

export default memo(VehicleList);
