"use client";
import { useState, useRef, useCallback, useMemo, memo, useEffect } from "react";
import { Modal, Tooltip } from "antd";
import { toast } from "react-toastify";
import { userColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { getAllUser, deleteUser } from "@/service/user";
import { Table } from "@/Components/atom/Table";
import { Input } from "@/Components/atom/Input";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { CiEdit } from "react-icons/ci";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";
import dynamic from "next/dynamic";

const AddUser = dynamic(() => import("@/Components/pages/User/AddUserModal"), {
  ssr: false,
  loading: () => <div className="p-4">Loading form...</div>,
});

const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    value={value}
    onChange={onChange}
    placeholder="Search by Branch..."
    className="h-[35px]"
    startIcon={<SearchOutlined style={{ color: "#666", fontSize: "18px" }} />}
  />
));

export default function UserList() {
  const tableRef = useRef(null);
  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  const [debouncedSearch, setDebouncedSearch] = useState("");
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 300);

    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    setFilterPayload((prevPayload) => {
      const newPayload = { ...prevPayload };

      newPayload.filters = newPayload.filters.filter(
        (f) => f.attribute !== "branch.branchId"
      );

      if (debouncedSearch.trim().length >= 2) {
        newPayload.filters.push({
          attribute: "branch.branchId",
          operation: "CONTAINS",
          value: debouncedSearch.trim(),
        });
      }

      return newPayload;
    });
  }, [debouncedSearch]);

  const tableQuery = useMemo(
    () => ({
      queryKey: ["getAllUser", filterPayload],
      queryFn: () => getAllUser(filterPayload),
      onError: (error) => {
        toast.error("Failed to load user list: " + error.message);
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
      await deleteUser(selectedId);
      tableRef.current?.Refetch();
      toast.success("User deleted successfully");
    } catch (error) {
      toast.error("Failed to delete user: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const formatData = useCallback((data) => {
    return (
      data?.map((item) => ({
        ...item,
        key: item.userId,
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
      ...userColumn,
      {
        title: "Actions",
        dataIndex: "userId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (userId) => (
          <div className="flex gap-2 justify-center">
            <Tooltip title="Edit User">
              <Button
                key={"edit"}
                onClick={() => openUpdateModal(userId)}
                icon={<CiEdit />}
              />
            </Tooltip>
            <Tooltip title="Delete User">
              <Button
                key={"delete"}
                className="font-semibold bg-[#750014]"
                type="primary"
                onClick={() => handleDeleteModal(userId)}
                icon={<RiDeleteBin6Fill />}
              />
            </Tooltip>
          </div>
        ),
      },
    ],
    [openUpdateModal, handleDeleteModal,currentPage, pageSize]
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
    return (
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
            Add User
          </Button>
        </div>
      </div>
    );
  }, [search, handleSearchChange, showModal]);

  return (
    <>
      <PageWrapper>
        <Title title="User List" className="text-center" />
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
              {selectedId !== null ? "Update User" : "Add User"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={() => handleCancel()}
          width="800px"
          destroyOnHidden={true}
        >
          <AddUser
            handleCancel={() => handleCancel()}
            ListDataRefetch={() => tableRef.current?.Refetch()}
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
            Are you sure you want to delete this user?
          </p>
        </Modal>
      )}
    </>
  );
}
