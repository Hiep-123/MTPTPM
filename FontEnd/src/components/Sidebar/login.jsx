import React, { useContext, useEffect, useState } from 'react'
import { HiOutlineUser } from "react-icons/hi";
import Button from '@components/Button/Button'
import styles from './styles.module.scss'
import { IoMdEyeOff } from "react-icons/io";
import { IoMdEye } from "react-icons/io";
import { ToastContext } from "@/context/ToastProvider";
import { loginAuth, registerAuth, googleOAuthLogin } from '@/apis/authorService';
import Cookies from 'js-cookie'
import { SideBarContext } from '@/context/SideBarProvider';
import { useNavigate } from 'react-router-dom';

function login() {

    const { containerLogin, containerBox, containerBox2, box1, box2, box3, containerBox3, containerBox4, input, active, button } = styles
    const [isRegister, setIsRegister] = useState(false);
    const [showPassword, setShowPassword] = useState(false)
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const { toast } = useContext(ToastContext);
    const [isLoading, setIsLoading] = useState(false)
    const { setIsOpen, setUserId, setUserInfo, userInfo } = useContext(SideBarContext);
    const navigate = useNavigate();
    const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

    const handleShowPassword = () => {
        setShowPassword(!showPassword)
    }


    const handleRegisterAndLogin = async () => {
        setIsLoading(true)
        try {
            if (isRegister === true) {
                await registerAuth({
                    userName: name,
                    password,
                    email: name
                })
                    .then((res) => {
                        toast.success(res.data.message);
                        setIsLoading(false);
                    })
                    .catch((err) => {
                        console.error("🔥 Lỗi đăng ký:", err);
                        toast.error(err.response?.data?.message || "Đăng ký thất bại!");
                        setIsLoading(false);
                    });
            }
        } catch (error) {
            console.log(error)
        }

        if (!isRegister) {
            await loginAuth({
                userName: name,
                password
            }).then((res) => {
                setUserInfo(res.data)
                setIsLoading(false)
                const { token } = res.data
                const { _id } = res.data.user
                setUserId(_id)
                Cookies.set('userId', _id, { expires: 1 });
                Cookies.set('token', token, { expires: 1 });
                toast.success('Sign in successfully')
                setIsOpen(false)

            }).catch((err) => {
                toast.error("Email or Password invalid")
                setIsLoading(false)
            })
        }
    }
    useEffect(() => {
        if (userInfo && userInfo.user.role) {
            navigate(userInfo.user.role === "admin" ? "/page/admin" : "/");
        }
    }, [userInfo]);



    return (
        <div className={containerLogin}>
            <div className={containerBox}>
                <div>
                    <HiOutlineUser size={'50'} />
                </div>
                <span>MY ACCOUNT</span>
            </div>

            <div className={containerBox2}>
                <div className={box1}>
                    <div style={{
                        borderBlockWidth: '5px',
                        cursor: 'pointer'
                    }}
                        className={!isRegister ? active : ""}
                        onClick={() => setIsRegister(false)}>LOGIN</div>
                    <div style={{
                        borderBlockWidth: '5px',
                        cursor: 'pointer'
                    }}
                        className={isRegister ? active : ""}
                        onClick={() => setIsRegister(true)}>REGISTER</div>
                </div>
            </div>

            <div className={containerBox3}>
                <div className={box2}>
                    <div>{!isRegister ? 'Username or email' : 'Email address'}<span style={{
                        color: 'red'
                    }}> *</span></div>
                    <input type="text" onChange={(e) => setName(e.target.value)} />
                </div>

                <div className={box2} >
                    <div>
                        Password <span style={{
                            color: 'red'
                        }}> *</span>
                    </div>
                    <div className={input}>
                        <input type={`${showPassword ? 'text' : 'password'}`}
                            onChange={(e) => setPassword(e.target.value)} />
                        <div onClick={handleShowPassword}
                            style={{
                                cursor: 'pointer'
                            }}>
                            {showPassword ? <IoMdEyeOff /> : <IoMdEye />}
                        </div>
                    </div>
                </div>

                {!isRegister && (<div className={containerBox4}>
                    <div className={box3}>
                        <input type="checkbox" />
                        <span>Remember me</span>
                    </div>
                    <div style={{
                        fontSize: '20px',
                        cursor: 'pointer',
                        textDecorationLine: 'underline',
                        fontWeight: 'bold'
                    }}>
                        Lost Password ?
                    </div>

                </div>)}
            </div>

            <button className={button}
                onClick={() => handleRegisterAndLogin()} >{!isRegister ? 'Log in' : 'Register'}</button>

            {!isRegister && (
                <div style={{ marginTop: '20px', textAlign: 'center' }}>
                    <p style={{ marginBottom: '10px' }}>Hoặc đăng nhập bằng:</p>
                </div>
            )}
        </div>
    )
}

export default login