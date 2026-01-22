// src/utils/cookies.js
import Cookies from 'js-cookie';

const TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';
const ROLE_KEY = 'role';
const USER_KEY = 'user';

export const setAuthCookies = ({ accessToken, refreshToken, role, user }) => {
  console.log("🔍 Setting cookies...");
  
  if (accessToken) {
    Cookies.set(TOKEN_KEY, accessToken, { 
      expires: 7,  // ✅ 7 days
      path: '/',   // ✅ Available on all pages
      sameSite: 'lax'
    });
    console.log("✅ Token set:", accessToken.substring(0, 20) + "...");
  }
  
  if (refreshToken) {
    Cookies.set(REFRESH_TOKEN_KEY, refreshToken, { expires: 7, path: '/' });
  }
  
  if (role) {
    Cookies.set(ROLE_KEY, role, { expires: 7, path: '/' });
  }
  
  if (user) {
    Cookies.set(USER_KEY, JSON.stringify(user), { expires: 7, path: '/' });
  }
  
  console.log("✅ All cookies set");
};

export const clearAuthCookies = () => {
  Cookies.remove(TOKEN_KEY, { path: '/' });
  Cookies.remove(REFRESH_TOKEN_KEY, { path: '/' });
  Cookies.remove(ROLE_KEY, { path: '/' });
  Cookies.remove(USER_KEY, { path: '/' });
  console.log("🗑️ Cookies cleared");
};

export const getAccessToken = () => {
  const token = Cookies.get(TOKEN_KEY);
  console.log("🔍 Getting token:", token ? "Found ✅" : "NOT FOUND ❌");
  return token;
};

export const getRefreshToken = () => Cookies.get(REFRESH_TOKEN_KEY);
export const getUserRole = () => Cookies.get(ROLE_KEY);
export const getUserDetail = () => {
  const userStr = Cookies.get(USER_KEY);
  return userStr ? JSON.parse(userStr) : null;
};