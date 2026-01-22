"use client";

import { HomeOutlined, CarOutlined } from "@ant-design/icons";
import { TbSteeringWheel, TbGitBranch } from "react-icons/tb";
import { MdCarRental } from "react-icons/md";
import { CiDeliveryTruck } from "react-icons/ci";
import { AiOutlineProduct } from "react-icons/ai";
import { GiExpense } from "react-icons/gi";
import { FaUserTie,  } from "react-icons/fa";
import { BsBoxSeam } from "react-icons/bs";
import { FaShippingFast, FaUsers,FaCommentDots  } from "react-icons/fa";
import { CiLocationOn } from "react-icons/ci";

export const paymentstatusDropdown = [
  { label: "Un Paid", value: "un-paid" },
  { label: "Paid", value: "paid" },
  { label: "Partial Paid", value: "partial-paid" },
];

export const trackingStatusOptions = [
  { value: "CREATED", label: "Created" },
  { value: "DISPATCHED", label: "Dispatched" },
  { value: "IN_TRANSIT", label: "In Transit" },
  { value: "REACHED", label: "Reached" },
];

export const paymentModeOptions = [
  { label: "💳 Card", value: "CARD" },
  { label: "📱 UPI", value: "UPI" },
  { label: "💵 Cash", value: "CASH" },
];

export const vehicleStatus = [
  { label: "Available", value: "Available" },
  { label: "Under Maintenance", value: "UnderMaintenance" },
  { label: "Reserved", value: "Reserved" },
  { label: "Retired", value: "Retired" },
  { label: "In Transit", value: "InTransit" },
  { label: "Out of Service", value: "OutOfService" },
  { label: "Scrapped", value: "Scrapped" },
];

/* ===========================
   SIDEBAR MENU (FIXED)
=========================== */

export const allMenuItems = [
  {
    key: "dashboard",
    icon: <HomeOutlined className="text-lg!" />,
    label: "Home",
    role: ["admin", "user", "branch_manager", "customer", "driver"],
    path: "/dashboard",
  },

  {
    key: "vehicle",
    icon: <CarOutlined className="text-lg!" />,
    label: "Vehicle",
    role: ["admin", "user", "branch_manager"],
    children: [
      {
        key: "vehicle-list",
        label: "Vehicle List",
        role: ["admin", "user", "branch_manager"],
        path: "/vehicle",
      },
      {
        key: "vehicle-type",
        label: "Vehicle Type List",
        role: ["admin", "user", "branch_manager"],
        path: "/vehicle/type",
      },
    ],
  },

  {
    key: "customer",
    icon: <FaUserTie className="text-lg!" />,
    label: "Customer",
    role: ["admin", "user", "branch_manager"],
    path: "/customer",
  },

  {
    key: "driver",
    icon: <TbSteeringWheel className="text-lg!" />,
    label: "Driver",
    role: ["admin", "user", "branch_manager"],
    path: "/driver",
  },

  {
    key: "consignment",
    icon: <BsBoxSeam className="text-lg!" />,
    label: "Consignment",
    role: ["admin", "user", "branch_manager"],
    children: [
      {
        key: "consignment-list",
        label: "Consignment",
        role: ["admin", "user", "branch_manager"],
        path: "/consignment",
      },
      {
        key: "consignment-product",
        label: "Consignment Box List",
        role: ["admin", "user", "branch_manager"],
        path: "/consignment/consignment-product",
      },
      {
        key: "consignment-confirm",
        label: "Consignment Confirm",
        role: ["admin", "user", "branch_manager"],
        path: "/consignment/confirm",
      },
    ],
  },


  {
    key: "box",
    icon: <BsBoxSeam className="text-lg!" />,
    label: "Box",
    role: ["admin", "user", "branch_manager"],
    path: "/product", 
  },
  {
    key: "rental",
    icon: <MdCarRental className="text-lg!" />,
    label: "Rental",
    role: ["admin", "user", "branch_manager"],
    path: "/rental",
  },

  {
    
    key: "dispatch",
    icon: <CiDeliveryTruck className="text-lg!" />,
    label: "Dispatch",
    role: ["admin", "user", "branch_manager"],
    children: [
      {
        key: "dispatch-list",
        label: "Dispatch List",
        role: ["admin", "user", "branch_manager"],
        path: "/dispatch",
      },
      {
        key: "dispatch-tracking",
        label: "Dispatch Tracking List",
        role: ["admin", "user", "branch_manager"],
        path: "/dispatch/tracking",
      },
    ],
  },

  {
    key: "manifest",
    icon: <FaShippingFast className="text-lg!" />,
    label: "Manifest",
    role: ["admin", "user", "branch_manager"],
    path: "/manifest",
  },

  {
    key: "expense",
    icon: <GiExpense className="text-lg!" />,
    label: "Expense",
    role: ["admin", "user", "branch_manager"],
    children: [
      {
        key: "expense-list",
        label: "Expense List",
        role: ["admin", "user", "branch_manager"],
        path: "/expense",
      },
      {
        key: "expense-type-list",
        label: "Expense Type List",
        role: ["admin", "user", "branch_manager"],
        path: "/expense/type",
      },
    ],
  },

  {
    key: "branch",
    icon: <TbGitBranch className="text-lg!" />,
    label: "Branch",
    role: ["admin", "branch_manager"],
    path: "/branch",
  },

  {
    key: "location",
    icon: <CiLocationOn className="text-lg!" />,
    label: "Location",
    role: ["admin", "user", "branch_manager"],
    path: "/location",
  },

  // {
  //   key: "product",
  //   icon: <AiOutlineProduct className="text-lg!" />,
  //   label: "Product",
  //   role: ["admin", "user", "branch_manager"],
  //   children: [
  //     {
  //       key: "product-list",
  //       label: "Product List",
  //       role: ["admin", "user", "branch_manager"],
  //       path: "/product",
  //     },
  //     {
  //       key: "product-category-list",
  //       label: "Product Category List",
  //       role: ["admin", "user", "branch_manager"],
  //       path: "/product/category",
  //     },
  //   ],
  // },

  {
    key: "user",
    icon: <FaUsers className="text-lg!" />,
    label: "Users",
    role: ["admin", "branch_manager"],
    path: "/user",
  },
    {
    key: "feedback",
    icon: <FaCommentDots className="text-lg!" />,
    role: ["admin", "branch_manager"],
    label: "Feedback",
    path: "/feedback",
  },
];

/* ===========================
   BREADCRUMB ITEMS
=========================== */

export const allBreadCrumbItems = [
  {
    key: "dashboard",
    icon: <HomeOutlined className="text-lg!" />,
    label: "Home",
    path: "/dashboard",
  },
  {
    key: "user",
    icon: <FaUsers className="text-lg!" />,
    label: "User",
    path: "/user",
  },
  {
    key: "vehicle",
    icon: <CarOutlined className="text-lg!" />,
    label: "Vehicle",
    path: "/vehicle",
  },
  {
    key: "vehicle-type",
    icon: <CarOutlined className="text-lg!" />,
    label: "Vehicle Type",
    path: "/vehicle/type",
  },
  {
    key: "customer",
    icon: <FaUserTie className="text-lg!" />,
    label: "Customer",
    path: "/customer",
  },
  {
    key: "driver",
    icon: <TbSteeringWheel className="text-lg!" />,
    label: "Driver",
    path: "/driver",
  },
  {
    key: "consignment",
    icon: <BsBoxSeam className="text-lg!" />,
    label: "consignment",
    path: "/consignment",
  },
  {
    key: "consignment-product",
    icon: <BsBoxSeam className="text-lg!" />,
    label: "Consignment Product List",
    path: "/consignment/consignment-product",
  },
  {
    key: "consignment-confirm",
    icon: <BsBoxSeam className="text-lg!" />,
    label: "Consignment Confirm",
    path: "/consignment/confirm",
  },
  
  {
    key: "rental",
    icon: <MdCarRental className="text-lg!" />,
    label: "Rental",
    path: "/rental",
  },
  {
    key: "dispatch",
    icon: <CiDeliveryTruck className="text-lg!" />,
    label: "Dispatch",
    path: "/dispatch",
  },
  {
    key: "dispatch-tracking",
    icon: <CiDeliveryTruck className="text-lg!" />,
    label: "Dispatch Tracking",
    path: "/dispatch/tracking",
  },
  {
    key: "manifest",
    icon: <FaShippingFast className="text-lg!" />,
    label: "Manifest",
    path: "/manifest",
  },
  {
    key: "expense",
    icon: <GiExpense className="text-lg!" />,
    label: "Expense",
    path: "/expense",
    parent: "expense",
  },
  {
    key: "expense-type",
    icon: <GiExpense className="text-lg!" />,
    label: "Expense Type",
    path: "/expense/type",
    parent: "expense",
  },
  {
    key: "branch",
    icon: <TbGitBranch className="text-lg!" />,
    label: "Branch",
    path: "/branch",
  },
  {
    key: "location",
    icon: <TbGitBranch className="text-lg!" />,
    label: "Location",
    path: "/location",
  },
  {
    key: "product",
    icon: <AiOutlineProduct className="text-lg!" />,
    label: "Product",
    path: "/product",
  },
  {
    key: "product-category",
    icon: <AiOutlineProduct className="text-lg!" />,
    label: "Product Category",
    path: "/product/category",
  },
  {
    key: "feedback",
    icon: <AiOutlineProduct className="text-lg!" />,
    label: "Feedback",
    path: "/feedback",
  },
];
