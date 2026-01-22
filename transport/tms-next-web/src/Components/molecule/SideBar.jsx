"use client";

import React, { useState, useEffect, useMemo, useCallback, memo } from "react";
import { usePathname, useRouter } from "next/navigation";
import { getUserRole } from "@/lib/cookies";
import { allMenuItems } from "@/Data/impData";
import Image from "next/image";
import TMSLogo from "@/Images/TMS-Logo.png";
import { FaChevronRight } from "react-icons/fa";

const SideBar = () => {
  const pathname = usePathname();
  const router = useRouter();
  const [userRole, setUserRole] = useState(null);
  const [openSubmenu, setOpenSubmenu] = useState(null);

  useEffect(() => {
    const role = getUserRole();
    setUserRole(role === null ? "admin" : role?.toLowerCase());
  }, []);

  const handleMenuClick = useCallback(
    (path) => {
      router.push(path);
      setOpenSubmenu(null);
    },
    [router]
  );

  const filteredMenuItems = useMemo(() => {
    if (!userRole) return [];

    return allMenuItems
      .filter((item) => item.role.includes(userRole))
      .map((item) => ({
        ...item,
        children: item.children?.filter((child) =>
          child.role.includes(userRole)
        ),
      }))
      .filter((item) => !item.children || item.children.length > 0);
  }, [userRole]);

  const isActiveItem = useCallback(
    (item) => {
      if (item.path === pathname) return true;
      if (item.children?.some((child) => child.path === pathname)) return true;

      if (item.key === "consignment") {
        return pathname.startsWith("/consignment");
      }
      if (item.key === "dispatch") {
        return pathname.startsWith("/dispatch");
      }
      if (item.key === "expense") {
        return pathname.startsWith("/expense");
      }
      if (item.key === "branch") {
        return pathname.startsWith("/branch");
      }
      if (item.key === "location") {
        return pathname.startsWith("/location");
      }
      if (item.key === "product") {
        return pathname.startsWith("/product");
      }
      if (item.key === "vehicle") {
        return pathname.startsWith("/vehicle");
      }
      // ✅ ADD BOX MENU DETECTION
      if (item.key === "box") {
        return pathname.startsWith("/box");
      }

      return false;
    },
    [pathname]
  );

  const MenuItem = memo(({ item }) => {
    const [menuRef, setMenuRef] = useState(null);
    const [submenuPosition, setSubmenuPosition] = useState({ top: 0, left: 0 });
    const [openTimeout, setOpenTimeout] = useState(null);
    const hasChildren = item.children && item.children.length > 0;
    const isActive = isActiveItem(item);
    const isSubmenuOpen = openSubmenu === item.key;

    useEffect(() => {
      if (menuRef && isSubmenuOpen) {
        const rect = menuRef.getBoundingClientRect();
        setSubmenuPosition({
          top: rect.top,
          left: rect.right + 12,
        });
      }
    }, [menuRef, isSubmenuOpen]);

    const handleMouseEnter = () => {
      if (hasChildren) {
        if (openTimeout) {
          clearTimeout(openTimeout);
        }
        const timeout = setTimeout(() => {
          setOpenSubmenu(item.key);
        }, 150);
        setOpenTimeout(timeout);
      }
    };

    const handleMouseLeave = () => {
      if (hasChildren) {
        if (openTimeout) {
          clearTimeout(openTimeout);
        }
        setTimeout(() => setOpenSubmenu(null), 150);
      }
    };

    const handleSubmenuEnter = () => {
      if (hasChildren) {
        if (openTimeout) {
          clearTimeout(openTimeout);
        }
        setOpenSubmenu(item.key);
      }
    };

    const baseClasses =
      "flex items-center px-4 py-3 text-sm font-medium transition-all duration-200 cursor-pointer mx-2 rounded-lg relative";

    const itemClasses = isActive
      ? `${baseClasses} bg-[#750014]/10 text-[#750014] border-r-4 border-[#750014]`
      : `${baseClasses} text-gray-700 hover:bg-gray-100 hover:text-[#750014]`;

    const isChildActive = (child) => {
      if (child.path === pathname) return true;
      if (
        child.key === "consignment-product" &&
        pathname.startsWith("/consignment/consignment-product")
      )
        return true;
      // ✅ ADD BOX SUBMENU DETECTION
      if (
        child.key === "box-list" &&
        pathname === "/box"
      )
        return true;
      if (
        child.key === "manage-box-products" &&
        // pathname.startsWith("/box/manage-products")
        pathname === "/box/manage-products"
      )
        return true;
      return false;
    };

    return (
      <div
        className="relative"
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
      >
        <div
          ref={setMenuRef}
          className={itemClasses}
          onClick={() => {
            if (!hasChildren && item.path) {
              handleMenuClick(item.path);
            }
          }}
        >
          {item.icon && <span className="mr-3 text-lg">{item.icon}</span>}
          <span className="flex-1">{item.label}</span>
          {hasChildren && <FaChevronRight className="w-4 h-4 ml-2" />}
        </div>

        {hasChildren && isSubmenuOpen && (
          <div
            className="fixed bg-white border border-gray-200 rounded-lg shadow-lg py-2 min-w-[180px] z-50"
            style={{
              left: `${submenuPosition.left}px`,
              top: `${submenuPosition.top}px`,
              display: submenuPosition.top === 0 ? "none" : "block",
            }}
            onMouseEnter={handleSubmenuEnter}
            onMouseLeave={() => setOpenSubmenu(null)}
          >
            {item.children.map((child) => (
              <div
                key={child.key}
                className={`px-4 py-2 text-sm font-medium cursor-pointer transition-all duration-200 ${
                  isChildActive(child)
                    ? "bg-[#750014] text-white"
                    : "text-gray-700 hover:bg-gray-100 hover:text-[#750014]"
                }`}
                onClick={() => handleMenuClick(child.path)}
              >
                {child.icon && (
                  <span className="mr-3 text-lg">{child.icon}</span>
                )}
                {child.label}
              </div>
            ))}
          </div>
        )}
      </div>
    );
  });

  MenuItem.displayName = "MenuItem";

  return (
    <div className="fixed left-0 top-0 h-full w-[200px] bg-white border-r border-gray-200 shadow-sm z-40">
      <div className="flex flex-col h-full">
        <div className="flex flex-col items-center justify-center p-4 border-b border-gray-200">
          <Image src={TMSLogo} alt="TMS Logo" width={80} height={80} />
          <span className="text-xs text-center font-bold text-[#750014] tracking-wider underdog">
            TRANSPORT
          </span>
        </div>

        <nav className="flex-1 overflow-y-auto py-4 scrollbar-hide">
          <div className="space-y-1">
            {filteredMenuItems.map((item) => (
              <MenuItem key={item.key} item={item} />
            ))}
          </div>
        </nav>
      </div>

      <style jsx>{`
        .scrollbar-hide {
          -ms-overflow-style: none;
          scrollbar-width: none;
        }
        .scrollbar-hide::-webkit-scrollbar {
          display: none;
        }
      `}</style>
    </div>
  );
};

export default memo(SideBar);
