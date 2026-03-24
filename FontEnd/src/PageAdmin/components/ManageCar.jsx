import React, { useState, useEffect, useContext } from "react";
import {
  getAllCar,
  addCar,
  updateCar,
  deleteCar,
  getAllBrandCar
} from "@/apis/carService";
import { ToastContext } from "@/context/ToastProvider";
import { Modal, Button, Form } from "react-bootstrap";
import "./styles.module.scss";

const ManageCar = () => {
  const { toast } = useContext(ToastContext);
  const [brands, setBrands] = useState([]);
  const [listCar, setListCars] = useState([]);

  const [carData, setCarData] = useState({
    img: "",
    category: "",
    brandId: "",
    pricePerDay: "",
    des: ""
  });

  const [editingId, setEditingId] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [isAdding, setIsAdding] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteId, setDeleteId] = useState(null);

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const itemsPerPage = 6;

  /* ================= FETCH ================= */

  const fetchBrands = async () => {
    try {
      const res = await getAllBrandCar();
      setBrands(res.data);
    } catch {
      toast.error("Lỗi khi tải danh sách thương hiệu");
    }
  };

  const fetchCars = async (page = 1) => {
    try {
      const res = await getAllCar(page, itemsPerPage);
      setListCars(res.data.cars);
      setTotalPages(res.data.totalPages);
      setCurrentPage(page);
    } catch {
      toast.error("Lỗi khi tải danh sách xe");
    }
  };

  useEffect(() => {
    fetchCars(currentPage);
    fetchBrands();
  }, [currentPage]);

  /* ================= HANDLER ================= */

  const handleFileChange = (e) => {
    setCarData({ ...carData, img: e.target.files[0] });
  };

  const handleEdit = (item) => {
    setCarData({
      img: item.car?.img,
      category: item.car?.category,
      brandId: item.brand?.id || "",
      pricePerDay: item.car?.pricePerDay,
      des: item.car?.des
    });

    setEditingId(item.car?.id);
    setIsAdding(false);
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    let imageUrl = carData.img; // giữ nguyên nếu edit và không chọn ảnh mới

    // Nếu có file mới → upload
    if (carData.img instanceof File) {
      console.log("Đang upload file:", carData.img.name, "type:", carData.img.type);

      const formData = new FormData();
      formData.append("file", carData.img); // tên phải là "file" – đã đúng

      try {
        const uploadRes = await fetch("http://localhost:8080/api/upload", {
          method: "POST",
          body: formData,
        });

        if (!uploadRes.ok) {
          const errorText = await uploadRes.text();
          console.error("Upload lỗi:", uploadRes.status, errorText);
          toast.error(`Upload ảnh thất bại: ${errorText || "Lỗi server"}`);
          return; // dừng lại, không lưu xe
        }

        const uploadData = await uploadRes.json();
        imageUrl = uploadData.url; // ← SỬA QUAN TRỌNG: dùng .url thay vì .imageUrl

        console.log("Upload thành công → URL:", imageUrl);
      } catch (err) {
        console.error("Lỗi fetch upload:", err);
        toast.error("Không kết nối được server upload ảnh");
        return;
      }
    } else {
      console.log("Không chọn ảnh mới → dùng URL cũ hoặc rỗng:", imageUrl);
    }

    // Chuẩn bị payload
    const payload = {
      img: imageUrl || "", // đảm bảo gửi string, không undefined
      category: carData.category.trim(),
      brandId: carData.brandId,
      pricePerDay: Number(carData.pricePerDay) || 0,
      des: carData.des.trim(),
    };


    try {
      if (isAdding) {
        await addCar(payload);
        toast.success("Thêm xe thành công");
      } else {
        await updateCar(editingId, payload);
        toast.success("Cập nhật xe thành công");
      }

      setShowModal(false);
      fetchCars(currentPage);
    } catch (err) {
      console.error("Lỗi khi lưu xe:", err);
      toast.error("Lỗi khi lưu xe");
    }
  };

  const handleDelete = async () => {
    try {
      await deleteCar(deleteId);
      toast.success("Xóa xe thành công");
      setShowDeleteModal(false);
      fetchCars(currentPage);
    } catch {
      toast.error("Lỗi khi xóa xe");
    }
  };

  /* ================= RENDER ================= */

  return (
    <div className="container-fluid mt-4">
      <h2 className="text-center fs-3 mb-3">Quản lý xe</h2>

      <div className="d-flex justify-content-end mt-4 mb-3">
        <button
          className="btn btn-success fs-4 px-4 py-2"
          onClick={() => {
            setCarData({
              img: "",
              category: "",
              brandId: "",
              pricePerDay: "",
              des: ""
            });
            setIsAdding(true);
            setShowModal(true);
          }}
        >
          + Thêm xe
        </button>
      </div>

      <div className="table-responsive">
        <table className="table table-bordered w-100" style={{ minWidth: 1200 }}>
          <thead>
            <tr className="fs-4 text-center">
              <th>STT</th>
              <th>Hình ảnh</th>
              <th>Danh mục</th>
              <th>Thương hiệu</th>
              <th>Giá thuê/ngày</th>
              <th>Mô tả</th>
              <th>Hành động</th>
            </tr>
          </thead>

          <tbody>
            {listCar.map((item, index) => (
              <tr key={item.car.id}>
                <td className="fs-5 text-center">
                  {(currentPage - 1) * itemsPerPage + index + 1}
                </td>

                <td className="text-center">
                  <img
                    src={
                      item.car.img?.startsWith("http")
                        ? item.car.img
                        : `http://localhost:8080${item.car.img}`
                    }
                    width="120"
                    height="70"
                    style={{ objectFit: "cover" }}
                  />
                </td>

                <td className="fs-4">{item.car.category}</td>
                <td className="fs-4">{item.brand?.nameBrandCar}</td>
                <td className="fs-4">{item.car.pricePerDay}.00 $</td>
                <td className="fs-5" style={{ width: "20%" }}>
                  {item.car.des}
                </td>

                <td className="d-flex justify-content-around">
                  <button
                    className="btn btn-warning fs-4"
                    onClick={() => handleEdit(item)}
                  >
                    Sửa
                  </button>

                  <button
                    className="btn btn-danger fs-4"
                    onClick={() => {
                      setDeleteId(item.car.id);
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

      {/* Pagination */}
      <div className="d-flex justify-content-center mt-3">
        <button
          className="btn btn-secondary mx-2"
          disabled={currentPage === 1}
          onClick={() => fetchCars(currentPage - 1)}
        >
          Trước
        </button>
        <span className="fs-5">
          Trang {currentPage} / {totalPages}
        </span>
        <button
          className="btn btn-secondary mx-2"
          disabled={currentPage === totalPages}
          onClick={() => fetchCars(currentPage + 1)}
        >
          Sau
        </button>
      </div>

      {/* MODAL ADD / EDIT */}
      <Modal show={showModal} onHide={() => setShowModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {isAdding ? "Thêm xe" : "Chỉnh sửa xe"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Hình ảnh</Form.Label>
              <Form.Control type="file" onChange={handleFileChange} />
              {typeof carData.img === "string" && carData.img && (
                <img
                  src={carData.img}
                  alt="preview"
                  width="100"
                  height="60"
                  className="mt-2"
                />
              )}
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Danh mục</Form.Label>
              <Form.Control
                value={carData.category}
                onChange={(e) =>
                  setCarData({ ...carData, category: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Thương hiệu</Form.Label>
              <Form.Select
                value={carData.brandId}
                onChange={(e) =>
                  setCarData({ ...carData, brandId: e.target.value })
                }
              >
                <option value="">Chọn thương hiệu</option>
                {brands.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.nameBrandCar}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Giá thuê/ngày</Form.Label>
              <Form.Control
                type="number"
                value={carData.pricePerDay}
                onChange={(e) =>
                  setCarData({ ...carData, pricePerDay: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                value={carData.des}
                onChange={(e) =>
                  setCarData({ ...carData, des: e.target.value })
                }
              />
            </Form.Group>

            <Button type="submit" variant="success">
              Lưu
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* MODAL DELETE */}
      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>Bạn có chắc chắn muốn xóa xe này?</Modal.Body>
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

export default ManageCar;
