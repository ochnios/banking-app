import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

export default function PublicView(props) {
  let authenticated = useSelector((state) => state.auth.authenticated);

  return (
    <>
      {authenticated ? (
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
