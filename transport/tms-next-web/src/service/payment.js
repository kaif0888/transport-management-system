import { smClient } from "@/lib";

export async function AddPayment(payload) {
  await smClient.post("/payment/createbyOrderandCustomerPayment", payload);
}


export async function getAllPayment(filterPayload = null) {
  const response = await smClient.post(
    `/payment/listOfPaymentByFilter`,
    filterPayload || { limit: 10, filters: [] }
  );

  return response.data;
}

