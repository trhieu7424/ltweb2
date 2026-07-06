import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Topbar from './Topbar';

const Layout = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);

    const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

    return (
        <div className="flex h-screen overflow-hidden bg-gray-50 font-sans">
            <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
            <div className="flex flex-col flex-1 w-full">
                <Topbar toggleSidebar={toggleSidebar} />
                <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8">
                    {/* Render component con (Dashboard) tại đây */}
                    <Outlet /> 
                </main>
            </div>
        </div>
    );
};

export default Layout;