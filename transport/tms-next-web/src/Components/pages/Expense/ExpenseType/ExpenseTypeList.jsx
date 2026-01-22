"use client";

import { useRef, useState, useMemo, useCallback, memo } from "react";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { Table } from "@/Components/atom/Table";
import { expenseTypeColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { Modal } from "antd";
import AddExpenseTypeModal from "./AddExpenseTypeModal";
import { toast } from "react-toastify";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { SearchOutlined } from "@ant-design/icons";
import { PlusOutlined } from "@ant-design/icons";
import { deleteExpenseType, getExpenseType } from "@/service/expense";

// Memoize the search input component
const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="expenseTypeName"
    value={value}
    onChange={onChange}
    placeholder="Search by Type"
    name="expenseTypeName"
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

export default function ExpenseTypeList() {
  const tableRef = useRef(null);
  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });

  const expenseTypeQuery = useMemo(
    () => ({
      queryKey: ["expenseTypeList", filterPayload],
      queryFn: () => getExpenseType(filterPayload),
      onError: (error) => {
        toast.error("Failed to load expense type list: " + error.message);
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
      await deleteExpenseType(selectedId);
      tableRef.current?.Refetch();
      toast.success("Expense Type deleted successfully");
    } catch (error) {
      toast.error("Failed to delete expense type: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const formatTableData = useCallback((data) => {
    return (
      data?.map((expenseType) => ({
        ...expenseType,
        key: expenseType.expenseTypeId,
      })) || []
    );
  }, []);

  const columns = useMemo(
    () => [
      ...expenseTypeColumn,
      {
      title: "Description",
      dataIndex: "description",
      key: "description",
      align: "center",
      render: (text) => text || "-",
    },
    {
  title: "Last Modified By",
  dataIndex: "lastModifiedBy",
  key: "lastModifiedBy",
  align: "center",
  render: (text) => text || "-",
},
{
  title: "Last Modified Date",
  dataIndex: "lastModifiedDate",
  key: "lastModifiedDate",
  align: "center",
  render: (text) =>
    text ? new Date(text).toLocaleString() : "-",
},
      {
        title: "Actions",
        dataIndex: "expenseTypeId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (expenseTypeId) => (
          <div className="flex gap-4 justify-center">
            <Button
              key={"delete"}
              className="font-semibold bg-[#750014]"
              type="primary"
              onClick={() => handleDeleteModal(expenseTypeId)}
              icon={<RiDeleteBin6Fill />}
            />
          </div>
        ),
      },
    ],
    [openUpdateModal, handleDeleteModal]
  );

  const paginationSettings = useMemo(
    () => ({
      pageSize: 10,
      showSizeChanger: true,
      pageSizeOptions: ["10", "20", "50"],
    }),
    []
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
            Add Expense Type
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
        <Title title="Expense Type List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={expenseTypeQuery}
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
              {selectedId !== null
                ? "Update Expesnse Type"
                : "Add Expense Type"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={handleCancel}
          width="800px"
          destroyOnHidden={true}
        >
          <AddExpenseTypeModal
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
