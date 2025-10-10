import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles/TaskItem.css';

const TaskItem = ({ task, onToggle, onDelete, onEdit }) => {
  const [isDeleting, setIsDeleting] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
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

  const handleToggle = async () => {
    await onToggle(task.id);
  };

  const handleDelete = async () => {
    setIsDeleting(true);
    await onDelete(task.id);
    setIsDeleting(false);
    setShowDeleteConfirm(false);
  };

  const handleDeleteClick = () => {
    setShowDeleteConfirm(true);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirm(false);
  };

  return (
    <div className={`task-item ${task.completed ? 'completed' : ''}`}>
      <div className="task-content">
        <div className="task-header">
          <div className="task-checkbox">
            <input
              type="checkbox"
              checked={task.completed}
              onChange={handleToggle}
              id={`task-${task.id}`}
            />
            <label htmlFor={`task-${task.id}`} className="checkbox-label">
              <span className="checkmark"></span>
            </label>
          </div>
          
          <div className="task-info">
            <Link to={`/tasks/${task.id}`} className="task-title">
              {task.title}
            </Link>
            {task.description && (
              <p className="task-description">{task.description}</p>
            )}
          </div>
          
          <div className="task-meta">
            <span className={`priority-badge ${getPriorityClass(task.priority)}`}>
              {task.priority}
            </span>
            {task.dueDate && (
              <span className={`due-date ${isOverdue(task.dueDate) && !task.completed ? 'overdue' : ''}`}>
                {formatDate(task.dueDate)}
              </span>
            )}
          </div>
        </div>
      </div>

      <div className="task-actions">
        <button
          onClick={() => onEdit(task)}
          className="btn-icon btn-edit"
          title="Edit task"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
            <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
          </svg>
        </button>
        
        <button
          onClick={handleDeleteClick}
          className="btn-icon btn-delete"
          title="Delete task"
          disabled={isDeleting}
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
            <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
          </svg>
        </button>
      </div>

      {showDeleteConfirm && (
        <div className="delete-confirm">
          <div className="delete-confirm-content">
            <p>Are you sure you want to delete this task?</p>
            <div className="delete-confirm-actions">
              <button
                onClick={handleCancelDelete}
                className="btn-secondary btn-small"
              >
                Cancel
              </button>
              <button
                onClick={handleDelete}
                className="btn-danger btn-small"
                disabled={isDeleting}
              >
                {isDeleting ? 'Deleting...' : 'Delete'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskItem;
