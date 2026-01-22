"use client";
import { Input } from "@/Components/atom/Input";
import { TextArea } from "../../atom/Textarea";
import { Select } from "@/Components/atom/Select";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { branchValidationSchema } from "@/Data/constant";
import { AddBranch, UpdateBranch, getBranchById } from "@/service/branch";
import { getAllLocation } from "@/service/location";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";


const branchTypeOptions = [
  { label: "Primary", value: "Primary" },
  { label: "Secondary", value: "Secondary" },
  { label: "Hub", value: "Hub" },
];

export default function AddBranchModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {
  const [locationOptions, setLocationOptions] = useState([]);
  const [selectedLocation, setSelectedLocation] = useState(null);

  // Fetch branch data when editing
  const { data: branchData, isLoading } = useQuery({
    queryKey: ["branchDetail", selectedId],
    queryFn: () => (selectedId ? getBranchById(selectedId) : null),
    enabled: !!selectedId,
  });

  // Fetch all locations
  const { data: locationData, isLoading: locationLoading } = useQuery({
    queryKey: ["locationData"],
    queryFn:()=> getAllLocation(),
  });

  // Transform location data into options for Select component
  useEffect(() => {
    if (locationData) {
      const options = locationData.map((location) => ({
        label: `${location.locationName} (${location.locationAddress})`,
        value: location.locationId,
        data: location,
      }));
      setLocationOptions(options);
    }
  }, [locationData]);

  const initialValues = {
    branchName: "",
    contactInfo: "",
    branchType: null,
    totalCapacity: "",
    locationId: null,
    locationAddress: "",
  };

  const formik = useFormik({
    initialValues,
    validationSchema: branchValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        if (selectedId === null) {
          await AddBranch(values);
          toast.success("Branch added successfully");
        } else {
          await UpdateBranch(selectedId, values);
          toast.success("Branch updated successfully");
        }

        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save branch");
      }
    },
  });

  // Update form values when editing an existing branch
  useEffect(() => {
    if (branchData) {
      formik.setValues({
        branchName: branchData.branchName || "",
        contactInfo: branchData.contactInfo || "",
        branchType: branchData.branchType || null,
        totalCapacity: branchData.totalCapacity || "",
        locationId: branchData.locationId || null,
        locationAddress: branchData.locationAddress || "",
      });
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [branchData, selectedId]);

  // Update selected location when locationId changes
  useEffect(() => {
    if (!formik.values.locationId) {
      setSelectedLocation(null);
      return;
    }
    
    const selectedOption = locationOptions.find(
      (option) => option.value === formik.values.locationId
    );
    
    setSelectedLocation(selectedOption?.data || null);
  }, [formik.values.locationId, locationOptions]);

  // Generate location display label
  const getLocationLabel = () => 
    selectedLocation ? `${selectedLocation.locationName} (${selectedLocation.locationAddress})` : null;

  const handleModalClose = () => {
    formik.resetForm();
    handleCancel();
  };

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading branch data...</div>;
  }

  return (
    <>
      <form className="space-y-4">
        <Input
          label="Branch Name"
          id="branchName"
          name="branchName"
          required
          type="text"
          placeholder="Enter branch name"
          value={formik.values.branchName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.branchName && formik.errors.branchName}
          touched={formik.touched.branchName}
        />
        <Select
          id="branchType"
          name="branchType"
          label="Branch Type"
          placeholder="Select Branch Type"
          fieldWidth="100%"
          value={formik.values.branchType}
          onChange={(value) => formik.setFieldValue("branchType", value)}
          onBlur={() => formik.setFieldTouched("branchType", true)}
          error={formik.errors.branchType}
          touched={formik.touched.branchType}
          required={true}
          options={branchTypeOptions}
        />
        <Input
          label="Total Capacity"
          id="totalCapacity"
          name="totalCapacity"
          required
          type="number"
          placeholder="Enter total capacity"
          value={formik.values.totalCapacity}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.totalCapacity && formik.errors.totalCapacity}
          touched={formik.touched.totalCapacity}
        />
        <TextArea
          id="contactInfo"
          name="contactInfo"
          label="Contact Info"
          placeholder="Enter contact info"
          className="w-full rounded-md"
          labelProps={{
            className: "block text-sm font-semibold text-gray-700 mb-1",
          }}
          value={formik.values.contactInfo}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.contactInfo && formik.errors.contactInfo}
          touched={formik.touched.contactInfo}
          required
        />
        <Input
          label="Location Address"
          id="locationAddress"
          name="locationAddress"
          required
          type="text"
          placeholder="Enter location address"
          value={formik.values.locationAddress}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.locationAddress && formik.errors.locationAddress}
          touched={formik.touched.locationAddress}
        />
        <Select
          id="locationId"
          name="locationId"
          label="Location"
          placeholder="Select a location"
          fieldWidth="100%"
          value={formik.values.locationId}
          onChange={(value) => {
            formik.setFieldValue("locationId", value);
            const selectedOption = locationOptions.find(
              (option) => option.value === value
            );
            if (selectedOption?.data) {
              formik.setFieldValue(
                "locationAddress",
                selectedOption.data.locationAddress || ""
              );
            }
          }}
          onBlur={() => formik.setFieldTouched("locationId", true)}
          error={formik.errors.locationId}
          touched={formik.touched.locationId}
          required={true}
          loading={locationLoading}
          options={locationOptions}
          labelRender={getLocationLabel()}
          noOptionMsg="No locations available. Please add locations first."
          disabled={locationLoading || locationOptions.length === 0}
          notFoundContent={
            locationLoading ? "Loading locations..." : "No locations available"
          }
        />

        <div className="flex gap-4 justify-end pt-4">
          <Button onClick={handleModalClose}>Cancel</Button>
          <Button
            onClick={formik.handleSubmit}
            disabled={!formik.isValid || formik.isSubmitting}
            className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]! "
          >
            {formik.isSubmitting ? "Saving..." : "Save"}
          </Button>
        </div>
      </form>
    </>
  );
}