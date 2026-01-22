"use client";
import { useFormik } from "formik";
import { Input } from "@/Components/atom/Input";
import { Button } from "@/Components/atom/Button";
import { rentalValidationSchema } from "@/Data/constant";
import {
  AddRental as AddRentalDetail,
  UpdateRental,
  getRentalById,
} from "@/service/rental";
import { getUnRentedVehicle } from "@/service/vehicle";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { DatePicker } from "@/Components/atom/DatePicker";
import { toast } from "react-toastify";
import { Select } from "@/Components/atom/Select";

export default function AddRental({
  handleCancel,
  vehicleListDataRefetch,
  selectedId,
}) {
  const [assignedVehicle, setAssignedVehicle] = useState(null);
  const [vehicleOptions, setVehicleOptions] = useState([]);

  // Fetch rental data when editing (selectedId exists)
  const { data: rentalData, isLoading } = useQuery({
    queryKey: ["rentalDetail", selectedId],
    queryFn: () => (selectedId ? getRentalById(selectedId) : null),
    enabled: !!selectedId,
  });

  // Fetch available vehicles
  const { data: availableVehicles, isLoading: vehicleLoading } = useQuery({
    queryKey: ["availableVehicles"],
    queryFn: getUnRentedVehicle,
  });

  // Setup vehicle options - combines unrented vehicles with currently assigned vehicle (if editing)
  useEffect(() => {
    let options = [];
    
    // Add available unrented vehicles
    if (availableVehicles) {
      options = availableVehicles.map((vehicle) => ({
        label: `${vehicle.registrationNumber} (${vehicle.model})`,
        value: vehicle.vehicleId,
        data: vehicle,
      }));
    }
    
    // Add the assigned vehicle to options if editing and not already in list
    if (rentalData?.vehicle && selectedId) {
      const currentVehicle = rentalData.vehicle;
      const vehicleExists = options.some(opt => opt.value === currentVehicle.vehicleId);
      
      if (!vehicleExists) {
        options.push({
          label: `${currentVehicle.registrationNumber} (${currentVehicle.model})`,
          value: currentVehicle.vehicleId,
          data: currentVehicle,
        });
      }
    }
    
    setVehicleOptions(options);
  }, [availableVehicles, rentalData, selectedId]);

  // Initialize form with Formik
  const formik = useFormik({
    initialValues: {
      providerName: "",
      rentalStartDate: "",
      rentalEndDate: "",
      rentalCost: 100,
      vehicleId: "",
    },
    validationSchema: rentalValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true, 
    onSubmit: async (values, { resetForm }) => {
      try {
        const payload = {
          providerName: values.providerName,
          rentalStartDate: values.rentalStartDate,
          rentalEndDate: values.rentalEndDate,
          rentalCost: values.rentalCost,
          vehicle: {
            vehicleId: values.vehicleId,
          },
        };

        // Create new or update existing rental
        if (selectedId === null) {
          await AddRentalDetail(payload);
          toast.success("Rental added successfully");
        } else {
          await UpdateRental(selectedId, payload);
          toast.success("Rental updated successfully");
        }

        await vehicleListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save rental");
      }
    },
  });

  // Update assigned vehicle reference when vehicle selection changes
  useEffect(() => {
    if (!formik.values.vehicleId) {
      setAssignedVehicle(null);
      return;
    }
    
    const selectedOption = vehicleOptions.find(
      (option) => option.value === formik.values.vehicleId
    );
    
    setAssignedVehicle(selectedOption?.data || rentalData?.vehicle || null);
  }, [formik.values.vehicleId, vehicleOptions, rentalData]);

  // Populate form when editing an existing rental
  useEffect(() => {
    if (rentalData) {
      formik.setValues({
        providerName: rentalData.providerName || "",
        rentalStartDate: rentalData.rentalStartDate || "",
        rentalEndDate: rentalData.rentalEndDate || "",
        rentalCost: rentalData.rentalCost || 100,
        vehicleId: rentalData.vehicle?.vehicleId || "",
      });
    }
  }, [rentalData]);

  // Generate vehicle display label
  const getVehicleLabel = () => 
    assignedVehicle ? `${assignedVehicle.registrationNumber} (${assignedVehicle.model})` : null;

  if (isLoading && selectedId) {
    return <div className="py-10 text-center">Loading rental data...</div>;
  }

  return (
    <form className="space-y-4">
      <Input
        label="Provider Name"
        id="providerName"
        name="providerName"
        type="text"
        placeholder="Enter vehicle provider name"
        value={formik.values.providerName}
        onChange={formik.handleChange}
        onBlur={formik.handleBlur}
        error={formik.touched.providerName && formik.errors.providerName}
        touched={formik.touched.providerName}
        required={true}
      />
      <DatePicker
        label="Rental Start Date"
        id="rentalStartDate"
        name="rentalStartDate"
        placeholder="Select rental start date"
        value={formik.values.rentalStartDate}
        onChange={formik.setFieldValue}
        onBlur={formik.handleBlur}
        error={formik.errors.rentalStartDate}
        touched={formik.touched.rentalStartDate}
        required={true}
      />
      <DatePicker
        label="Rental End Date"
        id="rentalEndDate"
        name="rentalEndDate"
        placeholder="Select rental end date"
        value={formik.values.rentalEndDate}
        onChange={formik.setFieldValue}
        onBlur={formik.handleBlur}
        error={formik.errors.rentalEndDate}
        touched={formik.touched.rentalEndDate}
        required={true}
      />
      <Input
        label="Rental Cost (₹)"
        id="rentalCost"
        name="rentalCost"
        type="number"
        placeholder="Enter rental cost"
        value={formik.values.rentalCost}
        onChange={formik.handleChange}
        onBlur={formik.handleBlur}
        error={formik.touched.rentalCost && formik.errors.rentalCost}
        touched={formik.touched.rentalCost}
        required={true}
      />
      <Select
        id="vehicleId"
        name="vehicleId"
        label="Vehicle"
        placeholder="Select a vehicle"
        fieldWidth="100%"
        value={formik.values.vehicleId}
        onChange={(value) => formik.setFieldValue("vehicleId", value)}
        onBlur={() => formik.setFieldTouched("vehicleId", true)}
        error={formik.errors.vehicleId}
        touched={formik.touched.vehicleId}
        required={true}
        loading={vehicleLoading}
        options={vehicleOptions}
        labelRender={getVehicleLabel()}
        noOptionMsg="All vehicles are booked from inventory. Add a new vehicle."
        disabled={vehicleLoading || vehicleOptions.length === 0}
        notFoundContent={
          vehicleLoading ? "Loading vehicles..." : "No vehicles available"
        }
      />

      <div className="flex gap-4 justify-end pt-4">
        <Button onClick={handleCancel}>Cancel</Button>
        <Button
          onClick={formik.handleSubmit}
          disabled={!formik.isValid || formik.isSubmitting}
          className="bg-[#750014]! disabled:opacity-50! disabled:bg-[#750014]! text-white! border-none! hover:bg-[#750014]! "
        >
          {formik.isSubmitting ? "Saving..." : "Save"}
        </Button>
      </div>
    </form>
  );
}