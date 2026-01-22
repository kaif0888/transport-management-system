import { smClient } from "@/lib";

export async function AddBranch(payload) {
    await smClient.post("/branch/createBranch", payload);
  }
  
  export async function UpdateBranch(id,payload) {
    await smClient.put(`/branch/updateByBranchId/${id}`, payload);
  }
  
  
  export async function getAllBranch(filterPayload = null) {
    const response = await smClient.post(`/branch/filteredBranchs`, filterPayload || { limit: 10, filters: [] });
 
    return response.data;
  }

  export async function getBranchById(id) {
    const response = await smClient.get(`/branch/getBranchById/${id}`);
    return response.data;
  }

  export async function deleteBranch(params) {
    await smClient.delete(`/branch/deleteBranch/${params}`);
   }