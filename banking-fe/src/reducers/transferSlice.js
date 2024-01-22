import axios from "axios";
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

const initialState = {
  transfers: null,
  transfer: null,
  pageNumber: null,
  totalPages: null,
  totalElements: null,
  loading: false,
  creating: false,
  errors: "",
};

export const fetchTransfers = createAsyncThunk(
  "fetchTransfers",
  async ({
    pageNumber = 1,
    pageSize = 5,
    sortField = "time",
    sortDirection = "desc",
  }) => {
    console.log(
      `/user/transfer/search?pageNumber=${pageNumber}&pageSize=${pageSize}&sortField=${sortField}&sortDirection=${sortDirection}`
    );
    return axios
      .get(
        `/user/transfer/search?pageNumber=${pageNumber}&pageSize=${pageSize}&sortField=${sortField}&sortDirection=${sortDirection}`
      )
      .then((response) => response.data);
  }
);

export const createTransferOrder = createAsyncThunk(
  "createTransferOrder",
  async (transferOrderDto) => {
    return axios
      .post("/user/transfer/new", transferOrderDto)
      .then((response) => response.data);
  }
);

const transferSlice = createSlice({
  name: "transfer",
  initialState,
  reducers: {
    logout: () => {
      return initialState;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(fetchTransfers.pending, (state) => {
      state.loading = true;
    });
    builder.addCase(fetchTransfers.fulfilled, (state, action) => {
      state.loading = false;
      state.transfers = action.payload.data.content;
      state.transfer = null;
      state.pageNumber = action.payload.data.number;
      state.totalPages = action.payload.data.totalPages;
      state.totalElements = action.payload.data.totalElements;
      state.errors = "";
    });
    builder.addCase(fetchTransfers.rejected, (state, action) => {
      state.loading = false;
      state.transfers = null;
      state.transfer = null;
      state.errors = action.error.message;
    });
    builder.addCase(createTransferOrder.pending, (state) => {
      state.loading = true;
    });
    builder.addCase(createTransferOrder.fulfilled, (state, action) => {
      state.loading = false;
      state.transfer = action.payload.data;
      state.errors = "";
    });
    builder.addCase(createTransferOrder.rejected, (state, action) => {
      state.loading = false;
      state.transfer = null;
      state.errors = action.error.message;
    });
  },
});

export default transferSlice.reducer;
