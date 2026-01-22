// // src/auth/authProvider.js
// import { createContext, useContext, useEffect, useState } from 'react';
// import { useRouter } from 'next/navigation';
// // import { setAuthCookies, getAccessToken } from "@/lib/cookies";
// import { getAccessToken, clearAuthCookies } from "@/lib/cookies";

// const AuthContext = createContext();

// export const AuthProvider = ({ children }) => {
//   const [authenticated, setAuthenticated] = useState(false);
//   const router = useRouter();

//   useEffect(() => {
//     const token = getAccessToken();
//     setAuthenticated(!!token);
//   }, []);

//   // const logout = () => {
//   //   clearAuthCookies();
//   //   setAuthenticated(false);
//   //   router.push('/login');
//   // };

//    const logout = () => {
//     clearAuthCookies();        // ✅ actually clears cookies
//     setAuthenticated(false);
//     router.replace("/login"); // ✅ prevent back navigation
//   };

//   return (
//     <AuthContext.Provider value={{ authenticated, setAuthenticated, logout }}>
//       {children}
//     </AuthContext.Provider>
//   );
// };

// export const useAuthContext = () => useContext(AuthContext);


// src/auth/authProvider.js
"use client";

import { createContext, useContext, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getAccessToken, clearAuthCookies } from "@/lib/cookies";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [authenticated, setAuthenticated] = useState(false);
  const router = useRouter();

  useEffect(() => {
    const token = getAccessToken();
    setAuthenticated(Boolean(token));
  }, []);

  const logout = () => {
    console.log("🚪 Logging out...");
    clearAuthCookies();
    setAuthenticated(false);
    router.replace("/login");
  };

  return (
    <AuthContext.Provider value={{ authenticated, setAuthenticated, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuthContext = () => useContext(AuthContext);
