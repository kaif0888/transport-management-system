"use Client";
import { Input } from "@/Components/atom/Input";
import { Select } from "@/Components/atom/Select";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import { bookingTrackingValidationSchema } from "@/Data/constant";
import { getAllDispatch } from "@/service/dispatch";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { getAllLocation } from "@/service/location";
import {
  AddDispatchTracking,
  UpdateDispatchTracking,
  getDispatchTrackingById,
} from "@/service/dispatchTracking";
import { toast } from "react-toastify";
import { trackingStatusOptions } from "@/Data/impData";

export default function AddDisptchTrackingModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {
  const [dispatchOption, setDispatchOption] = useState([]);
  const [locationOptions, setLocationOptions] = useState([]);
  const filterPayload = { limit: 0, filters: [] };

  const { data: DispatchData, isDispatchLoading } = useQuery({
    queryKey: ["dispatchList", filterPayload],
    queryFn: () => getAllDispatch(filterPayload),
  });

  const { data: locationData, isLoading: locationLoading } = useQuery({
    queryKey: ["locationData"],
    queryFn: () => getAllLocation(),
  });

  const { data: dispatchTrackingData, isLoading } = useQuery({
    queryKey: ["dispatchTrackingDetail", selectedId],
    queryFn: () => (selectedId ? getDispatchTrackingById(selectedId) : null),
    enabled: !!selectedId,
  });

  useEffect(() => {
    if (DispatchData) {
      const options = DispatchData.map((item) => ({
        label: `${item.dispatchId}`,
        value: item.dispatchId,
        data: item,
      }));
      setDispatchOption(options);
    }
    if (locationData) {
      const options = locationData.map((item) => ({
        label: `${item.locationName}`,
        value: item.locationId,
        data: item,
      }));
      setLocationOptions(options);
    }
  }, [DispatchData, locationData]);

  const initialValues = {
    dispatchId: "",
    activeLocation: "",
    status: "",
  };

  const formik = useFormik({
    initialValues,
    validationSchema: bookingTrackingValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        if (selectedId === null) {
          await AddDispatchTracking(values);
          toast.success("Dispatch Tracking added successfully");
        } else {
          await UpdateDispatchTracking(selectedId, values);
          toast.success("Dispatch Tracking updated successfully");
        }

        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save dispatch tracking");
      }
    },
  });

  useEffect(() => {
    if (dispatchTrackingData) {
      formik.setValues({
        dispatchId: dispatchTrackingData.dispatchId || "",
        activeLocation: dispatchTrackingData.activeLocation || "",
        status: dispatchTrackingData.status || "",
      });
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [dispatchTrackingData, selectedId]);

  const handleModalClose = () => {
    formik.resetForm();
    handleCancel();
  };

  if (isLoading && selectedId) {
    return (
      <div className="py-10 text-center">Loading dispatch tracking data...</div>
    );
  }

  return (
    <>
      <form className="space-y-4">
        <Select
          id="dispatchId"
          name="dispatchId"
          label="Dispatch Id"
          placeholder="Select a dispatch id"
          fieldWidth="100%"
          value={formik.values.dispatchId}
          onChange={(value) => {
            formik.setFieldValue("dispatchId", value);
          }}
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
          id="activeLocation"
          name="activeLocation"
          label="Active Location"
          placeholder="Select active location"
          fieldWidth="100%"
          value={formik.values.activeLocation}
          onChange={(value) => {
            formik.setFieldValue("activeLocation", value);
          }}
          onBlur={() => formik.setFieldTouched("activeLocation", true)}
          error={formik.errors.activeLocation}
          touched={formik.touched.activeLocation}
          required={true}
          loading={locationLoading}
          options={locationOptions}
          noOptionMsg="No location available. Please add location first."
          disabled={locationLoading || locationOptions.length === 0}
          notFoundContent={
            locationLoading ? "Loading locations..." : "No location available"
          }
        />

        <Select
          id="status"
          name="status"
          label="Status"
          placeholder="Select status"
          fieldWidth="100%"
          value={formik.values.status}
          onChange={(value) => {
            formik.setFieldValue("status", value);
          }}
          onBlur={() => formik.setFieldTouched("status", true)}
          error={formik.errors.status}
          touched={formik.touched.status}
          required={true}
          options={trackingStatusOptions}
          noOptionMsg="No payment status available."
        />

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
    </>
  );
}