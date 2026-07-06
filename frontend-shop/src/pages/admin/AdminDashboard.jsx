import React from 'react';
import { LuDownload, LuFilePlus } from 'react-icons/lu';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import StatCard from '../../components/dashboard/StatCard';
import { statCards, salesData, activities, recentUsers } from '../../data/dashboardData';

const AdminDashboard = () => {
    const getStatusBadge = (status) => {
        const styles = {
            Active: 'bg-green-100 text-green-700',
            Pending: 'bg-yellow-100 text-yellow-700',
            Suspended: 'bg-gray-100 text-gray-700',
        };
        return <span className={`px-2 py-1 text-xs font-medium rounded-full ${styles[status]}`}>{status}</span>;
    };

    return (
        <div className="space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
                    <p className="text-sm text-gray-500">Welcome back, monitor your business performance here.</p>
                </div>
                <div className="flex gap-3">
                    <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
                        <LuDownload className="w-4 h-4" /> Export
                    </button>
                    <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700">
                        <LuFilePlus className="w-4 h-4" /> Create Report
                    </button>
                </div>
            </div>

            {/* Metric Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6">
                {statCards.map(stat => (
                    <StatCard key={stat.id} title={stat.title} value={stat.value} change={stat.change} isUp={stat.isUp} color={stat.color} bg={stat.bg} iconName={stat.icon} />
                ))}
            </div>

            {/* Charts & Activity */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Sales Performance */}
                <div className="lg:col-span-2 bg-white p-6 rounded-lg shadow-sm border border-gray-100">
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="text-lg font-bold text-gray-900">Sales Performance</h2>
                        <button className="text-sm text-blue-600 hover:underline">View Details</button>
                    </div>
                    <div className="h-72">
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={salesData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E5E7EB" />
                                <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: '#6B7280' }} dy={10} />
                                <YAxis axisLine={false} tickLine={false} tick={{ fill: '#6B7280' }} />
                                <Tooltip cursor={{ fill: '#F3F4F6' }} contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }} />
                                <Bar dataKey="sales" fill="#3B82F6" radius={[4, 4, 0, 0]} barSize={40} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Team Activity */}
                <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100">
                    <h2 className="text-lg font-bold text-gray-900 mb-6">Team Activity</h2>
                    <div className="relative border-l border-gray-200 ml-3 space-y-6">
                        {activities.map(activity => (
                            <div key={activity.id} className="relative pl-6">
                                <span className={`absolute -left-1.5 top-1.5 w-3 h-3 rounded-full border-2 border-white ${activity.color}`}></span>
                                <p className="text-sm text-gray-800">{activity.text}</p>
                                <p className="text-xs text-gray-400 mt-1">{activity.time}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Data Table */}
            <div className="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
                <div className="flex items-center justify-between p-6 border-b border-gray-100">
                    <h2 className="text-lg font-bold text-gray-900">Recent Users</h2>
                    <button className="text-sm text-blue-600 hover:underline">Manage Users</button>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left text-gray-500">
                        <thead className="text-xs text-gray-700 uppercase bg-gray-50 border-b border-gray-100">
                            <tr>
                                <th className="px-6 py-4 font-medium">User</th>
                                <th className="px-6 py-4 font-medium">Role</th>
                                <th className="px-6 py-4 font-medium">Team</th>
                                <th className="px-6 py-4 font-medium">Status</th>
                                <th className="px-6 py-4 font-medium">Joined</th>
                                <th className="px-6 py-4 font-medium text-right">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {recentUsers.map(user => (
                                <tr key={user.id} className="border-b border-gray-50 hover:bg-gray-50">
                                    <td className="px-6 py-4 flex items-center gap-3">
                                        <img src={`https://i.pravatar.cc/150?u=${user.email}`} alt={user.name} className="w-10 h-10 rounded-full" />
                                        <div>
                                            <p className="font-medium text-gray-900">{user.name}</p>
                                            <p className="text-gray-400">{user.email}</p>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">{user.role}</td>
                                    <td className="px-6 py-4">{user.team}</td>
                                    <td className="px-6 py-4">{getStatusBadge(user.status)}</td>
                                    <td className="px-6 py-4">{user.joined}</td>
                                    <td className="px-6 py-4 text-right">
                                        <button className="font-medium text-blue-600 hover:underline">View</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;