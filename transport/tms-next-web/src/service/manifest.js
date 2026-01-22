
import { smClient } from "@/lib";

export async function AddManifest(payload) {
  await smClient.post("/manifest/createManifest", payload);
}

export async function UpdateManifest(id,payload) {
  await smClient.put(`/manifest/updateManifestById/${id}`, payload);
}

export async function deleteManifest(params) {
 await smClient.delete(`/manifest/deleteManifestById/${params}`);
}

export async function getAllManifest(filterPayload=null) {
  const response = await smClient.post(`/manifest/filteredManifests`, filterPayload || { limit: 10, filters: [] });

  return response.data;
}

export async function getManifestById(id) {
  const response = await smClient.get(`/manifest/getManifestById/${id}`);
  return response.data;
}


