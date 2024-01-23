import axios from "axios";
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

const initialState = {
  accountNumber: null,
  balance: null,
  loading: false,
  errors: "",
};

export const fetchAccount = createAsyncThunk("fetchAccount", async () => {
  return axios.get("/user/account").then((response) => response.data);
});

const accountSlice = createSlice({
  name: "account",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(fetchAccount.pending, (state) => {
      state.loading = true;
    });
    builder.addCase(fetchAccount.fulfilled, (state, action) => {
      state.loading = false;
      state.balance = action.payload.data.balance;
      state.accountNumber = action.payload.data.accountNumber;
      state.errors = "";
    });
    builder.addCase(fetchAccount.rejected, (state, action) => {
      state.loading = false;
      state.balance = null;
      state.accountNumber = null;
      state.errors = action.error.message;
    });
  },
});

export default accountSlice.reducer;
