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
    dueDate: '',
    reminderType: 'none',
    reminderTime: '',
    phoneNumber: ''
  });
  const [validationErrors, setValidationErrors] = useState({});

  useEffect(() => {
    if (task) {
      setFormData({
        title: task.title || '',
        description: task.description || '',
        priority: task.priority || 'medium',
        dueDate: task.dueDate ? task.dueDate.split('T')[0] : '',
        reminderType: task.reminderType?.toLowerCase() || 'none',
        reminderTime: task.reminderTime ? new Date(task.reminderTime).toISOString().slice(0, 16) : '',
        phoneNumber: task.phoneNumber || ''
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
    
    if (formData.dueDate && formData.dueDate < new Date().toISOString().split('T')[0]) {
      errors.dueDate = 'Due date cannot be in the past';
    }

    // Reminder validation
    if (formData.reminderType !== 'none' && !formData.reminderTime) {
      errors.reminderTime = 'Reminder time is required when reminder type is selected';
    }

    if (formData.reminderTime && formData.reminderTime < new Date().toISOString().slice(0, 16)) {
      errors.reminderTime = 'Reminder time cannot be in the past';
    }

    if (formData.reminderType === 'sms' && !formData.phoneNumber.trim()) {
      errors.phoneNumber = 'Phone number is required for SMS reminders';
    }

    if (formData.phoneNumber && !/^\+?[\d\s\-\(\)]+$/.test(formData.phoneNumber)) {
      errors.phoneNumber = 'Please enter a valid phone number';
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
      priority: formData.priority.toUpperCase(),
      reminderType: formData.reminderType === 'none' ? null : formData.reminderType.toUpperCase(),
      reminderTime: formData.reminderTime ? new Date(formData.reminderTime).toISOString() : null,
      phoneNumber: formData.reminderType === 'sms' ? formData.phoneNumber : null
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

          {/* Reminder Settings */}
          <div className="reminder-section">
            <h3>Reminder Settings</h3>
            
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="reminderType">Reminder Type</label>
                <select
                  id="reminderType"
                  name="reminderType"
                  value={formData.reminderType}
                  onChange={handleChange}
                >
                  <option value="none">No Reminder</option>
                  <option value="email">📧 Email</option>
                  <option value="sms">📱 SMS</option>
                  <option value="alarm">🔔 Browser Alarm</option>
                </select>
              </div>

              {formData.reminderType !== 'none' && (
                <div className="form-group">
                  <label htmlFor="reminderTime">
                    Reminder Time <span className="required">*</span>
                  </label>
                  <input
                    type="datetime-local"
                    id="reminderTime"
                    name="reminderTime"
                    value={formData.reminderTime}
                    onChange={handleChange}
                    className={validationErrors.reminderTime ? 'error' : ''}
                    min={new Date().toISOString().slice(0, 16)}
                  />
                  {validationErrors.reminderTime && (
                    <span className="error-text">{validationErrors.reminderTime}</span>
                  )}
                </div>
              )}
            </div>

            {formData.reminderType === 'sms' && (
              <div className="form-group">
                <label htmlFor="phoneNumber">
                  Phone Number <span className="required">*</span>
                </label>
                <input
                  type="tel"
                  id="phoneNumber"
                  name="phoneNumber"
                  value={formData.phoneNumber}
                  onChange={handleChange}
                  placeholder="+1 (555) 123-4567"
                  className={validationErrors.phoneNumber ? 'error' : ''}
                />
                {validationErrors.phoneNumber && (
                  <span className="error-text">{validationErrors.phoneNumber}</span>
                )}
                <small className="help-text">
                  Include country code (e.g., +1 for US)
                </small>
              </div>
            )}

            {formData.reminderType === 'email' && (
              <div className="reminder-info">
                <p>📧 Email reminders will be sent to your registered email address</p>
              </div>
            )}

            {formData.reminderType === 'alarm' && (
              <div className="reminder-info">
                <p>🔔 Browser notifications will appear when you have the app open</p>
              </div>
            )}
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
