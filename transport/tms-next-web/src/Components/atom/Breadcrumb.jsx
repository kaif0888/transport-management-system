import React from 'react';
import { usePathname, useRouter } from 'next/navigation';
import { Breadcrumb as AntBreadcrumb } from 'antd';
import { allBreadCrumbItems } from '@/Data/impData';

const Breadcrumb = () => {
  const pathname = usePathname();
  const router = useRouter();

  const findBreadcrumbItemByPath = (path) => {
    return allBreadCrumbItems.find(item => item.path === path);
  };

  const findParentBreadcrumbItem = (parentKey) => {
    return allBreadCrumbItems.find(item => item.key === parentKey);
  };

  const buildBreadcrumbChain = (path) => {
    const pathSegments = path.split('/').filter(segment => segment);
    const chain = [];
    
    if (path !== '/dashboard') {
      const homeItem = allBreadCrumbItems.find(item => item.key === 'dashboard');
      if (homeItem) {
        chain.push(homeItem);
      }
    }
    
    if (pathSegments.length > 0) {
      const exactMatch = findBreadcrumbItemByPath(path);
      
      if (exactMatch) {
        if (exactMatch.parent) {
          const parentItem = findParentBreadcrumbItem(exactMatch.parent);
          if (parentItem && !chain.find(item => item.key === parentItem.key)) {
            chain.push(parentItem);
          }
        }
        
        if (!chain.find(item => item.key === exactMatch.key)) {
          chain.push(exactMatch);
        }
      } else {
        const basePath = '/' + pathSegments[0];
        const baseItem = findBreadcrumbItemByPath(basePath);
        
        if (baseItem && !chain.find(item => item.key === baseItem.key)) {
          chain.push(baseItem);
        }
        
        // Handle multiple dynamic segments
        for (let i = 1; i < pathSegments.length; i++) {
          const segment = pathSegments[i];
          const segmentPath = '/' + pathSegments.slice(0, i + 1).join('/');

          // 🔥 Handle analysis route explicitly
          if (segment === "analysis") {
            chain.push({
              key: "analysis",
              label: "analysis",
              path: "/consignment",
            });
            continue;
          }

          const segmentItem = findBreadcrumbItemByPath(segmentPath);

          if (segmentItem) {
            chain.push(segmentItem);
          } else {
            chain.push({
              key: `dynamic-${segment}`,
              label: segment,
              path: segmentPath,
              isDynamic: true
            });
          }
        }

      }
    }
    
    return chain;
  };

  const generateBreadcrumbItems = () => {
    const breadcrumbChain = buildBreadcrumbChain(pathname);
    const items = [];

    if (!breadcrumbChain || breadcrumbChain.length === 0) {
      if (pathname === '/dashboard') {
        const homeItem = allBreadCrumbItems.find(item => item.key === 'dashboard');
        if (homeItem) {
          items.push({
            title: <span>{homeItem.label}</span>,
          });
        }
      }
      return items;
    }

    breadcrumbChain.forEach((item, index) => {
      const isLast = index === breadcrumbChain.length - 1;
      const isActive = item.path === pathname || isLast;
      const isClickable = item.path && !isActive;
      
      items.push({
        title: (
          <span
            onClick={() => isClickable && router.push(item.path)}
            style={{ 
              cursor: isClickable ? 'pointer' : 'default',
              color: isActive ? '#750014' : 'inherit',
              fontWeight: isActive ? '500' : 'normal',
              display: 'flex',
              alignItems: 'center',
              gap: '4px'
            }}
          >
            {!isActive && item.icon}
            {item.label}
          </span>
        ),
      });
    });

    return items;
  };

  return <AntBreadcrumb items={generateBreadcrumbItems()} />;
};

export default Breadcrumb;