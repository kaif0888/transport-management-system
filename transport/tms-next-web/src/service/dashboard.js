
import { smClient } from "@/lib";

export async function getDashboard() {
    const response = await smClient.get("/api/dashboard");
    return response.data;
  }