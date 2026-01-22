"use client";
import { useState, useRef, useCallback, useMemo, memo, useEffect } from "react";
import { Modal, Tooltip, notification } from "antd";
import { toast } from "react-toastify";
import { dispatchTrackingcolumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import {
  getAllDispatchTracking,
  deleteDispatchTracking,
  UpdateDispatchTrackingStatus,
} from "@/service/dispatchTracking";
import { Table } from "@/Components/atom/Table";
import { Input } from "@/Components/atom/Input";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { IoFilter } from "react-icons/io5";
import { RiDeleteBin6Fill } from "react-icons/ri";
import {
  PlusOutlined,
  SearchOutlined,
  DownloadOutlined,
  FileTextOutlined,
} from "@ant-design/icons";
import { MdEditLocationAlt } from "react-icons/md";
import { GiBoxUnpacking } from "react-icons/gi";
import dynamic from "next/dynamic";

const AddDispatchTracking = dynamic(
  () => import("@/Components/pages/DispatchTracking/AddDisptchTrackingModal"),
  { ssr: false }
);

const DispatchTrackingFilter = dynamic(
  () => import("@/Components/pages/DispatchTracking/DispatchTrackingFilter"),
  { ssr: false }
);

const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    value={value}
    onChange={onChange}
    placeholder="Search by Dispatch ID..."
    className="h-[35px]"
    startIcon={<SearchOutlined style={{ color: "#666", fontSize: "18px" }} />}
  />
));
SearchInput.displayName = "SearchInput";

export default function DispatchTrackingList() {
  const tableRef = useRef(null);
  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [filterModal, setFilterModal] = useState(false);
  const [deleteModal, setDeleteModal] = useState(false);
  const [deliveryModal, setDeliveryModal] = useState(false);
  const [invoiceModal, setInvoiceModal] = useState(false);
  const [invoiceData, setInvoiceData] = useState(null);
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  const [activeFilters, setActiveFilters] = useState({});
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [isGeneratingInvoice, setIsGeneratingInvoice] = useState(false);

  const tableQuery = useMemo(
    () => ({
      queryKey: ["getAllDispatchTracking", filterPayload],
      queryFn: () => getAllDispatchTracking(filterPayload),
      onError: (error) => {
        toast.error("Failed to load dispatch tracking list: " + error.message);
      },
    }),
    [filterPayload]
  );

  const refetchTable = useCallback(() => {
    tableRef.current?.Refetch();
  }, []);

  useEffect(() => {
    refetchTable();
  }, [refetchTable]);

  const handleSearchChange = useCallback((e) => {
    const value = e.target.value;
    setSearch(value);

    setFilterPayload((prev) => {
      const updated = { ...prev };
      if (value.trim().length >= 2) {
        updated.filters = [
          {
            attribute: "dispatch.dispatchId",
            operation: "CONTAINS",
            value: value.trim(),
          },
        ];
      } else {
        updated.filters = [];
      }
      return updated;
    });
  }, []);

  const openUpdateModal = useCallback((id) => {
    setSelectedId(id);
    setIsModalOpen(true);
  }, []);

  const handleDeliveryModal = useCallback((id = null) => {
    setSelectedId(id);
    setDeliveryModal((prev) => !prev);
  }, []);

  const confirmDelete = useCallback(async () => {
    try {
      await deleteDispatchTracking(selectedId);
      refetchTable();
      toast.success("Dispatch Tracking deleted successfully");
    } catch (e) {
      toast.error("Failed to delete dispatch tracking");
    } finally {
      setDeleteModal(false);
    }
  }, [selectedId, refetchTable]);

  // ✅ ONLY UPDATED PART
  const confirmDelivery = useCallback(async () => {
    setIsGeneratingInvoice(true);
    try {
      const response = await UpdateDispatchTrackingStatus(selectedId);
      refetchTable();

      if (response?.invoiceGenerated) {
        notification.success({
          message: "Invoice Generated",
          description: (
            <div style={{ fontSize: "13px" }}>
              <div>
                <b>Invoice No:</b> {response.invoiceNumber}
              </div>
              <div>
                <b>Saved at:</b> {response.invoicePath}
              </div>
            </div>
          ),
          placement: "topRight",
          duration: 5,
        });

        setInvoiceData(response);
        setInvoiceModal(true);
      } else {
        toast.success("Order delivered successfully");
      }
    } catch (error) {
      toast.error("Failed to update delivery status");
    } finally {
      setIsGeneratingInvoice(false);
      handleDeliveryModal();
    }
  }, [selectedId, handleDeliveryModal, refetchTable]);

  const columns = useMemo(
    () => [
      {
        title: "S.No",
        render: (_, __, index) =>
          (currentPage - 1) * pageSize + index + 1,
        align: "center",
        width: 70,
      },
      ...dispatchTrackingcolumn,
      {
        title: "Actions",
        align: "center",
        width: 160,
        render: (_, record) => (
          <div className="flex gap-2 justify-center">
            <Tooltip title="Update Location">
              <Button
                icon={<MdEditLocationAlt />}
                onClick={() => openUpdateModal(record.trackingId)}
                disabled={record.status?.toLowerCase() === "delivered"}
              />
            </Tooltip>

            <Tooltip title="Mark as Delivered">
              <Button
                icon={<GiBoxUnpacking />}
                onClick={() => handleDeliveryModal(record.trackingId)}
                disabled={record.status?.toLowerCase() !== "reached"}
              />
            </Tooltip>

            {record.invoiceId && (
              <Tooltip title="Download Invoice">
                <Button
                  icon={<DownloadOutlined />}
                  onClick={() =>
                    handleDownloadInvoice(
                      record.invoiceId,
                      record.invoiceNumber
                    )
                  }
                />
              </Tooltip>
            )}
          </div>
        ),
      },
    ],
    [
      openUpdateModal,
      handleDeliveryModal,
      currentPage,
      pageSize,
    ]
  );

  return (
    <>
      <PageWrapper>
        <Title title="Dispatch Tracking List" />
        <Table
          ref={tableRef}
          query={tableQuery}
          columns={columns}
          pagination={{
            current: currentPage,
            pageSize,
            onChange: setCurrentPage,
          }}
        />
      </PageWrapper>

      {deliveryModal && (
        <Modal
          title="Confirm Delivery"
          open={deliveryModal}
          onCancel={() => handleDeliveryModal()}
          onOk={confirmDelivery}
          confirmLoading={isGeneratingInvoice}
        >
          Are you sure you want to mark this order as delivered?
        </Modal>
      )}

      {invoiceModal && invoiceData && (
        <Modal
          title="Invoice Generated"
          open={invoiceModal}
          onCancel={() => setInvoiceModal(false)}
          footer={null}
        >
          <p>
            <b>Invoice No:</b> {invoiceData.invoiceNumber}
          </p>
          <p>
            <b>Saved At:</b> {invoiceData.invoicePath}
          </p>
        </Modal>
      )}

      {isModalOpen && (
        <Modal
          title={selectedId ? "Update Dispatch Tracking" : "Add Dispatch Tracking"}
          open={isModalOpen}
          onCancel={() => setIsModalOpen(false)}
          footer={null}
        >
          <AddDispatchTracking
            handleCancel={() => setIsModalOpen(false)}
            ListDataRefetch={refetchTable}
            selectedId={selectedId}
          />
        </Modal>
      )}
    </>
  );
}
