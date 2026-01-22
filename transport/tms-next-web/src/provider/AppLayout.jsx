
// components/layout/AppLayout.jsx
'use client'

import { memo, useMemo } from 'react';
import { usePathname } from 'next/navigation';
import dynamic from 'next/dynamic';
import SideBar from '@/Components/molecule/SideBar';
import { ConfigProvider, message } from 'antd';
// Lazy load TopBar for better performance
const TopBar = dynamic(() => import('@/Components/molecule/TopBar'), {
  ssr: false,
  loading: () => (
    <div className="h-16 bg-white border-b border-gray-200 animate-pulse" />
  )
});

// Memoized Footer component
const Footer = memo(() => (
  <footer className="bg-white border-t border-gray-200 py-4 text-center text-gray-600 text-sm">
    TMS © {new Date().getFullYear()} Transport Management System
  </footer>
));

Footer.displayName = 'Footer';

// Routes where layout should not be displayed
const NO_LAYOUT_ROUTES = ['/login'];

function AppLayout({ children }) {
  const pathname = usePathname();
  
  // Quick return if no layout should be shown
  // if (NO_LAYOUT_ROUTES.includes(pathname)) {
  //   return children;
  // }

  // If route is explicitly excluded or is the public tracking route, render children only
  if (
    NO_LAYOUT_ROUTES.includes(pathname) ||
    pathname?.startsWith('/publicDispatchTracking')
  ) {
    return children;
  }

  // Configure Ant Design message globally
  message.config({
    maxCount: 1, // Only one message can be displayed at a time
    duration: 2, // Message will disappear after 2 seconds
  });

  return (
    <ConfigProvider
      warning={{
        // Suppress the specific warning about Ant Design v5 compatibility with React v19.
        // This warning is usually safe to ignore if you're using React 18,
        // and it won't impact the functionality.
        // The key is 'antd-compatible' as per Ant Design's warning system.
        skip: [/antd-compatible/],
      }}
    >
      <div className="flex h-screen bg-gray-50">
        {/* Sidebar */}
        <SideBar />
        
        {/* Main content area */}
        <div className="flex-1 flex flex-col ml-[200px]">
          {/* TopBar */}
          <TopBar />
          
          {/* Content */}
          <main className="flex-1 overflow-auto">
            <div className="p-6">
              <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 min-h-[calc(100vh-200px)]">
                {children}
              </div>
            </div>
          </main>
          
          {/* Footer */}
          <Footer />
        </div>
      </div>
    </ConfigProvider>
  );
}

export default memo(AppLayout);