import {useEffect} from "react";

const LoginPage = () => {

    useEffect(() => {
        // @ts-ignore
        google.accounts.id.initialize({
            client_id: "333365127566-llqb3rl4kcvvcnurr7cih7s126iu0e5v.apps.googleusercontent.com",
            callback: handleGoogleCallbackResponse
        })

        // @ts-ignore
        google.accounts.id.renderButton(
            document.getElementById("sign-in-button"),
            {
                theme: "filled_black",
                size: "large",
                width: document.getElementById("sign-in-button")?.offsetWidth,
            }
        )
    });

    function handleGoogleCallbackResponse(response: any) {
        console.log("Encoded JWT ID token: " + response.credential);
    }


    return (
        <div className="login page">
            <div className="login-panel-container">
                <div className="login-panel">
                    <h2>Witaj w systemie logowania</h2>
                    <div id="sign-in-prompt">
                        <p>Kliknij przycisk poniżej, aby zalogować się za pomocą uczelnianego adresu email</p>
                    </div>
                    <div id="sign-in-button"></div>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
