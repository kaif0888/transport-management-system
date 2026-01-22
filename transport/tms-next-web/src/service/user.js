import { smClient } from "@/lib";

  
  export async function UpdateUser(id,payload) {
    await smClient.post(`/user/update/${id}`, payload);
  }
  
  
  export async function getAllUser(filterPayload = null) {
    const response = await smClient.post(`/user/filterUsers`, filterPayload || { limit: 10, filters: [] });
 
    return response.data;
  }

  export async function getUserById(id) {
    const response = await smClient.get(`/user/getUserById/${id}`);
    return response.data;
  }

  export async function deleteUser(params) {
    await smClient.delete(`/user/deleteUser/${params}`);
   }