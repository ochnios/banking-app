import { useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  resetPassword,
  sendResetPasswordToken,
} from "../../reducers/passwordSlice";
import { useParams } from "react-router-dom";

export default function ResetPasswordPage() {
  const dispatch = useDispatch();
  const password = useSelector((state) => state.password);
  let { token } = useParams();
  const usernameRef = useRef();
  const passwordRef = useRef();
  const retypedPasswordRef = useRef();

  const handleSubmitResetRequest = (e) => {
    e.preventDefault();
    dispatch(sendResetPasswordToken(usernameRef.current.value));
  };

  const handleSubmitResetPassword = (e) => {
    e.preventDefault();
    dispatch(
      resetPassword({
        token: token,
        password: passwordRef.current.value,
        passwordRetyped: retypedPasswordRef.current.value,
      })
    );
  };

  return (
    <div className="container-fluid py-5">
      <div className="row">
        <div className="col-sm-3"></div>
        <div className="col-sm-6">
          {token ? (
            <form
              className="border rounded p-2"
              onSubmit={handleSubmitResetPassword}
            >
              {password.errors ? (
                <div className="alert alert-danger">{password.errors}</div>
              ) : null}
              {password.successMessage ? (
                <div className="alert alert-success">
                  {password.successMessage}
                </div>
              ) : null}
              <div className="mb-3">
                <label htmlFor="password" className="form-label">
                  Password
                </label>
                <input
                  type="password"
                  name="password"
                  id="password"
                  ref={passwordRef}
                  className="form-control"
                  minLength={12}
                  maxLength={24}
                />
                <label htmlFor="retypedPassword" className="form-label">
                  Retype password
                </label>
                <input
                  type="password"
                  name="retypedPassword"
                  id="retypedPassword"
                  ref={retypedPasswordRef}
                  className="form-control"
                  minLength={12}
                  maxLength={24}
                />
              </div>
              <div className="d-flex justify-content-center">
                <button type="submit" className="btn btn-dark">
                  Send reset password link
                </button>
              </div>
            </form>
          ) : (
            <form
              className="border rounded p-2"
              onSubmit={handleSubmitResetRequest}
            >
              {password.errors ? (
                <div className="alert alert-danger">{password.errors}</div>
              ) : null}
              {password.successMessage ? (
                <div className="alert alert-success">
                  {password.successMessage}
                </div>
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
                  Send reset password link
                </button>
              </div>
            </form>
          )}
          <div className="text-center mt-5">
            {password.loading && (
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            )}
          </div>
        </div>
        <div className="col-sm-3"></div>
      </div>
    </div>
  );
}
