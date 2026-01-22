import { smClient } from "@/lib";

export async function AddVehicle(payload) {
  await smClient.post("/vehicles/createVehicle", payload);
}

export async function UpdateVehicle(id, payload) {
  await smClient.put(`/vehicles/updateVehicle/${id}`, payload);
}

export const UpdateVehcile = UpdateVehicle;

export async function getAllVehicle(filterPayload = null) {
  const response = await smClient.post(
    `/vehicles/getListOfVehiclesByFilterCriteria`, 
    filterPayload || { limit: 10, filters: [] }
  );
  return response.data;
}

export async function getAllVehicleModels() {
  const response = await smClient.get(`/vehicles/getAllVehicleModels`);
  return response.data;
}

export async function getAllVehicleCompany() {
  const response = await smClient.get(`/vehicles/getAllVehicleCompany`);
  return response.data;
}

export async function getAvailableVehicle() {
  const response = await smClient.get("/vehicles/available");
  return response.data;
}

export const getAvalaibleVehicle = getAvailableVehicle;

export async function getVehicleExpiries() {
  const response = await smClient.get("/vehicles/expiries");
  console.log("Expiries data details in vehicles.js file :- ",response.data)
  return response.data;
}

export async function getUnRentedVehicle() {
  const response = await smClient.get("/vehicles/unrented");
  return response.data;
}

// SIMPLIFIED: Single endpoint, no additional document fetch
export async function getVehicleById(id) {
  const response = await smClient.get(`/vehicles/getVehicleById/${id}`);
  return response.data;
}

// export async function getVehicleByNumber(vehicleNumber) {
//   const filterPayload = {
//     offset: 0,
//     limit: 1,
//     filters: [
//       {
//         attribute: "vehicleNumber",
//         operation: "EQUALS",
//         value: vehicleNumber,
//       },
//     ],
//   };

//   const vehicles = await getAllVehicle(filterPayload);
//   if (vehicles?.length > 0) {
//     return vehicles[0];
//   } else {
//     throw new Error("Vehicle not found");
//   }
// }
export async function getVehicleByNumber(vehicleNumber) {
  const cleaned = vehicleNumber.trim().toUpperCase().replace(/\s+/g, "");

  // 1st try with registrationNumber
  try {
    const payload1 = {
      offset: 0,
      limit: 1,
      filters: [
        {
          attribute: "registrationNumber",
          operation: "EQUALS",
          value: cleaned,
        },
      ],
    };

    const vehicles1 = await getAllVehicle(payload1);
    if (vehicles1?.length > 0) return vehicles1[0];
  } catch (e) {}

  // 2nd try with vehiclNumber
  const payload2 = {
    offset: 0,
    limit: 1,
    filters: [
      {
        attribute: "vehiclNumber",
        operation: "EQUALS",
        value: cleaned,
      },
    ],
  };

  const vehicles2 = await getAllVehicle(payload2);
  if (vehicles2?.length > 0) return vehicles2[0];

  throw new Error("Vehicle not found");
}


// Vehicle Type APIs
export async function AddVehicleType(payload) {
  await smClient.post("/vehicleType/createVehicleType", payload);
}

export async function UpdateVehicleType(id, payload) {
  await smClient.put(`/vehicleType/updateVehicleType/${id}`, payload);
}

export async function getAllVehicleType() {
  const res = await smClient.get("/vehicleType/listVehicleType");
  return res.data;
}

export async function deleteVehicleType(params) {
  await smClient.delete(`/vehicleType/deleteVehicleType/${params}`);
}

export async function uploadVehicleFile(formData) {
  const response = await smClient.post("/vehicles/upload", formData);
  return response.data;
}

export async function createVehicleWithDocuments(payload) {
  const response = await smClient.post("/vehicles/createVehicleWithDocuments", payload);
  return response.data;
}