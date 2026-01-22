import {  clsx } from "clsx";
import { twMerge } from "tailwind-merge";

export const cn = (...inputs) => {
    return twMerge(clsx(inputs));
  };

  export function transformShortName(name) {
    return name
      .split(" ")
      .slice(0, 2)
      .map((word) => word.at(0))
      .filter(Boolean)
      .join("");
  }

  // export function isAuthenticated() {
  //   if (typeof window !== 'undefined') {
  //     return !!localStorage.getItem("accessToken");
  //   }
  //   return false;
  // }
  
  // // Function to get user role
  // export function getUserRole() {
  //   if (typeof window !== 'undefined') {
  //     return localStorage.getItem("role");
  //   }
  //   return null;
  // }
  
  // // Function to logout user
  // export function logout() {
  //   if (typeof window !== 'undefined') {
  //     localStorage.removeItem("firstName");
  //     localStorage.removeItem("secondName");
  //     localStorage.removeItem("email");
  //     localStorage.removeItem("role");
  //     localStorage.removeItem("accessToken");
  //     localStorage.removeItem("refreshToken");
      
  //     // Redirect to login page
  //     window.location.href = "/login";
  //   }
  // }
  