import { smClient } from "@/lib";

export async function CreateExpense(payload) {
  await smClient.post("/expense/addExpense", payload);
}

export async function getExpenseById(id) {
  const response = await smClient.get(`/expense/getExpenseById/${id}`);
  return response.data;
}

export async function UpdateExpense(id, payload) {
  await smClient.put(`expense/updateExpenseBy/${id}`, payload);
}

export async function getAllExpense(filterPayload = null) {
  const response = await smClient.post(
    `/expense/getExpensebyfilterCriteria`,
    filterPayload || { limit: 10, filters: [] }
  );
  // const data = sessionTokensSchema.parse(response.data.tokens);
  return response.data;
}

export async function deleteExpense(params) {
  await smClient.delete(`/expense/deleteExpenseBy/${params}`);
 }


 //Expence type  

export async function AddExpenseType(payload) {
  await smClient.post("/expenseType/createExpenseType", payload);
}

export async function getExpenseType() {
 const res =  await smClient.get("/expenseType/listAllExpenseType");
 return res.data;
}
export async function deleteExpenseType(params) {
  await smClient.delete(`/expenseType/deleteById/${params}`);
}

export async function getAllExpenseType(filterPayload = null) {
  const response = await smClient.post(
    `/expenseType/getExpenseTypebyfilterCriteria`,
    filterPayload || { limit: 10, filters: [] }
  );
  // const data = sessionTokensSchema.parse(response.data.tokens);
  return response.data;
}