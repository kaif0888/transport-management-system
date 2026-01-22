"use client";

import { useRef, useState, useMemo, useCallback, memo, useEffect } from "react";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { getAllLocation, deleteLocation } from "@/service/location";
import { Table } from "@/Components/atom/Table";
import { CiEdit } from "react-icons/ci";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { Modal } from "antd";
import AddLocationModal from "./AddLocationModal";
import { toast } from "react-toastify";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { SearchOutlined } from "@ant-design/icons";
import { PlusOutlined } from "@ant-design/icons";

// Memoize the search input component
const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="locationName"
    value={value}
    onChange={onChange}
    placeholder="Search by Location Name"
    name="locationName"
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

export default function LocationList() {
  const tableRef = useRef(null);
  const [locationName, setLocationName] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const locationQuery = useMemo(
    () => ({
      queryKey: ["locationList", filterPayload],
      queryFn: () => getAllLocation(filterPayload),
      onError: (error) => {
        toast.error("Failed to load location list: " + error.message);
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

  const handleLocationNameChange = useCallback(
    (e) => {
      const value = e.target.value;
      setLocationName(value);

      const newFilters = { ...filterPayload };

      if (value.length >= 2) {
        const locationIndex = newFilters.filters.findIndex(
          (f) => f.attribute === "locationName"
        );

        if (locationIndex >= 0) {
          newFilters.filters[locationIndex].value = value;
        } else {
          newFilters.filters.push({
            attribute: "locationName",
            operation: "CONTAINS",
            value: value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "locationName"
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
      await deleteLocation(selectedId);
      tableRef.current?.Refetch();
      toast.success("Location deleted successfully");
    } catch (error) {
      toast.error("Failed to delete location: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const formatLocationData = useCallback((data) => {
    return (
      data?.map((location) => ({
        ...location,
        key: location.locationId,
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
      {
        title: "Location Name",
        dataIndex: "locationName",
        key: "locationName",
      },
      {
        title: "Location Area",
        dataIndex: "locationArea",
        key: "locationArea",
      },
      {
        title: "Location Address",
        dataIndex: "locationAddress",
        key: "locationAddress",
      },
      {
        title: "Pincode",
        dataIndex: "pincode",
        key: "pincode",
      },
      {
        title: "State",
        dataIndex: "state",
        key: "state",
      },
      {
        title: "Status",
        dataIndex: "status",
        key: "status",
      },
      {
        title: "Circle",
        dataIndex: "circle",
        key: "circle",
      },
      {
        title: "District",
        dataIndex: "district",
        key: "district",
      },
      {
        title: "Block",
        dataIndex: "block",
        key: "block",
      },
      {
        title: "Country",
        dataIndex: "country",
        key: "country",
      },

      {
        title: "Actions",
        dataIndex: "locationId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (locationId) => (
          <div className="flex gap-4">
            <Button
              key={"edit"}
              onClick={() => openUpdateModal(locationId)}
              icon={<CiEdit />}
            />
            <Button
              key={"delete"}
              className="font-semibold bg-[#750014]"
              type="primary"
              onClick={() => handleDeleteModal(locationId)}
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
          <SearchInput
            value={locationName}
            onChange={handleLocationNameChange}
          />
        </div>
        <div className="flex gap-2">
          <Button
            className="font-semibold bg-[#750014]"
            type="primary"
            onClick={showModal}
            icon={<PlusOutlined />}
          >
            Add Location
          </Button>
        </div>
      </div>
    ),
    [locationName, handleLocationNameChange, showModal]
  );


  return (
    <>
      <PageWrapper>
        <Title title="Location List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={locationQuery}
            exportEnabled={true}
            extraHeaderContent={extraHeaderContent}
            formatData={formatLocationData}
            columns={columns}
            pagination={paginationSettings}
          />
        </div>
      </PageWrapper>

      {isModalOpen && (
        <Modal
          title={
            <>
              <h1 className="text-2xl">
                {selectedId !== null ? "Update Location" : "Add Location"}
              </h1>
              <p className="text-sm text-gray-600 mt-1">
                {selectedId
                  ? "Update location information"
                  : "Enter location details to add a new location"}
              </p>
            </>
          }
          open={isModalOpen}
          footer={null}
          onCancel={handleCancel}
          width="800px"
          destroyOnHidden={true}
        >
          <AddLocationModal
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
          Are you sure you want to delete this location?
        </p>
      </Modal>
    </>
  );
}
