"use client";
import { useState, useRef, useMemo, useCallback, memo } from "react";
import { useRouter, useParams } from "next/navigation";
import { Modal, Tooltip, Tag, Card } from "antd";
import { toast } from "react-toastify";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { Table } from "@/Components/atom/Table";
import { getAllBox, addBoxToOrder, getBoxesForOrder } from "@/service/box";
import { getOrderById, confirmOrderStatus } from "@/service/order";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { ArrowLeftOutlined, SearchOutlined, CheckCircleOutlined, SaveOutlined } from "@ant-design/icons";
import { useQuery } from "@tanstack/react-query";

const SearchInput = memo(({ value, onChange }) => (
  <Input
    type="text"
    value={value}
    onChange={onChange}
    placeholder="Search by Box Name or Code"
    className="h-[35px]"
    startIcon={<SearchOutlined style={{ color: "#666", fontSize: 18 }} />}
  />
));
SearchInput.displayName = "SearchInput";

const CardValue = ({ field, value }) => (
  <div className="flex gap-2">
    <p className="text-gray-700 text-lg font-semibold">{field}</p>
    <p className="text-gray-700 text-base pt-[4px] capitalize">{value || "N/A"}</p>
  </div>
);

export default function AddBoxToConsignment() {
  const router = useRouter();
  const params = useParams();
  const slug = params?.slug;
  const tableRef = useRef(null);
  
  const [search, setSearch] = useState("");
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [confirmModal, setConfirmModal] = useState(false);
  const [selectedBox, setSelectedBox] = useState(null);
  const [addingBox, setAddingBox] = useState(false);
  const [savingConsignment, setSavingConsignment] = useState(false);

  // Fetch order details
  const {
    data: orderData,
    isLoading: isOrderLoading,
    isError: isOrderError,
    refetch: refetchOrderData,
  } = useQuery({
    queryKey: ["orderDetail", slug],
    queryFn: () => getOrderById(slug),
    enabled: !!slug,
  });

  // Fetch boxes already added to this order
  const {
    data: addedBoxes,
    isLoading: isAddedBoxesLoading,
    refetch: refetchAddedBoxes,
  } = useQuery({
    queryKey: ["orderAddedBoxes", slug],
    queryFn: async () => {
      console.log("📦 Fetching already added boxes for order:", slug);
      const data = await getBoxesForOrder(slug);
      console.log("✅ Added boxes:", data);
      return data;
    },
    enabled: !!slug,
  });

  // Fetch all available boxes
  const tableQuery = useMemo(
    () => ({
      queryKey: ["boxListForOrder", filterPayload],
      queryFn: () => getAllBox(filterPayload),
      onError: (error) => {
        toast.error("Failed to load boxes: " + error.message);
      },
    }),
    [filterPayload]
  );

  const cardDetail = useMemo(
    () => [
      { field: "Consignment ID:", value: slug },
      { field: "Customer:", value: orderData?.customerName },
      { field: "Receiver:", value: orderData?.receiverName },
      { field: "Origin Location:", value: orderData?.originLocationName },
      { field: "Destination Location:", value: orderData?.destinationLocationName },
      { field: "Status:", value: orderData?.status },
    ],
    [slug, orderData]
  );

  const handleSearchChange = useCallback(
    (e) => {
      const value = e.target.value;
      setSearch(value);

      const newFilters = { ...filterPayload };

      if (value.length >= 2) {
        const searchIndex = newFilters.filters.findIndex(
          (f) => f.attribute === "boxName" || f.attribute === "boxCode"
        );

        if (searchIndex >= 0) {
          newFilters.filters[searchIndex].value = value;
        } else {
          newFilters.filters.push({
            attribute: "boxName",
            operation: "CONTAINS",
            value: value,
          });
        }
      } else {
        newFilters.filters = newFilters.filters.filter(
          (f) => f.attribute !== "boxName" && f.attribute !== "boxCode"
        );
      }

      setFilterPayload(newFilters);
    },
    [filterPayload]
  );

  const handleAddBoxClick = (box) => {
    // Check if box is already added
    const isAlreadyAdded = addedBoxes?.some(b => b.boxId === box.boxId);
    if (isAlreadyAdded) {
      toast.warning("This box is already added to the consignment");
      return;
    }
    
    setSelectedBox(box);
    setConfirmModal(true);
  };

  const handleConfirmAddBox = async () => {
    if (!selectedBox) return;

    setAddingBox(true);
    try {
      await addBoxToOrder(slug, selectedBox.boxId);
      toast.success(`Box "${selectedBox.boxName}" added to consignment successfully`);
      
      // Refetch both tables and order data
      if (tableRef.current) {
        tableRef.current.Refetch();
      }
      await refetchAddedBoxes();
      await refetchOrderData();
      
      setConfirmModal(false);
      setSelectedBox(null);
    } catch (error) {
      console.error("❌ Failed to add box:", error);
      toast.error(error.message || "Failed to add box to consignment");
    } finally {
      setAddingBox(false);
    }
  };

  // Save Consignment - Confirm Order Status
  const handleSaveConsignment = async () => {
    if (!addedBoxes || addedBoxes.length === 0) {
      toast.error("Please add at least one box before saving the consignment");
      return;
    }

    Modal.confirm({
      title: "Confirm Consignment",
      content: (
        <div className="py-4">
          <p className="mb-2">Are you sure you want to save and confirm this consignment?</p>
          <div className="bg-blue-50 p-3 rounded mt-3">
            <p className="font-semibold text-gray-800">Summary:</p>
            <p className="text-gray-700">Total Boxes: {addedBoxes.length}</p>
            <p className="text-gray-700">
              Total Value: ₹{addedBoxes.reduce((sum, box) => sum + (box.totalValue || 0), 0).toFixed(2)}
            </p>
          </div>
          <p className="text-sm text-orange-600 mt-3">
            ⚠️ Once confirmed, the order status will change from PENDING to CONFIRM and boxes cannot be removed.
          </p>
        </div>
      ),
      okText: "Yes, Confirm Consignment",
      cancelText: "Cancel",
      okButtonProps: {
        className: "bg-[#28a745]",
      },
      onOk: async () => {
        setSavingConsignment(true);
        try {
          console.log("💾 Confirming order status for:", slug);
          await confirmOrderStatus(slug);
          toast.success("Consignment confirmed successfully!");
          
          // Redirect to consignment list
          setTimeout(() => {
            router.push("/consignment/consignment-product");
          }, 1500);
        } catch (error) {
          console.error("❌ Failed to confirm consignment:", error);
          toast.error(error.message || "Failed to confirm consignment");
        } finally {
          setSavingConsignment(false);
        }
      },
    });
  };

  const formatData = useCallback((data) => {
    // Filter out boxes that are already added
    const addedBoxIds = addedBoxes?.map(b => b.boxId) || [];
    const filtered = data?.filter(item => !addedBoxIds.includes(item.boxId)) || [];
    
    return filtered.map((item) => ({
      ...item,
      key: item.boxId,
    }));
  }, [addedBoxes]);

  const handlePaginationChange = useCallback((page, size) => {
    setCurrentPage(page);
    setPageSize(size);
  }, []);

  const getStatusColor = (status) => {
    const colors = {
      EMPTY: "default",
      PACKED: "blue",
      IN_TRANSIT: "orange",
      DELIVERED: "green",
    };
    return colors[status] || "default";
  };

  // Columns for already added boxes (simplified)
  const addedBoxesColumns = useMemo(
    () => [
      {
        title: "S.No",
        align: "center",
        width: 70,
        render: (_, __, index) => index + 1,
      },
      { 
        title: "Box Code", 
        dataIndex: "boxCode",
        width: 120,
      },
      { 
        title: "Box Name", 
        dataIndex: "boxName",
        width: 150,
      },
      { 
        title: "HSN Code", 
        dataIndex: "hsnCode",
        width: 100,
      },
      {
        title: "Total Value (₹)",
        dataIndex: "totalValue",
        align: "right",
        width: 130,
        render: (v) => `₹${v?.toFixed(2) || "0.00"}`,
      },
      {
        title: "Products",
        dataIndex: "products",
        align: "center",
        width: 100,
        render: (products) => products?.length || 0,
      },
      {
        title: "Status",
        dataIndex: "status",
        align: "center",
        width: 100,
        render: (s) => <Tag color={getStatusColor(s)}>{s}</Tag>,
      },
    ],
    []
  );

  // Columns for available boxes to add
  const columns = useMemo(
    () => [
      {
        title: "S.No",
        align: "center",
        width: 70,
        render: (_, __, index) => (currentPage - 1) * pageSize + index + 1,
      },
      { title: "Box Code", dataIndex: "boxCode", width: 120 },
      { title: "Box Name", dataIndex: "boxName", width: 150 },
      { title: "HSN Code", dataIndex: "hsnCode", width: 100 },
      {
        title: "Total Value (₹)",
        dataIndex: "totalValue",
        align: "right",
        width: 130,
        render: (v) => `₹${v?.toFixed(2) || "0.00"}`,
      },
      {
        title: "Products",
        dataIndex: "products",
        align: "center",
        width: 100,
        render: (products) => products?.length || 0,
      },
      {
        title: "Status",
        dataIndex: "status",
        align: "center",
        width: 100,
        render: (s) => <Tag color={getStatusColor(s)}>{s}</Tag>,
      },
      {
        title: "Actions",
        align: "center",
        width: 120,
        fixed: "right",
        render: (_, record) => {
          const isDisabled = record.status !== "PACKED";
          const tooltipText = isDisabled
            ? record.status === "EMPTY"
              ? "Box is empty - add products first"
              : "Box is already in transit or delivered"
            : "Add this box to consignment";

          return (
            <Tooltip title={tooltipText}>
              <Button
                icon={<CheckCircleOutlined />}
                className={`font-semibold ${
                  isDisabled
                    ? "bg-gray-400 text-gray-600"
                    : "bg-[#28a745] text-white hover:bg-[#218838]"
                }`}
                onClick={() => handleAddBoxClick(record)}
                disabled={isDisabled}
              >
                Add Box
              </Button>
            </Tooltip>
          );
        },
      },
    ],
    [currentPage, pageSize]
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
        <Button
          icon={<ArrowLeftOutlined />}
          onClick={() => router.push("/consignment/consignment-product")}
        >
          Back to Consignment List
        </Button>
      </div>
    ),
    [search, handleSearchChange, router]
  );

  // Check if order status is PENDING (can add more boxes)
  const canAddBoxes = orderData?.status?.toUpperCase() === "PENDING";
  const hasAddedBoxes = addedBoxes && addedBoxes.length > 0;

  if (!slug) {
    return (
      <PageWrapper>
        <Title title="Add Box to Consignment" className="text-center" />
        <div className="text-center text-red-500 mt-4">
          Consignment ID is required to add boxes.
        </div>
      </PageWrapper>
    );
  }

  if (isOrderLoading) {
    return (
      <PageWrapper>
        <Title title="Add Box to Consignment" className="text-center" />
        <div className="flex justify-center items-center mt-8">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto mb-4"></div>
            <div className="text-gray-500">Loading consignment details...</div>
          </div>
        </div>
      </PageWrapper>
    );
  }

  if (isOrderError || !orderData) {
    return (
      <PageWrapper>
        <Title title="Add Box to Consignment" className="text-center" />
        <div className="text-center mt-8">
          <div className="text-red-500 mb-4">Failed to load consignment data.</div>
          <Button onClick={() => window.location.reload()}>Retry</Button>
        </div>
      </PageWrapper>
    );
  }

  return (
    <>
      <PageWrapper>
        <Title title="Add Box to Consignment" className="text-center" />

        {/* Consignment Information Card */}
        <div className="flex flex-col gap-4 mt-6">
          <Card
            className="cardShadow border-0"
            title="Consignment Information"
            headStyle={{ backgroundColor: "#f8f9fa", fontWeight: "bold" }}
          >
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {cardDetail.map((item, index) => (
                <CardValue
                  key={`${item.field}-${index}`}
                  field={item.field}
                  value={item.value}
                />
              ))}
            </div>
          </Card>
        </div>

        {/* Already Added Boxes Table */}
        {hasAddedBoxes && (
          <div className="mt-6">
            <Card
              className="cardShadow border-0"
              title={`✅ Boxes Added to this Consignment (${addedBoxes.length})`}
              headStyle={{ backgroundColor: "#e8f5e9", fontWeight: "bold" }}
              extra={
                canAddBoxes && (
                  <Button
                    type="primary"
                    icon={<SaveOutlined />}
                    className="bg-[#28a745] text-white hover:bg-[#218838]"
                    onClick={handleSaveConsignment}
                    loading={savingConsignment}
                    size="large"
                  >
                    Save Consignment
                  </Button>
                )
              }
            >
              <Table
                dataSource={addedBoxes}
                columns={addedBoxesColumns}
                pagination={false}
                scroll={{ x: 800 }}
                rowKey="boxId"
              />
              
              {canAddBoxes && (
                <div className="mt-4 p-4 bg-blue-50 border border-blue-200 rounded">
                  <p className="text-sm text-blue-800">
                    💡 <strong>Next Step:</strong> Click "Save Consignment" button above to confirm this consignment and proceed further.
                  </p>
                </div>
              )}
            </Card>
          </div>
        )}

        {/* Available Boxes to Add */}
        {canAddBoxes && (
          <div className="mt-6">
            <Card
              className="cardShadow border-0"
              title="📦 Available Boxes (PACKED Status Only)"
              headStyle={{ backgroundColor: "#f8f9fa", fontWeight: "bold" }}
            >
              <Table
                ref={tableRef}
                query={tableQuery}
                formatData={formatData}
                columns={columns}
                pagination={paginationSettings}
                extraHeaderContent={extraHeaderContent}
                scroll={{ x: 1000 }}
              />
            </Card>
          </div>
        )}

        {/* Message when order is already confirmed */}
        {!canAddBoxes && (
          <div className="mt-6">
            <Card className="cardShadow border-0">
              <div className="text-center py-8">
                <p className="text-lg text-orange-600 font-semibold mb-2">
                  ⚠️ This consignment has been confirmed
                </p>
                <p className="text-gray-600">
                  Status: <Tag color="green">{orderData?.status}</Tag>
                </p>
                <p className="text-sm text-gray-500 mt-2">
                  No more boxes can be added to this consignment.
                </p>
                <Button
                  className="mt-4"
                  onClick={() => router.push("/consignment/consignment-product")}
                >
                  Back to Consignment List
                </Button>
              </div>
            </Card>
          </div>
        )}
      </PageWrapper>

      {/* Confirmation Modal for Adding Box */}
      <Modal
        title="Confirm Add Box"
        open={confirmModal}
        onCancel={() => {
          setConfirmModal(false);
          setSelectedBox(null);
        }}
        onOk={handleConfirmAddBox}
        okText="Add Box"
        confirmLoading={addingBox}
        okButtonProps={{
          className: "bg-[#28a745]",
        }}
      >
        {selectedBox && (
          <div className="py-4 space-y-3">
            <p className="text-base text-gray-700">
              Are you sure you want to add this box to the consignment?
            </p>
            <div className="bg-blue-50 p-4 rounded border border-blue-200">
              <p className="font-semibold text-gray-800">Box Details:</p>
              <p className="text-gray-700">Name: {selectedBox.boxName}</p>
              <p className="text-gray-700">Code: {selectedBox.boxCode}</p>
              <p className="text-gray-700">HSN Code: {selectedBox.hsnCode}</p>
              <p className="text-gray-700">
                Total Value: ₹{selectedBox.totalValue?.toFixed(2)}
              </p>
              <p className="text-gray-700">
                Products: {selectedBox.products?.length || 0}
              </p>
            </div>
            <p className="text-sm text-gray-600">
              ⚠️ Once added, the box status will change to "IN_TRANSIT".
            </p>
          </div>
        )}
      </Modal>
    </>
  );
}