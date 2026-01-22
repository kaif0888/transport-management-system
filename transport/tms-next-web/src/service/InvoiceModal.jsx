import React from "react";
import { Button } from "@/Components/atom/Button";
import { DownloadOutlined, FileTextOutlined } from "@ant-design/icons";

/**
 * Invoice Modal Component
 * Displays invoice generation success message and details
 */
const InvoiceModal = ({ invoiceData, onClose, onDownload }) => {
  if (!invoiceData) return null;

  return (
    <div className="space-y-4 py-4">
      {/* Success Message */}
      <div className="bg-green-50 border border-green-200 rounded-lg p-4">
        <p className="text-green-800 font-medium mb-2 flex items-center gap-2">
          <span className="text-xl">✓</span>
          Order has been marked as delivered
        </p>
        <p className="text-green-700 text-sm">
          Customer notifications have been sent successfully.
        </p>
      </div>

      {/* Invoice Details */}
      <div className="bg-gray-50 rounded-lg p-4 space-y-2">
        <h3 className="font-semibold text-gray-800 mb-3 flex items-center gap-2">
          <FileTextOutlined className="text-blue-600" />
          Invoice Details
        </h3>
        <div className="grid grid-cols-2 gap-3 text-sm">
          <div className="flex flex-col">
            <span className="text-gray-600 text-xs mb-1">Invoice Number:</span>
            <span className="font-semibold text-gray-800 text-base">
              {invoiceData.invoiceNumber || "N/A"}
            </span>
          </div>

          <div className="flex flex-col">
            <span className="text-gray-600 text-xs mb-1">Order ID:</span>
            <span className="font-medium text-gray-800">
              {invoiceData.orderId || "N/A"}
            </span>
          </div>

          <div className="flex flex-col">
            <span className="text-gray-600 text-xs mb-1">Dispatch ID:</span>
            <span className="font-medium text-gray-800">
              {invoiceData.dispatchId || "N/A"}
            </span>
          </div>

          <div className="flex flex-col">
            <span className="text-gray-600 text-xs mb-1">Generated:</span>
            <span className="font-medium text-gray-800">
              {new Date().toLocaleString("en-IN", {
                dateStyle: "medium",
                timeStyle: "short",
              })}
            </span>
          </div>
        </div>
      </div>

      {/* Additional Info */}
      <p className="text-sm text-gray-600 text-center bg-blue-50 p-3 rounded">
        💾 The invoice has been stored in the system and can be downloaded
        anytime from the Actions column.
      </p>

      {/* Action Buttons */}
      <div className="flex gap-2 justify-end pt-2">
        <Button onClick={onClose} className="font-semibold">
          Close
        </Button>
        <Button
          type="primary"
          className="bg-blue-600 font-semibold"
          icon={<DownloadOutlined />}
          onClick={() => {
            if (onDownload) {
              onDownload(invoiceData.invoiceId, invoiceData.invoiceNumber);
            }
            onClose();
          }}
        >
          Download Invoice
        </Button>
      </div>
    </div>
  );
};

export default InvoiceModal;