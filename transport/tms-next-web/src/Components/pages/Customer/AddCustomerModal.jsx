"use client";

import { Input } from "@/Components/atom/Input";
import { Select } from "@/Components/atom/Select";
import { Button } from "@/Components/atom/Button";
import { TextArea } from "@/Components/atom/Textarea";
import { useFormik } from "formik";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState, useCallback } from "react";
import { toast } from "react-toastify";
import { customerValidationSchema } from "@/Data/constant";
import { AddCustomer, UpdateCustomer, getCustomerById } from "@/service/customer";
import { getLocationsAddressByPincode } from "@/service/location";

const INITIAL_VALUES = {
  customerName: "",
  customerNumber: "",
  customerEmail: "",
  customerInfo: "",
  billingPincode: "",
  billingState: "",
  billingDistrict: "",
  billingArea: "",
  billingAddress: "",
  shippingAddress: "",
  shippingPincode: "",
  shippingState: "",
  shippingDistrict: "",
  shippingArea: "",
};

export default function AddCustomerModal({ handleCancel, ListDataRefetch, selectedId }) {
  const [billingLocationOptions, setBillingLocationOptions] = useState([]);
  const [shippingLocationOptions, setShippingLocationOptions] = useState([]);
  const [sameAsBilling, setSameAsBilling] = useState(false);
  const [selectedBillingLocation, setSelectedBillingLocation] = useState(null);
  const [selectedShippingLocation, setSelectedShippingLocation] = useState(null);

  const { data: customerData, isLoading } = useQuery({
    queryKey: ["customerDetail", selectedId],
    queryFn: () => getCustomerById(selectedId),
    enabled: !!selectedId,
  });

  const formik = useFormik({
    initialValues: INITIAL_VALUES,
    validationSchema: customerValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        const getLocationBean = (type, selectedLocation) => {
          if (selectedLocation) {
            return {
              locationArea: selectedLocation.locationArea || "",
              pincode: String(selectedLocation.pincode || ""),
              state: selectedLocation.state || selectedLocation.State || "",
              circle: selectedLocation.circle || selectedLocation.Circle || "",
              district: selectedLocation.district || selectedLocation.District || "",
              block: selectedLocation.block || selectedLocation.Block || "",
              country: selectedLocation.country || selectedLocation.Country || "",
            };
          }
          return {
            locationArea: values[`${type}Area`] || "",
            pincode: String(values[`${type}Pincode`] || ""),
            state: values[`${type}State`] || "",
            circle: "",
            district: values[`${type}District`] || "",
            block: "",
            country: "",
          };
        };

        const payload = {
          customerName: values.customerName,
          customerNumber: values.customerNumber,
          customerEmail: values.customerEmail,
          customerInfo: values.customerInfo,
          billingAddress: getLocationBean("billing", selectedBillingLocation),
          localBillingAddress: values.billingAddress,
          shippingAddress: sameAsBilling
            ? getLocationBean("billing", selectedBillingLocation)
            : getLocationBean("shipping", selectedShippingLocation),
          localShippingAddress: sameAsBilling
            ? values.billingAddress
            : values.shippingAddress,
        };

        if (selectedId) {
          await UpdateCustomer(selectedId, payload);
          toast.success("Customer updated successfully");
        } else {
          await AddCustomer(payload);
          toast.success("Customer added successfully");
        }

        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        toast.error(error.message || "Failed to save customer");
      }
    },
  });

  const { data: billingLocations, isLoading: billingLoading } = useQuery({
    queryKey: ["billingPincodeLocations", formik.values.billingPincode],
    queryFn: () => getLocationsAddressByPincode(formik.values.billingPincode),
    enabled: /^\d{6}$/.test(formik.values.billingPincode),
  });

  const { data: shippingLocations, isLoading: shippingLoading } = useQuery({
    queryKey: ["shippingPincodeLocations", formik.values.shippingPincode],
    queryFn: () => getLocationsAddressByPincode(formik.values.shippingPincode),
    enabled: /^\d{6}$/.test(formik.values.shippingPincode) && !sameAsBilling,
  });

  const createLocationOptions = useCallback((locations) => {
    if (!locations?.length) return [];
    return locations.map((item) => ({
      label: item.locationName || `${item.locationArea || "N/A"}, ${item.district || item.District || "N/A"}`,
      value: item.locationArea,
      data: item,
    }));
  }, []);

  useEffect(() => {
    setBillingLocationOptions(createLocationOptions(billingLocations));
  }, [billingLocations, createLocationOptions]);

  useEffect(() => {
    if (!sameAsBilling) {
      setShippingLocationOptions(createLocationOptions(shippingLocations));
    } else {
      setShippingLocationOptions([]);
    }
  }, [shippingLocations, sameAsBilling, createLocationOptions]);

  const handlePincodeChange = useCallback((type) => (e) => {
    const newPincode = e.target.value;
    formik.handleChange(e);

    if (newPincode.length !== 6) {
      if (type === 'billing') {
        setBillingLocationOptions([]);
        formik.setFieldValue("billingArea", "");
        formik.setFieldValue("billingDistrict", "");
        formik.setFieldValue("billingState", "");
        setSelectedBillingLocation(null);
      } else {
        setShippingLocationOptions([]);
        formik.setFieldValue("shippingArea", "");
        formik.setFieldValue("shippingDistrict", "");
        formik.setFieldValue("shippingState", "");
        setSelectedShippingLocation(null);
      }
    }

    if (type === 'billing' && sameAsBilling) {
      formik.setFieldValue("shippingPincode", newPincode);
    }
  }, [formik, sameAsBilling]);

  const handleLocationSelect = useCallback((type) => (selectedLocationName) => {
    const options = type === 'billing' ? billingLocationOptions : shippingLocationOptions;
    const selectedOption = options.find(option => option.value === selectedLocationName);
    
    if (selectedOption?.data) {
      const { locationArea, state, State, district, District } = selectedOption.data;
      const updates = {
        [`${type}Area`]: locationArea || "",
        [`${type}State`]: state || State || "",
        [`${type}District`]: district || District || "",
      };

      if (type === 'billing' && sameAsBilling) {
        updates.shippingArea = locationArea || "";
        updates.shippingState = state || State || "";
        updates.shippingDistrict = district || District || "";
      }

      formik.setValues({ ...formik.values, ...updates });

      if (type === 'billing') {
        setSelectedBillingLocation(selectedOption.data);
        if (sameAsBilling) setSelectedShippingLocation(selectedOption.data);
      } else {
        setSelectedShippingLocation(selectedOption.data);
      }
    } else {
      const resetFields = {
        [`${type}Area`]: "",
        [`${type}State`]: "",
        [`${type}District`]: "",
      };
      formik.setValues({ ...formik.values, ...resetFields });
      if (type === 'billing') setSelectedBillingLocation(null);
      else setSelectedShippingLocation(null);
    }
  }, [billingLocationOptions, shippingLocationOptions, formik, sameAsBilling]);

  const handleSameAsBilling = useCallback((e) => {
    const isChecked = e.target.checked;
    setSameAsBilling(isChecked);

    if (isChecked) {
      setShippingLocationOptions(billingLocationOptions);
      formik.setValues({
        ...formik.values,
        shippingPincode: formik.values.billingPincode,
        shippingArea: formik.values.billingArea,
        shippingDistrict: formik.values.billingDistrict,
        shippingState: formik.values.billingState,
        shippingAddress: formik.values.billingAddress,
      });
      setSelectedShippingLocation(selectedBillingLocation);
    } else {
      formik.setValues({
        ...formik.values,
        shippingPincode: "",
        shippingArea: "",
        shippingDistrict: "",
        shippingState: "",
        shippingAddress: "",
      });
      setShippingLocationOptions([]);
      setSelectedShippingLocation(null);
    }
  }, [formik, billingLocationOptions, selectedBillingLocation]);

  useEffect(() => {
    if (customerData) {
      const billingObj = customerData.billingAddress || {};
      const shippingObj = customerData.shippingAddress || {};

      formik.setValues({
        customerName: customerData.customerName || "",
        customerNumber: customerData.customerNumber || "",
        customerEmail: customerData.customerEmail || "",
        customerInfo: customerData.customerInfo || "",
        billingAddress: customerData.localBillingAddress || "",
        billingArea: billingObj.locationArea || "",
        billingDistrict: billingObj.district || "",
        billingState: billingObj.state || "",
        billingPincode: billingObj.pincode || "",
        shippingAddress: customerData.localShippingAddress || "",
        shippingArea: shippingObj.locationArea || "",
        shippingDistrict: shippingObj.district || "",
        shippingState: shippingObj.state || "",
        shippingPincode: shippingObj.pincode || "",
      });

      setSelectedBillingLocation(billingObj);
      setSelectedShippingLocation(shippingObj);

      setSameAsBilling(
        customerData.localBillingAddress === customerData.localShippingAddress &&
          billingObj.pincode === shippingObj.pincode
      );
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [customerData, selectedId]);

  const handleModalClose = useCallback(() => {
    formik.resetForm();
    setSameAsBilling(false);
    setBillingLocationOptions([]);
    setShippingLocationOptions([]);
    setSelectedShippingLocation(null);
    handleCancel();
  }, [formik, handleCancel]);

  if (isLoading && selectedId) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="flex flex-col items-center space-y-3">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-600"></div>
          <p className="text-gray-600 text-sm font-medium">Loading customer data...</p>
        </div>
      </div>
    );
  }

  const shouldShowBillingDropdown = /^\d{6}$/.test(formik.values.billingPincode) && billingLocationOptions.length > 0;
  const shouldShowShippingDropdown = !sameAsBilling && /^\d{6}$/.test(formik.values.shippingPincode) && shippingLocationOptions.length > 0;

  const renderLocationDetails = (locationData) => {
    if (!locationData) return null;
    const details = [
      { key: "Location Name", value: locationData.locationName },
      { key: "Location Area", value: locationData.locationArea },
      { key: "Pincode", value: locationData.pincode },
      { key: "State", value: locationData.state || locationData.State },
      { key: "Circle", value: locationData.circle || locationData.Circle },
      { key: "District", value: locationData.district || locationData.District },
      { key: "Block", value: locationData.block || locationData.Block },
      { key: "Country", value: locationData.country || locationData.Country },
    ].filter((item) => item.value);

    if (details.length === 0) return null;

    return (
      <div className="bg-gray-100 rounded-lg p-4 border border-gray-200 shadow-sm mt-4">
        <div className="flex items-center space-x-2 mb-3">
          <div className="w-2 h-2 bg-[#750014] rounded-full"></div>
          <h3 className="text-sm font-semibold text-[#750014]">
            Location Details
          </h3>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          {details.map((item, index) => (
            <div key={index} className="bg-white rounded p-2 shadow-sm border border-gray-100">
              <span className="text-xs font-medium text-gray-500 uppercase tracking-wide block mb-1">
                {item.key}
              </span>
              <span className="text-sm text-gray-800 font-medium">
                {item.value}
              </span>
            </div>
          ))}
        </div>
      </div>
    );
  };

  return (
    <form className="space-y-6" onSubmit={(e) => e.preventDefault()}>
      {/* Basic Information Section */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Basic Information</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Name"
            id="customerName"
            name="customerName"
            required
            type="text"
            placeholder="Enter Customer name"
            value={formik.values.customerName}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.customerName && formik.errors.customerName}
            touched={formik.touched.customerName}
          />
          <Input
            label="Phone Number"
            id="customerNumber"
            name="customerNumber"
            required
            type="text"
            placeholder="Enter Phone Number"
            value={formik.values.customerNumber}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.customerNumber && formik.errors.customerNumber}
            touched={formik.touched.customerNumber}
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Email"
            id="customerEmail"
            name="customerEmail"
            required
            type="email"
            placeholder="Enter Email"
            value={formik.values.customerEmail}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.customerEmail && formik.errors.customerEmail}
            touched={formik.touched.customerEmail}
          />
          <TextArea
            id="customerInfo"
            name="customerInfo"
            label="Info"
            placeholder="Enter Info"
            className="w-full rounded-md"
            labelProps={{ className: "block text-sm font-semibold text-gray-700 mb-1" }}
            value={formik.values.customerInfo}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.customerInfo && formik.errors.customerInfo}
            touched={formik.touched.customerInfo}
            required
          />
        </div>
      </div>

      {/* Billing Address Section */}
      <div className="border rounded-lg p-4 bg-gray-50">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Billing Address</h3>
        <div className="space-y-4">
          <Input
            label="Pincode"
            id="billingPincode"
            name="billingPincode"
            required
            type="text"
            placeholder="Enter 6-digit pincode"
            value={formik.values.billingPincode}
            onChange={handlePincodeChange('billing')}
            onBlur={formik.handleBlur}
            error={formik.touched.billingPincode && formik.errors.billingPincode}
            touched={formik.touched.billingPincode}
            maxLength={6}
          />

          {formik.values.billingPincode && formik.values.billingPincode.length < 6 && (
            <p className="text-xs text-gray-500">Enter a complete 6-digit pincode to search for locations</p>
          )}

          <div className="space-y-2">
            <Select
              id="billingArea"
              name="billingArea"
              label="Location Name"
              placeholder="Choose a location"
              fieldWidth="100%"
              disabled={!shouldShowBillingDropdown}
              value={formik.values.billingArea || null}
              onChange={handleLocationSelect('billing')}
              loading={billingLoading}
              options={billingLocationOptions}
              noOptionMsg={formik.values.billingPincode ? `Service not available for this pincode ${formik.values.billingPincode}` : undefined}
              notFoundContent={billingLoading ? "Loading locations..." : "No locations available"}
              required
            />
            {billingLoading && (
              <div className="flex items-center space-x-2 text-blue-600">
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                <span className="text-sm">Searching billing locations...</span>
              </div>
            )}
          </div>

          <TextArea
            id="billingAddress"
            name="billingAddress"
            label="Billing Address"
            placeholder="Enter detailed billing address"
            className="w-full rounded-md min-h-[100px]"
            labelProps={{ className: "block text-sm font-semibold text-gray-700 mb-1" }}
            value={formik.values.billingAddress}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.billingAddress && formik.errors.billingAddress}
            touched={formik.touched.billingAddress}
            required
          />

          {renderLocationDetails(selectedBillingLocation)}
        </div>
      </div>

      {/* Shipping Address Section */}
      <div className="border rounded-lg p-4 bg-gray-50">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-semibold text-gray-800">Shipping Address</h3>
          <label className="flex items-center space-x-2 cursor-pointer">
            <input
              type="checkbox"
              checked={sameAsBilling}
              onChange={handleSameAsBilling}
              className="rounded"
            />
            <span className="text-sm text-gray-700">Same as billing address</span>
          </label>
        </div>

        <div className="space-y-4">
          <Input
            label="Pincode"
            id="shippingPincode"
            name="shippingPincode"
            required
            type="text"
            placeholder="Enter 6-digit pincode"
            value={formik.values.shippingPincode}
            onChange={handlePincodeChange('shipping')}
            onBlur={formik.handleBlur}
            error={formik.touched.shippingPincode && formik.errors.shippingPincode}
            touched={formik.touched.shippingPincode}
            maxLength={6}
            disabled={sameAsBilling}
          />

          {formik.values.shippingPincode && formik.values.shippingPincode.length < 6 && !sameAsBilling && (
            <p className="text-xs text-gray-500">Enter a complete 6-digit pincode to search for locations</p>
          )}

          <Select
            id="shippingLocationArea"
            name="shippingLocationArea"
            label="Location Name"
            placeholder="Choose a location"
            fieldWidth="100%"
            disabled={!shouldShowShippingDropdown}
            value={formik.values.shippingArea}
            onChange={handleLocationSelect('shipping')}
            loading={shippingLoading}
            options={shippingLocationOptions}
            noOptionMsg={!formik.values.shippingArea || sameAsBilling ? "" : `Service not available for this pincode ${formik.values.shippingPincode}`}
            notFoundContent={shippingLoading ? "Loading locations..." : "No locations available"}
            required
          />

          {shippingLoading && !sameAsBilling && (
            <div className="flex items-center space-x-2 text-blue-600">
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
              <span className="text-sm">Searching shipping locations...</span>
            </div>
          )}

          <TextArea
            id="shippingAddress"
            name="shippingAddress"
            label="Shipping Address"
            placeholder="Enter detailed shipping address"
            className="w-full rounded-md min-h-[100px]"
            labelProps={{ className: "block text-sm font-semibold text-gray-700 mb-1" }}
            value={formik.values.shippingAddress}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.shippingAddress && formik.errors.shippingAddress}
            touched={formik.touched.shippingAddress}
            required
            disabled={sameAsBilling}
          />

          {renderLocationDetails(selectedShippingLocation)}
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex gap-4 justify-end pt-4">
        <Button onClick={handleModalClose} variant="outline">
          Cancel
        </Button>
        <Button
          onClick={formik.handleSubmit}
          disabled={formik.isSubmitting}
          className="bg-[#750014] disabled:opacity-50 disabled:bg-[#750014] text-white border-none hover:bg-[#750014]"
          type="button"
        >
          {formik.isSubmitting ? "Saving..." : selectedId ? "Update Customer" : "Save Customer"}
        </Button>
      </div>
    </form>
  );
}