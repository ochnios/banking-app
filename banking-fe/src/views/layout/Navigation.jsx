import { useDispatch, useSelector } from "react-redux";
import { Link, useMatch } from "react-router-dom";
import { logout } from "../../reducers/authSlice";

export default function Navigation() {
  const match = useMatch("/:page");
  const auth = useSelector((state) => state.auth);
  const dispatch = useDispatch();

  return (
    <nav className="navbar navbar-expand-sm navbar-dark bg-dark">
      <div className="container-fluid">
        <Link className="navbar-brand" to="/">
          BankingApp
        </Link>
        <div className="navbar-collapse" id="navbarText">
          <ul className="navbar-nav me-auto">
            {auth.authenticated && (
              <>
                <li className="nav-item">
                  <Link
                    className={
                      "nav-link " +
                      (match?.params?.page === "account" && "active")
                    }
                    to="/account"
                  >
                    Account
                  </Link>
                </li>
                <li className="nav-item">
                  <Link
                    className={
                      "nav-link " +
                      (match?.params?.page === "transfers" && "active")
                    }
                    to="/transfers"
                  >
                    Transfers
                  </Link>
                </li>
                <li className="nav-item">
                  <Link
                    className={
                      "nav-link " +
                      (match?.params?.page === "personal" && "active")
                    }
                    to="/personal"
                  >
                    Personal
                  </Link>
                </li>
              </>
            )}
          </ul>
          <div className="d-flex">
            {auth.authenticated ? (
              <>
                <span className="me-2 text-white">
                  {auth.name + " " + auth.surname} |
                </span>
                <a
                  className="sign-link text-white-50"
                  onClick={() => {
                    dispatch(logout());
                  }}
                >
                  Wyloguj
                </a>
              </>
            ) : (
              <Link className="sign-link text-white-50" to="/login-first-step">
                Zaloguj
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
