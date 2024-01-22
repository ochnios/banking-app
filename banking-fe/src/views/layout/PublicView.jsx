import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

export default function PublicView(props) {
  let authorized = useSelector((state) => state.auth.authorized);

  return (
    <>
      {authorized ? (
        <div>
          <Navigate to="/account" />
        </div>
      ) : (
        // eslint-disable-next-line react/prop-types
        <>{props.children}</>
      )}
    </>
  );
}
