"use client";
import axios from "axios";
import {
  getAccessToken,
  getRefreshToken,
  setAuthCookies,
  clearAuthCookies,
} from "./cookies";

// Base URL for API - REMOVED TRAILING SLASH
const SM_BASE_URL =
  process.env.NEXT_PUBLIC_APP_BASE_URL || "http://localhost:1001";

// Create axios instance
const smInstance = axios.create({
  baseURL: SM_BASE_URL,
});

// Request interceptor - add auth token to requests
smInstance.interceptors.request.use(
  (config) => {
    // Get token from cookies if available
    if (typeof window !== "undefined") {
      const token = getAccessToken();

      // If token exists, add it to the headers
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    // Enhanced logging
    console.log("🔵 Request:", {
      method: config.method?.toUpperCase(),
      url: config.url,
      fullUrl: `${config.baseURL}${config.url}`,
      hasAuth: !!config.headers.Authorization,
    });
    
    return config;
  },
  (error) => {
    console.error("❌ Request error:", error);
    return Promise.reject(error);
  }
);

// Response interceptor - handle token refresh
smInstance.interceptors.response.use(
  (response) => {
    console.log("✅ Response:", {
      status: response.status,
      url: response.config.url,
    });
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // Log the error for debugging
    console.error("❌ Response error:", {
      status: error.response?.status,
      url: error.config?.url,
      message: error.message,
    });

    // If error is 401 (Unauthorized) and we haven't already tried to refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = getRefreshToken();

        // Create a new axios instance without interceptors for refresh token request
        const refreshAxios = axios.create({
          baseURL: SM_BASE_URL,
        });

        // Call refresh token endpoint without any Authorization headers
        const response = await refreshAxios.post("/api/v1/auth/refresh", {
          token: refreshToken,
        });

        // Update tokens in cookies
        setAuthCookies({
          accessToken: response.data.token,
          refreshToken: response.data.refreshToken,
        });

        // Retry the original request with the new token
        originalRequest.headers.Authorization = `Bearer ${response.data.token}`;
        return smInstance(originalRequest);
      } catch (refreshError) {
        console.error("❌ Token refresh failed:", refreshError);
        if (typeof window !== "undefined") {
          clearAuthCookies();
          window.location.href = "/login";
        }
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export const smClient = smInstance;