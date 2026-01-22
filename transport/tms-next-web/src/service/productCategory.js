import { smClient } from "@/lib";

export async function AddProductCategory(payload) {
  await smClient.post("/category/createCategory", payload);
}

export async function UpdateProductCategory(id, payload) {
  await smClient.put(`/category/updateByProductId/${id}`, payload);
}

export async function getAllProductCategory(filterPayload = null) {
  const response = await smClient.post(
    `/category/filteredProductCategorys`,
    filterPayload || { limit: 10, filters: [] }
  );

  return response.data;
}

export async function getProductCategoryById(id) {
  const response = await smClient.get(`/category/updateByCategoryId/${id}`);
  return response.data;
}

export async function deleteProductCategory(params) {
  await smClient.delete(`/category/deleteCategory/${params}`);
}
