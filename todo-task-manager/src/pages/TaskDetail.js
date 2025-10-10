import React, { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useTask } from '../hooks/useTasks';
import { taskAPI } from '../services/api';
import '../styles/TaskDetail.css';

const TaskDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { task, loading, error } = useTask(id);
  const [isToggling, setIsToggling] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const formatDate = (dateString) => {
    if (!dateString) return 'Not set';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const formatDateTime = (dateString) => {
    if (!dateString) return 'Not available';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getPriorityClass = (priority) => {
    switch (priority) {
      case 'high':
        return 'priority-high';
      case 'medium':
        return 'priority-medium';
      case 'low':
        return 'priority-low';
      default:
        return 'priority-medium';
    }
  };

  const isOverdue = (dueDate) => {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date().setHours(0, 0, 0, 0);
  };

  const handleToggleComplete = async () => {
    try {
      setIsToggling(true);
      await taskAPI.toggleTask(id);
      window.location.reload(); // Simple refresh for now
    } catch (err) {
      console.error('Failed to toggle task:', err);
    } finally {
      setIsToggling(false);
    }
  };

  const handleDelete = async () => {
    try {
      setIsDeleting(true);
      await taskAPI.deleteTask(id);
      navigate('/dashboard');
    } catch (err) {
      console.error('Failed to delete task:', err);
      setIsDeleting(false);
    }
  };

  const handleEdit = () => {
    navigate(`/tasks/${id}/edit`);
  };

  if (loading) {
    return (
      <div className="task-detail-container">
        <div className="loading-state">
          <div className="loading-spinner"></div>
          <p>Loading task details...</p>
        </div>
      </div>
    );
  }

  if (error || !task) {
    return (
      <div className="task-detail-container">
        <div className="error-state">
          <h2>Task Not Found</h2>
          <p>{error || 'The requested task could not be found.'}</p>
          <Link to="/dashboard" className="btn-primary">
            Back to Dashboard
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="task-detail-container">
      <div className="task-detail-header">
        <Link to="/dashboard" className="back-link">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
          </svg>
          Back to Tasks
        </Link>
        
        <div className="task-actions">
          <button
            onClick={handleEdit}
            className="btn-secondary"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
            </svg>
            Edit
          </button>
          
          <button
            onClick={() => setShowDeleteConfirm(true)}
            className="btn-danger"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
            </svg>
            Delete
          </button>
        </div>
      </div>

      <div className="task-detail-card">
        <div className="task-status-bar">
          <div className="status-info">
            <span className={`status-badge ${task.completed ? 'completed' : 'pending'}`}>
              {task.completed ? 'Completed' : 'Pending'}
            </span>
            <span className={`priority-badge ${getPriorityClass(task.priority)}`}>
              {task.priority} priority
            </span>
            {task.dueDate && (
              <span className={`due-badge ${isOverdue(task.dueDate) && !task.completed ? 'overdue' : ''}`}>
                Due: {formatDate(task.dueDate)}
              </span>
            )}
          </div>
          
          <button
            onClick={handleToggleComplete}
            className={`btn-toggle ${task.completed ? 'btn-mark-pending' : 'btn-mark-complete'}`}
            disabled={isToggling}
          >
            {isToggling ? 'Updating...' : (task.completed ? 'Mark as Pending' : 'Mark as Complete')}
          </button>
        </div>

        <div className="task-content">
          <h1 className="task-title">{task.title}</h1>
          
          {task.description && (
            <div className="task-description">
              <h3>Description</h3>
              <p>{task.description}</p>
            </div>
          )}

          <div className="task-metadata">
            <div className="metadata-grid">
              <div className="metadata-item">
                <label>Priority</label>
                <span className={`priority-text ${getPriorityClass(task.priority)}`}>
                  {task.priority.charAt(0).toUpperCase() + task.priority.slice(1)}
                </span>
              </div>
              
              <div className="metadata-item">
                <label>Status</label>
                <span className={task.completed ? 'text-success' : 'text-warning'}>
                  {task.completed ? 'Completed' : 'Pending'}
                </span>
              </div>
              
              <div className="metadata-item">
                <label>Due Date</label>
                <span className={isOverdue(task.dueDate) && !task.completed ? 'text-danger' : ''}>
                  {formatDate(task.dueDate)}
                </span>
              </div>
              
              <div className="metadata-item">
                <label>Created</label>
                <span>{formatDateTime(task.createdAt)}</span>
              </div>
              
              <div className="metadata-item">
                <label>Last Updated</label>
                <span>{formatDateTime(task.updatedAt)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {showDeleteConfirm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Confirm Delete</h3>
            <p>Are you sure you want to delete this task? This action cannot be undone.</p>
            <div className="modal-actions">
              <button
                onClick={() => setShowDeleteConfirm(false)}
                className="btn-secondary"
                disabled={isDeleting}
              >
                Cancel
              </button>
              <button
                onClick={handleDelete}
                className="btn-danger"
                disabled={isDeleting}
              >
                {isDeleting ? 'Deleting...' : 'Delete Task'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskDetail;
