import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/shop/Login';
import AdminDashboard from './pages/admin/AdminDashboard';
import Layout from './components/layout/Layout'; 
import ProductList from './pages/admin/ProductList';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        
        <Route path="/admin" element={<Layout />}>
            <Route index element={<AdminDashboard />} />
            <Route path="products" element={<ProductList />} /> 

        </Route>
        
      </Routes>
    </Router>
  );
}

export default App;