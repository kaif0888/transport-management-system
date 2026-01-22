"use client";

import { Input } from "@/Components/atom/Input";
import { Select } from "@/Components/atom/Select";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import {
  AddLocation,
  UpdateLocation,
  getLocationById,
  getLocationByPincode,
} from "@/service/location";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

export default function AddLocationModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {
  const [pincodeLocationOptions, setPincodeLocationOptions] = useState([]);
  const [selectedPincodeLocation, setSelectedPincodeLocation] = useState(null);

  const { data: locationData, isLoading } = useQuery({
    queryKey: ["locationDetail", selectedId],
    queryFn: () => (selectedId ? getLocationById(selectedId) : null),
    enabled: !!selectedId,
  });

  const initialValues = {
    locationArea: "",
    pincode: "",
    state: "",
    circle: "",
    district: "",
    block: "",
    country: "",
  };

  const formik = useFormik({
    initialValues,
    validate: (values) => {
      const errors = {};
      if (!values.locationArea) errors.locationArea = "Location Name is required";
      if (!values.pincode) {
        errors.pincode = "Pincode is required";
      } else if (!/^\d{6}$/.test(values.pincode)) {
        errors.pincode = "Pincode must be 6 digits";
      }
      if (!values.state) errors.state = "State is required";
      if (!values.circle) errors.circle = "Circle is required";
      if (!values.district) errors.district = "District is required";
      if (!values.block) errors.block = "Block is required";
      if (!values.country) errors.country = "Country is required";
      return errors;
    },
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        const payload = {
          ...values,
          pincode: parseInt(values.pincode, 10),
        };
        if (!selectedId) {
          await AddLocation(payload);
          toast.success("Location added successfully");
        } else {
          await UpdateLocation(selectedId, payload);
          toast.success("Location updated successfully");
        }

        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save location");
      }
    },
  });

  // Query for pincode-based locations
  const { data: pincodeLocations, isLoading: pincodeLoading } = useQuery({
    queryKey: ["pincodeLocations", formik?.values?.pincode],
    queryFn: () => getLocationByPincode(formik.values.pincode),
    enabled:
      formik?.values?.pincode?.length === 6 &&
      /^\d{6}$/.test(formik?.values?.pincode),
  });

  // Set pincode location options in useEffect
  useEffect(() => {
    if (pincodeLocations && pincodeLocations.length > 0) {
      const options = pincodeLocations.map((item) => ({
        label: item.locationName || `${item.locationArea || "N/A"}, ${item.district || item.District || "N/A"}`,
        value: item.locationArea,
        data: item,
      }));
      setPincodeLocationOptions(options);
    } else {
      setPincodeLocationOptions([]);
      setSelectedPincodeLocation(null);
    }
  }, [pincodeLocations]);

  useEffect(() => {
    if (locationData) {
      formik.setValues({
        locationArea: locationData.locationArea || "",
        pincode: locationData.pincode || "",
        state: locationData.state || "",
        circle: locationData.circle || "",
        district: locationData.district || "",
        block: locationData.block || "",
        country: locationData.country || "",
      });
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [locationData, selectedId]);

  // Handle pincode change and reset dependent fields
  const handlePincodeChange = (e) => {
    const newPincode = e.target.value;
    formik.handleChange(e);

    // Reset dependent fields when pincode changes
    if (newPincode.length !== 6) {
      setPincodeLocationOptions([]);
      setSelectedPincodeLocation(null);
      formik.setFieldValue("locationArea", "");
      // Reset all location-related fields
      formik.setFieldValue("state", "");
      formik.setFieldValue("circle", "");
      formik.setFieldValue("district", "");
      formik.setFieldValue("block", "");
      formik.setFieldValue("country", "");
    }
  };

  // Handle location selection from dropdown
  const handleLocationSelect = (selectedLocationName) => {
    const selectedOption = pincodeLocationOptions.find(
      (option) => option.value === selectedLocationName
    );

    if (selectedOption && selectedOption.data) {
      const locationData = selectedOption.data;
      setSelectedPincodeLocation(locationData);

      // Use batch update for better performance and to avoid multiple re-renders
      formik.setValues({
        ...formik.values,
        locationArea: locationData.locationArea || "",
        state: locationData.state || locationData.State || "",
        circle: locationData.circle || locationData.Circle || "",
        district: locationData.district || locationData.District || "",
        block: locationData.block || locationData.Block || "",
        country: locationData.country || locationData.Country || "",
      });
    } else {
      setSelectedPincodeLocation(null);
      // Reset location fields if no valid selection
      formik.setValues({
        ...formik.values,
        locationArea: "",
        state: "",
        circle: "",
        district: "",
        block: "",
        country: "",
      });
    }
  };


  const handleModalClose = () => {
    formik.resetForm();
    setPincodeLocationOptions([]);
    setSelectedPincodeLocation(null);
    handleCancel();
  };

  if (isLoading && selectedId) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="flex flex-col items-center space-y-3">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-600"></div>
          <p className="text-gray-600 text-sm font-medium">
            Loading location data...
          </p>
        </div>
      </div>
    );
  }

  const shouldShowLocationDropdown =
    formik.values.pincode.length === 6 &&
    /^\d{6}$/.test(formik.values.pincode) &&
    pincodeLocationOptions.length > 0;

  const isManualEntry =
    formik.values.pincode.length === 6 &&
    /^\d{6}$/.test(formik.values.pincode) &&
    !pincodeLoading &&
    pincodeLocationOptions.length === 0;

  const locationDetailsToShow = [
    { key: "Location Name", value: selectedPincodeLocation?.locationName },
    { key: "Location Area", value: selectedPincodeLocation?.locationArea },
    { key: "Status", value: selectedPincodeLocation?.status || selectedPincodeLocation?.Status },
    { key: "Circle", value: selectedPincodeLocation?.circle || selectedPincodeLocation?.Circle },
    { key: "District", value: selectedPincodeLocation?.district || selectedPincodeLocation?.District },
    { key: "Block", value: selectedPincodeLocation?.block || selectedPincodeLocation?.Block },
    { key: "State", value: selectedPincodeLocation?.state || selectedPincodeLocation?.State },
    { key: "Country", value: selectedPincodeLocation?.country || selectedPincodeLocation?.Country },
    { key: "Pincode", value: selectedPincodeLocation?.pincode },
  ].filter((item) => item.value);

  return (
      <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
        {/* Pincode Field */}
        <div className="space-y-2">
          <Input
            label="Pincode"
            id="pincode"
            name="pincode"
            required
            type="text"
            placeholder="Enter 6-digit pincode"
            value={formik.values.pincode}
            onChange={handlePincodeChange}
            onBlur={formik.handleBlur}
            error={formik.touched.pincode && formik.errors.pincode}
            touched={formik.touched.pincode}
            maxLength={6}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors bg-gray-50 text-gray-800"
          />
          {formik.values.pincode && formik.values.pincode.length < 6 && (
            <p className="text-xs text-gray-500">
              Enter a complete 6-digit pincode to search for locations
            </p>
          )}
        </div>

        {/* Location Name Dropdown */}
        <div className="space-y-2">
          {shouldShowLocationDropdown ? (
            <Select
              id="locationArea"
              name="locationArea"
              label="Location Name"
              placeholder="Choose a location"
              fieldWidth="100%"
              value={formik.values.locationArea || null}
              onChange={handleLocationSelect}
              loading={pincodeLoading}
              options={pincodeLocationOptions}
              noOptionMsg="No locations found for this pincode."
              notFoundContent={
                pincodeLoading ? "Loading locations..." : "No locations available"
              }
              required
            />
          ) : (
            <Input
              label="Location Name"
              id="locationArea"
              name="locationArea"
              placeholder="Enter location name"
              value={formik.values.locationArea}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.locationArea && formik.errors.locationArea}
              touched={formik.touched.locationArea}
              required
              disabled={formik.values.pincode.length !== 6}
            />
          )}
          {pincodeLoading && (
            <div className="flex items-center space-x-2 text-blue-600">
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
              <span className="text-sm">Searching locations...</span>
            </div>
          )}
        </div>

        {/* Location Details Fields */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="State"
            name="state"
            value={formik.values.state}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.state && formik.errors.state}
            touched={formik.touched.state}
            required
          />
          <Input
            label="Circle"
            name="circle"
            value={formik.values.circle}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.circle && formik.errors.circle}
            touched={formik.touched.circle}
            required
          />
          <Input
            label="District"
            name="district"
            value={formik.values.district}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.district && formik.errors.district}
            touched={formik.touched.district}
            required
          />
          <Input
            label="Block"
            name="block"
            value={formik.values.block}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.block && formik.errors.block}
            touched={formik.touched.block}
            required
          />
          <Input
            label="Country"
            name="country"
            value={formik.values.country}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.country && formik.errors.country}
            touched={formik.touched.country}
            required
          />
        </div>

        {/* Location Details Card */}
        {selectedPincodeLocation && locationDetailsToShow.length > 0 && (
          <div className="bg-gray-100 rounded-lg p-5 border border-gray-200 shadow-sm">
            <div className="flex items-center space-x-2 mb-4">
              <div className="w-2 h-2 bg-[#750014] rounded-full"></div>
              <h3 className="text-sm font-semibold text-[#750014]">
                Location Details
              </h3>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {locationDetailsToShow.map((item, index) => (
                <div
                  key={index}
                  className="bg-white rounded-md p-3 shadow-sm border border-blue-100"
                >
                  <span className="text-xs font-medium text-[#750014] uppercase tracking-wide block mb-1">
                    {item.key}
                  </span>
                  <span className="text-sm text-gray-800 font-medium">
                    {item.value || "N/A"}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* No locations found message */}
        {formik.values.pincode.length === 6 &&
          /^\d{6}$/.test(formik.values.pincode) &&
          !pincodeLoading &&
          pincodeLocationOptions.length === 0 && (
            <div className="bg-amber-50 border border-amber-200 rounded-lg p-4">
              <div className="flex items-center space-x-2">
                <div className="w-2 h-2 bg-amber-500 rounded-full"></div>
                <h4 className="text-sm font-medium text-amber-800">
                  No Locations Found
                </h4>
              </div>
              <p className="text-sm text-amber-700 mt-2 ml-4">
                No locations found for pincode{" "}
                <span className="font-semibold">{formik.values.pincode}</span>.
                You can manually enter the location details below.
              </p>
            </div>
          )}

        <div className="flex items-center justify-end pt-4 gap-4">
          <Button onClick={handleModalClose} type="button">Cancel</Button>
          <Button
            onClick={formik.handleSubmit}
            disabled={formik.isSubmitting}
            className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]! "
            type="button"
          >
            {formik.isSubmitting ? (
              <div className="flex items-center space-x-2">
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                <span>Saving...</span>
              </div>
            ) : selectedId ? (
              "Update Location"
            ) : (
              "Save Location"
            )}
          </Button>
        </div>
      </form>
  );
}