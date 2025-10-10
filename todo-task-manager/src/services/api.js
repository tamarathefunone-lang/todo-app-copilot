import axios from 'axios';

// Base URL for the API - replace with your actual backend URL
const API_BASE_URL = 'http://localhost:3001/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user.token) {
      config.headers.Authorization = `Bearer ${user.token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Mock data for development (remove when backend is ready)
let mockTasks = [
  {
    id: 1,
    title: 'Complete project documentation',
    description: 'Write comprehensive documentation for the todo app',
    completed: false,
    priority: 'high',
    dueDate: '2025-10-15',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  },
  {
    id: 2,
    title: 'Review code changes',
    description: 'Review pull requests from team members',
    completed: true,
    priority: 'medium',
    dueDate: '2025-10-12',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  },
  {
    id: 3,
    title: 'Setup CI/CD pipeline',
    description: 'Configure automated testing and deployment',
    completed: false,
    priority: 'low',
    dueDate: '2025-10-20',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  },
];

// Simulate API delay
const simulateDelay = (ms = 500) => new Promise(resolve => setTimeout(resolve, ms));

// Task API functions
export const taskAPI = {
  // Get all tasks
  getTasks: async (filters = {}) => {
    await simulateDelay();
    
    try {
      // For now, return mock data. Replace with actual API call:
      // const response = await api.get('/tasks', { params: filters });
      
      let filteredTasks = [...mockTasks];
      
      if (filters.completed !== undefined) {
        filteredTasks = filteredTasks.filter(task => task.completed === filters.completed);
      }
      
      if (filters.priority) {
        filteredTasks = filteredTasks.filter(task => task.priority === filters.priority);
      }
      
      if (filters.search) {
        const searchLower = filters.search.toLowerCase();
        filteredTasks = filteredTasks.filter(task => 
          task.title.toLowerCase().includes(searchLower) ||
          task.description.toLowerCase().includes(searchLower)
        );
      }
      
      return {
        data: filteredTasks,
        total: filteredTasks.length
      };
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch tasks');
    }
  },

  // Get single task by ID
  getTask: async (id) => {
    await simulateDelay();
    
    try {
      // Replace with actual API call:
      // const response = await api.get(`/tasks/${id}`);
      
      const task = mockTasks.find(t => t.id === parseInt(id));
      if (!task) {
        throw new Error('Task not found');
      }
      
      return { data: task };
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch task');
    }
  },

  // Create new task
  createTask: async (taskData) => {
    await simulateDelay();
    
    try {
      // Replace with actual API call:
      // const response = await api.post('/tasks', taskData);
      
      const newTask = {
        id: Date.now(),
        ...taskData,
        completed: false,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };
      
      mockTasks.push(newTask);
      
      return { data: newTask };
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to create task');
    }
  },

  // Update existing task
  updateTask: async (id, taskData) => {
    await simulateDelay();
    
    try {
      // Replace with actual API call:
      // const response = await api.put(`/tasks/${id}`, taskData);
      
      const taskIndex = mockTasks.findIndex(t => t.id === parseInt(id));
      if (taskIndex === -1) {
        throw new Error('Task not found');
      }
      
      mockTasks[taskIndex] = {
        ...mockTasks[taskIndex],
        ...taskData,
        updatedAt: new Date().toISOString(),
      };
      
      return { data: mockTasks[taskIndex] };
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update task');
    }
  },

  // Delete task
  deleteTask: async (id) => {
    await simulateDelay();
    
    try {
      // Replace with actual API call:
      // const response = await api.delete(`/tasks/${id}`);
      
      const taskIndex = mockTasks.findIndex(t => t.id === parseInt(id));
      if (taskIndex === -1) {
        throw new Error('Task not found');
      }
      
      mockTasks.splice(taskIndex, 1);
      
      return { success: true };
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to delete task');
    }
  },

  // Toggle task completion
  toggleTask: async (id) => {
    await simulateDelay();
    
    try {
      const taskIndex = mockTasks.findIndex(t => t.id === parseInt(id));
      if (taskIndex === -1) {
        throw new Error('Task not found');
      }
      
      mockTasks[taskIndex].completed = !mockTasks[taskIndex].completed;
      mockTasks[taskIndex].updatedAt = new Date().toISOString();
      
      return { data: mockTasks[taskIndex] };
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to toggle task');
    }
  },
};

export default api;
