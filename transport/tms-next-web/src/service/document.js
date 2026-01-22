
import { smClient } from "@/lib";

export async function uploadDocument(payload) {
     const res = await smClient.post("/documents/upload",payload);
     return res.data

  }



