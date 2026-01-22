// // src/auth/useAuth.js
// import { useRouter,usePathname  } from 'next/navigation';
// import { useEffect } from 'react';
// import { getAccessToken } from '@/lib/cookies';  // ✅ @/lib/cookies (not @/utils)

// const unprotectedRoutes = ['/login', '/termsAndCondition'];

// const useAuth = () => {
//   const router = useRouter();
//   const pathname = usePathname(); 

//   useEffect(() => {
//     const token = getAccessToken();
//     // const isUnprotected = unprotectedRoutes.includes(router.pathname);
//      const isUnprotected = unprotectedRoutes.includes(pathname);

//     if (!token && !isUnprotected) {
//       router.replace('/login');
//     }
//   }, [router, router.pathname]);
// };

// export default useAuth;





// src/auth/useAuth.js
import { useRouter, usePathname } from "next/navigation";
import { useEffect } from "react";
import { getAccessToken } from "@/lib/cookies";

const unprotectedRoutes = ["/login", "/termsAndCondition"];

const useAuth = () => {
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    // Allow public tracking without login
    if (pathname?.startsWith("/publicDispatchTracking")) {
      return;
    }

    const token = getAccessToken();
    const isUnprotected = unprotectedRoutes.includes(pathname);

    if (!token && !isUnprotected) {
      router.replace("/login");
    }
  }, [pathname, router]);
};

export default useAuth;
