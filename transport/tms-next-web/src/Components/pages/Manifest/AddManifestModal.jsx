"use client";
import { DatePicker } from "@/Components/atom/DatePicker";
import { Select } from "@/Components/atom/Select";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { manifestValidationSchema } from "@/Data/constant";
import { getAllOrder } from "@/service/order";
import { getAllDispatch } from "@/service/dispatch";
import { getAllLocation } from "@/service/location";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState, useMemo } from "react";
import CustomsFileUpload from "@/Components/atom/CustomsFileUpload";
import {
  AddManifest,
  UpdateManifest,
  getManifestById,
} from "@/service/manifest";
import { toast } from "react-toastify";

export default function AddManifestModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {
  const [locationOptions, setLocationOptions] = useState([]);
  const [dispatchOption, setDispatchOption] = useState([]);
  const [selectedOriginLocation, setSelectedOriginLocation] = useState(null);
  const [selectedDestinationLocation, setSelectedDestinationLocation] = useState(null);
  const [selectedConsignments, setSelectedConsignments] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  
  const filterPayload = {
    limit: 0,
    filters: [
      {
        attribute: "status",
        operation: "EQUALS",
        value: "Scheduled",
      },
    ],
  };
  
  const orderFilter = {
    limit: 0,
    filters: [
      {
        attribute: "status",
        operation: "EQUALS",
        value: "CONFIRM",
      },
    ],
  };

  const { data: orderData, isLoading } = useQuery({
    queryKey: ["orderList", orderFilter],
    queryFn: () => getAllOrder(orderFilter),
  });
  
  const { data: manifestData } = useQuery({
    queryKey: ["manifestDetail", selectedId],
    queryFn: () => (selectedId ? getManifestById(selectedId) : null),
    enabled: !!selectedId,
  });

  const { data: DispatchData, isDispatchLoading } = useQuery({
    queryKey: ["dispatchList", filterPayload],
    queryFn: () => getAllDispatch(filterPayload),
  });

  const { data: locationData, isLoading: locationLoading } = useQuery({
    queryKey: ["locationData"],
    queryFn: () => getAllLocation(),
  });

  const formik = useFormik({
    initialValues: {
      dispatchId: "",
      startLocationId: "",
      endLocationId: "",
      deliveryDate: null,
      orderIds: [],
    },
    validationSchema: manifestValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        const submitValues = {
          ...values,
          orderIds: Array.isArray(values.orderIds)
            ? values.orderIds
            : [values.orderIds].filter(Boolean),
        };

        if (selectedId === null) {
          await AddManifest(submitValues);
          toast.success("Manifest added successfully");
        } else {
          await UpdateManifest(selectedId, submitValues);
          toast.success("Manifest updated successfully");
        }

        await ListDataRefetch();
        resetForm();
        setSelectedConsignments([]);
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save manifest");
      }
    },
  });

  const availableConsignments = useMemo(() => {
    if (!orderData || !formik.values.startLocationId) {
      return [];
    }
    
    return orderData
      .filter((item) => 
        item.originlocationId === formik.values.startLocationId &&
        !formik.values.orderIds.includes(item.orderId)
      )
      .filter((item) => {
        if (!searchTerm) return true;
        const search = searchTerm.toLowerCase();
        return (
          item.orderId?.toLowerCase().includes(search) ||
          item.customerName?.toLowerCase().includes(search) ||
          item.receiverName?.toLowerCase().includes(search)
        );
      });
  }, [orderData, formik.values.startLocationId, formik.values.orderIds, searchTerm]);

  const originLocationIdsWithOrders = useMemo(() => {
    if (!orderData) {
      return new Set();
    }
    return new Set(orderData.map((order) => order.originlocationId));
  }, [orderData]);

  // Update selected consignments when orderIds change
  useEffect(() => {
    if (!orderData || !formik.values.orderIds.length) {
      setSelectedConsignments([]);
      return;
    }

    const selected = orderData.filter((order) => 
      formik.values.orderIds.includes(order.orderId)
    );
    setSelectedConsignments(selected);
  }, [formik.values.orderIds, orderData]);

  useEffect(() => {
    if (DispatchData) {
      const options = DispatchData.map((item) => ({
        label: `${item.vehiclNumber} (${item.model})`,
        value: item.dispatchId,
        data: item,
      }));
      setDispatchOption(options);
    }
  }, [DispatchData]);

  useEffect(() => {
    if (locationData) {
      const options = locationData.map((item) => ({
        label: `${item.locationName}`,
        value: item.locationId,
        data: item,
      }));
      setLocationOptions(options);
    }
  }, [locationData]);

  useEffect(() => {
    if (formik.values.startLocationId) {
      formik.setFieldValue("orderIds", []);
    }
  }, [formik.values.startLocationId]);

  useEffect(() => {
    if (manifestData) {
      formik.setValues({
        dispatchId: manifestData.dispatchId || "",
        startLocationId: manifestData.startLocationId || "",
        endLocationId: manifestData.endLocationId || "",
        deliveryDate: manifestData.deliveryDate || "",
        orderIds: Array.isArray(manifestData.orderIds)
          ? manifestData.orderIds
          : manifestData.orderIds
          ? [manifestData.orderIds]
          : [],
      });
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [manifestData, selectedId]);

  useEffect(() => {
    if (!formik.values.startLocationId) {
      setSelectedOriginLocation(null);
      return;
    }

    const selectedOption = locationOptions.find(
      (option) => option.value === formik.values.startLocationId
    );
    setSelectedOriginLocation(selectedOption?.data || null);
  }, [formik.values.startLocationId, locationOptions]);

  useEffect(() => {
    if (!formik.values.endLocationId) {
      setSelectedDestinationLocation(null);
      return;
    }

    const selectedOption = locationOptions.find(
      (option) => option.value === formik.values.endLocationId
    );
    setSelectedDestinationLocation(selectedOption?.data || null);
  }, [formik.values.endLocationId, locationOptions]);

  const getOriginLocationLabel = () =>
    selectedOriginLocation ? `${selectedOriginLocation.locationName}` : null;

  const getDestinationLocationLabel = () =>
    selectedDestinationLocation
      ? `${selectedDestinationLocation.locationName}`
      : null;

  const getDestinationLocationOptions = () => {
    if (!formik.values.startLocationId) {
      return locationOptions;
    }
    return locationOptions.filter(
      (option) => option.value !== formik.values.startLocationId
    );
  };

  const getOriginLocationOptions = () => {
    let filteredOptions = locationOptions.filter((option) =>
      originLocationIdsWithOrders.has(option.value)
    );

    if (formik.values.endLocationId) {
      filteredOptions = filteredOptions.filter(
        (option) => option.value !== formik.values.endLocationId
      );
    }
    return filteredOptions;
  };

  const handleAddConsignment = (consignment) => {
    if (consignment?.orderId) {
      const updatedOrderIds = [...formik.values.orderIds, consignment.orderId];
      formik.setFieldValue("orderIds", updatedOrderIds);
      formik.setFieldTouched("orderIds", true, false);
    }
  };

  const handleRemoveConsignment = (orderId) => {
    const updatedOrderIds = formik.values.orderIds.filter(id => id !== orderId);
    formik.setFieldValue("orderIds", updatedOrderIds);
  };

  const handleModalClose = () => {
    formik.resetForm();
    setSelectedConsignments([]);
    setSearchTerm("");
    handleCancel();
  };

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading manifest data...</div>;
  }

  return (
    <form className="space-y-4" onSubmit={(e) => e.preventDefault()}> 
      <Select
        id="dispatchId"
        name="dispatchId"
        label="Vehicle Number"
        placeholder="Select a vehicle"
        fieldWidth="100%"
        value={formik.values.dispatchId}
        onChange={(value) => formik.setFieldValue("dispatchId", value)}
        onBlur={() => formik.setFieldTouched("dispatchId", true)}
        error={formik.errors.dispatchId}
        touched={formik.touched.dispatchId}
        required={true}
        loading={isDispatchLoading}
        options={dispatchOption}
        noOptionMsg="No dispatch available. Please add dispatch first."
        disabled={isDispatchLoading || dispatchOption.length === 0}
        notFoundContent={
          isDispatchLoading ? "Loading dispatch..." : "No dispatch available"
        }
      />

      <Select
        id="startLocationId"
        name="startLocationId"
        label="Start Location"
        placeholder="Select start location"
        fieldWidth="100%"
        value={formik.values.startLocationId}
        onChange={(value) => {
          formik.setFieldValue("startLocationId", value);
          if (formik.values.endLocationId === value) {
            formik.setFieldValue("endLocationId", "");
          }
        }}
        onBlur={() => formik.setFieldTouched("startLocationId", true)}
        error={formik.errors.startLocationId}
        touched={formik.touched.startLocationId}
        required={true}
        loading={locationLoading}
        options={getOriginLocationOptions()}
        labelRender={getOriginLocationLabel()}
        noOptionMsg="No location available. Please add location first."
        disabled={locationLoading || locationOptions.length === 0}
        notFoundContent={
          locationLoading ? "Loading locations..." : "No location available"
        }
      />

      {/* Added Consignments Section */}
      <div className="border rounded-lg overflow-hidden">
        <div className="bg-gray-50 px-4 py-3 border-b flex justify-between items-center">
          <h3 className="font-semibold text-gray-900">
            Added Consignments ({selectedConsignments.length})
            {formik.touched.orderIds && formik.errors.orderIds && (
              <span className="text-red-600 text-sm ml-2">*</span>
            )}
          </h3>
          <span className="text-xs text-gray-500">Total Items: {selectedConsignments.length}</span>
        </div>
        
        {selectedConsignments.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-[#750014] text-white">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Customer</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Receiver</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Destination</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Amount</th>
                  <th className="px-4 py-3 text-center text-sm font-semibold">Action</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {selectedConsignments.map((consignment) => (
                  <tr key={consignment.orderId} className="hover:bg-gray-50">
                    <td className="px-4 py-3 text-sm text-gray-900">{consignment.customerName}</td>
                    <td className="px-4 py-3 text-sm text-gray-900">{consignment.receiverName}</td>
                    <td className="px-4 py-3 text-sm text-gray-600 text-xs">
                      {consignment.destinationLocationName}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-900">
                      ₹{consignment.totalAmount?.toLocaleString()}
                    </td>
                    <td className="px-4 py-3 text-center">
                      <button
                        type="button"
                        onClick={() => handleRemoveConsignment(consignment.orderId)}
                        className="text-red-600 hover:text-red-800 font-bold text-lg"
                        title="Remove consignment"
                      >
                        ✕
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="px-4 py-8 text-center text-gray-500">
            No consignments added yet. Add consignments from the catalog below.
          </div>
        )}
        
        {formik.touched.orderIds && formik.errors.orderIds && (
          <div className="px-4 py-2 bg-red-50 border-t border-red-200">
            <p className="text-sm text-red-600">{typeof formik.errors.orderIds === 'string' ? formik.errors.orderIds : "At least one order is required"}</p>
          </div>
        )}
      </div>

      {/* Consignment Catalog */}
      <div className="border rounded-lg overflow-hidden mt-6">
        <div className="bg-gray-50 px-4 py-3 border-b">
          <h3 className="font-semibold text-gray-900 mb-3">Consignment Catalog</h3>
        </div>

        {!formik.values.startLocationId ? (
          <div className="px-4 py-8 text-center text-gray-500">
            Please select start location first to view available consignments
          </div>
        ) : isLoading ? (
          <div className="px-4 py-8 text-center text-gray-500">
            Loading consignments...
          </div>
        ) : availableConsignments.length > 0 ? (
          <div className="overflow-x-auto max-h-96 overflow-y-auto">
            <table className="w-full">
              <thead className="bg-[#750014] text-white sticky top-0">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Customer</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Receiver</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Destination</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Amount</th>
                  <th className="px-4 py-3 text-center text-sm font-semibold">Action</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {availableConsignments.map((consignment) => (
                  <tr key={consignment.orderId} className="hover:bg-gray-50">
                    
                    <td className="px-4 py-3 text-sm text-gray-900">{consignment.customerName}</td>
                    <td className="px-4 py-3 text-sm text-gray-900">{consignment.receiverName}</td>
                    <td className="px-4 py-3 text-sm text-gray-600 text-xs">
                      {consignment.destinationLocationName}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-900">
                      ₹{consignment.totalAmount?.toLocaleString()}
                    </td>
                    <td className="px-4 py-3 text-center">
                      <button
                        type="button"
                        onClick={() => handleAddConsignment(consignment)}
                        className="text-green-600 hover:text-green-800 font-bold text-lg"
                        title="Add consignment"
                      >
                        +
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="px-4 py-8 text-center text-gray-500">
            {searchTerm ? 'No consignments found matching your search' : 'No consignments available for selected start location'}
          </div>
        )}
      </div>
      <div className="flex gap-4 justify-end pt-4">
        <Button onClick={handleModalClose}>Cancel</Button>
        <Button
          onClick={formik.handleSubmit}
          disabled={formik.isSubmitting}
          className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]!"
          type="button"
        >
          {formik.isSubmitting ? "Saving..." : "Save"}
        </Button>
      </div>
    </form>
  );
}