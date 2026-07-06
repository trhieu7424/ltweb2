import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosClient from '../../api/axiosClient'; 

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axiosClient.post('/auth/login', {
                email,
                password
            });

            localStorage.setItem('accessToken', response.data.accessToken);
            navigate('/admin'); 

        } catch (error) {
            if (error.response && error.response.status === 401) {
                setMessage('Sai email hoặc mật khẩu!');
            } else {
                setMessage('Lỗi kết nối đến server!');
            }
        }
    };

    return (
        <div style={{ padding: '50px', maxWidth: '400px', margin: '0 auto', fontFamily: 'sans-serif' }}>
            <h2>Đăng Nhập</h2>
            <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <input 
                    type="email" 
                    placeholder="Email" 
                    value={email} 
                    onChange={(e) => setEmail(e.target.value)} 
                    required 
                    style={{ padding: '10px' }}
                />
                <input 
                    type="password" 
                    placeholder="Mật khẩu" 
                    value={password} 
                    onChange={(e) => setPassword(e.target.value)} 
                    required 
                    style={{ padding: '10px' }}
                />
                <button type="submit" style={{ padding: '10px', backgroundColor: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}>
                    Đăng nhập
                </button>
            </form>
            {message && <p style={{ marginTop: '20px', color: 'red' }}>{message}</p>}
        </div>
    );
};

export default Login;