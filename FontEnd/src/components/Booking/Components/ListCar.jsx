import styles from '../styles.module.scss'
import ProductItem from '@components/ProductItem/ProductItem';
import { useNavigate } from "react-router";
import React, { useContext } from 'react'
import Loading from '@components/Loading/Loading';
import { BookingContext } from '@/context/BookingProvider';
import className from 'classnames'
import { getbyIdCar } from '@/apis/carService';

function ListCar({ data, isLoading }) {
  const { container, containerList } = styles
  const navigate = useNavigate();
  const { isShowGrid } = useContext(BookingContext)

  const handleNavigateDetailProduct = async (name, id) => {
    const path = `/shop/${name.replace(/ /g, "-")}`;
    await getbyIdCar(id).then((res) => {
      navigate(path, { state: { data: res.data } });
    })
  }
  return (
    isLoading ? (
      <div className="overlayLoading">
        <Loading />
      </div>
    ) : (
      <div className={className(container, {
        [containerList]: !isShowGrid
      })}>
        {data?.map((item) => (
          <div key={item.car?.id || item.car?.category} onClick={() => handleNavigateDetailProduct(item.car?.category, item.car?.id)}>
            <ProductItem
              src={item.car?.img}
              categoryCar={item.car?.category}
              brandCar={item.brand?.nameBrandCar}
              price={item.car?.pricePerDay}
            // description={item.car?.des}
            />
          </div>
        ))}
      </div>
    )
  );

}

export default ListCar