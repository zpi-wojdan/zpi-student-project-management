import axios, { AxiosError, AxiosResponse } from 'axios';
import Cookies from 'js-cookie';
import globalRouter from "../globalRouter";

const api = axios.create();

api.interceptors.request.use((config) => {
    const token = Cookies.get('google_token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    function (response) {
      return response;
    },
    function (error) {
      if (!error.response && error.code === 'ERR_NETWORK' && globalRouter.navigate) {
        globalRouter.navigate("/error");
      }
      return Promise.reject(error);
    }
  );

export default api;
