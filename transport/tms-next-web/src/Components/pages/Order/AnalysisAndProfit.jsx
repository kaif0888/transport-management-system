"use client";

import React, { useState, useEffect } from "react";
import { Form, Input, Button, Card, Spin, Row, Col } from "antd";
import { toast } from "react-toastify";
import { useRouter } from "next/navigation";
import { getOrderById } from "@/service/order";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import { FiArrowLeft } from "react-icons/fi";

export default function AnalysisAndProfit({ slug }) {
  const router = useRouter();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [orderData, setOrderData] = useState(null);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [expenses, setExpenses] = useState({
    fuelExpense: "",
    driverExpense: "",
    maintenanceExpense: "",
  });

  // Fetch consignment/order data
  useEffect(() => {
    const fetchOrderData = async () => {
      try {
        setLoading(true);
        const data = await getOrderById(slug);
        console.log("Order data received:", data);
        setOrderData(data);
      } catch (error) {
        toast.error("Failed to load consignment details: " + error.message);
      } finally {
        setLoading(false);
      }
    };

    if (slug) {
      fetchOrderData();
    }
  }, [slug]);

  const handleExpenseChange = (field, value) => {
    setExpenses((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  // Calculate totals
  const totalExpenses =
    (parseFloat(expenses.fuelExpense) || 0) +
    (parseFloat(expenses.driverExpense) || 0) +
    (parseFloat(expenses.maintenanceExpense) || 0);
  const payableAmount = orderData?.totalAmount || 0;
  const profit = payableAmount - totalExpenses;

  const handleSubmit = async () => {
    // Validate that all fields are filled
    if (
      !expenses.fuelExpense ||
      !expenses.driverExpense ||
      !expenses.maintenanceExpense
    ) {
      toast.error("Please fill all expense fields");
      return;
    }

    try {
      setSubmitting(true);
      // Mark as submitted to show profit calculation
      setIsSubmitted(true);
      toast.success("Analysis calculated successfully!");
    } catch (error) {
      toast.error("Error processing analysis: " + error.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleGoBack = () => {
    router.push("/consignment");
  };

if (loading) {
  return <Spin fullscreen tip="Loading..." />;
}


  return (
    <PageWrapper>
      <div className="mb-6 flex items-center gap-2">
        <button
          onClick={handleGoBack}
          className="flex items-center gap-2 px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded transition"
        >
          <FiArrowLeft /> Back
        </button>
      </div>

      <Title title="Analysis & Profit" className="text-center mb-8" />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Expense Input Card */}
        <Card className="shadow-lg">
          <h2 className="text-2xl font-bold mb-6">Expense Details</h2>
          <Form layout="vertical" form={form}>
            <Form.Item label="Fuel Expense" required>
              <Input
                type="number"
                placeholder="Enter fuel expense"
                value={expenses.fuelExpense}
                onChange={(e) =>
                  handleExpenseChange("fuelExpense", e.target.value)
                }
                className="h-[40px]"
                min="0"
                step="0.01"
              />
            </Form.Item>

            <Form.Item label="Driver Expense" required>
              <Input
                type="number"
                placeholder="Enter driver expense"
                value={expenses.driverExpense}
                onChange={(e) =>
                  handleExpenseChange("driverExpense", e.target.value)
                }
                className="h-[40px]"
                min="0"
                step="0.01"
              />
            </Form.Item>

            <Form.Item label="Maintenance Expense" required>
              <Input
                type="number"
                placeholder="Enter maintenance expense"
                value={expenses.maintenanceExpense}
                onChange={(e) =>
                  handleExpenseChange("maintenanceExpense", e.target.value)
                }
                className="h-[40px]"
                min="0"
                step="0.01"
              />
            </Form.Item>

            <Button
              type="primary"
              size="large"
              onClick={handleSubmit}
              loading={submitting}
              disabled={submitting}
              className="w-full font-semibold bg-[#750014]"
            >
              Submit & Calculate Profit
            </Button>
          </Form>
        </Card>

        {/* Summary Card */}
        <Card className="shadow-lg bg-gradient-to-br from-blue-50 to-indigo-50">
          <h2 className="text-2xl font-bold mb-6">Summary</h2>
          <div className="space-y-4">
            {/* Order ID */}
            <Row className="border-b pb-3">
              <Col span={12}>
                <span className="text-gray-700 font-medium">Order ID:</span>
              </Col>
              <Col span={12} className="text-right">
                <span className="font-bold text-lg">{orderData?.orderId}</span>
              </Col>
            </Row>

            {/* Payable Amount */}
            <Row className="border-b pb-3">
              <Col span={12}>
                <span className="text-gray-700 font-medium">
                  Payable Amount:
                </span>
              </Col>
              <Col span={12} className="text-right">
                <span className="font-bold text-lg text-green-600">
                  ₹ {payableAmount.toFixed(2)}
                </span>
              </Col>
            </Row>

            {/* Expense Breakdown - Only show after submit */}
            {isSubmitted && (
              <>
                <Row className="border-b pb-2">
                  <Col span={12}>
                    <span className="text-gray-600 text-sm">Fuel Expense:</span>
                  </Col>
                  <Col span={12} className="text-right">
                    <span className="text-sm">₹ {(parseFloat(expenses.fuelExpense) || 0).toFixed(2)}</span>
                  </Col>
                </Row>

                <Row className="border-b pb-2">
                  <Col span={12}>
                    <span className="text-gray-600 text-sm">Driver Expense:</span>
                  </Col>
                  <Col span={12} className="text-right">
                    <span className="text-sm">₹ {(parseFloat(expenses.driverExpense) || 0).toFixed(2)}</span>
                  </Col>
                </Row>

                <Row className="border-b pb-2">
                  <Col span={12}>
                    <span className="text-gray-600 text-sm">
                      Maintenance Expense:
                    </span>
                  </Col>
                  <Col span={12} className="text-right">
                    <span className="text-sm">
                      ₹ {(parseFloat(expenses.maintenanceExpense) || 0).toFixed(2)}
                    </span>
                  </Col>
                </Row>

                {/* Total Expenses */}
                <Row className="border-b pb-3 bg-yellow-100 p-2 rounded">
                  <Col span={12}>
                    <span className="text-gray-700 font-semibold">
                      Total Expenses:
                    </span>
                  </Col>
                  <Col span={12} className="text-right">
                    <span className="font-bold text-lg text-orange-600">
                      ₹ {totalExpenses.toFixed(2)}
                    </span>
                  </Col>
                </Row>

                {/* Profit/Loss */}
                <Row
                  className={`p-3 rounded text-white font-bold text-lg ${
                    profit >= 0 ? "bg-green-600" : "bg-red-600"
                  }`}
                >
                  <Col span={12}>
                    <span>{profit >= 0 ? "Profit" : "Loss"}:</span>
                  </Col>
                  <Col span={12} className="text-right">
                    <span>₹ {profit.toFixed(2)}</span>
                  </Col>
                </Row>
              </>
            )}

            {/* Message before submit */}
            {!isSubmitted && (
              <div className="text-center py-8 text-gray-500">
                <p className="text-lg">Enter all expenses and click Submit to view profit calculation</p>
              </div>
            )}
          </div>
        </Card>
      </div>
    </PageWrapper>
  );
}
