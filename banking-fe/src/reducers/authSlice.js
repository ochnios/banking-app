import axios from "axios";
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

const initialState = {
  authenticated: false,
  username: null,
  positions: null,
  loading: false,
  errors: "",
};

export const fetchCurrentPositions = createAsyncThunk(
  "fetchCurrentPositions",
  async (username) => {
    return axios
      .get("/auth/current-positions?u=" + username)
      .then((response) => response.data);
  }
);

export const authenticate = createAsyncThunk(
  "authenticate",
  async (loginDto) => {
    return axios
      .post("/auth/login", loginDto)
      .then((response) => response.data);
  }
);

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    logout: () => {
      return initialState;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(fetchCurrentPositions.pending, (state) => {
      state.loading = true;
    });
    builder.addCase(fetchCurrentPositions.fulfilled, (state, action) => {
      state.loading = false;
      state.positions = action.payload.data.positions;
      state.username = action.meta.arg;
      state.errors = "";
    });
    builder.addCase(fetchCurrentPositions.rejected, (state, action) => {
      state.loading = false;
      state.positions = [];
      state.errors = action.error.message;
    });
    builder.addCase(authenticate.pending, (state) => {
      state.loading = true;
    });
    builder.addCase(authenticate.fulfilled, (state) => {
      state.loading = false;
      state.authenticated = true;
      state.positions = [];
      state.errors = "";
    });
    builder.addCase(authenticate.rejected, (state, action) => {
      state.loading = false;
      state.errors = action.error.message;
    });
  },
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;
