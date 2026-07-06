import React from 'react';
import { Link, useLocation } from 'react-router-dom'; // Thêm import này
import { LuLayoutDashboard, LuUsers, LuUserPlus, LuCircleUser, LuChartPie, LuTable, LuFileText, LuBox, LuBell, LuMaximize, LuSettings, LuFile, LuPackage } from 'react-icons/lu';

// Bổ sung thuộc tính 'path' cho mỗi menu
const menuItems = [
    { name: 'Dashboard', icon: LuLayoutDashboard, path: '/admin' },
    { name: 'Products', icon: LuPackage, path: '/admin/products' }, // Thêm tab Products
    { name: 'Users', icon: LuUsers, path: '/admin/users' },
    { name: 'Orders', icon: LuFileText, path: '/admin/orders' },
    { name: 'Coupons', icon: LuBell, path: '/admin/coupons' },
    { name: 'Settings', icon: LuSettings, path: '/admin/settings' },
];

const Sidebar = ({ isOpen, toggleSidebar }) => {
    const location = useLocation(); // Lấy đường dẫn hiện tại để bôi đậm menu tương ứng

    return (
        <>
            {isOpen && <div className="fixed inset-0 bg-black opacity-50 z-40 lg:hidden" onClick={toggleSidebar}></div>}
            
            <aside className={`fixed inset-y-0 left-0 z-50 w-64 bg-gray-900 text-gray-300 transition-transform duration-300 transform ${isOpen ? 'translate-x-0' : '-translate-x-full'} lg:relative lg:translate-x-0 flex flex-col h-screen`}>
                
                <div className="flex items-center justify-center h-16 border-b border-gray-800">
                    <span className="text-xl font-bold text-white flex items-center gap-2">
                        <LuBox className="text-blue-500" /> adminHMD
                    </span>
                </div>

                <div className="flex-1 overflow-y-auto py-4">
                    <ul className="space-y-1 px-3">
                        {menuItems.map((item, index) => {
                            const Icon = item.icon;
                            // Kiểm tra xem menu này có đang được chọn không
                            const isActive = location.pathname === item.path || (item.path !== '/admin' && location.pathname.startsWith(item.path));
                            
                            return (
                                <li key={index}>
                                    <Link 
                                        to={item.path} 
                                        className={`flex items-center gap-3 px-3 py-2 rounded-md transition-colors ${isActive ? 'bg-blue-600 text-white' : 'hover:bg-gray-800 hover:text-white'}`}
                                    >
                                        <Icon className="w-5 h-5" />
                                        {item.name}
                                    </Link>
                                </li>
                            );
                        })}
                    </ul>
                </div>

                <div className="border-t border-gray-800 p-4">
                    <div className="flex items-center gap-3 mb-4">
                        <img src="https://i.pravatar.cc/150?img=11" alt="Admin Hasan" className="w-10 h-10 rounded-full border-2 border-gray-600" />
                        <div>
                            <p className="text-sm font-semibold text-white">Admin Hasan</p>
                            <p className="text-xs text-gray-400">Active Workspace</p>
                        </div>
                    </div>
                    <div className="flex items-center gap-2 text-xs text-gray-400">
                        <span className="w-2 h-2 rounded-full bg-green-500 animate-pulse"></span>
                        System running smoothly
                    </div>
                </div>
            </aside>
        </>
    );
};

export default Sidebar;