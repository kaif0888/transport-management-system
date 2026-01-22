"use client";
import { ProgressBar } from "@/Components/atom/ProgressBar";
import Title from "@/Components/atom/Title";
import PageWrapper from "@/Data/PageWrapper";
import { getOrderById } from "@/service/order";
import { useQuery } from "@tanstack/react-query";
import { getAllPayment } from "@/service/payment";
import { IoMdCheckmarkCircleOutline } from "react-icons/io";
import moment from "moment";
import { SlCalender } from "react-icons/sl";
import { CiCreditCard1 } from "react-icons/ci";
import { Button } from "@/Components/atom/Button";

export default function PaymentHistory({ slug }) {
  const orderQuery = useQuery({
    queryKey: ["order", slug],
    queryFn: () => getOrderById(slug),
    enabled: !!slug,
  });

  const paymentQuery = useQuery({
    queryKey: ["payments", slug],
    queryFn: () => getAllPayment({
      limit: 0,
      filters: [{ attribute: "orderId", operation: "EQUALS", value: slug }],
    }),
    enabled: !!slug,
  });

  const order = orderQuery.data;
  const payments = paymentQuery.data || [];

  return (
    <PageWrapper>
      <Title title="Payment Log" />
      
      <div className="bg-gray-100 rounded-lg p-4 my-3">
        <h3 className="text-lg text-gray-500 font-semibold mb-3">Order Detail</h3>
        <div className="flex justify-between">
          {[
            { label: "Order Id", value: slug },
            { label: "Customer", value: order?.customerName },
            { label: "Receiver", value: order?.receiverName }
          ].map((item) => (
            <div key={item.label} className="flex gap-2 text-base">
              <span className="text-gray-400 font-semibold">{item.label}:</span>
              <span className="text-gray-600">{item.value}</span>
            </div>
          ))}
        </div>
      </div>

      <ProgressBar
        totalAmount={order?.totalAmount}
        paidAmount={order?.advancePayment}
        title="Payment Progress"
      />

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {[
          { label: "Total Amount", value: order?.totalAmount, bgcolor: "bg-blue-50",textcolor: "text-blue-600" },
          { label: "Paid Amount", value: order?.advancePayment, bgcolor: "bg-green-50",textcolor: "text-green-600" },
          { label: "Remaining", value: order?.remainingPayment, bgcolor: "bg-orange-50",textcolor: "text-orange-600" }
        ].map((item) => (
          <div key={item.label} className={`${item.bgcolor} rounded-lg p-4`}>
            <p className={`text-sm ${item.textcolor} font-semibold`}>{item.label}</p>
            <p className={`text-2xl font-bold text-${item.color}-900`}>
              ₹{item.value?.toFixed(2)}
            </p>
          </div>
        ))}
      </div>

      <h3 className="text-lg font-semibold text-gray-900 my-4">Payment History</h3>

      <div className="space-y-4">
        {payments.map((payment, index) => (
          <div
            key={payment.paymentId}
            className="flex items-center gap-4 p-4 rounded-lg border border-gray-100 hover:bg-gray-50 transition-colors"
          >
            <IoMdCheckmarkCircleOutline className="w-5 h-5 text-green-500 flex-shrink-0" />
            
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-3 mb-1">
                <h4 className="font-medium text-gray-900">Payment {index + 1}</h4>
                <span className="px-3 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
                  Paid
                </span>
              </div>
              <div className="flex flex-col sm:flex-row sm:items-center gap-2 text-sm text-gray-600">
                {[
                  { icon: SlCalender, text: `Due: ₹${payment.remainingPayment}` },
                  payment.paymentDate && { 
                    icon: IoMdCheckmarkCircleOutline, 
                    text: `Paid: ${moment(payment.paymentDate).format("DD-MM-YYYY HH:mm:ss")}` 
                  },
                  payment.paymentMethod && { 
                    icon: CiCreditCard1, 
                    text: payment.paymentMethod 
                  }
                ].filter(Boolean).map((item, idx) => (
                  <span key={idx} className="flex items-center gap-1">
                    <item.icon className="w-4 h-4" />
                    {item.text}
                  </span>
                ))}
              </div>
              {payment.transactionId && (
                <p className="text-xs text-gray-500 mt-1">
                  Transaction ID: {payment.transactionId}
                </p>
              )}
            </div>

            <div className="text-right">
              <p className="text-lg font-semibold text-gray-900">
                ₹{payment.advancePayment.toFixed(2)}
              </p>
            </div>

            <Button className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors text-sm font-medium flex-shrink-0" onClick={()=>{alert("Feature will be soon...")}}>
              Receipt
            </Button>
          </div>
        ))}
      </div>
    </PageWrapper>
  );
}