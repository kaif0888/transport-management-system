import { smClient } from "@/lib";


export async function AddRental(payload) {
    await smClient.post("/rental/AddRentalDetails", payload);
  }
  
  export async function UpdateRental(id,payload) {
    await smClient.put(`/rental/updateRentalDetail/${id}`, payload);
  }
export async function getAllRentalVehicle() {
    const response = await smClient.get("/rental/RentalDetailList");
    
    // const data = sessionTokensSchema.parse(response.data.tokens);
    return response.data;
  }

  export async function getRentalById(id) {
    const response = await smClient.get(`/rental/getRentalById/${id}`);
    // const data = sessionTokensSchema.parse(response.data.tokens);
  
    return response.data;
  }

  export async function deleteRental(params) {
    await smClient.delete(`/rental/DeleteRentalDetails/${params}`);
   }