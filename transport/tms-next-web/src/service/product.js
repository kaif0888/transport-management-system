import { smClient } from "@/lib";


export async function AddProduct(payload) {
  console.log("payload line 5", payload);
  await smClient.post("/product/createProduct", payload);
}

export async function UpdateProduct(id, payload) {
  await smClient.put(`/product/updateByProductId/${id}`, payload);
}

export async function getAllProduct(filterPayload = null) {
  const response = await smClient.post(
    `/product/filterProducts`,
    filterPayload || { limit: 10, filters: [] }
  );
  
  console.log("Filtered Products from Backend:", response.data);
  console.log("Filter Payload Sent:", filterPayload || { limit: 10, filters: [] });

  return response.data;
}

export async function getProductById(id) {
  const response = await smClient.get(`/product/getProductById/${id}`);
  return response.data;
}

export async function deleteProduct(params) {
  await smClient.delete(`/product/deleteProduct/${params}`);
}

export async function getAllHsnCodes() {
  const response = await smClient.get("/api/boxes/hsn-codes");
  return response.data;
}
