"use client";

import { useRef, useState, useMemo, useCallback, memo, useEffect } from "react";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { Table } from "@/Components/atom/Table";
import { vehicleTypeColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { Modal } from "antd";
import AddVehicleTypeModal from "./AddVehicleTypeModal";
import { toast } from "react-toastify";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { SearchOutlined } from "@ant-design/icons";
import { PlusOutlined } from "@ant-design/icons";
import { deleteVehicleType, getAllVehicleType } from "@/service/vehicle";

// Memoize the search input component
const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="typeName"
    value={value}
    onChange={onChange}
    placeholder="Search by Type Name"
    name="typeName"
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

export default function VehicleTypeList() {
  const tableRef = useRef(null);
  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const vehicleTypeQuery = useMemo(
    () => ({
      queryKey: ["vehcileTypeList", filterPayload],
      queryFn: () => getAllVehicleType(filterPayload),
      onError: (error) => {
        toast.error("Failed to load vehicle type list: " + error.message);
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
        const locationIndex = newFilters.filters.findIndex(
          (f) => f.attribute === "typeName"
        );

        if (locationIndex >= 0) {
          newFilters.filters[locationIndex].value = value;
        } else {
          newFilters.filters.push({
            attribute: "typeName",
            operation: "CONTAINS",
            value: value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "typeName"
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
      await deleteVehicleType(selectedId);
      tableRef.current?.Refetch();
      toast.success("Vehicle Type deleted successfully");
    } catch (error) {
      toast.error("Failed to delete vehicle type: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const formatTableData = useCallback((data) => {
    return (
      data?.map((vehicleType) => ({
        ...vehicleType,
        key: vehicleType.vehicleTypeId,
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
      ...vehicleTypeColumn,

      {
        title: "Actions",
        dataIndex: "vehicleTypeId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (vehicleTypeId) => (
          <div className="flex gap-4 justify-center">
            {/* <Button
                key={"edit"}
                onClick={() => openUpdateModal(expenseTypeId)}
                icon={<CiEdit />}
              /> */}
            <Button
              key={"delete"}
              className="font-semibold bg-[#750014]"
              type="primary"
              onClick={() => handleDeleteModal(vehicleTypeId)}
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
            Add Vehicle Type
          </Button>
        </div>
      </div>
    ),
    [search, handleSearchChange, showModal]
  );

  return (
    <>
      <PageWrapper>
        <Title title="Vehicle Type List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={vehicleTypeQuery}
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
              {selectedId !== null ? "Update Vehicle Type" : "Add Vehicle Type"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={handleCancel}
          width="500px"
          destroyOnHidden={true}
        >
          <AddVehicleTypeModal
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
          className: "font-semibold bg-[#750014]!",
          type: "primary",
        }}
        okText="Delete"
      >
        <p className="text-base text-gray-700">
          Are you sure you want to delete this expense type?
        </p>
      </Modal>
    </>
  );
}
