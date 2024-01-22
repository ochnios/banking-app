import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  clearPersonalData,
  fetchPersonalData,
} from "../../reducers/personalDataSlice";

export default function PersonalDataPage() {
  const dispatch = useDispatch();
  const personalData = useSelector((state) => state.personalData);
  const [showData, setShowData] = useState(false);

  useEffect(() => {
    if (showData) dispatch(fetchPersonalData());
    else dispatch(clearPersonalData());
  }, [showData]);

  return (
    <div className="container-fluid py-5">
      <div className="row">
        <div className="col-sm-12">
          {personalData.errors ? (
            <div className="alert alert-danger">{personalData.errors}</div>
          ) : null}
          <div className="mt-2">
            {personalData.loading && (
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            )}
            {!personalData.loading && !personalData.errors && (
              <div>
                <div className="d-flex justify-content-between mb-3">
                  <h1>Your personal data</h1>
                  <div>
                    <a
                      className="btn btn-primary"
                      to="/new-transfer"
                      onClick={() => setShowData(!showData)}
                    >
                      {showData ? "Hide" : "Show"}
                    </a>
                  </div>
                </div>
                {showData && (
                  <>
                    <p className="mb-1">
                      <strong>Address: </strong>
                      {personalData.address}
                    </p>
                    <p className="mb-1">
                      <strong>Card number: </strong>
                      {personalData.cardNumber}
                    </p>
                    <p>
                      <strong>Identification: </strong>{" "}
                      {personalData.identification}
                    </p>
                  </>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
