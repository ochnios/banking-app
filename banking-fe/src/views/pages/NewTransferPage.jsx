import { useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { createTransferOrder } from "../../reducers/transferSlice";
import { Navigate } from "react-router-dom";

export default function NewTransferPage() {
  const dispatch = useDispatch();
  const transfer = useSelector((state) => state.transfer);
  const titleRef = useRef();
  const amountRef = useRef();
  const recipientAccountNumberRef = useRef();
  const recipientNameRef = useRef();
  const recipientAddressRef = useRef();

  const handleSubmit = (e) => {
    e.preventDefault();
    dispatch(
      createTransferOrder({
        title: titleRef.current.value,
        amount: amountRef.current.value,
        recipientAccountNumber: recipientAccountNumberRef.current.value,
        recipientName: recipientNameRef.current.value,
        recipientAddress: recipientAddressRef.current.value,
      })
    );
  };

  return (
    <div className="container-fluid py-5">
      <div className="row">
        <div className="col-sm-3"></div>
        <div className="col-sm-6">
          <h1 className="mb-3">Send new transfer</h1>
          <form className="border rounded p-2" onSubmit={handleSubmit}>
            {transfer.errors ? (
              <div className="alert alert-danger">{transfer.errors}</div>
            ) : null}
            <div className="mb-3">
              <label htmlFor="title" className="form-label">
                Title
              </label>
              <input
                type="text"
                name="title"
                id="title"
                ref={titleRef}
                className="form-control"
              />
              <label htmlFor="amount" className="form-label mt-2">
                Amount
              </label>
              <input
                type="text"
                name="amount"
                id="amount"
                ref={amountRef}
                className="form-control"
              />
              <label
                htmlFor="recipientAccountNumber"
                className="form-label mt-2"
              >
                Recipient account number
              </label>
              <input
                type="text"
                name="recipientAccountNumber"
                id="recipientAccountNumber"
                ref={recipientAccountNumberRef}
                className="form-control"
              />
              <label htmlFor="recipientName" className="form-label mt-2">
                Recipient name
              </label>
              <input
                type="text"
                name="recipientName"
                id="recipientName"
                ref={recipientNameRef}
                className="form-control"
              />
              <label htmlFor="recipientAddress" className="form-label mt-2">
                Recipient address
              </label>
              <input
                type="text"
                name="recipientAddress"
                id="recipientAddress"
                ref={recipientAddressRef}
                className="form-control"
              />
            </div>
            <div className="d-flex justify-content-center">
              <button type="submit" className="btn btn-dark">
                Submit
              </button>
            </div>
          </form>
          <div className="text-center mt-5">
            {transfer.loading && (
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            )}
            {!transfer.loading && transfer.transfer ? (
              <Navigate to="/transfer-details" />
            ) : null}
          </div>
        </div>
        <div className="col-sm-3"></div>
      </div>
    </div>
  );
}
