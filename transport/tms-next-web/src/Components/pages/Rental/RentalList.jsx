"use client";
import { useState, useRef } from "react";
import { Modal } from "antd";
import { toast } from "react-toastify";
import { RentalColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { getAllRentalVehicle, deleteRental } from "@/service/rental";
import { CiEdit } from "react-icons/ci";
import AddRental from "@/Components/pages/Rental/AddRental";
import { Table } from "@/Components/atom/Table";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { RiDeleteBin6Fill } from "react-icons/ri";

export default function Rental() {
  const tableRef = useRef(null);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deleteModal, setDeleteModal] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  // Define query object separately for custom Table component
  const rentalQuery = {
    queryKey: ["rentalList"],
    queryFn: getAllRentalVehicle,
    onError: (error) => {
      toast.error("Failed to load rental list: " + error.message);
    },
  };

  const handleDeleteModal = (id = null) => {
    setSelectedId(id);
    setDeleteModal(!deleteModal);

    if (!deleteModal === false) {
      setTimeout(() => setSelectedId(null), 300);
    }
  };

  const confirmDelete = async () => {
    try {
      if (selectedId !== null) {
        // await handleVehicleUnassign(selectedId);
        await deleteRental(selectedId);
      } else {
        await deleteRental(selectedId);
      }
      tableRef.current?.Refetch();
      toast.success("Rental deleted successfully");
    } catch (error) {
      toast.error("Failed to delete rental: " + error.message);
    } finally {
      handleDeleteModal();
    }
  };
  const handleCancel = () => {
    setIsModalOpen(false);
    // Wait for modal animation to complete before clearing ID
    setTimeout(() => {
      setSelectedId(null);
    }, 300);
  };
  const formatVehicleData = (data) => {
    return data.map((vehicle) => ({
      ...vehicle,
      key: vehicle.rentalDetailsId,
    }));
  };
  const handleModal = (id = null) => {
    setSelectedId(id);
    setIsModalOpen(!isModalOpen);

    if (!isModalOpen === false && id === null) {
      setTimeout(() => setSelectedId(null), 300);
    }
  };
  const columns = [
    ...RentalColumn,
    {
      title: "Actions",
      dataIndex: "rentalDetailsId",
      key: "actions",
      className: "px-2 text-center",
      align: "center",
      render: (id) => (
        <div className="flex gap-4 max-w-max">
          <Button onClick={() => handleModal(id)} icon={<CiEdit />} />
          <Button
            onClick={() => handleDeleteModal(id)}
            className="font-semibold bg-[#750014]"
            type="primary"
            icon={<RiDeleteBin6Fill />}
          />
        </div>
      ),
    },
  ];

  return (
    <>
      <PageWrapper>
        <Title title="Rental List" className="text-center" />
        <div className="flex flex-col gap-4">
          <div className="flex justify-end items-center">
            <Button
              className="font-semibold bg-[#750014]"
              type="primary"
              onClick={() => handleModal()}
            >
              Add Rental
            </Button>
          </div>
          <Table
            ref={tableRef}
            query={rentalQuery}
            formatData={formatVehicleData}
            columns={columns}
            pagination={{
              pageSize: 10,
              showSizeChanger: true,
              pageSizeOptions: ["10", "20", "50"],
            }}
          />
        </div>
      </PageWrapper>
      <Modal
        title={
          <h1 className="text-2xl">
            {selectedId !== null ? "Update Rental" : "Add Rental"}
          </h1>
        }
        open={isModalOpen}
        footer={null}
        onCancel={handleCancel}
        width="800px"
        destroyOnHidden={true} // Important for proper form reset
      >
        <AddRental
          handleCancel={handleCancel}
          vehicleListDataRefetch={() => tableRef.current?.Refetch()}
          selectedId={selectedId}
        />
      </Modal>

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
          Are you sure you want to delete this rental?
        </p>
      </Modal>
    </>
  );
}
