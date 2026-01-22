import { smClient } from "@/lib";

export async function signIn(payload) {
  const res = await smClient.post("/api/v1/auth/signin", payload);
  return res.data;
}

export async function signUp(payload) {
  await smClient.post("/api/v1/auth/signup", payload);
}

export const forgotPassword = async (email) => {
  const res = await smClient.post("/api/user/forgot-password", { email });
  return res.data;
};

export const resetPassword = async (payload) => {
  const res = await smClient.post("/api/user/reset-password", payload);
  return res.data;
};
