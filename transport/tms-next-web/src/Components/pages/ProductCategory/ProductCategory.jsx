"use client";

import { useRef, useState, useMemo, useCallback, memo, useEffect } from "react";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import {
  getAllProductCategory,
  deleteProductCategory,
} from "@/service/productCategory";
import { Table } from "@/Components/atom/Table";
import { procustCategoryColumn } from "@/Data/TableColumn";
import { CiEdit } from "react-icons/ci";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { Modal } from "antd";
import AddProductCategoryModal from "./AddProductCategoryModal";
import { toast } from "react-toastify";
import { RiDeleteBin6Fill } from "react-icons/ri";
import { SearchOutlined } from "@ant-design/icons";
import { PlusOutlined } from "@ant-design/icons";

// Memoize the search input component
const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="categoryName"
    value={value}
    onChange={onChange}
    placeholder="Search by Category Name"
    name="categoryName"
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

export default function ProductCategory() {
  const tableRef = useRef(null);
  const [categoryName, setCategoryName] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  // Add state to track current pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const categoryQuery = useMemo(
    () => ({
      queryKey: ["categoryList", filterPayload],
      queryFn: () => getAllProductCategory(filterPayload),
      onError: (error) => {
        toast.error("Failed to load product category list: " + error.message);
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

  const handleCategoryNameChange = useCallback(
    (e) => {
      const value = e.target.value;
      setCategoryName(value);

      const newFilters = { ...filterPayload };

      if (value.length >= 2) {
        const categoryIndex = newFilters.filters.findIndex(
          (f) => f.attribute === "categoryName"
        );

        if (categoryIndex >= 0) {
          newFilters.filters[categoryIndex].value = value;
        } else {
          newFilters.filters.push({
            attribute: "categoryName",
            operation: "CONTAINS",
            value: value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "categoryName"
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
      await deleteProductCategory(selectedId);
      tableRef.current?.Refetch();
      toast.success("Product Category deleted successfully");
    } catch (error) {
      toast.error("Failed to delete product category: " + error.message);
    } finally {
      handleDeleteModal();
    }
  }, [selectedId, handleDeleteModal]);

  const formatCategoryData = useCallback((data) => {
    return (
      data?.map((category) => ({
        ...category,
        key: category.categoryId,
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
      ...procustCategoryColumn,

      {
        title: "Actions",
        dataIndex: "categoryId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (categoryId) => (
          <div className="flex gap-4 justify-center">
            <Button
              key={"edit"}
              onClick={() => openUpdateModal(categoryId)}
              icon={<CiEdit />}
            />
            <Button
              key={"delete"}
              className="font-semibold bg-[#750014]"
              type="primary"
              onClick={() => handleDeleteModal(categoryId)}
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
            value={categoryName}
            onChange={handleCategoryNameChange}
          />
        </div>
        <div className="flex gap-2">
          <Button
            className="font-semibold bg-[#750014]"
            type="primary"
            onClick={showModal}
            icon={<PlusOutlined />}
          >
            Add Category
          </Button>
        </div>
      </div>
    ),
    [categoryName, handleCategoryNameChange, showModal]
  );

  return (
    <>
      <PageWrapper>
        <Title title="Product Category List" className="text-center" />
        <div className="flex flex-col gap-4">
          <Table
            ref={tableRef}
            query={categoryQuery}
            exportEnabled={true}
            extraHeaderContent={extraHeaderContent}
            formatData={formatCategoryData}
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
                ? "Update Product Category"
                : "Add Product Category"}
            </h1>
          }
          open={isModalOpen}
          footer={null}
          onCancel={handleCancel}
          width="800px"
          destroyOnHidden={true}
        >
          <AddProductCategoryModal
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
          Are you sure you want to delete this category?
        </p>
      </Modal>
    </>
  );
}
