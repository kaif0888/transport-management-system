"use client";
import moment from "moment";

export const VehicleColumn = [
  {
    title: "Registration Number",
    dataIndex: "registrationNumber",
    className: "px-2 text-center",
    align: "center",
    key: "registrationNumber",
    render: (text) => text || "--",
  },
  {
    title: "Vehicle Number",
    dataIndex: "vehicleNumber",
    className: "px-2 text-center",
    align: "center",
    key: "vehiclNumber",
    render: (text) => text || "--",
  },
  {
    title: "Vehicle Type",
    dataIndex: ["vehicleType", "vehicleTypeName"],
    key: "vehicleTypeName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Model",
    className: "px-2 text-center",
    align: "center",
    dataIndex: "model",
    key: "model",
    render: (text) => text || "--",
  },
  {
    title: "Capacity",
    dataIndex: "capacity",
    className: "px-2 text-center",
    align: "center",
    key: "capacity",
    render: (text) => (text ? `${text} kg` : "--"),
  },
  {
    title: "Rented",
    dataIndex: "isRented",
    key: "isRented",
    className: "px-2 text-center",
    align: "center",
    render: (value) => (value === true ? "Yes" : value === false ? "No" : "--"),
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    align: "center",
    render: (text) =>
      <span className="px-2 text-center uppercase">{text}</span> || "--",
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
  // {
  //   title: "Rental Details ID",
  //   dataIndex: "rentalDetailsId",
  //   key: "rentalDetailsId",
  //   render: (text) => text || "--",
  // },
  // {
  //   title: "Company Name",
  //   dataIndex: "companyName",
  //   key: "companyName",
  //   render: (text) => text || "--",
  // },
];

export const DriverColumn = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "License Number",
    dataIndex: "licenseNumber",
    key: "licenseNumber",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "License Expiry Date",
    dataIndex: "licenseExpiry",
    key: "licenseExpiry",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Contact Number",
    dataIndex: "contactNumber",
    key: "contactNumber",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "--",
  },
];

export const RentalColumn = [
  {
    title: "S.No",
    dataIndex: "serialNumber",
    key: "serialNumber",
    className: "px-2 text-center",
    align: "center",
    render: (_, __, index) => index + 1,
  },
  {
    title: "Provider Name",
    dataIndex: "providerName",
    key: "providerName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Rental Start Date",
    dataIndex: "rentalStartDate",
    key: "rentalStartDate",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Rental End Date",
    dataIndex: "rentalEndDate",
    key: "rentalEndDate",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Rental Cost",
    dataIndex: "rentalCost",
    key: "rentalCost",
    className: "px-2 text-center",
    align: "center",
    render: (text) => `₹ ${text}` || "--",
  },
  {
    title: "Vehicle",
    dataIndex: "vehicleId",
    key: "VehicleId",
    className: "px-2 text-center",
    align: "center",
    render: (text, record) =>
      `${record.vehicle?.vehiclNumber} (${record.vehicle?.model})` ||
      "--",
  },
];

export const DispatchColumn = [
  {
    title: "Dispatch Type",
    dataIndex: "dispatchType",
    key: "dispatchType",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Dispatch Status",
    dataIndex: "status",
    key: "status",
    align: "center",
    render: (text) =>
      <span className="px-2 text-center uppercase">{text}</span> || "--",
  },
  {
    title: "Vehcile",
    dataIndex: "model",
    key: "model",
    className: "px-2 text-center",
    align: "center",
    render: (text, record) => `${text} (${record.vehiclNumber})` || "--",
  },
  {
    title: "Driver Name",
    dataIndex: "driverName",
    key: "driverName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const ExpenseColumn = [
  {
    title: "S.No",
    dataIndex: "serialNumber",
    key: "serialNumber",
    className: "px-2 text-center",
    align: "center",
    render: (_, __, index) => index + 1,
  },
  {
    title: "Expense Type",
    dataIndex: "expenseTypeName",
    key: "expenseTypeName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },

  {
    title: "Amount",
    dataIndex: "amount",
    key: "amount",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },

  {
    title: "Description",
    dataIndex: "description",
    key: "description",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Vehicle Number",
    dataIndex: "vehiclNumber",
    key: "vehiclNumber",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Date",
    dataIndex: "date",
    key: "date",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
];

export const locationColumn = [
  {
    title: "Location Name",
    dataIndex: "locationArea",
    key: "locationArea",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Location Address",
    dataIndex: "locationAddress",
    key: "locationAddress",
    className: "px-2 text-center",
    align: "center",
    render: (text, record) => (
      <div>
        {text
          ? `${text},${record.locationArea}, ${record.district}, ${record.state}, ${record.pincode}`
          : "--"}
      </div>
    ),
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const branchColumn = [
  {
    title: "Branch Name",
    dataIndex: "branchName",
    key: "branchName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Branch Type",
    dataIndex: "branchType",
    key: "branchType",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Total Capacity",
    dataIndex: "totalCapacity",
    key: "totalCapacity",
    className: "px-2 text-center",
    align: "center",
    render: (text) => (text ? `${text} ` : "--"),
  },
  {
    title: "Contact Info",
    dataIndex: "contactInfo",
    key: "contactInfo",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Location Address",
    dataIndex: "locationAddress",
    key: "locationAddress",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Location Name",
    dataIndex: "locationName",
    key: "locationName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text ?? "--",
  },

  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const procustCategoryColumn = [
  {
    title: "Category Name",
    dataIndex: "categoryName",
    key: "categoryName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Description",
    dataIndex: "description",
    key: "description",
    align: "center",
    render: (text) => text || "-",
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const producttColumn = [
  {
    title: "Box Type",
    dataIndex: "boxName",
    key: "boxName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => <span className="capitalize!">{text}</span> || "--",
  },
  {
    title: "Box Material",
    dataIndex: "boxCode",
    key: "boxCode",
    className: "px-2 text-center",
    align: "center",
    render: (text) => <span className="capitalize!">{text}</span> || "--",
  },
  {
    title: "Width",
    dataIndex: "width",
    key: "width",
    className: "px-2 text-center",
    align: "center",
    render: (text) => (text ? `${text} cm` : "--"),
  },
  {
    title: "length",
    dataIndex: "length",
    key: "length",
    className: "px-2 text-center",
    align: "center",
    render: (text) => (text ? `${text} cm` : "--"),
  },
  {
    title: "Height",
    dataIndex: "height",
    key: "height",
    className: "px-2 text-center",
    align: "center",
    render: (text) => (text ? `${text} cm` : "--"),
  },
  {
    title: "Box Capacity",
    dataIndex: "totalValue",
    key: "totalValue",
    className: "px-2 text-center",
    align: "center",
    render: (text) => `${text} pcs`|| "--",
  },
   {
    title: "Storage Condition",
    dataIndex: "storageCondition",
    key: "storageCondition",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Description",
    dataIndex: "description",
    key: "description",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const customerColumn = [
  {
    title: "Name",
    dataIndex: "customerName",
    key: "customerName",
    width: 150,
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
    responsive: ["xs", "sm", "md", "lg", "xl", "xxl"],
  },
  {
    title: "Phone No.",
    dataIndex: "customerNumber",
    key: "customerNumber",
    width: 120,
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
    responsive: ["sm", "md", "lg", "xl", "xxl"],
  },
  {
    title: "Email",
    dataIndex: "customerEmail",
    key: "customerEmail",
    width: 200,
    className: "px-2 text-center",
    align: "center",
    render: (text) => {
      if (!text || text === "--") return "--";

      // Split email at @ symbol for 2-line display
      const emailParts = text.split("@");
      if (emailParts.length === 2) {
        return (
          <div
            style={{
              lineHeight: "1.2",
              fontSize: "12px",
              wordBreak: "break-all",
            }}
          >
            <div>{emailParts[0]}</div>
            <div>@{emailParts[1]}</div>
          </div>
        );
      }
      return text;
    },
    responsive: ["md", "lg", "xl", "xxl"],
  },
  {
    title: "Info",
    dataIndex: "customerInfo",
    key: "customerInfo",
    width: 150,
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
    responsive: ["lg", "xl", "xxl"],
  },
  {
    title: "Billing Address",
    dataIndex: "localBillingAddress",
    key: "localBillingAddress",
    width: 220,
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
    render: (text, record) => {
      const local = text || "";
      const locName = record.billingAddress?.locationName || "";
      return [local, locName].filter(Boolean).join(" ") || "--";
    },
    responsive: ["xl", "xxl"],
  },
  {
    title: "Shipping Address",
    dataIndex: "localShippingAddress",
    key: "localShippingAddress",
    width: 220,
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
    render: (text, record) => {
      const local = text || "";
      const locName = record.shippingAddress?.locationName || "";
      return [local, locName].filter(Boolean).join(" ") || "--";
    },
    responsive: ["xl", "xxl"],
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const expenseTypeColumn = [
  {
    title: "S.No",
    dataIndex: "serialNumber",
    key: "serialNumber",
    className: "px-2 text-center",
    align: "center",
    render: (_, __, index) => index + 1,
  },
  {
    title: "Expense Type",
    dataIndex: "expenseTypeName",
    key: "expenseTypeName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
];

export const vehicleTypeColumn = [
  {
    title: "Vehicle Type",
    dataIndex: "vehicleTypeName",
    key: "vehicleTypeName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Description",
    dataIndex: "description",
    key: "description",
    align: "center",
    render: (text) => text || "-",
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const orderColumn = [
  {
    title: "Consignment",
    dataIndex: "orderId",
    key: "orderId",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },

  {
    title: "Customer",
    dataIndex: "customerName",
    key: "customerName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Recevier",
    dataIndex: "receiverName",
    key: "receiverName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Created At",
    dataIndex: "createdDate",
    key: "createdDate",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY HH:MM:SS") || "--",
  },
  {
    title: "Origin",
    dataIndex: "originLocationName",
    key: "originLocationName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Destination",
    dataIndex: "destinationLocationName",
    key: "destinationLocationName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Dispatch",
    dataIndex: "dispatchDate",
    key: "dispatchDate",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Delivery",
    dataIndex: "deliveryDate",
    key: "deliveryDate",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    align: "center",
    render: (text) =>
      <span className="px-2 text-center uppercase">{text}</span> || "--",
  },
  {
    title: "Payable Amount",
    dataIndex: "totalAmount",
    key: "totalAmount",
    className: "px-2 text-center",
    align: "center",
    render: (price) => (
      <span
        style={{
          whiteSpace: "nowrap",
          overflow: "hidden",
          textOverflow: "ellipsis",
          display: "inline-block",
          maxWidth: 120,
        }}
      >
        ₹ {Number(price || 0).toFixed(2)}
      </span>
    ),
  },
  {
    title: "Payment Status",
    dataIndex: "paymentStatus",
    key: "paymentStatus",
    align: "center",
    render: (text) =>
      <span className="px-2 text-center uppercase">{text}</span> || "--",
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const orderProductColumn = [
  {
    title: "Consignment ID",
    dataIndex: "orderId",
    key: "orderId",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Box Type",
    dataIndex: "boxName",
    key: "boxName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => <span className="capitalize!">{text}</span> || "--",
  },
  {
    title: "Quantity",
    dataIndex: "quantity",
    key: "quantity",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Price Per Unit",
    dataIndex: "pricePerUnit",
    key: "pricePerUnit",
    className: "px-2 text-center",
    align: "center",
    render: (price) => `₹ ${Number(price || 0).toFixed(2)}`,
  },
  {
    title: "Total Weight",
    dataIndex: "totalWeight",
    key: "totalWeight",
    className: "px-2 text-center",
    align: "center",
    render: (text) => (text ? `${text} kg` : "--"),
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const viewOrderProductcolumn = [
  {
    title: "Consignment Product ID",
    dataIndex: "orderProductId",
    key: "orderProductId",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Box Type",
    dataIndex: "boxName",
    key: "boxName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Quantity",
    dataIndex: "quantity",
    key: "quantity",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Price Per Unit",
    dataIndex: "pricePerUnit",
    key: "pricePerUnit",
    className: "px-2 text-center",
    align: "center",
    render: (price) => `₹ ${Number(price || 0).toFixed(2)}`,
  },
  {
    title: "Total Weight",
    dataIndex: "totalWeight",
    key: "totalWeight",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
];

export const manifestcolumn = [
  {
    title: "Vehicle Number",
    dataIndex: [ "vehiclNumber"],
    key: "vehiclNumber",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },

  {
    title: "Start Location ",
    dataIndex: "startLocationName",
    key: "startLocationName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },
];

export const dispatchTrackingcolumn = [
  {
    title: "Dispatch ID",
    dataIndex: "dispatchId",
    key: "dispatchId",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },

  {
    title: "Vehicle Number",
    dataIndex: [ "vehiclNumber"],
    key: "vehiclNumber",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },

  {
    title: "Active Location",
    dataIndex: "activeLocationName",
    key: "activeLocationName",
    className: "px-2 text-center capa",
    align: "center",
    render: (text) => text || "--",
  },

   {
    title: "Receiver Number",
    dataIndex: "customerNumber",
    key: "customerNumber",
    className: "px-2 text-center capa",
    align: "center",
    render: (text) => text || "--",
  },


  {
    title: "Receiver Name",
    dataIndex: "receiverName",
    key: "receiverName",
    className: "px-2 text-center capa",
    align: "center",
    render: (text) => text || "--",
  },


 
  

  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    align: "center",
    render: (text) =>
      <span className="px-2 text-center uppercase">{text}</span> || "--",
  },
  {
    title: "Modified Date",
    dataIndex: "lastModifiedDate",
    key: "lastModifiedDate",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },

  {
    title: "Modified By",
    dataIndex: "lastModifiedBy",
    key: "lastModifiedBy",
    align: "center",
    render: (text) => text || "-",
  },

  {
    title: "Time Stamp",
    dataIndex: "timeStamp",
    key: "timeStamp",
    className: "px-2 text-center",
    align: "center",
    render: (text) =>
      text ? moment(text).format("DD-MM-YYYY HH:mm:ss") : "--",
  },
];

export const userColumn = [
  {
    title: "Name",
    dataIndex: "firstName",
    key: "name",
    className: "px-2 text-center",
    align: "center",
    render: (text, record) => {
      const firstName = text || "";
      const lastName = record.secondName || "";
      const fullName = `${firstName} ${lastName}`.trim();
      return fullName || "--";
    },
  },
  {
    title: "Branch Name",
    dataIndex: "branchName",
    key: "branchName",
    align: "center",
    render: (text) => text || "--",
  },

  {
    title: "Email",
    dataIndex: "email",
    key: "email",
    align: "center",
    render: (text) => text || "--",
  },
];

export const orderConfirmColumn = [
  {
    title: "Consignment",
    dataIndex: "orderId",
    key: "orderId",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },

  {
    title: "Customer",
    dataIndex: "customerName",
    key: "customerName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },

  {
    title: "Recevier",
    dataIndex: "receiverName",
    key: "receiverName",
    className: "px-2 text-center",
    align: "center",
    render: (text) => text || "--",
  },
  {
    title: "Created At",
    dataIndex: "createdDate",
    key: "createdDate",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY ") || "--",
  },

  {
    title: "Dispatch",
    dataIndex: "dispatchDate",
    key: "dispatchDate",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Delivery",
    dataIndex: "deliveryDate",
    key: "deliveryDate",
    className: "px-2 text-center",
    align: "center",
    render: (text) => moment(text).format("DD-MM-YYYY") || "--",
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    align: "center",
    render: (text) =>
      <span className="px-2 text-center uppercase">{text}</span> || "--",
  },
  {
    title: "Payable Amount",
    dataIndex: "totalAmount",
    key: "totalAmount",
    className: "px-2 text-center",
    align: "center",
    render: (price) => `₹ ${Number(price || 0).toFixed(2)}`,
  },
  {
    title: "Amount Paid",
    dataIndex: "advancePayment",
    key: "advancePayment",
    className: "px-2 text-center",
    align: "center",
    render: (price) => `₹ ${Number(price || 0).toFixed(2)}`,
  },
  {
    title: "Amunt Due",
    dataIndex: "remainingPayment",
    key: "remainingPayment",
    className: "px-2 text-center",
    align: "center",
    render: (price) => `₹ ${Number(price || 0).toFixed(2)}`,
  },
  {
    title: "Payment Status",
    dataIndex: "paymentStatus",
    key: "paymentStatus",
    align: "center",
    render: (text) =>
      <span className="px-2 text-center uppercase">{text}</span> || "--",
  },
];

export const boxColumn = [
  {
    title: "Box Code",
    dataIndex: "boxCode",
    key: "boxCode",
    sorter: true,
  },
  {
    title: "Box Name",
    dataIndex: "boxName",
    key: "boxName",
  },
  {
    title: "HSN Code",
    dataIndex: "hsnCode",
    key: "hsnCode",
  },
  {
    title: "Total Value",
    dataIndex: "totalValue",
    key: "totalValue",
    render: (val) => `₹${val?.toFixed(2) || "0.00"}`,
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
  },
];
