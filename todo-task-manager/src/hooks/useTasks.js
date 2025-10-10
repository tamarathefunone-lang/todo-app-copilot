import { useState, useEffect, useCallback } from 'react';
import { taskAPI } from '../services/api';

export const useTasks = (initialFilters = {}) => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState(initialFilters);

  // Fetch tasks with current filters
  const fetchTasks = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const response = await taskAPI.getTasks(filters);
      setTasks(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [filters]);

  // Create a new task
  const createTask = async (taskData) => {
    try {
      setLoading(true);
      setError('');
      const response = await taskAPI.createTask(taskData);
      setTasks(prev => [response.data, ...prev]);
      return { success: true, data: response.data };
    } catch (err) {
      setError(err.message);
      return { success: false, error: err.message };
    } finally {
      setLoading(false);
    }
  };

  // Update an existing task
  const updateTask = async (id, taskData) => {
    try {
      setLoading(true);
      setError('');
      const response = await taskAPI.updateTask(id, taskData);
      setTasks(prev => prev.map(task => 
        task.id === parseInt(id) ? response.data : task
      ));
      return { success: true, data: response.data };
    } catch (err) {
      setError(err.message);
      return { success: false, error: err.message };
    } finally {
      setLoading(false);
    }
  };

  // Delete a task
  const deleteTask = async (id) => {
    try {
      setLoading(true);
      setError('');
      await taskAPI.deleteTask(id);
      setTasks(prev => prev.filter(task => task.id !== parseInt(id)));
      return { success: true };
    } catch (err) {
      setError(err.message);
      return { success: false, error: err.message };
    } finally {
      setLoading(false);
    }
  };

  // Toggle task completion
  const toggleTask = async (id) => {
    try {
      setError('');
      const response = await taskAPI.toggleTask(id);
      setTasks(prev => prev.map(task => 
        task.id === parseInt(id) ? response.data : task
      ));
      return { success: true, data: response.data };
    } catch (err) {
      setError(err.message);
      return { success: false, error: err.message };
    }
  };

  // Update filters and refetch
  const updateFilters = (newFilters) => {
    setFilters(prev => ({ ...prev, ...newFilters }));
  };

  // Clear error
  const clearError = () => {
    setError('');
  };

  // Initial fetch
  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  return {
    tasks,
    loading,
    error,
    filters,
    createTask,
    updateTask,
    deleteTask,
    toggleTask,
    updateFilters,
    refetch: fetchTasks,
    clearError
  };
};

export const useTask = (id) => {
  const [task, setTask] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchTask = useCallback(async () => {
    if (!id) return;
    
    try {
      setLoading(true);
      setError('');
      const response = await taskAPI.getTask(id);
      setTask(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchTask();
  }, [fetchTask]);

  return {
    task,
    loading,
    error,
    refetch: fetchTask
  };
};
