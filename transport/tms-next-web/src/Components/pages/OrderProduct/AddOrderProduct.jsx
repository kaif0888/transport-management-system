"use client";
import { useState, useMemo, useCallback, useEffect, memo } from "react";
import Title from "@/Components/atom/Title";
import PageWrapper from "@/Data/PageWrapper";
import { getOrderById } from "@/service/order";
import { useQuery } from "@tanstack/react-query";
import { Card, Select } from "antd";
import { AddOrderProducts } from "@/service/order-product";
import { getAllProduct, getAllHsnCodes } from "@/service/product";
import { Table } from "@/Components/atom/Table";
import { producttColumn } from "@/Data/TableColumn";
import { Button } from "@/Components/atom/Button";
import { Input } from "@/Components/atom/Input";
import { IoMdAdd, IoMdClose } from "react-icons/io";
import { SearchOutlined } from "@ant-design/icons";
import { useFormik } from "formik";
import { useRouter } from "next/navigation";
import { toast } from "react-toastify";

/* ---------------- UI Helpers ---------------- */

const CardValue = ({ field, value }) => (
  <div className="flex gap-2">
    <p className="font-semibold">{field}</p>
    <p>{value || "N/A"}</p>
  </div>
);

const SearchInput = memo(({ value, onChange }) => (
  <Input
    value={value}
    onChange={onChange}
    placeholder="Search by Box Name"
    startIcon={<SearchOutlined />}
  />
));

/* ================= MAIN ================= */

export default function AddOrderProduct({ slug }) {
  const router = useRouter();

  const [productName, setProductSearchName] = useState("");
  const [filterPayload, setFilterPayload] = useState({ limit: 0, filters: [] });
  const [addedProducts, setAddedProducts] = useState([]);

  /* ---------- Derived ---------- */
  const addedProductIds = useMemo(
    () => addedProducts.map(p => p.productId),
    [addedProducts]
  );

  /* ---------- Queries ---------- */

  const { data: orderDetail, isLoading: orderLoading } = useQuery({
    queryKey: ["orderViewDetail", slug],
    queryFn: () => getOrderById(slug),
    enabled: !!slug,
  });

  const { data: hsnCodes = [], isLoading: hsnLoading } = useQuery({
    queryKey: ["hsnCodes"],
    queryFn: getAllHsnCodes,
  });

  /* ---------- Format Products ---------- */

  const formatData = useCallback(
    (data = []) =>
      data.map(p => ({
        ...p,
        key: p.productId,
        isAlreadyAdded: addedProductIds.includes(p.productId),
      })),
    [addedProductIds]
  );

  /* ---------- Formik ---------- */

  const formik = useFormik({
    initialValues: { orderId: slug, products: [] },
    enableReinitialize: true,
    onSubmit: async (values) => {
      if (addedProducts.some(p => !p.hsnCode)) {
        toast.error("Please select HSN Code for all boxes");
        return;
      }
      await AddOrderProducts(values);
      toast.success("Consignment Boxes saved successfully");
      router.push("/consignment/confirm");
    },
  });

  /* ---------- Handlers ---------- */

  const handleAddOrderProduct = (_, product) => {
    if (addedProductIds.includes(product.productId)) return;

   setAddedProducts(prev => [
  ...prev,
  {
    ...product,
    quantity: 1,
    hsnCode: "",
    description: product.description || "", 
  },
]);
}

const handleDescriptionChange = (productId, value) => {
  setAddedProducts(prev =>
    prev.map(item =>
      item.productId === productId
        ? { ...item, description: value }
        : item
    )
  );
};


  const handleRemoveProduct = (id) =>
    setAddedProducts(prev => prev.filter(p => p.productId !== id));

  const handleQuantityChange = (id, qty) =>
    setAddedProducts(prev =>
      prev.map(p =>
        p.productId === id ? { ...p, quantity: +qty } : p
      )
    );

  const handleHsnChange = (id, hsnCode) =>
    setAddedProducts(prev =>
      prev.map(p =>
        p.productId === id ? { ...p, hsnCode } : p
      )
    );

  useEffect(() => {
    formik.setFieldValue(
      "products",
      addedProducts.map(p => ({
        productId: p.productId,
        quantity: p.quantity,
        hsnCode: p.hsnCode,
        description: p.description,
      }))
    );
  }, [addedProducts]);

  /* ---------- Search ---------- */

  const handleProductSearchChange = (e) => {
    const value = e.target.value;
    setProductSearchName(value);
    setFilterPayload({
      limit: 0,
      filters:
        value.length >= 2
          ? [{ attribute: "productName", operation: "CONTAINS", value }]
          : [],
    });
  };

  const productQuery = {
    queryKey: ["productList", filterPayload, addedProductIds],
    queryFn: () => getAllProduct(filterPayload),
    enabled: !!slug,
  };

  /* ---------- Columns ---------- */

  const columns = useMemo(() => [
    ...producttColumn,
    {
      title: "Status",
      align: "center",
      render: (_, r) =>
        r.isAlreadyAdded ? (
          <span className="text-orange-600 font-semibold">Already Added</span>
        ) : (
          <span className="text-green-600 font-semibold">Available</span>
        ),
    },
    {
      title: "Action",
      align: "center",
      render: (_, r) => (
        <Button
          icon={<IoMdAdd />}
          disabled={r.isAlreadyAdded}
          className={
            r.isAlreadyAdded
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-green-600 text-white"
          }
          onClick={() => handleAddOrderProduct(r.productId, r)}
        />
      ),
    },
  ], [addedProductIds]);

  const addedProductColumns = [
    { title: "Box Type", dataIndex: "boxName" },
    {
      title: "HSN Code",
      render: (_, r) => (
        <Select
          loading={hsnLoading}
          value={r.hsnCode || undefined}
          onChange={(v) => handleHsnChange(r.productId, v)}
          style={{ width: "100%" }}
          options={hsnCodes.map(h => ({
            value: h.hsnCode,
            label: `${h.hsnCode} - ${h.description}`,
          }))}
        />
      ),
    },
   {
  title: "Description",
  render: (_, r) => (
    <Input
      placeholder="Enter description"
      value={r.description || ""}
      onChange={(e) =>
        handleDescriptionChange(r.productId, e.target.value)
      }
    />
  ),
},
 {
      title: "Qty",
      render: (_, r) => (
        <Input
          type="number"
          min={1}
          value={r.quantity}
          onChange={(e) =>
            handleQuantityChange(r.productId, e.target.value)
          }
        />
      ),
    },
    {
      title: "Action",
      render: (_, r) => (
        <Button
          icon={<IoMdClose />}
          className="bg-red-600 text-white"
          onClick={() => handleRemoveProduct(r.productId)}
        />
      ),
    },
  ];

  /* ================= UI ================= */

  return (
    <PageWrapper>
      <Title title="Add Consignment Box" className="text-center" />

      {/* ✅ CONSIGNMENT INFORMATION */}
      <Card className="mb-6" loading={orderLoading}>
        <Title title="Consignment Information" level={1} />
        <div className="grid grid-cols-3 gap-6 mt-4">
          <CardValue field="Consignment ID:" value={orderDetail?.orderNo} />
          <CardValue field="Customer:" value={orderDetail?.customerName} />
          <CardValue field="Receiver:" value={orderDetail?.receiverName} />
          <CardValue
            field="Origin Location:"
            value={`${orderDetail?.originAddress}, ${orderDetail?.originCity} (${orderDetail?.originState}) - ${orderDetail?.originPincode}`}
          />
          <CardValue
            field="Destination Location:"
            value={orderDetail?.destinationAddress}
          />
        </div>
      </Card>

      {/* ✅ ADDED BOXES */}
      <Card className="mb-6">
        <Table
          dataSource={addedProducts}
          columns={addedProductColumns}
          pagination={false}
        />
        <div className="flex justify-end mt-4">
          <Button
            disabled={!addedProducts.length}
            onClick={formik.handleSubmit}
            className="bg-emerald-600 text-white"
          >
            Save Consignment Box
          </Button>
        </div>
      </Card>

      {/* ✅ PRODUCT CATALOG */}
      <Card
        title="Product Catalog"
        extra={
          <SearchInput
            value={productName}
            onChange={handleProductSearchChange}
          />
        }
      >
        <Table
          query={productQuery}
          formatData={formatData}
          columns={columns}
        />
      </Card>
    </PageWrapper>
  );
}
