import axios from "axios";
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

const initialState = {
  loading: false,
  successMessage: null,
  errors: "",
};

export const sendResetPasswordToken = createAsyncThunk(
  "sendResetPasswordToken",
  async (username) => {
    return axios
      .get("/user/reset-password?u=" + username)
      .then((response) => response.data);
  }
);

export const resetPassword = createAsyncThunk(
  "resetPassword",
  async ({ token, password, passwordRetyped }) => {
    return axios
      .post("/user/reset-password?t=" + token, {
        password: password,
        passwordRetyped: passwordRetyped,
      })
      .then((response) => response.data);
  }
);

const passwordSlice = createSlice({
  name: "password",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(sendResetPasswordToken.pending, (state) => {
      state.loading = true;
    });
    builder.addCase(sendResetPasswordToken.fulfilled, (state) => {
      state.loading = false;
      state.successMessage =
        "If provided username is correct we will send you mail with link to reset your password. Please check spam folder.";
      state.errors = "";
    });
    builder.addCase(sendResetPasswordToken.rejected, (state, action) => {
      state.loading = false;
      state.success = false;

      state.errors = action.error.message;
    });
    builder.addCase(resetPassword.pending, (state) => {
      state.loading = true;
    });
    builder.addCase(resetPassword.fulfilled, (state) => {
      state.loading = false;
      state.successMessage =
        "Your password has been changed. You can now log in with new password.";
      state.errors = "";
    });
    builder.addCase(resetPassword.rejected, (state, action) => {
      state.loading = false;
      state.success = false;
      state.errors = action.error.message;
    });
  },
});

export default passwordSlice.reducer;
