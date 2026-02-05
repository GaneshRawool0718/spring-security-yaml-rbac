import React from "react";

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error("Error caught by boundary:", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            height: "100vh",
            background: "#f5f7fa",
          }}
        >
          <div
            style={{
              padding: "40px",
              background: "white",
              borderRadius: "8px",
              boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
              maxWidth: "500px",
              textAlign: "center",
            }}
          >
            <h2 style={{ color: "#dc2626", marginBottom: "16px" }}>
              Something Went Wrong
            </h2>
            <p style={{ color: "#666", marginBottom: "16px" }}>
              {this.state.error?.message || "An unexpected error occurred"}
            </p>
            <details
              style={{
                textAlign: "left",
                background: "#f9fafb",
                padding: "12px",
                borderRadius: "4px",
                marginBottom: "16px",
                fontSize: "12px",
              }}
            >
              <summary style={{ cursor: "pointer", fontWeight: "bold" }}>
                Error Details
              </summary>
              <pre
                style={{
                  overflow: "auto",
                  marginTop: "8px",
                  color: "#666",
                }}
              >
                {this.state.error?.toString()}
              </pre>
            </details>
            <button
              onClick={() => window.location.reload()}
              style={{
                padding: "10px 20px",
                background: "#3b82f6",
                color: "white",
                border: "none",
                borderRadius: "6px",
                cursor: "pointer",
                fontSize: "14px",
              }}
            >
              Reload Page
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
