"use client";
import { useState, useRef, useCallback, useMemo, memo } from "react";
import { Modal, Tooltip } from "antd";
import { toast } from "react-toastify";
import { useRouter } from "next/navigation";
import { orderConfirmColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { getAllOrder, UpdateOrderStatus } from "@/service/order";
import { Table } from "@/Components/atom/Table";
import { Input } from "@/Components/atom/Input";
import PageWrapper from "@/Data/PageWrapper";
import { FaRegEye } from "react-icons/fa";
import Title from "@/Components/atom/Title";
import { IoFilter } from "react-icons/io5";
import { GiConfirmed } from "react-icons/gi";
import { SearchOutlined } from "@ant-design/icons";
import dynamic from "next/dynamic";
import PaymentForm from "./PaymentModal";
import { AddPayment } from "@/service/payment";

const SEARCH_MIN_LENGTH = 2;
const DEFAULT_PAGE_SIZE = 10;
const PAGE_SIZE_OPTIONS = ["10", "20", "50"];

const OrderFilter = dynamic(
  () => import("@/Components/pages/Order/OrderFilter"),
  {
    ssr: false,
    loading: () => <div className="p-4">Loading filter...</div>,
  }
);

const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    id="search"
    value={value}
    onChange={onChange}
    placeholder="Search orders..."
    name="search"
    className="h-[35px]"
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

SearchInput.displayName = "SearchInput";

const ActionButtons = memo(
  ({
    orderId,
    customerId,
    onConfirm,
    onView,
    onPayment,
    dueAmount,
    paidAmount,
    totalAmount,
    paymentStatus,
  }) => (
    <div className="flex gap-2">
      <Tooltip title="Confirm Order">
        <Button
          key="confirm"
          className="font-semibold border-emerald-700! hover:border-emerald-850! disabled:border-gray-300! "
          onClick={() => onConfirm(orderId)}
          disabled={paymentStatus === "un-paid"}
          icon={
            <GiConfirmed
              className={`text-lg ${
                paymentStatus !== "un-paid"
                  ? "text-emerald-700!"
                  : "text-gray-500!"
              }`}
            />
          }
        />
      </Tooltip>
      <Tooltip title="View Order">
        <Button
          key="view"
          className="font-semibold"
          onClick={() => onView(orderId)}
          icon={<FaRegEye className="text-lg" />}
        />
      </Tooltip>
      <Tooltip title="Payment">
        <Button
          key="payment"
          disabled={dueAmount === 0}
          className="text-[25px]! leading-0! items-center! pb-[5px]! px-1!"
          onClick={() =>
            onPayment(orderId, customerId, dueAmount, paidAmount, totalAmount)
          }
        >
          💳
        </Button>
      </Tooltip>
    </div>
  )
);

ActionButtons.displayName = "ActionButtons";

const DEFAULT_STATUS_FILTER = {
  attribute: "status",
  operation: "EQUALS",
  value: "CREATED",
};

const createBaseFilter = () => ({
  limit: 0,
  filters: [DEFAULT_STATUS_FILTER],
});

const createSearchFilter = (searchValue) => ({
  attribute: "orderId",
  operation: "CONTAINS",
  value: searchValue,
});

const updateFiltersArray = (filters, newFilter, attributeToReplace) => {
  const existingIndex = filters.findIndex(
    (f) => f.attribute === attributeToReplace
  );

  if (existingIndex >= 0) {
    filters[existingIndex] = newFilter;
  } else {
    filters.push(newFilter);
  }

  return filters;
};

const removeFilterByAttribute = (filters, attribute) =>
  filters.filter((f) => f.attribute !== attribute);

const ensureDefaultFilter = (filterPayload) => {
  const hasStatusFilter = filterPayload.filters?.some(
    (f) => f.attribute === "status"
  );

  if (!hasStatusFilter) {
    return {
      ...filterPayload,
      filters: [DEFAULT_STATUS_FILTER, ...(filterPayload.filters || [])],
    };
  }

  return filterPayload;
};

const useOrderFilters = (initialSearch = "") => {
  const [search, setSearch] = useState(initialSearch);
  const [filterPayload, setFilterPayload] = useState(createBaseFilter());
  const [activeFilters, setActiveFilters] = useState({});
  // Add state to track current pagination

  const handleSearchChange = useCallback((e) => {
    const value = e.target.value;
    setSearch(value);

    setFilterPayload((prevPayload) => {
      const newFilters = { ...prevPayload };

      if (value.length >= SEARCH_MIN_LENGTH) {
        newFilters.filters = updateFiltersArray(
          [...newFilters.filters],
          createSearchFilter(value),
          "orderId"
        );
      } else {
        newFilters.filters = removeFilterByAttribute(
          newFilters.filters,
          "orderId"
        );
      }

      return newFilters;
    });
  }, []);

  const handleApplyFilter = useCallback(
    (payload) => {
      const normalizedPayload = {
        limit: payload.limit || 0,
        filters: payload.filters || [],
      };

      const payloadWithDefaults = ensureDefaultFilter(normalizedPayload);

      const searchFilter = filterPayload.filters.find(
        (f) => f.attribute === "orderId"
      );

      if (searchFilter) {
        payloadWithDefaults.filters = updateFiltersArray(
          [...payloadWithDefaults.filters],
          searchFilter,
          "orderId"
        );
      }

      const active = {};
      payloadWithDefaults.filters?.forEach((filter) => {
        if (filter.attribute === "paymentStatus") {
          active.paymentStatus = filter.value;
        } else if (
          filter.attribute === "status" &&
          filter.value !== "CREATED"
        ) {
          active.status = filter.value;
        }
      });

      setActiveFilters(active);
      setFilterPayload(payloadWithDefaults);
    },
    [filterPayload.filters]
  );

  const clearFilters = useCallback(() => {
    const newFilters = createBaseFilter();

    if (search.length >= SEARCH_MIN_LENGTH) {
      newFilters.filters.push(createSearchFilter(search));
    }

    setFilterPayload(newFilters);
    setActiveFilters({});
  }, [search]);

  return {
    search,
    filterPayload,
    activeFilters,
    handleSearchChange,
    handleApplyFilter,
    clearFilters,
  };
};

const useOrderActions = (tableRef) => {
  const router = useRouter();
  const [confirmModal, setConfirmModal] = useState({
    open: false,
    orderId: null,
  });
  const [paymentModal, setPaymentModal] = useState({
    open: false,
    orderId: null,
    customerId: null,
    dueAmount: null,
    paidAmount: null,
    totalAmount: null,
  });
  const handleViewOrder = useCallback(
    (orderId) => {
      router.push(`/consignment/${orderId}`);
    },
    [router]
  );

  const handleConfirmModal = useCallback((orderId = null) => {
    setConfirmModal((prev) => ({
      open: !prev.open,
      orderId: prev.open ? null : orderId,
    }));
  }, []);

  // Fixed: Accept totalAmount as a parameter
  const handlePayment = useCallback(
    (orderId, customerId, dueAmount, paidAmount, totalAmount) => {
      setPaymentModal({
        open: true,
        orderId,
        customerId,
        dueAmount,
        paidAmount,
        totalAmount, // Now properly using the parameter
      });
    },
    []
  );

  const handlePaymentModal = useCallback(() => {
    setPaymentModal({
      open: false,
      orderId: null,
      customerId: null,
      dueAmount: null,
      paidAmount: null,
      totalAmount: null,
    });
  }, []);

  const confirmOrder = useCallback(async () => {
    if (!confirmModal.orderId) return;

    try {
      await UpdateOrderStatus(confirmModal.orderId);
      tableRef.current?.Refetch();
      toast.success("Consignment confirmed successfully");
    } catch (error) {
      toast.error(`Failed to confirm consignment: ${error.message}`);
    } finally {
      handleConfirmModal();
    }
  }, [confirmModal.orderId, tableRef, handleConfirmModal]);

  const processPayment = useCallback(
    async (paymentData) => {
      try {
        // Add your payment processing API call here
        await AddPayment(paymentData);

        toast.success("Payment processed successfully");
        tableRef.current?.Refetch();
        handlePaymentModal();
      } catch (error) {
        toast.error(`Payment failed: ${error.message}`);
        throw error; // Re-throw to handle in form
      }
    },
    [tableRef, handlePaymentModal]
  );

  return {
    confirmModal,
    paymentModal,
    handleViewOrder,
    handleConfirmModal,
    handlePayment,
    handlePaymentModal,
    confirmOrder,
    processPayment,
  };
};

export default function OrderConfirm() {
  const tableRef = useRef(null);
  const [filterModal, setFilterModal] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const {
    search,
    filterPayload,
    activeFilters,
    handleSearchChange,
    handleApplyFilter,
    clearFilters,
  } = useOrderFilters();

  const {
    confirmModal,
    paymentModal,
    handleViewOrder,
    handleConfirmModal,
    handlePayment,
    handlePaymentModal,
    confirmOrder,
    processPayment,
  } = useOrderActions(tableRef);

  const tableQuery = useMemo(
    () => ({
      queryKey: ["orderConfirmList", filterPayload],
      queryFn: () => {
        const finalPayload = ensureDefaultFilter(filterPayload);
        return getAllOrder(finalPayload);
      },
      onError: (error) => {
        toast.error(`Failed to load consignment list: ${error.message}`);
      },
    }),
    [filterPayload]
  );

  const formatData = useCallback(
    (data) =>
      data?.map((item) => ({
        ...item,
        key: item.orderId,
      })) || [],
    []
  );
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
      ...orderConfirmColumn,
      {
        title: "Actions",
        dataIndex: "orderId",
        key: "actions",
        className: "px-2 text-center",
        align: "center",
        render: (orderId, record) => (
          <ActionButtons
            orderId={orderId}
            customerId={record.customerId}
            dueAmount={record.remainingPayment}
            paidAmount={record.advancePayment}
            totalAmount={record.totalAmount}
            onConfirm={handleConfirmModal}
            onView={handleViewOrder}
            onPayment={handlePayment}
            paymentStatus={record.paymentStatus}
          />
        ),
      },
    ],
    [handleConfirmModal, handleViewOrder, handlePayment, currentPage, pageSize]
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

  const hasActiveAdvancedFilters = useMemo(
    () =>
      filterPayload?.filters?.some(
        (f) =>
          f.attribute !== "orderId" &&
          !(f.attribute === "status" && f.value === "CREATED")
      ),
    [filterPayload.filters]
  );

  const extraHeaderContent = useMemo(
    () => (
      <div className="flex justify-between items-center w-full">
        <div className="flex gap-2 w-[400px]">
          <SearchInput value={search} onChange={handleSearchChange} />
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
        </div>
      </div>
    ),
    [search, handleSearchChange, hasActiveAdvancedFilters, clearFilters]
  );

  const handleFilterApply = useCallback(
    (payload) => {
      handleApplyFilter(payload);
      setFilterModal(false);
    },
    [handleApplyFilter]
  );

  return (
    <>
      <PageWrapper>
        <Title title="Consignment Confirm List" className="text-center" />
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

      {filterModal && (
        <Modal
          title={<h1 className="text-2xl">Filter Consignment</h1>}
          open={filterModal}
          onCancel={() => setFilterModal(false)}
          width="800px"
          destroyOnClose={true}
          footer={null}
        >
          <OrderFilter
            onApplyFilter={handleFilterApply}
            onCancel={() => setFilterModal(false)}
            initialFilters={activeFilters}
          />
        </Modal>
      )}

      <Modal
        title="Confirm Consignment"
        open={confirmModal.open}
        onCancel={handleConfirmModal}
        onOk={confirmOrder}
        okButtonProps={{
          className: "font-semibold bg-[#750014]!",
          type: "primary",
        }}
        okText="Confirm"
      >
        <p className="text-base text-gray-700">
          Are you sure you want to confirm this consignment?
        </p>
      </Modal>

      <Modal
        title={
          <div className="flex-flex-col gap-2 bg-[#750014] px-[24px]! pb-[10px] text-white! pt-[20px]! rounded-t-lg">
            <div className="flex items-center gap-2">
              <span className="text-2xl pb-[10px]">💳</span>
              <h1 className="text-2xl">Make Payment</h1>
            </div>
            <p className="font-light! tracking-wider">Complete your payment</p>
          </div>
        }
        // open={true}
        open={paymentModal.open}
        onCancel={handlePaymentModal}
        destroyOnHidden={true}
        footer={null}
        okButtonProps={{
          className: "font-semibold bg-[#750014]!",
          type: "primary",
        }}
        className="payment-modal"
        okText="Process Payment"
        width="600px"
      >
        <div className="p-6">
          {/* Order Details Summary */}
          <div className="bg-gray-50 p-4 rounded-lg mb-4">
            <h3 className="text-sm font-semibold mb-2 text-gray-700">
              Order Information
            </h3>
            <div className="space-y-1 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-600">Order ID:</span>
                <span className="font-medium text-gray-800">
                  {paymentModal.orderId}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Customer ID:</span>
                <span className="font-medium text-gray-800">
                  {paymentModal.customerId}
                </span>
              </div>
            </div>
          </div>

          {/* Payment Form */}
          <PaymentForm
            onSubmit={processPayment}
            onCancel={handlePaymentModal}
            totalAmount={paymentModal.totalAmount}
            dueAmount={paymentModal.dueAmount}
            paidAmount={paymentModal.paidAmount}
            orderId={paymentModal.orderId}
            customerId={paymentModal.customerId}
          />
        </div>
      </Modal>
    </>
  );
}
