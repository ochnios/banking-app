import { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchTransfers } from "../../reducers/transferSlice";
import { Link } from "react-router-dom";
import { fetchAccount } from "../../reducers/accountSlice";

export default function TransfersPage() {
  const dispatch = useDispatch();
  const transfer = useSelector((state) => state.transfer);
  const account = useSelector((state) => state.account);
  const [pageNumber, setPageNumber] = useState(1);

  useEffect(() => {
    if (!account.accountNumber) dispatch(fetchAccount());
    dispatch(fetchTransfers({ pageNumber }));
  }, [pageNumber]);

  const handleSpecificPage = (specificPageNumber) => {
    setPageNumber(specificPageNumber);
  };

  const handlePreviousPage = () => {
    if (pageNumber > 1) setPageNumber(pageNumber - 1);
  };

  const handleNextPage = () => {
    if (pageNumber < transfer.totalPages) setPageNumber(pageNumber + 1);
  };

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
              !transfer.errors && (
                <>
                  <div className="d-flex justify-content-between mb-3">
                    <h1>Your transfers</h1>
                    <div>
                      <Link className="btn btn-primary" to="/new-transfer">
                        New transfer
                      </Link>
                    </div>
                  </div>
                  <table className="table table-striped">
                    <thead>
                      <tr>
                        <th>Date</th>
                        <th>Title</th>
                        <th>Amount</th>
                        <th>Sender</th>
                        <th>Recipient</th>
                      </tr>
                    </thead>
                    <tbody>
                      {transfer.transfers?.map((item, idx) => (
                        <tr key={idx}>
                          <td>
                            {item?.time?.substring(0, 10) +
                              " " +
                              item?.time?.substring(11, 16)}
                          </td>
                          <td>{item.title}</td>
                          <td
                            className={
                              item.senderAccountNumber == account.accountNumber
                                ? "text-danger"
                                : "text-success"
                            }
                          >
                            {parseFloat(item.amount).toLocaleString("pl-PL", {
                              style: "currency",
                              currency: "PLN",
                            })}
                          </td>
                          <td>{item.senderName}</td>
                          <td>{item.recipientName}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                  <nav className="d-flex justify-content-end">
                    <ul className="pagination">
                      <li
                        className={`page-item ${
                          pageNumber === 1 ? "disabled" : ""
                        }`}
                      >
                        <a className="page-link" onClick={handlePreviousPage}>
                          Previous
                        </a>
                      </li>
                      {[...Array(transfer.totalPages)].map((e, idx) => (
                        <li
                          className={`page-item ${
                            pageNumber === idx + 1 ? "active" : ""
                          }`}
                          key={idx}
                        >
                          <a
                            className="page-link"
                            onClick={() => handleSpecificPage(idx + 1)}
                          >
                            {idx + 1}
                          </a>
                        </li>
                      ))}
                      <li
                        className={`page-item ${
                          pageNumber === transfer.totalPages ? "disabled" : ""
                        }`}
                      >
                        <a className="page-link" onClick={handleNextPage}>
                          Next
                        </a>
                      </li>
                    </ul>
                  </nav>
                </>
              )
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
