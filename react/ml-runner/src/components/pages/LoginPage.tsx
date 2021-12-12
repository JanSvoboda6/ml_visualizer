import React from "react";
import { Link, Redirect } from 'react-router-dom';
import Popup from "../popup/Popup";
import { useDispatch } from "react-redux";
import logo from '../../styles/logo_but_text.png';
import cube from '../../styles/cube_animation.gif';
import dots from '../../styles/dots_logo_big.svg';
import { LoginService } from '../../services/LoginService';
import { useState } from "react";
import { store } from '../../redux/store';
import HelperBox from "../navigation/HelperBox";
import FadeIn from "react-fade-in";

function Login()
{
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [isPopupClosed, setPopupClosed] = useState(false);
    const [loading, setLoading] = useState(false);
    const [isLoggedIn, setLoggedIn] = useState(false);

    const search = window.location.search;
    const params = new URLSearchParams(search);
    const showPopup = params.get('popup');

    const dispatch = useDispatch();

    const onChangeUsername = (e: { target: { value: any; }; }) =>
    {
        setUsername(e.target.value);
    }

    const onChangePassword = (e: { target: { value: any; }; }) =>
    {
        setPassword(e.target.value);
    }

    const handleLogin = (e: { preventDefault: () => void; }) =>
    {
        e.preventDefault();

        setLoading(true);

        var isValidationSuccesfull = true;
        if (isValidationSuccesfull) //TODO Jan: implement proper validation
        {
            var user = { username: username, password: password, accessToken: "" };

            LoginService(dispatch, user)
                .then(
                    (user: any) =>
                    {
                        setLoggedIn(true);
                    },
                    (error: any) =>
                    {
                        setLoggedIn(false);
                        setLoading(false);

                        var message = "";
                        if (error && error.response && error.response.data.message)
                        {
                            message = error.response.data.message;
                        }
                        else if (error.message)
                        {
                            message = error.message;
                        }
                        else if (error.toString())
                        {
                            message = error.toString();
                        }

                        setMessage(message);
                    });
        } else
        {
            setLoading(false);
        }
    }


    const mainState = store.getState();
    console.log(mainState.user.isLoggedIn);


    if (isLoggedIn)
    {
        return <Redirect to="/preparing" />;
    }

    return (
            <>
            <div className="wrapper">
                {showPopup == 't' && <HelperBox content="Thanks for registration.  Now you can login!" />}
                {/* <a className="register-item logo-register"><img className='logo-dots-bigger' src={dots} alt="logo_dots" /></a> */}
                {/* <a className="login-banner-text">Random</a> */}
            </div>
            <div className="landing-page-wrapper">
                <div className="landing-page-content">
                    <FadeIn delay={250}>
                        <div className="landing-page">
                            <div className="landing-page-text">Machine Learning Runner</div>
                            <div className="landing-page-information-text-wrapper">
                                <div className='landing-page-information-text'>
                                    Upload data.
                                    Run model.
                                    Analyze.
                                </div>
                            </div>
                            <div className="login-form-wrapper">
                                <div className="login-form">
                                    <img className='logo' src={logo} alt="logo_but" />
                                    <div className="login-page-content">
                                        <form onSubmit={handleLogin}>
                                            <div className="login-item">
                                                <input
                                                    type="text"
                                                    className="input-text"
                                                    name="email"
                                                    placeholder="Email"
                                                    value={username}
                                                    onChange={onChangeUsername}
                                                />
                                            </div>
                                            <div className="login-item">
                                                <input
                                                    type="password"
                                                    className="input-text"
                                                    name="password"
                                                    placeholder="Password"
                                                    value={password}
                                                    onChange={onChangePassword}
                                                />
                                            </div>
                                            <div className="login-item">
                                                <button className="submit-button" disabled={loading}>
                                                    <span>Login</span>
                                                </button>
                                            </div>
                                            {message && (
                                                <div className="login-item">
                                                    <div className="alert-text">
                                                        {message}
                                                    </div>
                                                </div>
                                            )}
                                        </form>
                                        <div className="login-link">
                                            <p className="login-link-text" >Do not have an account?</p>
                                            <Link className="login-link-reference" to="/register">Register</Link>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="cube-image-wrapper">
                                <img className='cube-animation fade-in-image' src={cube} alt="cube_animation" />
                            </div>
                        </div>
                    </FadeIn>


                </div >
            </div>
            </>
    );
}

export default Login;
