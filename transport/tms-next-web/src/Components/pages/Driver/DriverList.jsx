"use client";
import { useState, useRef, useCallback, useMemo, memo, useEffect } from "react";
import { Modal } from "antd";
import { Select } from "@/Components/atom/Select";
import { toast } from "react-toastify";
import { DriverColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import {
  getAllDriver,
  deleDriver,
  assignVechileDriver,
  unAssignVechileDriver,
} from "@/service/driver";
import { useQuery } from "@tanstack/react-query";
import { getAvalaibleVehicle, getAllVehicle } from "@/service/vehicle";
import { Input } from "@/Components/atom/Input";
import { Table } from "@/Components/atom/Table";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { SearchOutlined } from "@ant-design/icons";
import { IoFilter } from "react-icons/io5";
import { CiEdit } from "react-icons/ci";
import dynamic from "next/dynamic";
import { PlusOutlined } from "@ant-design/icons";
import { RiDeleteBin6Fill } from "react-icons/ri";

const AddDriverModal = dynamic(
  () => import("@/Components/pages/Driver/AddDriver"),
  {
    ssr: true,
    loading: () => <div className="p-4">Loading form...</div>,
  }
);

const DriverFilter = dynamic(
  () => import("@/Components/pages/Driver/DriverFilter"),
  {
    ssr: true,
    loading: () => <div className="p-4">Loading filter...</div>,
  }
);

// Memoized search input component
const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="name"
    value={value}
    onChange={onChange}
    placeholder="Search by Driver Name"
    name="name"
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

export default function Driver() {
  const tableRef = useRef(null);
  const isProcessingRef = useRef(false);
  const [fullName, setFullName] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [assigningVehicle, setAssigningVehicle] = useState(false);
  const [filterModal, setFilterModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  const [activeFilters, setActiveFilters] = useState({});
  
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // New state for assign/unassign confirmation modals
  const [assignConfirmModal, setAssignConfirmModal] = useState(false);
  const [unassignConfirmModal, setUnassignConfirmModal] = useState(false);
  const [pendingAssignment, setPendingAssignment] = useState({
    vehicleId: null,
    driverId: null,
    vehicleLabel: "",
  });
  const [pendingUnassignment, setPendingUnassignment] = useState({
    vehicleId: null,
    driverId: null,
  });

  const {
    data: availableVehicles,
    isLoading: vehiclesLoading,
    refetch: refetchVehicles,
  } = useQuery({
    queryKey: ["available"],
    queryFn: getAvalaibleVehicle,
  });

  const { data: allVehicles } = useQuery({
    queryKey: ["allVehicles"],
    queryFn: () => getAllVehicle({ limit: 0, filters: [] }),
  });

  const allVehiclesMap = useMemo(() => {
    if (!allVehicles) return new Map();
    return new Map(allVehicles.map((v) => [v.vehicleId, v]));
  }, [allVehicles]);

  const driverQuery = useMemo(
    () => ({
      queryKey: ["DriverList", filterPayload],
      queryFn: () => getAllDriver(filterPayload),
      onError: (error) =>
        toast.error("Failed to load driver list: " + error.message),
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

  const vehicleOptions = useMemo(
    () =>
      availableVehicles?.map((vehicle) => ({
        label: `${vehicle.vehiclNumber} (${vehicle.model})`,
        value: vehicle.vehicleId,
      })) || [],
    [availableVehicles]
  );

  const handleModal = useCallback(
    (id = null) => {
      setSelectedId(id);
      setIsModalOpen((prev) => !prev);
      if (isModalOpen && id === null) {
        setTimeout(() => setSelectedId(null), 300);
      }
    },
    [isModalOpen]
  );

  const formatDriverData = useCallback((data) => {
    return (
      data?.map((driver) => ({
        ...driver,
        key: driver.driverId,
      })) || []
    );
  }, []);

  const handleDeleteModal = useCallback(
    (id = null) => {
      setSelectedId(id);
      setDeleteModal((prev) => !prev);
      if (deleteModal) {
        setTimeout(() => setSelectedId(null), 300);
      }
    },
    [deleteModal]
  );

  const confirmDelete = useCallback(async () => {
    try {
      if (selectedId !== null) {
        await handleVehicleUnassign(selectedId);
        await deleDriver(selectedId);
      } else {
        await deleDriver(selectedId);
      }
      tableRef.current?.Refetch();
      toast.success("Driver deleted successfully");
    } catch (error) {
      toast.error("Failed to delete driver: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  // Handle assign confirmation modal
  const handleAssignConfirmModal = useCallback(
    (vehicleId = null, driverId = null, vehicleLabel = "") => {
      setPendingAssignment({ vehicleId, driverId, vehicleLabel });
      setAssignConfirmModal((prev) => !prev);
    },
    []
  );

  // Handle unassign confirmation modal
  const handleUnassignConfirmModal = useCallback(
    (vehicleId = null, driverId = null) => {
      setPendingUnassignment({ vehicleId, driverId });
      setUnassignConfirmModal(!unassignConfirmModal);
    },
    []
  );

  const handleNameChange = useCallback(
    (e) => {
      const value = e.target.value;
      setFullName(value);

      const newFilters = { ...filterPayload };

      if (value.length >= 2) {
        const nameIndex = newFilters.filters.findIndex(
          (f) => f.attribute === "name"
        );

        if (nameIndex >= 0) {
          newFilters.filters[nameIndex].value = value;
        } else {
          newFilters.filters.push({
            attribute: "name",
            operation: "CONTAINS",
            value: value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "name"
        );
      }

      setFilterPayload(newFilters);
    },
    [filterPayload]
  );

  // Vehicle unassign function that uses pendingUnassignment state
  const confirmUnassignVehicle = useCallback(async () => {
    const { vehicleId, driverId } = pendingUnassignment;
    if (isProcessingRef.current || !vehicleId || !driverId) return;

    try {
      isProcessingRef.current = true;
      setAssigningVehicle(true);
      await unAssignVechileDriver(driverId, vehicleId);
      refetchVehicles();
      tableRef.current?.Refetch();
      toast.success("Vehicle unassigned successfully");
      setUnassignConfirmModal(!unassignConfirmModal);
    } catch (error) {
      toast.error("Failed to unassign vehicle: " + error.message);
    } finally {
      setAssigningVehicle(false);
      isProcessingRef.current = false;
    }
  }, [pendingUnassignment, refetchVehicles]);

  // Confirm assign vehicle
  const confirmAssignVehicle = useCallback(async () => {
    const { vehicleId, driverId } = pendingAssignment;
    if (isProcessingRef.current) return;
    try {
      isProcessingRef.current = true;
      setAssigningVehicle(true);
      await assignVechileDriver(driverId, vehicleId);
      refetchVehicles();
      tableRef.current?.Refetch();
      toast.success("Vehicle assigned successfully");
    } catch (error) {
      toast.error("Failed to assign vehicle: " + error.message);
    } finally {
      setAssigningVehicle(false);
      isProcessingRef.current = false;
      handleAssignConfirmModal();
    }
  }, [pendingAssignment, refetchVehicles, handleAssignConfirmModal]);

  // Handle vehicle assignment/unassignment
  const handleVehicleAssign = useCallback(
    async (vehicleId, driverId, record) => {
      if (!vehicleId) {
        // When clearing (unassigning), use the current assigned vehicle ID
        handleUnassignConfirmModal(record.assignedVehicleId, driverId);
        return;
      }

      const selectedVehicle = allVehiclesMap.get(vehicleId);
      const vehicleLabel = selectedVehicle
        ? `${selectedVehicle.vehicleNumber || selectedVehicle.vehiclNumber} (${selectedVehicle.model})`
        : "";
      handleAssignConfirmModal(vehicleId, driverId, vehicleLabel);
    },
    [allVehiclesMap, handleAssignConfirmModal, handleUnassignConfirmModal]
  );

  // Handle clear action specifically
  const handleVehicleClear = useCallback(
    (record) => {
      if (record?.assignedVehicleId) {
        handleUnassignConfirmModal(record.assignedVehicleId, record.driverId);
      }
    },
    [handleUnassignConfirmModal]
  );

  const handleApplyFilter = useCallback(
    (payload) => {
      const nameFilter = filterPayload.filters.find(
        (f) => f.attribute === "name"
      );

      if (nameFilter) {
        const nameIndex = payload.filters.findIndex(
          (f) => f.attribute === "name"
        );
        if (nameIndex >= 0) {
          payload.filters[nameIndex] = nameFilter;
        } else {
          payload.filters.push(nameFilter);
        }
      }

      const active = {};
      if (payload.filters && payload.filters.length > 0) {
        payload.filters.forEach((filter) => {
          if (filter.attribute === "licenseNumber") {
            active.licenseNumber = filter.value;
          } else if (filter.attribute === "contactNumber") {
            active.contactNumber = filter.value;
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

    if (fullName.length >= 2) {
      newFilters.filters.push({
        attribute: "name",
        operation: "CONTAINS",
        value: fullName,
      });
    }

    setFilterPayload(newFilters);
    setActiveFilters({});
  }, [fullName]);

  // Handler for pagination changes
  const handlePaginationChange = useCallback((page, size) => {
    setCurrentPage(page);
    setPageSize(size);
  }, []);

  const columns = useMemo(
    () => [
      // Updated Serial Number column with pagination-aware numbering
      {
        title: "S.No",
        dataIndex: "serialNumber",
        key: "serialNumber",
        className: "px-2 text-center",
        align: "center",
        render: (_, __, index) => (currentPage - 1) * pageSize + index + 1,
      },
      // Rest of the DriverColumn (excluding the original S.No column)
      ...DriverColumn,
     
      {
        title: "Assigned Vehicle",
        dataIndex: "assignedVehicleId",
        key: "assignedVehicleId",
        className: "px-2 text-center",
        align: "center",
        render: (assignedVehicleId, record) => {
          const rowVehicleOptions = [...vehicleOptions];

          if (assignedVehicleId && allVehiclesMap.has(assignedVehicleId)) {
            const isAssignedVehicleInOptions = rowVehicleOptions.some(
              (v) => v.value === assignedVehicleId
            );

            if (!isAssignedVehicleInOptions) {
              const assignedVehicleDetails = allVehiclesMap.get(assignedVehicleId);
              if (assignedVehicleDetails) {
                rowVehicleOptions.push({
                  label: `${assignedVehicleDetails.vehicleNumber || assignedVehicleDetails.vehiclNumber} (${assignedVehicleDetails.model})`,
                  value: assignedVehicleDetails.vehicleId,
                });
              }
            }
          }

          return (
            <Select
              value={assignedVehicleId ?? undefined}
              fieldWidth="200px"
              loading={vehiclesLoading || assigningVehicle}
              onChange={(value) =>
                handleVehicleAssign(value, record.driverId, record)
              }
              allowClear
              onClear={() => handleVehicleClear(record)}
              options={rowVehicleOptions}
              placeholder="Select vehicle"
              optionFilterProp="label"
              showSearch
              filterOption={(input, option) =>
                option?.label?.toLowerCase().includes(input.toLowerCase())
              }
              optionLabelProp="label"
              className="mb-0"
            />
          );
        },
      },
      {
        title: "Actions",
        dataIndex: "driverId",
        className: "px-2 text-center",
        align: "center",
        key: "actions",
        render: (driverId) => (
          <div className="flex gap-4 max-w-max">
            <Button onClick={() => handleModal(driverId)} icon={<CiEdit />} />
            <Button
              onClick={() => handleDeleteModal(driverId)}
              className="font-semibold bg-[#750014]"
              type="primary"
              icon={<RiDeleteBin6Fill />}
            />
          </div>
        ),
      },
    ],
    [
      currentPage,
      pageSize,
      vehiclesLoading,
      assigningVehicle,
      vehicleOptions,
      handleVehicleAssign,
      handleVehicleClear,
      handleModal,
      handleDeleteModal,
      allVehiclesMap,
    ]
  );

  const paginationSettings = useMemo(
    () => ({
      pageSize: pageSize,
      current: currentPage,
      showSizeChanger: true,
      pageSizeOptions: ["10", "20", "50"],
      onChange: handlePaginationChange,
      onShowSizeChange: handlePaginationChange,
    }),
    [currentPage, pageSize, handlePaginationChange]
  );

  const extraHeaderContent = useMemo(() => {
    const hasActiveAdvancedFilters = filterPayload.filters.some(
      (f) => f.attribute !== "name"
    );

    return (
      <div className="flex justify-between items-center w-full">
        <div className="flex gap-2 w-[400px]">
          <SearchInput value={fullName} onChange={handleNameChange} />
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
            onClick={() => handleModal()}
            icon={<PlusOutlined />}
          >
            Add Driver
          </Button>
        </div>
      </div>
    );
  }, [fullName, handleNameChange, filterPayload, clearFilters]);

  return (
    <>
      <PageWrapper>
        <Title title="Driver List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={driverQuery}
            exportEnabled={true}
            extraHeaderContent={extraHeaderContent}
            formatData={formatDriverData}
            columns={columns}
            pagination={paginationSettings}
          />
        </div>
      </PageWrapper>

      {isModalOpen && (
        <Modal
          title={
            <h1 className="text-2xl">
              {selectedId !== null ? "Update Driver" : "Add Driver"}
            </h1>
          }
          open={true}
          footer={null}
          onCancel={() => handleModal()}
          width="800px"
          destroyOnHidden={true}
        >
          <AddDriverModal
            handleCancel={() => handleModal()}
            driverListDataRefetch={() => tableRef.current?.Refetch()}
            selectedId={selectedId}
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
            Are you sure you want to delete this driver?
          </p>
        </Modal>
      )}

      {/* Assign Vehicle Confirmation Modal */}
      {assignConfirmModal && (
        <Modal
          title="Confirm Vehicle Assignment"
          onCancel={() => handleAssignConfirmModal()}
          onOk={confirmAssignVehicle}
          open={assignConfirmModal}
          okButtonProps={{
            className: "font-semibold bg-[#750014]!",
            type: "primary",
            loading: assigningVehicle,
          }}
          okText="Assign Vehicle"
          cancelButtonProps={{
            disabled: assigningVehicle,
          }}
        >
          <p className="text-base text-gray-700">
            Are you sure you want to assign vehicle{" "}
            <strong>{pendingAssignment.vehicleLabel}</strong> to this driver?
          </p>
        </Modal>
      )}

      {/* Unassign Vehicle Confirmation Modal */}
      {unassignConfirmModal && (
        <Modal
          title="Confirm Vehicle Unassignment"
          onCancel={() => setUnassignConfirmModal(!unassignConfirmModal)}
          onOk={confirmUnassignVehicle}
          open={unassignConfirmModal}
          okButtonProps={{
            className: "font-semibold bg-[#750014]!",
            type: "primary",
            loading: assigningVehicle,
          }}
          okText="Unassign Vehicle"
          cancelButtonProps={{
            disabled: assigningVehicle,
          }}
        >
          <p className="text-base text-gray-700">
            Are you sure you want to unassign the vehicle from this driver?
          </p>
        </Modal>
      )}

      {filterModal && (
        <Modal
          title={<h1 className="text-2xl">Filter Drivers</h1>}
          open={filterModal}
          onCancel={() => setFilterModal(false)}
          width="800px"
          destroyOnHidden={true}
          footer={null}
        >
          <DriverFilter
            onApplyFilter={handleApplyFilter}
            onCancel={() => setFilterModal(false)}
            initialFilters={activeFilters}
          />
        </Modal>
      )}
    </>
  );
}