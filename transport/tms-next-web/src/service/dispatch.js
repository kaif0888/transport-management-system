import { smClient } from "@/lib";

export async function getAllDispatch(filterPayload = null) {
  const response = await smClient.post(
    `/dispatch/listOfDispatchByFilter`,
    filterPayload || { limit: 10, filters: [] }
  );

  // const data = sessionTokensSchema.parse(response.data.tokens);
  return response.data;
}
export async function AddDispatchData(payload) {
  await smClient.post("/dispatch/addDispatchDetail", payload);
}

export async function UpdateDispatch(id, payload) {
  await smClient.put(`/dispatch/updateDispatchById/${id}`, payload);
}

export async function pickupDispatch(id,payload) {
  await smClient.put(`/dispatch/dispatchUpdateByDriverPiksUp/${id}?location=${payload}`);
}
