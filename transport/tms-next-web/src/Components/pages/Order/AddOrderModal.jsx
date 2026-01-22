"use client";
import { Input } from "@/Components/atom/Input";
import { Select } from "@/Components/atom/Select";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { orderValidationSchema } from "@/Data/constant";
import { AddOrder, UpdateOrder, getOrderById } from "@/service/order";
import { getAllCustomer } from "@/service/customer";
import { getAllLocation } from "@/service/location";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { DatePicker } from "@/Components/atom/DatePicker";
import { toast } from "react-toastify";
import { paymentstatusDropdown } from "@/Data/impData";

export default function AddOrderModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {
  const [locationOptions, setLocationOptions] = useState([]);
  const [originLocationOptions, setOriginLocationOptions] = useState([]);
  const [destinationLocationOptions, setDestinationLocationOptions] = useState(
    []
  );
  const [customerOption, setCustomerOption] = useState([]);
  const [selectedOriginLocation, setSelectedOriginLocation] = useState(null);
  const [selectedDestinationLocation, setSelectedDestinationLocation] =
    useState(null);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [selectedReceiver, setSelectedReceiver] = useState(null);

  const { data: orderData, isLoading } = useQuery({
    queryKey: ["orderDetail", selectedId],
    queryFn: () => (selectedId ? getOrderById(selectedId) : null),
    enabled: !!selectedId,
  });

  const { data: locationData, isLoading: locationLoading } = useQuery({
    queryKey: ["locationData"],
    queryFn: () => getAllLocation(),
  });

  const { data: customerData, isLoading: customerLoading } = useQuery({
    queryKey: ["customerData"],
    queryFn: () => getAllCustomer(),
  });

  useEffect(() => {
    if (customerData) {
      const options = customerData.map((item) => ({
        label: `${item.customerName}`,
        value: item.customerId,
        data: item,
      }));
      setCustomerOption(options);
    }
    if (locationData) {
      const options = locationData.map((item) => ({
        label: `${item.locationName}, ${item.district}, ${item.state}, ${item.pincode}`,
        value: item.locationId,
        data: item,
      }));
      setLocationOptions(options);
      setOriginLocationOptions(options);
      setDestinationLocationOptions(options);
    }
  }, [customerData, locationData]);

  const initialValues = {
    customerId: null,
    receiverId: null,
    originlocationId: null,
    destinationlocationId: null,
    dispatchDate: null,
    deliveryDate: null,
    totalAmount: null,
    status: selectedId === null ? "PENDING" : "",
    paymentStatus: "un-paid",
  };

  const formik = useFormik({
    initialValues,
    validationSchema: orderValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        const payload = {
          ...values,
          status: selectedId === null ? "PENDING" : values.status,
        };

        if (selectedId === null) {
          await AddOrder(payload);
          toast.success("Order added successfully");
        } else {
          await UpdateOrder(selectedId, payload);
          toast.success("Order updated successfully");
        }

        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save order");
      }
    },
  });

  useEffect(() => {
    if (orderData) {
      formik.setValues({
        customerId: orderData.customerId || null,
        receiverId: orderData.receiverId || null,
        originlocationId: orderData.originlocationId || null,
        destinationlocationId: orderData.destinationlocationId || null,
        dispatchDate: orderData.dispatchDate || null,
        deliveryDate: orderData.deliveryDate || null,
        totalAmount: orderData.totalAmount || null,
        status: orderData.status || "",
        paymentStatus: orderData.paymentStatus || null,
      });
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [orderData, selectedId]);

  useEffect(() => {
    if (!formik.values.customerId) {
      setSelectedCustomer(null);
      return;
    }
    const selectedOption = customerOption.find(
      (option) => option.value === formik.values.customerId
    );
    setSelectedCustomer(selectedOption?.data || null);
  }, [formik.values.customerId, customerOption]);

  useEffect(() => {
    if (selectedCustomer) {
      const customerLocations = [];
      if (selectedCustomer.billingAddress) {
        const billingLocation = locationOptions.find(
          (loc) => loc.value === selectedCustomer.billingAddress.locationId
        );
        if (billingLocation) customerLocations.push(billingLocation);
      }
      if (selectedCustomer.shippingAddress) {
        const shippingLocation = locationOptions.find(
          (loc) => loc.value === selectedCustomer.shippingAddress.locationId
        );
        if (
          shippingLocation &&
          (!selectedCustomer.billingAddress ||
            shippingLocation.value !==
              selectedCustomer.billingAddress.locationId)
        ) {
          customerLocations.push(shippingLocation);
        }
      }
      setOriginLocationOptions(customerLocations);
      if (
        formik.values.originlocationId &&
        !customerLocations.some(
          (loc) => loc.value === formik.values.originlocationId
        )
      ) {
        formik.setFieldValue("originlocationId", null);
      }
    } else {
      setOriginLocationOptions(locationOptions);
    }
  }, [selectedCustomer, locationOptions]);

  useEffect(() => {
    if (selectedReceiver) {
      const receiverLocations = [];
      if (selectedReceiver.billingAddress) {
        const billingLocation = locationOptions.find(
          (loc) => loc.value === selectedReceiver.billingAddress.locationId
        );
        if (billingLocation) receiverLocations.push(billingLocation);
      }
      if (selectedReceiver.shippingAddress) {
        const shippingLocation = locationOptions.find(
          (loc) => loc.value === selectedReceiver.shippingAddress.locationId
        );
        if (
          shippingLocation &&
          (!selectedReceiver.billingAddress ||
            shippingLocation.value !==
              selectedReceiver.billingAddress.locationId)
        ) {
          receiverLocations.push(shippingLocation);
        }
      }
      setDestinationLocationOptions(receiverLocations);
      if (
        formik.values.destinationlocationId &&
        !receiverLocations.some(
          (loc) => loc.value === formik.values.destinationlocationId
        )
      ) {
        formik.setFieldValue("destinationlocationId", null);
      }
    } else {
      setDestinationLocationOptions(locationOptions);
    }
  }, [selectedReceiver, locationOptions]);

  useEffect(() => {
    if (!formik.values.receiverId) {
      setSelectedReceiver(null);
      return;
    }
    const selectedOption = customerOption.find(
      (option) => option.value === formik.values.receiverId
    );
    setSelectedReceiver(selectedOption?.data || null);
  }, [formik.values.receiverId, customerOption]);

  useEffect(() => {
    if (!formik.values.originlocationId) {
      setSelectedOriginLocation(null);
      return;
    }
    const selectedOption = locationOptions.find(
      (option) => option.value === formik.values.originlocationId
    );
    setSelectedOriginLocation(selectedOption?.data || null);
  }, [formik.values.originlocationId, locationOptions]);

  useEffect(() => {
    if (!formik.values.destinationlocationId) {
      setSelectedDestinationLocation(null);
      return;
    }
    const selectedOption = locationOptions.find(
      (option) => option.value === formik.values.destinationlocationId
    );
    setSelectedDestinationLocation(selectedOption?.data || null);
  }, [formik.values.destinationlocationId, locationOptions]);

  const getCustomerLabel = () =>
    selectedCustomer ? `${selectedCustomer.customerName}` : null;

  const getReceiverLabel = () =>
    selectedReceiver ? `${selectedReceiver.customerName}` : null;

  const getOriginLocationLabel = () =>
    selectedOriginLocation ? `${selectedOriginLocation.locationName}` : null;

  const getDestinationLocationLabel = () =>
    selectedDestinationLocation
      ? `${selectedDestinationLocation.locationName}`
      : null;

  const getCustomerOptions = () => {
    if (!formik.values.receiverId) {
      return customerOption;
    }
    return customerOption.filter(
      (option) => option.value !== formik.values.receiverId
    );
  };

  const getReceiverOptions = () => {
    if (!formik.values.customerId) {
      return customerOption;
    }
    return customerOption.filter(
      (option) => option.value !== formik.values.customerId
    );
  };

  const getDestinationLocationOptions = () => {
    return destinationLocationOptions;
  };

  const getOriginLocationOptions = () => {
    return originLocationOptions;
  };

  const handleModalClose = () => {
    formik.resetForm();
    handleCancel();
  };

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading order data...</div>;
  }

  return (
    <form className="space-y-4 ">
      <div className="grid grid-cols-2 gap-2">
        <Select
          id="customerId"
          name="customerId"
          label="Customer"
          placeholder="Select a customer"
          fieldWidth="100%"
          value={formik.values.customerId}
          onChange={(value) => {
            formik.setFieldValue("customerId", value);
            if (formik.values.receiverId === value) {
              formik.setFieldValue("receiverId", null);
            }
          }}
          onBlur={() => formik.setFieldTouched("customerId", true)}
          error={formik.errors.customerId}
          touched={formik.touched.customerId}
          required={true}
          loading={customerLoading}
          options={getCustomerOptions()}
          labelRender={getCustomerLabel()}
          noOptionMsg="No customer available. Please add customer first."
          disabled={customerLoading || customerOption.length === 0}
          notFoundContent={
            customerLoading ? "Loading customers..." : "No customer available"
          }
        />

        <Select
          id="receiverId"
          name="receiverId"
          label="Receiver"
          placeholder="Select a receiver"
          fieldWidth="100%"
          value={formik.values.receiverId}
          onChange={(value) => {
            formik.setFieldValue("receiverId", value);
            if (formik.values.customerId === value) {
              formik.setFieldValue("customerId", null);
            }
          }}
          onBlur={() => formik.setFieldTouched("receiverId", true)}
          error={formik.errors.receiverId}
          touched={formik.touched.receiverId}
          required={true}
          loading={customerLoading}
          options={getReceiverOptions()}
          labelRender={getReceiverLabel()}
          noOptionMsg="No receiver available. Please add receiver first."
          disabled={customerLoading || customerOption.length === 0}
          notFoundContent={
            customerLoading ? "Loading receivers..." : "No receiver available"
          }
        />

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
          onBlur={() => formik.setFieldTouched("originlocationId", true)}
          error={formik.errors.originlocationId}
          touched={formik.touched.originlocationId}
          required={true}
          loading={locationLoading}
          options={getOriginLocationOptions()}
          labelRender={getOriginLocationLabel()}
          noOptionMsg="No location available. Please add location first."
          disabled={
            locationLoading ||
            locationOptions.length === 0 ||
            !formik.values.customerId
          }
          notFoundContent={
            locationLoading ? "Loading locations..." : "No location available"
          }
        />

        <Select
          id="destinationlocationId"
          name="destinationlocationId"
          label="Destination Location"
          placeholder="Select destination location"
          fieldWidth="100%"
          value={formik.values.destinationlocationId}
          onChange={(value) => {
            formik.setFieldValue("destinationlocationId", value);
            if (formik.values.originlocationId === value) {
              formik.setFieldValue("originlocationId", null);
            }
          }}
          onBlur={() => formik.setFieldTouched("destinationlocationId", true)}
          error={formik.errors.destinationlocationId}
          touched={formik.touched.destinationlocationId}
          required={true}
          loading={locationLoading}
          options={getDestinationLocationOptions()}
          labelRender={getDestinationLocationLabel()}
          noOptionMsg="No location available. Please add location first."
          disabled={
            locationLoading ||
            locationOptions.length === 0 ||
            !formik.values.receiverId
          }
          notFoundContent={
            locationLoading ? "Loading locations..." : "No location available"
          }
        />

        <DatePicker
          label="Dispatch Date"
          id="dispatchDate"
          name="dispatchDate"
          placeholder="Select dispatch date"
          value={formik.values.dispatchDate}
          onChange={formik.setFieldValue}
          onBlur={formik.handleBlur}
          error={formik.errors.dispatchDate}
          touched={formik.touched.dispatchDate}
          required={true}
        />

        <DatePicker
          label="Delivery Date"
          id="deliveryDate"
          name="deliveryDate"
          placeholder="Select delivery date"
          value={formik.values.deliveryDate}
          onChange={formik.setFieldValue}
          onBlur={formik.handleBlur}
          error={formik.errors.deliveryDate}
          touched={formik.touched.deliveryDate}
          required={true}
        />

        <Input
          label="Total Amount (₹)"
          id="totalAmount"
          name="totalAmount"
          type="number"
          required
          placeholder="Enter Total Amount"
          value={formik.values.totalAmount}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.totalAmount && formik.errors.totalAmount}
          touched={formik.touched.totalAmount}
        />

        {selectedId && (
          <Input
            label="Status"
            id="status"
            name="status"
            required
            type="text"
            placeholder="Enter status"
            value={formik.values.status}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.status && formik.errors.status}
            touched={formik.touched.status}
            disabled={selectedId === null}
          />
        )}

        {selectedId && (
          <Select
            id="paymentStatus"
            name="paymentStatus"
            label="Payment Status"
            placeholder="Select payment status"
            fieldWidth="100%"
            value={formik.values.paymentStatus}
            onChange={(value) => {
              formik.setFieldValue("paymentStatus", value);
            }}
            onBlur={() => formik.setFieldTouched("paymentStatus", true)}
            error={formik.errors.paymentStatus}
            touched={formik.touched.paymentStatus}
            required={true}
            options={paymentstatusDropdown}
            noOptionMsg="No payment status available."
          />
        )}
      </div>
      <div className="flex gap-4 justify-end pt-4">
        <Button onClick={handleModalClose}>Cancel</Button>
        <Button
          onClick={formik.handleSubmit}
          disabled={!formik.isValid || formik.isSubmitting}
          className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]!"
        >
          {formik.isSubmitting ? "Saving..." : "Save"}
        </Button>
      </div>
    </form>
  );
}
