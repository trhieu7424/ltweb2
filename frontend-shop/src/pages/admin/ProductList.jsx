import React, { useEffect, useState } from 'react';
import { LuPlus, LuPencil, LuTrash2 } from 'react-icons/lu';
import axiosClient from '../../api/axiosClient';
import ProductModal from './ProductModal'; // Import Component vừa tạo

const ProductList = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // State quản lý Modal
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState(null);

    useEffect(() => {
        fetchProducts();
    }, []);

const fetchProducts = async () => {
        try {
            const response = await axiosClient.get('/products');
            
            // LỚP BẢO VỆ: Đảm bảo dữ liệu chắc chắn là Mảng (Array) trước khi set
            if (Array.isArray(response.data)) {
                setProducts(response.data);
            } else {
                console.warn("Dữ liệu trả về bị lỗi định dạng:", response.data);
                setProducts([]); 
            }
            
        } catch (error) {
            console.error("Lỗi khi tải dữ liệu:", error);
            setProducts([]); // Nếu mất mạng hoặc lỗi server, gán mảng rỗng để web không sập
        } finally {
            setLoading(false);
        }
    };

    // Mở modal Thêm Mới
    const handleAddClick = () => {
        setSelectedProduct(null); // Null nghĩa là đang thêm mới
        setIsModalOpen(true);
    };

    // Mở modal Sửa
    const handleEditClick = (product) => {
        setSelectedProduct(product); // Có dữ liệu nghĩa là đang sửa
        setIsModalOpen(true);
    };

// Xử lý khi nhấn nút Lưu trong Modal
    const handleSaveProduct = async (formData) => {
        try {
            // 1. Khởi tạo đối tượng FormData để chứa cả file và text
            const submitData = new FormData();

            // 2. Gắn file ảnh vào biến 'file' (khớp với @RequestPart("file"))
            if (formData.mainImageFile) {
                submitData.append('file', formData.mainImageFile);
            }

            // 3. Cấu trúc lại dữ liệu text để khớp với cấu trúc Entity dưới Backend
            const productInfo = {
                name: formData.name,
                description: formData.description,
                category: {
                    id: parseInt(formData.categoryId) // Ánh xạ đúng vào đối tượng Category
                },
                variants: formData.variants // Gửi kèm mảng thông số kỹ thuật
            };

            // 4. Ép kiểu JSON và gắn vào biến 'data' (khớp với @RequestPart("data"))
            submitData.append('data', JSON.stringify(productInfo));

            if (selectedProduct) {
                // Gọi API Sửa 
                await axiosClient.post(`/products/${selectedProduct.id}`, submitData);
                alert("Cập nhật sản phẩm thành công!");
            } else {
                // Gọi API Thêm mới
                await axiosClient.post('/products', submitData);
                alert("Thêm sản phẩm thành công!");
            }

            // 6. Đóng form và tải lại bảng danh sách
            setIsModalOpen(false);
            fetchProducts();

        } catch (error) {
            console.error("Lỗi hệ thống khi lưu sản phẩm:", error);
            const errorMsg = error.response?.data?.error || error.response?.data?.message || "Lỗi không xác định";
            alert("Có lỗi xảy ra: " + errorMsg);
        }
    };

    // Xử lý khi bấm nút Xóa
    const handleDeleteClick = async (id) => {
        if (window.confirm("Bạn có chắc chắn muốn xóa vĩnh viễn sản phẩm này không?")) {
            try {
                await axiosClient.delete(`/products/${id}`);
                alert("Đã xóa sản phẩm!");
                fetchProducts();
            } catch (error) {
                console.error("Lỗi xóa sản phẩm:", error);
                alert("Không thể xóa: " + (error.response?.data?.error || "Lỗi hệ thống"));
            }
        }
    };
    const handleRestoreClick = async (id) => {
        try {
            await axiosClient.put(`/products/${id}/restore`);
            alert("Sản phẩm đã được khôi phục!");
            fetchProducts();
        } catch (error) {
            alert("Lỗi khôi phục: " + (error.response?.data?.error || "Lỗi hệ thống"));
        }
    };

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Products</h1>
                    <p className="text-sm text-gray-500">Quản lý toàn bộ sản phẩm trong hệ thống.</p>
                </div>
                <button 
                    onClick={handleAddClick} 
                    className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors"
                >
                    <LuPlus className="w-5 h-5" /> Thêm Sản Phẩm Mới
                </button>
            </div>

            {/* Bảng dữ liệu */}
            <div className="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left text-gray-500">
                        <thead className="text-xs text-gray-700 uppercase bg-gray-50 border-b border-gray-100">
                            <tr>
                                <th className="px-6 py-4 font-medium">ID</th>
                                <th className="px-6 py-4 font-medium">Tên Sản Phẩm</th>
                                <th className="px-6 py-4 font-medium">Danh Mục</th>
                                <th className="px-6 py-4 font-medium text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr><td colSpan="4" className="px-6 py-8 text-center">Đang tải...</td></tr>
                            ) : (
                                products.map((item) => (
                                    <tr 
                                        key={item.id} 
                                        className={`border-b transition-colors ${item.deleted ? 'bg-gray-100 opacity-50' : 'border-gray-50 hover:bg-gray-50'}`}
                                    >
                                        <td className="px-6 py-4 font-medium text-gray-900">{item.id}</td>
                                        <td className="px-6 py-4 font-medium text-gray-900">
                                            {item.name} 
                                            {item.deleted && <span className="ml-2 text-xs text-red-500 font-bold">(Đã xóa)</span>}
                                        </td>
                                        <td className="px-6 py-4">{item.categoryName}</td>
                                        <td className="px-6 py-4 flex items-center justify-center gap-3">
                                            
                                            {/* Chỉ hiện nút Sửa nếu CHƯA xóa */}
                                            {!item.deleted && (
                                                <button onClick={() => handleEditClick(item)} className="text-gray-400 hover:text-blue-600 transition-colors">
                                                    <LuPencil className="w-5 h-5" />
                                                </button>
                                            )}

                                            {/* Hiển thị Nút Khôi Phục hoặc Nút Xóa dựa vào trạng thái */}
                                            {item.deleted ? (
                                                <button onClick={() => handleRestoreClick(item.id)} className="px-3 py-1 text-xs font-medium text-white bg-green-500 rounded hover:bg-green-600">
                                                    Khôi phục
                                                </button>
                                            ) : (
                                                <button onClick={() => handleDeleteClick(item.id)} className="text-gray-400 hover:text-red-600 transition-colors">
                                                    <LuTrash2 className="w-5 h-5" />
                                                </button>
                                            )}

                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Nhúng Modal Component vào đây */}
            <ProductModal 
                isOpen={isModalOpen} 
                onClose={() => setIsModalOpen(false)} 
                productToEdit={selectedProduct}
                onSave={handleSaveProduct}
            />
        </div>
    );
};

export default ProductList;