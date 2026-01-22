"use client";
import { useState, useRef, useCallback, useMemo, memo } from "react";
import { Modal } from "antd";
import { toast } from "react-toastify";
import { ExpenseColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { getAllExpense, deleteExpense } from "@/service/expense";
import { Table } from "@/Components/atom/Table";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { IoFilter } from "react-icons/io5";
import { CiEdit } from "react-icons/ci";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { PlusOutlined } from "@ant-design/icons";
import dynamic from "next/dynamic";

const AddExpense = dynamic(
  () => import("@/Components/pages/Expense/AddExpense"),
  {
    ssr: false,
    loading: () => <div className="p-4">Loading form...</div>,
  }
);

const ExpenseFilter = dynamic(
  () => import("@/Components/pages/Expense/ExpenseFilter"),
  {
    ssr: false,
    loading: () => <div className="p-4">Loading filter...</div>,
  }
);

export default function ExpenseList() {
  const tableRef = useRef(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [filterModal, setFilterModal] = useState(false);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 10, filters: [] });
  const [activeFilters, setActiveFilters] = useState({});

  const expenseQuery = useMemo(
    () => ({
      queryKey: ["expenseList", filterPayload],
      queryFn: () => getAllExpense(filterPayload),
      onError: (error) => {
        toast.error("Failed to load expense list: " + error.message);
      },
    }),
    [filterPayload]
  );

  const formatExpenseData = useCallback((data) => {
    return (
      data?.map((expense) => ({
        ...expense,
        key: expense.expenseId,
      })) || []
    );
  }, []);

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
        await deleteExpense(selectedId);
      }
      tableRef.current?.Refetch();
      toast.success("Expense deleted successfully");
    } catch (error) {
      toast.error("Failed to delete expense: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const handleApplyFilter = useCallback((payload) => {
    const active = {};
    if (payload.filters && payload.filters.length > 0) {
      payload.filters.forEach((filter) => {
        if (filter.attribute === "amount") {
          active.amount = filter.value;
        } else if (filter.attribute === "date") {
          active.date = filter.value;
        }
      });
    }

    setActiveFilters(active);
    setFilterPayload(payload);
    setFilterModal(false);
  }, []);

  const clearFilters = useCallback(() => {
    setFilterPayload({ limit: 10, filters: [] });
    setActiveFilters({});
  }, []);

  const columns = useMemo(
    () => [
      ...ExpenseColumn,
      {
        title: "Actions",
        dataIndex: "expenseId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (expenseId) => (
          <div className="flex gap-4 max-w-max">
            <Button onClick={() => handleModal(expenseId)} icon={<CiEdit />} />
            <Button
              onClick={() => handleDeleteModal(expenseId)}
              className="font-semibold bg-[#750014]"
              type="primary"
              icon={<RiDeleteBin6Fill />}
            />
          </div>
        ),
      },
    ],
    [handleModal, handleDeleteModal]
  );

  const paginationSettings = useMemo(
    () => ({
      pageSize: 10,
      showSizeChanger: true,
      pageSizeOptions: ["10", "20", "50"],
    }),
    []
  );

  const extraHeaderContent = useMemo(() => {
    const hasActiveFilters = filterPayload.filters.length > 0;

    return (
      <div className="flex justify-end gap-2 items-center w-full">
        <div className="flex gap-2">
          <Button
            icon={<IoFilter />}
            className={`font-semibold ${
              hasActiveFilters ? "bg-[#750014] text-white" : ""
            }`}
            onClick={() => setFilterModal(true)}
          >
            Filter {hasActiveFilters ? "(Active)" : ""}
          </Button>
          {hasActiveFilters && (
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
          Add Expense
        </Button>
      </div>
    );
  }, [filterPayload, clearFilters, handleModal]);

  return (
    <>
      <PageWrapper>
        <Title title="Expense List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={expenseQuery}
            exportEnabled={true}
            extraHeaderContent={extraHeaderContent}
            formatData={formatExpenseData}
            columns={columns}
            pagination={paginationSettings}
          />
        </div>
      </PageWrapper>

      {isModalOpen && (
        <Modal
          title={
            <h1 className="text-2xl">
              {selectedId !== null ? "Update Expense" : "Add Expense"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={() => handleModal()}
          width="800px"
          destroyOnHidden={true}
        >
          <AddExpense
            handleCancel={() => handleModal()}
            expenseListDataRefetch={() => tableRef.current?.Refetch()}
            selectedId={selectedId}
          />
        </Modal>
      )}

      {deleteModal && (
        <Modal
          title="Confirm Deletion"
          open={deleteModal}
          onOk={confirmDelete}
          onCancel={() => handleDeleteModal()}
          okText="Delete"
          okButtonProps={{
            className: "font-semibold bg-[#750014]!",
            type: "primary",
          }}
        >
          <p className="text-base text-gray-700">
            Are you sure you want to delete this expense?
          </p>
        </Modal>
      )}

      {filterModal && (
        <Modal
          title={<h1 className="text-2xl">Filter Expenses</h1>}
          open={filterModal}
          onCancel={() => setFilterModal(false)}
          width="800px"
          destroyOnHidden={true}
          footer={null}
        >
          <ExpenseFilter
            onApplyFilter={handleApplyFilter}
            onCancel={() => setFilterModal(false)}
            initialFilters={activeFilters}
          />
        </Modal>
      )}
    </>
  );
}
