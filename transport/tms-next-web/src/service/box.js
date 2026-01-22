// service/box.js
// import { smClient } from "./api"; // or wherever your smClient is exported from

/**
 * Get all boxes with optional filters
 * @param {Object} payload - Filter payload { limit: 0, filters: [] }
 * @returns {Promise<Array>} List of boxes
 */

import axios from "axios";

export const smClient = axios.create({
  baseURL: "http://localhost:1001", // BACKEND PORT
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 30000,
});

export const getAllBox = async (payload = { limit: 0, filters: [] }) => {
  try {
    console.log(' getAllBox - Request payload:', payload);
    const response = await smClient.post("/api/boxes/getAllBoxes", payload);
    console.log(' getAllBox - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' getAllBox - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to fetch boxes";
  }
};

/**
 * Get box by ID
 * @param {string} boxId - Box ID
 * @returns {Promise<Object>} Box details
 */
export const getBoxById = async (boxId) => {
  try {
    console.log(' getBoxById - Fetching box:', boxId);
    const response = await smClient.get(`/api/boxes/${boxId}`);
    console.log(' getBoxById - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' getBoxById - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to fetch box details";
  }
};

/**
 * Create a new box
 * @param {Object} payload - Box data
 * @returns {Promise<Object>} Created box
 */
export const AddBox = async (payload) => {
  try {
    console.log(' AddBox - Creating box:', payload);
    const response = await smClient.post("/api/boxes/createBox", payload);
    console.log(' AddBox - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' AddBox - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to create box";
  }
};

/**
 * Update existing box
 * @param {string} boxId - Box ID
 * @param {Object} payload - Updated box data
 * @returns {Promise<Object>} Updated box
 */
export const UpdateBox = async (boxId, payload) => {
  try {
    console.log(' UpdateBox - Updating box:', boxId, payload);
    const response = await smClient.put(`/api/boxes/${boxId}`, payload);
    console.log(' UpdateBox - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' UpdateBox - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to update box";
  }
};

/**
 * Delete box
 * @param {string} boxId - Box ID
 * @returns {Promise<string>} Success message
 */
export const deleteBox = async (boxId) => {
  try {
    console.log(' deleteBox - Deleting box:', boxId);
    const response = await smClient.delete(`/api/boxes/${boxId}`);
    console.log(' deleteBox - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' deleteBox - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to delete box";
  }
};

/**
 * Add product to box
 * @param {string} boxId - Box ID
 * @param {string} productId - Product ID
 * @param {number} quantity - Quantity to add
 * @returns {Promise<Object>} Updated box
 */
export const addProductToBox = async (boxId, productId, quantity) => {
  try {
    console.log(' addProductToBox - Request:', { boxId, productId, quantity });
    const response = await smClient.post(
      `/api/boxes/${boxId}/products`,
      null,
      {
        params: { productId, quantity }
      }
    );
    console.log(' addProductToBox - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' addProductToBox - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to add product to box";
  }
};

/**
 * Remove product from box
 * @param {string} boxId - Box ID
 * @param {string} productId - Product ID
 * @returns {Promise<Object>} Updated box
 */
export const removeProductFromBox = async (boxId, productId) => {
  try {
    console.log(' removeProductFromBox - Request:', { boxId, productId });
    const response = await smClient.delete(`/api/boxes/${boxId}/products/${productId}`);
    console.log(' removeProductFromBox - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' removeProductFromBox - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to remove product from box";
  }
};

/**
 * Add box to order
 * @param {string} orderId - Order ID
 * @param {string} boxId - Box ID
 * @returns {Promise<string>} Success message
 */
export const addToOrderBox = async (orderId, boxId) => {
  try {
    console.log(' addBoxToOrder - Request:', { orderId, boxId });
    const response = await smClient.post(`/api/boxes/orders/${orderId}/boxes/${boxId}`);
    console.log(' addBoxToOrder - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' addBoxToOrder - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to add box to order";
  }
};

export const addBoxToOrder = async (orderId, boxId) => {
  try {
    console.log('📦 addBoxToOrder - Request:', { orderId, boxId });
    const response = await smClient.post(`/order/addBoxToOrder/${orderId}/${boxId}`);
    console.log('✅ addBoxToOrder - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ addBoxToOrder - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to add box to order";
  }
};

/**
 * Get boxes for order
 * @param {string} orderId - Order ID
 * @returns {Promise<Array>} List of boxes in order
 */
// export const getBoxesForOrder = async (orderId) => {
//   try {
//     console.log(' getBoxesForOrder - Request:', orderId);
//     const response = await smClient.get(`/api/boxes/orders/${orderId}/boxes`);
//     console.log(' getBoxesForOrder - Success:', response.data);
//     return response.data;
//   } catch (error) {
//     console.error(' getBoxesForOrder - Error:', error.response || error);
//     throw error.response?.data?.message || error.message || "Failed to fetch boxes for order";
//   }
// };

export const getBoxesForOrder = async (orderId) => {
  try {
    console.log('📦 getBoxesForOrder - Request:', orderId);
    const response = await smClient.get(`/order/getBoxesForOrder/${orderId}`);
    console.log('✅ getBoxesForOrder - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ getBoxesForOrder - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to fetch boxes for order";
  }
};


/**
 * Get all HSN codes
 * @returns {Promise<Array>} List of HSN codes
 */
export const getAllHSNCodes = async () => {
  try {
    console.log(' getAllHSNCodes - Fetching all HSN codes');
    const response = await smClient.get("/api/boxes/hsn-codes");
    console.log(' getAllHSNCodes - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' getAllHSNCodes - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to fetch HSN codes";
  }
};

/**
 * Search HSN codes by query
 * @param {string} query - Search query
 * @returns {Promise<Array>} List of matching HSN codes
 */
export const searchHSNCodes = async (query) => {
  try {
    console.log(' searchHSNCodes - Query:', query);
    const response = await smClient.get("/api/boxes/hsn-codes/search", {
      params: { query }
    });
    console.log(' searchHSNCodes - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error(' searchHSNCodes - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to search HSN codes";
  }
};

export const removeBoxFromOrder = async (orderId, boxId) => {
  try {
    console.log('📦 removeBoxFromOrder - Request:', { orderId, boxId });
    const response = await smClient.delete(`/order/removeBoxFromOrder/${orderId}/${boxId}`);
    console.log('✅ removeBoxFromOrder - Success:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ removeBoxFromOrder - Error:', error.response || error);
    throw error.response?.data?.message || error.message || "Failed to remove box from order";
  }
};