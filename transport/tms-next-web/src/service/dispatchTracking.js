import { smClient } from "@/lib";

export async function AddDispatchTracking(payload) {
  try {
    const response = await smClient.post("/dispatchTracking/createDispatchTracking", payload);
    return response.data;
  } catch (error) {
    console.error("Add dispatch tracking error:", error);
    throw error;
  }
}

export async function UpdateDispatchTracking(id, payload) {
  try {
    const response = await smClient.put(
      `/dispatchTracking/updateDispatchTrackingById/${id}`,
      payload
    );
    return response.data;
  } catch (error) {
    console.error("Update dispatch tracking error:", error);
    throw error;
  }
}

export async function UpdateDispatchTrackingStatus(id) {
  try {
    const response = await smClient.put(
      `/dispatchTracking/updateTrackingStatusById/${id}`
    );
    console.log("Status update response:", response);
    return response.data;
  } catch (error) {
    console.error("Update status error:", error);
    throw error;
  }
}

export async function deleteDispatchTracking(params) {
  try {
    const response = await smClient.delete(
      `/dispatchTracking/deleteDispatchTrackingById/${params}`
    );
    return response.data;
  } catch (error) {
    console.error("Delete dispatch tracking error:", error);
    throw error;
  }
}

export async function getAllDispatchTracking(filterPayload = null) {
  try {
    const response = await smClient.post(
      `/dispatchTracking/listOfDispatchTrackingByFilter`,
      filterPayload || { limit: 0, filters: [] }
    );
    console.log("Get all dispatch tracking response:", response);
    return response.data;
  } catch (error) {
    console.error("Get all dispatch tracking error:", error);
    throw error;
  }
}

export async function getDispatchTrackingById(id) {
  try {
    const response = await smClient.get(
      `/dispatchTracking/getDispatchTrackingById/${id}`
    );
    return response.data;
  } catch (error) {
    console.error("Get dispatch tracking by id error:", error);
    throw error;
  }
}

export async function getorderstatus(id) {
  try {
    const response = await smClient.get(
      `/dispatchTracking/orderStatus/${id}`
    );
    return response.data;
  } catch (error) {
    console.error("Get order status error:", error);
    throw error;
  }
}

// New function to download invoice
export async function downloadInvoice(invoiceId) {
  try {
    const response = await smClient.get(
      `/invoice/download/${invoiceId}`,
      { responseType: 'blob' }
    );
    return response.data;
  } catch (error) {
    console.error("Download invoice error:", error);
    throw error;
  }
}

// New function to get invoice details
export async function getInvoiceDetails(invoiceId) {
  try {
    const response = await smClient.get(`/invoice/${invoiceId}`);
    return response.data;
  } catch (error) {
    console.error("Get invoice details error:", error);
    throw error;
  }
}