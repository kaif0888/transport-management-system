'use client'
import { useState,memo } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import AppLayout from "@/provider/AppLayout";
import { AuthProvider } from '@/auth/authProvider';
import useAuth from "@/auth/useAuth";
import dynamic from 'next/dynamic';

// Dynamically import ToastContainer with no SSR to reduce initial bundle size
const ToastContainer = dynamic(
  () => import("react-toastify").then(mod => ({ default: mod.ToastContainer })),
  { ssr: false }
);

// Default toast options to avoid recreating on each render
const toastOptions = {
  className: "max-w-[50vw]",
  position: "bottom-right",
  autoClose: 3000,
};

// Default query client options
const queryClientOptions = {
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 30 * 1000, // Reduce to 30 seconds for more frequent updates
      cacheTime: 5 * 60 * 1000, // Reduce to 5 minutes
      suspense: false, // Disable suspense to prevent loading states from blocking
    },
  },
};

// Memoize the entire component to prevent unnecessary rerenders
const Providers = memo(function Providers({ children }) {
  // Create QueryClient instance once and reuse it
  const [queryClient] = useState(() => new QueryClient(queryClientOptions));
  
  // Use auth hook
  useAuth();
  
  return (
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <AppLayout>
            {children}
            <ToastContainer {...toastOptions} />
          </AppLayout>
        </AuthProvider>
      </QueryClientProvider>
  );
});

export default Providers;