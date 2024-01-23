import { useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { authenticate, unauthenticate } from "../../reducers/authSlice";
import { Link, Navigate } from "react-router-dom";

export default function LoginSecondStep() {
  const dispatch = useDispatch();
  const auth = useSelector((state) => state.auth);
  const usernameRef = useRef();
  const pwdCharactersRefs = useRef([]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const partialPasswordPositions = auth.positions;
    const partialPassword = partialPasswordPositions
      .map((i) => {
        const ref = pwdCharactersRefs.current[i - 1];
        return ref && ref.value ? ref.value : "";
      })
      .join("");

    dispatch(
      authenticate({
        username: auth.username,
        password: partialPassword,
      })
    );
  };

  const clearPartialPassword = () => {
    auth.positions.map((i) => {
      const ref = pwdCharactersRefs.current[i - 1];
      if (ref) ref.value = "";
    });
  };

  const moveToNextCharacter = (e, nextIndex) => {
    if (e.target.value.length === e.target.maxLength) {
      while (
        nextIndex < pwdCharactersRefs.current.length &&
        pwdCharactersRefs.current[nextIndex].disabled
      ) {
        nextIndex += 1;
      }
      if (nextIndex < pwdCharactersRefs.current.length) {
        pwdCharactersRefs.current[nextIndex].focus();
      }
    }
  };

  return (
    <>
      {auth.positions && auth.username ? (
        <div className="container-fluid py-5">
          <div className="row">
            <div className="col-sm-3"></div>
            <div className="col-sm-6">
              <form className="border rounded p-2" onSubmit={handleSubmit}>
                {auth.errors
                  ? (clearPartialPassword(),
                    (<div className="alert alert-danger">{auth.errors}</div>))
                  : null}
                <div className="mb-3">
                  <label htmlFor="username" className="form-label">
                    Username
                  </label>
                  <input
                    type="text"
                    name="username"
                    id="username"
                    value={auth.username}
                    ref={usernameRef}
                    disabled
                    className="form-control"
                  />
                  <label className="form-label mt-3">Password</label>
                  <div className="partial-password-line d-flex justify-content-center gap-1 mb-1">
                    {Array.from({ length: 12 }, (_, index) => (
                      <div
                        className="partial-password-char d-flex flex-column text-center"
                        key={index}
                      >
                        <label htmlFor={index + 1}>{index + 1}</label>
                        <input
                          type="password"
                          name={index + 1}
                          ref={(el) => (pwdCharactersRefs.current[index] = el)}
                          defaultValue={
                            auth.positions.includes(index + 1) ? "" : "*"
                          }
                          disabled={!auth.positions.includes(index + 1)}
                          maxLength="1"
                          minLength="1"
                          onChange={(e) => moveToNextCharacter(e, index + 1)}
                          required
                        ></input>
                      </div>
                    ))}
                  </div>
                  <div className="partial-password-line d-flex justify-content-center gap-1">
                    {Array.from({ length: 12 }, (_, index) => (
                      <div
                        className="partial-password-char d-flex flex-column text-center"
                        key={index}
                      >
                        <label htmlFor={index + 13}>{index + 13}</label>
                        <input
                          type="password"
                          name={index + 1}
                          ref={(el) =>
                            (pwdCharactersRefs.current[index + 12] = el)
                          }
                          defaultValue={
                            auth.positions.includes(index + 13) ? "" : "*"
                          }
                          disabled={!auth.positions.includes(index + 13)}
                          maxLength="1"
                          minLength="1"
                          onChange={(e) => moveToNextCharacter(e, index + 13)}
                          required
                        ></input>
                      </div>
                    ))}
                  </div>
                </div>
                <div className="d-flex justify-content-center">
                  <button type="submit" className="btn btn-dark">
                    Sign in
                  </button>
                </div>
                <Link
                  to="/login-first-step"
                  onClick={() => dispatch(unauthenticate())}
                  className="mt-2"
                >
                  Back
                </Link>
              </form>
              <div className="text-center mt-5">
                {auth.loading && (
                  <div className="spinner-border" role="status">
                    <span className="visually-hidden">Loading...</span>
                  </div>
                )}
                {!auth.loading && auth.authenticated ? (
                  <Navigate to="/account" />
                ) : null}
              </div>
            </div>
            <div className="col-sm-3"></div>
          </div>
        </div>
      ) : (
        <Navigate to="/login-first-step" />
      )}
    </>
  );
}
