"use client";

// Import only the specific icons needed
import { FaUsers, FaCheck, FaChartLine, FaFileInvoiceDollar,FaTimes,FaShippingFast, FaBoxOpen,FaTruck } from "react-icons/fa";
import { memo, useCallback, useMemo } from 'react';
import { StatCard } from "@/Components/atom/StatCard";
import { getDashboard } from "@/service/dashboard";
import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import PageWrapper from "@/Data/PageWrapper";
import Title from "@/Components/atom/Title";
import FloatingFeedbackButton from "@/Components/atom/FloatingFeedbackButton";

// Memoize StatCard component
const MemoizedStatCard = memo(StatCard);

export default function Dashboard() {
  const router = useRouter();
  
  // Use query with optimized configuration
  const { data: dashboardData, isLoading } = useQuery({
    queryKey: ["dashboard"],
    queryFn: getDashboard,
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
    refetchOnWindowFocus: false, // Prevent unnecessary refetches
    refetchOnMount: true,
  });

  // Create a memoized icon map for better performance
  const iconMap = useMemo(() => ({
    FaUser: FaUsers,
    FaTruck: FaTruck,
    FaChartLine: FaChartLine,
    FaCheck:FaCheck,
    FaTimes:FaTimes,
    FaFileInvoiceDollar: FaFileInvoiceDollar,
    FaShippingFast:FaShippingFast,
    FaBoxOpen: FaBoxOpen
  }), []);
  
  // Memoize card click handler
  const handleCardClick = useCallback((route) => {
    router.push(route);
  }, [router]);

  // Create skeletons with useMemo
  const skeletonCards = useMemo(() => [0, 1, 2, 3, 4], []);

  return (
    <PageWrapper>
      <Title title="Dashboard" className="mb-2"/>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
    {!isLoading && dashboardData && dashboardData.length > 0 ? (
      dashboardData.map((stat, index) => (
        <MemoizedStatCard
          key={stat.label || index}
          label={stat.label}
          count={stat.count}
          icon={stat.icon ? iconMap[stat.icon] : null}
          color={stat.color}
          loading={false}
          onClick={() => handleCardClick(stat.route)}
        />
      ))
    ) : (
      skeletonCards.map((index) => (
        <MemoizedStatCard
          key={`skeleton-${index}`}
          label="Loading..."
          count={0}
          icon={null}
          loading={true}
          className="opacity-70"
        />
      ))
    )}
  </div>
  <FloatingFeedbackButton />  
    </PageWrapper>
  );
}