import React, { useState } from 'react';
import TaskList from '../components/TaskList';
import TaskForm from '../components/TaskForm';
import { useTasks } from '../hooks/useTasks';
import '../styles/Dashboard.css';

const Dashboard = () => {
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingTask, setEditingTask] = useState(null);

  const {
    tasks,
    loading,
    error,
    filters,
    createTask,
    updateTask,
    deleteTask,
    toggleTask,
    updateFilters,
    clearError
  } = useTasks();

  const handleCreateTask = async (taskData) => {
    const result = await createTask(taskData);
    if (result.success) {
      setShowCreateForm(false);
    }
  };

  const handleUpdateTask = async (taskData) => {
    const result = await updateTask(editingTask.id, taskData);
    if (result.success) {
      setEditingTask(null);
    }
  };

  const handleEditTask = (task) => {
    setEditingTask(task);
  };

  const handleToggleTask = async (taskId) => {
    await toggleTask(taskId);
  };

  const handleDeleteTask = async (taskId) => {
    await deleteTask(taskId);
  };

  const handleCancelForm = () => {
    setShowCreateForm(false);
    setEditingTask(null);
    clearError();
  };

  if (showCreateForm) {
    return (
      <TaskForm
        title="Create New Task"
        onSubmit={handleCreateTask}
        onCancel={handleCancelForm}
        loading={loading}
      />
    );
  }

  if (editingTask) {
    return (
      <TaskForm
        title="Edit Task"
        task={editingTask}
        onSubmit={handleUpdateTask}
        onCancel={handleCancelForm}
        loading={loading}
      />
    );
  }

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <div className="header-content">
          <h1>My Tasks</h1>
          <button
            onClick={() => setShowCreateForm(true)}
            className="btn-primary btn-create"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
            </svg>
            Create Task
          </button>
        </div>
      </div>

      <div className="dashboard-content">
        {error && (
          <div className="error-banner">
            <span>{error}</span>
            <button onClick={clearError} className="btn-close">×</button>
          </div>
        )}
        
        <TaskList
          tasks={tasks}
          loading={loading}
          error={error}
          filters={filters}
          onFiltersChange={updateFilters}
          onToggleTask={handleToggleTask}
          onDeleteTask={handleDeleteTask}
          onEditTask={handleEditTask}
        />
      </div>
    </div>
  );
};

export default Dashboard;
