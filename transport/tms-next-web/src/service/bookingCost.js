import { smClient } from "@/lib";

export async function AddBookingCost(payload) {
    await smClient.post("/bookingCost/createBookingCost", payload);
  }
  
  export async function UpdateBookingCost(id,payload) {
    await smClient.put(`/bookingCost/updateByBookingCostId/${id}`, payload);
  }
  
  
  export async function getAllBookingCost(filterPayload = null) {
    const response = await smClient.post(`/bookingCost/listBookingCost`, filterPayload || { limit: 10, filters: [] });
 
    return response.data;
  }

  export async function getBookingCostById(id) {
    const response = await smClient.get(`/bookingCost/getBookingCostById/${id}`);
    return response.data;
  }

  export async function deleteBookingCost(params) {
    await smClient.delete(`/bookingCost/deleteBookingCost/${params}`);
   }