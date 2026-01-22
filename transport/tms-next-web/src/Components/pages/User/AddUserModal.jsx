"use client";
import { Select } from "@/Components/atom/Select";
import { Input } from "@/Components/atom/Input";
import { useFormik } from "formik";
import { Button } from "@/Components/atom/Button";
import {
  userValidationSchema,
  updateUuserValidationSchema,
} from "@/Data/constant";
import { getAllBranch } from "@/service/branch";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { UpdateUser, getUserById } from "@/service/user";
import { toast } from "react-toastify";
import { signUp } from "@/service/auth";

const roleOptions = [
  //   { label: "Admin", value: 1 },
  { label: "Branch Manager", value: "BRANCH_MANAGER" },
  { label: "Branch User", value: "USER" },
  //   { label: "Driver", value: 4 },
  //   { label: "Customer", value: 5 },
];

export default function AddUserModal({
  handleCancel,
  ListDataRefetch,
  selectedId,
}) {
  const [branchOptions, setBranchOptions] = useState([]);

  const { data: userData, isUserLoading } = useQuery({
    queryKey: ["userDetail", selectedId],
    queryFn: () => (selectedId ? getUserById(selectedId) : null),
    enabled: !!selectedId,
  });

  const { data: branchData, isLoading: branchLoading } = useQuery({
    queryKey: ["branchData"],
    queryFn: () => getAllBranch(),
  });

  useEffect(() => {
    if (branchData) {
      const options = branchData.map((item) => ({
        label: `${item.branchName}`,
        value: item.branchId,
        data: item,
      }));
      setBranchOptions(options);
    }
  }, [branchData]);

  const initialValues = {
    firstName: "",
    secondName: "",
    email: "",
    branchIds: "",
    role: "",
    password: "",
  };

  const formik = useFormik({
    initialValues,
    validationSchema:
      selectedId === null ? userValidationSchema : updateUuserValidationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    enableReinitialize: true,
    onSubmit: async (values, { resetForm }) => {
      try {
        if (selectedId === null) {
          await signUp(values);
          toast.success("User added successfully");
        } else {
          await UpdateUser(selectedId, values);
          toast.success("User updated successfully");
        }

        await ListDataRefetch();
        resetForm();
        handleCancel();
      } catch (error) {
        console.error("Error submitting form:", error);
        toast.error(error.message || "Failed to save user");
      }
    },
  });

  useEffect(() => {
    if (userData) {
      formik.setValues({
        firstName: userData.firstName || "",
        secondName: userData.secondName || "",
        email: userData.email || "",
        branchIds: userData.branchIds || "",
        role: userData.role || "",
      });
    } else if (!selectedId) {
      formik.resetForm();
    }
  }, [userData, selectedId]);

  const handleModalClose = () => {
    formik.resetForm();
    handleCancel();
  };

  if (isUserLoading && selectedId) {
    return <div className="py-10 text-center">Loading user data...</div>;
  }

  return (
    <>
      <form className="space-y-4">
        <Input
          id="firstName"
          name="firstName"
          label="First Name"
          placeholder="Enter first name"
          fieldWidth="100%"
          value={formik.values.firstName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.errors.firstName}
          touched={formik.touched.firstName}
          required={true}
        />

        <Input
          id="secondName"
          name="secondName"
          label="Last Name"
          placeholder="Enter last name"
          fieldWidth="100%"
          value={formik.values.secondName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.errors.secondName}
          touched={formik.touched.secondName}
          required={true}
        />

        <Input
          id="email"
          name="email"
          label="Email"
          placeholder="Enter email address"
          fieldWidth="100%"
          value={formik.values.email}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.errors.email}
          touched={formik.touched.email}
          required={true}
          type="email"
        />
        {selectedId === null && (
          <Input
            id="password"
            name="password"
            label="Password"
            placeholder="Enter password"
            fieldWidth="100%"
            value={formik.values.password}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.errors.password}
            touched={formik.touched.password}
            required={true}
            type="password"
          />
        )}
        <Select
          id="branchIds"
          name="branchIds"
          label="Branch"
          placeholder="Select a branch"
          fieldWidth="100%"
          value={formik.values.branchIds}
          onChange={(value) => {
            formik.setFieldValue("branchIds", value);
          }}
          onBlur={() => formik.setFieldTouched("branchIds", true)}
          error={formik.errors.branchIds}
          touched={formik.touched.branchIds}
          required={true}
          loading={branchLoading}
          options={branchOptions}
          noOptionMsg="No branch available. Please add branch first."
          disabled={branchLoading || branchOptions.length === 0}
          notFoundContent={
            branchLoading ? "Loading branches..." : "No branch available"
          }
        />

        <Select
          id="role"
          name="role"
          label="Role"
          placeholder="Select a role"
          fieldWidth="100%"
          value={formik.values.role}
          onChange={(value) => {
            formik.setFieldValue("role", value);
          }}
          onBlur={() => formik.setFieldTouched("role", true)}
          error={formik.errors.role}
          touched={formik.touched.role}
          required={true}
          options={roleOptions}
          noOptionMsg="No role available."
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
