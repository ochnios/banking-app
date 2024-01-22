import Navigation from "./Navigation";
import { Navigate, Outlet, useOutlet } from "react-router-dom";

export default function Layout() {
  let outlet = useOutlet();
  return (
    <div className="container shadow main-container vh-100 p-0">
      <Navigation />
      {outlet === null && <Navigate to="/login-first-step" />}
      <Outlet />
    </div>
  );
}
