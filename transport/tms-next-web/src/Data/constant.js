import * as Yup from "yup";

export const vehicleValidationSchema = Yup.object({
  registrationNumber: Yup.string()
    .trim()
    .required("Registration Number is required"),

  vehicleTypeId: Yup.mixed().required("Vehicle Type is required").nullable(),

  model: Yup.string().trim().required("Vehicle Model is required"),

  capacity: Yup.number()
    .required("Vehicle Capacity is required")
    .min(1, "Vehicle Capacity must be greater than 0")
    .integer("Capacity must be a whole number"),

  isRented: Yup.boolean(),

  status: Yup.mixed().required("Status is required").nullable(),

  vehicleNumber: Yup.string().trim().required("Vehicle Number is required"),

  fileUrl: Yup.array().of(Yup.string()).required("File is required"),

  rentalDetailsId: Yup.mixed().nullable(),

  // Conditional rental fields - only validated when isRented is true
  providerName: Yup.string().when("isRented", {
    is: true,
    then: (schema) => schema.trim().required("Provider Name is required"),
    otherwise: (schema) => schema.nullable(),
  }),

  rentalStartDate: Yup.date().when("isRented", {
    is: true,
    then: (schema) =>
      schema
        .required("Rental Start Date is required")
        .typeError("Please select a valid start date"),
    otherwise: (schema) => schema.nullable(),
  }),

  rentalEndDate: Yup.date().when("isRented", {
    is: true,
    then: (schema) =>
      schema
        .required("Rental End Date is required")
        .typeError("Please select a valid end date")
        .min(Yup.ref("rentalStartDate"), "End Date must be after Start Date"),
    otherwise: (schema) => schema.nullable(),
  }),

  rentalCost: Yup.number().when("isRented", {
    is: true,
    then: (schema) =>
      schema
        .typeError("Rental Cost must be a number")
        .required("Rental Cost is required")
        .min(0, "Rental Cost cannot be negative"),
    otherwise: (schema) => schema.nullable(),
  }),
});

export const filterVehicleValidationSchema = Yup.object({
  model: Yup.string().nullable(),
  company: Yup.string().nullable(),
  capacity: Yup.number().nullable(),
});

export const driverValidationSchema = Yup.object({
  name: Yup.string().required("Driver Name is required"),
  licenseNumber: Yup.string().required("License Number is required"),
  licenseExpiry: Yup.string().required("Expiry Date is required"),
  documentIds: Yup.array().min(1, "At least one document is required"),
  contactNumber: Yup.string()
    .required("Contact Number is required")
    .matches(/^\+91\d{10}$/, "Contact number must be in format +91XXXXXXXXXX"),
});

export const filterDriverValidationSchema = Yup.object({
  licenseNumber: Yup.string()
    .trim()
    .max(20, "License number should be at most 20 characters"),
  contactNumber: Yup.string()
    .trim()
    .max(15, "Contact number should be at most 15 characters"),
  unassignedOnly: Yup.boolean(),
});

export const dispatchValidationSchema = Yup.object().shape({
  dispatchType: Yup.string().required("Dispatch type is required"),
  vehicle: Yup.string().required("Vehicle selection is required"),
  driver: Yup.string().required("Driver selection is required"),
});

export const filterDispatchValidationSchema = Yup.object({
  status: Yup.string().nullable(),
  dispatchType: Yup.string()
    .trim()
    .max(50, "Dispatch type cannot exceed 50 characters"),
});

export const rentalValidationSchema = Yup.object({
  providerName: Yup.string().required("Provider Name  is required"),
  rentalStartDate: Yup.string().required("Rental Start is required"),
  rentalEndDate: Yup.string().required("Rental End Date is required"),
  rentalCost: Yup.number()
    .required("Rental Cost is required")
    .min(1, "Rental Cost must be greater than 99"),
  vehicleId: Yup.string().required("Vehicle is required for rental"),
});

export const loginValidationSchema = Yup.object({
  email: Yup.string()
    .required("Username is required")
    .min(3, "Username must be at least 3 characters"),
  password: Yup.string()
    .required("Password is required")
    .min(4, "Password must be at least 6 characters"),
});

export const locationValidationSchema = Yup.object({
  locationArea: Yup.string().required("Location Name is required"),
  locationAddress: Yup.string().required("Location Address is required"),
  pincode: Yup.string()
    .required("Pincode is required")
    .matches(/^\d+$/, "Pincode must be numeric"),
});

export const branchValidationSchema = Yup.object({
  branchName: Yup.string().required("Branch Name is required"),
  branchType: Yup.string().required("Branch Type is required"),
  totalCapacity: Yup.number()
    .required("Total Capacity is required")
    .min(1, "Capacity must be greater than 0"),
  contactInfo: Yup.string().required("Contact Info is required"),
  locationId: Yup.string().required("Location of branch required"),
  locationAddress: Yup.string().required("Location Address is required"),
});

export const filterBranchValidationSchema = Yup.object({
  locationName: Yup.string().trim().max(100, "Location name is too long"),
  locationAddress: Yup.string().trim().max(200, "Location address is too long"),
});

export const productCategoryValidationSchema = Yup.object({
  categoryName: Yup.string().required("Category Name is required"),
});

export const productValidationSchema = Yup.object({
  productName: Yup.string().required("Product Name is required"),
  productCode: Yup.string().required("Product Code is required"),
  weight: Yup.number().required("Weight is required"),
  price: Yup.number().required("Price is required"),
  categoryId: Yup.string().required("Category of product required"),
  quantity: Yup.string().required("Quantity of product required"),
});

export const customerValidationSchema = Yup.object({
  customerName: Yup.string().required("Name is required"),
  customerNumber: Yup.string().required("Phone Number is required"),
  customerEmail: Yup.string().required("Email is required"),
  customerInfo: Yup.string().required("Info is required"),
  billingAddress: Yup.string().required("Billing Address is required"),
  shippingAddress: Yup.string().required("Shipping Address is required"),
});

export const orderValidationSchema = Yup.object({
  customerId: Yup.string().required("Customer is required"),
  receiverId: Yup.string().required("Receiver is required"),
  originlocationId: Yup.string().required("Origin Location is required"),
  destinationlocationId: Yup.string().required(
    "Destination location is required"
  ),
  dispatchDate: Yup.string().required("Dispatch Date required"),
  deliveryDate: Yup.string().required("Delivery Date required"),
  // status: Yup.string().required("status required"),
  totalAmount: Yup.number().required("Total Amount is required"),
});

export const manifestValidationSchema = Yup.object({
  dispatchId: Yup.string().required("Dispatch is required"),
  startLocationId: Yup.string().required("Start Location is required"),
  endLocationId: Yup.string().nullable(),
  deliveryDate: Yup.string().nullable(),
  orderIds: Yup.array()
    .of(Yup.string())
    .min(1, "At least one order is required")
    .required("Orders required"),
});

export const bookingTrackingValidationSchema = Yup.object({
  dispatchId: Yup.string().required("Dispatch is required"),
  activeLocation: Yup.string().required("Acyive Location is required"),
  status: Yup.string().required("Status is required"),
});

export const userValidationSchema = Yup.object({
  firstName: Yup.string()
    .min(2, "First name must be at least 2 characters")
    .max(50, "First name must be less than 50 characters")
    .matches(/^[a-zA-Z\s]+$/, "First name can only contain letters and spaces")
    .required("First name is required"),

  secondName: Yup.string()
    .min(2, "Last name must be at least 2 characters")
    .max(50, "Last name must be less than 50 characters")
    .matches(/^[a-zA-Z\s]+$/, "Last name can only contain letters and spaces")
    .required("Last name is required"),

  email: Yup.string()
    .email("Please enter a valid email address")
    .max(100, "Email must be less than 100 characters")
    .required("Email is required"),

  branchIds: Yup.string().required("Branch selection is required"),

  password: Yup.string()
    .required("Password is required")
    .min(8, "Password must be at least 8 characters")
    .matches(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
      "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
    ),

  role: Yup.string().required("Role selection is required"),
});
export const updateUuserValidationSchema = Yup.object({
  firstName: Yup.string()
    .min(2, "First name must be at least 2 characters")
    .max(50, "First name must be less than 50 characters")
    .matches(/^[a-zA-Z\s]+$/, "First name can only contain letters and spaces")
    .required("First name is required"),

  secondName: Yup.string()
    .min(2, "Last name must be at least 2 characters")
    .max(50, "Last name must be less than 50 characters")
    .matches(/^[a-zA-Z\s]+$/, "Last name can only contain letters and spaces")
    .required("Last name is required"),

  email: Yup.string()
    .email("Please enter a valid email address")
    .max(100, "Email must be less than 100 characters")
    .required("Email is required"),

  branchIds: Yup.string().required("Branch selection is required"),

  role: Yup.string().required("Role selection is required"),
});

export const forgotPasswordValidationSchema = Yup.object({
  email: Yup.string()
    .email("Invalid email address")
    .required("Email is required"),
});

export const otpValidationSchema = Yup.object({
  otp: Yup.string().required("OTP is required").min(6, "OTP must be 6 digits"),
});

export const resetPasswordValidationSchema = Yup.object({
  newPassword: Yup.string()
    .min(6, "Password must be at least 6 characters")
    .required("New password is required"),
  confirmPassword: Yup.string()
    .oneOf([Yup.ref("newPassword"), null], "Passwords must match")
    .required("Please confirm your password"),
});

export const paymentValidationSchema = Yup.object({
  totalAmount: Yup.number()
    .required("Total amount is required")
    .min(0.01, "Amount must be greater than 0"),
  advancePayment: Yup.number()
    .required("Advance payment is required")
    .min(0, "Advance payment cannot be negative")
    .test(
      "max-advance",
      "Advance payment cannot exceed total amount",
      function (value) {
        const { totalAmount } = this.parent;
        return !value || !totalAmount || value <= totalAmount;
      }
    ),
  paymentMode: Yup.string().required("Payment mode is required"),
});
