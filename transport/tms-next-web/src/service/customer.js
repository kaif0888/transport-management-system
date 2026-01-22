import { smClient } from "@/lib";

export async function AddCustomer(payload) {
    await smClient.post("/customer/createCustomer", payload);
  }

  export async function UpdateCustomer(id,payload) {
    await smClient.put(`/customer/updateByCustomerId/${id}`, payload);
  }
  
  
  export async function getAllCustomer(filterPayload = null) {
    const response = await smClient.post(`/customer/filterCustomers`, filterPayload || { limit: 10, filters: [] });
 
    return response.data;
  }

  export async function getCustomerById(id) {
    const response = await smClient.get(`/customer/getCustomerById/${id}`);
    return response.data;
  }

  export async function deleteCustomer(params) {
    await smClient.delete(`/customer/deleteCustomer/${params}`);
   }