"use client";

import { MessageOutlined } from "@ant-design/icons";
import { useRouter } from "next/navigation";

export default function FloatingFeedbackButton() {
  const router = useRouter();

  return (
    <button
      onClick={() => router.push("/help-feedback")}
      className="
        fixed
        bottom-6
        right-6
        z-50
        flex
        items-center
        gap-2
        bg-blue-600
        hover:bg-blue-700
        text-white
        px-5
        py-3
        rounded-xl
        shadow-lg
        transition-all
      "
    >
      <MessageOutlined className="text-lg" />
      <span className="font-medium">Feedback us</span>
    </button>
  );
}