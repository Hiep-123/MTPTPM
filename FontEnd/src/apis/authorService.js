import axiosClient from "./axiosClient";

const loginAuth = async (data) => {
    return await axiosClient.post('/user/login', data);
};
const registerAuth = async (data) => {
    return await axiosClient.post('/user/register', data);
}

const getUserId = async (id) => {
    return await axiosClient.get(`/user/getInfo/${id}`)
}

const googleOAuthLogin = async (idToken) => {
    return await axiosClient.post('/user/oauth/google', { idToken });
}

const updateInfoUser = async (data) => {
    return
}
export { loginAuth, registerAuth, getUserId, googleOAuthLogin, updateInfoUser }; 