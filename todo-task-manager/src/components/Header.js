import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Header.css';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [showUserMenu, setShowUserMenu] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const toggleUserMenu = () => {
    setShowUserMenu(!showUserMenu);
  };

  if (!user) {
    return null; // Don't show header on auth pages
  }

  return (
    <header className="app-header">
      <div className="header-container">
        <div className="header-left">
          <Link to="/dashboard" className="logo">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
            </svg>
            <span>Todo Manager</span>
          </Link>
        </div>

        <nav className="header-nav">
          <Link to="/dashboard" className="nav-link">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
            </svg>
            Dashboard
          </Link>
        </nav>

        <div className="header-right">
          <div className="user-menu">
            <button onClick={toggleUserMenu} className="user-button">
              <div className="user-avatar">
                {user.name.charAt(0).toUpperCase()}
              </div>
              <span className="user-name">{user.name}</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" className={`chevron ${showUserMenu ? 'open' : ''}`}>
                <path d="M7 10l5 5 5-5z"/>
              </svg>
            </button>

            {showUserMenu && (
              <div className="user-dropdown">
                <div className="user-info">
                  <div className="user-name">{user.name}</div>
                  <div className="user-email">{user.email}</div>
                </div>
                <div className="dropdown-divider"></div>
                <button onClick={handleLogout} className="dropdown-item logout-btn">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.59L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
                  </svg>
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
