import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./reducers/authSlice";
import accountReducer from "./reducers/accountSlice";

const store = configureStore({
  reducer: {
    auth: authReducer,
    account: accountReducer,
  },
});

export default store;
