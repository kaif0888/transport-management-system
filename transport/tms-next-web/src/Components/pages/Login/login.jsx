"use client";
import { useState } from "react";
import { Input } from "@/Components/atom/Input";
import { useFormik } from "formik";
import { loginValidationSchema } from "@/Data/constant";
import { setAuthCookies, getAccessToken } from "@/lib/cookies";
import { useAuthContext } from "@/auth/authProvider";
import { FaTruck } from "react-icons/fa";
import { useRouter } from "next/navigation";
import { toast } from "react-toastify";
import { signIn } from "@/service/auth";
import Transport from "@/Images/Transportation.webp";
import { Modal } from "antd";
import ForgotPassword from "@/Components/pages/Login/ForgotPassword";

export default function Login() {
  const [isLoading, setIsLoading] = useState(false);
  const [loginType, setLoginType] = useState("email");
  const [otpSent, setOtpSent] = useState(false);
  const { setAuthenticated } = useAuthContext();
  const router = useRouter();
  const [isForgotPasswordModalOpen, setIsForgotPasswordModalOpen] = useState(false);

  const formik = useFormik({
    initialValues: {
      email: "",
      password: "",
      mobile: "",
      otp: "",
    },
    validationSchema: loginType === "email" ? loginValidationSchema : null,
    onSubmit: async (values) => {
      try {
        setIsLoading(true);

        // EMAIL LOGIN
        if (loginType === "email") {
          const response = await signIn({
            email: values.email,
            password: values.password,
          });
          handleSuccess(response);
          return;
        }

        // SEND OTP
        if (!otpSent) {
          const response = await fetch("http://localhost:1001/api/auth/otp/send-otp", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ mobile: values.mobile }),
          });

          if (!response.ok) {
            throw new Error("Failed to send OTP");
          }

          toast.success("OTP sent to mobile number");
          setOtpSent(true);
          return;
        }

        // VERIFY OTP
        const res = await fetch("http://localhost:1001/api/auth/otp/verify-otp", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            mobileNumber: values.mobile,
            otp: values.otp,
          }),
        });

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data.message || "Login failed");
        }

        handleSuccess({
          token: data.token,
          refreshToken: null,
          role: "USER",
          email: values.mobile,
          firstName: null,
          secondName: null,
        });
      } catch (error) {
        console.error("Login error:", error);
        toast.error(error.message || "Login failed");
      } finally {
        setIsLoading(false);
      }
    },
  });

  const handleSuccess = (response) => {
    const { firstName, secondName, email, role, token, refreshToken } = response;

    console.log(" Login success:", { token: token?.substring(0, 20), role, email });

    if (!token) {
      toast.error("No token received");
      return;
    }

    setAuthCookies({
      accessToken: token,
      refreshToken,
      role,
      user: { firstName, secondName, email },
    });

    // Verify cookie
    setTimeout(() => {
      const verifyToken = getAccessToken();
      
      if (verifyToken) {
        console.log(" Token verified, redirecting...");
        setAuthenticated(true);
        toast.success("Login successful");
        router.push("/dashboard");
      } else {
        console.error("Token not found after setting");
        toast.error("Authentication failed");
      }
    }, 300);
  };

  return (
    <div className="relative min-h-screen">
      <div className="swoosh-container absolute! inset-0 z-0"></div>

      <div className="relative z-10 h-[100vh] flex items-center justify-center p-4 md:p-6">
        <div className="flex flex-col md:flex-row w-full max-w-7xl shadow-2xl rounded-xl overflow-hidden bg-white/10 backdrop-blur-sm">
          
          {/* LEFT SIDE */}
          <div className="hidden md:flex md:w-1/2">
            <div className="flex flex-col w-full justify-center items-center bg-gradient-to-br from-[#750014] to-[#950018] p-10 text-white">
              <FaTruck className="text-6xl mb-4" />
              <h1 className="text-4xl font-bold mb-2">Transport Management</h1>
              <p className="mb-6">Efficient. Reliable. Connected.</p>
              <img src={Transport.src} className="w-80 h-80 rounded-full shadow-lg mb-4" alt="Transport" />
            </div>
          </div>

          {/* RIGHT SIDE */}
          <div className="w-full md:w-1/2 flex items-center justify-center bg-white">
            <div className="max-w-md w-full p-8">
              <div className="bg-white rounded-2xl p-8 shadow-xl">
                <h2 className="text-3xl font-bold text-center mb-2">Welcome Back</h2>

                {/* TOGGLE */}
                <div className="flex bg-gray-100 rounded-lg mb-6 p-1">
                  <button
                    type="button"
                    onClick={() => {
                      setLoginType("email");
                      setOtpSent(false);
                      formik.resetForm();
                    }}
                    className={`flex-1 py-2 rounded-md transition-all ${
                      loginType === "email" ? "bg-white shadow" : ""
                    }`}
                  >
                    Email
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setLoginType("phone");
                      setOtpSent(false);
                      formik.resetForm();
                    }}
                    className={`flex-1 py-2 rounded-md transition-all ${
                      loginType === "phone" ? "bg-white shadow" : ""
                    }`}
                  >
                    Phone
                  </button>
                </div>

                {/* FORM */}
                <form onSubmit={formik.handleSubmit} className="space-y-5">
                  {loginType === "email" && (
                    <>
                      <Input label="Email" name="email" type="email" value={formik.values.email} onChange={formik.handleChange} placeholder="Enter your email" />
                      <Input label="Password" name="password" type="password" value={formik.values.password} onChange={formik.handleChange} placeholder="Enter your password" />
                    </>
                  )}

                  {loginType === "phone" && (
                    <>
                      <Input label="Mobile" name="mobile" type="tel" value={formik.values.mobile} onChange={formik.handleChange} placeholder="Enter mobile number" />
                      {otpSent && (
                        <Input label="OTP" name="otp" type="text" value={formik.values.otp} onChange={formik.handleChange} placeholder="Enter OTP" />
                      )}
                    </>
                  )}

                  <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full bg-[#750014] text-white py-2 rounded-md hover:bg-[#950018] transition-colors disabled:opacity-50"
                  >
                    {isLoading ? "Please wait..." : loginType === "phone" && !otpSent ? "Send OTP" : "Sign In"}
                  </button>
                </form>

                {loginType === "email" && (
                  <div className="mt-4 text-center">
                    <button
                      type="button"
                      onClick={() => setIsForgotPasswordModalOpen(true)}
                      className="text-sm text-[#750014] hover:text-[#950018]"
                    >
                      Forgot password?
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>

      {isForgotPasswordModalOpen && (
        <Modal
          title={<h1 className="text-2xl">Forgot Password</h1>}
          open={isForgotPasswordModalOpen}
          footer={null}
          onCancel={() => {
            setIsForgotPasswordModalOpen(false);
            formik.resetForm();
          }}
          width="600px"
        >
          <ForgotPassword handleCancel={() => setIsForgotPasswordModalOpen(false)} />
        </Modal>
      )}
    </div>
  );
}