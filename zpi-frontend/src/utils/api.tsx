import axios, { AxiosError, AxiosResponse } from 'axios';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';


const api = axios.create();

api.interceptors.request.use((config) => {
    const token = Cookies.get('google_token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    (response: AxiosResponse) => {
      return response;
    },
    (error: AxiosError) => {
      if (error.code === 'ERR_NETWORK') {
        console.error('Błąd sieciowy:', error.message, error.response);
        //useNavigate()('/error');
      }
      return Promise.reject(error);
    }
  );

export default api;
