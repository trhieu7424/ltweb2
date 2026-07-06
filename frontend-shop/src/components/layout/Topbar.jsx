import React from 'react';
import { LuMenu, LuSearch, LuMoon, LuBell, LuUser } from 'react-icons/lu';

const Topbar = ({ toggleSidebar }) => {
    return (
        <header className="flex items-center justify-between h-16 px-4 bg-white border-b border-gray-200 sm:px-6">
            <div className="flex items-center gap-4">
                <button onClick={toggleSidebar} className="text-gray-500 hover:text-gray-700 focus:outline-none lg:hidden">
                    <LuMenu className="w-6 h-6" />
                </button>
                <div className="relative hidden sm:block">
                    <LuSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                    <input 
                        type="text" 
                        placeholder="Search users, orders, reports..." 
                        className="pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500 w-64"
                    />
                </div>
            </div>

            <div className="flex items-center gap-4">
                <button className="text-gray-500 hover:text-gray-700">
                    <LuMoon className="w-5 h-5" />
                </button>
                
                <button className="relative text-gray-500 hover:text-gray-700">
                    <LuBell className="w-5 h-5" />
                    <span className="absolute top-0 right-0 block w-2 h-2 bg-red-500 rounded-full"></span>
                </button>

                <div className="relative cursor-pointer flex items-center gap-2">
                    <img src="https://i.pravatar.cc/150?img=11" alt="Profile" className="w-8 h-8 rounded-full" />
                </div>
            </div>
        </header>
    );
};

export default Topbar;