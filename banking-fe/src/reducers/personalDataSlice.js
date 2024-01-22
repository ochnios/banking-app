import axios from "axios";
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

const initialState = {
  address: null,
  cardNumber: null,
  identification: null,
  loading: false,
  errors: "",
};

export const fetchPersonalData = createAsyncThunk(
  "fetchPersonalData",
  async () => {
    return axios.get("/user/personal").then((response) => response.data);
  }
);

const personalDataSlice = createSlice({
  name: "personal-data",
  initialState,
  reducers: {
    clearPersonalData: () => {
      return initialState;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(fetchPersonalData.pending, (state) => {
      state.loading = true;
    });
    builder.addCase(fetchPersonalData.fulfilled, (state, action) => {
      state.loading = false;
      state.address = action.payload.data.address;
      state.cardNumber = action.payload.data.cardNumber;
      state.identification = action.payload.data.identification;
      state.errors = "";
    });
    builder.addCase(fetchPersonalData.rejected, (state, action) => {
      state.loading = false;
      state.balance = null;
      state.accountNumber = null;
      state.errors = action.error.message;
    });
  },
});

export const { clearPersonalData } = personalDataSlice.actions;
export default personalDataSlice.reducer;
