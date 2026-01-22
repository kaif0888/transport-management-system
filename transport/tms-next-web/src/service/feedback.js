import { smClient } from "@/lib";

export async function submitFeedback(payload) {
   await smClient.post(`/feedback/addFeedback`,payload)};

   export async function getAllFeedback(filterPayload) {
    const response = await smClient.post(`/feedback/getListOfFilterFeedback`, filterPayload || { limit: 10, filters: [] }); 
    return response.data;
  } 
   export async function deleteFeedback(feedbackId) { 
      await smClient.delete(`/feedback/deleteFeedback/${feedbackId}`);
   }