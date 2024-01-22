import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./reducers/authSlice";
import accountReducer from "./reducers/accountSlice";
import transferReducer from "./reducers/transferSlice";

const store = configureStore({
  reducer: {
    auth: authReducer,
    account: accountReducer,
    transfer: transferReducer,
  },
});

export default store;
