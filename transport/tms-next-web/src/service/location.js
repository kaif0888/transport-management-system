import { smClient } from "@/lib";

export async function AddLocation(payload) {
  await smClient.post("/location/createLocation", payload);
}

export async function UpdateLocation(id, payload) {
  await smClient.put(`/location/updateByLocationId/${id}`, payload);
}

export async function getAllLocation(filterPayload = null) {
  const response = await smClient.post(
    `/location/filteredLocations`,
    filterPayload || { limit: 10, filters: [] }
  );

  return response.data;
}

export async function getLocationById(id) {
  const response = await smClient.get(`/location/getLocationById/${id}`);
  return response.data;
}

export async function getLocationByPincode(id) {
  const response = await smClient.get(`/location/getLocationsByPincode/${id}`);
  return response.data;
}

export async function getLocationsAddressByPincode(id) {
  const response = await smClient.get(`/location/getLocationsAddressByPincode/${id}`);
  return response.data;
}

export async function deleteLocation(params) {
  await smClient.delete(`/location/deleteLocation/${params}`);
}
