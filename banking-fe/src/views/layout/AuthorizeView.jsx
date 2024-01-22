import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

export default function AuthorizeView(props) {
  const authenticated = useSelector((state) => state.auth.authenticated);

  return (
    <>
      {!authenticated ? (
        <div>
          <Navigate to="/login-first-step" />
        </div>
      ) : (
        // eslint-disable-next-line react/prop-types
        <>{props.children}</>
      )}
    </>
  );
}
