import { smClient } from "@/lib";

export async function AddOrder(payload) {
  await smClient.post("/order/createOrder", payload);
}

export async function UpdateOrder(id, payload) {
  await smClient.put(`/order/updateByOrderId/${id}`, payload);
}

export async function UpdateOrderStatus(id) {
  await smClient.put(`/order/confirmOrderStatus/${id}`);
}

export async function getAllOrder(filterPayload = null) {
  const response = await smClient.post(
    `/order/listOfOrderByFilter`,
    filterPayload || { limit: 10, filters: [] }
  );

  return response.data;
}

export async function getOrderById(id) {
  const response = await smClient.get(`/order/getOrderById/${id}`);
  return response.data;
}

export async function deleteOrder(params) {
  await smClient.delete(`/order/deleteOrder/${params}`);
}
