import { useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchCurrentPositions } from "../../reducers/authSlice";
import { Link, Navigate } from "react-router-dom";

export default function LoginFirstStep() {
  const dispatch = useDispatch();
  const auth = useSelector((state) => state.auth);
  const usernameRef = useRef();

  const handleSubmit = (e) => {
    e.preventDefault();
    dispatch(fetchCurrentPositions(usernameRef.current.value));
  };

  return (
    <div className="container-fluid py-5">
      <div className="row">
        <div className="col-sm-3"></div>
        <div className="col-sm-6">
          <form className="border rounded p-2" onSubmit={handleSubmit}>
            {auth.errors ? (
              <div className="alert alert-danger">{auth.errors}</div>
            ) : null}
            <div className="mb-3">
              <label htmlFor="username" className="form-label">
                Username
              </label>
              <input
                type="text"
                name="username"
                id="username"
                ref={usernameRef}
                className="form-control"
              />
            </div>
            <div className="d-flex justify-content-center">
              <button type="submit" className="btn btn-dark">
                Next
              </button>
            </div>
            <Link to="/reset-password" className="mt-2">
              Reset password
            </Link>
          </form>
          <div className="text-center mt-5">
            {auth.loading && (
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            )}
            {!auth.loading && auth.positions && auth.username ? (
              <Navigate to="/login-second-step" />
            ) : null}
          </div>
        </div>
        <div className="col-sm-3"></div>
      </div>
    </div>
  );
}
