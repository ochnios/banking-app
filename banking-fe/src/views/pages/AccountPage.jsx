import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchAccount } from "../../reducers/accountSlice";

export default function AccountPage() {
  const dispatch = useDispatch();
  const auth = useSelector((state) => state.auth);
  const account = useSelector((state) => state.account);

  useEffect(() => {
    dispatch(fetchAccount());
  }, []);

  return (
    <div className="container-fluid py-5">
      <div className="row">
        <div className="col-sm-3"></div>
        <div className="col-sm-6">
          {account.errors ? (
            <div className="alert alert-danger">{account.errors}</div>
          ) : null}
          <div className="mt-5">
            {account.loading && (
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            )}
            {!account.loading && !account.errors && (
              <div>
                <h1 className="mb-3">Hello, {auth.name}!</h1>
                <p className="mb-1">
                  <strong>Your balance:</strong> {account.balance}
                </p>
                <p>
                  <strong>Account number:</strong> {account.accountNumber}
                </p>
              </div>
            )}
          </div>
        </div>
        <div className="col-sm-3"></div>
      </div>
    </div>
  );
}
