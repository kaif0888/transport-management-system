"use client";
import { useRef, useState } from "react";
import { useFormik } from "formik";
import * as Yup from "yup";
import { Input } from "@/Components/atom/Input";
import { assignVechileDriver } from "@/service/driver";
import { Button } from "@/Components/atom/Button";
import { getAvalaibleVehicle } from "@/service/vehicle";
import { useQuery } from "@tanstack/react-query";
import { getAvilableDriver } from "@/service/driver";
import { toast } from "react-toastify";
import { Select } from "@/Components/atom/Select";
import { AddDispatchData } from "@/service/dispatch";
import { dispatchValidationSchema } from "@/Data/constant";

export default function AddDispatch({
  selectedId,
  handleCancel,
  dispatchListDataRefetch,
}) {
  const [assigningVehicle, setAssigningVehicle] = useState(false);

  const {
    data: availableVehicles,
    isLoading: vehiclesLoading,
    refetch: refetchVehicles,
  } = useQuery({
    queryKey: ["available"],
    queryFn: getAvalaibleVehicle,
  });

  const { data: availableDrivers, isLoading: driversLoading } = useQuery({
    queryKey: ["available-drivers"],
    queryFn: getAvilableDriver,
  });

  const dispatchTypeMap = {
    STANDARD: "Standard",
    EXPRESS: "Express",
    SAME_DAY: "Same Day",
    BULK: "Bulk",
    RETURN: "Return",
  };

  const dispatchTypeOptions = Object.entries(dispatchTypeMap).map(
    ([key, value]) => ({
      label: value,
      value: key,
    })
  );

  const vehicleOptions =
    availableVehicles?.map((vehicle) => ({
      label: `${vehicle.vehiclNumber} (${vehicle.model})`,
      value: vehicle.vehicleId,
      data: vehicle,
    })) || [];

  const driverOptions =
    availableDrivers?.map((driver) => ({
      label: `${driver.name} (${driver.licenceNumber})`,
      value: driver.driverId,
      data: driver,
    })) || [];

  const initialValues = {
    dispatchType: "",
    driver: "",
    vehicle: "",
  };

  const formik = useFormik({
    initialValues,
    validationSchema: dispatchValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        setAssigningVehicle(true);

        const formattedDispatch = {
          dispatchType: values.dispatchType,
          driverId: values.driver,
          status: "Scheduled",
          vehicleId: values.vehicle,
        };

        await AddDispatchData(formattedDispatch);

        await assignVechileDriver(values.driver, values.vehicle);

        toast.success("Dispatch created successfully");
        handleCancel();
        dispatchListDataRefetch();
        resetForm();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save dispatch");
      } finally {
        setAssigningVehicle(false);
      }
    },
  });

  const getVehicleLabel = (vehicle) =>
    vehicle ? `${vehicle.vehiclNumber} (${vehicle.model})` : null;

  const getDriverLabel = (driver) =>
    driver ? `${driver.name} (${driver.licenceNumber})` : null;

  return (
    <form onSubmit={formik.handleSubmit} className="space-y-4">
      <Select
        id="dispatchType"
        name="dispatchType"
        label="Dispatch Type"
        placeholder="Select dispatch type"
        fieldWidth="100%"
        required
        value={formik.values.dispatchType}
        onChange={(value) => formik.setFieldValue("dispatchType", value)}
        onBlur={() => formik.setFieldTouched("dispatchType", true)}
        allowClear
        options={dispatchTypeOptions}
        optionFilterProp="label"
        error={formik.touched.dispatchType && formik.errors.dispatchType}
        touched={formik.touched.dispatchType}
        noOptionMsg="No dispatch types available"
      />

      <Select
        id="vehicle"
        name="vehicle"
        label="Select Vehicle"
        placeholder="Select vehicle"
        fieldWidth="100%"
        required
        value={formik.values.vehicle}
        onChange={(value) => formik.setFieldValue("vehicle", value)}
        onBlur={() => formik.setFieldTouched("vehicle", true)}
        loading={vehiclesLoading || assigningVehicle}
        allowClear
        options={vehicleOptions}
        optionFilterProp="label"
        error={formik.touched.vehicle && formik.errors.vehicle}
        touched={formik.touched.vehicle}
        noOptionMsg="No available vehicles"
        disabled={vehiclesLoading || assigningVehicle}
        notFoundContent={
          vehiclesLoading ? "Loading vehicles..." : "No vehicles available"
        }
      />

      <Select
        id="driver"
        name="driver"
        label="Select Driver"
        placeholder="Select driver"
        fieldWidth="100%"
        value={formik.values.driver}
        onChange={(value) => formik.setFieldValue("driver", value)}
        onBlur={() => formik.setFieldTouched("driver", true)}
        loading={driversLoading}
        allowClear
        options={driverOptions}
        optionFilterProp="label"
        required
        error={formik.touched.driver && formik.errors.driver}
        touched={formik.touched.driver}
        noOptionMsg="No available drivers"
        disabled={driversLoading}
        notFoundContent={
          driversLoading ? "Loading drivers..." : "No drivers available"
        }
      />

      <div className="flex justify-end">
        <Button
          type="primary"
          htmlType="submit"
          className="font-semibold bg-[#750014]"
          disabled={assigningVehicle || vehiclesLoading || !formik.isValid}
        >
          {assigningVehicle ? "Creating..." : "Create Dispatch"}
        </Button>
      </div>
    </form>
  );
}
