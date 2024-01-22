import { Link, useMatch } from "react-router-dom";

export default function Navigation() {
  const match = useMatch("/:page");

  return (
    <nav className="navbar navbar-expand-sm navbar-dark bg-dark">
      <div className="container-fluid">
        <Link className="navbar-brand" to="/">
          BankingApp
        </Link>
        <div className="navbar-collapse" id="navbarText">
          <ul className="navbar-nav">
            <li className="nav-item">
              <Link
                className={
                  "nav-link " + (match?.params?.page === "/account" && "active")
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
                  (match?.params?.page === "/transfers" && "active")
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
                  (match?.params?.page === "/personal" && "active")
                }
                to="/personal"
              >
                Personal
              </Link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
}
