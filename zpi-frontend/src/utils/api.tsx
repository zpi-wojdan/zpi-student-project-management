import axios from 'axios';
import Cookies from 'js-cookie';


const api = axios.create();

api.interceptors.request.use((config) => {
    const token = Cookies.get('google_token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    config.headers['Content-Type'] = 'application/json';
    return config;
});

export default api;
