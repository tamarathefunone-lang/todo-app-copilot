import React, { useState } from 'react';
import TaskItem from './TaskItem';
import '../styles/TaskList.css';

const TaskList = ({ 
  tasks, 
  loading, 
  error, 
  onToggleTask, 
  onDeleteTask, 
  onEditTask,
  filters,
  onFiltersChange 
}) => {
  const [searchTerm, setSearchTerm] = useState(filters.search || '');
  const [sortBy, setSortBy] = useState('createdAt');
  const [sortOrder, setSortOrder] = useState('desc');

  const handleFilterChange = (filterType, value) => {
    onFiltersChange({
      ...filters,
      [filterType]: value
    });
  };

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchTerm(value);
    
    // Debounce search
    setTimeout(() => {
      handleFilterChange('search', value);
    }, 300);
  };

  const handleSortChange = (field) => {
    if (sortBy === field) {
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setSortOrder('asc');
    }
  };

  const sortTasks = (tasks) => {
    return [...tasks].sort((a, b) => {
      let aValue = a[sortBy];
      let bValue = b[sortBy];
      
      // Handle different data types
      if (sortBy === 'createdAt' || sortBy === 'updatedAt' || sortBy === 'dueDate') {
        aValue = new Date(aValue || 0);
        bValue = new Date(bValue || 0);
      } else if (typeof aValue === 'string') {
        aValue = aValue.toLowerCase();
        bValue = bValue.toLowerCase();
      }
      
      if (sortOrder === 'asc') {
        return aValue > bValue ? 1 : -1;
      } else {
        return aValue < bValue ? 1 : -1;
      }
    });
  };

  const getFilterButtonClass = (filterValue, currentValue) => {
    return `filter-btn ${filterValue === currentValue ? 'active' : ''}`;
  };

  const getTaskStats = () => {
    const total = tasks.length;
    const completed = tasks.filter(task => task.completed).length;
    const pending = total - completed;
    const overdue = tasks.filter(task => 
      !task.completed && 
      task.dueDate && 
      new Date(task.dueDate) < new Date().setHours(0, 0, 0, 0)
    ).length;

    return { total, completed, pending, overdue };
  };

  const stats = getTaskStats();
  const sortedTasks = sortTasks(tasks);

  if (loading) {
    return (
      <div className="task-list-container">
        <div className="loading-state">
          <div className="loading-spinner"></div>
          <p>Loading tasks...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="task-list-container">
        <div className="error-state">
          <p>Error loading tasks: {error}</p>
          <button onClick={() => window.location.reload()} className="btn-primary">
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="task-list-container">
      {/* Task Statistics */}
      <div className="task-stats">
        <div className="stat-item">
          <span className="stat-number">{stats.total}</span>
          <span className="stat-label">Total</span>
        </div>
        <div className="stat-item">
          <span className="stat-number">{stats.pending}</span>
          <span className="stat-label">Pending</span>
        </div>
        <div className="stat-item">
          <span className="stat-number">{stats.completed}</span>
          <span className="stat-label">Completed</span>
        </div>
        <div className="stat-item">
          <span className="stat-number text-danger">{stats.overdue}</span>
          <span className="stat-label">Overdue</span>
        </div>
      </div>

      {/* Filters and Search */}
      <div className="task-filters">
        <div className="search-box">
          <svg className="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
          </svg>
          <input
            type="text"
            placeholder="Search tasks..."
            value={searchTerm}
            onChange={handleSearchChange}
            className="search-input"
          />
        </div>

        <div className="filter-buttons">
          <button
            onClick={() => handleFilterChange('completed', undefined)}
            className={getFilterButtonClass(filters.completed, undefined)}
          >
            All
          </button>
          <button
            onClick={() => handleFilterChange('completed', false)}
            className={getFilterButtonClass(filters.completed, false)}
          >
            Pending
          </button>
          <button
            onClick={() => handleFilterChange('completed', true)}
            className={getFilterButtonClass(filters.completed, true)}
          >
            Completed
          </button>
        </div>

        <div className="priority-filters">
          <select
            value={filters.priority || ''}
            onChange={(e) => handleFilterChange('priority', e.target.value || undefined)}
            className="priority-select"
          >
            <option value="">All Priorities</option>
            <option value="high">High Priority</option>
            <option value="medium">Medium Priority</option>
            <option value="low">Low Priority</option>
          </select>
        </div>
      </div>

      {/* Sort Options */}
      <div className="sort-options">
        <span>Sort by:</span>
        <button
          onClick={() => handleSortChange('createdAt')}
          className={`sort-btn ${sortBy === 'createdAt' ? 'active' : ''}`}
        >
          Date Created
          {sortBy === 'createdAt' && (
            <span className="sort-arrow">{sortOrder === 'asc' ? '↑' : '↓'}</span>
          )}
        </button>
        <button
          onClick={() => handleSortChange('dueDate')}
          className={`sort-btn ${sortBy === 'dueDate' ? 'active' : ''}`}
        >
          Due Date
          {sortBy === 'dueDate' && (
            <span className="sort-arrow">{sortOrder === 'asc' ? '↑' : '↓'}</span>
          )}
        </button>
        <button
          onClick={() => handleSortChange('priority')}
          className={`sort-btn ${sortBy === 'priority' ? 'active' : ''}`}
        >
          Priority
          {sortBy === 'priority' && (
            <span className="sort-arrow">{sortOrder === 'asc' ? '↑' : '↓'}</span>
          )}
        </button>
        <button
          onClick={() => handleSortChange('title')}
          className={`sort-btn ${sortBy === 'title' ? 'active' : ''}`}
        >
          Title
          {sortBy === 'title' && (
            <span className="sort-arrow">{sortOrder === 'asc' ? '↑' : '↓'}</span>
          )}
        </button>
      </div>

      {/* Task List */}
      <div className="task-list">
        {sortedTasks.length === 0 ? (
          <div className="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor" className="empty-icon">
              <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
            </svg>
            <h3>No tasks found</h3>
            <p>
              {filters.search || filters.completed !== undefined || filters.priority
                ? 'Try adjusting your filters or search terms.'
                : 'Get started by creating your first task!'}
            </p>
          </div>
        ) : (
          sortedTasks.map(task => (
            <TaskItem
              key={task.id}
              task={task}
              onToggle={onToggleTask}
              onDelete={onDeleteTask}
              onEdit={onEditTask}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default TaskList;
