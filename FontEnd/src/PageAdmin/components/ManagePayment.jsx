import React, { useState, useEffect, useContext } from "react";
import {
    getPayments,
    createPayment,
    updatePayment,
    deletePayment,
    getDetailPayment
} from "@/apis/paymentService";
import { ToastContext } from "@/context/ToastProvider";
import { Modal, Button, Form } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";

const ManagePayment = () => {
    const [payments, setPayments] = useState([]);
    const [detailPayments, setDetailPayments] = useState([]);
    const [paymentData, setPaymentData] = useState({
        name: "",
        userId: "",
        bookingId: "",
        method: "cash",
        paymentStatus: "pending",
    });
    const [editingId, setEditingId] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [isAdding, setIsAdding] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [deleteId, setDeleteId] = useState(null);
    const { toast } = useContext(ToastContext);
    const [totalRevenue, setTotalRevenue] = useState(0);

    useEffect(() => {
        fetchPayments();
        fetchDetailPayment();
    }, []);

    const fetchDetailPayment = async () => {
        try {
            const res = await getDetailPayment();
            const data = res.data || [];
            setDetailPayments(data);
            calculateTotalRevenue(data);
        } catch (error) {
            console.error("Lỗi lấy chi tiết payment:", error);
            toast.error("Lỗi tải chi tiết payment");
        }
    };

    const fetchPayments = async () => {
        try {
            const response = await getPayments();
            const paymentsData = response.data || [];

            if (!Array.isArray(paymentsData)) {
                console.error("Dữ liệu payments không phải mảng:", paymentsData);
                return;
            }

            setPayments(paymentsData);
        } catch (error) {
            console.error("Lỗi tải payments:", error);
            toast.error("Lỗi tải danh sách payments");
        }
    };

    const calculateTotalRevenue = (details) => {
        if (!Array.isArray(details)) {
            setTotalRevenue(0);
            return;
        }

        const total = details.reduce((sum, item) => {
            return sum + (item?.detailPayment?.totalAmount || 0);
        }, 0);

        setTotalRevenue(total);
    };

    const handleEdit = (item) => {
        const user = item.user || {};
        const booking = item.booking || {};
        const payment = item.payment || {};

        setPaymentData({
            name: user.name || "N/A",
            userId: user._id || user.id || item.userId || "",
            bookingId: booking._id || booking.id || item.bookingId || "",
            method: payment.method || "cash",
            paymentStatus: payment.paymentStatus || "pending",
        });

        setEditingId(payment._id || payment.id || item.payment?.id || "");
        setIsAdding(false);
        setShowModal(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!paymentData.userId || !paymentData.bookingId) {
            toast.error("Vui lòng điền đầy đủ User ID và Booking ID");
            return;
        }

        if (!isAdding && !editingId) {
            toast.error("Không tìm thấy ID để cập nhật");
            return;
        }

        const payload = {
            userId: paymentData.userId,
            bookingId: paymentData.bookingId,
            method: paymentData.method,
            paymentStatus: paymentData.paymentStatus,
        };

        try {
            if (isAdding) {
                await createPayment(payload);
                toast.success("Thêm payment thành công!");
            } else {
                await updatePayment(editingId, payload);
                toast.success("Cập nhật payment thành công!");
            }

            setShowModal(false);
            fetchPayments();
            fetchDetailPayment(); // reload tổng doanh thu
        } catch (error) {
            console.error("Lỗi lưu payment:", error);
            toast.error("Lỗi khi lưu payment!");
        }
    };

    const handleDelete = async () => {
        if (!deleteId) return;

        try {
            await deletePayment(deleteId);
            toast.success("Xóa payment thành công!");
            setShowDeleteModal(false);
            fetchPayments();
            fetchDetailPayment(); // reload tổng doanh thu
        } catch (error) {
            console.error("Lỗi xóa payment:", error);
            toast.error("Lỗi khi xóa payment!");
        }
    };

    return (
        <div className="container-fluid mt-4">
            <h2 className="text-center fs-3 mb-3">Quản lý Payments</h2>

            {/* Tổng doanh thu */}
            <div className="d-flex justify-content-end mt-4 mb-3">
                <h4>
                    Tổng Doanh Thu: {totalRevenue.toLocaleString("vi-VN")} $
                </h4>
            </div>

            {/* Nút thêm mới */}
            <div className="d-flex justify-content-end mt-4 mb-3">
                <button
                    className="btn btn-success fs-4 px-4 py-2"
                    onClick={() => {
                        setPaymentData({
                            name: "",
                            userId: "",
                            bookingId: "",
                            method: "cash",
                            paymentStatus: "pending",
                        });
                        setEditingId(null);
                        setIsAdding(true);
                        setShowModal(true);
                    }}
                >
                    + Thêm Payment
                </button>
            </div>

            <div className="table-responsive">
                <table className="table table-bordered w-100">
                    <thead>
                        <tr className="fs-4 text-center">
                            <th>STT</th>
                            <th>Người dùng</th>
                            <th>Booking</th>
                            <th>Số lượng xe</th>
                            <th>Tổng tiền</th>
                            <th>Phương thức</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody className="text-center">
                        {payments.map((item, index) => (
                            <tr key={item.payment?._id || item.payment?.id || index}>
                                <td>{index + 1}</td>
                                <td>{item.user?.name || item.userId || "N/A"}</td>
                                <td>{item.booking?.id || item.bookingId || "N/A"}</td>
                                <td>{item.detailPayment?.amountCar || 0}</td>
                                <td>{item.detailPayment?.totalAmount || 0}</td>
                                <td>{item.payment?.method || "N/A"}</td>
                                <td>{item.payment?.paymentStatus || "N/A"}</td>
                                <td className="d-flex justify-content-around">
                                    <button className="btn btn-warning fs-5" onClick={() => handleEdit(item)}>
                                        Sửa
                                    </button>
                                    <button
                                        className="btn btn-danger fs-5"
                                        onClick={() => {
                                            setDeleteId(item.payment?._id || item.payment?.id);
                                            setShowDeleteModal(true);
                                        }}
                                    >
                                        Xóa
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Modal Thêm / Sửa */}
            <Modal show={showModal} onHide={() => setShowModal(false)} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>{isAdding ? "Thêm Payment" : "Chỉnh Sửa Payment"}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label>Người dùng (tên hiển thị)</Form.Label>
                            <Form.Control
                                type="text"
                                value={paymentData.name}
                                onChange={(e) => setPaymentData({ ...paymentData, name: e.target.value })}
                                disabled={!isAdding}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>User ID</Form.Label>
                            <Form.Control
                                type="text"
                                value={paymentData.userId}
                                onChange={(e) => setPaymentData({ ...paymentData, userId: e.target.value })}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Booking ID</Form.Label>
                            <Form.Control
                                type="text"
                                value={paymentData.bookingId}
                                onChange={(e) => setPaymentData({ ...paymentData, bookingId: e.target.value })}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Phương thức</Form.Label>
                            <Form.Select
                                value={paymentData.method}
                                onChange={(e) => setPaymentData({ ...paymentData, method: e.target.value })}
                            >
                                <option value="cash">Cash</option>
                                <option value="transfer">Transfer</option>
                            </Form.Select>
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Trạng thái</Form.Label>
                            <Form.Select
                                value={paymentData.paymentStatus}
                                onChange={(e) => setPaymentData({ ...paymentData, paymentStatus: e.target.value })}
                            >
                                <option value="pending">Pending</option>
                                <option value="completed">Completed</option>
                                <option value="failed">Failed</option>
                            </Form.Select>
                        </Form.Group>

                        <Button type="submit" variant="success">
                            Lưu
                        </Button>
                    </Form>
                </Modal.Body>
            </Modal>

            {/* Modal Xóa */}
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Xác nhận xóa</Modal.Title>
                </Modal.Header>
                <Modal.Body>Bạn có chắc chắn muốn xóa payment này không?</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
                        Hủy
                    </Button>
                    <Button variant="danger" onClick={handleDelete}>
                        Xóa
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default ManagePayment;