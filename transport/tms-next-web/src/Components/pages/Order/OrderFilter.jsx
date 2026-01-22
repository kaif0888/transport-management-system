"use client";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { Select } from "@/Components/atom/Select";
import { DatePicker } from "@/Components/atom/DatePicker";
import { useMemo, useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { getAllCustomer } from "@/service/customer";
import { getAllLocation } from "@/service/location";
import { paymentstatusDropdown } from "@/Data/impData";
import { usePathname } from "next/navigation";

const orderStatusOptions = [
  { label: "Pending", value: "PENDING" },
  { label: "In Progress", value: "IN_PROGRESS" },
  { label: "Completed", value: "COMPLETED" },
  { label: "Cancelled", value: "CANCELLED" },
];

const FILTER_CONFIG = [
  { key: "customerId", attribute: "customerId", operation: "EQUALS" },
  { key: "originlocationId", attribute: "originlocationId", operation: "EQUALS" },
  { key: "destinationlocationId", attribute: "destinationlocationId", operation: "EQUALS" },
  { key: "dispatchDate", attribute: "dispatchDate", operation: "EQUALS" },
  { key: "deliveryDate", attribute: "deliveryDate", operation: "EQUALS" },
  { key: "status", attribute: "status", operation: "EQUALS" },
  { key: "paymentStatus", attribute: "paymentStatus", operation: "EQUALS" },
];

export default function OrderFilter({
  onApplyFilter,
  onCancel,
  initialFilters = {},
}) {
  const pathname = usePathname();
  const [customerOptions, setCustomerOptions] = useState([]);
  const [locationOptions, setLocationOptions] = useState([]);

  const isConsignmentConfirmRoute = pathname === "/consignment/confirm";

  const { data: customerData, isLoading: customerLoading } = useQuery({
    queryKey: ["customerData"],
    queryFn: () => getAllCustomer(),
  });

  const { data: locationData, isLoading: locationLoading } = useQuery({
    queryKey: ["locationData"],
    queryFn: () => getAllLocation(),
  });

  useEffect(() => {
    if (customerData) {
      const options = customerData.map((item) => ({
        label: item.customerName,
        value: item.customerId,
      }));
      setCustomerOptions(options);
    }

    if (locationData) {
      const options = locationData.map((item) => ({
        label: item.locationName,
        value: item.locationId,
      }));
      setLocationOptions(options);
    }
  }, [customerData, locationData]);

  const formik = useFormik({
    initialValues: {
      customerId: initialFilters.customerId || null,
      originlocationId: initialFilters.originlocationId || null,
      destinationlocationId: initialFilters.destinationlocationId || null,
      dispatchDate: initialFilters.dispatchDate || null,
      deliveryDate: initialFilters.deliveryDate || null,
      status: initialFilters.status || null,
      paymentStatus: initialFilters.paymentStatus || null,
    },
    onSubmit: (values) => {
      const filters = FILTER_CONFIG.filter((config) => values[config.key])
        .map((config) => ({
          attribute: config.attribute,
          operation: config.operation,
          value: values[config.key],
        }));

      const filterPayload = {
        limit: 0,
        filters: filters,
      };

      onApplyFilter(filterPayload);
    },
  });

  const handleResetFilter = () => {
    if (isConsignmentConfirmRoute) {
      formik.setValues({
        ...formik.initialValues,
        status: formik.values.status,
      });
    } else {
      formik.resetForm();
    }
  };

  const hasChanges = useMemo(() => {
    return (
      formik.values.customerId !== (initialFilters.customerId || null) ||
      formik.values.originlocationId !== (initialFilters.originlocationId || null) ||
      formik.values.destinationlocationId !== (initialFilters.destinationlocationId || null) ||
      formik.values.dispatchDate !== (initialFilters.dispatchDate || null) ||
      formik.values.deliveryDate !== (initialFilters.deliveryDate || null) ||
      formik.values.status !== (initialFilters.status || null) ||
      formik.values.paymentStatus !== (initialFilters.paymentStatus || null)
    );
  }, [formik.values, initialFilters]);

  const getDestinationLocationOptions = () => {
    if (!formik.values.originlocationId) {
      return locationOptions;
    }
    return locationOptions.filter(
      (option) => option.value !== formik.values.originlocationId
    );
  };

  const getOriginLocationOptions = () => {
    if (!formik.values.destinationlocationId) {
      return locationOptions;
    }
    return locationOptions.filter(
      (option) => option.value !== formik.values.destinationlocationId
    );
  };

  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Select
          id="customerId"
          name="customerId"
          label="Customer"
          placeholder="Select customer"
          value={formik.values.customerId}
          fieldWidth="100%"
          onChange={(value) => formik.setFieldValue("customerId", value)}
          options={customerOptions}
          loading={customerLoading}
          disabled={customerLoading}
          notFoundContent={customerLoading ? "Loading..." : "No customers found"}
        />

        {!isConsignmentConfirmRoute && (
          <Select
            id="status"
            name="status"
            fieldWidth="100%"
            label="Order Status"
            placeholder="Select status"
            value={formik.values.status}
            onChange={(value) => formik.setFieldValue("status", value)}
            options={orderStatusOptions}
          />
        )}

        <Select
          id="originlocationId"
          name="originlocationId"
          label="Origin Location"
          placeholder="Select origin location"
          fieldWidth="100%"
          value={formik.values.originlocationId}
          onChange={(value) => {
            formik.setFieldValue("originlocationId", value);
            if (formik.values.destinationlocationId === value) {
              formik.setFieldValue("destinationlocationId", null);
            }
          }}
          options={getOriginLocationOptions()}
          loading={locationLoading}
          disabled={locationLoading}
          notFoundContent={locationLoading ? "Loading..." : "No locations found"}
        />

        <Select
          id="destinationlocationId"
          name="destinationlocationId"
          fieldWidth="100%"
          label="Destination Location"
          placeholder="Select destination location"
          value={formik.values.destinationlocationId}
          onChange={(value) => {
            formik.setFieldValue("destinationlocationId", value);
            if (formik.values.originlocationId === value) {
              formik.setFieldValue("originlocationId", null);
            }
          }}
          options={getDestinationLocationOptions()}
          loading={locationLoading}
          disabled={locationLoading}
          notFoundContent={locationLoading ? "Loading..." : "No locations found"}
        />

        <DatePicker
          label="Dispatch Date"
          id="dispatchDate"
          name="dispatchDate"
          placeholder="Select dispatch date"
          value={formik.values.dispatchDate}
          onChange={formik.setFieldValue}
        />

        <DatePicker
          label="Delivery Date"
          id="deliveryDate"
          name="deliveryDate"
          placeholder="Select delivery date"
          value={formik.values.deliveryDate}
          onChange={formik.setFieldValue}
        />

        <div className="md:col-span-2">
          <Select
            id="paymentStatus"
            name="paymentStatus"
            label="Payment Status"
            placeholder="Select payment status"
            fieldWidth="50%"
            value={formik.values.paymentStatus}
            onChange={(value) => formik.setFieldValue("paymentStatus", value)}
            options={paymentstatusDropdown}
          />
        </div>
      </div>

      <div className="flex justify-end gap-2 mt-4">
        <Button onClick={handleResetFilter} disabled={!hasChanges}>
          Reset
        </Button>
        <Button onClick={onCancel}>Cancel</Button>
        <Button
          type="primary"
          className="bg-[#750014] text-white border-none hover:bg-[#750014]"
          onClick={formik.handleSubmit}
        >
          Apply Filter
        </Button>
      </div>
    </div>
  );
}