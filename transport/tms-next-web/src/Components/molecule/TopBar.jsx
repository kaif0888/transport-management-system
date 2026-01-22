// components/layout/TopBar.jsx
"use client";

import React, { useState, memo, useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import { IoMdLogOut } from "react-icons/io";
import { MdNotifications } from "react-icons/md";
import { IoClose } from "react-icons/io5";
import { useAuthContext } from "@/auth/authProvider";
import Breadcrumb from "../atom/Breadcrumb";
import Modal from "@/Components/atom/customModal";
import Avatar from "@/Components/atom/Avatar";
import { Button } from "@/Components/atom/Button";
import { getUserDetail } from "@/lib/cookies";
import { FaRegUserCircle } from "react-icons/fa";
import { getVehicleExpiries } from "@/service/vehicle";
import { getDriverLicenseExpiries } from "@/service/driver";

const TopBar = () => {
  const router = useRouter();
  const { logout: logoutHandler } = useAuthContext();
  const user = getUserDetail();
  const [showAvatarDropdown, setShowAvatarDropdown] = useState(false);
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const [showNotificationPanel, setShowNotificationPanel] = useState(false);

  // Notifications state - populated from API
  const [notifications, setNotifications] = useState([]);
  const hasFetchedRef = useRef(false);

  // Fetch vehicle expiries on component mount
  useEffect(() => {
    if (hasFetchedRef.current) return; // prevent second call in React StrictMode
    hasFetchedRef.current = true;
    const fetchExpiries = async () => {
      try {
        const allNotifications = [];

        // Fetch Vehicle Expiries
        console.log("[TopBar] Starting API call to getVehicleExpiries...");
        const vehicleExpiryData = await getVehicleExpiries();
        console.log("[TopBar] Vehicle Expiry Response received:", vehicleExpiryData);

        if (Array.isArray(vehicleExpiryData) && vehicleExpiryData.length > 0) {
          console.log("[TopBar] Converting", vehicleExpiryData.length, "vehicles to notifications");
          const vehicleNotifications = vehicleExpiryData.map((vehicle, index) => {
            console.log(`[TopBar] Vehicle ${index}:`, vehicle);
            
            // Remove RegistrationNumber from expiry list
            const expiryFields = { ...vehicle };
            delete expiryFields.RegistrationNumber;
            
            // Check if any expiry is within 5 days
            const today = new Date();
            const fiveDaysLater = new Date(today.getTime() + 5 * 24 * 60 * 60 * 1000);
            
            let expiryCount = 0;
            Object.values(expiryFields).forEach((expiryDate) => {
              if (expiryDate) {
                const expDate = new Date(expiryDate);
                if (expDate <= fiveDaysLater && expDate >= today) {
                  expiryCount++;
                }
              }
            });
            
            const hasExpiryWithin5Days = expiryCount > 0;
            const title = hasExpiryWithin5Days 
              ? `Registration Number ${vehicle.RegistrationNumber}, have some expiries within 5 days.`
              : `Registration Number ${vehicle.RegistrationNumber}`;
            
            return {
              id: `expiry-${vehicle.RegistrationNumber || index}`,
              title: title,
              expiries: expiryFields,   // dynamic expiries
              timestamp: new Date().toLocaleDateString(),
              read: false,
              type: "expiry",
            };
          });
          allNotifications.push(...vehicleNotifications);
        }

        // Fetch Driver License Expiries
        console.log("[TopBar] Starting API call to getDriverLicenseExpiries...");
        let driverLicenseData = await getDriverLicenseExpiries();
        console.log("[TopBar] Driver License Response received:", driverLicenseData);
        console.log("[TopBar] Type of response:", typeof driverLicenseData);
        console.log("[TopBar] Is Array:", Array.isArray(driverLicenseData));

        // Handle if response is wrapped in a data property
        if (driverLicenseData && driverLicenseData.data) {
          driverLicenseData = driverLicenseData.data;
        }

        // Ensure it's an array
        if (!Array.isArray(driverLicenseData)) {
          driverLicenseData = [driverLicenseData];
        }

        if (driverLicenseData && driverLicenseData.length > 0 && driverLicenseData[0]) {
          console.log("[TopBar] Converting", driverLicenseData.length, "driver licenses to notifications");
          const driverNotifications = driverLicenseData.map((driver, index) => {
            console.log(`[TopBar] Driver ${index}:`, driver);
            console.log(`[TopBar] License_Number value: ${driver.License_Number}`);
            console.log(`[TopBar] driverLicencesExpiry value: ${driver.driverLicencseExpiry}`);
            
            // Check if license is expiring within 5 days
            const today = new Date();
            const fiveDaysLater = new Date(today.getTime() + 5 * 24 * 60 * 60 * 1000);
            
            const licenseExpiryRaw = driver.driverLicencseExpiry || null;
            const licenseExpiryDate = licenseExpiryRaw ? new Date(licenseExpiryRaw) : null;
            
            const hasExpiryWithin5Days =
              licenseExpiryDate &&
              licenseExpiryDate <= fiveDaysLater &&
              licenseExpiryDate >= today;
            
            const licenseNumber = driver.License_Number || "Unknown";
            const title = hasExpiryWithin5Days 
              ? `Driving License Number ${licenseNumber} have expiry within 5 days.`
              : `Driving License Number ${licenseNumber} have expiry within 5 days.`;
            
            return {
              id: `driver-license-${licenseNumber || index}`,
              title: title,
              licenseNumber: licenseNumber,
              licenseExpiry: licenseExpiryRaw || "N/A",
              timestamp: new Date().toLocaleDateString(),
              read: false,
              type: "driverLicense",
            };
          });
          allNotifications.push(...driverNotifications);
        }

        console.log("[TopBar] Setting all notifications:", allNotifications);
        setNotifications(allNotifications);
      } catch (error) {
        console.error("[TopBar] Error fetching expiries:", error);
        console.error("[TopBar] Error message:", error.message);
        console.error("[TopBar] Error stack:", error.stack);
        setNotifications([]);
      }
    };

    fetchExpiries();
  }, []);

  const handleOpenNotifications = () => {
    console.log("[TopBar] Opening notifications panel. Current notifications:", notifications);
    // Mark all unread notifications as read
    const updatedNotifications = notifications.map((notification) => ({
      ...notification,
      read: true,
    }));
    setNotifications(updatedNotifications);
    setShowNotificationPanel(true);
  };

  const handleConfirmLogout = () => {
    logoutHandler();
    setShowLogoutModal(false);
  };

  const handleLogoutClick = () => {
    setShowAvatarDropdown(false);
    setShowLogoutModal(true);
  };

  return (
    <>
      {/* Notification Panel */}
      {showNotificationPanel && (
        <>
          {/* Backdrop */}
          <div
            className="fixed inset-0 z-40 bg-black/50"
            onClick={() => setShowNotificationPanel(false)}
          />

          {/* Notification Panel */}
          <div className="fixed right-0 top-16 bottom-0 w-1/2 bg-white shadow-2xl z-50 flex flex-col animate-in slide-in-from-right-full duration-300">
            {/* Header */}
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
              <h2 className="text-xl font-bold text-gray-800">Notifications</h2>
              <button
                onClick={() => setShowNotificationPanel(false)}
                className="p-1 hover:bg-gray-100 rounded-full transition-colors"
              >
                <IoClose className="w-6 h-6 text-gray-600" />
              </button>
            </div>

            {/* Notification List */}
            <div className="flex-1 overflow-y-auto">
              {notifications && notifications.length > 0 ? (
                notifications.map((notification) => (
                  <div
                    key={notification.id}
                    className={`px-6 py-4 border-b border-gray-100 hover:bg-gray-50 cursor-pointer transition-colors ${
                      !notification.read ? "bg-blue-50" : ""
                    }`}
                  >
                    <div className="flex items-start gap-3">
                      <div
                        className={`w-2 h-2 rounded-full mt-2 flex-shrink-0 ${
                          !notification.read ? "bg-[#750014]" : "bg-gray-300"
                        }`}
                      />
                      <div className="flex-1 min-w-0">
                        <p className="font-semibold text-gray-800 text-sm">
                          {notification.title}
                        </p>
                        {notification.type === "expiry" && notification.expiries ? (
                          <div className="mt-2 space-y-1">
                            {Object.entries(notification.expiries).map(([key, value]) => {
                              // Format date values properly
                              let displayValue = value;
                              if (value && !isNaN(Date.parse(value))) {
                                try {
                                  displayValue = new Date(value).toLocaleDateString();
                                } catch (e) {
                                  displayValue = value;
                                }
                              }
                              return (
                                <div key={key} className="text-xs text-gray-600">
                                  <span className="font-medium">
                                    {key.replace(/([A-Z])/g, " $1").trim()}:
                                  </span>{" "}
                                  {displayValue}
                                </div>
                              );
                            })}
                          </div>
                        ) : null}
                        {notification.type === "driverLicense" ? (
                          <div className="mt-2 space-y-1">
                            <div className="text-xs text-gray-600">
                              <span className="font-medium">License Number:</span>{" "}
                              {notification.licenseNumber}
                            </div>
                            <div className="text-xs text-gray-600">
                              <span className="font-medium">Expiry Date:</span>{" "}
                              {notification.licenseExpiry && notification.licenseExpiry !== "N/A" && !isNaN(Date.parse(notification.licenseExpiry))
                                ? new Date(notification.licenseExpiry).toLocaleDateString()
                                : notification.licenseExpiry}
                            </div>
                          </div>
                        ) : null}
                        <p className="text-gray-400 text-xs mt-2">
                          {notification.timestamp}
                        </p>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="flex items-center justify-center h-full">
                  <p className="text-gray-500">No notifications</p>
                </div>
              )}
            </div>

            {/* Footer */}
            <div className="px-6 py-4 border-t border-gray-200">
              <p className="text-sm text-gray-500 text-center">
                End of notifications
              </p>
            </div>
          </div>
        </>
      )}

      {/* Logout Confirmation Modal */}
      <Modal
        isOpen={showLogoutModal}
        onClose={() => setShowLogoutModal(false)}
        title="Confirm Logout"
        footer={
          <div className="flex justify-end space-x-3">
            <Button variant="outline" onClick={() => setShowLogoutModal(false)}>
              Cancel
            </Button>
            <Button
              variant="primary"
              onClick={handleConfirmLogout}
              className="bg-[#750014] hover:bg-[#750014]/90"
            >
              <IoMdLogOut className="w-4 h-4 mr-2" />
              Logout
            </Button>
          </div>
        }
      >
        <p className="text-gray-600">Are you sure you want to logout?</p>
      </Modal>

      {/* Header */}
      <header className="sticky top-0 z-30 h-16 bg-white border-b border-gray-200 shadow-sm">
        <div className="flex items-center justify-between h-full px-6">
          {/* Breadcrumb */}
          <Breadcrumb />

          {/* User Menu */}
          <div className="flex items-center gap-4">
            {/* Notification Icon */}
            <div
              className="relative cursor-pointer group"
              onClick={handleOpenNotifications}
            >
              <MdNotifications className="w-6 h-6 text-gray-600 hover:text-gray-800 transition-colors" />
              <span className="absolute -top-1 -right-1 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-[#750014] rounded-full">
                {notifications.filter((n) => !n.read).length}
              </span>
            </div>

            {/* User Avatar */}
            <div className="relative">
              <div
                className="flex items-center cursor-pointer"
                onClick={() => setShowAvatarDropdown(!showAvatarDropdown)}
              >
                <Avatar name={`${user?.firstName} ${user?.secondName}`} />
              </div>

            {/* Dropdown Menu */}
            {showAvatarDropdown && (
              <>
                {/* Backdrop */}
                <div
                  className="fixed inset-0 z-10"
                  onClick={() => setShowAvatarDropdown(false)}
                />

                {/* Dropdown Content */}
                <div className="absolute right-0 top-full mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 z-20">
                  <div className="px-4 py-2 flex flex-col gap-2 ">
                    <p className="text-gray-800 font-semibold capitalize flex gap-2 items-center">
                      <FaRegUserCircle /> {user?.firstName}{" "}
                      {user?.secondName}
                    </p>
                    <Button
                      onClick={handleLogoutClick}
                      icon={<IoMdLogOut />}
                      className="bg-[#750014]!  text-white! justify-start!"
                    >
                      Logout
                    </Button>
                  </div>
                </div>
              </>
            )}
            </div>
          </div>
        </div>
      </header>
    </>
  );
};

export default memo(TopBar);
