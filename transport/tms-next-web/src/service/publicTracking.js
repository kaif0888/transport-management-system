import axios from "axios";

export const getPublicOrderSummary = async (orderId) => {
  const res = await axios.get(
    `http://localhost:1001/api/public/tracking/order/${orderId}`
  );
  return res.data;
};

export const getPublicOrderTimeline = async (orderId) => {
  const res = await axios.get(
    `http://localhost:1001/api/public/tracking/order/${orderId}/timeline`
  );
  return res.data;
};

//http://localhost:1001/api/public/tracking/order/ORD-20260105-003
