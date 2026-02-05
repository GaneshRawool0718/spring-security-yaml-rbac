import { useEffect, useState } from "react";
import Layout from "../layout/Layout";
import { getMyProfile, updateMyProfile } from "../../services/userProfileService";

export default function UserProfile() {
  const [profile, setProfile] = useState({
    email: "",
    status: "",
    roles: []
  });

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    nickName: "",
    mobilePhone: "",
    idolName: ""
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await getMyProfile();

      setProfile({
        email: data.email ?? "",
        status: data.status ?? "",
        roles: data.roles ?? []
      });

      setForm({
        firstName: data.firstName ?? "",
        lastName: data.lastName ?? "",
        nickName: data.nickName ?? "",
        mobilePhone: data.mobilePhone ?? "",
        idolName: data.idolName ?? ""
      });
    } catch (e) {
      console.error("Failed to load profile:", e);
      setError("Failed to load profile: " + (e.message || "Unknown error"));
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(false);

    try {
      await updateMyProfile(form);
      setSuccess(true);
      await loadProfile();
    } catch (e) {
      console.error("Failed to update profile:", e);
      setError("Failed to update profile: " + (e.message || "Unknown error"));
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Layout>
        <div>Loading profile...</div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="dashboard">
        <h1>My Profile</h1>

        {error && <div className="error-message">{error}</div>}
        {success && (
          <div className="api-response">Profile updated successfully</div>
        )}

        <div className="dashboard-section">
          <h2>Account Information</h2>

          <p>
            <strong>Email:</strong> {profile.email}
          </p>

          <p>
            <strong>Status:</strong> {profile.status}
          </p>

          <p>
            <strong>Roles:</strong> {profile.roles.join(", ")}
          </p>
        </div>

        <div className="dashboard-section">
          <h2>Edit Profile</h2>

          <form onSubmit={handleSubmit} className="profile-form">
            <label htmlFor="firstName">First Name</label>
            <input
              id="firstName"
              name="firstName"
              value={form.firstName}
              onChange={handleChange}
              placeholder="Enter first name"
            />

            <label htmlFor="lastName">Last Name</label>
            <input
              id="lastName"
              name="lastName"
              value={form.lastName}
              onChange={handleChange}
              placeholder="Enter last name"
            />

            <label htmlFor="nickName">Nickname</label>
            <input
              id="nickName"
              name="nickName"
              value={form.nickName}
              onChange={handleChange}
              placeholder="Enter nickname"
            />

            <label htmlFor="idolName">Idol Name</label>
            <input
              id="idolName"
              name="idolName"
              value={form.idolName}
              onChange={handleChange}
              placeholder="Enter idol name"
            />

            <label htmlFor="mobilePhone">Mobile Phone</label>
            <input
              id="mobilePhone"
              name="mobilePhone"
              value={form.mobilePhone}
              onChange={handleChange}
              placeholder="Enter mobile phone"
            />

            <button className="btn btn-primary" disabled={saving}>
              {saving ? "Saving..." : "Save Changes"}
            </button>
          </form>
        </div>
      </div>
    </Layout>
  );
}
