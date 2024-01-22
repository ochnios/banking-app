import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./reducers/authSlice";
import accountReducer from "./reducers/accountSlice";
import personalDataReducer from "./reducers/personalDataSlice";

import transferReducer from "./reducers/transferSlice";

const persistedState = JSON.parse(localStorage.getItem("reduxState") || "{}");

const store = configureStore({
  reducer: {
    auth: authReducer,
    account: accountReducer,
    personalData: personalDataReducer,
    transfer: transferReducer,
  },
  preloadedState: persistedState,
});

store.subscribe(() => {
  const { auth } = store.getState();
  localStorage.setItem("reduxState", JSON.stringify({ auth }));
});

export default store;
