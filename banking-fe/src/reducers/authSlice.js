import axios from "axios";
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

const initialState = {
  authenticated: false,
  username: null,
  name: null,
  surname: null,
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
      state.positions = null;
      state.errors = action.error.message;
    });
    builder.addCase(authenticate.pending, (state) => {
      state.loading = true;
    });
    builder.addCase(authenticate.fulfilled, (state, action) => {
      state.loading = false;
      state.authenticated = true;
      state.name = action.payload.data.name;
      state.surname = action.payload.data.surname;
      state.positions = null;
      state.username = null;
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
