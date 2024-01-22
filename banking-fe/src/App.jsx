import { Provider } from "react-redux";
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import axios from "axios";
import config from "./config.js";
import store from "./store";
import Layout from "./views/layout/Layout";
import AuthorizeView from "./views/layout/AuthorizeView";
import LoginFirstStep from "./views/pages/LoginFirstStep.jsx";
import LoginSecondStep from "./views/pages/LoginSecondStep.jsx";
import AccountPage from "./views/pages/AccountPage";
import { logout } from "./reducers/authSlice.js";
import PublicView from "./views/layout/PublicView.jsx";

axios.defaults.baseURL = config.baseUrl;
axios.defaults.withCredentials = true;
axios.defaults.headers.common["Content-Type"] = "application/json";
axios.defaults.headers.common["Accept"] = "application/json";
axios.interceptors.response.use(
  function (response) {
    return response;
  },
  function (error) {
    console.error(error.response);
    if (error.response?.status === 401 && !error.response?.data?.message) {
      store.dispatch(logout());
      return Promise.reject(config.unauthorizedMessage);
    } else if (error.response?.data?.message) {
      return Promise.reject(error.response.data.message);
    } else return Promise.reject(config.genericErrorMessage);
  }
);

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        path: "account",
        element: (
          <AuthorizeView>
            <AccountPage />
          </AuthorizeView>
        ),
      },
      {
        path: "login-first-step",
        element: (
          <PublicView>
            <LoginFirstStep />
          </PublicView>
        ),
      },
      {
        path: "login-second-step",
        element: (
          <PublicView>
            <LoginSecondStep />
          </PublicView>
        ),
      },
    ],
  },
]);

export default function App() {
  return (
    <Provider store={store}>
      <RouterProvider router={router} />
    </Provider>
  );
}
