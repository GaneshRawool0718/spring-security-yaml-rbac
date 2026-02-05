import Navbar from "./Navbar";
import Sidebar from "./Sidebar";

/**
 * Main layout wrapper for authenticated pages
 * Includes navbar (top) and sidebar (left)
 */
export default function Layout({ children }) {
  return (
    <div className="app-shell">
      <Sidebar />
      <div className="app-main">
        <Navbar />
        <div className="app-content">{children}</div>
      </div>
    </div>
  );
}
