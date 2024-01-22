import axios from "axios";
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

const initialState = {
  transfers: null,
  pageNumber: null,
  totalPages: null,
  totalElements: null,
  loading: false,
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
      state.pageNumber = action.payload.data.number;
      state.totalPages = action.payload.data.totalPages;
      state.totalElements = action.payload.data.totalElements;
      state.errors = "";
    });
    builder.addCase(fetchTransfers.rejected, (state, action) => {
      state.loading = false;
      state.transfers = null;
      state.errors = action.error.message;
    });
  },
});

export default transferSlice.reducer;
