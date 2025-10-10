import React, { useState, useEffect } from 'react';
import '../styles/TaskForm.css';

const TaskForm = ({ 
  task = null, 
  onSubmit, 
  onCancel, 
  loading = false,
  title = 'Create Task'
}) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    priority: 'medium',
    dueDate: ''
  });
  const [validationErrors, setValidationErrors] = useState({});

  useEffect(() => {
    if (task) {
      setFormData({
        title: task.title || '',
        description: task.description || '',
        priority: task.priority || 'medium',
        dueDate: task.dueDate ? task.dueDate.split('T')[0] : ''
      });
    }
  }, [task]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear validation error for this field
    if (validationErrors[name]) {
      setValidationErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.title.trim()) {
      errors.title = 'Task title is required';
    } else if (formData.title.length > 100) {
      errors.title = 'Title must be less than 100 characters';
    }
    
    if (formData.description.length > 500) {
      errors.description = 'Description must be less than 500 characters';
    }
    
    if (formData.dueDate && new Date(formData.dueDate) < new Date().setHours(0, 0, 0, 0)) {
      errors.dueDate = 'Due date cannot be in the past';
    }
    
    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    const submitData = {
      ...formData,
      title: formData.title.trim(),
      description: formData.description.trim()
    };
    
    onSubmit(submitData);
  };

  return (
    <div className="task-form-container">
      <div className="task-form-card">
        <h2>{title}</h2>
        <form onSubmit={handleSubmit} className="task-form">
          <div className="form-group">
            <label htmlFor="title">
              Task Title <span className="required">*</span>
            </label>
            <input
              type="text"
              id="title"
              name="title"
              value={formData.title}
              onChange={handleChange}
              placeholder="Enter task title"
              className={validationErrors.title ? 'error' : ''}
              maxLength="100"
            />
            {validationErrors.title && (
              <span className="error-text">{validationErrors.title}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Enter task description (optional)"
              rows="4"
              className={validationErrors.description ? 'error' : ''}
              maxLength="500"
            />
            {validationErrors.description && (
              <span className="error-text">{validationErrors.description}</span>
            )}
            <small className="char-count">
              {formData.description.length}/500 characters
            </small>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="priority">Priority</label>
              <select
                id="priority"
                name="priority"
                value={formData.priority}
                onChange={handleChange}
              >
                <option value="low">Low</option>
                <option value="medium">Medium</option>
                <option value="high">High</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="dueDate">Due Date</label>
              <input
                type="date"
                id="dueDate"
                name="dueDate"
                value={formData.dueDate}
                onChange={handleChange}
                className={validationErrors.dueDate ? 'error' : ''}
                min={new Date().toISOString().split('T')[0]}
              />
              {validationErrors.dueDate && (
                <span className="error-text">{validationErrors.dueDate}</span>
              )}
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={onCancel}
              className="btn-secondary"
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn-primary"
              disabled={loading}
            >
              {loading ? 'Saving...' : (task ? 'Update Task' : 'Create Task')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TaskForm;
