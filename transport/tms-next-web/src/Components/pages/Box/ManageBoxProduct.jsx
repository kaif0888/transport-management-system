"use client";
import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { Card, Table, Modal, InputNumber, Button as AntButton, Tag, Statistic, Spin } from "antd";
import { toast } from "react-toastify";
import { Button } from "@/Components/atom/Button";
import { Select } from "@/Components/atom/Select";
import { getBoxById, addProductToBox, removeProductFromBox } from "@/service/box";
import { getAllProduct } from "@/service/product";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { ArrowLeftOutlined, PlusOutlined, DeleteOutlined } from "@ant-design/icons";

export default function ManageBoxProducts() {
  const params = useParams();
  const router = useRouter();
  const boxId = params?.boxId;

  const [box, setBox] = useState(null);
  const [loading, setLoading] = useState(true);
  const [addModal, setAddModal] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [productOptions, setProductOptions] = useState([]);
  const [addingProduct, setAddingProduct] = useState(false);

  useEffect(() => {
    console.log(" Component mounted with boxId:", boxId);
    if (boxId) {
      loadBoxData();
      loadProducts();
    } else {
      console.error(" No boxId found in params");
      toast.error("Invalid box ID");
      router.push("/Box/BoxList");
    }
  }, [boxId]);

  const loadBoxData = async () => {
    try {
      console.log(" Loading box data for:", boxId);
      setLoading(true);
      const data = await getBoxById(boxId);
      console.log(" Box data loaded:", data);
      setBox(data);
    } catch (error) {
      console.error(" Failed to load box data:", error);
      toast.error("Failed to load box data");
      // Don't redirect immediately, user might want to retry
    } finally {
      setLoading(false);
    }
  };

  const loadProducts = async () => {
    try {
      console.log(" Loading available products");
      const data = await getAllProduct({ limit: 0, filters: [] });
      console.log(" Products loaded:", data?.length || 0);
      
      const options = (data || []).map((p) => ({
        label: `${p.productName} (${p.productCode}) - ₹${p.price}`,
        value: p.productId,
        data: p,
      }));
      setProductOptions(options);
    } catch (error) {
      console.error(" Failed to load products:", error);
      toast.error("Failed to load products");
    }
  };

  const handleAddProduct = async () => {
    if (!selectedProduct || !quantity || quantity <= 0) {
      toast.error("Please select a product and enter a valid quantity");
      return;
    }

    try {
      setAddingProduct(true);
      console.log(" Adding product:", selectedProduct, "quantity:", quantity);
      
      await addProductToBox(boxId, selectedProduct, quantity);
      toast.success("Product added to box successfully");
      
      setAddModal(false);
      setSelectedProduct(null);
      setQuantity(1);
      
      await loadBoxData();
    } catch (error) {
      console.error(" Failed to add product:", error);
      toast.error(error.message || "Failed to add product");
    } finally {
      setAddingProduct(false);
    }
  };

  const handleRemoveProduct = async (productId, productName) => {
    Modal.confirm({
      title: "Remove Product",
      content: `Are you sure you want to remove "${productName}" from this box?`,
      okText: "Remove",
      okButtonProps: { danger: true },
      onOk: async () => {
        try {
          console.log(" Removing product:", productId);
          await removeProductFromBox(boxId, productId);
          toast.success("Product removed successfully");
          await loadBoxData();
        } catch (error) {
          console.error(" Failed to remove product:", error);
          toast.error("Failed to remove product");
        }
      },
    });
  };

  const handleBack = () => {
    console.log(" Navigating back to box list");
    router.push("/Box/BoxList");
  };

  const columns = [
    {
      title: "S.No",
      key: "sno",
      render: (_, __, index) => index + 1,
      width: 70,
      align: "center",
    },
    {
      title: "Product Name",
      dataIndex: "productName",
      key: "productName",
      width: 200,
    },
    {
      title: "Product Code",
      dataIndex: "productCode",
      key: "productCode",
      width: 150,
    },
    {
      title: "Quantity",
      dataIndex: "quantity",
      key: "quantity",
      align: "center",
      width: 100,
    },
    {
      title: "Weight/Unit (Kg)",
      dataIndex: "weightPerUnit",
      key: "weightPerUnit",
      align: "right",
      width: 130,
      render: (val) => val?.toFixed(2) || "0.00",
    },
    {
      title: "Total Weight (Kg)",
      dataIndex: "totalWeight",
      key: "totalWeight",
      align: "right",
      width: 140,
      render: (val) => val?.toFixed(2) || "0.00",
    },
    {
      title: "Price/Unit (₹)",
      dataIndex: "pricePerUnit",
      key: "pricePerUnit",
      align: "right",
      width: 130,
      render: (val) => `₹${val?.toFixed(2) || "0.00"}`,
    },
    {
      title: "Total Price (₹)",
      dataIndex: "totalPrice",
      key: "totalPrice",
      align: "right",
      width: 140,
      render: (val) => `₹${val?.toFixed(2) || "0.00"}`,
    },
    {
      title: "Actions",
      key: "actions",
      align: "center",
      width: 120,
      fixed: "right",
      render: (_, record) => (
        <AntButton
          type="primary"
          danger
          size="small"
          icon={<DeleteOutlined />}
          onClick={() => handleRemoveProduct(record.productId, record.productName)}
        >
          Remove
        </AntButton>
      ),
    },
  ];

  if (loading) {
    return (
      <PageWrapper>
        <div className="flex justify-center items-center h-96">
          <Spin size="large" tip="Loading box data..." />
        </div>
      </PageWrapper>
    );
  }

  if (!box) {
    return (
      <PageWrapper>
        <div className="flex flex-col justify-center items-center h-96 gap-4">
          <p className="text-lg text-gray-600">Box not found</p>
          <Button icon={<ArrowLeftOutlined />} onClick={handleBack}>
            Back to Box List
          </Button>
        </div>
      </PageWrapper>
    );
  }

  const totalWeight = box?.products?.reduce((sum, p) => sum + (p.totalWeight || 0), 0) || 0;
  const isOverWeight = totalWeight > (box?.maxWeight || 0);
  const productCount = box?.products?.length || 0;

  return (
    <PageWrapper>
      <div className="flex items-center gap-4 mb-4">
        <Button 
          icon={<ArrowLeftOutlined />} 
          onClick={handleBack}
          className="flex items-center gap-2"
        >
          Back to Box List
        </Button>
        <Title title={`Manage Products - ${box?.boxName || "Box"}`} />
      </div>

      {/* Box Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <Card>
          <Statistic title="Box Code" value={box?.boxCode || "N/A"} />
        </Card>
        <Card>
          <Statistic 
            title="HSN Code" 
            value={box?.hsnCode || "N/A"}
            suffix={box?.hsnDescription ? `(${box.hsnDescription.substring(0, 20)}...)` : ""}
          />
        </Card>
        <Card>
          <Statistic
            title="Total Weight (Kg)"
            value={totalWeight.toFixed(2)}
            suffix={`/ ${box?.maxWeight || 0}`}
            valueStyle={{ color: isOverWeight ? "#cf1322" : "#3f8600" }}
          />
        </Card>
        <Card>
          <Statistic
            title="Total Value"
            value={box?.totalValue || 0}
            precision={2}
            prefix="₹"
          />
        </Card>
      </div>

      {/* Warning for overweight */}
      {isOverWeight && (
        <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded">
          <p className="text-red-600 font-semibold">
             Warning: Total weight ({totalWeight.toFixed(2)} kg) exceeds maximum box capacity ({box?.maxWeight} kg)!
          </p>
        </div>
      )}

      {/* Products Table */}
      <Card
        title={
          <div className="flex justify-between items-center">
            <span>Products in Box</span>
            <Tag color={box?.status === "EMPTY" ? "default" : "blue"}>
              {box?.status} ({productCount} {productCount === 1 ? "product" : "products"})
            </Tag>
          </div>
        }
        extra={
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => setAddModal(true)}
            className="bg-[#750014]"
          >
            Add Product
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={box?.products || []}
          rowKey="id"
          pagination={false}
          scroll={{ x: 1200 }}
          locale={{
            emptyText: (
              <div className="py-8">
                <p className="text-gray-500 mb-4">No products in this box yet</p>
                <Button
                  type="primary"
                  icon={<PlusOutlined />}
                  onClick={() => setAddModal(true)}
                  className="bg-[#750014]"
                >
                  Add Your First Product
                </Button>
              </div>
            ),
          }}
        />
      </Card>

      {/* Add Product Modal */}
      <Modal
        title="Add Product to Box"
        open={addModal}
        onCancel={() => {
          setAddModal(false);
          setSelectedProduct(null);
          setQuantity(1);
        }}
        onOk={handleAddProduct}
        okText="Add Product"
        confirmLoading={addingProduct}
        okButtonProps={{
          disabled: !selectedProduct || !quantity || quantity <= 0,
          className: "bg-[#750014]",
        }}
      >
        <div className="space-y-4 py-4">
          <div>
            <label className="block text-sm font-medium mb-2">
              Select Product <span className="text-red-500">*</span>
            </label>
            <Select
              placeholder="Choose a product"
              value={selectedProduct}
              onChange={setSelectedProduct}
              options={productOptions}
              showSearch
              filterOption={(input, option) =>
                option.label.toLowerCase().includes(input.toLowerCase())
              }
              required
              fieldWidth="100%"
              disabled={productOptions.length === 0}
              notFoundContent={
                productOptions.length === 0
                  ? "No products available. Please add products first."
                  : "No matching products found"
              }
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-2">
              Quantity <span className="text-red-500">*</span>
            </label>
            <InputNumber
              min={1}
              value={quantity}
              onChange={setQuantity}
              style={{ width: "100%" }}
              placeholder="Enter quantity"
            />
          </div>
          {selectedProduct && (
            <div className="mt-2 p-3 bg-blue-50 border border-blue-200 rounded">
              <p className="text-sm text-gray-700">
                 <strong>Note:</strong> This will add the selected quantity to the box. 
                Make sure the total weight doesn't exceed the box capacity.
              </p>
            </div>
          )}
        </div>
      </Modal>
    </PageWrapper>
  );
}