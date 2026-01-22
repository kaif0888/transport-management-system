
import { smClient } from "@/lib";

export async function AddDriver(payload) {
  await smClient.post("/drivers/addDriverDetails", payload);
}

export async function UpdateDriver(id,payload) {
  await smClient.put(`/drivers/Updateby/${id}`, payload);
}

export async function deleDriver(params) {
 await smClient.delete(`/drivers/deleteDriverBy/${params}`);
}

export async function getAllDriver(filterPayload = null) {
  const response = await smClient.post(`/drivers/getlistOfDriversByFilterCriteria`, filterPayload || { limit: 10, filters: [] });
  return response.data;
}

export async function getAvilableDriver() {
  const response = await smClient.get("/drivers/availableDrivers");
  return response.data;
}

export async function getDriverById(id) {
  const response = await smClient.get(`/drivers/getDriverById/${id}`);
  return response.data;
}

export async function assignVechileDriver(driverId,vechicleId) {
  const response = await smClient.put(`/drivers/assignVechileDriver/${driverId}/${vechicleId}`);
  return response.data;
}

export async function unAssignVechileDriver(driverId,vechicleId) {
  const response = await smClient.put(`/drivers/unAssignVechileDriver/${driverId}/${vechicleId}`);
  return response.data;
}

export async function getDriverLicenseExpiries() {
  try {
    const response = await smClient.get("/drivers/driverLicenseExpiries");
    console.log("Driver license expiries full response:", response);
    console.log("Driver license expiries data:", response.data);
    console.log("Response type:", typeof response.data);
    console.log("Is array:", Array.isArray(response.data));
    return response.data;
  } catch (error) {
    console.error("Error fetching driver license expiries:", error);
    throw error;
  }
}
