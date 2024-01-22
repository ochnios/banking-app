import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchTransfer } from "../../reducers/transferSlice";
import { useParams } from "react-router-dom";
import { fetchAccount } from "../../reducers/accountSlice";
import { Link } from "react-router-dom";

export default function TransferDetailsPage() {
  const dispatch = useDispatch();
  let { id } = useParams();
  const transfer = useSelector((state) => state.transfer);
  const account = useSelector((state) => state.account);

  useEffect(() => {
    if (!account.accountNumber) dispatch(fetchAccount());
    if (id) {
      dispatch(fetchTransfer(id));
    }
  }, []);

  return (
    <div className="container-fluid py-5">
      <div className="row">
        <div className="col-sm-12">
          {transfer.errors && (
            <div className="alert alert-danger">{transfer.errors}</div>
          )}
          <div className="mt-2">
            {transfer.loading ? (
              <div className="spinner-border" role="status">
                <span className="sr-only">Loading...</span>
              </div>
            ) : (
              !transfer.errors &&
              transfer.transfer && (
                <>
                  <h1 className="mb-3">Transfer details</h1>
                  <table className="table table-striped">
                    <tbody>
                      <tr>
                        <td className="text-end fw-bold">Time</td>
                        <td>
                          {transfer.transfer.time?.substring(0, 10) +
                            " " +
                            transfer.transfer.time?.substring(11, 16)}
                        </td>
                      </tr>
                      <tr>
                        <td className="text-end fw-bold">Title</td>
                        <td>{transfer.transfer.title}</td>
                      </tr>
                      <tr>
                        <td className="text-end fw-bold">Amount</td>
                        <td
                          className={
                            transfer.transfer.senderAccountNumber ==
                            account.accountNumber
                              ? "text-danger"
                              : "text-success"
                          }
                        >
                          {parseFloat(transfer.transfer.amount).toLocaleString(
                            "pl-PL",
                            {
                              style: "currency",
                              currency: "PLN",
                            }
                          )}
                        </td>
                      </tr>
                      <tr>
                        <td className="text-end fw-bold">Sender name</td>
                        <td>{transfer.transfer.senderName}</td>
                      </tr>
                      <tr>
                        <td className="text-end fw-bold">Sender account no.</td>
                        <td>{transfer.transfer.senderAccountNumber}</td>
                      </tr>
                      <tr>
                        <td className="text-end fw-bold">Sender address</td>
                        <td>{transfer.transfer.senderAddress}</td>
                      </tr>
                      <tr>
                        <td className="text-end fw-bold">Recipient name</td>
                        <td>{transfer.transfer.recipientName}</td>
                      </tr>
                      <tr>
                        <td className="text-end fw-bold">
                          Recipient account no.
                        </td>
                        <td>{transfer.transfer.recipientAccountNumber}</td>
                      </tr>
                      <tr>
                        <td className="text-end fw-bold">Recipient address</td>
                        <td>{transfer.transfer.recipientAddress}</td>
                      </tr>
                      <tr>
                        <td className="text-end fw-bold">Transfer type</td>
                        <td>{transfer.transfer.type}</td>
                      </tr>
                    </tbody>
                  </table>
                  <div className="d-flex justify-content-end">
                    <Link to="/transfers">Back to transfer list</Link>
                  </div>
                </>
              )
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
