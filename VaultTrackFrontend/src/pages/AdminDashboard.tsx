import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';
import authService from '../services/authService';
import Header from '../components/Header';
import { AxiosError } from 'axios';

interface User {
    userId: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    role: 'USER' | 'ADMIN';
    createdAt: string;
    updatedAt: string;
}

interface ErrorResponse {
    message?: string;
}

function AdminDashboard() {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [emailSearch, setEmailSearch] = useState('');
    const [roleFilter, setRoleFilter] = useState<string>('');
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [newRole, setNewRole] = useState<'USER' | 'ADMIN'>('USER');
    const [updateLoading, setUpdateLoading] = useState(false);
    const [updateError, setUpdateError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        // Check if user is admin
        if (!authService.isAdmin()) {
            navigate('/');
            return;
        }

        fetchUsers();
    }, [navigate]);

    const fetchUsers = async (email: string = '', role: string = '') => {
        try {
            setLoading(true);
            setError(null);

            // Build query params
            const params = new URLSearchParams();
            if (email) params.append('email', email);
            if (role) params.append('role', role);

            const queryString = params.toString();
            const endpoint = queryString ? `/admin?${queryString}` : '/admin';

            const response = await apiClient.get<User[]>(endpoint);
            setUsers(response.data);
        } catch (err) {
            const axiosError = err as AxiosError<ErrorResponse>;
            setError(axiosError.response?.data?.message || 'Failed to fetch users');
            console.error('Error fetching users:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = () => {
        fetchUsers(emailSearch, roleFilter);
    };

    const handleReset = () => {
        setEmailSearch('');
        setRoleFilter('');
        fetchUsers('', '');
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    const openEditModal = (user: User) => {
        setSelectedUser(user);
        setNewRole(user.role);
        setUpdateError(null);
        setIsEditModalOpen(true);
    };

    const closeEditModal = () => {
        setIsEditModalOpen(false);
        setSelectedUser(null);
        setUpdateError(null);
    };

    const handleUpdateRole = async () => {
        if (!selectedUser) return;

        try {
            setUpdateLoading(true);
            setUpdateError(null);

            await apiClient.patch(`/admin/${selectedUser.userId}?role=${newRole}`);

            // Refresh the users list
            await fetchUsers(emailSearch, roleFilter);

            closeEditModal();
        } catch (err) {
            const axiosError = err as AxiosError<ErrorResponse>;
            setUpdateError(axiosError.response?.data?.message || 'Failed to update user role');
            console.error('Error updating user role:', err);
        } finally {
            setUpdateLoading(false);
        }
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getRoleBadgeColor = (role: string) => {
        return role === 'ADMIN'
            ? 'bg-purple-100 text-purple-800'
            : 'bg-blue-100 text-blue-800';
    };

    if (loading && users.length === 0) {
        return (
            <>
                <Header />
                <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                    <div className="text-center">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                        <p className="mt-4 text-gray-600">Loading users...</p>
                    </div>
                </div>
            </>
        );
    }

    return (
        <>
            <Header />
            <div className="min-h-screen bg-gray-50 py-8">
                <div className="container mx-auto px-4">
                    <div className="mb-8">
                        <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
                        <p className="text-gray-600 mt-2">Manage all users in the system</p>
                    </div>

                    {error && (
                        <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
                            {error}
                        </div>
                    )}

                    {/* Search and Filter Section */}
                    <div className="bg-white rounded-lg shadow p-6 mb-6">
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            {/* Email Search */}
                            <div>
                                <label htmlFor="email-search" className="block text-sm font-medium text-gray-700 mb-2">
                                    Search by Email
                                </label>
                                <input
                                    id="email-search"
                                    type="text"
                                    placeholder="Enter email..."
                                    value={emailSearch}
                                    onChange={(e) => setEmailSearch(e.target.value)}
                                    onKeyPress={handleKeyPress}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                />
                            </div>

                            {/* Role Filter */}
                            <div>
                                <label htmlFor="role-filter" className="block text-sm font-medium text-gray-700 mb-2">
                                    Filter by Role
                                </label>
                                <select
                                    id="role-filter"
                                    value={roleFilter}
                                    onChange={(e) => setRoleFilter(e.target.value)}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                >
                                    <option value="">All Roles</option>
                                    <option value="USER">USER</option>
                                    <option value="ADMIN">ADMIN</option>
                                </select>
                            </div>

                            {/* Action Buttons */}
                            <div className="flex items-end gap-2">
                                <button
                                    onClick={handleSearch}
                                    disabled={loading}
                                    className="flex-1 px-6 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
                                >
                                    {loading ? 'Searching...' : 'Search'}
                                </button>
                                <button
                                    onClick={handleReset}
                                    disabled={loading}
                                    className="flex-1 px-6 py-2 bg-gray-600 text-white rounded-lg font-medium hover:bg-gray-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
                                >
                                    Reset
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Users Table */}
                    {/* Users Table */}
                    <div className="bg-white rounded-lg shadow overflow-hidden">
                        <div className="px-6 py-4 border-b border-gray-200 bg-gray-50">
                            <h2 className="text-lg font-semibold text-gray-900">
                                All Users ({users.length})
                            </h2>
                        </div>

                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        User ID
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Name
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Email
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Role
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Created At
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Updated At
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Actions
                                    </th>
                                </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                {users.length === 0 ? (
                                    <tr>
                                        <td colSpan={7} className="px-6 py-8 text-center text-gray-500">
                                            No users found
                                        </td>
                                    </tr>
                                ) : (
                                    users.map((user) => (
                                        <tr key={user.userId} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="text-xs text-gray-900 font-mono max-w-[150px] truncate" title={user.userId}>
                                                    {user.userId}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="text-sm font-medium text-gray-900">
                                                    {user.firstName} {user.lastName}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="text-sm text-gray-900">{user.email}</div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getRoleBadgeColor(user.role)}`}>
                  {user.role}
                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {formatDate(user.createdAt)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {formatDate(user.updatedAt)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                                <button
                                                    onClick={() => openEditModal(user)}
                                                    className="text-blue-600 hover:text-blue-900"
                                                >
                                                    Edit
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            {/* Edit Role Modal */}
            {isEditModalOpen && selectedUser && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
                        <h2 className="text-2xl font-bold text-gray-900 mb-4">Edit User Role</h2>

                        <div className="mb-4">
                            <p className="text-sm text-gray-600 mb-2">
                                <span className="font-medium">User:</span> {selectedUser.firstName} {selectedUser.lastName}
                            </p>
                            <p className="text-sm text-gray-600 mb-4">
                                <span className="font-medium">Email:</span> {selectedUser.email}
                            </p>
                            <p className="text-sm text-gray-600 mb-2">
                                <span className="font-medium">Current Role:</span>{' '}
                                <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getRoleBadgeColor(selectedUser.role)}`}>
                  {selectedUser.role}
                </span>
                            </p>
                        </div>

                        {updateError && (
                            <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
                                {updateError}
                            </div>
                        )}

                        <div className="mb-6">
                            <label htmlFor="new-role" className="block text-sm font-medium text-gray-700 mb-2">
                                New Role
                            </label>
                            <select
                                id="new-role"
                                value={newRole}
                                onChange={(e) => setNewRole(e.target.value as 'USER' | 'ADMIN')}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            >
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </div>

                        <div className="flex gap-3">
                            <button
                                onClick={closeEditModal}
                                disabled={updateLoading}
                                className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg font-medium hover:bg-gray-50 transition-colors disabled:bg-gray-100 disabled:cursor-not-allowed"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleUpdateRole}
                                disabled={updateLoading || newRole === selectedUser.role}
                                className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
                            >
                                {updateLoading ? 'Updating...' : 'Update Role'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default AdminDashboard;