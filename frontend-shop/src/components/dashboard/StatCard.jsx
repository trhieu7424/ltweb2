import React from 'react';
import * as Icons from 'react-icons/lu';

const StatCard = ({ title, value, change, isUp, color, bg, iconName }) => {
    const Icon = Icons[iconName];

    return (
        <div className="p-6 bg-white rounded-lg shadow-sm border border-gray-100">
            <div className="flex items-center justify-between">
                <div>
                    <p className="text-sm font-medium text-gray-500">{title}</p>
                    <p className="text-2xl font-bold text-gray-900 mt-1">{value}</p>
                </div>
                <div className={`p-3 rounded-full ${bg}`}>
                    <Icon className={`w-6 h-6 ${color}`} />
                </div>
            </div>
            <div className="mt-4 flex items-center gap-2 text-sm">
                <span className={isUp ? 'text-green-600' : 'text-red-600'}>{change}</span>
                <span className="text-gray-400">from last month</span>
            </div>
        </div>
    );
};

export default StatCard;