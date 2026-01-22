"use client";
import { useState, useRef, useCallback, useMemo, memo, useEffect } from "react";
import { useQuery } from "@tanstack/react-query"; // Added missing import
import { Modal } from "antd";
import { toast } from "react-toastify";
import { DispatchColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { getAllDispatch, pickupDispatch } from "@/service/dispatch";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { Table } from "@/Components/atom/Table";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { Select } from "@/Components/atom/Select";
import { getAllLocation } from "@/service/location";
import { CiEdit } from "react-icons/ci";
import { Input } from "@/Components/atom/Input";
import { IoFilter } from "react-icons/io5";
import dynamic from "next/dynamic";
import { PlusOutlined } from "@ant-design/icons";
import { FaPeopleCarryBox } from "react-icons/fa6";

const AddDispatchModal = dynamic(
  () => import("@/Components/pages/Dispatch/AddDispatch"),
  {
    ssr: true,
    loading: () => <div className="p-4">Loading form...</div>,
  }
);

const DispatchFilter = dynamic(
  () => import("@/Components/pages/Dispatch/DispatchFilter"),
  {
    ssr: true,
    loading: () => <div className="p-4">Loading filter...</div>,
  }
);

export default function Dispatch() {
  const tableRef = useRef(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterModal, setFilterModal] = useState(false);
  const [pickupModal, setPickupModal] = useState(false);
  const [pickupLocation, setPickupLocation] = useState(""); // Changed from string to ID
  const [pickupLoading, setPickupLoading] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  const [activeFilters, setActiveFilters] = useState({});
  const [locationOptions, setLocationOptions] = useState([]);
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const { data: locationData, isLoading: locationLoading } = useQuery({
    queryKey: ["locationDataDrop"],
    queryFn: () => getAllLocation(),
  });

  const dispatchQuery = useMemo(
    () => ({
      queryKey: ["dispatchList", filterPayload],
      queryFn: () => getAllDispatch(filterPayload),
      onError: (error) =>
        toast.error("Failed to load dispatch list: " + error.message),
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

  // Fixed useEffect - removed customerData dependency
  useEffect(() => {
    if (locationData) {
      const options = locationData.map((item) => ({
        label: `${item.locationName}`,
        value: item.locationId,
        data: item,
      }));
      setLocationOptions(options);
    }
  }, [locationData]); // Removed customerData dependency

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

  const formatDispatchData = useCallback((data) => {
    return (
      data?.map((dispatch) => ({
        ...dispatch,
        key: dispatch.dispatchId,
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

  const handlePickupModal = useCallback(
    (id = null) => {
      setSelectedId(id);
      setPickupModal((prev) => !prev);
      if (pickupModal) {
        setPickupLocation(""); // Reset pickup location
        setTimeout(() => setSelectedId(null), 300);
      }
    },
    [pickupModal]
  );

  const confirmDelete = useCallback(async () => {
    try {
      if (selectedId !== null) {
        // await deleteDispatch(selectedId);
      }
      tableRef.current?.Refetch();
      toast.success("Dispatch deleted successfully");
    } catch (error) {
      toast.error("Failed to delete dispatch: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const confirmPickup = useCallback(async () => {
    if (!pickupLocation) {
      toast.error("Please select a pickup location");
      return;
    }

    if (!selectedId) {
      toast.error("No dispatch selected");
      return;
    }

    setPickupLoading(true);
    try {
      // Find the selected location data
      const selectedLocationData = locationOptions.find(
        (option) => option.value === pickupLocation
      );

      // Pass the location ID or name based on your API requirements
      await pickupDispatch(selectedId, pickupLocation);
      toast.success("Pickup scheduled successfully");
      tableRef.current?.Refetch();
      handlePickupModal();
    } catch (error) {
      toast.error("Failed to schedule pickup: " + error.message);
    } finally {
      setPickupLoading(false);
    }
  }, [selectedId, pickupLocation, handlePickupModal, locationOptions]);

  const handleApplyFilter = useCallback((payload) => {
    const active = {};
    if (payload.filters && payload.filters.length > 0) {
      payload.filters.forEach((filter) => {
        if (filter.attribute === "destination") {
          active.destination = filter.value;
        } else if (filter.attribute === "origin") {
          active.origin = filter.value;
        } else if (filter.attribute === "status") {
          active.status = filter.value;
        }
      });
    }

    setActiveFilters(active);
    setFilterPayload(payload);
    setFilterModal(false);
  }, []);

  const clearFilters = useCallback(() => {
    setFilterPayload({ limit: 0, filters: [] });
    setActiveFilters({});
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
      ...DispatchColumn,

      {
        title: "Actions",
        dataIndex: "dispatchId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (dispatchId, record) => (
          <div className="flex gap-4 justify-center">
            <Button
              onClick={() => handlePickupModal(dispatchId)}
              className="font-semibold bg-[#750014]"
              type="primary"
              disabled={record?.status !== "Scheduled"}
              icon={<FaPeopleCarryBox />}
              title="Schedule Pickup"
            />
          </div>
        ),
      },
    ],
    [handlePickupModal, currentPage, pageSize]
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
    const hasActiveAdvancedFilters = filterPayload.filters.length > 0;

    return (
      <div className="flex justify-end gap-2 items-center w-full">
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
        <Button
          className="font-semibold bg-[#750014]"
          type="primary"
          onClick={() => handleModal()}
          icon={<PlusOutlined />}
        >
          Add Dispatch
        </Button>
      </div>
    );
  }, [filterPayload, clearFilters, handleModal]);

  return (
    <>
      <PageWrapper>
        <Title title="Dispatch List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={dispatchQuery}
            exportEnabled={true}
            extraHeaderContent={extraHeaderContent}
            formatData={formatDispatchData}
            columns={columns}
            pagination={paginationSettings}
          />
        </div>
      </PageWrapper>

      {isModalOpen && (
        <Modal
          title={
            <h1 className="text-2xl">
              {selectedId !== null ? "Update Dispatch" : "Add Dispatch"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={() => handleModal()}
          width="800px"
          destroyOnHidden={true}
        >
          <AddDispatchModal
            handleCancel={() => handleModal()}
            dispatchListDataRefetch={() => tableRef.current?.Refetch()}
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
            Are you sure you want to delete this dispatch?
          </p>
        </Modal>
      )}

      {pickupModal && (
        <Modal
          title="Add Pickup Location"
          open={pickupModal}
          onCancel={() => handlePickupModal()}
          width="500px"
          destroyOnHidden={true}
          footer={[
            <Button
              key="cancel"
              onClick={() => handlePickupModal()}
              disabled={pickupLoading}
            >
              Cancel
            </Button>,
            <Button
              key="confirm"
              type="primary"
              onClick={confirmPickup}
              loading={pickupLoading}
              className="font-semibold bg-[#750014]"
              disabled={!pickupLocation || pickupLoading}
            >
              Confirm
            </Button>,
          ]}
        >
          <div className="py-2">
            <Select
              label="Pickup Location"
              placeholder="Select pickup location"
              fieldWidth="100%"
              value={pickupLocation}
              onChange={(value) => setPickupLocation(value)}
              required={true}
              loading={locationLoading}
              options={locationOptions}
              noOptionMsg="No location available. Please add location first."
              disabled={locationLoading || locationOptions.length === 0}
              notFoundContent={
                locationLoading
                  ? "Loading locations..."
                  : "No location available"
              }
            />
          </div>
        </Modal>
      )}

      {filterModal && (
        <Modal
          title={<h1 className="text-2xl">Filter Dispatches</h1>}
          open={filterModal}
          onCancel={() => setFilterModal(false)}
          width="800px"
          destroyOnHidden={true}
          footer={null}
        >
          <DispatchFilter
            onApplyFilter={handleApplyFilter}
            onCancel={() => setFilterModal(false)}
            initialFilters={activeFilters}
          />
        </Modal>
      )}
    </>
  );
}
