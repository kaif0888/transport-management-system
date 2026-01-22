import { smClient } from "@/lib";

export async function AddOrderProducts(payload) {
  await smClient.post("/orderProducts/createOrderProducts", payload);
}

export async function UpdateOrderProducts(id, payload) {
  await smClient.put(`/orderProducts/updateOrderProducts/${id}`, payload);
}

export async function getAllOrderProducts(filterPayload = null) {
  const response = await smClient.post(
    `/orderProducts/listOfOrderProductsByFilter`,
    filterPayload || { limit: 10, filters: [] }
  );
  return response.data;
}
export async function deleteOrderProducts(params) {
  await smClient.delete(`/orderProducts/deleteOrderProductsById/${params}`);
}


