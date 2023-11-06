import i18n from "i18next";
import i18nBackend from "i18next-http-backend";
import { initReactI18next } from "react-i18next";
import Cookies from 'js-cookie';

const lang = Cookies.get('lang') || 'pl';

i18n
    .use(i18nBackend)
    .use(initReactI18next)
    .init({
        fallbackLng: "pl",
        lng: lang,
        interpolation: {
            escapeValue: false,
        },
    });

export default i18n;
