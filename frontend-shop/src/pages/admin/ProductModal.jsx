import React, { useState, useEffect } from 'react';
import { LuX, LuUpload, LuPlus, LuTrash2 } from 'react-icons/lu';

const ProductModal = ({ isOpen, onClose, productToEdit, onSave }) => {
    // State lưu trữ dữ liệu form
    const [formData, setFormData] = useState({
        name: '',
        categoryId: '',
        description: '',
    });

    // State riêng cho File Upload
    const [mainImageFile, setMainImageFile] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);

    // State quản lý danh sách các phiên bản (Variants)
    const [variants, setVariants] = useState([
        { color: '', ram: '', rom: '', price: '', sku: '' }
    ]);

    // Nếu đang là chế độ Sửa (Edit), nạp dữ liệu cũ vào form
    useEffect(() => {
        if (productToEdit) {
            setFormData({
                name: productToEdit.name,
                categoryId: productToEdit.categoryId || '',
                description: productToEdit.description,
            });
            // Giả lập preview ảnh cũ nếu có
            setImagePreview(productToEdit.mainImage || null);
            // Nếu API có trả về variants thì nạp vào, không thì để mảng rỗng
            setVariants(productToEdit.variants?.length > 0 ? productToEdit.variants : []);
        } else {
            // Reset form khi Thêm mới
            setFormData({ name: '', categoryId: '', description: '' });
            setMainImageFile(null);
            setImagePreview(null);
            setVariants([{ color: '', ram: '', rom: '', price: '', sku: '' }]);
        }
    }, [productToEdit, isOpen]);

    // Xử lý khi chọn file ảnh từ máy tính
    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setMainImageFile(file);
            // Tạo URL tạm thời để hiển thị preview ảnh ngay lập tức
            setImagePreview(URL.createObjectURL(file));
        }
    };

    // Xử lý thay đổi dữ liệu của từng phiên bản (Variant)
    const handleVariantChange = (index, field, value) => {
        const updatedVariants = [...variants];
        updatedVariants[index][field] = value;
        setVariants(updatedVariants);
    };

    const addVariantRow = () => {
        setVariants([...variants, { color: '', ram: '', rom: '', price: '', sku: '' }]);
    };

    const removeVariantRow = (index) => {
        const updatedVariants = variants.filter((_, i) => i !== index);
        setVariants(updatedVariants);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Gọi hàm onSave truyền từ file cha, kèm theo toàn bộ dữ liệu
        onSave({ ...formData, mainImageFile, variants });
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white rounded-lg w-full max-w-4xl max-h-[90vh] overflow-y-auto shadow-xl">
                
                {/* Modal Header */}
                <div className="flex items-center justify-between p-6 border-b border-gray-100 sticky top-0 bg-white z-10">
                    <h2 className="text-xl font-bold text-gray-900">
                        {productToEdit ? 'Chỉnh Sửa Sản Phẩm' : 'Thêm Sản Phẩm Mới'}
                    </h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
                        <LuX className="w-6 h-6" />
                    </button>
                </div>

                {/* Modal Body - Form */}
                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                    
                    {/* Phần 1: Thông tin cơ bản & Ảnh */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        
                        {/* Cột Tải Ảnh */}
                        <div className="col-span-1 space-y-2">
                            <label className="block text-sm font-medium text-gray-700">Ảnh đại diện</label>
                            <div className="flex flex-col items-center justify-center w-full h-48 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer hover:bg-gray-50 overflow-hidden relative">
                                {imagePreview ? (
                                    <img src={imagePreview} alt="Preview" className="w-full h-full object-cover" />
                                ) : (
                                    <div className="flex flex-col items-center justify-center pt-5 pb-6">
                                        <LuUpload className="w-8 h-8 text-gray-400 mb-2" />
                                        <p className="text-sm text-gray-500">Click để chọn file</p>
                                    </div>
                                )}
                                <input type="file" className="absolute inset-0 w-full h-full opacity-0 cursor-pointer" accept="image/*" onChange={handleImageChange} />
                            </div>
                        </div>

                        {/* Cột Text Input */}
                        <div className="col-span-2 space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Tên Sản Phẩm</label>
                                <input type="text" required value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" placeholder="VD: iPhone 15 Pro Max" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Danh mục (ID)</label>
                                <input type="number" required value={formData.categoryId} onChange={(e) => setFormData({...formData, categoryId: e.target.value})} className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" placeholder="VD: 1" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả ngắn</label>
                                <textarea rows="2" value={formData.description} onChange={(e) => setFormData({...formData, description: e.target.value})} className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" placeholder="Nhập mô tả sản phẩm..."></textarea>
                            </div>
                        </div>
                    </div>

                    {/* Phần 2: Các phiên bản (Variants) */}
                    <div className="pt-4 border-t border-gray-100">
                        <div className="flex items-center justify-between mb-4">
                            <h3 className="text-lg font-bold text-gray-900">Phân loại hàng (Variants)</h3>
                            <button type="button" onClick={addVariantRow} className="flex items-center gap-1 text-sm font-medium text-blue-600 hover:text-blue-700">
                                <LuPlus className="w-4 h-4" /> Thêm phiên bản
                            </button>
                        </div>
                        
                        <div className="space-y-3">
                            {variants.map((variant, index) => (
                                <div key={index} className="flex items-end gap-3 p-3 bg-gray-50 rounded-lg border border-gray-200">
                                    <div className="flex-1 grid grid-cols-5 gap-3">
                                        <div>
                                            <label className="block text-xs text-gray-500 mb-1">Màu sắc</label>
                                            <input type="text" value={variant.color} onChange={(e) => handleVariantChange(index, 'color', e.target.value)} className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:ring-blue-500" placeholder="VD: Titan" />
                                        </div>
                                        <div>
                                            <label className="block text-xs text-gray-500 mb-1">RAM</label>
                                            <input type="text" value={variant.ram} onChange={(e) => handleVariantChange(index, 'ram', e.target.value)} className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:ring-blue-500" placeholder="VD: 8GB" />
                                        </div>
                                        <div>
                                            <label className="block text-xs text-gray-500 mb-1">ROM</label>
                                            <input type="text" value={variant.rom} onChange={(e) => handleVariantChange(index, 'rom', e.target.value)} className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:ring-blue-500" placeholder="VD: 256GB" />
                                        </div>
                                        <div>
                                            <label className="block text-xs text-gray-500 mb-1">Giá bán</label>
                                            <input type="number" value={variant.price} onChange={(e) => handleVariantChange(index, 'price', e.target.value)} className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:ring-blue-500" placeholder="VNĐ" />
                                        </div>
                                        <div>
                                            <label className="block text-xs text-gray-500 mb-1">Mã SKU</label>
                                            <input type="text" value={variant.sku} onChange={(e) => handleVariantChange(index, 'sku', e.target.value)} className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:ring-blue-500" placeholder="VD: IP15-256-TT" />
                                        </div>
                                    </div>
                                    {variants.length > 1 && (
                                        <button type="button" onClick={() => removeVariantRow(index)} className="p-2 text-red-500 hover:bg-red-50 rounded-md transition-colors">
                                            <LuTrash2 className="w-5 h-5" />
                                        </button>
                                    )}
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Modal Footer */}
                    <div className="flex justify-end gap-3 pt-4 border-t border-gray-100">
                        <button type="button" onClick={onClose} className="px-5 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
                            Hủy bỏ
                        </button>
                        <button type="submit" className="px-5 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700">
                            Lưu Sản Phẩm
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ProductModal;